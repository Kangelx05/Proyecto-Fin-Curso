<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content wide">
      <h2 class="modal-title">Añadir nueva mesa</h2>

      <form @submit.prevent="submitForm" class="form-container form-grid">
        <!-- Número de mesa -->
        <div class="form-group">
          <label for="numTable">Número de mesa</label>
          <input id="numTable" v-model.number="formData.num_Table" type="number" min="0" required />
        </div>

        <!-- Capacidad -->
        <div class="form-group">
          <label for="numCustomers">Capacidad (nº de clientes)</label>
          <input
            id="numCustomers"
            v-model.number="formData.num_Customers"
            type="number"
            min="1"
            required
          />
        </div>

        <!-- Camarero -->
        <div class="form-group">
          <label for="waiterSelect">Camarero asignado</label>
          <select
            id="waiterSelect"
            v-model.number="formData.waiter"
            :disabled="loadingUsers || users.length === 0"
            required
          >
            <option value="" disabled>Selecciona camarero</option>
            <option v-for="u in users" :key="u.id" :value="u.id">
              {{ displayUser(u) }}
            </option>
          </select>
        </div>

        <!-- Estado -->
        <div class="form-group">
          <label for="stateSelect">Estado de la mesa</label>
          <select id="stateSelect" v-model="formData.state" required>
            <option v-for="s in states" :key="s" :value="s">{{ stateLabel(s) }}</option>
          </select>
        </div>

        <!-- Coordenadas -->
        <div class="form-group coords">
          <div>
            <label for="posX">Posición X</label>
            <input id="posX" v-model.number="formData.posX" type="number" required />
          </div>
          <div>
            <label for="posY">Posición Y</label>
            <input id="posY" v-model.number="formData.posY" type="number" required />
          </div>
          <div class="center-btn">
            <label>&nbsp;</label>
            <!-- vacío para alinear -->
            <button type="button" class="btn tertiary" @click="centerOnBoard" :disabled="centering">
              {{ centering ? 'Calculando…' : 'Centrar' }}
            </button>
          </div>
        </div>

        <div class="modal-actions">
          <button class="btn primary" type="submit" :disabled="submitting || !isValid">
            {{ submitting ? 'Guardando…' : 'Guardar' }}
          </button>
          <button class="btn secondary" type="button" @click="close">Cancelar</button>
        </div>

        <p v-if="error" class="error">{{ error }}</p>
      </form>

      <!-- Preview -->
      <section class="preview-section">
        <h3>Vista previa</h3>

        <div class="preview">
          <div class="preview-card" :class="stateClass">
            <div class="table-badge">{{ previewObj.num_Table || '-' }}</div>

            <div v-if="isWaiting" class="pending-indicator" title="Pedido pendiente">
              <span class="ping"></span><span class="dot"></span>
            </div>

            <div class="preview-body">
              <div class="table-icon">🍽️</div>
              <div class="table-info">
                <div class="row">
                  <span class="label">Capacidad</span
                  ><span class="value">{{ previewObj.num_Customers ?? '-' }}</span>
                </div>
                <div class="row">
                  <span class="label">Camarero</span
                  ><span class="value">#{{ previewObj.waiter_id ?? '-' }}</span>
                </div>
              </div>
            </div>

            <div class="state-chip" :class="stateClass">{{ stateLabel(formData.state) }}</div>
          </div>
        </div>

        <small class="preview-note muted"
          >* La posición (X/Y) solo afecta al plano, no a esta vista previa.</small
        >
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useTables } from '../composables/useTables'

type User = {
  id: number
  name?: string
  username?: string
  fullName?: string
  email?: string
  [k: string]: any
}

const { reloadContent } = useTables()
const router = useRouter()

const users = ref<User[]>([])
const loadingUsers = ref(true)
const submitting = ref(false)
const centering = ref(false)
const error = ref<string | null>(null)

const states = ['FREE', 'BUSY', 'RESERVED', 'WAITING'] as const

const formData = ref({
  num_Table: 0,
  num_Customers: 1,
  waiter: undefined as number | undefined,
  state: 'FREE' as (typeof states)[number],
  posX: 600,
  posY: 500,
})

/** ===== Carga de camareros con caché ===== */
const CACHE_KEY = 'waiters_cache_v1'
const CACHE_TTL_MS = 5 * 60 * 1000 // 5 min

onMounted(async () => {
  // 1) Cargar waiters con caché
  try {
    const cachedRaw = localStorage.getItem(CACHE_KEY)
    let fromCache = false
    if (cachedRaw) {
      const cached = JSON.parse(cachedRaw)
      if (Date.now() - cached.time < CACHE_TTL_MS && Array.isArray(cached.data)) {
        users.value = cached.data
        fromCache = true
      }
    }
    if (!fromCache) {
      const res = await axios.get('http://localhost:8081/users')
      users.value = Array.isArray(res.data) ? res.data : []
      localStorage.setItem(CACHE_KEY, JSON.stringify({ time: Date.now(), data: users.value }))
    }
    if (!formData.value.waiter && users.value.length > 0) {
      formData.value.waiter = users.value[0].id
    }
  } catch (e) {
    console.error(e)
    error.value = 'No se pudieron cargar los camareros.'
  } finally {
    loadingUsers.value = false
  }

  // 2) Centrar en el plano al abrir
  await centerOnBoard()
})

/** Centra posX/posY en el área del plano (#escritorio) */
async function centerOnBoard() {
  try {
    centering.value = true
    // Espera al próximo tick por si la vista aún no está del todo montada
    await Promise.resolve()
    const board = document.getElementById('escritorio')
    if (board) {
      const rect = board.getBoundingClientRect()
      // Coordenadas relativas al plano (su origen es 0,0 arriba-izquierda del contenedor)
      formData.value.posX = Math.round(rect.width / 2)
      formData.value.posY = Math.round(rect.height / 2)
    }
  } finally {
    centering.value = false
  }
}

/** ===== Validación ===== */
const isValid = computed(() => {
  const f = formData.value
  return (
    Number.isInteger(f.num_Table) &&
    f.num_Table >= 0 &&
    Number.isInteger(f.num_Customers) &&
    f.num_Customers > 0 &&
    typeof f.waiter === 'number' &&
    !!f.state &&
    Number.isFinite(f.posX) &&
    Number.isFinite(f.posY)
  )
})

/** ===== Preview ===== */
const previewObj = computed(() => ({
  id: -1,
  num_Table: formData.value.num_Table,
  num_Customers: formData.value.num_Customers,
  waiter_id: formData.value.waiter,
  state: formData.value.state,
  pendingOrder: formData.value.state === 'WAITING',
}))

const isWaiting = computed(
  () => previewObj.value.state === 'WAITING' || !!previewObj.value.pendingOrder,
)

const stateClass = computed(() => {
  switch (formData.value.state) {
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

/** ===== Submit ===== */
async function submitForm() {
  if (!isValid.value || submitting.value) return
  submitting.value = true
  error.value = null

  const payload = {
    num_Table: formData.value.num_Table,
    num_Customers: formData.value.num_Customers,
    waiter: formData.value.waiter as number,
    state: formData.value.state,
    posX: formData.value.posX,
    posY: formData.value.posY,
  }

  try {
    const res = await axios.post('http://localhost:8081/table', payload)
    console.log('Mesa creada:', res.data)
    reloadContent()
    close()
  } catch (e: any) {
    console.error(e)
    error.value = e?.response?.data?.message || 'No se pudo crear la mesa.'
  } finally {
    submitting.value = false
  }
}

function close() {
  router.back()
}

function displayUser(u: User) {
  return u.name || u.fullName || u.username || u.email || `Usuario #${u.id}`
}

function stateLabel(s: (typeof states)[number]) {
  const map: Record<string, string> = {
    FREE: 'Libre',
    BUSY: 'Ocupada',
    RESERVED: 'Reservada',
    WAITING: 'Esperando pedido',
  }
  return map[s] ?? s
}
</script>

<style scoped>
@import './modal-styles.css';

.form-group label {
  display: block;
  margin-bottom: 0.35rem;
  font-size: 0.85rem;
  color: var(--muted);
}
.form-group input,
.form-group select {
  width: 100%;
}
.coords label {
  margin-bottom: 0.35rem;
}
.center-btn {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

/* Modal ancho con fondo oscuro */
.modal-content.wide {
  width: 720px;
  max-width: 92vw;
  background: var(--panel);
  color: var(--text);
}

/* Grid del formulario */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}
.form-grid .modal-actions {
  grid-column: 1 / -1;
}
.form-grid input,
.form-grid select {
  width: 100%;
  background: var(--bg-elev);
  color: var(--text);
  border: 1px solid var(--border);
}

input,
select {
  padding: 0.6rem;
  border-radius: 6px;
  outline: none;
  transition:
    border 0.2s ease,
    box-shadow 0.2s ease;
}
input:focus,
select:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}

/* Inputs y botón centrar */
.coords {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 0.5rem;
  margin-bottom: 1rem; /* separación extra antes de la preview */
}

/* Select flecha custom */
select {
  appearance: none;
  background-image:
    linear-gradient(45deg, transparent 50%, var(--muted) 50%),
    linear-gradient(135deg, var(--muted) 50%, transparent 50%),
    linear-gradient(to right, var(--border), var(--border));
  background-position:
    calc(100% - 18px) calc(50% - 3px),
    calc(100% - 13px) calc(50% - 3px),
    calc(100% - 2.2rem) 50%;
  background-size:
    6px 6px,
    6px 6px,
    1px 60%;
  background-repeat: no-repeat;
}

/* Botón terciario */
.btn.tertiary {
  border: 1px solid var(--border);
  background: var(--bg-elev);
  color: var(--text);
}
.btn.tertiary:hover {
  background: rgba(255, 255, 255, 0.08);
}

/* ================== Preview (aislada) ================== */
.preview-section {
  margin-top: 1rem;
  background: var(--bg-elev);
  color: var(--text);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1rem;
}

.preview-section h3 {
  margin: 0 0 0.75rem;
  font-size: 1rem;
  text-align: center;
}

.preview-section .preview {
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-section .preview-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1; /* ocupa el espacio disponible */
  padding: 6px;
  text-align: center;
}

/* Tarjeta de mesa (solo preview) */
.preview-section .preview-card {
  position: relative;
  width: 112px;
  height: 112px;
  border: 2px solid var(--border);
  border-radius: 14px;
  background: var(--bg);
  color: var(--text);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
  display: grid;
  grid-template-rows: 1fr auto;
}
.preview-section .preview-card.is-free {
  border-color: rgba(16, 185, 129, 0.5);
  background: rgba(16, 185, 129, 0.1);
}
.preview-section .preview-card.is-busy {
  border-color: rgba(239, 68, 68, 0.5);
  background: rgba(239, 68, 68, 0.1);
}
.preview-section .preview-card.is-reserved {
  border-color: rgba(99, 102, 241, 0.5);
  background: rgba(99, 102, 241, 0.1);
}
.preview-section .preview-card.is-waiting {
  border-color: rgba(245, 158, 11, 0.5);
  background: rgba(245, 158, 11, 0.1);
}

/* Badge número */
.preview-section .table-badge {
  position: absolute;
  top: -10px;
  left: -10px;
  background: var(--bg-elev);
  color: var(--text);
  font-weight: 700;
  font-size: 0.9rem;
  line-height: 1;
  padding: 6px 8px;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
  min-width: 28px;
  text-align: center;
}

/* Icono */
.preview-section .table-icon {
  font-size: 1.6rem;
  filter: drop-shadow(0 1px 0 rgba(0, 0, 0, 0.1));
}

/* Info */
.preview-section .table-info {
  width: 88%;
  margin: 4px auto 6px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 6px;
  font-size: 0.72rem;
}
.preview-section .row {
  display: flex;
  justify-content: space-between;
  line-height: 1.2;
}
.preview-section .label {
  opacity: 0.85;
}

/* Estado chip */
.preview-section .state-chip {
  margin: 6px;
  padding: 4px 6px;
  font-size: 0.68rem;
  font-weight: 600;
  border-radius: 8px;
  width: fit-content;
  justify-self: end;
}
.preview-section .state-chip.is-free {
  background: rgba(16, 185, 129, 0.15);
  border: 1px solid rgba(16, 185, 129, 0.35);
}
.preview-section .state-chip.is-busy {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.35);
}
.preview-section .state-chip.is-reserved {
  background: rgba(99, 102, 241, 0.15);
  border: 1px solid rgba(99, 102, 241, 0.35);
}
.preview-section .state-chip.is-waiting {
  background: rgba(245, 158, 11, 0.15);
  border: 1px solid rgba(245, 158, 11, 0.35);
}

/* Indicador pedido pendiente */
.preview-section .pending-indicator .ping {
  background: #f59e0b;
  opacity: 0.4;
}
.preview-section .pending-indicator .dot {
  background: #d97706;
}

/* Nota */
.preview-note {
  display: block;
  margin-top: 0.75rem;
  text-align: center;
  font-size: 0.8rem;
  color: var(--muted);
}

/* Error */
.error {
  color: #ef4444;
  margin-top: 0.5rem;
}
</style>
