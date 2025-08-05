<template>
  <div class="board">
    <div class="actions">
      <div class="left logo">🍽 Restaurante</div>

      <!-- Leyenda de estados -->
      <div class="legend" aria-label="Leyenda de estados de las mesas">
        <div class="legend-item">
          <span class="legend-dot free"></span>
          <span>Libre</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot busy"></span>
          <span>Ocupada</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot reserved"></span>
          <span>Reservada</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot waiting"></span>
          <span>Esperando pedido</span>
        </div>
      </div>

      <div class="right">
        <button class="btn secondary" @click="changeDraggable">
          {{ draggable ? 'Bloquear' : 'Desbloquear' }}
        </button>
        <button class="btn primary" @click="openModal">Añadir mesa</button>
      </div>
    </div>

    <div id="escritorio">
      <table-component
        v-for="icono in iconos"
        :key="icono.id"
        class="icono"
        :style="{ left: icono.x + 'px', top: icono.y + 'px' }"
        @mousedown="startDrag($event, icono)"
        :object="icono"
        @dblclick="openRead(icono.id)"
      />
    </div>

    <router-view />
  </div>
</template>

<script lang="ts">
import { defineComponent, provide, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import TableComponent from './TableComponent.vue'
import axios from 'axios'
import { useTables } from '../composables/useTables'

const { iconos, reloadContent } = useTables()

export default defineComponent({
  components: { TableComponent },
  setup() {
    const router = useRouter()
    const draggable = ref(false)

    onMounted(() => reloadContent())

    provide('iconos', iconos)
    provide('reloadContent', reloadContent)

    let selected: any = null
    let offsetX = 0
    let offsetY = 0

    const changeDraggable = () => {
      draggable.value = !draggable.value
    }

    const startDrag = (event: MouseEvent, icono: any) => {
      if (!draggable.value) return
      selected = icono
      offsetX = event.clientX - icono.x
      offsetY = event.clientY - icono.y
      window.addEventListener('mousemove', onDrag)
      window.addEventListener('mouseup', endDrag)
    }

    const onDrag = (event: MouseEvent) => {
      if (selected) {
        selected.x = event.clientX - offsetX
        selected.y = event.clientY - offsetY
      }
    }

    const endDrag = async () => {
      if (selected) {
        await axios.put(`http://localhost:8081/table/${selected.id}`, {
          posX: selected.x,
          posY: selected.y,
          num_Table: selected.num_Table,
          waiter: selected.waiter_id,
          state: selected.state,
          num_Customers: selected.num_Customers,
        })
        selected = null
      }
      window.removeEventListener('mousemove', onDrag)
      window.removeEventListener('mouseup', endDrag)
    }

    const openModal = () => router.push('/application/add')
    const openRead = (id: number) => router.push(`/application/${id}`)

    return { iconos, startDrag, openModal, openRead, changeDraggable, draggable }
  },
})
</script>

<style scoped>
/* Contenedor raíz del plano */
.board {
  /* No dependas del 100% del padre, usa viewport menos la topbar */
  min-height: calc(100vh - var(--topbar-h));
  display: flex;
  flex-direction: column;
}

/* Barra superior del plano (tu leyenda y botones) — lo que ya tenías */
.actions {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 0.75rem;
  align-items: center;
  padding: 0.75rem 1rem;
  background: #282828;
  color: white;
}

/* Lienzo del mapa */
#escritorio {
  flex: 1; /* ocupa todo lo disponible bajo .actions */
  min-height: 0; /* importante en flex para poder encajar sin forzar scroll */
  width: 100%;
  background-color: #1e1e1e;
  position: relative;
  overflow: hidden;
}

@media (max-width: 900px) {
  .actions {
    grid-template-columns: 1fr;
    grid-auto-rows: auto;
  }
}

.logo {
  font-weight: bold;
  font-size: 1.1rem;
}

/* Leyenda */
.legend {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-self: center;
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
} /* verde */
.legend-dot.busy {
  background: #ef4444;
} /* rojo */
.legend-dot.reserved {
  background: #6366f1;
} /* índigo */
.legend-dot.waiting {
  background: #f59e0b;
} /* ámbar */

.right {
  display: flex;
  gap: 0.75rem;
  justify-self: end;
}

/* Ítems arrastrables */
.icono {
  width: 96px;
  height: 96px;
  position: absolute;
  cursor: grab;
  user-select: none;
}
.icono:active {
  cursor: grabbing;
}

/* Botones */
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
