<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  ArrowLeft, FileText, Building2, Calendar, Clock, Wifi, WifiOff,
  ClipboardList, CheckCircle2, AlertTriangle, Activity, Cpu,
  Package, ChevronRight, Loader2, MapPin, Wrench, Key,
  ChevronDown, ChevronUp, Info,
} from 'lucide-vue-next'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { maquinaContratoService, type MaquinaContratoResponseDTO } from '@/services/maquinaContratoService'
import { maquinaSoftwareInstaladoService, type MaquinaSoftwareInstaladoResponseDTO } from '@/services/maquinaSoftwareInstaladoService'
import { catalogoMaquinaService, type CatalogoMaquinaResponseDTO } from '@/services/catalogoMaquinaService'
import { catalogoSoftwareService, type CatalogoSoftwareResponseDTO } from '@/services/catalogoSoftwareService'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import { tecnicoService } from '@/services/tecnicoService'

const route = useRoute()
const router = useRouter()

const contratoId = Number(route.params.id)

// ── Estado ─────────────────────────────────────────────────────────────────
const contrato   = ref<ContratoResponseDTO | null>(null)
const cliente    = ref<ClienteResponseDTO | null>(null)
const ordens     = ref<OrdemServicoResponseDTO[]>([])
const tecnicoMap = ref<Record<number, string>>({})

interface MaquinaEnriquecida {
  maquina:    MaquinaContratoResponseDTO
  catalogo:   CatalogoMaquinaResponseDTO | null
  softwares:  { instalado: MaquinaSoftwareInstaladoResponseDTO; catalogo: CatalogoSoftwareResponseDTO | null }[]
}
const maquinas = ref<MaquinaEnriquecida[]>([])

const loading         = ref(true)
const loadingMaquinas = ref(true)
const loadingOrdens   = ref(true)
const erro            = ref('')

const maquinaExpandida = ref<number | null>(null)
const toggleMaquina = (id: number) => {
  maquinaExpandida.value = maquinaExpandida.value === id ? null : id
}

// ── Configs de exibição ────────────────────────────────────────────────────
const contratoStatusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  ATIVO:      { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Ativo'      },
  EM_VIGOR:   { dot: 'bg-blue-500',    badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',          label: 'Em Vigor'   },
  ENCERRADO:  { dot: 'bg-slate-500',   badge: 'bg-slate-500/15 text-slate-400 border-slate-500/30',       label: 'Encerrado'  },
  SUSPENSO:   { dot: 'bg-amber-500',   badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',       label: 'Suspenso'   },
  CANCELADO:  { dot: 'bg-red-500',     badge: 'bg-red-500/15 text-red-400 border-red-500/30',             label: 'Cancelado'  },
  PENDENTE:   { dot: 'bg-amber-500',   badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',       label: 'Pendente'   },
  EXPIRANDO:  { dot: 'bg-orange-500',  badge: 'bg-orange-500/15 text-orange-400 border-orange-500/30',    label: 'Expirando'  },
  INATIVO:    { dot: 'bg-slate-500',   badge: 'bg-slate-500/15 text-slate-400 border-slate-500/30',       label: 'Inativo'    },
}

const ordemStatusConfig: Record<string, { badge: string; label: string }> = {
  AGUARDANDO:  { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',      label: 'Aguardando'  },
  AGENDADO:    { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',         label: 'Agendado'    },
  EM_EXECUCAO: { badge: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/30',   label: 'Em Execução' },
  CONCLUIDA:   { badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Concluída'  },
  CANCELADA:   { badge: 'bg-red-500/15 text-red-400 border-red-500/30',            label: 'Cancelada'   },
}

const criticidadeConfig: Record<string, { badge: string }> = {
  CRITICA: { badge: 'bg-red-500/15 text-red-400 border-red-500/30'        },
  ALTA:    { badge: 'bg-orange-500/15 text-orange-400 border-orange-500/30' },
  MEDIA:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30'  },
  BAIXA:   { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30'     },
}

const softwareStatusConfig: Record<string, { badge: string }> = {
  ATIVO:    { badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30' },
  INATIVO:  { badge: 'bg-slate-500/15 text-slate-400 border-slate-500/30'      },
  LEGADO:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30'      },
}

// ── Utilitários ────────────────────────────────────────────────────────────
const buildContratoId = (codigo: number) => `CTR-${String(codigo).padStart(4, '0')}`

const formatDate = (dt?: string | null) => {
  if (!dt) return '—'
  const d = new Date(`${dt}T00:00:00`)
  return isNaN(d.getTime()) ? dt : d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const formatDateTime = (dt?: string | null) => {
  if (!dt) return '—'
  const d = new Date(dt)
  return isNaN(d.getTime()) ? dt : d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const getDiasParaVencimento = (dataFim?: string | null) => {
  if (!dataFim) return null
  const d = new Date(`${dataFim}T00:00:00`)
  if (isNaN(d.getTime())) return null
  return Math.ceil((d.getTime() - Date.now()) / (1000 * 60 * 60 * 24))
}

const statusApresentacao = computed(() => {
  if (!contrato.value) return ''
  const s = contrato.value.status ?? 'ATIVO'
  const dias = getDiasParaVencimento(contrato.value.dataFim)
  if (s === 'INATIVO') return 'INATIVO'
  if (dias !== null && dias < 0)  return 'PENDENTE'
  if (dias !== null && dias <= 30) return 'EXPIRANDO'
  return s
})

// ── Stats ──────────────────────────────────────────────────────────────────
const stats = computed(() => [
  {
    label: 'Máquinas',
    value: maquinas.value.length,
    icon: Cpu,
    color: 'text-blue-400',
    bg:   'bg-blue-500/10',
  },
  {
    label: 'Softwares Instalados',
    value: maquinas.value.reduce((acc, m) => acc + m.softwares.length, 0),
    icon: Package,
    color: 'text-purple-400',
    bg:   'bg-purple-500/10',
  },
  {
    label: 'Total de Ordens',
    value: ordens.value.length,
    icon: ClipboardList,
    color: 'text-indigo-400',
    bg:   'bg-indigo-500/10',
  },
  {
    label: 'Ordens Abertas',
    value: ordens.value.filter((o: OrdemServicoResponseDTO) =>
      ['AGUARDANDO', 'AGENDADO', 'EM_EXECUCAO'].includes(o.status)
    ).length,
    icon: AlertTriangle,
    color: 'text-amber-400',
    bg:   'bg-amber-500/10',
  },
])

// ── Carregamento ───────────────────────────────────────────────────────────
onMounted(async () => {
  // 1. Contrato
  try {
    contrato.value = await contratoService.buscarPorId(contratoId)
    if (contrato.value.codigoCliente) {
      cliente.value = await clienteService.buscarPorId(contrato.value.codigoCliente).catch(() => null)
    }
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar contrato'
    loading.value = false
    return
  } finally {
    loading.value = false
  }

  // 2. Máquinas + catálogo + softwares (paralelo por máquina)
  try {
    const maquinasBruto = await maquinaContratoService.buscarPorContrato(contratoId)

    const enriquecidas = await Promise.all(
      maquinasBruto.map(async (maq: MaquinaContratoResponseDTO) => {
        const [catalogoResult, softwaresBruto] = await Promise.all([
          catalogoMaquinaService.buscarPorId(maq.codigoCatalogoMaquina).catch(() => null),
          maquinaSoftwareInstaladoService.buscarPorMaquinaContrato(maq.codigo).catch(() => [] as MaquinaSoftwareInstaladoResponseDTO[]),
        ])

        const softwaresEnriquecidos = await Promise.all(
          softwaresBruto.map(async (sw: MaquinaSoftwareInstaladoResponseDTO) => ({
            instalado: sw,
            catalogo: await catalogoSoftwareService.buscarPorId(sw.codigoSoftware).catch(() => null),
          }))
        )

        return {
          maquina:   maq,
          catalogo:  catalogoResult,
          softwares: softwaresEnriquecidos,
        } as MaquinaEnriquecida
      })
    )

    maquinas.value = enriquecidas
  } catch {
    maquinas.value = []
  } finally {
    loadingMaquinas.value = false
  }

  // 3. Ordens + técnicos
  try {
    ordens.value = await ordemServicoService.buscarPorContrato(contratoId)
    const tecnicoIds: number[] = Array.from(
      new Set(
        ordens.value
          .filter((o: OrdemServicoResponseDTO) => o.codigoFuncionario)
          .map((o: OrdemServicoResponseDTO) => o.codigoFuncionario as number)
      )
    )
    const tecnicos = await Promise.all(tecnicoIds.map((id: number) => tecnicoService.buscarPorId(id).catch(() => null)))
    tecnicos.forEach((t, i) => { if (t) tecnicoMap.value[tecnicoIds[i]] = t.nome })
  } catch {
    ordens.value = []
  } finally {
    loadingOrdens.value = false
  }
})
</script>

<template>
  <div class="p-6 space-y-6">

    <!-- Breadcrumb -->
    <div class="flex items-center gap-2 text-sm text-muted-foreground">
      <button
        class="flex items-center gap-1.5 hover:text-foreground transition-colors cursor-pointer"
        @click="router.push('/contratos')"
      >
        <ArrowLeft class="size-4" />
        Contratos
      </button>
      <ChevronRight class="size-3.5" />
      <span class="text-foreground font-medium">{{ contrato ? buildContratoId(contrato.codigo) : 'Detalhes' }}</span>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="flex items-center justify-center py-24 text-muted-foreground gap-3">
      <Loader2 class="size-5 animate-spin" /> Carregando contrato...
    </div>
    <div v-else-if="erro" class="py-24 text-center text-red-400">{{ erro }}</div>

    <template v-else-if="contrato">

      <!-- Hero -->
      <div class="rounded-xl border border-border bg-sidebar p-6 flex flex-col sm:flex-row items-start sm:items-center gap-5">
        <div class="flex items-center justify-center size-16 rounded-xl bg-blue-500/15 border border-blue-500/25 shrink-0">
          <FileText class="size-8 text-blue-400" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-3 flex-wrap">
            <h1 class="text-2xl font-bold text-foreground font-mono">{{ buildContratoId(contrato.codigo) }}</h1>
            <span
              :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', contratoStatusConfig[statusApresentacao]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
            >
              <span :class="['size-1.5 rounded-full', contratoStatusConfig[statusApresentacao]?.dot ?? 'bg-slate-400']" />
              {{ contratoStatusConfig[statusApresentacao]?.label ?? statusApresentacao }}
            </span>
          </div>
          <p class="text-sm text-muted-foreground mt-1">
            {{ cliente ? cliente.nomeEmpresa : (contrato.nomeCliente ?? 'Cliente não informado') }}
          </p>
          <p class="text-xs text-muted-foreground mt-0.5">
            {{ formatDate(contrato.dataInicio) }} → {{ formatDate(contrato.dataFim) }}
          </p>
        </div>
        <Button variant="outline" class="shrink-0" @click="router.push('/contratos')">
          <ArrowLeft class="size-4 mr-2" /> Voltar
        </Button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-2 xl:grid-cols-4 gap-4">
        <Card v-for="s in stats" :key="s.label" class="bg-sidebar border-border">
          <CardContent class="p-4 flex items-center gap-4">
            <div :class="['p-2.5 rounded-lg', s.bg]">
              <component :is="s.icon" :class="['size-5', s.color]" />
            </div>
            <div>
              <p class="text-2xl font-bold text-foreground">{{ s.value }}</p>
              <p class="text-[11px] text-muted-foreground uppercase tracking-wide">{{ s.label }}</p>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Main grid -->
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">

        <!-- Coluna esquerda: info do contrato + cliente -->
        <div class="xl:col-span-1 space-y-5">

          <!-- Dados do contrato -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <FileText class="size-4" /> Dados do Contrato
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <Calendar class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Início da Vigência</p>
                  <p class="text-sm text-foreground mt-0.5">{{ formatDate(contrato.dataInicio) }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Calendar class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Fim da Vigência</p>
                  <div class="flex items-center gap-2 mt-0.5">
                    <p class="text-sm text-foreground">{{ formatDate(contrato.dataFim) }}</p>
                    <span
                      v-if="getDiasParaVencimento(contrato.dataFim) !== null && getDiasParaVencimento(contrato.dataFim)! <= 30 && getDiasParaVencimento(contrato.dataFim)! > 0"
                      class="text-[10px] px-1.5 py-0.5 rounded bg-amber-500/15 text-amber-400 border border-amber-500/30 font-semibold"
                    >
                      {{ getDiasParaVencimento(contrato.dataFim) }}d
                    </span>
                    <span
                      v-else-if="getDiasParaVencimento(contrato.dataFim) !== null && getDiasParaVencimento(contrato.dataFim)! < 0"
                      class="text-[10px] px-1.5 py-0.5 rounded bg-red-500/15 text-red-400 border border-red-500/30 font-semibold"
                    >
                      Vencido
                    </span>
                  </div>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Clock class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Período Preventiva</p>
                  <p class="text-sm text-foreground mt-0.5">
                    {{ contrato.periodoManutencaoPreventiva ? `${contrato.periodoManutencaoPreventiva} dias` : '—' }}
                  </p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <AlertTriangle class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Venc. Preventiva</p>
                  <p class="text-sm text-foreground mt-0.5">{{ formatDate(contrato.vencimentoManutencaoPreventiva) }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <component :is="contrato.conexaoInternet ? Wifi : WifiOff" class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Conexão à Internet</p>
                  <p :class="['text-sm mt-0.5', contrato.conexaoInternet ? 'text-emerald-400' : 'text-muted-foreground']">
                    {{ contrato.conexaoInternet ? 'Sim' : 'Não' }}
                  </p>
                </div>
              </div>
              <div v-if="contrato.descricao" class="flex items-start gap-3">
                <Info class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Descrição</p>
                  <p class="text-sm text-muted-foreground mt-0.5 leading-relaxed">{{ contrato.descricao }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Dados do cliente -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Building2 class="size-4" /> Cliente
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <Building2 class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Empresa</p>
                  <p class="text-sm text-foreground mt-0.5">
                    {{ cliente?.nomeEmpresa ?? contrato.nomeCliente ?? '—' }}
                  </p>
                </div>
              </div>
              <div v-if="cliente" class="flex items-start gap-3">
                <span class="size-4 flex items-center justify-center mt-0.5 shrink-0 text-muted-foreground text-xs font-bold">@</span>
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">E-mail</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.emailContato || '—' }}</p>
                </div>
              </div>
              <div v-if="cliente" class="flex items-start gap-3">
                <MapPin class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Localização</p>
                  <p class="text-sm text-foreground mt-0.5">
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
                Ver página do cliente
                <ChevronRight class="size-3.5 ml-1" />
              </Button>
            </CardContent>
          </Card>

        </div>

        <!-- Coluna direita: máquinas + ordens -->
        <div class="xl:col-span-2 space-y-6">

          <!-- Máquinas do contrato -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <div class="flex items-center justify-between">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <Cpu class="size-4" /> Máquinas do Contrato
                </CardTitle>
                <span class="text-xs text-muted-foreground">
                  {{ maquinas.length }} {{ maquinas.length === 1 ? 'máquina' : 'máquinas' }}
                </span>
              </div>
            </CardHeader>
            <CardContent class="p-0">

              <div v-if="loadingMaquinas" class="flex items-center justify-center py-10 text-muted-foreground gap-2">
                <Loader2 class="size-4 animate-spin" /> Carregando máquinas...
              </div>

              <div v-else-if="maquinas.length === 0" class="py-10 text-center text-muted-foreground/50 text-sm">
                Nenhuma máquina vinculada a este contrato
              </div>

              <div v-else class="divide-y divide-border">
                <div v-for="m in maquinas" :key="m.maquina.codigo">

                  <!-- Header da máquina (clicável) -->
                  <button
                    class="w-full flex items-center gap-4 px-5 py-4 hover:bg-muted/20 transition-colors text-left"
                    @click="toggleMaquina(m.maquina.codigo)"
                  >
                    <div class="flex items-center justify-center size-9 rounded-lg bg-blue-500/10 border border-blue-500/20 shrink-0">
                      <Cpu class="size-4 text-blue-400" />
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-semibold text-foreground truncate">
                        {{ m.catalogo?.descricao ?? `Máquina #${m.maquina.codigoCatalogoMaquina}` }}
                      </p>
                      <div class="flex flex-wrap items-center gap-x-3 gap-y-0.5 mt-0.5">
                        <span class="text-[11px] text-muted-foreground font-mono">
                          NS: {{ m.maquina.numeroSerie ?? '—' }}
                        </span>
                        <span class="text-[11px] text-muted-foreground">
                          Instalação: {{ formatDate(m.maquina.dataInstalacao) }}
                        </span>
                        <span
                          v-if="m.maquina.trabalhoEmAltura"
                          class="text-[10px] px-1.5 py-0.5 rounded bg-amber-500/15 text-amber-400 border border-amber-500/30 font-semibold"
                        >
                          Trabalho em altura
                        </span>
                      </div>
                    </div>
                    <div class="flex items-center gap-3 shrink-0">
                      <span class="inline-flex items-center gap-1 text-[11px] text-muted-foreground">
                        <Package class="size-3.5" />
                        {{ m.softwares.length }} sw
                      </span>
                      <component :is="maquinaExpandida === m.maquina.codigo ? ChevronUp : ChevronDown" class="size-4 text-muted-foreground" />
                    </div>
                  </button>

                  <!-- Detalhes expandíveis da máquina -->
                  <div v-if="maquinaExpandida === m.maquina.codigo" class="border-t border-border bg-muted/5">

                    <!-- Info técnica da máquina -->
                    <div class="px-5 py-4 grid grid-cols-2 sm:grid-cols-3 gap-4">
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Número de Série</p>
                        <p class="text-sm font-mono text-foreground mt-0.5">{{ m.maquina.numeroSerie ?? '—' }}</p>
                      </div>
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Data de Instalação</p>
                        <p class="text-sm text-foreground mt-0.5">{{ formatDate(m.maquina.dataInstalacao) }}</p>
                      </div>
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Trabalho em Altura</p>
                        <p :class="['text-sm mt-0.5', m.maquina.trabalhoEmAltura ? 'text-amber-400' : 'text-muted-foreground']">
                          {{ m.maquina.trabalhoEmAltura ? 'Sim' : 'Não' }}
                        </p>
                      </div>
                      <div v-if="m.maquina.manutencaoFeita">
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Manutenção Feita</p>
                        <p class="text-sm text-foreground mt-0.5">{{ m.maquina.manutencaoFeita }}</p>
                      </div>
                      <div v-if="m.maquina.latitude != null">
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Latitude</p>
                        <p class="text-sm font-mono text-foreground mt-0.5">{{ m.maquina.latitude }}</p>
                      </div>
                      <div v-if="m.maquina.longitude != null">
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Longitude</p>
                        <p class="text-sm font-mono text-foreground mt-0.5">{{ m.maquina.longitude }}</p>
                      </div>
                      <div v-if="m.catalogo?.especificacao" class="col-span-2 sm:col-span-3">
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Especificação</p>
                        <p class="text-sm text-muted-foreground mt-0.5 leading-relaxed">{{ m.catalogo.especificacao }}</p>
                      </div>
                      <div v-if="m.catalogo?.limiteManutencao">
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Limite de Manutenção</p>
                        <p class="text-sm text-foreground mt-0.5">{{ m.catalogo.limiteManutencao }}</p>
                      </div>
                    </div>

                    <!-- Softwares instalados nesta máquina -->
                    <div class="border-t border-border">
                      <div class="px-5 py-3 flex items-center justify-between">
                        <p class="text-[11px] font-bold uppercase tracking-wider text-muted-foreground flex items-center gap-1.5">
                          <Package class="size-3.5" /> Softwares Instalados
                        </p>
                        <span class="text-xs text-muted-foreground">{{ m.softwares.length }} software(s)</span>
                      </div>

                      <div v-if="m.softwares.length === 0" class="px-5 pb-4 text-sm text-muted-foreground/50">
                        Nenhum software instalado nesta máquina
                      </div>

                      <div v-else class="px-5 pb-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div
                          v-for="sw in m.softwares"
                          :key="sw.instalado.codigo"
                          class="rounded-lg border border-border bg-background/50 p-3 space-y-2"
                        >
                          <div class="flex items-start justify-between gap-2">
                            <div class="flex-1 min-w-0">
                              <p class="text-sm font-semibold text-foreground truncate">
                                {{ sw.catalogo?.nomeSoftware ?? `Software #${sw.instalado.codigoSoftware}` }}
                              </p>
                              <p v-if="sw.catalogo?.tipo" class="text-[11px] text-muted-foreground mt-0.5">
                                {{ sw.catalogo.tipo }}
                                <span v-if="sw.catalogo.desenvolvedorFornecedor"> · {{ sw.catalogo.desenvolvedorFornecedor }}</span>
                              </p>
                            </div>
                            <span
                              v-if="sw.catalogo?.status"
                              :class="['shrink-0 inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase', softwareStatusConfig[sw.catalogo.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                            >
                              {{ sw.catalogo.status }}
                            </span>
                          </div>
                          <div class="grid grid-cols-2 gap-x-4 gap-y-1">
                            <div>
                              <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Versão Catálogo</p>
                              <p class="text-xs font-mono text-foreground mt-0.5">{{ sw.catalogo?.versao ?? '—' }}</p>
                            </div>
                            <div>
                              <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Versão Instalada</p>
                              <p class="text-xs font-mono text-foreground mt-0.5">{{ sw.instalado.versaoInstalada }}</p>
                            </div>
                          </div>
                          <div v-if="sw.instalado.chaveLicenca" class="flex items-center gap-1.5 rounded bg-muted/30 px-2 py-1.5">
                            <Key class="size-3 text-muted-foreground shrink-0" />
                            <p class="text-[10px] font-mono text-muted-foreground truncate">{{ sw.instalado.chaveLicenca }}</p>
                          </div>
                        </div>
                      </div>
                    </div>

                  </div>
                </div>
              </div>

            </CardContent>
          </Card>

          <!-- Ordens de Serviço -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <div class="flex items-center justify-between">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <ClipboardList class="size-4" /> Ordens de Serviço
                </CardTitle>
                <span class="text-xs text-muted-foreground">
                  {{ ordens.length }} {{ ordens.length === 1 ? 'ordem' : 'ordens' }}
                </span>
              </div>
            </CardHeader>
            <CardContent class="p-0">

              <div v-if="loadingOrdens" class="flex items-center justify-center py-10 text-muted-foreground gap-2">
                <Loader2 class="size-4 animate-spin" /> Carregando ordens...
              </div>

              <div v-else-if="ordens.length === 0" class="py-10 text-center text-muted-foreground/50 text-sm">
                Nenhuma ordem de serviço vinculada a este contrato
              </div>

              <Table v-else>
                <TableHeader>
                  <TableRow class="hover:bg-transparent border-border text-[10px] uppercase font-bold text-muted-foreground">
                    <TableHead class="pl-5 h-10 w-14">Nº</TableHead>
                    <TableHead class="h-10">Técnico</TableHead>
                    <TableHead class="h-10">Máquina</TableHead>
                    <TableHead class="h-10">Status</TableHead>
                    <TableHead class="h-10">Criticidade</TableHead>
                    <TableHead class="h-10">Abertura</TableHead>
                    <TableHead class="h-10">Agendamento</TableHead>
                    <TableHead class="h-10 pr-5 w-16" />
                  </TableRow>
                </TableHeader>
                <TableBody>
                  <TableRow
                    v-for="o in ordens"
                    :key="o.codigo"
                    class="border-border hover:bg-muted/30 transition-colors even:bg-muted/5"
                  >
                    <TableCell class="pl-5 py-3">
                      <span class="text-xs font-mono font-semibold text-foreground">#{{ o.codigo }}</span>
                    </TableCell>
                    <TableCell class="py-3 text-sm text-foreground">
                      {{ o.codigoFuncionario ? (tecnicoMap[o.codigoFuncionario] ?? `Téc. #${o.codigoFuncionario}`) : '—' }}
                    </TableCell>
                    <TableCell class="py-3 text-xs text-muted-foreground font-mono">
                      {{ o.codigoMaquinaContrato ? `#${o.codigoMaquinaContrato}` : '—' }}
                    </TableCell>
                    <TableCell class="py-3">
                      <span :class="['inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', ordemStatusConfig[o.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']">
                        {{ ordemStatusConfig[o.status]?.label ?? o.status }}
                      </span>
                    </TableCell>
                    <TableCell class="py-3">
                      <span :class="['inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', criticidadeConfig[o.criticidade]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']">
                        {{ o.criticidade || '—' }}
                      </span>
                    </TableCell>
                    <TableCell class="py-3">
                      <div class="flex items-center gap-1.5 text-xs text-muted-foreground">
                        <Calendar class="size-3.5 shrink-0" />
                        {{ formatDate(o.dataAbertura) }}
                      </div>
                    </TableCell>
                    <TableCell class="py-3 text-xs text-muted-foreground">{{ formatDate(o.dataAgendamento) }}</TableCell>
                    <TableCell class="py-2 pr-5 text-right">
                      <Button variant="ghost" size="sm" class="h-7 px-2 text-muted-foreground hover:text-foreground text-xs gap-1" @click="router.push(`/ordens/${o.codigo}`)">
                        Ver <ChevronRight class="size-3.5" />
                      </Button>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>

            </CardContent>
          </Card>

        </div>
      </div>
    </template>

  </div>
</template>
