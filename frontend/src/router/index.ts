import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/clientes',
      component: () => import('@/views/ClientesView.vue'),
    }
  ],
})

export default router