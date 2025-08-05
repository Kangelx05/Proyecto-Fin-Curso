<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <h2 class="modal-title">Nuevo formulario</h2>
      <form @submit.prevent="submitForm" class="form-container">
        <input v-model="formData.nombre" type="text" placeholder="Nombre" required />
        <input v-model="formData.email" type="email" placeholder="Email" required />

        <div class="modal-actions">
          <button class="btn primary" type="submit">Enviar</button>
          <button class="btn secondary" type="button" @click="close">Cancelar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const tableId = route.params.tableId as string

onMounted(() => {
  console.log('Componente montado con tableId:', tableId)
})

const formData = ref({ nombre: '', email: '' })

function close() {
  router.back()
}

function submitForm() {
  console.log('Enviado:', formData.value)
  close()
}
</script>

<style scoped>
@import './modal-styles.css';
</style>
