<template>
  <div class="board">
    <!-- Canvas where tables are drawn -->
    <div
      id="escritorio"
      :style="boardStyle"
      @contextmenu.prevent="onContextMenu"
    >
      <table-component
        v-for="icono in iconos"
        :key="icono.id"
        class="icono"
        :style="{ left: icono.x + 'px', top: icono.y + 'px' }"
        @mousedown="startDrag($event, icono)"
        :object="icono"
        @dblclick="openRead(icono.id)"
        @contextmenu.prevent.stop="onTableContextMenu($event, icono)"
      />

      <!-- Context menu to change the floorplan template -->
      <div
        v-if="showContextMenu"
        class="context-menu"
        :style="{ top: contextMenuPos.y + 'px', left: contextMenuPos.x + 'px' }"
        @mousedown.stop
      >
        <ul>
          <li @click="triggerFileDialog">Cambiar plantilla</li>
        </ul>
      </div>

      <!-- Context menu for table actions -->
      <div
        v-if="showTableMenu"
        class="context-menu"
        :style="{ top: tableMenuPos.y + 'px', left: tableMenuPos.x + 'px' }"
        @mousedown.stop
      >
        <ul>
          <li @click="goToFactura">Generar factura</li>
        </ul>
      </div>

      <!-- Hidden file input used to select a new background image -->
      <input
        type="file"
        ref="fileInputRef"
        style="display: none"
        accept="image/*"
        @change="onFileSelected"
      />
    </div>
    <!-- Child routes such as add table or table info -->
    <router-view />
  </div>
</template>

<script lang="ts">
import { defineComponent, provide, onMounted, onBeforeUnmount, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import TableComponent from './TableComponent.vue'
import api from '@/services/api'
import { useTables } from '../../composables/useTables'
import useDrag from '../../composables/useDrag'

// Access global table data
const { iconos, reloadContent } = useTables()

export default defineComponent({
  components: { TableComponent },
  setup() {
    const router = useRouter()
    // Import shared draggable state; toggling is performed in the header
    const { draggable } = useDrag()

    // ======== Plano de mesas: fondo personalizable ========
    // URL en base64 o ruta a la imagen de fondo del plano.  Se carga desde localStorage
    // para persistir entre sesiones.  Si está vacía, se usa el color por defecto.
    const backgroundUrl = ref<string>(localStorage.getItem('plano-background') || '')

    // Controla la visibilidad del menú contextual y su posición en pantalla
    const showContextMenu = ref(false)
    const contextMenuPos = ref({ x: 0, y: 0 })
    // Controla la visibilidad del menú contextual específico para una mesa y su posición
    const showTableMenu = ref(false)
    const tableMenuPos = ref({ x: 0, y: 0 })
    // Almacena la mesa seleccionada (por id) cuando se invoca el menú contextual
    const selectedTableId = ref<number | null>(null)
    // Referencia al input de archivos oculto
    const fileInputRef = ref<HTMLInputElement | null>(null)

    // Estilos dinámicos para el tablero (#escritorio).  Si hay una imagen seleccionada,
    // se aplica como fondo; en caso contrario se deja el color definido por CSS.
    const boardStyle = computed(() => {
      if (backgroundUrl.value) {
        return {
          backgroundImage: `url(${backgroundUrl.value})`,
          // Utiliza "contain" para que la imagen se ajuste por completo dentro del plano
          // sin recortarse y manteniendo su relación de aspecto.  Se centrará el
          // contenido y se evitará repetir la imagen.
          backgroundSize: 'contain',
          backgroundPosition: 'center center',
          backgroundRepeat: 'no-repeat',
        }
      }
      return {}
    })

    /**
     * Muestra el menú contextual al hacer clic derecho en el plano.  Se detiene la
     * propagación para evitar el menú del navegador y se almacena la posición
     * donde debe aparecer el menú.
     */
    const onContextMenu = (event: MouseEvent) => {
      // no mostrar si se ha hecho clic sobre una mesa (icono)
      const target = event.target as HTMLElement
      if (target.closest('.icono')) return
      showContextMenu.value = true
      // Calcula la posición relativa al contenedor del plano.  Al restar el
      // desplazamiento del contenedor (su bounding rect) se evita que el
      // desplazamiento del header o del menú lateral afecte a la ubicación del menú.
      const boardRect = (event.currentTarget as HTMLElement).getBoundingClientRect()
      contextMenuPos.value = {
        x: event.clientX - boardRect.left,
        y: event.clientY - boardRect.top,
      }
    }

    /**
     * Oculta el menú contextual cuando se hace clic fuera de él.
     */
    const hideContextMenu = () => {
      showContextMenu.value = false
      showTableMenu.value = false
    }

    /**
     * Abre el cuadro de selección de archivos para que el usuario elija una nueva
     * plantilla de fondo.  Se oculta el menú contextual después de hacer clic.
     */
    const triggerFileDialog = () => {
      hideContextMenu()
      const input = fileInputRef.value
      if (input) {
        input.value = '' // reset para poder seleccionar el mismo archivo
        input.click()
      }
    }

    /**
     * Muestra el menú contextual al hacer clic derecho sobre una mesa.
     * Se previene el menú del navegador y se almacena la posición
     * donde debe aparecer el menú. También se guarda el id de la mesa
     * para usarlo al generar la factura.
     */
    const onTableContextMenu = (event: MouseEvent, icono: any) => {
      // Prevenir menú del navegador y detener propagación para no activar otros menús
      event.preventDefault()
      event.stopPropagation()
      showTableMenu.value = true
      showContextMenu.value = false // ocultar cualquier otro menú
      selectedTableId.value = icono.id
      const boardEl = (event.currentTarget as HTMLElement).closest('#escritorio') as HTMLElement | null
      const boardRect = boardEl ? boardEl.getBoundingClientRect() : null
      if (boardRect) {
        tableMenuPos.value = {
          x: event.clientX - boardRect.left,
          y: event.clientY - boardRect.top,
        }
      } else {
        tableMenuPos.value = { x: event.clientX, y: event.clientY }
      }
    }

    /**
     * Navega a la vista de factura para la mesa seleccionada y oculta el menú.
     */
    const goToFactura = () => {
      if (selectedTableId.value != null) {
        hideContextMenu()
        router.push(`/application/factura/${selectedTableId.value}`)
      }
    }

    /**
     * Maneja la selección de archivos.  Convierte la imagen a base64 para
     * aplicarla inmediatamente como fondo y la guarda en localStorage.  Además,
     * intenta escribir una copia en el directorio `src/assets/backgrounds` si
     * está disponible un entorno Node (por ejemplo, en Electron).  Esta copia
     * permite persistir la imagen en el proyecto para futuras ejecuciones.
     */
    const onFileSelected = async (event: Event) => {
      const input = event.target as HTMLInputElement
      if (!input.files || input.files.length === 0) return
      const file = input.files[0]
      const reader = new FileReader()
      reader.onload = async () => {
        const dataUrl = reader.result as string
        backgroundUrl.value = dataUrl
        // Guarda la imagen en localStorage para restaurarla en futuras sesiones
        localStorage.setItem('plano-background', dataUrl)

        // Intentar guardar el archivo físicamente en el proyecto si Node está disponible
        try {
          // La API require solo existe si el contexto tiene integración de Node (p.ej. Electron)
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const nodeRequire = (window as any).require
          if (typeof nodeRequire === 'function') {
            const fs = nodeRequire('fs')
            const path = nodeRequire('path')
            // Directorio base del proyecto.  process.cwd() suele apuntar a la raíz del paquete cuando se ejecuta con Electron.
            const appPath = nodeRequire('process').cwd()
            const bgDir = path.join(appPath, 'src', 'assets', 'backgrounds')
            if (!fs.existsSync(bgDir)) {
              fs.mkdirSync(bgDir, { recursive: true })
            }
            const extMatch = file.name.match(/\.([a-zA-Z0-9]+)$/)
            const ext = extMatch ? extMatch[1] : 'png'
            const dest = path.join(bgDir, `background-${Date.now()}.${ext}`)
            // Extraer datos base64 tras la coma
            const base64Data = dataUrl.split(',')[1]
            // Obtener Buffer desde el módulo 'buffer' para evitar que no esté definido en el contexto
            const BufferClass = nodeRequire('buffer').Buffer
            const buf = BufferClass.from(base64Data, 'base64')
            fs.writeFileSync(dest, buf)
          }
        } catch (err) {
          console.warn('No se pudo escribir el archivo de plantilla en disco:', err)
        }
      }
      reader.readAsDataURL(file)
    }

    // Escucha global para cerrar el menú contextual cuando se haga clic en cualquier parte
    onMounted(() => {
      reloadContent()
      document.addEventListener('click', hideContextMenu)
    })
    // Limpieza al desmontar: eliminar listener para evitar fugas de memoria
    onBeforeUnmount(() => {
      document.removeEventListener('click', hideContextMenu)
    })

    provide('iconos', iconos)
    provide('reloadContent', reloadContent)

    let selected: any = null
    let offsetX = 0
    let offsetY = 0

    const startDrag = (event: MouseEvent, icono: any) => {
      // Solo iniciar arrastre con botón izquierdo (button === 0)
      if (event.button !== 0) return
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
        await api.put(`/table/${selected.id}`, {
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

    return {
      iconos,
      startDrag,
      openModal,
      openRead,
      // Datos y funciones para el menú contextual y la plantilla de fondo
      boardStyle,
      showContextMenu,
      contextMenuPos,
      triggerFileDialog,
      onFileSelected,
      fileInputRef,
      onContextMenu,
      // Datos y funciones del menú contextual de mesas
      showTableMenu,
      tableMenuPos,
      onTableContextMenu,
      goToFactura,
    }
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
/*
  The original component defined a local header inside the table plan view.  The
  legend and action buttons have been moved to the global topbar (HeaderContent)
  component, so the `.actions` styles are removed.  Styles for the map area
  and draggable items remain unchanged.
*/
/* .actions styles removed */

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

/* ====== Menú contextual para cambiar la plantilla ====== */
.context-menu {
  position: absolute;
  background: #1f2937; /* gris oscuro ligeramente más claro que el fondo */
  color: #f9fafb; /* texto claro */
  border: 1px solid #4b5563;
  border-radius: 6px;
  padding: 4px 0;
  min-width: 160px;
  z-index: 1000;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.25);
}
.context-menu ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.context-menu li {
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.9rem;
  white-space: nowrap;
  transition: background 0.15s ease;
}
.context-menu li:hover {
  background: #374151; /* gris algo más claro para el hover */
}
</style>
