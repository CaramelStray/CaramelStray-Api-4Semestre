<script setup lang="ts">
import { computed } from 'vue'
import { Anchor, ShipWheel } from 'lucide-vue-next'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import type { ViagemResponseDTO } from '@/services/viagemService'

const props = defineProps<{
  viagens: ViagemResponseDTO[]
}>()

const calcularDiasRestantes = (dataRetorno?: string | null): number => {
  if (!dataRetorno) return 0

  try {
    const hoje = new Date()
    hoje.setHours(0, 0, 0, 0)

    const dataRetornoDate = new Date(dataRetorno)
    dataRetornoDate.setHours(0, 0, 0, 0)

    const diferenca = dataRetornoDate.getTime() - hoje.getTime()
    const dias = Math.ceil(diferenca / (1000 * 60 * 60 * 24))

    return Math.max(0, dias)
  } catch {
    return 0
  }
}

const embarcacoesAtracadas = computed(() => {
  const embarcacoes = props.viagens
    .filter(v => v.status === 'EM_ANDAMENTO' || v.status === 'ABERTA')
    .slice(0, 3)
    .map((v) => ({
      codigo: v.codigo,
      nome: v.nomeCliente ?? `Cliente #${v.codigoCliente}`,
      local: v.destino ?? v.origem ?? 'Local não informado',
      chegada: v.dataSaidaPrevista,
      saida: v.dataRetornoPrevisto,
      dias: calcularDiasRestantes(v.dataRetornoPrevisto),
    }))

  // Encontrar o menor número de dias para destacar
  const menorDias = embarcacoes.length > 0
    ? Math.min(...embarcacoes.map(e => e.dias))
    : null

  return embarcacoes.map(e => ({
    ...e,
    destaque: menorDias !== null && e.dias === menorDias,
  }))
})
</script>

<template>
  <Card class="bg-sidebar border-border">
    <CardHeader class="pb-3 border-b border-border">
      <CardTitle class="flex items-center justify-between text-sm font-semibold text-foreground">
        <span class="flex items-center gap-2">
          <Anchor class="size-4 text-cyan-400" />
          Embarcações atracadas agora
        </span>

        <span class="rounded-full bg-cyan-500/15 text-cyan-300 px-2 py-0.5 text-xs font-bold">
          {{ embarcacoesAtracadas.length }}
        </span>
      </CardTitle>
    </CardHeader>

    <CardContent class="p-4 space-y-3">
      <div
        v-for="embarcacao in embarcacoesAtracadas"
        :key="embarcacao.codigo"
        class="flex items-center gap-3 rounded-xl border p-3 transition-colors"
        :class="embarcacao.destaque
          ? 'border-amber-500/40 bg-amber-500/10'
          : 'border-border bg-muted/20 hover:bg-muted/30'"
      >
        <div
          class="flex size-10 items-center justify-center rounded-lg border"
          :class="embarcacao.destaque
            ? 'border-amber-500/40 bg-amber-500/15 text-amber-300'
            : 'border-cyan-500/30 bg-cyan-500/10 text-cyan-300'"
        >
          <ShipWheel class="size-5" />
        </div>

        <div class="min-w-0 flex-1">
          <p class="truncate text-sm font-bold text-foreground">
            {{ embarcacao.nome }}
          </p>
          <p class="truncate text-xs text-muted-foreground">
            {{ embarcacao.local }}
          </p>
          <p class="mt-0.5 text-[11px] text-muted-foreground">
            Saída prevista
          </p>
        </div>

        <div
          class="shrink-0 rounded-full px-3 py-1 text-xs font-bold"
          :class="embarcacao.destaque
            ? 'bg-amber-500/20 text-amber-300'
            : 'bg-cyan-500/15 text-cyan-300'"
        >
          {{ embarcacao.dias }} dias
        </div>
      </div>

      <div
        v-if="embarcacoesAtracadas.length === 0"
        class="rounded-lg border border-dashed border-border p-5 text-center text-sm text-muted-foreground"
      >
        Nenhuma embarcação atracada no momento.
      </div>
    </CardContent>
  </Card>
</template>