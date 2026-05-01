<script setup lang="ts">
import { computed } from 'vue'
import type { SidebarProps } from '@/components/ui/sidebar'
import {
  LayoutDashboard, Map, Wrench, ClipboardList,
  Users, UserCog, FileText,
  Settings2, GalleryVerticalEnd, Plus, Monitor, Award, Server,
} from "lucide-vue-next"

import NavMain from '@/components/NavMain.vue'
import NavUser from '@/components/NavUser.vue'
import TeamSwitcher from '@/components/TeamSwitcher.vue'
import {
  Sidebar, SidebarContent, SidebarFooter,
  SidebarHeader, SidebarRail,
} from '@/components/ui/sidebar'

const props = withDefaults(defineProps<SidebarProps>(), {
  collapsible: "icon",
})

const role = computed(() => localStorage.getItem('user_role'))
const userEmail = computed(() => localStorage.getItem('user_email') ?? 'usuario@altave.com.br')
const isTecnico = computed(() => role.value === 'ROLE_TECNICO')

const teams = [
  { name: "Altave", logo: GalleryVerticalEnd, plan: "Gestão de Ativos" },
]

const navGeralAdmin = [
  { title: "Dashboard", url: "/dashboard", icon: LayoutDashboard },
  { title: "Mapa", url: "/mapa", icon: Map },
  { title: "Manutenção", url: "/manutencao", icon: Wrench },
  { title: "Gestão de Ordens", url: "/ordens", icon: ClipboardList },
  { title: "Minhas Ordens", url: "/minhas-ordens", icon: ClipboardList },
]

const navCadastros = [
  { title: "Clientes", url: "/clientes", icon: Users },
  { title: "Técnicos", url: "/tecnicos", icon: UserCog },
  { title: "Contratos", url: "/contratos", icon: FileText },
]

const navCatalogo = [
  { title: "Sistemas", url: "/softwares", icon: Monitor },
  { title: "Certificações", url: "/certificacoes", icon: Award },
  { title: "Máquinas", url: "/catalogo-maquinas", icon: Server },
]

const navConfig = [
  { title: "Configurações", url: "/configuracoes", icon: Settings2 },
]

const navGeralTecnico = [
  { title: "Ordens Técnico", url: "/ordens-tecnico", icon: ClipboardList },
]

const currentUser = computed(() => ({
  name: userEmail.value,
  email: userEmail.value,
  avatar: "",
}))
</script>

<template>
  <Sidebar v-bind="props">
    <SidebarHeader>
      <TeamSwitcher :teams="teams" />
    </SidebarHeader>

    <SidebarContent>
      <template v-if="!isTecnico">
        <div class="px-3 pt-2 group-data-[collapsible=icon]:px-0 group-data-[collapsible=icon]:pt-2">
          <RouterLink to="/ordens">
            <button class="w-full flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-sm font-medium py-2 rounded-lg transition-colors group-data-[collapsible=icon]:rounded-md group-data-[collapsible=icon]:w-8 group-data-[collapsible=icon]:h-8 group-data-[collapsible=icon]:mx-auto">
              <Plus class="w-4 h-4 shrink-0" />
              <span class="group-data-[collapsible=icon]:hidden">Cadastrar Nova Ordem</span>
            </button>
          </RouterLink>
        </div>

        <NavMain label="Geral" :items="navGeralAdmin" />
        <NavMain label="Cadastros" :items="navCadastros" />
        <NavMain label="Catálogos" :items="navCatalogo" />
      </template>

      <template v-else>
        <NavMain label="Geral" :items="navGeralTecnico" />
      </template>

      <NavMain label="" :items="navConfig" />
    </SidebarContent>

    <SidebarFooter>
      <NavUser :user="currentUser" />
    </SidebarFooter>

    <SidebarRail />
  </Sidebar>
</template>