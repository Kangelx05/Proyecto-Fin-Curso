<template>
  <div>
    <!-- LISTA POR CATEGORÍAS -->
    <div v-for="(productos, categoria) in productosAgrupados" :key="categoria" class="categoria">
      <button
        @click="toggleCategoria(categoria)"
        class="categoria-btn"
        :aria-expanded="categoriasAbiertas[categoria] || false"
      >
        {{ categoria }}
        <span class="flecha">{{ categoriasAbiertas[categoria] ? '▲' : '▼' }}</span>
      </button>

      <ul v-if="categoriasAbiertas[categoria]" class="lista-productos grid-cards">
        <li v-for="producto in productos" :key="producto.id" class="card">
          <!-- Imagen del producto o un marcador de posición.  La altura del contenedor de la imagen es fija
               para que todas las tarjetas tengan la misma altura. -->
          <div class="card-image-container">
            <img
              :src="getImageSrc(producto)"
              class="card-image"
              alt="Imagen del producto"
              @error="onImgError"
            />
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ producto.name }}</h3>
            <p class="card-desc">{{ producto.description }}</p>
          </div>
          <div class="card-footer">
            <span class="price">{{ formatPrice(producto.price) }}</span>
            <button class="btn-add" @click="addToCart(producto)">Añadir al carrito</button>
          </div>
        </li>
      </ul>
    </div>

    <!-- BOTÓN FLOTANTE DEL CARRITO -->
    <button class="fab-cart" @click="toggleCart(true)" aria-label="Abrir carrito">
      🛒 <span v-if="cartCount" class="badge">{{ cartCount }}</span>
    </button>

    <!-- OVERLAY -->
    <div v-if="isCartOpen" class="overlay" @click="toggleCart(false)"></div>

    <!-- DRAWER DEL CARRITO -->
    <aside class="cart-drawer" :class="{ open: isCartOpen }" aria-label="Carrito">
      <header class="cart-header">
        <h2>Tu pedido</h2>
        <button class="btn-icon" @click="toggleCart(false)" aria-label="Cerrar">✕</button>
      </header>

      <div class="cart-content" v-if="cart.length">
        <div v-for="item in cart" :key="item.id" class="cart-item">
          <div class="ci-info">
            <h3 class="ci-name">{{ item.name }}</h3>
            <p class="ci-desc">{{ item.description }}</p>
            <div class="ci-price-row">
              <span class="ci-price">{{ formatPrice(item.price) }}</span>
              <span class="ci-subtotal">{{ formatPrice(item.price * item.qty) }}</span>
            </div>
          </div>
          <div class="ci-actions">
            <div class="qty">
              <button @click="decreaseQty(item)" class="q-btn" aria-label="Disminuir">−</button>
              <span class="q-num">{{ item.qty }}</span>
              <button @click="increaseQty(item)" class="q-btn" aria-label="Aumentar">＋</button>
            </div>
            <button class="btn-remove" @click="removeFromCart(item)">Eliminar</button>
          </div>
        </div>
      </div>

      <div class="cart-empty" v-else>🧺 Tu carrito está vacío</div>

      <footer class="cart-footer">
        <div class="totals">
          <span>Total</span>
          <strong>{{ formatPrice(cartTotal) }}</strong>
        </div>
        <button class="btn-checkout" :disabled="!cart.length" @click="checkout">
          Finalizar pedido
        </button>
      </footer>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted, watch } from 'vue'
// Use the shared API service which attaches session tokens and
// automatically handles token renewal on 401 responses.
import api from '../services/api'

// Import a placeholder image used when a product has no data image.  The bundler will
// resolve this import to a URL that can be used as an <img> src.  We copy the
// placeholder file into the assets directory so it can be bundled.
import placeholder from '../assets/placeholder.png'

interface Producto {
  id: number
  name: string
  data: Uint8Array | null
  description: string
  price: number
  category: string
}

interface CartItem extends Producto {
  qty: number
}

const productos = ref<Producto[]>([])

type ProductosAgrupados = Record<string, Producto[]>

const productosAgrupados = computed<ProductosAgrupados>(() => {
  return productos.value.reduce((acc: ProductosAgrupados, producto: Producto) => {
    const cat = producto.category || 'Sin categoría'
    if (!acc[cat]) acc[cat] = []
    acc[cat].push(producto)
    return acc
  }, {})
})

const categoriasAbiertas = reactive<Record<string, boolean>>({})
watch(
  productosAgrupados,
  (grupos) => {
    for (const cat of Object.keys(grupos)) {
      if (categoriasAbiertas[cat] === undefined) categoriasAbiertas[cat] = true
    }
  },
  { immediate: true },
)
function toggleCategoria(categoria: string): void {
  categoriasAbiertas[categoria] = !categoriasAbiertas[categoria]
}

const formatPrice = (n: number) =>
  new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(n)

/* --------- CARRITO --------- */
const cart = ref<CartItem[]>([])
const isCartOpen = ref(false)

const cartCount = computed(() => cart.value.reduce((s, it) => s + it.qty, 0))
const cartTotal = computed(() => cart.value.reduce((s, it) => s + it.price * it.qty, 0))

function toggleCart(open?: boolean) {
  isCartOpen.value = typeof open === 'boolean' ? open : !isCartOpen.value
}

function addToCart(p: Producto) {
  const found = cart.value.find((i) => i.id === p.id)
  if (found) found.qty++
  else cart.value.push({ ...p, qty: 1 })
  // Do not automatically open the cart when adding an item.
  // Keep the drawer closed so users can continue browsing products.
  persistCart()
}

function removeFromCart(item: CartItem) {
  cart.value = cart.value.filter((i) => i.id !== item.id)
  persistCart()
}

function increaseQty(item: CartItem) {
  item.qty++
  persistCart()
}
function decreaseQty(item: CartItem) {
  item.qty--
  if (item.qty <= 0) removeFromCart(item)
  else persistCart()
}

function getImageSrc(p: Producto): string {
  const d = (p as any)?.data
  if (!d) return placeholder

  // 1) Si ya viene como string base64 (con o sin prefijo data:)
  if (typeof d === 'string') {
    if (d.startsWith('data:image')) return d // ya formateada
    // si es base64 pura, prefija con el tipo (asumimos jpeg por defecto)
    return `data:image/jpeg;base64,${d}`
  }

  // 2) Si viene como objeto tipo Buffer { type: 'Buffer', data: [...] }
  if (d && typeof d === 'object' && Array.isArray(d.data)) {
    const u8 = new Uint8Array(d.data)
    const type = detectMime(u8) ?? 'image/jpeg'
    return `data:${type};base64,${u8ToBase64(u8)}`
  }

  // 3) Si viene como array de números o ya es un Uint8Array
  if (Array.isArray(d) || d instanceof Uint8Array) {
    const u8 = d instanceof Uint8Array ? d : new Uint8Array(d)
    const type = detectMime(u8) ?? 'image/jpeg'
    return `data:${type};base64,${u8ToBase64(u8)}`
  }

  return placeholder
}

// Convierte Uint8Array -> base64 en trozos (evita petadas de stack/memoria)
function u8ToBase64(u8: Uint8Array): string {
  let binary = ''
  const chunk = 0x8000
  for (let i = 0; i < u8.length; i += chunk) {
    binary += String.fromCharCode.apply(null, u8.subarray(i, i + chunk) as any)
  }
  return btoa(binary)
}

// Detección simple de MIME por cabecera
function detectMime(u8: Uint8Array): string | null {
  // JPEG
  if (u8[0] === 0xff && u8[1] === 0xd8 && u8[2] === 0xff) return 'image/jpeg'
  // PNG
  if (u8[0] === 0x89 && u8[1] === 0x50 && u8[2] === 0x4e && u8[3] === 0x47) return 'image/png'
  // WEBP: "RIFF....WEBP"
  if (
    u8[0] === 0x52 &&
    u8[1] === 0x49 &&
    u8[2] === 0x46 &&
    u8[3] === 0x46 &&
    u8[8] === 0x57 &&
    u8[9] === 0x45 &&
    u8[10] === 0x42 &&
    u8[11] === 0x50
  )
    return 'image/webp'
  return null
}

function onImgError(ev: Event) {
  ;(ev.target as HTMLImageElement).src = placeholder
}

function checkout() {
  // Perform the checkout by creating an order and its details via the API.
  ;(async () => {
    try {
      const token = localStorage.getItem('sessionToken')
      if (!token) {
        alert('No se ha encontrado una sesión activa. Escanea el QR de la mesa primero.')
        return
      }
      // Decode the JWT payload to extract the table identifier.  The payload is the second
      // part of the JWT (header.payload.signature) and is Base64URL-encoded.  The
      // `atob` function decodes Base64 strings but does not handle URL-safe characters,
      // so replace URL-safe characters before decoding.
      const base64 = token.split('.')[1]
      const json = atob(base64.replace(/-/g, '+').replace(/_/g, '/'))
      const payload = JSON.parse(json)
      const tableId = payload.tableId
      if (!tableId) {
        alert('La sesión no contiene información de la mesa.')
        return
      }
      // Create the order.  The backend will link the order to the existing table via
      // the provided tableId.  Use the current timestamp and an initial state.
      const orderRes = await api.post('/order', {
        tableId: tableId,
        date: new Date().toISOString(),
        // Always create or resume an open order.  The backend will reuse an
        // existing order with the same tableId and state "IN_SERVICE" if one
        // already exists.
        state: 'IN_SERVICE',
      })
      const orderId = orderRes.data.id
      // Para cada artículo del carrito, crea tantas líneas de pedido como unidades
      // se hayan solicitado.  Cada llamada a la API representa una única unidad
      // de producto (OrderDetail) y no incluye un campo de cantidad.  Esto
      // permite que la cocina muestre un único contador por producto.
      for (const item of cart.value) {
        for (let i = 0; i < item.qty; i++) {
          await api.post('/order-detail', {
            orderId: orderId,
            productName: item.name,
            unitPrice: item.price,
            productId: item.id,
          })
        }
      }
      // Clear the cart and persist the empty state
      cart.value = []
      persistCart()
      isCartOpen.value = false
      alert('¡Pedido enviado correctamente!')
    } catch (err) {
      console.error('Error al enviar el pedido:', err)
      alert('Hubo un error al enviar el pedido. Inténtalo de nuevo.')
    }
  })()
}

/* Persistencia simple en localStorage */
const CART_KEY = 'carrito'
function persistCart() {
  localStorage.setItem(CART_KEY, JSON.stringify(cart.value))
}
onMounted(() => {
  try {
    const raw = localStorage.getItem(CART_KEY)
    if (raw) cart.value = JSON.parse(raw)
  } catch {}
})

/* Carga de productos */
onMounted(async () => {
  try {
    const response = await api.get<Producto[]>('/cardProduct')
    productos.value = response.data
  } catch (error) {
    console.error('Error al cargar productos:', error)
  }
})
</script>

<style scoped>
:global(body) {
  background-color: #111827;
  color: #e5e7eb;
  font-family:
    system-ui,
    -apple-system,
    Segoe UI,
    Roboto,
    Ubuntu,
    Cantarell,
    'Helvetica Neue',
    Arial,
    'Noto Sans',
    sans-serif;
}

/* Categorías y cards (igual que antes, con botón extra) */
.categoria {
  margin-bottom: 1.25rem;
  border: 1px solid #374151;
  border-radius: 0.75rem;
  background-color: #1f2937;
  overflow: hidden;
}
.categoria-btn {
  width: 100%;
  text-align: left;
  padding: 0.9rem 1rem;
  background-color: #374151;
  color: #f9fafb;
  font-weight: 700;
  border: none;
  cursor: pointer;
  letter-spacing: 0.2px;
}
.categoria-btn:hover {
  background-color: #4b5563;
}
.flecha {
  float: right;
}
.lista-productos {
  background-color: #111827;
  margin: 0;
  padding: 1rem;
  list-style: none;
}

.grid-cards {
  display: grid;
  grid-template-columns: repeat(1, minmax(0, 1fr));
  gap: 1rem;
}
@media (min-width: 640px) {
  .grid-cards {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (min-width: 1024px) {
  .grid-cards {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.card {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 0.75rem;
  border: 1px solid #374151;
  border-radius: 0.75rem;
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.35);
  padding: 0;
  overflow: hidden;
  transition:
    transform 0.14s ease,
    box-shadow 0.14s ease,
    border-color 0.14s ease;
}

/* Contenedor de la imagen en la tarjeta.  Se fija la altura para que todas las
   imágenes tengan la misma proporción y las tarjetas sean uniformes. */
.card-image-container {
  width: 100%;
  height: 150px;
  overflow: hidden;
  border-bottom: 1px solid #374151;
}
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.45);
  border-color: #4b5563;
}
.card-title {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 800;
  line-height: 1.2;
  color: #f3f4f6;
  letter-spacing: 0.2px;
}
.card-desc {
  margin: 0.25rem 0 0;
  color: #cbd5e1;
  font-size: 0.95rem;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-body {
  /* Add horizontal padding so the product title and description are not flush
     against the card edges.  The padding on the sides improves readability
     without affecting the image above. */
  padding-left: 0.85rem;
  padding-right: 0.85rem;
}

/* Add horizontal padding to the footer to space the price and add button
   away from the card edges. */
.card-footer {
  padding-left: 0.85rem;
  padding-right: 0.85rem;
}
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  gap: 0.75rem;
}
.price {
  font-weight: 800;
  padding: 0.35rem 0.6rem;
  border-radius: 9999px;
  background: #10b9811a;
  color: #34d399;
  border: 1px solid #065f46;
  font-variant-numeric: tabular-nums;
}

.btn-add {
  padding: 0.5rem 0.8rem;
  border-radius: 0.6rem;
  background: #2563eb;
  color: #fff;
  border: 1px solid #1d4ed8;
  cursor: pointer;
  font-weight: 700;
}
.btn-add:hover {
  background: #1d4ed8;
}

/* FAB carrito */
.fab-cart {
  position: fixed;
  right: 1rem;
  bottom: 1rem;
  z-index: 60;
  background: #111827;
  color: #f9fafb;
  border: 1px solid #374151;
  border-radius: 9999px;
  padding: 0.7rem 1rem;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.45);
}
.badge {
  margin-left: 0.5rem;
  background: #ef4444;
  color: #fff;
  border-radius: 9999px;
  padding: 0.1rem 0.45rem;
  font-size: 0.8rem;
}

/* Overlay */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 70;
}

/* Drawer carrito */
.cart-drawer {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: 100%;
  max-width: 380px;
  background: #0b1220;
  border-left: 1px solid #1f2937;
  box-shadow: -10px 0 24px rgba(0, 0, 0, 0.45);
  transform: translateX(100%);
  transition: transform 0.2s ease;
  z-index: 80;
  display: flex;
  flex-direction: column;
}
.cart-drawer.open {
  transform: translateX(0);
}

.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid #1f2937;
  background: #0e172a;
}
.cart-header h2 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 800;
}
.btn-icon {
  background: transparent;
  color: #cbd5e1;
  border: none;
  font-size: 1.1rem;
  cursor: pointer;
}

.cart-content {
  flex: 1;
  overflow: auto;
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.cart-item {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 0.75rem;
  border: 1px solid #1f2937;
  border-radius: 0.75rem;
  padding: 0.75rem;
  background: #0b1324;
}
.ci-name {
  margin: 0;
  font-weight: 800;
  color: #f3f4f6;
  font-size: 1rem;
}
.ci-desc {
  margin: 0.2rem 0 0.5rem;
  color: #9ca3af;
  font-size: 0.9rem;
}
.ci-price-row {
  display: flex;
  gap: 0.5rem;
  align-items: baseline;
}
.ci-price {
  color: #34d399;
  font-weight: 700;
}
.ci-subtotal {
  margin-left: auto;
  color: #e5e7eb;
  font-weight: 800;
}

.ci-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
}
.qty {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.q-btn {
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  border: 1px solid #374151;
  background: #111827;
  color: #e5e7eb;
  cursor: pointer;
}
.q-num {
  min-width: 1.5rem;
  text-align: center;
  font-weight: 800;
}
.btn-remove {
  border: 1px solid #ef4444;
  background: #7f1d1d;
  color: #fff;
  padding: 0.3rem 0.6rem;
  border-radius: 0.5rem;
  cursor: pointer;
}
.btn-remove:hover {
  background: #991b1b;
}

.cart-empty {
  flex: 1;
  display: grid;
  place-items: center;
  color: #9ca3af;
  padding: 2rem;
}

.cart-footer {
  border-top: 1px solid #1f2937;
  padding: 1rem;
  background: #0e172a;
  display: grid;
  gap: 0.5rem;
}
.totals {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  font-size: 1.05rem;
}
.btn-checkout {
  padding: 0.7rem 1rem;
  border-radius: 0.7rem;
  background: #22c55e;
  border: 1px solid #16a34a;
  color: #052e16;
  font-weight: 900;
  cursor: pointer;
}
.btn-checkout:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
