<script lang="ts">
export const description = "A sidebar that collapses to icons."
export const iframeHeight = "800px"
export const containerClass = "w-full h-full"
import { Toaster } from '@/components/ui/sonner'
</script>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from "@/components/AppSidebar.vue"
import {
  Breadcrumb, BreadcrumbItem, BreadcrumbLink,
  BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { dynamicBreadcrumb } from '@/composables/useBreadcrumbs'

const route = useRoute()

const basePageTitle = computed(() => {
  if (route.path.startsWith('/ordens')) return 'Gestão de Ordens'
  if (route.path.startsWith('/clientes')) return 'Clientes'
  if (route.path.startsWith('/contratos')) return 'Contratos'
  if (route.path.startsWith('/tecnicos')) return 'Técnicos'
  
  const titles: Record<string, string> = {
    '/dashboard': 'Dashboard',
    '/mapa': 'Mapa (GIS)',
    '/manutencao': 'Manutenção',
    '/relatorio-manutencao': 'Histórico de Manutenções',
    '/viagem-preparacao': 'Preparacao de Viagem',
    '/maquinas': 'Máquinas',
    '/catalogo-maquinas': 'Máquinas',
    '/softwares': 'Sistemas',
    '/certificacoes': 'Certificações',
    '/configuracoes': 'Configurações',
  }
  return titles[route.path] ?? 'Altave'
})
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
      <SidebarInset>
        <header class="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
          <div class="flex items-center gap-2 px-4">
            <SidebarTrigger class="-ml-1" />
            <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <template v-if="dynamicBreadcrumb">
                    <BreadcrumbLink :href="'#'" @click.prevent="$router.back()">
                      {{ basePageTitle }}
                    </BreadcrumbLink>
                  </template>
                  <template v-else>
                    <BreadcrumbPage>{{ basePageTitle }}</BreadcrumbPage>
                  </template>
                </BreadcrumbItem>
                
                <template v-if="dynamicBreadcrumb">
                  <BreadcrumbSeparator />
                  <BreadcrumbItem>
                    <BreadcrumbPage>{{ dynamicBreadcrumb }}</BreadcrumbPage>
                  </BreadcrumbItem>
                </template>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>

        <RouterView />

      </SidebarInset>
  </SidebarProvider>
  <Toaster richColors position="top-right" />
</template>
