<template>
  <div class="login-wrapper">
    <form class="login-form" @submit.prevent="onSubmit">
      <h2>Iniciar sesión</h2>
      <div class="form-group">
        <label for="username">Usuario</label>
        <input
          id="username"
          v-model="username"
          type="text"
          autocomplete="username"
          required
        />
      </div>
      <div class="form-group">
        <label for="password">Contraseña</label>
        <input
          id="password"
          v-model="password"
          type="password"
          autocomplete="current-password"
          required
        />
      </div>
      <button class="btn primary" type="submit" :disabled="loading">
        {{ loading ? 'Ingresando…' : 'Entrar' }}
      </button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import useAuth from '../composables/useAuth'

const router = useRouter()
const { login } = useAuth()

// form state
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref<string | null>(null)

async function onSubmit() {
  if (loading.value) return
  loading.value = true
  error.value = null
  try {
    await login(username.value, password.value)
    // on success go to application root
    router.push('/application')
  } catch (e: any) {
    // show message from backend if available
    error.value = e?.response?.data?.message || e?.message || 'Error desconocido'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - var(--topbar-h));
  background: var(--bg);
  color: var(--text);
}
.login-form {
  background: var(--bg-elev);
  padding: 2rem;
  border-radius: 0.5rem;
  box-shadow: var(--shadow);
  width: 100%;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.form-group label {
  display: block;
  margin-bottom: 0.35rem;
  font-size: 0.85rem;
  color: var(--muted);
}
.form-group input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid var(--border);
  border-radius: 0.25rem;
  background: var(--panel);
  color: var(--text);
}
.form-group input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 1px var(--primary);
}
.btn.primary {
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  background: var(--primary);
  color: white;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: background 0.2s ease;
}
.btn.primary:hover {
  background: var(--primary-700);
}
.btn.primary:disabled {
  background: var(--border);
  cursor: default;
}
.error {
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}
h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  text-align: center;
  font-size: 1.25rem;
  font-weight: 600;
}
</style>