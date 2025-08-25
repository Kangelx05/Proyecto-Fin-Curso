import { ref, onMounted, onBeforeUnmount } from 'vue'
import api from '@/services/api'
import useAuth from './useAuth'

/**
 * Composable para gestionar los pedidos de barra (bebidas pendientes) mediante SSE.
 *
 * Se conecta a un endpoint SSE del backend y actualiza la lista de pedidos en tiempo real
 * cuando se reciben eventos.  Se mantiene la función de obtención manual como respaldo.
 */
export function useBarOrders() {
  const barOrders = ref<any[]>([])
  let eventSource: EventSource | null = null

  /**
   * Recupera la lista completa de pedidos de barra de manera puntual.
   * Puede usarse como fallback cuando SSE no esté disponible.
   */
  async function fetchBarOrders() {
    try {
    const response = await api.get('/order-detail/bar-pending')
      // Recibimos la lista ordenada desde el backend; no aplicamos ordenamiento adicional
      barOrders.value = Array.isArray(response.data) ? response.data : []
    } catch (error) {
      console.error('Error al obtener los pedidos de barra:', error)
    }
  }

  onMounted(() => {
    // Intentamos conectar a la transmisión SSE para recibir actualizaciones en tiempo real
    const { token } = useAuth()
    const connectSSE = () => {
      try {
        // Adjuntar el token en la query string si existe
        let url = `${api.defaults.baseURL}/order-detail/bar-stream`
        if (token.value) {
          url += '?token=' + encodeURIComponent(token.value)
        }
        eventSource = new EventSource(url)
        eventSource.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            // El backend ya envía los pedidos ordenados por la hora de creación
            barOrders.value = Array.isArray(data) ? data : []
          } catch (e) {
            console.error('Error al parsear evento SSE de pedidos de barra:', e)
          }
        }
        eventSource.onerror = () => {
          console.warn('SSE de pedidos de barra desconectado, usando polling como respaldo')
          // Cierra la conexión para evitar reconexiones continuas
          if (eventSource) {
            eventSource.close()
            eventSource = null
          }
          // Recarga manual y comienza el polling como fallback
          fetchBarOrders()
          // Poll cada 10 segundos si no hay SSE
          const interval = setInterval(fetchBarOrders, 10000)
          // Limpiar al desmontar
          onBeforeUnmount(() => clearInterval(interval))
        }
      } catch (err) {
        console.error('No se pudo establecer conexión SSE, usando polling:', err)
        fetchBarOrders()
        const interval = setInterval(fetchBarOrders, 10000)
        onBeforeUnmount(() => clearInterval(interval))
      }
    }
    // Recupera pedidos pendientes al inicializar antes de conectar al SSE
    fetchBarOrders()
    connectSSE()
  })

  onBeforeUnmount(() => {
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
  })

  return { barOrders, fetchBarOrders }
}