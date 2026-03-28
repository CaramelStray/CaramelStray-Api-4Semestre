import { createRouter, createWebHistory } from 'vue-router'

// Importe o componente que criamos (ajuste o nome se você escolheu outro)
import ClienteCadastro from '../views/ClienteCadastro.vue'

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