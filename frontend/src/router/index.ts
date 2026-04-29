import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'), // Certifique-se que criou o Login.vue aqui
    },
    {
      path: '/',
      redirect: '/ordens', // Quando acessar localhost:5173/, vai direto para ordens
    },
    {
      path: '/clientes',
      component: () => import('@/views/ClientesView.vue'),
    },
    {
      path: '/clientes/:id',
      component: () => import('@/views/ClienteDetalhesView.vue'),
    },
    {
      path: '/contratos',
      component: () => import('@/views/ContratosView.vue'),
    },
    {
      path: '/contratos/:id',
      component: () => import('@/views/ContratoDetalhesView.vue'),
    },
    {
      path: '/tecnicos',
      component: () => import('@/views/TecnicosView.vue'),
    },
    {
      path: '/tecnicos/:id',
      component: () => import('@/views/TecnicoDetalhesView.vue'),
    },
    {
      path: '/softwares',
      component: () => import('@/views/CatalogoSoftwareView.vue'),
    },
    {
      path: '/certificacoes',
      component: () => import('@/views/HabilidadesView.vue')
    },
    {
      // Corrigido o caminho para usar o alias @ (padrão)
      path: '/catalogo-maquinas',
      component: () => import('@/views/CatalogoMaquinasView.vue') 
    },
    {
      path: '/ordens',
      component: () => import('@/views/OrdensView.vue'),
    },
    {
      path: '/ordens/:id',
      component: () => import('@/views/OrdemDetalhesView.vue'),
    },
    {
      path: '/relatorio-manutencao',
      component: () => import('@/views/RelatorioManutencaoView.vue'),
    },
    {
      path: '/minhas-ordens',
      component: () => import('@/views/MinhasOrdensView.vue'),
    },
  ],
})

// GUARDIÃO DE ROTAS (Bloqueia quem não tem token)
router.beforeEach((to, from, next) => {
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path)
  
  // Verifica se o token existe no localStorage
  const loggedIn = localStorage.getItem('token')

  if (authRequired && !loggedIn) {
    // Se a rota exige login e NÃO há token, joga pro login
    next('/login')
  } else if (to.path === '/login' && loggedIn) {
    // Se a pessoa JÁ ESTÁ logada e tenta acessar a tela de login, joga pra Ordens
    next('/ordens')
  } else {
    // Tudo certo, permite o acesso à rota desejada
    next()
  }
})

export default router