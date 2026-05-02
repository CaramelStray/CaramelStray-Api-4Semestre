<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  ArrowLeft, ClipboardList, User, Building2, FileText, Cpu,
  Package, Calendar, Clock, ChevronRight, Loader2, CheckCircle2,
  AlertTriangle, MapPin, MessageSquare, Wrench, ListChecks,
  Box, XCircle, Circle, History,
} from 'lucide-vue-next'
import {
  ordemServicoService,
  type OrdemServicoResponseDTO,
  type OrdemServicoChecklistAtivoResponseDTO,
  type MaquinaChecklistManutencaoResponseDTO,
} from '@/services/ordemServicoService'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'
import { maquinaContratoService, type MaquinaContratoResponseDTO } from '@/services/maquinaContratoService'
import { catalogoMaquinaService, type CatalogoMaquinaResponseDTO } from '@/services/catalogoMaquinaService'
import { maquinaSoftwareInstaladoService, type MaquinaSoftwareInstaladoResponseDTO } from '@/services/maquinaSoftwareInstaladoService'
import { catalogoSoftwareService, type CatalogoSoftwareResponseDTO } from '@/services/catalogoSoftwareService'

const route  = useRoute()
const router = useRouter()

const ordemId = Number(route.params.id)

// ── Estado ────────────────────────────────────────────────────────────────
const ordem        = ref<OrdemServicoResponseDTO | null>(null)
const cliente      = ref<ClienteResponseDTO | null>(null)
const tecnico      = ref<TecnicoResponseDTO | null>(null)
const contrato     = ref<ContratoResponseDTO | null>(null)
const maquina      = ref<MaquinaContratoResponseDTO | null>(null)
const catMaquina   = ref<CatalogoMaquinaResponseDTO | null>(null)
const software     = ref<MaquinaSoftwareInstaladoResponseDTO | null>(null)
const catSoftware  = ref<CatalogoSoftwareResponseDTO | null>(null)

const checklistAtivos   = ref<OrdemServicoChecklistAtivoResponseDTO[]>([])
const checklistMaquina  = ref<MaquinaChecklistManutencaoResponseDTO[]>([])

const loading           = ref(true)
const loadingChecklists = ref(false)
const erro              = ref('')

// ── Configs ───────────────────────────────────────────────────────────────
const statusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  AGUARDANDO:  { dot: 'bg-amber-500',   badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',       label: 'Aguardando'  },
  AGENDADO:    { dot: 'bg-blue-500',    badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',          label: 'Agendado'    },
  EM_EXECUCAO: { dot: 'bg-indigo-500',  badge: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/30',    label: 'Em Execução' },
  CONCLUIDA:   { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Concluída'   },
  CANCELADA:   { dot: 'bg-red-500',     badge: 'bg-red-500/15 text-red-400 border-red-500/30',             label: 'Cancelada'   },
}

const criticidadeConfig: Record<string, { badge: string }> = {
  CRITICA: { badge: 'bg-red-500/15 text-red-400 border-red-500/30'          },
  ALTA:    { badge: 'bg-orange-500/15 text-orange-400 border-orange-500/30' },
  MEDIA:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30'    },
  BAIXA:   { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30'       },
}

// ── Utilitários ───────────────────────────────────────────────────────────
const formatDate = (dt?: string | null) => {
  if (!dt) return null
  const d = new Date(dt)
  return isNaN(d.getTime()) ? null : d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const formatDateTime = (dt?: string | null) => {
  if (!dt) return null
  const d = new Date(dt)
  return isNaN(d.getTime()) ? null
    : d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const getDuracao = computed(() => {
  if (!ordem.value?.dataInicioExecucao || !ordem.value?.dataFimExecucao) return null
  const diff = new Date(ordem.value.dataFimExecucao).getTime() - new Date(ordem.value.dataInicioExecucao).getTime()
  if (diff <= 0) return null
  const h = Math.floor(diff / 3_600_000)
  const m = Math.floor((diff % 3_600_000) / 60_000)
  return h > 0 ? `${h}h ${m}min` : `${m}min`
})

const timeline = computed(() => {
  if (!ordem.value) return []
  return [
    { label: 'Abertura',        dt: formatDateTime(ordem.value.dataAbertura),        icon: ClipboardList, done: !!ordem.value.dataAbertura        },
    { label: 'Agendamento',     dt: formatDateTime(ordem.value.dataAgendamento),     icon: Calendar,      done: !!ordem.value.dataAgendamento     },
    { label: 'Início Execução', dt: formatDateTime(ordem.value.dataInicioExecucao),  icon: Clock,         done: !!ordem.value.dataInicioExecucao  },
    { label: 'Conclusão',       dt: formatDateTime(ordem.value.dataFimExecucao),     icon: CheckCircle2,  done: !!ordem.value.dataFimExecucao     },
  ]
})

// Progresso checklist manutenção
const checklistMaquinaStats = computed(() => {
  const total      = checklistMaquina.value.length
  const concluidos = checklistMaquina.value.filter(i => i.realizado === true).length
  const naoFeitos  = checklistMaquina.value.filter(i => i.realizado === false).length
  const pendentes  = total - concluidos - naoFeitos
  return { total, concluidos, naoFeitos, pendentes }
})

const checklistAtivosStats = computed(() => {
  const total    = checklistAtivos.value.length
  const levados  = checklistAtivos.value.filter(a => a.levado).length
  const devolvidos = checklistAtivos.value.filter(a => a.devolvido).length
  return { total, levados, devolvidos }
})

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500', 'bg-emerald-500', 'bg-rose-500', 'bg-indigo-500']
  return colors[name.length % colors.length]
}

// ── Carregamento ──────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    ordem.value = await ordemServicoService.buscarPorId(ordemId)
    const o = ordem.value

    await Promise.all([
      clienteService.buscarPorId(o.codigoCliente)
        .then(r => { cliente.value = r }).catch(() => {}),

      o.codigoFuncionario
        ? tecnicoService.buscarPorId(o.codigoFuncionario)
            .then(r => { tecnico.value = r }).catch(() => {})
        : Promise.resolve(),

      contratoService.buscarPorId(o.codigoContrato)
        .then(r => { contrato.value = r }).catch(() => {}),

      maquinaContratoService.buscarPorId(o.codigoMaquinaContrato)
        .then(async r => {
          maquina.value = r
          catMaquina.value = await catalogoMaquinaService.buscarPorId(r.codigoCatalogoMaquina).catch(() => null)
        }).catch(() => {}),

      o.codigoSoftwareInstalado
        ? maquinaSoftwareInstaladoService.buscarPorId(o.codigoSoftwareInstalado)
            .then(async r => {
              software.value = r
              catSoftware.value = await catalogoSoftwareService.buscarPorId(r.codigoSoftware).catch(() => null)
            }).catch(() => {})
        : Promise.resolve(),
    ])
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar ordem'
  } finally {
    loading.value = false
  }

  // Checklists em paralelo após o carregamento principal
  loadingChecklists.value = true
  try {
    const [ativos, maquinaItems] = await Promise.all([
      ordemServicoService.listarChecklistAtivos(ordemId).catch(() => []),
      ordemServicoService.listarChecklistMaquina(ordemId).catch(() => []),
    ])
    checklistAtivos.value  = ativos  ?? []
    checklistMaquina.value = maquinaItems ?? []
  } finally {
    loadingChecklists.value = false
  }
})
</script>

<template>
  <div class="p-6 space-y-6">

    <!-- Breadcrumb -->
    <div class="flex items-center gap-2 text-sm text-muted-foreground">
      <button
        class="flex items-center gap-1.5 hover:text-foreground transition-colors cursor-pointer"
        @click="router.push('/ordens')"
      >
        <ArrowLeft class="size-4" />
        Ordens de Serviço
      </button>
      <ChevronRight class="size-3.5" />
      <span class="text-foreground font-medium">{{ ordem ? `#${ordem.codigo}` : 'Detalhes' }}</span>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="flex items-center justify-center py-24 text-muted-foreground gap-3">
      <Loader2 class="size-5 animate-spin" /> Carregando ordem...
    </div>
    <div v-else-if="erro" class="py-24 text-center text-red-400">{{ erro }}</div>

    <template v-else-if="ordem">

      <!-- Hero -->
      <div class="rounded-xl border border-border bg-sidebar p-6 flex flex-col sm:flex-row items-start sm:items-center gap-5">
        <div class="flex items-center justify-center size-16 rounded-xl bg-indigo-500/10 border border-indigo-500/20 shrink-0">
          <ClipboardList class="size-8 text-indigo-400" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center flex-wrap gap-3">
            <h1 class="text-2xl font-bold font-mono text-foreground">#{{ ordem.codigo }}</h1>
            <span
              :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', statusConfig[ordem.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
            >
              <span :class="['size-1.5 rounded-full', statusConfig[ordem.status]?.dot ?? 'bg-slate-400']" />
              {{ statusConfig[ordem.status]?.label ?? ordem.status }}
            </span>
            <span
              :class="['inline-flex px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', criticidadeConfig[ordem.criticidade]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
            >
              {{ ordem.criticidade || '—' }}
            </span>
            <span
              v-if="ordem.tipoOrdem"
              class="inline-flex px-2.5 py-1 rounded-full border border-slate-500/30 bg-slate-500/10 text-slate-400 text-[11px] font-semibold uppercase tracking-wide"
            >
              {{ ordem.tipoOrdem.replace(/_/g, ' ') }}
            </span>
          </div>
          <p class="text-sm text-muted-foreground mt-1">
            {{ cliente?.nomeEmpresa ?? `Cliente #${ordem.codigoCliente}` }}
            <span v-if="tecnico"> · {{ tecnico.nome }}</span>
          </p>
          <p class="text-xs text-muted-foreground mt-0.5">
            Aberta em {{ formatDateTime(ordem.dataAbertura) ?? '—' }}
            <span v-if="getDuracao"> · Duração: {{ getDuracao }}</span>
          </p>
        </div>
        <Button variant="outline" class="shrink-0" @click="router.push('/ordens')">
          <ArrowLeft class="size-4 mr-2" /> Voltar
        </Button>
      </div>

      <!-- Timeline -->
      <Card class="bg-sidebar border-border">
        <CardContent class="p-5">
          <div class="flex items-start gap-0 overflow-x-auto">
            <template v-for="(step, i) in timeline" :key="step.label">
              <div class="flex flex-col items-center min-w-[120px] flex-1">
                <div :class="['flex items-center justify-center size-9 rounded-full border-2 transition-colors', step.done ? 'bg-indigo-500/15 border-indigo-500 text-indigo-400' : 'bg-muted/20 border-border text-muted-foreground/40']">
                  <component :is="step.icon" class="size-4" />
                </div>
                <p :class="['text-[11px] font-bold uppercase tracking-wide mt-2 text-center', step.done ? 'text-foreground' : 'text-muted-foreground/50']">
                  {{ step.label }}
                </p>
                <p class="text-[11px] text-muted-foreground mt-0.5 text-center">{{ step.dt ?? '—' }}</p>
              </div>
              <div
                v-if="i < timeline.length - 1"
                :class="['h-0.5 flex-1 mt-4 self-start transition-colors', timeline[i + 1].done ? 'bg-indigo-500/40' : 'bg-border']"
              />
            </template>
          </div>
        </CardContent>
      </Card>

      <!-- Grid principal -->
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">

        <!-- Coluna esquerda -->
        <div class="xl:col-span-1 space-y-5">

          <!-- Dados da ordem -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <ClipboardList class="size-4" /> Dados da Ordem
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Status</p>
                <span :class="['inline-flex items-center gap-1.5 mt-1 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', statusConfig[ordem.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']">
                  <span :class="['size-1.5 rounded-full', statusConfig[ordem.status]?.dot ?? 'bg-slate-400']" />
                  {{ statusConfig[ordem.status]?.label ?? ordem.status }}
                </span>
              </div>
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Criticidade</p>
                <span :class="['inline-flex mt-1 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', criticidadeConfig[ordem.criticidade]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']">
                  {{ ordem.criticidade || '—' }}
                </span>
              </div>
              <div v-if="ordem.tipoOrdem">
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Tipo de Ordem</p>
                <p class="text-sm text-foreground mt-0.5">{{ ordem.tipoOrdem.replace(/_/g, ' ') }}</p>
              </div>
              <div v-if="getDuracao">
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Duração da Execução</p>
                <p class="text-sm text-foreground mt-0.5">{{ getDuracao }}</p>
              </div>
              <div v-if="ordem.codigoHistoricoManutencao">
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Histórico de Manutenção</p>
                <p class="text-sm font-mono text-foreground mt-0.5">#{{ ordem.codigoHistoricoManutencao }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Observações -->
          <Card v-if="ordem.observacaoGeral" class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <MessageSquare class="size-4" /> Observações
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p class="text-sm text-muted-foreground leading-relaxed">{{ ordem.observacaoGeral }}</p>
            </CardContent>
          </Card>

        </div>

        <!-- Coluna direita -->
        <div class="xl:col-span-2 space-y-5">

          <!-- Participantes: cliente + técnico -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">

            <!-- Cliente -->
            <Card class="bg-sidebar border-border">
              <CardHeader class="pb-3">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <Building2 class="size-4" /> Cliente
                </CardTitle>
              </CardHeader>
              <CardContent class="space-y-3">
                <div v-if="cliente" class="flex items-center gap-3">
                  <div :class="['flex items-center justify-center size-9 rounded-lg text-sm font-bold text-white shrink-0', getAvatarColor(cliente.nomeEmpresa)]">
                    {{ cliente.nomeEmpresa.substring(0, 2).toUpperCase() }}
                  </div>
                  <div class="min-w-0">
                    <p class="text-sm font-semibold text-foreground truncate">{{ cliente.nomeEmpresa }}</p>
                    <p class="text-xs text-muted-foreground truncate">{{ cliente.emailContato || '—' }}</p>
                  </div>
                </div>
                <div v-else class="text-sm text-muted-foreground/50">Carregando...</div>
                <div v-if="cliente" class="space-y-1.5 pt-1 border-t border-border">
                  <div class="flex items-start gap-2">
                    <MapPin class="size-3.5 text-muted-foreground mt-0.5 shrink-0" />
                    <p class="text-xs text-muted-foreground">
                      {{ [cliente.cidade, cliente.estadoRegiao, cliente.pais].filter(Boolean).join(', ') || '—' }}
                    </p>
                  </div>
                </div>
                <Button v-if="cliente" variant="outline" size="sm" class="w-full text-xs" @click="router.push(`/clientes/${cliente.id}`)">
                  Ver cliente <ChevronRight class="size-3.5 ml-1" />
                </Button>
              </CardContent>
            </Card>

            <!-- Técnico -->
            <Card class="bg-sidebar border-border">
              <CardHeader class="pb-3">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <User class="size-4" /> Técnico Responsável
                </CardTitle>
              </CardHeader>
              <CardContent class="space-y-3">
                <div v-if="tecnico" class="flex items-center gap-3">
                  <div :class="['flex items-center justify-center size-9 rounded-full text-sm font-bold text-white shrink-0', getAvatarColor(tecnico.nome)]">
                    {{ tecnico.nome.substring(0, 2).toUpperCase() }}
                  </div>
                  <div class="min-w-0">
                    <p class="text-sm font-semibold text-foreground truncate">{{ tecnico.nome }}</p>
                    <p class="text-xs text-muted-foreground truncate">{{ tecnico.cargo || '—' }}</p>
                  </div>
                </div>
                <div v-else class="text-sm text-muted-foreground/50 py-2">Nenhum técnico atribuído</div>
                <div v-if="tecnico" class="space-y-1.5 pt-1 border-t border-border">
                  <div class="flex items-center gap-2">
                    <span :class="['size-2 rounded-full shrink-0', tecnico.estado === 'DISPONÍVEL' ? 'bg-emerald-500' : tecnico.estado === 'EM CAMPO' ? 'bg-blue-500' : 'bg-slate-500']" />
                    <p class="text-xs text-muted-foreground">{{ tecnico.estado ?? 'Status desconhecido' }}</p>
                  </div>
                </div>
                <Button v-if="tecnico" variant="outline" size="sm" class="w-full text-xs" @click="router.push(`/tecnicos/${tecnico.id}`)">
                  Ver técnico <ChevronRight class="size-3.5 ml-1" />
                </Button>
              </CardContent>
            </Card>
          </div>

          <!-- Recursos Vinculados -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <AlertTriangle class="size-4" /> Recursos Vinculados
              </CardTitle>
            </CardHeader>
            <CardContent class="p-0">
              <div class="divide-y divide-border">

                <!-- Contrato -->
                <div class="px-5 py-4 flex items-center justify-between gap-4">
                  <div class="flex items-center gap-3 min-w-0">
                    <div class="flex items-center justify-center size-9 rounded-lg bg-blue-500/10 border border-blue-500/20 shrink-0">
                      <FileText class="size-4 text-blue-400" />
                    </div>
                    <div class="min-w-0">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Contrato</p>
                      <p class="text-sm font-mono font-semibold text-foreground">CTR-{{ String(ordem.codigoContrato).padStart(4, '0') }}</p>
                      <p v-if="contrato" class="text-xs text-muted-foreground truncate">{{ formatDate(contrato.dataInicio) }} → {{ formatDate(contrato.dataFim) }}</p>
                    </div>
                  </div>
                  <Button variant="ghost" size="sm" class="shrink-0 text-xs text-muted-foreground hover:text-foreground" @click="router.push(`/contratos/${ordem.codigoContrato}`)">
                    Ver <ChevronRight class="size-3.5 ml-0.5" />
                  </Button>
                </div>

                <!-- Máquina -->
                <div class="px-5 py-4 flex items-center justify-between gap-4">
                  <div class="flex items-center gap-3 min-w-0">
                    <div class="flex items-center justify-center size-9 rounded-lg bg-purple-500/10 border border-purple-500/20 shrink-0">
                      <Cpu class="size-4 text-purple-400" />
                    </div>
                    <div class="min-w-0">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Máquina</p>
                      <p class="text-sm font-semibold text-foreground truncate">{{ catMaquina?.descricao ?? `Máquina #${ordem.codigoMaquinaContrato}` }}</p>
                      <p v-if="maquina?.numeroSerie" class="text-xs font-mono text-muted-foreground">NS: {{ maquina.numeroSerie }}</p>
                    </div>
                  </div>
                  <div v-if="maquina" class="shrink-0 flex flex-col items-end gap-1">
                    <span v-if="maquina.trabalhoEmAltura" class="text-[10px] px-1.5 py-0.5 rounded bg-amber-500/15 text-amber-400 border border-amber-500/30 font-semibold">Altura</span>
                    <span class="text-xs text-muted-foreground">{{ formatDate(maquina.dataInstalacao) ?? '—' }}</span>
                  </div>
                </div>

                <!-- Software instalado -->
                <div v-if="ordem.codigoSoftwareInstalado" class="px-5 py-4 flex items-center justify-between gap-4">
                  <div class="flex items-center gap-3 min-w-0">
                    <div class="flex items-center justify-center size-9 rounded-lg bg-emerald-500/10 border border-emerald-500/20 shrink-0">
                      <Package class="size-4 text-emerald-400" />
                    </div>
                    <div class="min-w-0">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Software Vinculado</p>
                      <p class="text-sm font-semibold text-foreground truncate">{{ catSoftware?.nomeSoftware ?? `Software #${ordem.codigoSoftwareInstalado}` }}</p>
                      <p v-if="software" class="text-xs font-mono text-muted-foreground">v{{ software.versaoInstalada }}<span v-if="catSoftware"> (catálogo: v{{ catSoftware.versao }})</span></p>
                    </div>
                  </div>
                  <div v-if="catSoftware" class="shrink-0">
                    <span :class="['text-[10px] px-1.5 py-0.5 rounded border font-semibold uppercase', catSoftware.ativo ? 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30' : 'bg-slate-500/15 text-slate-400 border-slate-500/30']">
                      {{ catSoftware.ativo ? 'Ativo' : 'Inativo' }}
                    </span>
                  </div>
                </div>

              </div>
            </CardContent>
          </Card>

        </div>
      </div>

      <!-- ════════════════════════════════════════════════════════════════
           Seção de Histórico e Checklists (largura total)
      ════════════════════════════════════════════════════════════════ -->

      <!-- Loading checklists -->
      <div v-if="loadingChecklists" class="flex items-center gap-3 text-sm text-muted-foreground p-5 border border-border rounded-xl bg-sidebar">
        <Loader2 class="size-4 animate-spin" /> Carregando checklists...
      </div>

      <template v-else>

        <!-- ── Histórico de Manutenção (header informativo) ────────────── -->
        <Card v-if="ordem.codigoHistoricoManutencao" class="bg-sidebar border-border">
          <CardHeader class="pb-4 border-b border-border">
            <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
              <History class="size-4" /> Histórico de Manutenção
            </CardTitle>
          </CardHeader>
          <CardContent class="pt-4">
            <div class="flex flex-wrap items-center gap-6">
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Código</p>
                <p class="text-lg font-mono font-bold text-foreground mt-0.5">#{{ ordem.codigoHistoricoManutencao }}</p>
              </div>
              <div v-if="checklistMaquina.length > 0" class="flex items-center gap-5">
                <div class="text-center">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Total</p>
                  <p class="text-lg font-bold text-foreground mt-0.5">{{ checklistMaquinaStats.total }}</p>
                </div>
                <div class="text-center">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-emerald-400">Concluídas</p>
                  <p class="text-lg font-bold text-emerald-400 mt-0.5">{{ checklistMaquinaStats.concluidos }}</p>
                </div>
                <div class="text-center">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-red-400">Não feitas</p>
                  <p class="text-lg font-bold text-red-400 mt-0.5">{{ checklistMaquinaStats.naoFeitos }}</p>
                </div>
                <div class="text-center">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Pendentes</p>
                  <p class="text-lg font-bold text-muted-foreground mt-0.5">{{ checklistMaquinaStats.pendentes }}</p>
                </div>
                <!-- Barra de progresso -->
                <div class="flex-1 min-w-[160px]">
                  <div class="flex justify-between text-[10px] text-muted-foreground mb-1">
                    <span>Progresso</span>
                    <span>{{ checklistMaquinaStats.total > 0 ? Math.round((checklistMaquinaStats.concluidos / checklistMaquinaStats.total) * 100) : 0 }}%</span>
                  </div>
                  <div class="h-2 rounded-full bg-muted/40 overflow-hidden">
                    <div
                      class="h-full rounded-full bg-emerald-500 transition-all"
                      :style="{ width: checklistMaquinaStats.total > 0 ? `${(checklistMaquinaStats.concluidos / checklistMaquinaStats.total) * 100}%` : '0%' }"
                    />
                  </div>
                </div>
              </div>
              <div v-else class="text-sm text-muted-foreground/60 italic">Nenhuma tarefa de manutenção registrada.</div>
            </div>
          </CardContent>
        </Card>

        <!-- ── Checklist de Ativos ──────────────────────────────────────── -->
        <Card class="bg-sidebar border-border">
          <CardHeader class="pb-3 border-b border-border">
            <CardTitle class="flex items-center justify-between">
              <span class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Box class="size-4" /> Checklist de Ativos
              </span>
              <span class="text-xs font-normal text-muted-foreground">{{ checklistAtivos.length }} {{ checklistAtivos.length === 1 ? 'item' : 'itens' }}</span>
            </CardTitle>
          </CardHeader>
          <CardContent class="p-0">

            <!-- Vazio -->
            <div v-if="checklistAtivos.length === 0" class="flex flex-col items-center justify-center py-10 text-muted-foreground/50">
              <ListChecks class="size-8 mb-2" />
              <p class="text-sm">Nenhum ativo registrado nesta ordem.</p>
            </div>

            <!-- Lista -->
            <div v-else class="divide-y divide-border">
              <div
                v-for="ativo in checklistAtivos"
                :key="ativo.codigo"
                class="px-5 py-4 flex items-start gap-4"
              >
                <!-- Ícone -->
                <div class="flex items-center justify-center size-9 rounded-lg bg-blue-500/10 border border-blue-500/20 shrink-0 mt-0.5">
                  <Box class="size-4 text-blue-400" />
                </div>

                <!-- Dados do ativo -->
                <div class="flex-1 min-w-0 space-y-1">
                  <p class="text-sm font-semibold text-foreground">
                    {{ ativo.descricaoProduto ?? `Ativo #${ativo.codigoAtivo}` }}
                  </p>
                  <p v-if="ativo.descricaoAtivo" class="text-xs text-muted-foreground">{{ ativo.descricaoAtivo }}</p>
                  <div class="flex flex-wrap gap-x-4 gap-y-1 text-xs text-muted-foreground">
                    <span v-if="ativo.marca">Marca: <span class="text-foreground/70">{{ ativo.marca }}</span></span>
                    <span v-if="ativo.modelo">Modelo: <span class="text-foreground/70">{{ ativo.modelo }}</span></span>
                    <span v-if="ativo.numeroSerie">S/N: <span class="font-mono text-foreground/70">{{ ativo.numeroSerie }}</span></span>
                    <span v-if="ativo.lote">Lote: <span class="text-foreground/70">{{ ativo.lote }}</span></span>
                  </div>
                  <p v-if="ativo.observacao" class="text-xs text-muted-foreground italic mt-1">"{{ ativo.observacao }}"</p>
                </div>

                <!-- Badges levado / devolvido -->
                <div class="flex flex-col gap-1.5 items-end shrink-0">
                  <span
                    :class="['inline-flex items-center gap-1 text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full border', ativo.levado ? 'bg-blue-500/15 text-blue-400 border-blue-500/30' : 'bg-muted/20 text-muted-foreground/50 border-border']"
                  >
                    <span :class="['size-1.5 rounded-full', ativo.levado ? 'bg-blue-400' : 'bg-muted-foreground/30']" />
                    Levado
                  </span>
                  <span
                    :class="['inline-flex items-center gap-1 text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full border', ativo.devolvido ? 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30' : 'bg-muted/20 text-muted-foreground/50 border-border']"
                  >
                    <span :class="['size-1.5 rounded-full', ativo.devolvido ? 'bg-emerald-400' : 'bg-muted-foreground/30']" />
                    Devolvido
                  </span>
                </div>
              </div>
            </div>

            <!-- Resumo levados/devolvidos -->
            <div v-if="checklistAtivos.length > 0" class="px-5 py-3 border-t border-border bg-muted/10 flex gap-6 text-xs text-muted-foreground">
              <span>{{ checklistAtivosStats.levados }}/{{ checklistAtivosStats.total }} levados</span>
              <span>{{ checklistAtivosStats.devolvidos }}/{{ checklistAtivosStats.total }} devolvidos</span>
            </div>
          </CardContent>
        </Card>

        <!-- ── Checklist de Manutenção ─────────────────────────────────── -->
        <Card class="bg-sidebar border-border">
          <CardHeader class="pb-3 border-b border-border">
            <CardTitle class="flex items-center justify-between">
              <span class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Wrench class="size-4" /> Checklist de Manutenção
              </span>
              <span class="text-xs font-normal text-muted-foreground">{{ checklistMaquina.length }} {{ checklistMaquina.length === 1 ? 'tarefa' : 'tarefas' }}</span>
            </CardTitle>
          </CardHeader>
          <CardContent class="p-0">

            <!-- Vazio -->
            <div v-if="checklistMaquina.length === 0" class="flex flex-col items-center justify-center py-10 text-muted-foreground/50">
              <Wrench class="size-8 mb-2" />
              <p class="text-sm">Nenhuma tarefa de manutenção registrada.</p>
            </div>

            <!-- Lista agrupada por categoria -->
            <div v-else>
              <div
                v-for="item in checklistMaquina"
                :key="item.codigo"
                class="flex items-center gap-4 px-5 py-4 border-b border-border last:border-0"
              >
                <!-- Ícone de status (read-only) -->
                <div class="mt-0.5 shrink-0">
                  <div v-if="item.realizado === true" class="flex items-center justify-center size-7 rounded-full bg-emerald-500/15 border border-emerald-500/30">
                    <CheckCircle2 class="size-4 text-emerald-400" />
                  </div>
                  <div v-else-if="item.realizado === false" class="flex items-center justify-center size-7 rounded-full bg-red-500/15 border border-red-500/30">
                    <XCircle class="size-4 text-red-400" />
                  </div>
                  <div v-else class="flex items-center justify-center size-7 rounded-full bg-muted/20 border border-border">
                    <Circle class="size-4 text-muted-foreground/40" />
                  </div>
                </div>

                <!-- Conteúdo -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 flex-wrap">
                    <p class="text-sm font-medium text-foreground">{{ item.descricaoTarefa ?? `Tarefa #${item.codigoTarefa}` }}</p>
                    <span
                      v-if="item.categoriaTarefa"
                      class="text-[10px] px-1.5 py-0.5 rounded bg-muted/30 border border-border text-muted-foreground font-medium"
                    >
                      {{ item.categoriaTarefa }}
                    </span>
                  </div>
                  <p v-if="item.observacaoTarefa" class="text-xs text-muted-foreground italic mt-0.5">"{{ item.observacaoTarefa }}"</p>
                </div>

                <!-- Status badge -->
                <div class="shrink-0">
                  <span v-if="item.realizado === true" class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full border bg-emerald-500/15 text-emerald-400 border-emerald-500/30">
                    Realizado
                  </span>
                  <span v-else-if="item.realizado === false" class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full border bg-red-500/15 text-red-400 border-red-500/30">
                    Não realizado
                  </span>
                  <span v-else class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full border bg-muted/20 text-muted-foreground border-border">
                    Pendente
                  </span>
                </div>
              </div>
            </div>

          </CardContent>
        </Card>

      </template>

    </template>

  </div>
</template>
