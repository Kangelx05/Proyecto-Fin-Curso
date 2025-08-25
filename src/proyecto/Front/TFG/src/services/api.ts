import axios from 'axios'

/**
 * Configuración centralizada de Axios para la aplicación de escritorio.
 *
 * Define una instancia con la URL base del backend y un interceptor de
 * solicitud que añade el token almacenado en localStorage en la cabecera
 * Authorization de todas las peticiones.
 */
const api = axios.create({
  baseURL: 'http://localhost:8081',
})

// Añade el token JWT a las solicitudes si existe en localStorage
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers = config.headers || {}
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

export default api