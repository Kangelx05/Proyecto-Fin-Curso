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
import axios from 'axios'

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
  isCartOpen.value = true
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

function checkout() {
  // Aquí harías POST a tu backend con el carrito
  // axios.post('/pedido', { items: cart.value })
  alert('Pedido enviado (demo).')
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
    const response = await axios.get<Producto[]>('http://localhost:8081/cardProduct')
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
  gap: 0.75rem;
  border: 1px solid #374151;
  border-radius: 0.75rem;
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.35);
  padding: 1rem;
  transition:
    transform 0.14s ease,
    box-shadow 0.14s ease,
    border-color 0.14s ease;
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
