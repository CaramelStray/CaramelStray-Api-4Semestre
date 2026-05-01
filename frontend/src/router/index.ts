import path from 'path/win32'
import { createRouter, createWebHistory } from 'vue-router'

// Página inicial de cada role após login
const HOME_BY_ROLE: Record<string, string> = {
  ROLE_ADMIN: '/ordens',
  ROLE_TECNICO: '/minhas-ordens',
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/',
      redirect: '/ordens',
    },
    {
      path: '/minhas-ordens',
      component: () => import('@/views/MinhasOrdensView.vue'),
      meta: { roles: ['ROLE_TECNICO'] },
    },
    {
      path: '/clientes',
      component: () => import('@/views/ClientesView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/clientes/:id',
      component: () => import('@/views/ClienteDetalhesView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/contratos',
      component: () => import('@/views/ContratosView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/contratos/:id',
      component: () => import('@/views/ContratoDetalhesView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/tecnicos',
      component: () => import('@/views/TecnicosView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/tecnicos/:id',
      component: () => import('@/views/TecnicoDetalhesView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/softwares',
      component: () => import('@/views/CatalogoSoftwareView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/certificacoes',
      component: () => import('@/views/HabilidadesView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/catalogo-maquinas',
      component: () => import('@/views/CatalogoMaquinasView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/ordens',
      component: () => import('@/views/OrdensView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/ordens/:id',
      component: () => import('@/views/OrdemDetalhesView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/relatorio-manutencao',
      component: () => import('@/views/RelatorioManutencaoView.vue'),
      meta: { roles: ['ROLE_ADMIN'] },
    },
    {
      path: '/ordens-tecnico',
      component: () => import('@/views/OrdensTecnicoView.vue'),
      meta: { roles: ['ROLE_TECNICO'] },
    }
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('user_role') ?? ''

  if (!token && to.path !== '/login') {
    next('/login')
    return
  }

  if (token && (to.path === '/login' || to.path === '/')) {
    next(HOME_BY_ROLE[role] ?? '/ordens')
    return
  }

  const allowedRoles = to.meta.roles as string[] | undefined
  if (allowedRoles && !allowedRoles.includes(role)) {
    next(HOME_BY_ROLE[role] ?? '/ordens')
    return
  }

  next()
})

export default router
