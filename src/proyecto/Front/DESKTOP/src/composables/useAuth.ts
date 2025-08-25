import { ref } from 'vue'
import api from '@/services/api'
import axios from 'axios'

/**
 * Simple authentication composable.
 *
 * Exposes a reactive `token` ref along with `login`, `logout` and
 * `isAuthenticated` helpers. When a token is present it is persisted
 * to localStorage and automatically attached to all axios requests
 * via the Authorization header.
 */
const stored = localStorage.getItem('auth_token')
// a reactive token that other components can watch
const token = ref<string | null>(stored)

// if we already have a token in storage, make sure axios sends it
if (stored) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${stored}`
}

/**
 * Perform a login against the API.
 *
 * The backend at `/auth/login` is expected to accept an object with
 * `username` and `password` fields and return a JSON payload with a
 * `token` property when authentication succeeds. On success the
 * returned token will be stored and attached to all subsequent
 * requests. If the request fails an error will be thrown.
 *
 * @param username the username or email of the user
 * @param password the userʼs password
 */
async function login(username: string, password: string): Promise<void> {
  const res = await api.post('/auth/login', {
    username,
    password,
  })
  const received = res.data?.token
  if (!received) {
    throw new Error('No se recibió token en la respuesta de inicio de sesión')
  }
  token.value = received
  localStorage.setItem('auth_token', received)
  api.defaults.headers.common['Authorization'] = `Bearer ${received}`
}

/**
 * Clear the current authentication state.
 */
function logout() {
  token.value = null
  localStorage.removeItem('auth_token')
  delete api.defaults.headers.common['Authorization']
}

/**
 * Determine whether a user is currently authenticated.
 */
function isAuthenticated(): boolean {
  return !!token.value
}

export default function useAuth() {
  return { token, login, logout, isAuthenticated }
}
