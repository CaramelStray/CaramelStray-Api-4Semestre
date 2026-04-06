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

const route = useRoute()

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/dashboard': 'Dashboard',
    '/mapa': 'Mapa (GIS)',
    '/manutencao': 'Manutenção',
    '/ordens': 'Gestão de Ordens',
    '/clientes': 'Clientes',
    '/maquinas': 'Máquinas',
    '/tecnicos': 'Técnicos',
    '/contratos': 'Contratos',
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
                  <BreadcrumbPage>{{ pageTitle }}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>

        <RouterView />

      </SidebarInset>
  </SidebarProvider>
  <Toaster richColors position="top-right" />
</template>