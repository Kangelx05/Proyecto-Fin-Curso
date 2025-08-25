<template>
  <section class="serve-view">
    <header class="header">
      <h1>Platos por servir</h1>
      <div class="actions">
        <button class="btn refresh" @click="fetchDishes" :disabled="loading">
          {{ loading ? 'Actualizando…' : 'Actualizar' }}
        </button>
        <button
          class="btn serve"
          :disabled="serving || selectedCount === 0"
          @click="serveSelected"
          :title="
            selectedCount === 0
              ? 'Selecciona al menos un plato'
              : 'Marcar seleccionados como servidos'
          "
        >
          {{ serving ? 'Sirviendo…' : `Servir (${selectedCount})` }}
        </button>
      </div>
    </header>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="groupedTables.length === 0 && !loading" class="empty">
      No hay platos por servir ahora mismo.
    </div>

    <div v-else class="tables-grid">
      <article v-for="group in groupedTables" :key="group.tableKey" class="table-card column">
        <div class="table-card__header">
          <h2 class="table-title">Mesa {{ group.tableLabel }}</h2>
          <div class="table-header-actions">
            <button
              class="btn subtle"
              @click="toggleSelectAll(group)"
              :disabled="group.dishes.length === 0"
            >
              {{ areAllSelected(group) ? 'Quitar selección' : 'Seleccionar todos' }}
            </button>
            <span class="count"
              >{{ selectedInGroup(group).length }} / {{ group.dishes.length }}</span
            >
          </div>
        </div>

        <div class="dishes-grid">
          <div
            v-for="dish in group.dishes"
            :key="dish.detailId"
            class="dish-card"
            :class="{ selected: isSelected(dish.detailId) }"
            :title="dish.productName"
            @click="toggleSelect(dish)"
          >
            <label class="checkbox">
              <input type="checkbox" :checked="isSelected(dish.detailId)" @change.prevent />
              <span class="box" aria-hidden="true" />
            </label>

            <div class="image-wrap">
              <img :src="imageSrc(dish)" :alt="dish.productName" />
              <span class="timer" :aria-label="'Preparado hace ' + humanSince(dish.preparedAt)">
                {{ formatSince(dish.preparedAt) }}
              </span>
            </div>
            <div class="dish-name" :title="dish.productName">{{ dish.productName }}</div>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>

<script>
import axios from 'axios'
import useAuth from '../composables/useAuth'

// Ajusta BASE_URL si tu backend usa otra URL
const { token } = useAuth()
const BASE_URL = 'http://localhost:8081'

export default {
  name: 'ServeView',
  data() {
    return {
      loading: false,
      serving: false,
      error: '',
      dishes: [], // KitchenDishResponse[] del backend
      selected: new Set(), // detailIds seleccionados
      nowTs: Date.now(),
      intervalId: null,
      pollId: null,
    }
  },
  computed: {
    // Solo platos preparados (preparedAt != null) y aún no servidos
    preparedDishes() {
      return (this.dishes || []).filter((d) => d.preparedAt && !d.servedAt)
    },
    groupedTables() {
      const map = new Map()
      for (const d of this.preparedDishes) {
        const tableNumber = d.tableNumber ?? null // null si para llevar
        const key = tableNumber === null ? 'no-table' : String(tableNumber)
        if (!map.has(key)) {
          map.set(key, {
            tableKey: key,
            tableLabel: tableNumber === null ? '—' : tableNumber,
            dishes: [],
          })
        }
        map.get(key).dishes.push(d)
      }
      const entries = Array.from(map.values())
      entries.sort((a, b) => {
        if (a.tableKey === 'no-table') return 1
        if (b.tableKey === 'no-table') return -1
        return Number(a.tableKey) - Number(b.tableKey)
      })
      return entries
    },
    selectedCount() {
      return this.selected.size
    },
  },
  created() {
    this.fetchDishes()
    // refrescar los timers "hace x min"
    this.intervalId = setInterval(() => (this.nowTs = Date.now()), 30_000)
  },
  beforeUnmount() {
    if (this.intervalId) clearInterval(this.intervalId)
    if (this.pollId) clearInterval(this.pollId)
  },
  methods: {
    async fetchDishes() {
      this.loading = true
      this.error = ''
      try {
        const res = await axios.get(`${BASE_URL}/order-detail/kitchen-dishes`, {
          headers: token?.value ? { Authorization: `Bearer ${token.value}` } : {},
        })
        const list = Array.isArray(res.data) ? res.data : []
        // Normalizamos fechas LocalDateTime (sin zona) como locales
        this.dishes = list.map((x) => ({
          ...x,
          preparedAt: x.preparedAt ? this.parseIsoLocal(x.preparedAt) : null,
          createdAt: x.createdAt ? this.parseIsoLocal(x.createdAt) : null,
        }))
        // limpia selección de items que ya no están
        const ids = new Set(this.preparedDishes.map((d) => d.detailId))
        this.selected.forEach((id) => {
          if (!ids.has(id)) this.selected.delete(id)
        })
      } catch (e) {
        console.error('Error al obtener platos de cocina:', e)
        this.error = e?.message || 'No se pudieron cargar los platos.'
      } finally {
        this.loading = false
      }
    },

    // --- Selección ---
    isSelected(detailId) {
      return this.selected.has(detailId)
    },
    toggleSelect(dish) {
      const id = dish.detailId
      if (this.selected.has(id)) this.selected.delete(id)
      else this.selected.add(id)
    },
    selectedInGroup(group) {
      return group.dishes.filter((d) => this.isSelected(d.detailId))
    },
    areAllSelected(group) {
      return group.dishes.length > 0 && this.selectedInGroup(group).length === group.dishes.length
    },
    toggleSelectAll(group) {
      const all = this.areAllSelected(group)
      for (const d of group.dishes) {
        if (all) this.selected.delete(d.detailId)
        else this.selected.add(d.detailId)
      }
    },

    // --- Servir ---
    async serveSelected() {
      if (this.selected.size === 0) return
      this.serving = true
      this.error = ''
      const ids = Array.from(this.selected)
      try {
        const headers = token?.value ? { Authorization: `Bearer ${token.value}` } : {}
        const results = await Promise.allSettled(
          ids.map((id) => axios.put(`${BASE_URL}/order-detail/${id}/serve`, null, { headers })),
        )
        const ok = results.filter((r) => r.status === 'fulfilled').length
        const ko = results.length - ok
        // si todo OK, refrescamos; si no, quitamos solo los que se marcaron como servidos
        if (ok > 0) {
          // Quitamos de selección y lista local los servidos OK
          const servedIds = ids.filter((_, i) => results[i].status === 'fulfilled')
          servedIds.forEach((id) => this.selected.delete(id))
          // Marca localmente como servido para desaparecer de preparedDishes sin esperar al fetch
          this.dishes = this.dishes.map((d) =>
            servedIds.includes(d.detailId) ? { ...d, servedAt: new Date() } : d,
          )
        }
        if (ko > 0) {
          this.error = `No se pudieron servir ${ko} plato(s).`
        }
      } catch (e) {
        console.error('Error al servir platos:', e)
        this.error = e?.message || 'Ocurrió un error al servir los platos.'
      } finally {
        this.serving = false
        // sincroniza con backend
        this.fetchDishes()
      }
    },

    // --- utilidades de fecha/tiempo ---
    parseIsoLocal(value) {
      if (typeof value !== 'string') return null
      const match = value.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(?:\.(\d+))?$/)
      if (!match) {
        const d = new Date(value)
        return Number.isNaN(d.getTime()) ? null : d
      }
      const [_, Y, M, D, h, m, s] = match
      return new Date(Number(Y), Number(M) - 1, Number(D), Number(h), Number(m), Number(s))
    },
    formatSince(date) {
      if (!date) return '—'
      const minutes = Math.max(0, Math.round((this.nowTs - date.getTime()) / 60000))
      if (minutes < 1) return 'ahora'
      if (minutes < 60) return `${minutes} min`
      const hours = Math.floor(minutes / 60)
      const rem = minutes % 60
      return `${hours}h ${rem}m`
    },
    humanSince(date) {
      if (!date) return '—'
      const minutes = Math.max(0, Math.round((this.nowTs - date.getTime()) / 60000))
      if (minutes < 1) return 'unos segundos'
      if (minutes < 60) return `${minutes} minuto(s)`
      const hours = Math.floor(minutes / 60)
      const rem = minutes % 60
      return `${hours} hora(s) y ${rem} min`
    },

    // Imágenes
    imageSrc(dish) {
      if (dish.imageData) return `data:image/jpeg;base64,${dish.imageData}`
      return this.placeholderDataUri()
    },
    placeholderDataUri() {
      // Placeholder SVG inline
      const svg = encodeURIComponent(
        `<svg xmlns='http://www.w3.org/2000/svg' width='240' height='160'>\n  <rect width='100%' height='100%' fill='#eee'/>\n  <text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle' font-family='sans-serif' font-size='14' fill='#999'>Foto</text>\n</svg>`,
      )
      return `data:image/svg+xml;charset=utf-8,${svg}`
    },
  },
}
</script>

<style scoped>
.serve-view {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
  max-width: 1200px; /* limita el ancho y centra */
  margin: 0 auto; /* centra horizontalmente */
  padding: 0 1rem; /* respiración en los lados */
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}
.actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}
.btn {
  padding: 0.5rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  cursor: pointer;
  background: var(--bg-elev);
}
.btn[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn.refresh {
}
.btn.serve {
  font-weight: 600;
}
.btn.subtle {
  background: transparent;
}

.error {
  color: #b00020;
  background: #fde7ea;
  border: 1px solid #f6c2c9;
  padding: 0.6rem 0.8rem;
  border-radius: 6px;
}
.empty {
  opacity: 0.8;
}

.tables-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1rem;
}
.table-card {
  background: var(--bg-elev);
  border: 1px solid var(--border);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  min-height: 220px;
  max-height: 65vh;
}
.table-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  background: var(--bg-elev);
  z-index: 1;
}
.table-title {
  margin: 0;
  font-size: 0.95rem;
  font-weight: 600;
}
.table-header-actions {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
}
.table-header-actions .count {
  font-size: 0.8rem;
  opacity: 0.8;
}

.dishes-grid {
  padding: 0.75rem;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 0.75rem;
  overflow: auto;
  flex: 1 1 auto;
}

.dish-card {
  position: relative;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  padding: 0.45rem;
  cursor: pointer;
  transition:
    box-shadow 0.15s ease,
    border-color 0.15s ease,
    transform 0.05s ease;
}
.dish-card:hover {
  border-color: var(--border-strong, #999);
}
.dish-card.selected {
  border-color: var(--accent, #0a84ff);
  box-shadow: 0 0 0 2px rgba(10, 132, 255, 0.2) inset;
}

.checkbox {
  position: absolute;
  top: 6px;
  left: 6px;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
}
.checkbox input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}
.checkbox .box {
  width: 18px;
  height: 18px;
  border: 2px solid var(--border);
  border-radius: 4px;
  background: white;
  display: inline-block;
}
.dish-card.selected .checkbox .box {
  border-color: var(--accent, #0a84ff);
  box-shadow: inset 0 0 0 6px var(--accent, #0a84ff);
}

.image-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 4/3;
  border-radius: 6px;
  overflow: hidden;
  background: #f3f3f3;
}
.image-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.timer {
  position: absolute;
  right: 6px;
  bottom: 6px;
  font-variant-numeric: tabular-nums;
  background: rgba(0, 0, 0, 0.65);
  color: white;
  padding: 0.2rem 0.4rem;
  border-radius: 0.4rem;
  font-size: 0.8rem;
}
.dish-name {
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
