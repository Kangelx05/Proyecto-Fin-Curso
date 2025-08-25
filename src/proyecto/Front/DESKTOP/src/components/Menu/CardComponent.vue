<template>
  <!--
    Envolvemos todo el contenido de la vista en un contenedor para poder centrarlo
    y añadir márgenes laterales.  También situamos el <router-view> dentro de este
    contenedor para que cualquier vista hija (editar/añadir) herede dichos márgenes.
  -->
  <div class="card-page">
    <!-- Agrupación de productos por categoría -->
    <div
      v-for="(productos, categoria) in productosAgrupados"
      :key="categoria"
      class="categoria"
    >
      <!-- Botón que abre/cierra una categoría -->
      <button
        @click="toggleCategoria(categoria)"
        class="categoria-btn"
        :aria-expanded="categoriasAbiertas[categoria] || false"
      >
        {{ categoria }}
        <span class="flecha">{{ categoriasAbiertas[categoria] ? '▲' : '▼' }}</span>
      </button>

      <!-- Lista de productos dentro de la categoría, mostrados en un grid -->
      <ul v-if="categoriasAbiertas[categoria]" class="lista-productos grid-cards">
        <li
          v-for="producto in productos"
          :key="producto.id"
          class="card"
          @dblclick="editarProducto(producto)"
        >
          <!-- Imagen del producto o una imagen de marcador de posición -->
          <div class="card-image-container">
            <img :src="getImageSrc(producto)" class="card-image" alt="Imagen del producto" />
          </div>
          <!-- Cuerpo de la tarjeta -->
          <div class="card-body">
            <h3 class="card-title">{{ producto.name }}</h3>
            <p class="card-desc">{{ producto.description }}</p>
          </div>
          <!-- Pie de la tarjeta con precio -->
          <div class="card-footer">
            <span class="price">{{ formatPrice(producto.price) }}</span>
          </div>
        </li>
      </ul>
    </div>
    <!-- Espacio para las vistas hijas de Vue Router -->
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted, watch } from 'vue'
import api from '@/services/api'
import { useRouter, useRoute } from 'vue-router'

// Importamos una imagen de marcador de posición para los productos que no tienen datos binarios.
import placeholder from '../../assets/placeholder.png'

// Instancia del enrutador para navegar a la pantalla de edición
const router = useRouter()
const route = useRoute()

// Definición del modelo de producto que devuelve la API
interface Producto {
  id: number
  name: string
  /**
   * Datos de la imagen del producto.  Puede ser un Array de bytes cuando
   * proviene de la API (Uint8Array), una cadena base64 sin prefijo o
   * incluso una URL de datos completa (data:image/...).  También puede
   * ser null si el producto no tiene imagen.
   */
  data: Uint8Array | string | null
  description: string
  price: number
  category: string
}

// Estado local: lista de productos cargados desde la API
const productos = ref<Producto[]>([])

// Agrupar productos por categoría para renderizar secciones
type ProductosAgrupados = Record<string, Producto[]>
const productosAgrupados = computed<ProductosAgrupados>(() => {
  return productos.value.reduce((acc: ProductosAgrupados, producto: Producto) => {
    const cat = producto.category || 'Sin categoría'
    if (!acc[cat]) acc[cat] = []
    acc[cat].push(producto)
    return acc
  }, {})
})

// Estado reactivo para controlar qué categorías están abiertas (expanded)
const categoriasAbiertas = reactive<Record<string, boolean>>({})
function toggleCategoria(categoria: string): void {
  categoriasAbiertas[categoria] = !categoriasAbiertas[categoria]
}

// Formatear el precio como moneda en euros
function formatPrice(n: number): string {
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(n)
}

/*
 * Detecta el tipo MIME de una imagen a partir de los primeros bytes del
 * ArrayBuffer.  Soporta los formatos más comunes: PNG, JPEG, GIF y WebP.
 */
function detectMimeFromBytes(u8: Uint8Array): string {
  // PNG: 89 50 4E 47
  if (u8.length >= 4 && u8[0] === 0x89 && u8[1] === 0x50 && u8[2] === 0x4e && u8[3] === 0x47) {
    return 'image/png'
  }
  // JPEG: FF D8 FF
  if (u8.length >= 3 && u8[0] === 0xff && u8[1] === 0xd8 && u8[2] === 0xff) {
    return 'image/jpeg'
  }
  // GIF: 47 49 46 38 ("GIF8")
  if (u8.length >= 4 && u8[0] === 0x47 && u8[1] === 0x49 && u8[2] === 0x46 && u8[3] === 0x38) {
    return 'image/gif'
  }
  // WebP: "RIFF....WEBP"
  if (
    u8.length >= 12 &&
    u8[0] === 0x52 &&
    u8[1] === 0x49 &&
    u8[2] === 0x46 &&
    u8[3] === 0x46 &&
    u8[8] === 0x57 &&
    u8[9] === 0x45 &&
    u8[10] === 0x42 &&
    u8[11] === 0x50
  ) {
    return 'image/webp'
  }
  // Valor por defecto razonable si no se reconoce ninguna cabecera
  return 'image/png'
}

/*
 * Detecta el tipo MIME a partir de los primeros caracteres de una cadena
 * base64.  Este método utiliza las firmas base64 típicas de algunos
 * formatos de imagen para adivinar el MIME.  Si la firma no coincide
 * con ningún formato conocido, se devuelve image/png por defecto.
 */
function detectMimeFromBase64(b64: string): string {
  if (b64.startsWith('iVBOR')) {
    return 'image/png'
  }
  if (b64.startsWith('/9j/')) {
    return 'image/jpeg'
  }
  if (b64.startsWith('R0lGOD')) {
    return 'image/gif'
  }
  // WebP en base64 comienza con "UklGR" que corresponde a RIFF
  if (b64.startsWith('UklGR')) {
    return 'image/webp'
  }
  return 'image/png'
}

/*
 * Convierte un Uint8Array a una cadena base64.  Divide el array en
 * pequeños bloques para evitar problemas de memoria cuando las imágenes
 * son grandes.
 */
function toBase64(u8: Uint8Array): string {
  let binary = ''
  const chunkSize = 0x8000
  for (let i = 0; i < u8.length; i += chunkSize) {
    const chunk = u8.subarray(i, i + chunkSize)
    binary += String.fromCharCode(...Array.from(chunk))
  }
  return btoa(binary)
}

/*
 * Devuelve la URL de la imagen que se mostrará en la tarjeta.  Si el producto incluye datos binarios,
 * se convierten a una cadena base64 y se construye un URI data:image.  En caso contrario se devuelve
 * la imagen de marcador de posición importada arriba.
 */
function getImageSrc(p: Producto): string {
  const data: unknown = p.data
  // Si no hay datos de imagen, devolver el placeholder
  if (!data) return placeholder

  // Si los datos ya vienen como una cadena
  if (typeof data === 'string') {
    // Si la cadena es un Data URI completo, úsalo tal cual
    if (data.startsWith('data:image')) {
      return data
    }
    // Si es una cadena base64 sin prefijo, detectamos el MIME y construimos la URL
    const mime = detectMimeFromBase64(data)
    return `data:${mime};base64,${data}`
  }

  // Si los datos son binarios (Uint8Array o ArrayBuffer)
  try {
    const u8 = new Uint8Array(data as ArrayBuffer)
    // Si no hay bytes, devolver el placeholder
    if (!u8.length) return placeholder
    const mime = detectMimeFromBytes(u8)
    const base64 = toBase64(u8)
    return `data:${mime};base64,${base64}`
  } catch (e) {
    // Si falla la conversión por cualquier motivo, usar placeholder
    return placeholder
  }
}

// Navega a la vista de edición del producto
/**
 * Navega a la vista de edición del producto.  El componente de edición es una ruta hija de
 * "/card" con la ruta relativa "update/:id", por lo que debemos incluir el prefijo
 * "/card" para evitar que el enrutador interprete la ruta como absoluta y nos redirija
 * a la pantalla principal.  Al utilizar una ruta absoluta incorrecta (por ejemplo
 * "/update/:id"), el guardián de rutas coincide con la ruta comodín y nos lleva a
 * "/application".  Por ello construimos la ruta completa aquí.
 */
function editarProducto(producto: { id: number }) {
  // Utiliza el nombre de la ruta para mayor claridad; también podríamos hacer
  // router.push(`/card/update/${producto.id}`)
  router.push({ name: 'card-update', params: { id: producto.id } })
}

// Cargar la lista de productos desde la API al montar el componente
// Función para cargar productos desde la API
async function loadProducts() {
  try {
    const response = await api.get<Producto[]>('/cardProduct')
    productos.value = response.data
  } catch (error) {
    console.error('Error al cargar productos:', error)
  }
}

// Cargar al montar
onMounted(() => {
  loadProducts()
})

// Recargar productos cuando se vuelve a la vista principal de /card
watch(
  () => route.fullPath,
  (newPath) => {
    // Si estamos en la ruta exacta '/card', volver a cargar
    if (newPath === '/card') {
      loadProducts()
    }
  },
)
</script>

<style scoped>
/* Colores base coherentes con el tema oscuro */
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

/* Estilos para la tarjeta de producto y la categorización */
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
  /* Si la lista de productos es demasiado grande para caber en pantalla,
     permitimos que el usuario se desplace verticalmente dentro de la propia
     lista.  Se limita la altura máxima al 70 % de la altura de la ventana
     para que nunca ocupe más espacio del disponible. */
  max-height: 70vh;
  overflow-y: auto;
}

/* Diseño en grid similar al frontend móvil */
.grid-cards {
  display: grid;
  /* Utilizamos auto-fill para que se ajusten tantos elementos como
     quepan en una fila.  El ancho mínimo de cada tarjeta se reduce a
     160 px para que las tarjetas sean aproximadamente la mitad del
     tamaño original y quepan más tarjetas por fila. */
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 1rem;
 }
 @media (min-width: 640px) {
  .grid-cards {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
 }
 @media (min-width: 1024px) {
  .grid-cards {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
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
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.45);
  border-color: #4b5563;
}

/* Contenedor de la imagen en la tarjeta con altura fija */
.card-image-container {
  width: 100%;
  /* Reducimos la altura de la imagen para que las tarjetas sean más
     compactas (aproximadamente la mitad de la versión original) */
  height: 100px;
  overflow: hidden;
  border-bottom: 1px solid #374151;
}
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-title {
  margin: 0;
  /* Reducimos el tamaño de la fuente para adaptarnos al tamaño más
     pequeño de la tarjeta */
  font-size: 0.9rem;
  font-weight: 800;
  line-height: 1.2;
  color: #f3f4f6;
  letter-spacing: 0.2px;
}
.card-desc {
  margin: 0.25rem 0 0;
  color: #cbd5e1;
  /* También reducimos ligeramente el tamaño de la descripción y
     mostramos menos líneas para mantener las tarjetas compactas */
  font-size: 0.8rem;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  gap: 0.75rem;
  /* Añadimos padding para separar el precio del borde inferior y lateral */
  padding: 0 0.75rem 0.75rem;
}
.price {
  font-weight: 800;
  padding: 0.35rem 0.6rem;
  border-radius: 9999px;
  background: #10b9811a;
  color: #34d399;
  border: 1px solid #065f46;
  font-variant-numeric: tabular-nums;
  /* Disminuimos el tamaño de la tipografía del precio para que
     corresponda con el tamaño general de la tarjeta */
  font-size: 0.85rem;
}

/* Cuerpo de la tarjeta: añadimos margen interno para que el texto no quede pegado */
.card-body {
  /* Reducimos el padding lateral para las tarjetas compactas */
  padding: 0 0.75rem;
}

/* Contenedor principal: centrado y con márgenes laterales */
.card-page {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  /* Espacio lateral para que el contenido no toque los bordes de la pantalla */
  padding: 0 1rem;
  /* Permite que la lista de categorías completa se desplace dentro del área de contenido.
     Al establecer la altura al 100% hacemos que el contenedor ocupe toda la altura
     disponible en la sección .content (que ya tiene en cuenta la barra superior).
     De este modo, si hay muchas categorías o productos, el usuario puede hacer
     scroll vertical sin que se oculte contenido. */
  height: 100%;
  overflow-y: auto;
}
</style>
