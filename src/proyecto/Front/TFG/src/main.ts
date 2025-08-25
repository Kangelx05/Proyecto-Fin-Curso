import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import api from './services/api'

// initialise auth composable to setup axios defaults if a token exists
import useAuth from './composables/useAuth'

const app = createApp(App)
app.use(router)

// call useAuth once so that any stored token is attached to axios
useAuth()

// Configure a global axios response interceptor to handle expired tokens.
// If the backend returns a 401/403 (e.g. token caducado) we cerramos la sesión
// y redirigimos al usuario a la página de login.
const { logout } = useAuth()
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status
    if (status === 401 || status === 403) {
      // limpiar token y navegar a login
      logout()
      // evita quedarse en una ruta protegida
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
    return Promise.reject(error)
  },
)

// Espera a que el router esté listo para evitar flashes/404 temporales
router.isReady().then(() => {
  app.mount('#app')
})
