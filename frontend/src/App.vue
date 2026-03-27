<script setup lang="ts">
import { SidebarProvider, SidebarInset } from '@/components/ui/sidebar'
import AppSidebar from '@/components/layout/Sidebar.vue'
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <SidebarInset>
      <RouterView />
    </SidebarInset>
  </SidebarProvider>
</template>