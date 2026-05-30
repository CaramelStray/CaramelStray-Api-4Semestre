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

const ordensRecentes = ref([
  {
    codigo: '#OM-0000',
    titulo: 'Nenhuma ordem encontrada',
    cliente: 'Sem dados disponíveis',
    tipo: 'Aguardando integração',
    status: 'Aberta',
    prazo: '-',
  },
])

const filtroSelecionado = ref('Hoje')
</script>

<template>
  <div class="p-6 space-y-6 text-foreground">
    <!-- Cabeçalho -->
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold">
          Dashboard de ordens
        </h1>
        <p class="text-sm text-muted-foreground">
          Atualizado agora · maio 2026
        </p>
      </div>
      
      <div class="flex gap-2">
        <button
          v-for="filtro in ['Hoje', 'Semana', 'Mês']"
          :key="filtro"
          type="button"
          class="px-4 py-2 rounded-xl border text-sm transition"
          :class="filtroSelecionado === filtro
            ? 'bg-blue-100 text-blue-700 border-blue-200'
            : 'bg-muted/30 border-border text-muted-foreground hover:bg-muted'"
          @click="filtroSelecionado = filtro"
        >
          {{ filtro }}
        </button>
      </div>
    </div>

    <!-- Cards superiores -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
      <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <Circle class="w-3 h-3 fill-blue-500 text-blue-500" />
          Abertas
        </div>
        <p class="mt-2 text-4xl font-bold">
          {{ resumo.abertas }}
        </p>
        <p class="mt-1 text-sm text-muted-foreground">
          +0 desde ontem
        </p>
      </div>

      <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <Circle class="w-3 h-3 fill-yellow-500 text-yellow-500" />
          Em andamento
        </div>
        <p class="mt-2 text-4xl font-bold">
          {{ resumo.emAndamento }}
        </p>
        <p class="mt-1 text-sm text-muted-foreground">
          0 técnicos ativos
        </p>
      </div>

      <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <Circle class="w-3 h-3 fill-green-500 text-green-500" />
          Concluídas
        </div>
        <p class="mt-2 text-4xl font-bold">
          {{ resumo.concluidas }}
        </p>
        <p class="mt-1 text-sm text-muted-foreground">
          Este mês
        </p>
      </div>

      <div class="rounded-xl border border-red-400/70 bg-card p-5 shadow-sm">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <Circle class="w-3 h-3 fill-red-500 text-red-500" />
          Atrasadas
        </div>
        <p class="mt-2 text-4xl font-bold text-red-500">
          {{ resumo.atrasadas }}
        </p>
        <p class="mt-1 text-sm text-red-500">
          Requer atenção
        </p>
      </div>
    </div>

    <!-- Conteúdo inferior -->
    <div class="grid grid-cols-1 xl:grid-cols-2 gap-4">
      <!-- Volume por status -->
      <div class="rounded-xl border border-border bg-card p-6 shadow-sm">
        <h2 class="text-xl font-bold mb-5">
          Volume por status — mês atual
        </h2>

        <div class="space-y-4">
          <div class="grid grid-cols-[100px_1fr_40px] items-center gap-4">
            <span class="text-sm text-muted-foreground">Abertas</span>
            <div class="h-3 rounded-full bg-muted overflow-hidden">
              <div class="h-full rounded-full bg-blue-500" style="width: 0%" />
            </div>
            <span class="text-sm text-right">{{ resumo.abertas }}</span>
          </div>

          <div class="grid grid-cols-[100px_1fr_40px] items-center gap-4">
            <span class="text-sm text-muted-foreground">Em andamento</span>
            <div class="h-3 rounded-full bg-muted overflow-hidden">
              <div class="h-full rounded-full bg-yellow-500" style="width: 0%" />
            </div>
            <span class="text-sm text-right">{{ resumo.emAndamento }}</span>
          </div>

          <div class="grid grid-cols-[100px_1fr_40px] items-center gap-4">
            <span class="text-sm text-muted-foreground">Concluídas</span>
            <div class="h-3 rounded-full bg-muted overflow-hidden">
              <div class="h-full rounded-full bg-green-500" style="width: 0%" />
            </div>
            <span class="text-sm text-right">{{ resumo.concluidas }}</span>
          </div>

          <div class="grid grid-cols-[100px_1fr_40px] items-center gap-4">
            <span class="text-sm text-muted-foreground">Atrasadas</span>
            <div class="h-3 rounded-full bg-muted overflow-hidden">
              <div class="h-full rounded-full bg-red-500" style="width: 0%" />
            </div>
            <span class="text-sm text-right">{{ resumo.atrasadas }}</span>
          </div>

          <div class="grid grid-cols-[100px_1fr_40px] items-center gap-4">
            <span class="text-sm text-muted-foreground">Canceladas</span>
            <div class="h-3 rounded-full bg-muted overflow-hidden">
              <div class="h-full rounded-full bg-gray-500" style="width: 0%" />
            </div>
            <span class="text-sm text-right">{{ resumo.canceladas }}</span>
          </div>
        </div>

        <div class="border-t border-border mt-6 pt-6 flex flex-col items-center">
          <div class="relative w-36 h-20 overflow-hidden">
            <div class="absolute inset-0 rounded-t-full border-[12px] border-muted border-b-0" />
            <div class="absolute inset-0 rounded-t-full border-[12px] border-green-500 border-b-0" style="clip-path: inset(0 100% 0 0)" />
          </div>

          <p class="text-3xl font-bold mt-2">
            {{ resumo.taxaConclusao }}%
          </p>
          <p class="text-sm text-muted-foreground">
            taxa de conclusão
          </p>
        </div>
      </div>

      <!-- Ordens recentes -->
      <div class="rounded-xl border border-border bg-card p-6 shadow-sm">
        <h2 class="text-xl font-bold mb-5">
          Ordens recentes
        </h2>

        <div class="divide-y divide-border">
          <div
            v-for="ordem in ordensRecentes"
            :key="ordem.codigo"
            class="py-4 first:pt-0"
          >
            <div class="flex items-start justify-between gap-4">
              <div>
                <p class="font-bold">
                  {{ ordem.codigo }} · {{ ordem.titulo }}
                </p>
                <p class="text-sm text-muted-foreground">
                  {{ ordem.cliente }} — {{ ordem.tipo }}
                </p>
              </div>

              <div class="flex items-center gap-3">
                <span
                  class="px-3 py-1 rounded-full text-xs font-semibold"
                  :class="{
                    'bg-blue-100 text-blue-700': ordem.status === 'Aberta',
                    'bg-yellow-100 text-yellow-700': ordem.status === 'Em andamento',
                    'bg-green-100 text-green-700': ordem.status === 'Concluída',
                    'bg-red-100 text-red-700': ordem.status === 'Atrasada',
                  }"
                >
                  {{ ordem.status }}
                </span>

                <span class="text-sm text-muted-foreground">
                  {{ ordem.prazo }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <button
          type="button"
          class="mt-5 w-full rounded-lg border border-border py-3 font-semibold hover:bg-muted transition flex items-center justify-center gap-2"
        >
          Ver todas as ordens
          <ArrowUpRight class="w-4 h-4" />
        </button>
      </div>
    </div>
  </div>
</template>