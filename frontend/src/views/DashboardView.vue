<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Component } from 'vue'
import OrdemDashboardCards from '@/components/ordemServico/OrdemDashboardCards.vue'

// TODO: Integrar com API para buscar dados reais
// Estrutura esperada do card:
// {
//   label: string
//   value: string | number
//   sub?: string
//   icon: Component
//   color?: string
// }

const stats = ref<{
  label: string
  value: string | number
  sub?: string
  icon: Component
  color?: string
}[]>([])

onMounted(() => {
  // TODO: Chamar serviço de API aqui para buscar dados das ordens
  // exemplo: const data = await ordemService.getDashboardStats()
  // stats.value = data
})
</script>

<template>
  <div class="p-6 space-y-6">
    <h1 class="text-3xl font-bold text-foreground">
      Dashboard de Ordens
    </h1>

    <OrdemDashboardCards :cards="stats" />
  </div>
</template>