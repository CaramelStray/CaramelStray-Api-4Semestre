<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  ArrowLeft, Boxes, CalendarClock, ChevronRight, ClipboardCheck,
  FileText, Loader2, MapPinned, Milestone, Pencil, Route, Truck,
} from 'lucide-vue-next'
import { viagemService, type ViagemResponseDTO } from '@/services/viagemService'
import {
  ordemServicoService,
  type OrdemServicoChecklistAtivoResponseDTO,
  type OrdemServicoResponseDTO,
} from '@/services/ordemServicoService'
import ViagemPreparacaoEdicaoModal from '@/components/viagem/ViagemPreparacaoEdicaoModal.vue'

const route = useRoute()
const router = useRouter()
const viagemId = Number(route.params.id)

const viagem = ref<ViagemResponseDTO | null>(null)
const ordem = ref<OrdemServicoResponseDTO | null>(null)
const checklistAtivos = ref<OrdemServicoChecklistAtivoResponseDTO[]>([])
const loading = ref(true)
const starting = ref(false)
const finishing = ref(false)
const erro = ref('')
const editModalOpen = ref(false)

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

const checklistConferidos = computed(() =>
  checklistAtivos.value.filter(item => item.levado).length
)

const podeEditar = computed(() =>
  viagem.value?.status === 'ABERTA' || viagem.value?.status === 'EM_ANDAMENTO'
)

const formatDateTime = (dt?: string | null) => {
  if (!dt) return '-'
  const d = new Date(dt)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

async function iniciarViagem() {
  if (!viagem.value) return
  starting.value = true
  erro.value = ''
  try {
    viagem.value = await viagemService.atualizar(viagem.value.codigo, {
      codigoTipoViagem: viagem.value.codigoTipoViagem,
      codigoCliente: viagem.value.codigoCliente,
      codigoFuncionarioResponsavel: viagem.value.codigoFuncionarioResponsavel,
      codigoOrdemServico: viagem.value.codigoOrdemServico,
      status: 'EM_ANDAMENTO',
      origem: viagem.value.origem ?? undefined,
      destino: viagem.value.destino ?? undefined,
      kmPrevisto: viagem.value.kmPrevisto ?? undefined,
      dataSaidaPrevista: viagem.value.dataSaidaPrevista ?? undefined,
      dataRetornoPrevisto: viagem.value.dataRetornoPrevisto ?? undefined,
      observacao: viagem.value.observacao ?? undefined,
    })
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao iniciar viagem.'
  } finally {
    starting.value = false
  }
}

async function finalizarViagem() {
  if (!viagem.value) return
  finishing.value = true
  erro.value = ''
  try {
    viagem.value = await viagemService.atualizar(viagem.value.codigo, {
      codigoTipoViagem: viagem.value.codigoTipoViagem,
      codigoCliente: viagem.value.codigoCliente,
      codigoFuncionarioResponsavel: viagem.value.codigoFuncionarioResponsavel,
      codigoOrdemServico: viagem.value.codigoOrdemServico,
      status: 'FINALIZADA',
      origem: viagem.value.origem ?? undefined,
      destino: viagem.value.destino ?? undefined,
      kmPrevisto: viagem.value.kmPrevisto ?? undefined,
      dataSaidaPrevista: viagem.value.dataSaidaPrevista ?? undefined,
      dataRetornoPrevisto: viagem.value.dataRetornoPrevisto ?? undefined,
      observacao: viagem.value.observacao ?? undefined,
    })
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao finalizar preparação.'
  } finally {
    finishing.value = false
  }
}

function onViagemAtualizada(atualizada: ViagemResponseDTO) {
  viagem.value = atualizada
}

async function carregar() {
  loading.value = true
  erro.value = ''
  try {
    viagem.value = await viagemService.buscarPorId(viagemId)
    if (viagem.value.codigoOrdemServico) {
      await Promise.all([
        ordemServicoService.buscarPorId(viagem.value.codigoOrdemServico)
          .then(r => { ordem.value = r }).catch(() => {}),
        ordemServicoService.listarChecklistAtivos(viagem.value.codigoOrdemServico)
          .then(r => { checklistAtivos.value = r }).catch(() => {}),
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
    <!-- Breadcrumb -->
    <div class="flex items-center gap-2 text-sm text-muted-foreground">
      <button
        class="flex items-center gap-1.5 hover:text-foreground transition-colors cursor-pointer"
        @click="router.push('/viagem-preparacao')"
      >
        <ArrowLeft class="size-4" />
        Preparação de Viagem
      </button>
      <ChevronRight class="size-3.5" />
      <span class="text-foreground font-medium">
        {{ viagem ? `Viagem #${viagem.codigo}` : 'Carregando...' }}
      </span>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-24 text-muted-foreground gap-3">
      <Loader2 class="size-5 animate-spin" /> Carregando preparação de viagem...
    </div>

    <div v-else-if="erro && !viagem" class="py-24 text-center text-red-400">{{ erro }}</div>

    <template v-else-if="viagem">
      <!-- Header -->
      <div class="rounded-xl border border-border bg-sidebar p-6 flex flex-col lg:flex-row items-start lg:items-center gap-5">
        <div class="flex items-center justify-center size-16 rounded-xl bg-cyan-500/10 border border-cyan-500/20 shrink-0">
          <Route class="size-8 text-cyan-400" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center flex-wrap gap-3">
            <h1 class="text-2xl font-bold text-foreground">Preparação de Viagem #{{ viagem.codigo }}</h1>
            <span
              :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', statusConfig[viagem.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
            >
              <span :class="['size-1.5 rounded-full', statusConfig[viagem.status]?.dot ?? 'bg-slate-400']" />
              {{ statusConfig[viagem.status]?.label ?? viagem.status }}
            </span>
          </div>
          <p class="text-sm text-muted-foreground mt-1">
            {{ viagem.nomeCliente ?? `Cliente #${viagem.codigoCliente}` }}
            <span v-if="viagem.descricaoTipoViagem"> — {{ viagem.descricaoTipoViagem }}</span>
          </p>
          <p v-if="viagem.nomeFuncionarioResponsavel" class="text-xs text-muted-foreground mt-0.5">
            Técnico: {{ viagem.nomeFuncionarioResponsavel }}
          </p>
        </div>
        <div class="flex items-center gap-2 shrink-0 flex-wrap">
          <Button
            v-if="podeEditar"
            variant="outline"
            @click="editModalOpen = true"
          >
            <Pencil class="size-4 mr-2" />
            Editar
          </Button>
          <Button
            v-if="viagem.status === 'ABERTA'"
            class="bg-blue-600 hover:bg-blue-700 text-white border-none"
            :disabled="starting"
            @click="iniciarViagem"
          >
            <Loader2 v-if="starting" class="size-4 mr-2 animate-spin" />
            <Truck v-else class="size-4 mr-2" />
            Iniciar Viagem
          </Button>
          <Button
            v-if="viagem.status === 'ABERTA' || viagem.status === 'EM_ANDAMENTO'"
            class="bg-emerald-600 hover:bg-emerald-700 text-white border-none"
            :disabled="finishing"
            @click="finalizarViagem"
          >
            <Loader2 v-if="finishing" class="size-4 mr-2 animate-spin" />
            Finalizar
          </Button>
        </div>
      </div>

      <div v-if="erro" class="rounded-md border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
        {{ erro }}
      </div>

      <!-- Main content -->
      <div class="grid grid-cols-1 xl:grid-cols-[minmax(0,1fr)_300px] gap-6">
        <!-- Left: details -->
        <div class="space-y-5">

          <!-- Identificação -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                Identificação
              </CardTitle>
            </CardHeader>
            <CardContent class="p-5">
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Cliente</p>
                  <p class="text-sm font-semibold text-foreground">{{ viagem.nomeCliente ?? `#${viagem.codigoCliente}` }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Técnico</p>
                  <p class="text-sm font-semibold text-foreground">{{ viagem.nomeFuncionarioResponsavel ?? 'Não atribuído' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Ordem de Serviço</p>
                  <p class="text-sm font-semibold text-foreground font-mono">{{ viagem.codigoOrdemServico ? `#${viagem.codigoOrdemServico}` : '-' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Tipo de Viagem</p>
                  <p class="text-sm font-semibold text-foreground">{{ viagem.descricaoTipoViagem ?? '-' }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Rota -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Truck class="size-4 text-cyan-400" /> Deslocamento
              </CardTitle>
            </CardHeader>
            <CardContent class="p-5">
              <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Origem</p>
                  <p class="text-sm text-foreground">{{ viagem.origem ?? '-' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Destino</p>
                  <p class="text-sm text-foreground">{{ viagem.destino ?? '-' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Km Previsto</p>
                  <p class="text-sm text-foreground">{{ viagem.kmPrevisto ? `${viagem.kmPrevisto} km` : '-' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Saída Prevista</p>
                  <p class="text-sm text-foreground">{{ formatDateTime(viagem.dataSaidaPrevista) }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Retorno Previsto</p>
                  <p class="text-sm text-foreground">{{ formatDateTime(viagem.dataRetornoPrevisto) }}</p>
                </div>
                <div v-if="viagem.kmReal" class="space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Km Real</p>
                  <p class="text-sm text-foreground">{{ viagem.kmReal }} km</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Paradas -->
          <Card v-if="viagem.paradas?.length" class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <MapPinned class="size-4 text-cyan-400" /> Paradas ({{ viagem.paradas.length }})
              </CardTitle>
            </CardHeader>
            <CardContent class="p-0">
              <div class="divide-y divide-border">
                <div
                  v-for="parada in [...viagem.paradas].sort((a, b) => a.ordem - b.ordem)"
                  :key="parada.ordem"
                  class="p-5"
                >
                  <div class="flex items-center gap-3 mb-3">
                    <div class="flex size-7 items-center justify-center rounded-full border border-cyan-500/30 bg-cyan-500/10 text-xs font-bold text-cyan-300">
                      {{ parada.ordem }}
                    </div>
                    <p class="text-sm font-semibold text-foreground">
                      {{ parada.descricaoLocal || `Parada ${parada.ordem}` }}
                    </p>
                  </div>
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-3 text-sm">
                    <div v-if="parada.endereco" class="space-y-0.5">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Endereço</p>
                      <p class="text-foreground">{{ parada.endereco }}</p>
                    </div>
                    <div v-if="parada.cidade" class="space-y-0.5">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Cidade</p>
                      <p class="text-foreground">{{ parada.cidade }}</p>
                    </div>
                    <div v-if="parada.estadoRegiao" class="space-y-0.5">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Estado</p>
                      <p class="text-foreground">{{ parada.estadoRegiao }}</p>
                    </div>
                    <div v-if="parada.dataChegadaPrevista" class="space-y-0.5">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Chegada prevista</p>
                      <p class="text-foreground">{{ formatDateTime(parada.dataChegadaPrevista) }}</p>
                    </div>
                  </div>
                  <p v-if="parada.observacao" class="mt-2 text-xs text-muted-foreground">{{ parada.observacao }}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Right sidebar -->
        <div class="space-y-5">
          <Card class="bg-sidebar border-border">
            <CardContent class="p-5 space-y-4">
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Saída prevista</p>
                <p class="text-sm text-foreground mt-1">{{ formatDateTime(viagem.dataSaidaPrevista) }}</p>
              </div>
              <div class="h-px bg-border" />
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Retorno previsto</p>
                <p class="text-sm text-foreground mt-1">{{ formatDateTime(viagem.dataRetornoPrevisto) }}</p>
              </div>
              <div class="h-px bg-border" />
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
                  {{ viagem.descricaoTipoViagem ?? 'Tipo não informado' }}
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Boxes class="size-4 text-emerald-400" /> Equipamentos técnicos
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
                      {{ [item.marca, item.modelo, item.numeroSerie].filter(Boolean).join(' - ') || 'Detalhes não informados' }}
                    </p>
                  </div>
                </div>
              </div>
              <div v-else class="p-5 text-sm text-muted-foreground">
                Nenhum equipamento vinculado à ordem.
              </div>
            </CardContent>
          </Card>

          <Card v-if="viagem.observacao || ordem?.observacaoGeral" class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <FileText class="size-4 text-blue-400" /> Observações
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

    <!-- Edit modal -->
    <ViagemPreparacaoEdicaoModal
      v-if="viagem"
      :open="editModalOpen"
      :codigoViagem="viagem.codigo"
      @close="editModalOpen = false"
      @updated="onViagemAtualizada"
    />
  </div>
</template>
