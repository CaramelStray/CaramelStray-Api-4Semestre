<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBreadcrumbs } from '@/composables/useBreadcrumbs'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  ArrowLeft, ClipboardList, User, Building2, FileText, Cpu,
  Package, Calendar, Clock, ChevronRight, Loader2, CheckCircle2,
  AlertTriangle, MapPin, MessageSquare,
} from 'lucide-vue-next'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
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
const ordem      = ref<OrdemServicoResponseDTO | null>(null)
const cliente    = ref<ClienteResponseDTO | null>(null)
const tecnico    = ref<TecnicoResponseDTO | null>(null)
const contrato   = ref<ContratoResponseDTO | null>(null)
const maquina    = ref<MaquinaContratoResponseDTO | null>(null)
const catMaquina = ref<CatalogoMaquinaResponseDTO | null>(null)
const software   = ref<MaquinaSoftwareInstaladoResponseDTO | null>(null)
const catSoftware = ref<CatalogoSoftwareResponseDTO | null>(null)

const loading = ref(true)
const erro    = ref('')

const { setDynamicBreadcrumb } = useBreadcrumbs()

// ── Configs ───────────────────────────────────────────────────────────────
const statusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  AGUARDANDO:  { dot: 'bg-amber-500',   badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',     label: 'Aguardando'  },
  AGENDADO:    { dot: 'bg-blue-500',    badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',        label: 'Agendado'    },
  EM_EXECUCAO: { dot: 'bg-indigo-500',  badge: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/30',  label: 'Em Execução' },
  CONCLUIDA:   { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Concluída' },
  CANCELADA:   { dot: 'bg-red-500',     badge: 'bg-red-500/15 text-red-400 border-red-500/30',           label: 'Cancelada'   },
}

const criticidadeConfig: Record<string, { badge: string; color: string }> = {
  CRITICA: { badge: 'bg-red-500/15 text-red-400 border-red-500/30',          color: 'text-red-400'     },
  ALTA:    { badge: 'bg-orange-500/15 text-orange-400 border-orange-500/30', color: 'text-orange-400'  },
  MEDIA:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',    color: 'text-amber-400'   },
  BAIXA:   { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',       color: 'text-blue-400'    },
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
  return isNaN(d.getTime()) ? null : d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const getDuracao = computed(() => {
  if (!ordem.value?.dataInicioExecucao || !ordem.value?.dataFimExecucao) return null
  const inicio = new Date(ordem.value.dataInicioExecucao)
  const fim    = new Date(ordem.value.dataFimExecucao)
  const diff   = fim.getTime() - inicio.getTime()
  if (diff <= 0) return null
  const h = Math.floor(diff / 3_600_000)
  const m = Math.floor((diff % 3_600_000) / 60_000)
  if (h > 0) return `${h}h ${m}min`
  return `${m}min`
})

// Etapas do timeline
const timeline = computed(() => {
  if (!ordem.value) return []
  return [
    { label: 'Abertura',       dt: formatDateTime(ordem.value.dataAbertura),       icon: ClipboardList, done: !!ordem.value.dataAbertura       },
    { label: 'Agendamento',    dt: formatDateTime(ordem.value.dataAgendamento),    icon: Calendar,      done: !!ordem.value.dataAgendamento    },
    { label: 'Início Execução', dt: formatDateTime(ordem.value.dataInicioExecucao), icon: Clock,        done: !!ordem.value.dataInicioExecucao },
    { label: 'Conclusão',      dt: formatDateTime(ordem.value.dataFimExecucao),    icon: CheckCircle2,  done: !!ordem.value.dataFimExecucao    },
  ]
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
        .then(r => { 
          cliente.value = r
          setDynamicBreadcrumb(r.nomeEmpresa)
        }).catch(() => {}),

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
})

import { onUnmounted } from 'vue'
onUnmounted(() => {
  setDynamicBreadcrumb(null)
})
</script>

<template>
  <div class="p-6 space-y-6">

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
              <!-- Etapa -->
              <div class="flex flex-col items-center min-w-[120px] flex-1">
                <div :class="['flex items-center justify-center size-9 rounded-full border-2 transition-colors', step.done ? 'bg-indigo-500/15 border-indigo-500 text-indigo-400' : 'bg-muted/20 border-border text-muted-foreground/40']">
                  <component :is="step.icon" class="size-4" />
                </div>
                <p :class="['text-[11px] font-bold uppercase tracking-wide mt-2 text-center', step.done ? 'text-foreground' : 'text-muted-foreground/50']">
                  {{ step.label }}
                </p>
                <p class="text-[11px] text-muted-foreground mt-0.5 text-center">
                  {{ step.dt ?? '—' }}
                </p>
              </div>
              <!-- Linha conectora -->
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
                <span
                  :class="['inline-flex items-center gap-1.5 mt-1 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', statusConfig[ordem.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                >
                  <span :class="['size-1.5 rounded-full', statusConfig[ordem.status]?.dot ?? 'bg-slate-400']" />
                  {{ statusConfig[ordem.status]?.label ?? ordem.status }}
                </span>
              </div>
              <div>
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Criticidade</p>
                <span
                  :class="['inline-flex mt-1 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', criticidadeConfig[ordem.criticidade]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                >
                  {{ ordem.criticidade || '—' }}
                </span>
              </div>
              <div v-if="getDuracao">
                <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Duração da Execução</p>
                <p class="text-sm text-foreground mt-0.5">{{ getDuracao }}</p>
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
                <Button
                  v-if="cliente"
                  variant="outline"
                  size="sm"
                  class="w-full text-xs"
                  @click="router.push(`/clientes/${cliente.id}`)"
                >
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
                <div v-else class="text-sm text-muted-foreground/50 py-2">
                  Nenhum técnico atribuído
                </div>
                <div v-if="tecnico" class="space-y-1.5 pt-1 border-t border-border">
                  <div class="flex items-center gap-2">
                    <span :class="['size-2 rounded-full shrink-0', tecnico.estado === 'DISPONÍVEL' ? 'bg-emerald-500' : tecnico.estado === 'EM CAMPO' ? 'bg-blue-500' : 'bg-slate-500']" />
                    <p class="text-xs text-muted-foreground">{{ tecnico.estado ?? 'Status desconhecido' }}</p>
                  </div>
                </div>
                <Button
                  v-if="tecnico"
                  variant="outline"
                  size="sm"
                  class="w-full text-xs"
                  @click="router.push(`/tecnicos/${tecnico.id}`)"
                >
                  Ver técnico <ChevronRight class="size-3.5 ml-1" />
                </Button>
              </CardContent>
            </Card>
          </div>

          <!-- Recursos: contrato + máquina + software -->
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
                      <p class="text-sm font-mono font-semibold text-foreground">
                        CTR-{{ String(ordem.codigoContrato).padStart(4, '0') }}
                      </p>
                      <p v-if="contrato" class="text-xs text-muted-foreground truncate">
                        {{ formatDate(contrato.dataInicio) }} → {{ formatDate(contrato.dataFim) }}
                      </p>
                    </div>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    class="shrink-0 text-xs text-muted-foreground hover:text-foreground"
                    @click="router.push(`/contratos/${ordem.codigoContrato}`)"
                  >
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
                      <p class="text-sm font-semibold text-foreground truncate">
                        {{ catMaquina?.descricao ?? `Máquina #${ordem.codigoMaquinaContrato}` }}
                      </p>
                      <p v-if="maquina?.numeroSerie" class="text-xs font-mono text-muted-foreground">
                        NS: {{ maquina.numeroSerie }}
                      </p>
                    </div>
                  </div>
                  <div v-if="maquina" class="shrink-0 flex flex-col items-end gap-1">
                    <span
                      v-if="maquina.trabalhoEmAltura"
                      class="text-[10px] px-1.5 py-0.5 rounded bg-amber-500/15 text-amber-400 border border-amber-500/30 font-semibold"
                    >
                      Altura
                    </span>
                    <span class="text-xs text-muted-foreground">
                      {{ formatDate(maquina.dataInstalacao) ?? '—' }}
                    </span>
                  </div>
                </div>

                <!-- Software instalado (opcional) -->
                <div v-if="ordem.codigoSoftwareInstalado" class="px-5 py-4 flex items-center justify-between gap-4">
                  <div class="flex items-center gap-3 min-w-0">
                    <div class="flex items-center justify-center size-9 rounded-lg bg-emerald-500/10 border border-emerald-500/20 shrink-0">
                      <Package class="size-4 text-emerald-400" />
                    </div>
                    <div class="min-w-0">
                      <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Software Vinculado</p>
                      <p class="text-sm font-semibold text-foreground truncate">
                        {{ catSoftware?.nomeSoftware ?? `Software #${ordem.codigoSoftwareInstalado}` }}
                      </p>
                      <p v-if="software" class="text-xs font-mono text-muted-foreground">
                        v{{ software.versaoInstalada }}
                        <span v-if="catSoftware"> (catálogo: v{{ catSoftware.versao }})</span>
                      </p>
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
    </template>

  </div>
</template>
