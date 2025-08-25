import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ShowTableInfo from '../components/TableMap/CRUD/ShowTableInfo.vue'
import AddTable from '../components/TableMap/CRUD/AddTable.vue'
import EditCardElement from '../components/Menu/EditCardElement.vue'
import AddCardProduct from '../components/Menu/AddCardProduct.vue'
import KitchenView from '../views/KitchenView.vue'
import CardComponent from '../components/Menu/CardComponent.vue'
import DragAndDrop from '../components/TableMap/DragAndDrop.vue'
import ServeView from '../views/ServeView.vue'
import FacturaView from '../views/FacturaView.vue'

// small helper to check auth state from localStorage
function hasToken() {
  return !!localStorage.getItem('auth_token')
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Página de login
    { path: '/login', name: 'login', component: LoginView },

    // Redirección raíz -> application
    { path: '/', redirect: '/application' },

    {
      path: '/application',
      name: 'application',
      component: DragAndDrop,
      children: [
        { path: 'add', name: 'application-add', component: AddTable },
        // Ruta de factura antes de la ruta genérica para evitar conflictos
        { path: 'factura/:tableId', name: 'application-factura', component: FacturaView, props: true },
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
        { path: 'add', name: 'card-add', component: AddCardProduct },
      ],
    },

    {
      path: '/kitchen',
      name: 'kitchen',
      component: KitchenView,
    },

    {
      path: '/serve',
      name: 'serve',
      component: ServeView,
    },

    // Cualquier otra ruta -> application
    { path: '/:pathMatch(.*)*', redirect: '/application' },
  ],
})

// navegación global: redirige a /login si no hay token y la ruta no es pública
router.beforeEach((to, from, next) => {
  // permitir acceso libre a /login
  if (to.path === '/login') {
    return next()
  }
  // cualquier otra ruta requiere token
  if (!hasToken()) {
    return next({ path: '/login' })
  }
  next()
})

export default router
