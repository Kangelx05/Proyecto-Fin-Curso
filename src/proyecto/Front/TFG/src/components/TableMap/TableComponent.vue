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
  // Muestra el indicador si el estado es WAITING o si hay un pedido pendiente (pendingOrder).
  const s = (props.object.state || '').toUpperCase()
  return s === 'WAITING' || !!props.object.pendingOrder
})

const stateClass = computed(() => {
  const s = (props.object.state || 'FREE').toUpperCase()
  switch (s) {
    case 'BUSY':
    case 'BUSSY':
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
    BUSSY: 'Ocupada',
    RESERVED: 'Reservada',
    WAITING: 'Esperando pedido',
  }
  return map[s] ?? s
})

const pendingTitle = computed(() => `Esta mesa está esperando un pedido`)
</script>

<style scoped>
:root {
  /* Fondo común para todas las tarjetas de mesa.  Utiliza un tono oscuro
     coherente con el panel principal de la aplicación para evitar
     transparencias y armonizar con la temática. */
  --table-bg: #1f2937;

  /* Colores de borde distintivos para los diferentes estados de mesa.  Se
     basan en los colores de la leyenda (verde para libres, rojo para
     ocupadas, índigo para reservadas, ámbar para en espera). */
  --free-bg: var(--table-bg);
  --free-border: #10b981;

  --busy-bg: var(--table-bg);
  --busy-border: #ef4444;

  --reserved-bg: var(--table-bg);
  --reserved-border: #6366f1;

  --waiting-bg: var(--table-bg);
  --waiting-border: #f59e0b;

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
  /* Fondo sólido para todo el cuerpo de la mesa, igual que el borde de la tarjeta,
     evitando transparencias residuales.  Además se replican las esquinas
     redondeadas inferiores para que coincidan con la tarjeta exterior. */
  background: #111827;
  border-radius: 10px;
}

.table-icon {
  font-size: 1.6rem;
  filter: drop-shadow(0 1px 0 rgba(0, 0, 0, 0.1));
}

.table-info {
  width: 88%;
  margin: 4px auto 6px;
  /* Fondo sólido para el bloque de información, en línea con la paleta oscura
     de la aplicación. Se elimina la transparencia para que la tarjeta de la
     mesa no parezca translúcida. */
  background: #111827;
  border: 1px solid rgba(255, 255, 255, 0.08);
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
  /* Fondo semi translúcido por defecto para etiquetas de estado.  Se
     utiliza un tono oscuro con baja opacidad que se integra con la
     temática general pero sin volverse totalmente opaco. */
  background: rgba(17, 24, 39, 0.06);
  border: 1px solid rgba(17, 24, 39, 0.08);
}

.state-chip.is-free {
  /* Etiqueta de estado para mesas libres: se usa el color verde con opacidad reducida para la etiqueta. */
  background: rgba(16, 185, 129, 0.5);
  border-color: rgba(16, 185, 129, 0.35);
}
.state-chip.is-busy {
  /* Etiqueta de estado para mesas ocupadas: utiliza un rojo translúcido similar a la versión original. */
  background: rgba(239, 68, 68, 0.5);
  border-color: rgba(239, 68, 68, 0.35);
}
.state-chip.is-reserved {
  /* Etiqueta de estado para mesas reservadas: vuelve al estilo translúcido original en color índigo. */
  background: rgba(99, 102, 241, 0.5);
  border-color: rgba(99, 102, 241, 0.35);
}
.state-chip.is-waiting {
  /* Etiqueta de estado para mesas en espera: estilo translúcido original con color ámbar. */
  background: rgba(245, 158, 11, 0.5);
  border-color: rgba(245, 158, 11, 0.35);
}
</style>
