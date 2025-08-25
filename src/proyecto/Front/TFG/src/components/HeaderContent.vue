<template>
  <!-- This component renders dynamic content inside the topbar depending on the current view -->
  <div class="header-content" v-if="contentVisible">
    <!-- Content for the table plan (/application) view -->
    <template v-if="isApplication">
      <div class="legend" aria-label="Leyenda de estados de las mesas">
        <div class="legend-item" v-for="state in legendStates" :key="state.key">
          <span class="legend-dot" :class="state.class"></span>
          <span>{{ state.label }}</span>
        </div>
      </div>
      <div class="header-actions">
        <button class="btn secondary" @click="toggleDraggable">
          {{ draggable ? 'Bloquear' : 'Desbloquear' }}
        </button>
        <button class="btn primary" @click="goToAddTable">Añadir mesa</button>
      </div>
    </template>

    <!-- Content for the card (/card) view -->
    <template v-else-if="isCard">
      <div class="header-actions">
        <button class="btn primary" @click="goToAddProduct">Añadir producto</button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import useDrag from '../composables/useDrag'

// Retrieve shared draggable state and toggler
const { draggable, toggleDraggable } = useDrag()

const route = useRoute()
const router = useRouter()

// Determine if current route belongs to the application (table plan) section
const isApplication = computed(() => {
  return route.path.startsWith('/application')
})

// Determine if current route is exactly the card listing (without children)
// We only display the "Añadir producto" button on the root '/card' route.
const isCard = computed(() => {
  return route.path === '/card'
})

// Only render header content on these sections
const contentVisible = computed(() => isApplication.value || isCard.value)

// Legend definitions for table states used in the plan view
const legendStates = [
  { key: 'free', label: 'Libre', class: 'free' },
  { key: 'busy', label: 'Ocupada', class: 'busy' },
  // { key: 'reserved', label: 'Reservada', class: 'reserved' },
  // { key: 'waiting', label: 'Esperando pedido', class: 'waiting' },
]

// Navigate to the add table route
function goToAddTable() {
  router.push('/application/add')
}

// Navigate to the add product route
function goToAddProduct() {
  router.push('/card/add')
}
</script>

<style scoped>
/* Container wrapper for all header content */
.header-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
  margin-left: auto; /* push content to the right of the brand */
}

/* Legend styles mirrored from the original DragAndDrop component */
.legend {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.85rem;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  white-space: nowrap;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  display: inline-block;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.15) inset;
}

.legend-dot.free {
  background: #10b981;
}

.legend-dot.busy {
  background: #ef4444;
}

.legend-dot.reserved {
  background: #6366f1;
}

.legend-dot.waiting {
  background: #f59e0b;
}

/* Action button group */
.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

/* Generic button styles reused here to maintain consistency */
.btn {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition:
    background 0.2s ease,
    transform 0.06s ease;
}

.btn:active {
  transform: translateY(1px);
}

.btn.primary {
  background: #007bff;
  color: white;
}

.btn.primary:hover {
  background: #0062cc;
}

.btn.secondary {
  background: #6c757d;
  color: white;
}

.btn.secondary:hover {
  background: #5a6268;
}
</style>
