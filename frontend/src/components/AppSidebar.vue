<script setup lang="ts">
import type { SidebarProps } from '@/components/ui/sidebar'
import {
  LayoutDashboard, Map, Wrench, ClipboardList,
  Users, Bot, UserCog, FileText,
  Settings2, GalleryVerticalEnd, Plus, Monitor, Award
} from "lucide-vue-next"

import NavMain from '@/components/NavMain.vue'
import NavUser from '@/components/NavUser.vue'
import TeamSwitcher from '@/components/TeamSwitcher.vue'
import {
  Sidebar, SidebarContent, SidebarFooter,
  SidebarHeader, SidebarRail, SidebarMenu,
  SidebarMenuItem, SidebarMenuButton,
} from '@/components/ui/sidebar'

const props = withDefaults(defineProps<SidebarProps>(), {
  collapsible: "icon",
})

const data = {
  user: {
    name: "Usuário",
    email: "usuario@altave.com.br",
    avatar: "",
  },
  teams: [
    {
      name: "Altave Industrial",
      logo: GalleryVerticalEnd,
      plan: "Gestão de Ativos",
    },
  ],
  navGeral: [
    { title: "Dashboard", url: "/dashboard", icon: LayoutDashboard },
    { title: "Mapa", url: "/mapa", icon: Map },
    { title: "Manutenção", url: "/manutencao", icon: Wrench },
    { title: "Gestão de Ordens", url: "/ordens", icon: ClipboardList },

  ],
    navCadastros: [
    { title: "Clientes", url: "/clientes", icon: Users },
    { title: "Máquinas", url: "/maquinas", icon: Bot },
    { title: "Técnicos", url: "/tecnicos", icon: UserCog },
    { title: "Contratos", url: "/contratos", icon: FileText },
  ],
  navCatalogo: [
    { title: "Sistemas", url: "/softwares", icon: Monitor },
    { title: "Habilidades", url: "/habilidades", icon: Award },
  ],
  navConfig: [
    { title: "Configurações", url: "/configuracoes", icon: Settings2 },
  ],
}
</script>

<template>
  <Sidebar v-bind="props">
    <SidebarHeader>
      <TeamSwitcher :teams="data.teams" />
    </SidebarHeader>

    <SidebarContent>

    <div class="px-3 pt-2 group-data-[collapsible=icon]:px-0 group-data-[collapsible=icon]:pt-2">
      <RouterLink to="/ordens">
        <button class="w-full flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-sm font-medium py-2 rounded-lg transition-colors group-data-[collapsible=icon]:rounded-md group-data-[collapsible=icon]:w-8 group-data-[collapsible=icon]:h-8 group-data-[collapsible=icon]:mx-auto">
          <Plus class="w-4 h-4 shrink-0" />
          <span class="group-data-[collapsible=icon]:hidden">Cadastrar Nova Ordem</span>
        </button>
      </RouterLink>
    </div>

      <NavMain label="Geral" :items="data.navGeral" />
      <NavMain label="Cadastros" :items="data.navCadastros" />
      <NavMain label="Catálogos" :items="data.navCatalogo" />
      <NavMain label="" :items="data.navConfig" />

    </SidebarContent>

    <SidebarFooter>
      <NavUser :user="data.user" />
    </SidebarFooter>

    <SidebarRail />
  </Sidebar>
</template>