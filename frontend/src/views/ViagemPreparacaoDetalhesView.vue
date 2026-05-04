<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  ArrowLeft, Boxes, CalendarClock, CheckCircle2, ChevronRight, ClipboardCheck,
  Clock, FileText, Loader2, MapPinned, Milestone, Route, ShipWheel, Truck,
} from 'lucide-vue-next'
import { viagemService, type ViagemCreateDTO, type ViagemResponseDTO } from '@/services/viagemService'
import {
  ordemServicoService,
  type OrdemServicoChecklistAtivoResponseDTO,
  type OrdemServicoResponseDTO,
} from '@/services/ordemServicoService'

const route = useRoute()
const router = useRouter()
const viagemId = Number(route.params.id)

const viagem = ref<ViagemResponseDTO | null>(null)
const ordem = ref<OrdemServicoResponseDTO | null>(null)
const checklistAtivos = ref<OrdemServicoChecklistAtivoResponseDTO[]>([])
const loading = ref(true)
const finishing = ref(false)
const erro = ref('')

const statusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  ABERTA: {
    dot: 'bg-amber-500',
    badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',
    label: 'Aberta',
  },
  EM_ANDAMENTO: {
    dot: 'bg-blue-500',
    badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',
    label: 'Em andamento',
  },
  FINALIZADA: {
    dot: 'bg-emerald-500',
    badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30',
    label: 'Finalizada',
  },
  CANCELADA: {
    dot: 'bg-red-500',
    badge: 'bg-red-500/15 text-red-400 border-red-500/30',
    label: 'Cancelada',
  },
}

const paradasOrdenadas = computed(() =>
  [...(viagem.value?.paradas ?? [])].sort((a, b) => a.ordem - b.ordem)
)

const distancia = computed(() => viagem.value?.kmReal ?? viagem.value?.kmPrevisto ?? null)

const dataInicioBase = computed(() =>
  viagem.value?.dataSaidaReal ?? viagem.value?.dataSaidaPrevista ?? null
)

const dataFimBase = computed(() =>
  viagem.value?.dataRetornoReal ?? viagem.value?.dataRetornoPrevisto ?? null
)

const duracaoTotal = computed(() => {
  if (!dataInicioBase.value || !dataFimBase.value) return null
  const inicio = new Date(dataInicioBase.value)
  const fim = new Date(dataFimBase.value)
  const diff = fim.getTime() - inicio.getTime()
  if (Number.isNaN(diff) || diff <= 0) return null

  const horas = Math.floor(diff / 3_600_000)
  const minutos = Math.floor((diff % 3_600_000) / 60_000)
  if (horas > 0) return `${horas}h ${String(minutos).padStart(2, '0')}m`
  return `${minutos}m`
})

const checklistConferidos = computed(() =>
  checklistAtivos.value.filter(item => item.levado).length
)

const origemDestino = computed(() => {
  const origem = viagem.value?.origem ?? 'Origem nao informada'
  const destino = viagem.value?.destino ?? 'Destino nao informado'
  return `${origem} -> ${destino}`
})

const formatDateTime = (dt?: string | null) => {
  if (!dt) return '-'
  const d = new Date(dt)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatTime = (dt?: string | null) => {
  if (!dt) return '-'
  const d = new Date(dt)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
}

const getParadaSaida = () => {
  const ultimaParada = paradasOrdenadas.value[paradasOrdenadas.value.length - 1]
  return ultimaParada?.dataSaidaPrevista ?? ultimaParada?.dataSaidaReal ?? null
}

const montarUpdateDTO = (status: string): ViagemCreateDTO | null => {
  if (!viagem.value) return null

  return {
    codigoTipoViagem: viagem.value.codigoTipoViagem,
    codigoCliente: viagem.value.codigoCliente,
    codigoFuncionarioResponsavel: viagem.value.codigoFuncionarioResponsavel,
    codigoOrdemServico: viagem.value.codigoOrdemServico,
    status,
    dataSaidaPrevista: viagem.value.dataSaidaPrevista,
    dataSaidaReal: viagem.value.dataSaidaReal,
    dataRetornoPrevisto: viagem.value.dataRetornoPrevisto,
    dataRetornoReal: viagem.value.dataRetornoReal,
    origem: viagem.value.origem,
    destino: viagem.value.destino,
    kmPrevisto: viagem.value.kmPrevisto,
    kmReal: viagem.value.kmReal,
    observacao: viagem.value.observacao,
    paradas: paradasOrdenadas.value.map(parada => ({
      ordem: parada.ordem,
      descricaoLocal: parada.descricaoLocal,
      endereco: parada.endereco,
      cidade: parada.cidade,
      estadoRegiao: parada.estadoRegiao,
      latitude: parada.latitude,
      longitude: parada.longitude,
      dataChegadaPrevista: parada.dataChegadaPrevista,
      dataChegadaReal: parada.dataChegadaReal,
      dataSaidaPrevista: parada.dataSaidaPrevista,
      dataSaidaReal: parada.dataSaidaReal,
      observacao: parada.observacao,
    })),
  }
}

async function finalizarViagemPreparacao() {
  const dto = montarUpdateDTO('FINALIZADA')
  if (!dto || !viagem.value) return

  finishing.value = true
  erro.value = ''
  try {
    viagem.value = await viagemService.atualizar(viagem.value.codigo, dto)
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao finalizar preparacao de viagem'
  } finally {
    finishing.value = false
  }
}

async function carregar() {
  loading.value = true
  erro.value = ''

  try {
    viagem.value = await viagemService.buscarPorId(viagemId)

    if (viagem.value.codigoOrdemServico) {
      await Promise.all([
        ordemServicoService.buscarPorId(viagem.value.codigoOrdemServico)
          .then(response => { ordem.value = response })
          .catch(() => {}),
        ordemServicoService.listarChecklistAtivos(viagem.value.codigoOrdemServico)
          .then(response => { checklistAtivos.value = response })
          .catch(() => {}),
      ])
    }
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar preparação de viagem'
  } finally {
    loading.value = false
  }
}

onMounted(carregar)
</script>

<template>
  <div class="p-6 space-y-6">
    <div class="flex items-center gap-2 text-sm text-muted-foreground">
      <button
        class="flex items-center gap-1.5 hover:text-foreground transition-colors cursor-pointer"
        @click="router.push('/viagem-preparacao')"
      >
        <ArrowLeft class="size-4" />
        Preparação de Viagem
      </button>
      <ChevronRight class="size-3.5" />
      <span class="text-foreground font-medium">{{ viagem ? `Viagem #${viagem.codigo}` : 'Preparacao' }}</span>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-24 text-muted-foreground gap-3">
      <Loader2 class="size-5 animate-spin" /> Carregando preparação de viagem...
    </div>

    <div v-else-if="erro && !viagem" class="py-24 text-center text-red-400">{{ erro }}</div>

    <template v-else-if="viagem">
      <div class="rounded-xl border border-border bg-sidebar p-6 flex flex-col lg:flex-row items-start lg:items-center gap-5">
        <div class="flex items-center justify-center size-16 rounded-xl bg-cyan-500/10 border border-cyan-500/20 shrink-0">
          <Route class="size-8 text-cyan-400" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center flex-wrap gap-3">
            <h1 class="text-2xl font-bold text-foreground">Preparação de Viagem</h1>
            <span
              :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', statusConfig[viagem.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
            >
              <span :class="['size-1.5 rounded-full', statusConfig[viagem.status]?.dot ?? 'bg-slate-400']" />
              {{ statusConfig[viagem.status]?.label ?? viagem.status }}
            </span>
          </div>
          <p class="text-sm text-muted-foreground mt-1 truncate">
            {{ viagem.nomeCliente ?? `Cliente #${viagem.codigoCliente}` }}
            <span v-if="viagem.descricaoTipoViagem"> - {{ viagem.descricaoTipoViagem }}</span>
          </p>
          <p class="text-xs text-muted-foreground mt-0.5 truncate">
            {{ origemDestino }}
          </p>
        </div>
        <Button
          class="shrink-0 bg-emerald-600 hover:bg-emerald-700 text-white border-none"
          :disabled="viagem.status === 'FINALIZADA' || finishing"
          @click="finalizarViagemPreparacao"
        >
          <Loader2 v-if="finishing" class="size-4 mr-2 animate-spin" />
          <CheckCircle2 v-else class="size-4 mr-2" />
          Finalizar Preparacao
        </Button>
      </div>

      <div v-if="erro" class="rounded-md border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
        {{ erro }}
      </div>

      <div class="grid grid-cols-1 xl:grid-cols-[minmax(0,1fr)_320px] gap-6">
        <div class="space-y-5">
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Truck class="size-4 text-cyan-400" /> Deslocamento terrestre
              </CardTitle>
            </CardHeader>
            <CardContent class="p-5">
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Distancia</p>
                  <p class="text-lg font-semibold text-foreground mt-1">
                    {{ distancia ?? '-' }}<span v-if="distancia !== null" class="text-xs text-muted-foreground ml-1">km</span>
                  </p>
                </div>
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Tempo estimado</p>
                  <p class="text-lg font-semibold text-foreground mt-1">{{ duracaoTotal ?? '-' }}</p>
                </div>
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Responsavel</p>
                  <p class="text-sm font-semibold text-foreground mt-1 truncate">
                    {{ viagem.nomeFuncionarioResponsavel ?? 'Nao atribuido' }}
                  </p>
                </div>
              </div>

              <div class="mt-5 grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Saida</p>
                  <p class="text-sm text-foreground mt-1">{{ formatDateTime(viagem.dataSaidaReal ?? viagem.dataSaidaPrevista) }}</p>
                </div>
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Retorno</p>
                  <p class="text-sm text-foreground mt-1">{{ formatDateTime(viagem.dataRetornoReal ?? viagem.dataRetornoPrevisto) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <ShipWheel class="size-4 text-blue-400" /> Travessia / paradas
              </CardTitle>
            </CardHeader>
            <CardContent class="p-5 space-y-4">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Horario da ultima saida</p>
                  <p class="text-lg font-semibold text-foreground mt-1">{{ formatTime(getParadaSaida()) }}</p>
                </div>
                <div class="rounded-lg border border-border bg-muted/20 p-4">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Paradas planejadas</p>
                  <p class="text-lg font-semibold text-foreground mt-1">{{ paradasOrdenadas.length }}</p>
                </div>
              </div>

              <div v-if="paradasOrdenadas.length" class="rounded-md border border-border overflow-hidden">
                <div
                  v-for="parada in paradasOrdenadas"
                  :key="parada.codigo"
                  class="p-4 border-b border-border last:border-b-0 bg-muted/10"
                >
                  <div class="flex items-start gap-3">
                    <div class="flex items-center justify-center size-8 rounded-full bg-cyan-500/10 border border-cyan-500/20 text-xs font-bold text-cyan-300 shrink-0">
                      {{ parada.ordem }}
                    </div>
                    <div class="min-w-0 flex-1">
                      <p class="text-sm font-semibold text-foreground truncate">{{ parada.descricaoLocal }}</p>
                      <p class="text-xs text-muted-foreground truncate">
                        {{ [parada.endereco, parada.cidade, parada.estadoRegiao].filter(Boolean).join(', ') || 'Endereco nao informado' }}
                      </p>
                      <p class="text-xs text-muted-foreground mt-1">
                        Chegada: {{ formatDateTime(parada.dataChegadaReal ?? parada.dataChegadaPrevista) }}
                        <span class="mx-2">-</span>
                        Saida: {{ formatDateTime(parada.dataSaidaReal ?? parada.dataSaidaPrevista) }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="rounded-md border border-border bg-muted/10 p-5 text-sm text-muted-foreground">
                Nenhuma parada cadastrada para esta viagem.
              </div>
            </CardContent>
          </Card>
        </div>

        <div class="space-y-5">
          <Card class="bg-sidebar border-border">
            <CardContent class="p-5 space-y-5">
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Tempo total da viagem</p>
                <p class="text-3xl font-bold text-foreground mt-1">{{ duracaoTotal ?? '-' }}</p>
              </div>
              <div class="h-px bg-border" />
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Ordem de servico</p>
                <p class="text-sm font-mono text-foreground mt-1">
                  {{ viagem.codigoOrdemServico ? `#${viagem.codigoOrdemServico}` : '-' }}
                </p>
              </div>
              <div class="space-y-3">
                <div class="flex items-center gap-2 text-xs text-muted-foreground">
                  <ClipboardCheck class="size-4 text-emerald-400" />
                  {{ checklistConferidos }} de {{ checklistAtivos.length }} equipamentos levados
                </div>
                <div class="flex items-center gap-2 text-xs text-muted-foreground">
                  <CalendarClock class="size-4 text-blue-400" />
                  Cadastro: {{ formatDateTime(viagem.dataCadastro) }}
                </div>
                <div class="flex items-center gap-2 text-xs text-muted-foreground">
                  <Milestone class="size-4 text-cyan-400" />
                  {{ viagem.descricaoTipoViagem ?? 'Tipo nao informado' }}
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Boxes class="size-4 text-emerald-400" /> Equipamentos tecnicos
              </CardTitle>
            </CardHeader>
            <CardContent class="p-0">
              <div v-if="checklistAtivos.length" class="divide-y divide-border">
                <div v-for="item in checklistAtivos" :key="item.codigo" class="p-4 flex items-start gap-3">
                  <span :class="['mt-1 size-2 rounded-full shrink-0', item.levado ? 'bg-emerald-500' : 'bg-slate-500']" />
                  <div class="min-w-0">
                    <p class="text-sm font-semibold text-foreground truncate">
                      {{ item.descricaoAtivo || item.descricaoProduto || `Ativo #${item.codigoAtivo}` }}
                    </p>
                    <p class="text-xs text-muted-foreground truncate">
                      {{ [item.marca, item.modelo, item.numeroSerie].filter(Boolean).join(' - ') || 'Detalhes nao informados' }}
                    </p>
                  </div>
                </div>
              </div>
              <div v-else class="p-5 text-sm text-muted-foreground">
                Nenhum equipamento vinculado a ordem.
              </div>
            </CardContent>
          </Card>

          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <MapPinned class="size-4 text-cyan-400" /> Rota estimada
              </CardTitle>
            </CardHeader>
            <CardContent class="p-4">
              <div class="aspect-[4/3] rounded-md border border-dashed border-border bg-muted/20 flex flex-col items-center justify-center text-center px-6">
                <MapPinned class="size-9 text-muted-foreground/60 mb-3" />
                <p class="text-sm font-semibold text-foreground">Mapa reservado</p>
                <p class="text-xs text-muted-foreground mt-1">
                  A integracao de mapa sera implementada futuramente.
                </p>
              </div>
            </CardContent>
          </Card>

          <Card v-if="viagem.observacao || ordem?.observacaoGeral" class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <FileText class="size-4 text-blue-400" /> Observacoes
              </CardTitle>
            </CardHeader>
            <CardContent class="p-5 space-y-3 text-sm text-muted-foreground">
              <p v-if="viagem.observacao">{{ viagem.observacao }}</p>
              <p v-if="ordem?.observacaoGeral">{{ ordem.observacaoGeral }}</p>
            </CardContent>
          </Card>
        </div>
      </div>
    </template>
  </div>
</template>
