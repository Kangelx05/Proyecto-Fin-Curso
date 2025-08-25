<template>
  <!-- Modal overlay to center the invoice within the current view -->
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content factura-modal">
      <!-- Encabezado de factura con botón para marcar como cobrado -->
      <div class="factura-header">
        <h2 class="modal-title">Factura de mesa {{ tableId }}</h2>
        <button
          v-if="!loading && details.length > 0"
          type="button"
          class="btn primary cobrar-btn"
          @click="cobrar"
        >
          Cobrado
        </button>
      </div>
      <div v-if="loading" class="loading">Cargando detalles…</div>
      <div v-else>
        <!-- Tabla de detalles de pedido -->
        <table class="factura-table" v-if="details.length > 0">
          <thead>
            <tr>
              <!-- Columna de selección: el checkbox del encabezado selecciona/desselecciona todas las filas -->
              <th class="select">
                <input
                  type="checkbox"
                  v-model="allSelected"
                  aria-label="Seleccionar todos los detalles"
                />
              </th>
              <th class="name">Nombre</th>
              <th class="price">Precio</th>
              <th class="amount">Cantidad</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in details" :key="idx">
              <!-- Casilla individual para seleccionar cada fila -->
              <td class="select">
                <input
                  type="checkbox"
                  v-model="item.selected"
                  @click.stop
                  :aria-label="`Seleccionar detalle ${idx + 1}`"
                />
              </td>
              <td class="name">{{ item.name }}</td>
              <td class="price">{{ formatPrice(item.precio) }}</td>
              <td class="amount">{{ item.amount }}</td>
            </tr>
          </tbody>
        </table>
        <!-- Fila de totales: muestra el total general y el total de filas seleccionadas -->
        <div v-if="details.length > 0" class="factura-totals">
          <span>Total factura: {{ formatPrice(totalFactura) }}</span>
          <span>selected: {{ formatPrice(selectedTotal) }}</span>
        </div>
        <p v-else class="empty">No hay detalles para esta mesa.</p>
        <!-- Mensaje de error si la carga falla -->
        <p v-if="error" class="error">{{ error }}</p>
      </div>
      <div class="modal-actions">
        <button type="button" class="btn secondary" @click="close">Cerrar</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useTables } from '../composables/useTables'

// Obtenemos el ID de la mesa desde la ruta
const route = useRoute()
const router = useRouter()
const tableId = route.params.tableId as string

// Estado local
// Cada detalle incluye un campo `selected` para gestionar la selección de filas
const details = ref<Array<{ name: string; precio: number; amount: number; selected?: boolean }>>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Identifier of the order associated with this table.  This will be
// extracted when the details are fetched to allow marking the order
// as finished.  It remains null until the data is loaded.
const orderId = ref<number | null>(null)

/**
 * Computed property para manejar la selección global de filas.
 * Su getter devuelve true si todas las filas están seleccionadas.
 * Su setter marca o desmarca todas las filas según el valor recibido.
 */
const allSelected = computed({
  get() {
    if (details.value.length === 0) return false
    return details.value.every((item) => item.selected)
  },
  set(value: boolean) {
    details.value.forEach((item) => {
      item.selected = value
    })
  },
})

/**
 * Calcula el total completo de la factura sumando el precio de cada artículo multiplicado por su cantidad.
 */
const totalFactura = computed(() => {
  return details.value.reduce((acc, item) => acc + item.precio * item.amount, 0)
})

/**
 * Calcula el total de las filas seleccionadas, considerando el precio y la cantidad de cada elemento seleccionado.
 */
const selectedTotal = computed(() => {
  return details.value
    .filter((item) => item.selected)
    .reduce((acc, item) => acc + item.precio * item.amount, 0)
})

/**
 * Carga los detalles de pedido de la mesa.  Intenta distintas rutas del backend
 * hasta encontrar una que devuelva una lista de detalles.
 */
async function fetchDetails() {
  // Endpoints potenciales que podrían existir en el backend para obtener detalles de una mesa
  const url = 'order-detail/table/' + tableId

  const res = await api.get(url)
  const data = res.data
  // Si recibimos un array, asumimos que son líneas de pedido
  if (Array.isArray(data)) {
    return data
  }
  // Si el objeto contiene un campo "details" o "items", úsalo
  if (data && typeof data === 'object') {
    if (Array.isArray((data as any).details)) {
      return (data as any).details
    }
    if (Array.isArray((data as any).items)) {
      return (data as any).items
    }
  }

  // Si ninguna ruta funcionó, lanzamos un error
  throw new Error('No se encontraron detalles de pedido para esta mesa.')
}

onMounted(async () => {
  try {
    const rawDetails = await fetchDetails()
    // Determine the order id from the first detail entry, if available.
    if (Array.isArray(rawDetails) && rawDetails.length > 0) {
      const first: any = rawDetails[0]
      // The API may embed the order under different field names.  Attempt
      // to extract the id from `order` or `pedido` objects.
      const maybeOrder: any = (first as any).order ?? (first as any).pedido ?? null
      if (maybeOrder && typeof maybeOrder === 'object' && maybeOrder.id != null) {
        orderId.value = Number(maybeOrder.id)
      }
    }
    // Agrupa las líneas por nombre y precio para obtener la cantidad
    const grouped: Record<string, { name: string; precio: number; amount: number }> = {}
    rawDetails.forEach((d: any) => {
      // Compatibilidad de nombres de campos
      const name = d.productName ?? d.product_name ?? d.name ?? ''
      const price = d.unitPrice ?? d.unit_price ?? d.price ?? 0
      const key = `${name}|${price}`
      if (!grouped[key]) {
        grouped[key] = { name, precio: price, amount: 0 }
      }
      grouped[key].amount++
    })
    // Añadimos la propiedad `selected` en false a cada objeto para permitir la selección
    details.value = Object.values(grouped).map((item) => ({ ...item, selected: false }))
  } catch (e: any) {
    console.error(e)
    error.value = e?.message || 'Error al obtener los detalles.'
  } finally {
    loading.value = false
  }
})

// Formatea un número como precio en euros
function formatPrice(value: number) {
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(value)
}

function close() {
  router.back()
}

/**
 * Marca la orden asociada como cobrada.  Llama al endpoint del backend para
 * finalizar la orden y liberar la mesa.  Si la operación es exitosa,
 * se navega de vuelta a la vista anterior.  Si se produce un error,
 * se asigna un mensaje descriptivo a la variable de error.
 */
async function cobrar() {
  const { reloadContent } = useTables()

  if (!orderId.value) {
    error.value = 'No se ha podido determinar el identificador del pedido.'
    return
  }
  try {
    await api.put(`/order/${orderId.value}/finish`)
    reloadContent()
    router.back()
  } catch (e: any) {
    console.error(e)
    // Extrae el mensaje de error desde la respuesta del servidor si existe
    error.value = e?.response?.data?.message || 'Error al finalizar el pedido.'
  }
}
</script>

<style scoped>
@import '../components/modal-styles.css';
/* Anchura más amplia para la ventana de factura */
.factura-modal {
  width: 600px;
  max-width: 95%;
  background: var(--panel);
  color: var(--text);
  border-radius: 12px;
  padding: 1.5rem;
}

/* Heredamos estilos base de modal-content */
.modal-title {
  margin-bottom: 1rem;
  font-size: 1.4rem;
  font-weight: 600;
  text-align: center;
}

.loading {
  font-style: italic;
  color: #d1d5db;
  margin-bottom: 1rem;
}

.empty {
  font-style: italic;
  color: #6b7280;
  margin: 1rem 0;
}

.error {
  color: #ef4444;
  margin-top: 1rem;
  text-align: center;
}

.factura-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
}

.factura-table th,
.factura-table td {
  padding: 0.6rem;
  text-align: left;
}

/* Encabezado de la factura que alinea el título y el botón de cobro */
.factura-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

/* Ajusta la separación del botón de cobro respecto al título */
.cobrar-btn {
  margin-left: 1rem;
}

.factura-table thead th {
  background: var(--bg-elev);
  color: var(--text);
  border-bottom: 2px solid var(--border);
}

/* Alterna tonalidades usando variables globales para mantener coherencia */
.factura-table tbody tr:nth-child(odd) {
  background: var(--panel);
}
.factura-table tbody tr:nth-child(even) {
  background: var(--bg-elev);
}
.factura-table tbody td {
  border-bottom: 1px solid #374151;
}

/* Ajusta la anchura y alineación de la columna de selección */
.factura-table th.select,
.factura-table td.select {
  width: 2rem;
  text-align: center;
}

/* Contenedor de totales: coloca los totales general y seleccionado en extremos opuestos */
.factura-totals {
  display: flex;
  justify-content: space-between;
  font-weight: 600;
  margin-top: 0.5rem;
}

.modal-actions {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: background 0.2s ease;
}
.btn.secondary {
  background: #6c757d;
  color: white;
}
.btn.secondary:hover {
  background: #5a6268;
}
</style>
