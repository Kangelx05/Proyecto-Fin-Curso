<template>
  <div>
    <div
      v-for="(productos, categoria) in productosAgrupados"
      :key="categoria"
      class="categoria"
      wid
    >
      <button @click="toggleCategoria(categoria)" class="categoria-btn">
        {{ categoria }}
        <span class="flecha">{{ categoriasAbiertas[categoria] ? '▲' : '▼' }}</span>
      </button>

      <ul v-if="categoriasAbiertas[categoria]" class="lista-productos">
        <li v-for="producto in productos" :key="producto.id" class="producto">
          {{ producto.name }}
          <button @click="editarProducto(producto)">Edit</button>
        </li>
      </ul>
    </div>
  </div>
  <router-view></router-view>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
// Tipos del modelo que devuelve la API
interface Producto {
  id: number
  name: string
  data: Uint8Array | null
  description: string
  price: number
  category: string
}

// Lista de productos cargada desde la API
const productos = ref<Producto[]>([])

// Agrupación de productos por categoría
type ProductosAgrupados = Record<string, Producto[]>

const productosAgrupados = computed<ProductosAgrupados>(() => {
  return productos.value.reduce((acc: ProductosAgrupados, producto: Producto) => {
    const cat = producto.category
    if (!acc[cat]) acc[cat] = []
    acc[cat].push(producto)
    return acc
  }, {})
})

// Controlar qué categorías están abiertas
const categoriasAbiertas = reactive<Record<string, boolean>>({})

function toggleCategoria(categoria: string): void {
  categoriasAbiertas[categoria] = !categoriasAbiertas[categoria]
}

function editarProducto(producto: { id: number }) {
  router.push(`/update/${producto.id}`)
}

// Llamada a la API
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
  font-family: system-ui, sans-serif;
}

.categoria {
  margin-bottom: 1rem;
  border: 1px solid #374151;
  border-radius: 0.5rem;
  background-color: #1f2937;
  overflow: hidden;
}

.categoria-btn {
  width: 100%;
  text-align: left;
  padding: 0.75rem 1rem;
  background-color: #374151;
  color: #e5e7eb;
  font-weight: 600;
  border: none;
  cursor: pointer;
}

.categoria-btn:hover {
  background-color: #4b5563;
}

.flecha {
  float: right;
}

.lista-productos {
  background-color: #111827;
  padding: 0.75rem 1.5rem;
  list-style: none;
  margin: 0;
}

.producto {
  padding: 0.5rem 0;
  border-bottom: 1px solid #374151;
}

.producto:last-child {
  border-bottom: none;
}
</style>
