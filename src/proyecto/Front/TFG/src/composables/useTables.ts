import { ref } from 'vue'
import api from '@/services/api'

const iconos = ref<any[]>([])

const reloadContent = async () => {
  try {
    const response = await api.get('/table')
    iconos.value = response.data.map((table: any) => ({
      id: table.id,
      x: table.posX,
      y: table.posY,
      num_Table: table.num_Table,
      waiter_id: table.waiter.id,
      num_Customers: table.num_Customers,
      state: table.state.toLowerCase(),
    }))
  } catch (error) {
    console.error('Error al obtener las mesas:', error)
  }
}

export function useTables() {
  return {
    iconos,
    reloadContent,
  }
}
