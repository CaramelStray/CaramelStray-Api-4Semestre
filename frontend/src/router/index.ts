import { createRouter, createWebHistory } from 'vue-router'

// Importe o componente que criamos (ajuste o nome se você escolheu outro)
import ClienteCadastro from '../views/ClienteCadastro.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/clientes',
      name: 'clientes',
      component: ClienteCadastro
      // Você também pode usar "lazy loading" (carregamento sob demanda) assim:
      // component: () => import('../views/ClienteCadastro.vue')
    }
  ],
})

export default router