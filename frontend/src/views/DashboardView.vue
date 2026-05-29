<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Component } from 'vue'
import { ClipboardList, Clock, AlertTriangle, CheckCircle2 } from 'lucide-vue-next'
import OrdemDashboardCards from '@/components/ordemServico/OrdemDashboardCards.vue'
import { ordemServicoService } from '@/services/ordemServicoService'

const stats = ref<{
  label: string
  value: string | number
  sub?: string
  icon: Component
  color?: string
}[]>([])

const iconMap: Record<string, Component> = {
  ClipboardList,
  Clock,
  AlertTriangle,
  CheckCircle2,
}

onMounted(async () => {
  try {
    const data = await ordemServicoService.dashboardStats()
    stats.value = data.map(card => ({
      label: card.label,
      value: card.value,
      sub: card.sub,
      color: card.color,
      icon: iconMap[card.icon ?? 'ClipboardList'] ?? ClipboardList,
    }))
  } catch (error) {
    console.error('Falha ao carregar dashboard:', error)
  }
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