<template>
  <div
    class="table-card"
    :class="stateClass"
    role="button"
    tabindex="0"
    @keydown.enter="$emit('enter')"
  >
    <!-- Número de mesa -->
    <div class="table-badge" :title="'Mesa ' + object.num_Table">
      {{ object.num_Table }}
    </div>

    <!-- Indicador de pedido pendiente -->
    <div
      v-if="isWaiting"
      class="pending-indicator"
      aria-label="Pedido pendiente"
      :title="pendingTitle"
    >
      <span class="ping"></span>
      <span class="dot"></span>
    </div>

    <!-- Contenido -->
    <div class="table-body">
      <div class="table-icon">🍽️</div>

      <div class="table-info">
        <div class="row">
          <span class="label">Capacidad</span>
          <span class="value">{{ object.num_Customers ?? '-' }}</span>
        </div>
        <div class="row">
          <span class="label">Camarero</span>
          <span class="value">#{{ object.waiter_id ?? '-' }}</span>
        </div>
      </div>
    </div>

    <!-- Chip de estado -->
    <div class="state-chip" :class="stateClass">
      {{ stateLabel }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

type TableObj = {
  id: number | string
  num_Table: number
  num_Customers?: number
  waiter_id?: number | string
  state?: 'FREE' | 'BUSY' | 'RESERVED' | 'WAITING' | string
  pendingOrder?: boolean
}

const props = defineProps<{
  object: TableObj
}>()

defineEmits<{
  (e: 'enter'): void
}>()

const isWaiting = computed(() => {
  // Preparado para cuando implementes la lógica real:
  // muestra el indicador si el estado es WAITING o si señalas pendingOrder = true
  return props.object.state === 'WAITING' || !!props.object.pendingOrder
})

const stateClass = computed(() => {
  switch ((props.object.state || 'FREE').toUpperCase()) {
    case 'BUSY':
      return 'is-busy'
    case 'RESERVED':
      return 'is-reserved'
    case 'WAITING':
      return 'is-waiting'
    default:
      return 'is-free'
  }
})

const stateLabel = computed(() => {
  const s = (props.object.state || 'FREE').toUpperCase()
  const map: Record<string, string> = {
    FREE: 'Libre',
    BUSY: 'Ocupada',
    RESERVED: 'Reservada',
    WAITING: 'Esperando pedido',
  }
  return map[s] ?? s
})

const pendingTitle = computed(() => `Esta mesa está esperando un pedido`)
</script>

<style scoped>
:root {
  --free-bg: linear-gradient(180deg, #e8f7ef, #d8f1e5);
  --free-border: #8fd4b3;

  --busy-bg: linear-gradient(180deg, #fdecea, #f9e0dd);
  --busy-border: #f08a7a;

  --reserved-bg: linear-gradient(180deg, #eef0ff, #e3e6ff);
  --reserved-border: #8a94ff;

  --waiting-bg: linear-gradient(180deg, #fff7e6, #ffefd1);
  --waiting-border: #f5b249;

  --shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
  --shadow-hover: 0 10px 24px rgba(0, 0, 0, 0.2);
}

.table-card {
  position: relative;
  width: 96px;
  height: 96px;
  border: 2px solid var(--free-border);
  border-radius: 14px;
  background: var(--free-bg);
  box-shadow: var(--shadow);
  display: grid;
  grid-template-rows: 1fr auto;
  outline: none;
  transition:
    transform 0.15s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease;
  user-select: none;
}

.table-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}

.table-card:focus-visible {
  box-shadow:
    0 0 0 3px rgba(0, 123, 255, 0.35),
    var(--shadow-hover);
}

/* Variantes por estado */
.table-card.is-busy {
  border-color: var(--busy-border);
  background: var(--busy-bg);
}
.table-card.is-reserved {
  border-color: var(--reserved-border);
  background: var(--reserved-bg);
}
.table-card.is-waiting {
  border-color: var(--waiting-border);
  background: var(--waiting-bg);
}

/* Badge con número de mesa */
.table-badge {
  position: absolute;
  top: -10px;
  left: -10px;
  background: #111827;
  color: #fff;
  font-weight: 700;
  font-size: 0.9rem;
  line-height: 1;
  padding: 6px 8px;
  border-radius: 10px;
  box-shadow: var(--shadow);
  min-width: 28px;
  text-align: center;
}

/* Indicador pedido pendiente (esquina superior derecha) */
.pending-indicator {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
}

.pending-indicator .ping {
  position: absolute;
  inset: 0;
  border-radius: 9999px;
  animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
  background: #f59e0b; /* ámbar */
  opacity: 0.4;
}

.pending-indicator .dot {
  position: absolute;
  inset: 4px;
  border-radius: 9999px;
  background: #d97706; /* ámbar más oscuro */
}

@keyframes ping {
  75%,
  100% {
    transform: scale(2);
    opacity: 0;
  }
}

/* Cuerpo */
.table-body {
  display: grid;
  place-items: center;
  padding-top: 6px;
}

.table-icon {
  font-size: 1.6rem;
  filter: drop-shadow(0 1px 0 rgba(0, 0, 0, 0.1));
}

.table-info {
  width: 88%;
  margin: 4px auto 6px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(17, 24, 39, 0.06);
  border-radius: 10px;
  padding: 6px;
  font-size: 0.72rem;
}

.row {
  display: flex;
  justify-content: space-between;
  line-height: 1.2;
}

.label {
  opacity: 0.65;
}

/* Chip de estado */
.state-chip {
  margin: 6px;
  padding: 4px 6px;
  font-size: 0.68rem;
  font-weight: 600;
  border-radius: 8px;
  width: fit-content;
  justify-self: end;
  background: rgba(17, 24, 39, 0.06);
  border: 1px solid rgba(17, 24, 39, 0.08);
}

.state-chip.is-free {
  background: rgba(16, 185, 129, 0.15);
  border-color: rgba(16, 185, 129, 0.35);
}
.state-chip.is-busy {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.35);
}
.state-chip.is-reserved {
  background: rgba(99, 102, 241, 0.15);
  border-color: rgba(99, 102, 241, 0.35);
}
.state-chip.is-waiting {
  background: rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.35);
}
</style>
