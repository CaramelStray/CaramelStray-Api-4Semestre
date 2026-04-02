import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/clientes',
      component: () => import('@/views/ClientesView.vue'),
    },
    {
      path: '/contratos',
      component: () => import('@/views/ContratosView.vue'),
     },
    {
      path: '/tecnicos',
      component: () => import('@/views/TecnicosView.vue'),
    }
  ],
})

export default router