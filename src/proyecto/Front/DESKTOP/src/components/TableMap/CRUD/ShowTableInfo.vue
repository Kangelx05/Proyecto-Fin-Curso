<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content wide">
      <h2 class="modal-title">Información de la mesa</h2>
      <form class="form-container form-grid">
        <!-- Número de mesa -->
        <div class="form-group">
          <label for="numTable">Número de mesa</label>
          <input id="numTable" type="number" :value="tableData.num_Table" disabled />
        </div>

        <!-- Capacidad -->
        <div class="form-group">
          <label for="numCustomers">Capacidad (nº de clientes)</label>
          <input id="numCustomers" type="number" :value="tableData.num_Customers" disabled />
        </div>

        <!-- Camarero -->
        <div class="form-group">
          <label for="waiterSelect">Camarero asignado</label>
          <select id="waiterSelect" :value="tableData.waiter_id" disabled>
            <option value="" disabled>Sin camarero</option>
            <option v-for="u in users" :key="u.id" :value="u.id">
              {{ displayUser(u) }}
            </option>
          </select>
        </div>

        <!-- Estado -->
        <div class="form-group">
          <label for="stateSelect">Estado de la mesa</label>
          <select id="stateSelect" :value="tableData.state" disabled>
            <option v-for="s in states" :key="s" :value="s">{{ stateLabel(s) }}</option>
          </select>
        </div>

        <!-- Coordenadas -->
        <div class="form-group coords">
          <div>
            <label for="posX">Posición X</label>
            <input id="posX" type="number" :value="tableData.posX" disabled />
          </div>
          <div>
            <label for="posY">Posición Y</label>
            <input id="posY" type="number" :value="tableData.posY" disabled />
          </div>
          <!-- Se deja un tercer elemento vacío para mantener la alineación como en AddTable -->
          <div class="center-btn"></div>
        </div>

        <!-- Token de la mesa -->
        <div class="form-group">
          <label for="tokenField">Token</label>
          <div class="token-row">
            <input id="tokenField" type="text" :value="tableToken || ''" disabled />
            <button type="button" class="btn tertiary" @click="copyToken" :disabled="!tableToken">
              Copiar
            </button>
            <button
              type="button"
              class="btn tertiary"
              @click="generateToken"
              :disabled="generatingToken"
            >
              Generar
            </button>
          </div>
        </div>

        <div class="modal-actions">
          <button class="btn secondary" type="button" @click="close">Cerrar</button>
        </div>

        <p v-if="error" class="error">{{ error }}</p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useRouter, useRoute } from 'vue-router'

// Definición mínima de usuario para mostrar camarero
type User = {
  id: number
  name?: string
  username?: string
  fullName?: string
  email?: string
  [k: string]: any
}

const router = useRouter()
const route = useRoute()
const tableId = route.params.tableId as string

const users = ref<User[]>([])
const tableData = ref({
  num_Table: 0,
  num_Customers: 0,
  waiter_id: undefined as number | undefined,
  state: '' as string,
  posX: 0,
  posY: 0,
})
// almacena el token de la mesa
const tableToken = ref<string | null>(null)
// estado de carga para la generación
const generatingToken = ref(false)
const error = ref<string | null>(null)

const states = ['FREE', 'BUSY', 'RESERVED', 'WAITING'] as const

onMounted(async () => {
  try {
    // Cargar detalles de la mesa
    const res = await api.get(`/table/${tableId}`)
    const table = res.data
    tableData.value = {
      num_Table: table.num_Table,
      num_Customers: table.num_Customers,
      waiter_id: table.waiter?.id,
      state: table.state,
      posX: table.posX,
      posY: table.posY,
    }
  } catch (e) {
    console.error(e)
    error.value = 'No se pudo obtener la información de la mesa.'
  }

  // Cargar camareros con caché (opcional)
  try {
    const CACHE_KEY = 'waiters_cache_v1'
    const CACHE_TTL_MS = 5 * 60 * 1000
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
      const usersRes = await api.get('/users')
      users.value = Array.isArray(usersRes.data) ? usersRes.data : []
      localStorage.setItem(CACHE_KEY, JSON.stringify({ time: Date.now(), data: users.value }))
    }
  } catch (e) {
    console.error(e)
    // no necesitamos manejar error: dejamos lista vacía
  }

  // Cargar token inicial de la mesa, si existe
  try {
    const tokenRes = await api.get(`/qr/token?tableId=${tableId}`)
    // intenta asignar token de distintas estructuras posibles
    const data = tokenRes.data
    if (data && typeof data === 'object' && 'token' in data) {
      tableToken.value = (data as any).token
    } else {
      tableToken.value = typeof data === 'string' ? data : null
    }
  } catch (e) {
    // no pasa nada si no existe token
    console.error(e)
  }
})

function displayUser(u: User) {
  return u.name || u.fullName || u.username || u.email || `Usuario #${u.id}`
}

function stateLabel(s: string) {
  const map: Record<string, string> = {
    FREE: 'Libre',
    BUSY: 'Ocupada',
    RESERVED: 'Reservada',
    WAITING: 'Esperando pedido',
  }
  return map[s] ?? s
}

function close() {
  router.back()
}

/**
 * Copiar el token actual al portapapeles
 */
async function copyToken() {
  if (!tableToken.value) return
  try {
    await navigator.clipboard.writeText(tableToken.value)
  } catch (e) {
    console.error('No se pudo copiar el token', e)
  }
}

/**
 * Generar un nuevo token para la mesa durante 15 minutos
 */
async function generateToken() {
  if (generatingToken.value) return
  generatingToken.value = true
  try {
    const res = await api.get(`/qr/generate?tableId=${tableId}&minutes=15`)
    const data = res.data
    if (data && typeof data === 'object' && 'token' in data) {
      tableToken.value = (data as any).token
    } else {
      tableToken.value = typeof data === 'string' ? data : null
    }
  } catch (e) {
    console.error(e)
    error.value = 'No se pudo generar el token.'
  } finally {
    generatingToken.value = false
  }
}
</script>

<style scoped>
@import '../../modal-styles.css';

/* Ajustes de estilo para coincidir con AddTable */
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
  margin-bottom: 1rem;
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

/* Botón terciario (no se usa aquí pero se define para consistencia temática) */
.btn.tertiary {
  border: 1px solid var(--border);
  background: var(--bg-elev);
  color: var(--text);
}
.btn.tertiary:hover {
  background: rgba(255, 255, 255, 0.08);
}

/* Fila del token: alinea el campo y los botones horizontalmente */
.token-row {
  display: flex;
  align-items: stretch;
  gap: 0.5rem;
}
.token-row input {
  flex: 1;
}
</style>
