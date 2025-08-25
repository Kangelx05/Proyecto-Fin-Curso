<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <h2>Añadir producto</h2>
      <form @submit.prevent="submitForm">
        <input v-model="formData.name" type="text" placeholder="Nombre" required />
        <input v-model="formData.description" type="text" placeholder="Descripción" />
        <input
          v-model.number="formData.price"
          type="number"
          step="0.01"
          placeholder="Precio"
          required
        />
        <input v-model="formData.category" type="text" placeholder="Categoría" required />
        <input type="file" @change="onFileChange" accept="image/*" />
        <div class="modal-actions">
          <button class="btn primary" type="submit">Guardar</button>
          <button class="btn secondary" type="button" @click="close">Cancelar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

interface NewProduct {
  name: string
  description: string
  price: number
  category: string
  data: string | null
}

const router = useRouter()

const formData = ref<NewProduct>({
  name: '',
  description: '',
  price: 0,
  category: '',
  data: null,
})

// Convert selected image to a base64 string (without the data URL prefix)
function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target && target.files && target.files[0]) {
    const file = target.files[0]
    const reader = new FileReader()
    reader.onload = () => {
      const result = reader.result as string
      // result looks like "data:<mime>;base64,<base64>"
      const base64 = result.split(',')[1]
      formData.value.data = base64
    }
    reader.readAsDataURL(file)
  }
}

function close() {
  router.back()
}

async function submitForm() {
  try {
    await api.post('/cardProduct', formData.value)
    // return to card list after successful creation
    router.push('/card')
  } catch (e) {
    console.error('Error al crear el producto:', e)
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Estilo del panel modal: adaptado al tema oscuro de la aplicación */
.modal-content {
  background: #1f2937;
  padding: 2rem;
  border-radius: 8px;
  width: 320px;
  max-width: 90%;
  color: #e5e7eb;
  border: 1px solid #374151;
}

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.5rem;
}

.modal-content form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

/* Estilo para los campos de entrada en el formulario de creación */
.modal-content input[type='text'],
.modal-content input[type='number'],
.modal-content input[type='file'] {
  background-color: #111827;
  border: 1px solid #374151;
  color: #f3f4f6;
  border-radius: 0.375rem;
  padding: 0.5rem;
  width: 100%;
  box-sizing: border-box;
}

.modal-content input::placeholder {
  color: #9ca3af;
}

.modal-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}

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