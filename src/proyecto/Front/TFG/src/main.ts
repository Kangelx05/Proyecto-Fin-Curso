import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)

// Espera a que el router esté listo para evitar flashes/404 temporales
router.isReady().then(() => {
  app.mount('#app')
})
