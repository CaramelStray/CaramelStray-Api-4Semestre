<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft, ClipboardList, Building2, Calendar, MapPin, Phone, Mail,
  Cpu, Wifi, WifiOff, AlertTriangle, MessageSquare, ChevronDown, ChevronUp,
  Loader2, CheckCircle2, XCircle, Circle, Package, PackageCheck, RotateCcw,
  Wrench, Layers,
} from 'lucide-vue-next'
import {
  ordemServicoService,
  type TecnicoOrdemDetalhesResponseDTO,
  type MaquinaChecklistManutencaoResponseDTO,
} from '@/services/ordemServicoService'

const route   = useRoute()
const router  = useRouter()
const ordemId = Number(route.params.id)

// ── Estado ────────────────────────────────────────────────────────────────
const ordem    = ref<TecnicoOrdemDetalhesResponseDTO | null>(null)
const loading  = ref(true)
const erro     = ref('')

type AtivoLocal = NonNullable<TecnicoOrdemDetalhesResponseDTO['checklistAtivos']>[number]
const checklistAtivos  = ref<AtivoLocal[]>([])
const checklistMaquina = ref<MaquinaChecklistManutencaoResponseDTO[]>([])

const secaoLevarAberta    = ref(true)
const secaoManutAberta    = ref(true)
const secaoDevolverAberta = ref(true)

const salvandoLevar    = ref<Record<number, boolean>>({})
const salvandoDevolver = ref<Record<number, boolean>>({})
const salvandoManut    = ref<Record<number, boolean>>({})
const obsAberto        = ref<Record<number, boolean>>({})
const obsInputs        = ref<Record<number, string>>({})

// Erros de ação inline
const erroLevar    = ref('')
const erroDevolver = ref('')
const erroManut    = ref('')

function limparErroComDelay(refErro: typeof erroLevar) {
  setTimeout(() => { refErro.value = '' }, 5000)
}

// ── Computed ──────────────────────────────────────────────────────────────
const levarTotal     = computed(() => checklistAtivos.value.length)
const levarFeitos    = computed(() => checklistAtivos.value.filter(a => a.levado).length)
const levarConcluido = computed(() => levarTotal.value > 0 && levarFeitos.value === levarTotal.value)

const manutTotal     = computed(() => checklistMaquina.value.length)
const manutFeitos    = computed(() => checklistMaquina.value.filter(i => i.realizado !== null && i.realizado !== undefined).length)
const manutConcluida = computed(() => manutTotal.value > 0 && manutFeitos.value === manutTotal.value)

const ativosLevados     = computed(() => checklistAtivos.value.filter(a => a.levado))
const devolverTotal     = computed(() => ativosLevados.value.length)
const devolverFeitos    = computed(() => ativosLevados.value.filter(a => a.devolvido).length)
const devolverConcluida = computed(() => devolverTotal.value > 0 && devolverFeitos.value === devolverTotal.value)

// Detecta se a ordem está em estado que bloqueia edição
const STATUS_BLOQUEADOS = ['FINALIZADA', 'FINALIZADO', 'CANCELADA', 'CANCELADO', 'CONCLUIDA']
const ordemBloqueada = computed(() => STATUS_BLOQUEADOS.includes(ordem.value?.status ?? ''))

watch(levarConcluido,    (v) => { if (v) secaoLevarAberta.value = false })
watch(manutConcluida,    (v) => { if (v) secaoManutAberta.value = false })
watch(devolverConcluida, (v) => { if (v) secaoDevolverAberta.value = false })

// ── Visual config ─────────────────────────────────────────────────────────
const statusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  AGUARDANDO:  { dot: 'bg-amber-500',   badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',       label: 'Aguardando'  },
  AGENDADO:    { dot: 'bg-blue-400',    badge: 'bg-blue-400/15 text-blue-400 border-blue-400/30',          label: 'Agendado'    },
  EM_EXECUCAO: { dot: 'bg-indigo-500',  badge: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/30',    label: 'Em Execução' },
  CONCLUIDA:   { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Concluída'   },
  FINALIZADA:  { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Finalizada'  },
  FINALIZADO:  { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Finalizado'  },
  CANCELADA:   { dot: 'bg-red-500',     badge: 'bg-red-500/15 text-red-400 border-red-500/30',             label: 'Cancelada'   },
  CANCELADO:   { dot: 'bg-red-500',     badge: 'bg-red-500/15 text-red-400 border-red-500/30',             label: 'Cancelado'   },
}
const criticidadeConfig: Record<string, string> = {
  CRITICA: 'bg-red-500/15 text-red-400 border-red-500/30',
  ALTA:    'bg-orange-500/15 text-orange-400 border-orange-500/30',
  MEDIA:   'bg-amber-500/15 text-amber-400 border-amber-500/30',
  BAIXA:   'bg-blue-500/15 text-blue-400 border-blue-500/30',
}

// ── Formatação ────────────────────────────────────────────────────────────
function fmt(dt?: string | null) {
  if (!dt) return null
  const d = new Date(dt)
  return isNaN(d.getTime()) ? null : d.toLocaleString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}

// ── Ações: Levar ──────────────────────────────────────────────────────────
async function marcarLevado(item: AtivoLocal) {
  if (item.levado || salvandoLevar.value[item.codigo]) return
  salvandoLevar.value[item.codigo] = true
  erroLevar.value = ''
  try {
    const atualizado = await ordemServicoService.marcarAtivoLevado(ordemId, item.codigo)
    const idx = checklistAtivos.value.findIndex(a => a.codigo === item.codigo)
    if (idx !== -1 && atualizado) {
      checklistAtivos.value[idx] = { ...checklistAtivos.value[idx], levado: atualizado.levado, devolvido: atualizado.devolvido }
    }
  } catch(e: any) {
    const msg = e?.response?.data?.message ?? e?.message ?? 'Erro ao marcar ativo como levado.'
    erroLevar.value = msg
    limparErroComDelay(erroLevar)
  } finally {
    salvandoLevar.value[item.codigo] = false
  }
}

// ── Ações: Devolver ───────────────────────────────────────────────────────
async function marcarDevolvido(item: AtivoLocal) {
  if (item.devolvido || salvandoDevolver.value[item.codigo]) return
  salvandoDevolver.value[item.codigo] = true
  erroDevolver.value = ''
  try {
    const atualizado = await ordemServicoService.marcarAtivoDevolvido(ordemId, item.codigo)
    const idx = checklistAtivos.value.findIndex(a => a.codigo === item.codigo)
    if (idx !== -1 && atualizado) {
      checklistAtivos.value[idx] = { ...checklistAtivos.value[idx], levado: atualizado.levado, devolvido: atualizado.devolvido }
    }
  } catch(e: any) {
    const msg = e?.response?.data?.message ?? e?.message ?? 'Erro ao marcar ativo como devolvido.'
    erroDevolver.value = msg
    limparErroComDelay(erroDevolver)
  } finally {
    salvandoDevolver.value[item.codigo] = false
  }
}

// ── Ações: Manutenção ─────────────────────────────────────────────────────
async function toggleRealizado(item: MaquinaChecklistManutencaoResponseDTO) {
  if (!item.codigoHistoricoManutencao || salvandoManut.value[item.codigo]) return
  if (item.realizado === false) return
  if (!item.realizado) {
    await salvarManut(item, true, undefined)
  } else {
    obsAberto.value[item.codigo] = true
  }
}

async function salvarManut(item: MaquinaChecklistManutencaoResponseDTO, realizado: boolean, obs: string | undefined) {
  if (!item.codigoHistoricoManutencao) return
  salvandoManut.value[item.codigo] = true
  erroManut.value = ''
  try {
    const atualizado = await ordemServicoService.atualizarChecklistMaquinaItem(
      item.codigoHistoricoManutencao, item.codigo, { realizado, observacaoTarefa: obs },
    )
    if (atualizado) {
      const idx = checklistMaquina.value.findIndex(i => i.codigo === item.codigo)
      if (idx !== -1) checklistMaquina.value[idx] = atualizado
    }
  } catch(e: any) {
    const msg = e?.response?.data?.message ?? e?.message ?? 'Erro ao atualizar item de manutenção.'
    erroManut.value = msg
    limparErroComDelay(erroManut)
  } finally {
    salvandoManut.value[item.codigo] = false
  }
}

async function confirmarNaoRealizado(item: MaquinaChecklistManutencaoResponseDTO) {
  const obs = (obsInputs.value[item.codigo] ?? '').trim()
  if (!obs) return
  await salvarManut(item, false, obs)
  obsAberto.value[item.codigo] = false
  obsInputs.value[item.codigo] = ''
}

function cancelarObs(codigo: number) {
  obsAberto.value[codigo] = false
  obsInputs.value[codigo] = ''
}

// ── Carregamento ──────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    const data = await ordemServicoService.tecnicoOrdemPorId(ordemId)
    ordem.value = data
    checklistAtivos.value  = data?.checklistAtivos  ?? []
    checklistMaquina.value = data?.checklistMaquina ?? []
  } catch {
    erro.value = 'Ordem não encontrada ou sem permissão de acesso.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="p-4 sm:p-6 pb-12 max-w-[1400px] mx-auto">

    <!-- Voltar -->
    <button
      class="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors mb-5"
      @click="router.back()"
    >
      <ArrowLeft class="size-4" /> Voltar
    </button>

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-24 gap-2 text-muted-foreground">
      <Loader2 class="size-5 animate-spin" />
      <span class="text-sm">Carregando...</span>
    </div>

    <!-- Erro -->
    <div v-else-if="erro" class="flex flex-col items-center gap-3 py-20 text-muted-foreground">
      <AlertTriangle class="size-10 opacity-40" />
      <p class="text-sm text-center">{{ erro }}</p>
    </div>

    <template v-else-if="ordem">

      <!-- Aviso quando ordem está bloqueada para edição -->
      <div
        v-if="ordemBloqueada"
        class="mb-5 flex items-center gap-2.5 px-4 py-3 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm"
      >
        <AlertTriangle class="size-4 shrink-0" />
        Esta ordem está <strong>{{ statusConfig[ordem.status ?? '']?.label ?? ordem.status }}</strong> e não pode ser alterada.
      </div>

      <!-- ═══════════════════════════════════════════════════════════
           LINHA SUPERIOR: 3 cards de informação
      ═══════════════════════════════════════════════════════════ -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-5">

        <!-- Card: Dados da Ordem -->
        <div class="rounded-xl border border-border bg-sidebar overflow-hidden">
          <div class="flex items-center gap-2 px-4 py-3 border-b border-border">
            <ClipboardList class="size-3.5 text-muted-foreground" />
            <span class="text-[11px] font-bold uppercase tracking-wider text-muted-foreground">Dados da Ordem</span>
          </div>
          <div class="p-4 space-y-3">
            <!-- Código + badges -->
            <div class="flex items-start justify-between gap-2">
              <h1 class="text-2xl font-bold font-mono text-foreground">#{{ ordem.codigo }}</h1>
              <div class="flex flex-col items-end gap-1.5 shrink-0">
                <span
                  v-if="ordem.status"
                  :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[10px] font-bold uppercase tracking-wide', statusConfig[ordem.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                >
                  <span :class="['size-1.5 rounded-full', statusConfig[ordem.status]?.dot ?? 'bg-slate-500']" />
                  {{ statusConfig[ordem.status]?.label ?? ordem.status }}
                </span>
                <span
                  v-if="ordem.criticidade"
                  :class="['inline-flex px-2.5 py-1 rounded-full border text-[10px] font-bold uppercase tracking-wide', criticidadeConfig[ordem.criticidade] ?? 'bg-muted/30 text-muted-foreground border-border']"
                >{{ ordem.criticidade }}</span>
              </div>
            </div>

            <!-- Agendamento -->
            <div class="flex items-center gap-2 pt-2 border-t border-border">
              <Calendar class="size-3.5 text-blue-400 shrink-0" />
              <span class="text-[11px] text-muted-foreground">Agendado:</span>
              <span class="text-xs font-semibold text-foreground">{{ fmt(ordem.dataAgendamento) ?? 'Não definido' }}</span>
            </div>

            <!-- Tipo -->
            <div v-if="ordem.tipoOrdem" class="flex items-center gap-2">
              <Layers class="size-3.5 text-muted-foreground shrink-0" />
              <span class="text-xs text-foreground">{{ ordem.tipoOrdem.replace(/_/g, ' ') }}</span>
            </div>

            <!-- Execução -->
            <div v-if="ordem.dataInicioExecucao || ordem.dataFimExecucao" class="space-y-0.5 pt-2 border-t border-border">
              <p v-if="ordem.dataInicioExecucao" class="text-xs text-foreground">
                <span class="text-muted-foreground">Início: </span>{{ fmt(ordem.dataInicioExecucao) }}
              </p>
              <p v-if="ordem.dataFimExecucao" class="text-xs text-foreground">
                <span class="text-muted-foreground">Fim: </span>{{ fmt(ordem.dataFimExecucao) }}
              </p>
            </div>

            <!-- Alertas -->
            <div
              v-if="ordem.trabalhoEmAltura || ordem.internacionalCliente || ordem.conexaoInternet === false || ordem.conexaoInternet === true"
              class="flex flex-wrap gap-1.5 pt-2 border-t border-border"
            >
              <span v-if="ordem.trabalhoEmAltura" class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-amber-500/15 border border-amber-500/30 text-amber-400 text-[10px] font-semibold">
                <AlertTriangle class="size-2.5" /> Em Altura
              </span>
              <span v-if="ordem.internacionalCliente" class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-blue-500/15 border border-blue-500/30 text-blue-400 text-[10px] font-semibold">
                <MapPin class="size-2.5" /> Internacional
              </span>
              <span v-if="ordem.conexaoInternet === false" class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-slate-500/15 border border-slate-500/30 text-slate-400 text-[10px] font-semibold">
                <WifiOff class="size-2.5" /> Sem Internet
              </span>
              <span v-if="ordem.conexaoInternet === true" class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-emerald-500/15 border border-emerald-500/30 text-emerald-400 text-[10px] font-semibold">
                <Wifi class="size-2.5" /> Internet
              </span>
            </div>
          </div>
        </div>

        <!-- Card: Cliente -->
        <div class="rounded-xl border border-border bg-sidebar overflow-hidden">
          <div class="flex items-center gap-2 px-4 py-3 border-b border-border">
            <Building2 class="size-3.5 text-muted-foreground" />
            <span class="text-[11px] font-bold uppercase tracking-wider text-muted-foreground">Cliente</span>
          </div>
          <div class="p-4 space-y-3">
            <!-- Avatar + nome -->
            <div class="flex items-center gap-3">
              <div class="size-10 rounded-full bg-blue-500/15 border border-blue-500/30 flex items-center justify-center shrink-0">
                <span class="text-sm font-bold text-blue-400 select-none">
                  {{ (ordem.nomeCliente ?? '?').slice(0, 2).toUpperCase() }}
                </span>
              </div>
              <div class="min-w-0">
                <p class="text-sm font-semibold text-foreground truncate">{{ ordem.nomeCliente ?? '—' }}</p>
                <p v-if="ordem.nomeResponsavelCliente" class="text-xs text-muted-foreground truncate">{{ ordem.nomeResponsavelCliente }}</p>
              </div>
            </div>
            <!-- Contato -->
            <div v-if="ordem.emailCliente" class="flex items-center gap-2">
              <Mail class="size-3.5 text-muted-foreground shrink-0" />
              <span class="text-xs text-foreground truncate">{{ ordem.emailCliente }}</span>
            </div>
            <div v-if="ordem.telefoneCliente" class="flex items-center gap-2">
              <Phone class="size-3.5 text-muted-foreground shrink-0" />
              <span class="text-xs text-foreground">{{ ordem.telefoneCliente }}</span>
            </div>
            <!-- Localização -->
            <div v-if="ordem.cidadeCliente" class="flex items-start gap-2 pt-2 border-t border-border">
              <MapPin class="size-3.5 text-muted-foreground shrink-0 mt-0.5" />
              <span class="text-xs text-muted-foreground leading-relaxed">
                {{ [ordem.cidadeCliente, ordem.estadoRegiaoCliente, ordem.paisCliente].filter(Boolean).join(', ') }}
                <span v-if="ordem.fusoHorarioCliente"> &middot; {{ ordem.fusoHorarioCliente }}</span>
              </span>
            </div>
          </div>
        </div>

        <!-- Card: Máquina -->
        <div class="rounded-xl border border-border bg-sidebar overflow-hidden sm:col-span-2 lg:col-span-1">
          <div class="flex items-center gap-2 px-4 py-3 border-b border-border">
            <Cpu class="size-3.5 text-muted-foreground" />
            <span class="text-[11px] font-bold uppercase tracking-wider text-muted-foreground">Máquina</span>
          </div>
          <div class="p-4 space-y-2">
            <p class="text-sm font-semibold text-foreground">{{ ordem.descricaoMaquina ?? 'Não especificada' }}</p>
            <p v-if="ordem.numeroSerieMaquina" class="text-xs font-mono text-muted-foreground">S/N: {{ ordem.numeroSerieMaquina }}</p>
            <p v-if="ordem.manutencaoFeitaMaquina" class="text-xs text-muted-foreground">Última manutenção: {{ ordem.manutencaoFeitaMaquina }}</p>
            <p v-if="ordem.limiteManutencaoMaquina" class="text-xs text-muted-foreground">Limite: {{ ordem.limiteManutencaoMaquina }} manutenções</p>
            <p
              v-if="ordem.especificacaoMaquina"
              class="text-xs text-muted-foreground leading-relaxed pt-2 mt-1 border-t border-border"
            >{{ ordem.especificacaoMaquina }}</p>
          </div>
        </div>

      </div>
      <!-- /LINHA SUPERIOR -->

      <!-- ═══════════════════════════════════════════════════════════
           LINHA INFERIOR: Observações (esq) + Checklists (dir)
      ═══════════════════════════════════════════════════════════ -->
      <div class="grid grid-cols-1 lg:grid-cols-[minmax(0,1fr)_minmax(0,2fr)] gap-6 items-start">

        <!-- Checklists: primeiro no DOM (mobile), direita no desktop -->
        <div class="order-1 lg:order-2 space-y-4">

          <!-- ── SEÇÃO A: Levar Ativos ── -->
          <div
            v-if="checklistAtivos.length > 0"
            class="rounded-xl border overflow-hidden transition-colors duration-300"
            :class="levarConcluido ? 'border-emerald-500/40 bg-emerald-500/5' : 'border-border bg-sidebar'"
          >
            <button
              class="w-full flex items-center justify-between px-4 py-3 text-left hover:bg-muted/10 transition-colors"
              @click="secaoLevarAberta = !secaoLevarAberta"
            >
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-8 rounded-lg border shrink-0', levarConcluido ? 'border-emerald-500/40 bg-emerald-500/15' : 'border-blue-500/30 bg-blue-500/10']">
                  <Package :class="['size-3.5', levarConcluido ? 'text-emerald-400' : 'text-blue-400']" />
                </div>
                <div>
                  <p :class="['text-sm font-semibold flex items-center gap-1.5', levarConcluido ? 'text-emerald-400' : 'text-foreground']">
                    Ativos a Levar
                    <CheckCircle2 v-if="levarConcluido" class="size-3.5" />
                  </p>
                  <p class="text-xs text-muted-foreground">{{ levarFeitos }}/{{ levarTotal }} confirmados</p>
                </div>
              </div>
              <div class="flex items-center gap-2 shrink-0">
                <span v-if="levarConcluido" class="text-[10px] font-bold text-emerald-400 bg-emerald-500/15 border border-emerald-500/30 px-2 py-0.5 rounded-full">Concluído</span>
                <span v-else-if="levarTotal > 0" class="text-[10px] font-bold text-blue-400 bg-blue-500/15 border border-blue-500/30 px-2 py-0.5 rounded-full">{{ Math.round((levarFeitos / levarTotal) * 100) }}%</span>
                <ChevronDown v-if="!secaoLevarAberta" :class="['size-4', levarConcluido ? 'text-emerald-400' : 'text-muted-foreground']" />
                <ChevronUp   v-else                   :class="['size-4', levarConcluido ? 'text-emerald-400' : 'text-muted-foreground']" />
              </div>
            </button>

            <!-- Erro de ação -->
            <div v-if="erroLevar && secaoLevarAberta" class="mx-4 mb-2 px-3 py-2 rounded-lg bg-red-500/15 border border-red-500/30 text-red-400 text-xs flex items-center gap-2">
              <XCircle class="size-3.5 shrink-0" /> {{ erroLevar }}
            </div>

            <div
              v-if="secaoLevarAberta"
              class="border-t divide-y"
              :class="levarConcluido ? 'border-emerald-500/20 divide-emerald-500/10' : 'border-border divide-border'"
            >
              <div
                v-for="item in checklistAtivos"
                :key="item.codigo"
                class="flex items-center gap-3 px-4 py-3 transition-colors"
                :class="[
                  !item.levado && !ordemBloqueada ? 'cursor-pointer hover:bg-muted/10' : 'cursor-default',
                ]"
                @click="marcarLevado(item)"
              >
                <div class="shrink-0 flex items-center justify-center pointer-events-none">
                  <Loader2 v-if="salvandoLevar[item.codigo]" class="size-4 animate-spin text-muted-foreground" />
                  <input
                    v-else
                    type="checkbox"
                    :checked="item.levado"
                    :disabled="item.levado || ordemBloqueada"
                    class="size-4 accent-blue-500"
                  />
                </div>
                <div class="flex-1 min-w-0 pointer-events-none">
                  <p :class="['text-sm font-medium truncate', item.levado ? 'text-muted-foreground line-through' : 'text-foreground']">
                    {{ item.descricaoProduto ?? item.descricaoAtivo ?? `Ativo #${item.codigo}` }}
                  </p>
                  <p class="text-xs text-muted-foreground">
                    <span v-if="item.marca">{{ item.marca }}</span>
                    <span v-if="item.modelo"> &middot; {{ item.modelo }}</span>
                    <span v-if="item.numeroSerie" class="font-mono"> &middot; S/N {{ item.numeroSerie }}</span>
                  </p>
                </div>
                <span v-if="item.levado" class="shrink-0 text-xs font-semibold text-blue-400 pointer-events-none">levando</span>
                <span v-else class="shrink-0 text-xs font-semibold text-amber-400 pointer-events-none">falta levar</span>
              </div>
            </div>
          </div>

          <!-- ── SEÇÃO B: Checklist de Manutenção ── -->
          <div
            v-if="checklistMaquina.length > 0"
            class="rounded-xl border overflow-hidden transition-colors duration-300"
            :class="manutConcluida ? 'border-emerald-500/40 bg-emerald-500/5' : 'border-border bg-sidebar'"
          >
            <button
              class="w-full flex items-center justify-between px-4 py-3 text-left hover:bg-muted/10 transition-colors"
              @click="secaoManutAberta = !secaoManutAberta"
            >
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-8 rounded-lg border shrink-0', manutConcluida ? 'border-emerald-500/40 bg-emerald-500/15' : 'border-purple-500/30 bg-purple-500/10']">
                  <Wrench :class="['size-3.5', manutConcluida ? 'text-emerald-400' : 'text-purple-400']" />
                </div>
                <div>
                  <p :class="['text-sm font-semibold flex items-center gap-1.5', manutConcluida ? 'text-emerald-400' : 'text-foreground']">
                    Checklist de Manutenção
                    <CheckCircle2 v-if="manutConcluida" class="size-3.5" />
                  </p>
                  <p class="text-xs text-muted-foreground">{{ manutFeitos }}/{{ manutTotal }} concluídas</p>
                </div>
              </div>
              <div class="flex items-center gap-2 shrink-0">
                <span v-if="manutConcluida" class="text-[10px] font-bold text-emerald-400 bg-emerald-500/15 border border-emerald-500/30 px-2 py-0.5 rounded-full">Concluído</span>
                <span v-else-if="manutTotal > 0" class="text-[10px] font-bold text-purple-400 bg-purple-500/15 border border-purple-500/30 px-2 py-0.5 rounded-full">{{ Math.round((manutFeitos / manutTotal) * 100) }}%</span>
                <ChevronDown v-if="!secaoManutAberta" :class="['size-4', manutConcluida ? 'text-emerald-400' : 'text-muted-foreground']" />
                <ChevronUp   v-else                   :class="['size-4', manutConcluida ? 'text-emerald-400' : 'text-muted-foreground']" />
              </div>
            </button>

            <!-- Erro de ação -->
            <div v-if="erroManut && secaoManutAberta" class="mx-4 mb-2 px-3 py-2 rounded-lg bg-red-500/15 border border-red-500/30 text-red-400 text-xs flex items-center gap-2">
              <XCircle class="size-3.5 shrink-0" /> {{ erroManut }}
            </div>

            <div
              v-if="secaoManutAberta"
              class="border-t divide-y"
              :class="manutConcluida ? 'border-emerald-500/20 divide-emerald-500/10' : 'border-border divide-border'"
            >
              <div
                v-for="item in checklistMaquina"
                :key="item.codigo"
                class="px-4 py-3 space-y-2"
              >
                <div class="flex items-center gap-3">
                  <button
                    class="shrink-0 disabled:opacity-40 disabled:cursor-not-allowed"
                    :disabled="!!salvandoManut[item.codigo] || item.realizado === false || ordemBloqueada"
                    @click="toggleRealizado(item)"
                  >
                    <Loader2      v-if="salvandoManut[item.codigo]"   class="size-5 animate-spin text-muted-foreground" />
                    <CheckCircle2 v-else-if="item.realizado === true"  class="size-5 text-emerald-400" />
                    <XCircle      v-else-if="item.realizado === false" class="size-5 text-red-400" />
                    <Circle       v-else                               class="size-5 text-muted-foreground/40 hover:text-muted-foreground transition-colors" />
                  </button>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 flex-wrap">
                      <p :class="['text-sm font-medium', item.realizado === true ? 'text-muted-foreground line-through' : 'text-foreground']">
                        {{ item.descricaoTarefa ?? 'Tarefa sem descrição' }}
                      </p>
                      <span v-if="item.categoriaTarefa" class="text-[10px] px-1.5 py-0.5 rounded bg-muted/30 border border-border text-muted-foreground font-medium">
                        {{ item.categoriaTarefa }}
                      </span>
                    </div>
                    <p v-if="item.observacaoTarefa" class="text-xs text-amber-400/80 italic mt-0.5">"{{ item.observacaoTarefa }}"</p>
                  </div>
                  <button
                    v-if="(item.realizado === null || item.realizado === undefined) && !ordemBloqueada"
                    :disabled="!!salvandoManut[item.codigo]"
                    class="shrink-0 text-xs text-muted-foreground hover:text-red-400 transition-colors border border-border hover:border-red-500/30 px-2 py-1 rounded-lg disabled:opacity-40"
                    @click="obsAberto[item.codigo] = !obsAberto[item.codigo]"
                  >
                    Não feito
                  </button>
                </div>
                <div v-if="obsAberto[item.codigo]" class="pl-8 space-y-2">
                  <textarea
                    v-model="obsInputs[item.codigo]"
                    rows="2"
                    placeholder="Motivo pelo qual não foi realizado (obrigatório)..."
                    class="w-full rounded-md border border-border bg-muted/20 px-3 py-1.5 text-xs text-foreground placeholder:text-muted-foreground resize-none focus:outline-none focus:border-red-500/50"
                  />
                  <div class="flex gap-2">
                    <button
                      class="px-3 py-1 rounded text-xs bg-red-500/20 text-red-400 border border-red-500/30 hover:bg-red-500/30 disabled:opacity-50"
                      :disabled="!obsInputs[item.codigo]?.trim() || !!salvandoManut[item.codigo]"
                      @click="confirmarNaoRealizado(item)"
                    >
                      <Loader2 v-if="salvandoManut[item.codigo]" class="size-3 animate-spin inline mr-1" />
                      Confirmar
                    </button>
                    <button class="px-3 py-1 rounded text-xs text-muted-foreground hover:text-foreground" @click="cancelarObs(item.codigo)">
                      Cancelar
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- ── SEÇÃO C: Devolver Ativos ── -->
          <div
            v-if="ativosLevados.length > 0"
            class="rounded-xl border overflow-hidden transition-colors duration-300"
            :class="devolverConcluida ? 'border-emerald-500/40 bg-emerald-500/5' : 'border-border bg-sidebar'"
          >
            <button
              class="w-full flex items-center justify-between px-4 py-3 text-left hover:bg-muted/10 transition-colors"
              @click="secaoDevolverAberta = !secaoDevolverAberta"
            >
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-8 rounded-lg border shrink-0', devolverConcluida ? 'border-emerald-500/40 bg-emerald-500/15' : 'border-amber-500/30 bg-amber-500/10']">
                  <RotateCcw :class="['size-3.5', devolverConcluida ? 'text-emerald-400' : 'text-amber-400']" />
                </div>
                <div>
                  <p :class="['text-sm font-semibold flex items-center gap-1.5', devolverConcluida ? 'text-emerald-400' : 'text-foreground']">
                    Devolver Ativos
                    <CheckCircle2 v-if="devolverConcluida" class="size-3.5" />
                  </p>
                  <p class="text-xs text-muted-foreground">{{ devolverFeitos }}/{{ devolverTotal }} devolvidos</p>
                </div>
              </div>
              <div class="flex items-center gap-2 shrink-0">
                <span v-if="devolverConcluida" class="text-[10px] font-bold text-emerald-400 bg-emerald-500/15 border border-emerald-500/30 px-2 py-0.5 rounded-full">Concluído</span>
                <span v-else-if="devolverTotal > 0" class="text-[10px] font-bold text-amber-400 bg-amber-500/15 border border-amber-500/30 px-2 py-0.5 rounded-full">{{ Math.round((devolverFeitos / devolverTotal) * 100) }}%</span>
                <ChevronDown v-if="!secaoDevolverAberta" :class="['size-4', devolverConcluida ? 'text-emerald-400' : 'text-muted-foreground']" />
                <ChevronUp   v-else                      :class="['size-4', devolverConcluida ? 'text-emerald-400' : 'text-muted-foreground']" />
              </div>
            </button>

            <!-- Erro de ação -->
            <div v-if="erroDevolver && secaoDevolverAberta" class="mx-4 mb-2 px-3 py-2 rounded-lg bg-red-500/15 border border-red-500/30 text-red-400 text-xs flex items-center gap-2">
              <XCircle class="size-3.5 shrink-0" /> {{ erroDevolver }}
            </div>

            <div
              v-if="secaoDevolverAberta"
              class="border-t divide-y"
              :class="devolverConcluida ? 'border-emerald-500/20 divide-emerald-500/10' : 'border-border divide-border'"
            >
              <div
                v-for="item in ativosLevados"
                :key="item.codigo"
                class="flex items-center gap-3 px-4 py-3 transition-colors"
                :class="[
                  !item.devolvido && !ordemBloqueada ? 'cursor-pointer hover:bg-muted/10' : 'cursor-default',
                ]"
                @click="marcarDevolvido(item)"
              >
                <div class="shrink-0 flex items-center justify-center pointer-events-none">
                  <Loader2 v-if="salvandoDevolver[item.codigo]" class="size-4 animate-spin text-muted-foreground" />
                  <input
                    v-else
                    type="checkbox"
                    :checked="item.devolvido"
                    :disabled="item.devolvido || ordemBloqueada"
                    class="size-4 accent-amber-500"
                  />
                </div>
                <div class="flex-1 min-w-0 pointer-events-none">
                  <p :class="['text-sm font-medium truncate', item.devolvido ? 'text-muted-foreground line-through' : 'text-foreground']">
                    {{ item.descricaoProduto ?? item.descricaoAtivo ?? `Ativo #${item.codigo}` }}
                  </p>
                  <p class="text-xs text-muted-foreground">
                    <span v-if="item.marca">{{ item.marca }}</span>
                    <span v-if="item.modelo"> &middot; {{ item.modelo }}</span>
                    <span v-if="item.numeroSerie" class="font-mono"> &middot; S/N {{ item.numeroSerie }}</span>
                  </p>
                </div>
                <span v-if="item.devolvido" class="shrink-0 text-xs font-semibold text-emerald-400 pointer-events-none">devolvido</span>
                <span v-else class="shrink-0 text-xs font-semibold text-amber-400 pointer-events-none">falta devolver</span>
              </div>
            </div>
          </div>

          <!-- Estado vazio -->
          <div
            v-if="checklistAtivos.length === 0 && checklistMaquina.length === 0"
            class="flex flex-col items-center justify-center py-12 text-muted-foreground/50 border border-dashed border-border rounded-xl"
          >
            <ClipboardList class="size-8 mb-2" />
            <p class="text-sm">Nenhum checklist definido para esta ordem.</p>
          </div>

        </div>
        <!-- /Checklists -->

        <!-- Observações: segunda no DOM (mobile), esquerda no desktop -->
        <div class="order-2 lg:order-1 space-y-4">
          <div v-if="ordem.observacaoGeral" class="rounded-xl border border-border bg-sidebar overflow-hidden">
            <div class="flex items-center gap-2 px-4 py-3 border-b border-border">
              <MessageSquare class="size-3.5 text-muted-foreground" />
              <span class="text-[11px] font-bold uppercase tracking-wider text-muted-foreground">Observações</span>
            </div>
            <div class="p-4">
              <p class="text-sm text-foreground leading-relaxed">{{ ordem.observacaoGeral }}</p>
            </div>
          </div>

          <!-- Placeholder quando sem observações -->
          <div v-else class="rounded-xl border border-dashed border-border p-6 flex flex-col items-center gap-2 text-muted-foreground/40">
            <MessageSquare class="size-6" />
            <p class="text-xs text-center">Sem observações para esta ordem.</p>
          </div>
        </div>

      </div>
      <!-- /LINHA INFERIOR -->

    </template>

  </div>
</template>
