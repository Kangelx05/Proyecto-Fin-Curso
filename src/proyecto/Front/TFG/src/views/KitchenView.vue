<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import useAuth from '@/composables/useAuth'

// Describes a single kitchen dish coming from the API
interface KitchenDish {
  detailId: number
  productId: number | null
  productName: string
  imageData?: string
  createdAt: string | null
  tableId?: number | null
  tableNumber?: number | null
  prepStartedAt?: string | null
  preparedAt?: string | null
}

type Column = 'new' | 'prep' | 'plate'

const newDishes = ref<KitchenDish[]>([])
const prepDishes = ref<KitchenDish[]>([])
const plateDishes = ref<KitchenDish[]>([])

// ---- UI reactive timer and helpers ----
const now = ref<number>(Date.now())
let timer: number | undefined

function elapsedSince(dateStr: string | null): string {
  if (!dateStr) return '—'
  const ms = now.value - new Date(dateStr).getTime()
  if (!Number.isFinite(ms) || ms < 0) return '0s'
  const totalSec = Math.floor(ms / 1000)
  const h = Math.floor(totalSec / 3600)
  const m = Math.floor((totalSec % 3600) / 60)
  const s = totalSec % 60
  if (h > 0) return `${h}h ${m}m ${s}s`
  if (m > 0) return `${m}m ${s}s`
  return `${s}s`
}

function dishImgSrc(dish: KitchenDish): string | null {
  if (!dish.imageData) return null
  return `data:image/jpeg;base64,${dish.imageData}`
}

const { token } = useAuth()
const BASE_URL = 'http://localhost:8081'

// Fetch pending kitchen dishes from API and group them
async function fetchKitchenDishes() {
  try {
    const headers: any = {}
    if (token.value) headers['Authorization'] = `Bearer ${token.value}`

    const response = await axios.get<KitchenDish[]>(`${BASE_URL}/order-detail/kitchen-dishes`, {
      headers,
    })
    const dishes: KitchenDish[] = response.data || []

    const byOldest = (a: KitchenDish, b: KitchenDish) => {
      const ta = a.createdAt ? new Date(a.createdAt).getTime() : 0
      const tb = b.createdAt ? new Date(b.createdAt).getTime() : 0
      return ta - tb
    }

    newDishes.value = dishes.filter((d) => !d.prepStartedAt && !d.preparedAt).sort(byOldest)
    prepDishes.value = dishes.filter((d) => d.prepStartedAt && !d.preparedAt).sort(byOldest)
    plateDishes.value = dishes.filter((d) => d.preparedAt).sort(byOldest)
  } catch (error) {
    console.error('Error al obtener platos de cocina:', error)
  }
}

// ---- Drag & Drop state and handlers (solo izquierda → derecha) ----
const dragState = ref<{ dishId: number | null; from: Column | null }>({ dishId: null, from: null })
const dragOverColumn = ref<Column | null>(null)
const blockedOverColumn = ref<Column | null>(null)

const columnOrder: Column[] = ['new', 'prep', 'plate']
function canMove(from: Column | null, to: Column): boolean {
  if (!from) return false
  return columnOrder.indexOf(to) > columnOrder.indexOf(from) // permite saltar columnas a la derecha
  // Si quieres obligar paso a paso, usa: === columnOrder.indexOf(from) + 1
}

function columnList(c: Column) {
  if (c === 'new') return newDishes.value
  if (c === 'prep') return prepDishes.value
  return plateDishes.value
}

function sortLists() {
  const byOldest = (a: KitchenDish, b: KitchenDish) => {
    const ta = a.createdAt ? new Date(a.createdAt).getTime() : 0
    const tb = b.createdAt ? new Date(b.createdAt).getTime() : 0
    return ta - tb
  }
  newDishes.value.sort(byOldest)
  prepDishes.value.sort(byOldest)
  plateDishes.value.sort(byOldest)
}

function onDragStart(dish: KitchenDish, from: Column, e: DragEvent) {
  dragState.value = { dishId: dish.detailId, from }
  e.dataTransfer?.setData('text/plain', String(dish.detailId))
  e.dataTransfer?.setData('application/x-from', from)
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}

function onDragEnd() {
  dragState.value = { dishId: null, from: null }
  dragOverColumn.value = null
  blockedOverColumn.value = null
}

function onDragOver(to: Column, e: DragEvent) {
  e.preventDefault()
  const allowed = canMove(dragState.value.from, to)
  if (e.dataTransfer) e.dataTransfer.dropEffect = allowed ? 'move' : 'none'
  dragOverColumn.value = allowed ? to : null
  blockedOverColumn.value = allowed ? null : to
}

function onDrop(to: Column, e: DragEvent) {
  e.preventDefault()
  const idStr = e.dataTransfer?.getData('text/plain')
  const from = (e.dataTransfer?.getData('application/x-from') as Column) || dragState.value.from
  if (!idStr || !from) return onDragEnd()
  if (!canMove(from, to)) return onDragEnd()
  const id = Number(idStr)
  moveDish(id, from, to)
  onDragEnd()
}

async function moveDish(id: number, from: Column, to: Column) {
  if (from === to) return
  const fromList = columnList(from)
  const toList = columnList(to)
  const index = fromList.findIndex((d) => d.detailId === id)
  if (index === -1) return
  const dish = fromList.splice(index, 1)[0]

  // Guardamos estado previo para posible rollback
  const prevPrep = dish.prepStartedAt
  const prevPrepared = dish.preparedAt

  // Actualización optimista de timestamps
  const nowISO = new Date().toISOString()
  if (to === 'new') {
    dish.prepStartedAt = null
    dish.preparedAt = null
  } else if (to === 'prep') {
    dish.prepStartedAt = dish.prepStartedAt ?? nowISO
    dish.preparedAt = null
  } else if (to === 'plate') {
    if (!dish.prepStartedAt) dish.prepStartedAt = nowISO
    dish.preparedAt = dish.preparedAt ?? nowISO
  }

  toList.push(dish)
  sortLists()

  try {
    await persistTransition(dish, from, to)
  } catch (e) {
    console.error('No se pudo persistir el cambio de estado', e)
    // Rollback
    const i = toList.findIndex((d) => d.detailId === dish.detailId)
    if (i > -1) toList.splice(i, 1)
    dish.prepStartedAt = prevPrep
    dish.preparedAt = prevPrepared
    fromList.push(dish)
    sortLists()
  }
}

// Persiste la transición real en backend según el movimiento
async function persistTransition(dish: KitchenDish, from: Column, to: Column) {
  const headers: any = {}
  if (token.value) headers['Authorization'] = `Bearer ${token.value}`

  // Nuevos -> En preparación
  if (from === 'new' && to === 'prep') {
    await axios.put(`${BASE_URL}/order-detail/${dish.detailId}/start-prep`, {}, { headers })
    return
  }

  // En preparación -> Emplatado
  if (from === 'prep' && to === 'plate') {
    await axios.put(`${BASE_URL}/order-detail/${dish.detailId}/plate`, {}, { headers })
    return
  }

  // Caso salto: Nuevos -> Emplatado (encadena ambos)
  if (from === 'new' && to === 'plate') {
    await axios.put(`${BASE_URL}/order-detail/${dish.detailId}/start-prep`, {}, { headers })
    await axios.put(`${BASE_URL}/order-detail/${dish.detailId}/plate`, {}, { headers })
    return
  }

  // Otros movimientos no deberían ocurrir por las reglas de DnD
}

onMounted(() => {
  fetchKitchenDishes()
  timer = window.setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})
</script>

<template>
  <div class="kitchen-board">
    <!-- Column: New Dishes -->
    <div
      class="column new"
      :class="{
        'drop-target': dragOverColumn === 'new',
        'drop-blocked': blockedOverColumn === 'new',
      }"
      @dragover.prevent="onDragOver('new', $event)"
      @dragenter.prevent="onDragOver('new', $event)"
      @drop="onDrop('new', $event)"
    >
      <h2>Nuevos platos</h2>
      <div class="dish-grid">
        <div v-for="dish in newDishes" :key="dish.detailId" class="dish-group">
          <div
            class="kitchen-dish-card"
            :draggable="true"
            @dragstart="onDragStart(dish, 'new', $event)"
            @dragend="onDragEnd"
            :class="{ dragging: dragState.dishId === dish.detailId }"
          >
            <div class="img-wrapper">
              <img
                v-if="dishImgSrc(dish)"
                :src="dishImgSrc(dish)!"
                alt=""
                class="dish-img"
                draggable="false"
              />
              <div v-else class="img-fallback">🍽️</div>
            </div>
            <div class="dish-info">
              <div class="dish-name" :title="dish.productName">{{ dish.productName }}</div>
              <div class="dish-meta">
                <span class="mesa">Mesa {{ dish.tableNumber ?? '—' }}</span>
                <span class="elapsed" :title="dish.createdAt || ''">{{
                  elapsedSince(dish.createdAt ?? null)
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Column: In Preparation -->
    <div
      class="column prep"
      :class="{
        'drop-target': dragOverColumn === 'prep',
        'drop-blocked': blockedOverColumn === 'prep',
      }"
      @dragover.prevent="onDragOver('prep', $event)"
      @dragenter.prevent="onDragOver('prep', $event)"
      @drop="onDrop('prep', $event)"
    >
      <h2>En preparación</h2>
      <div class="dish-grid">
        <div v-for="dish in prepDishes" :key="dish.detailId" class="dish-group">
          <div
            class="kitchen-dish-card"
            :draggable="true"
            @dragstart="onDragStart(dish, 'prep', $event)"
            @dragend="onDragEnd"
            :class="{ dragging: dragState.dishId === dish.detailId }"
          >
            <div class="img-wrapper">
              <img
                v-if="dishImgSrc(dish)"
                :src="dishImgSrc(dish)!"
                alt=""
                class="dish-img"
                draggable="false"
              />
              <div v-else class="img-fallback">🍽️</div>
            </div>
            <div class="dish-info">
              <div class="dish-name" :title="dish.productName">{{ dish.productName }}</div>
              <div class="dish-meta">
                <span class="mesa">Mesa {{ dish.tableNumber ?? '—' }}</span>
                <span class="elapsed" :title="dish.createdAt || ''">{{
                  elapsedSince(dish.createdAt ?? null)
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Column: Plating -->
    <div
      class="column plate"
      :class="{
        'drop-target': dragOverColumn === 'plate',
        'drop-blocked': blockedOverColumn === 'plate',
      }"
      @dragover.prevent="onDragOver('plate', $event)"
      @dragenter.prevent="onDragOver('plate', $event)"
      @drop="onDrop('plate', $event)"
    >
      <h2>Emplatado</h2>
      <div class="dish-grid">
        <div v-for="dish in plateDishes" :key="dish.detailId" class="dish-group">
          <div
            class="kitchen-dish-card"
            :draggable="false"
            @dragstart.prevent
            :class="{ dragging: dragState.dishId === dish.detailId }"
          >
            <div class="img-wrapper">
              <img
                v-if="dishImgSrc(dish)"
                :src="dishImgSrc(dish)!"
                alt=""
                class="dish-img"
                draggable="false"
              />
              <div v-else class="img-fallback">🍽️</div>
            </div>
            <div class="dish-info">
              <div class="dish-name" :title="dish.productName">{{ dish.productName }}</div>
              <div class="dish-meta">
                <span class="mesa">Mesa {{ dish.tableNumber ?? '—' }}</span>
                <span class="elapsed" :title="dish.createdAt || ''">{{
                  elapsedSince(dish.createdAt ?? null)
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.kitchen-board {
  display: grid;
  grid-template-columns: 2fr 2fr 1fr;
  gap: 1rem;
  padding: 1rem;
  height: calc(100vh - var(--topbar-h));
  overflow: hidden; /* el scroll va dentro de cada columna */
}
.column {
  background: var(--bg-elev);
  border: 1px solid var(--border);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  min-height: 200px;
}
.column h2 {
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid var(--border);
  font-size: 1rem;
  font-weight: 600;
  text-transform: uppercase;
}

/* Contenedor de tarjetas: ocupa toda la columna y hace grid responsivo */
.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 0.5rem;
  padding: 0.5rem;
  flex: 1;
  overflow: auto;
  align-content: start;
}

/* Visual hint when a column is a valid / blocked drop target */
.column.drop-target {
  outline: 2px dashed var(--primary, #4f46e5);
  outline-offset: -2px;
}
.column.drop-blocked {
  outline: 2px dashed #e11d48;
  outline-offset: -2px;
}

.kitchen-dish-card {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0.4rem;
  cursor: grab;
  user-select: none;
  width: 100%; /* ocupa el ancho de la celda del grid */
  height: auto; /* se adapta al contenido */
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;
  transition:
    transform 0.15s ease,
    opacity 0.15s ease;
}
.kitchen-dish-card.dragging {
  opacity: 0.5;
  transform: scale(0.98);
}
.kitchen-dish-card:active {
  cursor: grabbing;
}

/* Imagen con relación 4:3 para que no se deforme y llene horizontalmente */
.img-wrapper {
  width: 100%;
  aspect-ratio: 4 / 3;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 6px;
  background: var(--bg-elev);
  margin-bottom: 0.5rem;
}
.dish-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.img-fallback {
  font-size: 1.5rem;
  opacity: 0.6;
}

.dish-info {
  width: 100%;
}
.dish-name {
  font-weight: 600;
  font-size: 0.85rem;
  line-height: 1.1;
  margin-bottom: 0.2rem;
  text-align: center;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.dish-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.75rem;
  opacity: 0.85;
}
</style>
