<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <h2>Editar producto</h2>
      <form @submit.prevent="submitForm">
        <input v-model="formData.name" type="text" placeholder="Nombre" required />
        <br />
        <input v-model="formData.description" type="text" placeholder="Descripción" />
        <br />
        <input
          v-model.number="formData.price"
          type="number"
          placeholder="Precio"
          step="0.01"
          required
        />
        <br />
        <input v-model="formData.category" type="text" placeholder="Categoría" required />
        <br />
        <button type="submit">Guardar</button>
        <button type="button" @click="close">Cancelar</button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'

interface Producto {
  id: number
  name: string
  description: string
  price: number
  category: string
  data: Uint8Array | null
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

// Obtener los datos del producto al cargar el componente
onMounted(async () => {
  try {
    const response = await axios.get<Producto>(`http://localhost:8081/cardProduct/${productoId}`)
    formData.value = response.data
  } catch (error) {
    console.error('Error al cargar el producto:', error)
  }
})

function close() {
  router.back()
}

function submitForm() {
  axios
    .put(`http://localhost:8081/cardProduct/${formData.value.id}`, formData.value)
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

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 300px;
}
</style>
