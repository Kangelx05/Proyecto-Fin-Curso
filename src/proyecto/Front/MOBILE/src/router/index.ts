import { createRouter, createWebHistory } from 'vue-router'

// Only mount the Card component at the root path.  Any other path
// redirects back to the root.  This simplifies the mobile app to
// display just the menu and cart functionality.
const Card = () => import('../components/Card.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'card',
      component: Card,
    },
    // catch-all route: redirect unknown paths to root
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
})

export default router
