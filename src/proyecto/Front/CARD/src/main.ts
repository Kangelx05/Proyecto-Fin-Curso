import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

/*
 * Bootstrap function that runs before the Vue app is mounted.  It looks
 * for the query parameter `t` in the current URL (representing a QR
 * token), stores it in localStorage as `qrParam` for later renewal,
 * and, if no session token is present, attempts to redeem the QR
 * token by calling the backend `/qr/access` endpoint.  If successful
 * it stores the returned JWT under `sessionToken`.
 */
async function bootstrap() {
  try {
    const url = new URL(window.location.href)
    const t = url.searchParams.get('t')
    // Persist the QR parameter for token renewals
    if (t) {
      localStorage.setItem('qrParam', t)
    }
    const existingToken = localStorage.getItem('sessionToken')
    // If there is no token but a QR parameter exists, redeem it
    if (!existingToken && t) {
      const base = import.meta.env.VITE_API_URL ?? 'http://192.168.1.34:8081'
      const res = await fetch(`${base}/qr/access?t=${encodeURIComponent(t)}`)
      if (res.ok) {
        const token = await res.text()
        localStorage.setItem('sessionToken', token)
      } else {
        // If redemption fails, ensure any stale token is cleared
        localStorage.removeItem('sessionToken')
      }
    }
  } catch (e) {
    console.error('bootstrap error', e)
  }
}

// Initialise the application after bootstrapping
;(async () => {
  await bootstrap()
  const app = createApp(App)
  app.use(router)
  app.mount('#app')
})()
