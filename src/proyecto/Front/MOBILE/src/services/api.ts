import axios from 'axios'

/*
 * Axios instance configured for our API.  It attaches the JWT from
 * localStorage to each request and attempts to transparently renew
 * expired QR session tokens.  If the backend returns a 401 for a QR
 * token and a `qrParam` is stored, the interceptor will call
 * `/qr/access?t=<qrParam>` once to obtain a new JWT and retry the
 * original request.  If the renewal fails, the session token and
 * qrParam are cleared from localStorage and the error is propagated.
 */

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://192.168.1.34:8081'

const api = axios.create({ baseURL: BASE_URL })

// Request interceptor: attach Authorization header if a session token
// exists in localStorage.  This applies to both user tokens and QR
// session tokens.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('sessionToken')
  if (token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor: if a 401 is returned, attempt to renew the
// QR session token once by calling /qr/access with the stored qrParam.
let isRefreshing = false
let pendingQueue: Array<() => void> = []

function flushQueue() {
  pendingQueue.forEach((fn) => fn())
  pendingQueue = []
}

async function tryRenewQrJwt(): Promise<boolean> {
  const qrParam = localStorage.getItem('qrParam')
  if (!qrParam) return false
  try {
    const res = await fetch(`${BASE_URL}/qr/access?t=${encodeURIComponent(qrParam)}`)
    if (!res.ok) return false
    const newToken = await res.text()
    localStorage.setItem('sessionToken', newToken)
    return true
  } catch {
    return false
  }
}

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const status = error.response?.status
    const config = error.config
    // Only handle 401 responses for requests that have not been retried yet
    if (status === 401 && config && !config.__isRetryRequest) {
      if (isRefreshing) {
        // If a refresh is already in progress, wait for it to finish
        await new Promise<void>((resolve) => pendingQueue.push(resolve))
      } else {
        isRefreshing = true
        const ok = await tryRenewQrJwt()
        isRefreshing = false
        flushQueue()
        if (!ok) {
          // Renewal failed: clear tokens
          localStorage.removeItem('sessionToken')
          localStorage.removeItem('qrParam')
          return Promise.reject(error)
        }
      }
      // Mark this request as a retry to avoid infinite loops
      config.__isRetryRequest = true
      const newToken = localStorage.getItem('sessionToken')
      if (newToken) {
        config.headers = config.headers ?? {}
        config.headers.Authorization = `Bearer ${newToken}`
      }
      return api(config)
    }
    return Promise.reject(error)
  },
)

export default api
