import { createRouter, createWebHistory } from 'vue-router'
import DragAndDrop from '@/components/DragAndDrop.vue'
import AddTable from '@/components/AddTable.vue'
import ShowTableInfo from '@/components/ShowTableInfo.vue'
import CardComponent from '@/components/CardComponent.vue'
import EditCardElement from '@/components/EditCardElement.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Redirección raíz -> application
    { path: '/', redirect: '/application' },

    {
      path: '/application',
      name: 'application',
      component: DragAndDrop,
      children: [
        { path: 'add', name: 'application-add', component: AddTable },
        { path: ':tableId', name: 'application-table', component: ShowTableInfo, props: true },
      ],
    },

    {
      path: '/card',
      name: 'card',
      component: CardComponent,
      // importante: sin "/" inicial para que sea hija real
      children: [
        { path: 'update/:id', name: 'card-update', component: EditCardElement, props: true },
      ],
    },

    // Cualquier otra ruta -> application
    { path: '/:pathMatch(.*)*', redirect: '/application' },
  ],
})

export default router
