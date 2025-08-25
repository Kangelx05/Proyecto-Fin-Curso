<template>
  <transition name="slide-up">
    <!-- Cajón se muestra cuando el usuario lo indica, aunque no haya pedidos -->
    <div
      v-if="isVisible"
      class="bar-orders-drawer"
      role="complementary"
      aria-label="Pedidos pendientes de barra"
    >
      <div class="drawer-header">
        <span>Pedidos en barra: {{ barOrders.length }}</span>
        <button class="icon-btn close" aria-label="Cerrar" @click="closeDrawer">×</button>
      </div>
      <!-- Lista de pedidos en fila; si no hay pedidos muestra un mensaje -->
      <template v-if="barOrders.length > 0">
        <div class="orders-list" ref="ordersListRef">
          <div
            v-for="order in barOrders"
            :key="order.orderId"
            class="order-card"
            @dblclick="serveOrder(order.orderId)"
          >
            <div class="order-info">
              <span class="order-id">Pedido #{{ order.orderId }}</span>
              <span class="table-num">Mesa {{ order.tableNumber ?? order.tableId }}</span>
            </div>
            <ul class="items-list">
              <li v-for="(item, idx) in order.items" :key="idx">
                <span class="qty">{{ item.count }}×</span>
                <span class="name">{{ item.productName }}</span>
              </li>
            </ul>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="no-orders">No hay pedidos de barra pendientes</div>
      </template>
    </div>
  </transition>

  <!-- Botón para mostrar u ocultar el desplegable (siempre visible) -->
  <button class="drawer-toggle-btn" @click="toggleDrawer">
    {{ isVisible ? 'Ocultar barra' : 'Mostrar barra' }} ({{ barOrders.length }})
  </button>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useBarOrders } from '@/composables/useBarOrders'
import useAuth from '@/composables/useAuth'
import api from '@/services/api'

// Retrieve bar orders and fetch function from the composable
const { barOrders, fetchBarOrders } = useBarOrders()

// Token para autenticación del usuario
const { token } = useAuth()

// Controls visibility of the drawer
const isVisible = ref(false)
// Estado interno para detectar si el usuario ha cerrado manualmente
const userClosed = ref(false)

// Cargar pedidos pendientes al montar
onMounted(() => {
  // Llama a fetchBarOrders para obtener pedidos iniciales (SSE ya los actualiza)
  fetchBarOrders()
})

/**
 * Observa los cambios en la lista de pedidos de barra.  Si aparece un nuevo pedido
 * (un identificador que no estaba en la lista anterior) y el usuario no tiene
 * actualmente el cajón abierto, entonces lo despliega automáticamente.
 */
watch(
  barOrders,
  (newVal, oldVal) => {
    if (!newVal) return
    // Si el usuario ya lo tiene abierto, no forzamos nada
    if (isVisible.value) return
    // Determina si hay un nuevo pedido (ID no presente en la lista antigua)
    const newIds = new Set((newVal as any[]).map((o: any) => o.orderId))
    const oldIds = new Set((oldVal || []).map((o: any) => o.orderId))
    let hasNew = false
    newIds.forEach((id) => {
      if (!oldIds.has(id)) {
        hasNew = true
      }
    })
    if (hasNew) {
      isVisible.value = true
      userClosed.value = false
    }
  },
  { deep: true },
)

/**
 * Marca como servidas todas las bebidas de un pedido y elimina la tarjeta.
 * Envía una petición PUT al backend para actualizar los campos servedAt de los detalles.
 *
 * @param orderId identificador del pedido a marcar como servido
 */
async function serveOrder(orderId: number) {
  if (!orderId) return
  try {
    const url = `/order-detail/bar-pending/${orderId}/serve`
    const headers: any = {}
    // Adjunta el token JWT en el encabezado Authorization, aunque api ya lo añade si existe
    if (token.value) {
      headers['Authorization'] = `Bearer ${token.value}`
    }
    await api.put(url, null, { headers })
    // Tras marcar como servidas, el SSE enviará la lista actualizada automáticamente.
    // Opcionalmente, actualiza manualmente la lista para una respuesta inmediata
    // barOrders.value = barOrders.value.filter(o => o.orderId !== orderId)
  } catch (error) {
    console.error('Error al marcar pedido como servido:', error)
  }
}

function closeDrawer() {
  isVisible.value = false
  userClosed.value = true
}

function toggleDrawer() {
  isVisible.value = !isVisible.value
  if (!isVisible.value) {
    userClosed.value = true
  }
}
</script>

<style scoped>
.bar-orders-drawer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 50;
  background: var(--panel);
  color: var(--text);
  border-top: 1px solid var(--border);
  max-height: 40vh;
  overflow-y: auto;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.4);
  padding: 0.75rem;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

/* Contenedor de las tarjetas en fila horizontal */
.orders-list {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  gap: 0.75rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
}

/* Estilo de cada tarjeta de pedido */
.order-card {
  background: var(--bg-elev);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 200px;
  flex-shrink: 0;
}

.drawer-toggle-btn {
  position: fixed;
  bottom: calc(var(--topbar-h, 56px) + 0.5rem);
  right: 1rem;
  z-index: 60;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 0.85rem;
  background: var(--panel);
  color: var(--text);
  border: 1px solid var(--border);
  transition:
    background 0.2s ease,
    border-color 0.2s ease;
}
.drawer-toggle-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.18);
}
.order-info {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  font-weight: 600;
}

.items-list {
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 0.85rem;
}

.items-list li {
  display: flex;
  gap: 0.4rem;
  align-items: center;
}

.no-orders {
  padding: 1rem;
  text-align: center;
  font-size: 0.9rem;
  color: var(--text);
  opacity: 0.8;
}

.qty {
  font-weight: 600;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
}
</style>
