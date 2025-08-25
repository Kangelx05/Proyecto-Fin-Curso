<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <h2>Editar producto</h2>
      <form @submit.prevent="submitForm" class="edit-form">
        <input v-model="formData.name" type="text" placeholder="Nombre" required />
        <input v-model="formData.description" type="text" placeholder="Descripción" />
        <input
          v-model.number="formData.price"
          type="number"
          placeholder="Precio"
          step="0.01"
          required
        />
        <input v-model="formData.category" type="text" placeholder="Categoría" required />
        <!--
          Campo de selección de imagen.  Si se selecciona una nueva imagen
          se convertirá a base64 y se enviará al servidor.  Si no se
          selecciona ninguna imagen se mantendrá la imagen existente.
        -->
        <input type="file" accept="image/*" @change="onFileChange" />
        <!-- Vista previa opcional de la imagen cargada -->
        <img
          v-if="previewSrc"
          :src="previewSrc"
          alt="Vista previa de la imagen"
          class="image-preview"
        />
        <div class="modal-actions">
          <button class="btn primary" type="submit">Guardar</button>
          <button class="btn secondary" type="button" @click="close">Cancelar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useRouter, useRoute } from 'vue-router'

interface Producto {
  id: number
  name: string
  description: string
  price: number
  category: string
  // La imagen del producto puede ser binaria (Uint8Array) al cargarla desde la API
  // o una cadena base64 (string) cuando el usuario selecciona una nueva imagen.
  data: Uint8Array | string | null
}

const router = useRouter()
const route = useRoute()

// Se espera recibir el ID del producto por parámetro en la ruta
const productoId = Number(route.params.id)

const formData = ref<Producto>({
  id: productoId,
  name: '',
  description: '',
  price: 0,
  category: '',
  data: null,
})

// Propiedad reactiva para almacenar la imagen cargada como base64.  También sirve
// como origen de la vista previa.  Si no se selecciona nueva imagen se
// utilizará la que viene del servidor.
const previewSrc = ref<string | null>(null)

// Procesa el cambio de archivo.  Convierte la imagen a base64 (sin prefijo) y
// la guarda en formData.data.  También actualiza previewSrc para mostrar la
// vista previa.  Si no se selecciona archivo se mantiene la imagen existente.
function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target && target.files && target.files[0]) {
    const file = target.files[0]
    const reader = new FileReader()
    reader.onload = () => {
      const result = reader.result as string
      // El resultado es data:<mime>;base64,<base64>
      const base64 = result.split(',')[1]
      formData.value.data = base64 as unknown as any
      previewSrc.value = result
    }
    reader.readAsDataURL(file)
  }
}

// Obtener los datos del producto al cargar el componente
/*
 * Detecta el tipo MIME de una imagen a partir de sus primeros bytes.  Soporta
 * los formatos más comunes: PNG, JPEG, GIF y WebP.  Si no se reconoce
 * ninguna cabecera, se devuelve image/png.
 */
function detectMimeFromBytes(u8: Uint8Array): string {
  if (u8.length >= 4 && u8[0] === 0x89 && u8[1] === 0x50 && u8[2] === 0x4e && u8[3] === 0x47) {
    return 'image/png'
  }
  if (u8.length >= 3 && u8[0] === 0xff && u8[1] === 0xd8 && u8[2] === 0xff) {
    return 'image/jpeg'
  }
  if (u8.length >= 4 && u8[0] === 0x47 && u8[1] === 0x49 && u8[2] === 0x46 && u8[3] === 0x38) {
    return 'image/gif'
  }
  if (
    u8.length >= 12 &&
    u8[0] === 0x52 &&
    u8[1] === 0x49 &&
    u8[2] === 0x46 &&
    u8[3] === 0x46 &&
    u8[8] === 0x57 &&
    u8[9] === 0x45 &&
    u8[10] === 0x42 &&
    u8[11] === 0x50
  ) {
    return 'image/webp'
  }
  return 'image/png'
}

/*
 * Detecta el tipo MIME basándose en los primeros caracteres de una cadena
 * base64.  Utiliza firmas típicas de cada formato para inferir el MIME.
 */
function detectMimeFromBase64(b64: string): string {
  if (b64.startsWith('iVBOR')) return 'image/png'
  if (b64.startsWith('/9j/')) return 'image/jpeg'
  if (b64.startsWith('R0lGOD')) return 'image/gif'
  if (b64.startsWith('UklGR')) return 'image/webp'
  return 'image/png'
}

/*
 * Convierte un Uint8Array en una cadena base64.  Se procesa en bloques
 * para evitar problemas de rendimiento con arrays grandes.
 */
function toBase64(u8: Uint8Array): string {
  let binary = ''
  const chunkSize = 0x8000
  for (let i = 0; i < u8.length; i += chunkSize) {
    const chunk = u8.subarray(i, i + chunkSize)
    binary += String.fromCharCode(...Array.from(chunk))
  }
  return btoa(binary)
}

// Obtener los datos del producto al cargar el componente
onMounted(async () => {
  try {
    const response = await api.get<Producto>(`/cardProduct/${productoId}`)
    formData.value = response.data
    // Si el producto tiene datos, intentar construir una vista previa correctamente tipada
    const data = response.data.data as unknown
    if (data) {
      try {
        if (typeof data === 'string') {
          // Si ya es un Data URI completo, usarlo tal cual
          if (data.startsWith('data:image')) {
            previewSrc.value = data
          } else {
            // Es base64 sin prefijo
            const mime = detectMimeFromBase64(data)
            previewSrc.value = `data:${mime};base64,${data}`
          }
        } else {
          const u8 = new Uint8Array(data as ArrayBuffer)
          if (u8.length) {
            const mime = detectMimeFromBytes(u8)
            const base64 = toBase64(u8)
            previewSrc.value = `data:${mime};base64,${base64}`
          }
        }
      } catch {
        previewSrc.value = null
      }
    }
  } catch (error) {
    console.error('Error al cargar el producto:', error)
  }
})

function close() {
  router.back()
}

function submitForm() {
  api
    .put(`/cardProduct/${formData.value.id}`, formData.value)
    .then((response) => {
      console.log('Producto actualizado:', response.data)
      close()
    })
    .catch((error) => {
      console.error('Error al actualizar el producto:', error)
    })
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.5);
  height: 100%;
  width: 100%;
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Estilo del contenedor de edición: adaptado al tema oscuro */
.modal-content {
  background: #1f2937;
  padding: 2rem;
  border-radius: 8px;
  width: 320px;
  max-width: 90%;
  color: #e5e7eb;
  border: 1px solid #374151;
}

/* Inputs en el formulario de edición */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.edit-form input[type='text'],
.edit-form input[type='number'] {
  background-color: #111827;
  border: 1px solid #374151;
  color: #f3f4f6;
  border-radius: 0.375rem;
  padding: 0.5rem;
  width: 100%;
  box-sizing: border-box;
}

.edit-form input::placeholder {
  color: #9ca3af;
}

/* Contenedor para los botones Guardar/Cancelar */
.modal-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}

/* Reutilizamos clases de botones de la aplicación */
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

/* Estilo para la vista previa de la imagen en la edición */
.image-preview {
  width: 100%;
  max-height: 150px;
  object-fit: cover;
  margin-bottom: 0.75rem;
  border-radius: 0.375rem;
  border: 1px solid #374151;
}
</style>
