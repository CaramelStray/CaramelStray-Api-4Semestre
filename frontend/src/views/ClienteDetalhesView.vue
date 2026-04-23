<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  ArrowLeft, Building2, Mail, Phone, User, MapPin, Globe, Clock,
  FileText, ClipboardList, CheckCircle2, AlertTriangle, Activity,
  Calendar, ChevronRight, Loader2, Wifi, WifiOff, ChevronDown, ChevronUp,
} from 'lucide-vue-next'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import { tecnicoService } from '@/services/tecnicoService'

const route = useRoute()
const router = useRouter()

const clienteId = Number(route.params.id)

const cliente = ref<ClienteResponseDTO | null>(null)
const contratos = ref<ContratoResponseDTO[]>([])
const ordens = ref<OrdemServicoResponseDTO[]>([])
const tecnicoMap = ref<Record<number, string>>({})
const loading = ref(true)
const loadingContratos = ref(true)
const loadingOrdens = ref(true)
const erro = ref('')

const contratoExpandido = ref<number | null>(null)

const toggleContrato = (id: number) => {
  contratoExpandido.value = contratoExpandido.value === id ? null : id
}

const contratoStatusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  ATIVO:      { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Ativo'      },
  ENCERRADO:  { dot: 'bg-slate-500',   badge: 'bg-slate-500/15 text-slate-400 border-slate-500/30',       label: 'Encerrado'  },
  SUSPENSO:   { dot: 'bg-amber-500',   badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',       label: 'Suspenso'   },
  CANCELADO:  { dot: 'bg-red-500',     badge: 'bg-red-500/15 text-red-400 border-red-500/30',             label: 'Cancelado'  },
}

const ordemStatusConfig: Record<string, { badge: string; label: string }> = {
  AGUARDANDO:  { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',     label: 'Aguardando'  },
  AGENDADO:    { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',        label: 'Agendado'    },
  EM_EXECUCAO: { badge: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/30',  label: 'Em Execução' },
  CONCLUIDA:   { badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Concluída' },
  CANCELADA:   { badge: 'bg-red-500/15 text-red-400 border-red-500/30',           label: 'Cancelada'   },
}

const criticidadeConfig: Record<string, { badge: string }> = {
  CRITICA: { badge: 'bg-red-500/15 text-red-400 border-red-500/30'       },
  ALTA:    { badge: 'bg-orange-500/15 text-orange-400 border-orange-500/30' },
  MEDIA:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30' },
  BAIXA:   { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30'    },
}

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500', 'bg-emerald-500', 'bg-rose-500', 'bg-indigo-500']
  return colors[name.length % colors.length]
}

const formatDate = (dt?: string | null) => {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const formatDateTime = (dt?: string | null) => {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const isContratoVencendo = (dataFim?: string | null) => {
  if (!dataFim) return false
  const diff = new Date(dataFim).getTime() - Date.now()
  return diff > 0 && diff < 30 * 24 * 60 * 60 * 1000
}

const isContratoVencido = (dataFim?: string | null) => {
  if (!dataFim) return false
  return new Date(dataFim) < new Date()
}

const ordensPorContrato = computed(() => {
  const map: Record<number, OrdemServicoResponseDTO[]> = {}
  ordens.value.forEach((o: OrdemServicoResponseDTO) => {
    if (!map[o.codigoContrato]) map[o.codigoContrato] = []
    map[o.codigoContrato].push(o)
  })
  return map
})

const stats = computed(() => [
  {
    label: 'Total de Contratos',
    value: contratos.value.length,
    icon: FileText,
    color: 'text-blue-400',
    bg: 'bg-blue-500/10',
  },
  {
    label: 'Contratos Ativos',
    value: contratos.value.filter((c: ContratoResponseDTO) => c.status === 'ATIVO').length,
    icon: CheckCircle2,
    color: 'text-emerald-400',
    bg: 'bg-emerald-500/10',
  },
  {
    label: 'Total de Ordens',
    value: ordens.value.length,
    icon: ClipboardList,
    color: 'text-indigo-400',
    bg: 'bg-indigo-500/10',
  },
  {
    label: 'Ordens Abertas',
    value: ordens.value.filter((o: OrdemServicoResponseDTO) => o.status === 'AGUARDANDO' || o.status === 'AGENDADO' || o.status === 'EM_EXECUCAO').length,
    icon: AlertTriangle,
    color: 'text-amber-400',
    bg: 'bg-amber-500/10',
  },
])

onMounted(async () => {
  try {
    cliente.value = await clienteService.buscarPorId(clienteId)
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar cliente'
  } finally {
    loading.value = false
  }

  try {
    contratos.value = await contratoService.buscarPorCliente(clienteId)
  } catch {
    contratos.value = []
  } finally {
    loadingContratos.value = false
  }

  try {
    ordens.value = await ordemServicoService.buscarPorCliente(clienteId)
    const tecnicoIds: number[] = Array.from(
      new Set(ordens.value.filter((o: OrdemServicoResponseDTO) => o.codigoFuncionario).map((o: OrdemServicoResponseDTO) => o.codigoFuncionario as number))
    )
    const tecnicos = await Promise.all(tecnicoIds.map((id: number) => tecnicoService.buscarPorId(id).catch(() => null)))
    tecnicos.forEach((t, i) => {
      if (t) tecnicoMap.value[tecnicoIds[i]] = t.nome
    })
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
        @click="router.push('/clientes')"
      >
        <ArrowLeft class="size-4" />
        Clientes
      </button>
      <ChevronRight class="size-3.5" />
      <span class="text-foreground font-medium">{{ cliente?.nomeEmpresa ?? 'Detalhes' }}</span>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="flex items-center justify-center py-24 text-muted-foreground gap-3">
      <Loader2 class="size-5 animate-spin" /> Carregando dados do cliente...
    </div>
    <div v-else-if="erro" class="py-24 text-center text-red-400">{{ erro }}</div>

    <template v-else-if="cliente">

      <!-- Hero -->
      <div class="rounded-xl border border-border bg-sidebar p-6 flex flex-col sm:flex-row items-start sm:items-center gap-5">
        <div :class="['flex items-center justify-center size-16 rounded-xl text-2xl font-bold text-white shrink-0', getAvatarColor(cliente.nomeEmpresa)]">
          {{ cliente.nomeEmpresa.substring(0, 2).toUpperCase() }}
        </div>
        <div class="flex-1 min-w-0">
          <h1 class="text-2xl font-bold text-foreground truncate">{{ cliente.nomeEmpresa }}</h1>
          <p class="text-sm text-muted-foreground mt-0.5">{{ cliente.documento || 'Documento não informado' }}</p>
          <div class="flex flex-wrap items-center gap-2 mt-2">
            <span
              :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', cliente.ativo ? 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30' : 'bg-red-500/15 text-red-400 border-red-500/30']"
            >
              <span :class="['size-1.5 rounded-full', cliente.ativo ? 'bg-emerald-500' : 'bg-red-500']" />
              {{ cliente.ativo ? 'Ativo' : 'Inativo' }}
            </span>
            <span v-if="cliente.classificacaoDistancia" class="text-xs text-muted-foreground border border-border rounded-full px-2.5 py-1">
              {{ cliente.classificacaoDistancia }}
            </span>
            <span v-if="cliente.internacional" class="text-xs text-muted-foreground border border-border rounded-full px-2.5 py-1 flex items-center gap-1">
              <Globe class="size-3" /> Internacional
            </span>
          </div>
        </div>
        <Button variant="outline" class="shrink-0" @click="router.push('/clientes')">
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

        <!-- Left: Info cards -->
        <div class="xl:col-span-1 space-y-5">

          <!-- Dados da empresa -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Building2 class="size-4" /> Dados da Empresa
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <Building2 class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Razão Social</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.nomeEmpresa }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <FileText class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">CNPJ / Documento</p>
                  <p class="text-sm font-mono text-foreground mt-0.5">{{ cliente.documento || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Clock class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Fuso Horário</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.fusoHorario || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Calendar class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Data de Cadastro</p>
                  <p class="text-sm text-foreground mt-0.5">{{ formatDateTime(cliente.dataCadastro) }}</p>
                </div>
              </div>
              <div v-if="cliente.observacao" class="flex items-start gap-3">
                <FileText class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Observação</p>
                  <p class="text-sm text-muted-foreground mt-0.5 leading-relaxed">{{ cliente.observacao }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Contato -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <User class="size-4" /> Contato
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <User class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Responsável</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.nomeResponsavel || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Mail class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">E-mail</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.emailContato || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Phone class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Telefone</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.telefoneContato || '—' }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Endereço -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <MapPin class="size-4" /> Endereço
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <MapPin class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Logradouro</p>
                  <p class="text-sm text-foreground mt-0.5">
                    {{ [cliente.rua, cliente.numero].filter(Boolean).join(', ') || '—' }}
                  </p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Building2 class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Cidade / Estado</p>
                  <p class="text-sm text-foreground mt-0.5">
                    {{ [cliente.cidade, cliente.estadoRegiao].filter(Boolean).join(' — ') || '—' }}
                  </p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Globe class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">País</p>
                  <p class="text-sm text-foreground mt-0.5">{{ cliente.pais || '—' }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

        </div>

        <!-- Right: Contratos + Ordens -->
        <div class="xl:col-span-2 space-y-6">

          <!-- Contratos -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <div class="flex items-center justify-between">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <FileText class="size-4" /> Contratos
                </CardTitle>
                <span class="text-xs text-muted-foreground">
                  {{ contratos.length }} {{ contratos.length === 1 ? 'contrato' : 'contratos' }}
                </span>
              </div>
            </CardHeader>
            <CardContent class="p-0">

              <div v-if="loadingContratos" class="flex items-center justify-center py-10 text-muted-foreground gap-2">
                <Loader2 class="size-4 animate-spin" /> Carregando contratos...
              </div>

              <div v-else-if="contratos.length === 0" class="py-10 text-center text-muted-foreground/50 text-sm">
                Nenhum contrato vinculado a este cliente
              </div>

              <div v-else class="divide-y divide-border">
                <div v-for="ct in contratos" :key="ct.codigo">

                  <!-- Contrato header (clicável) -->
                  <button
                    class="w-full flex items-center gap-4 px-5 py-4 hover:bg-muted/20 transition-colors text-left"
                    @click="toggleContrato(ct.codigo)"
                  >
                    <div class="flex-1 min-w-0 grid grid-cols-1 sm:grid-cols-3 gap-x-6 gap-y-1">
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Contrato</p>
                        <p class="text-sm font-mono font-semibold text-foreground">#{{ ct.codigo }}</p>
                      </div>
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Vigência</p>
                        <p class="text-sm text-foreground">
                          {{ formatDate(ct.dataInicio) }} → {{ formatDate(ct.dataFim) }}
                        </p>
                      </div>
                      <div class="flex items-center gap-2">
                        <span
                          :class="['inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', contratoStatusConfig[ct.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                        >
                          <span :class="['size-1.5 rounded-full', contratoStatusConfig[ct.status]?.dot ?? 'bg-slate-400']" />
                          {{ contratoStatusConfig[ct.status]?.label ?? ct.status }}
                        </span>
                        <span
                          v-if="isContratoVencendo(ct.dataFim)"
                          class="inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold bg-amber-500/15 text-amber-400 border-amber-500/30"
                        >
                          Vencendo
                        </span>
                        <span
                          v-else-if="isContratoVencido(ct.dataFim)"
                          class="inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold bg-red-500/15 text-red-400 border-red-500/30"
                        >
                          Vencido
                        </span>
                      </div>
                    </div>
                    <div class="flex items-center gap-3 shrink-0">
                      <span class="text-[11px] text-muted-foreground">
                        {{ (ordensPorContrato[ct.codigo] ?? []).length }} ordem(s)
                      </span>
                      <component :is="contratoExpandido === ct.codigo ? ChevronUp : ChevronDown" class="size-4 text-muted-foreground" />
                    </div>
                  </button>

                  <!-- Contrato detalhes expandíveis -->
                  <div v-if="contratoExpandido === ct.codigo" class="border-t border-border bg-muted/5">

                    <!-- Info do contrato -->
                    <div class="px-5 py-4 grid grid-cols-2 sm:grid-cols-4 gap-4">
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Período Preventiva</p>
                        <p class="text-sm text-foreground mt-0.5">
                          {{ ct.periodoManutencaoPreventiva ? `${ct.periodoManutencaoPreventiva} dias` : '—' }}
                        </p>
                      </div>
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Venc. Preventiva</p>
                        <p class="text-sm text-foreground mt-0.5">{{ formatDate(ct.vencimentoManutencaoPreventiva) }}</p>
                      </div>
                      <div>
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Conexão à Internet</p>
                        <div class="flex items-center gap-1.5 mt-0.5">
                          <component :is="ct.conexaoInternet ? Wifi : WifiOff" :class="['size-3.5', ct.conexaoInternet ? 'text-emerald-400' : 'text-slate-500']" />
                          <span :class="['text-sm', ct.conexaoInternet ? 'text-emerald-400' : 'text-muted-foreground']">
                            {{ ct.conexaoInternet ? 'Sim' : 'Não' }}
                          </span>
                        </div>
                      </div>
                      <div v-if="ct.descricao">
                        <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Descrição</p>
                        <p class="text-sm text-muted-foreground mt-0.5 leading-relaxed">{{ ct.descricao }}</p>
                      </div>
                    </div>

                    <!-- Ordens do contrato -->
                    <div class="border-t border-border">
                      <div class="px-5 py-3 flex items-center justify-between">
                        <p class="text-[11px] font-bold uppercase tracking-wider text-muted-foreground flex items-center gap-1.5">
                          <ClipboardList class="size-3.5" />
                          Ordens de Manutenção deste Contrato
                        </p>
                        <span class="text-xs text-muted-foreground">
                          {{ (ordensPorContrato[ct.codigo] ?? []).length }} {{ (ordensPorContrato[ct.codigo] ?? []).length === 1 ? 'ordem' : 'ordens' }}
                        </span>
                      </div>

                      <div v-if="loadingOrdens" class="flex items-center justify-center py-6 text-muted-foreground gap-2 text-sm">
                        <Loader2 class="size-3.5 animate-spin" /> Carregando...
                      </div>

                      <div v-else-if="!(ordensPorContrato[ct.codigo] ?? []).length" class="px-5 pb-4 text-sm text-muted-foreground/50">
                        Nenhuma ordem vinculada a este contrato
                      </div>

                      <Table v-else class="text-xs">
                        <TableHeader>
                          <TableRow class="hover:bg-transparent border-border text-[10px] uppercase font-bold text-muted-foreground">
                            <TableHead class="pl-5 h-8 w-14">Nº</TableHead>
                            <TableHead class="h-8">Técnico</TableHead>
                            <TableHead class="h-8">Status</TableHead>
                            <TableHead class="h-8">Criticidade</TableHead>
                            <TableHead class="h-8">Abertura</TableHead>
                            <TableHead class="h-8 pr-5 w-16" />
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          <TableRow
                            v-for="o in ordensPorContrato[ct.codigo]"
                            :key="o.codigo"
                            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/5"
                          >
                            <TableCell class="pl-5 py-2">
                              <span class="font-mono font-semibold text-foreground">#{{ o.codigo }}</span>
                            </TableCell>
                            <TableCell class="py-2 text-muted-foreground">
                              {{ o.codigoFuncionario ? (tecnicoMap[o.codigoFuncionario] ?? `Téc. #${o.codigoFuncionario}`) : '—' }}
                            </TableCell>
                            <TableCell class="py-2">
                              <span :class="['inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', ordemStatusConfig[o.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']">
                                {{ ordemStatusConfig[o.status]?.label ?? o.status }}
                              </span>
                            </TableCell>
                            <TableCell class="py-2">
                              <span :class="['inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', criticidadeConfig[o.criticidade]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']">
                                {{ o.criticidade || '—' }}
                              </span>
                            </TableCell>
                            <TableCell class="py-2 text-muted-foreground">{{ formatDate(o.dataAbertura) }}</TableCell>
                            <TableCell class="py-2 pr-5 text-right">
                              <Button variant="ghost" size="sm" class="h-7 px-2 text-muted-foreground hover:text-foreground text-xs gap-1" @click="router.push(`/ordens/${o.codigo}`)">
                                Ver <ChevronRight class="size-3.5" />
                              </Button>
                            </TableCell>
                          </TableRow>
                        </TableBody>
                      </Table>
                    </div>
                  </div>

                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Todas as ordens -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3 border-b border-border">
              <div class="flex items-center justify-between">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <ClipboardList class="size-4" /> Todas as Ordens de Manutenção
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
                Nenhuma ordem de manutenção vinculada a este cliente
              </div>

              <Table v-else>
                <TableHeader>
                  <TableRow class="hover:bg-transparent border-border text-[10px] uppercase font-bold text-muted-foreground">
                    <TableHead class="pl-5 h-10 w-14">Nº</TableHead>
                    <TableHead class="h-10">Contrato</TableHead>
                    <TableHead class="h-10">Técnico</TableHead>
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
                    <TableCell class="py-3">
                      <span class="text-xs font-mono text-muted-foreground">#{{ o.codigoContrato }}</span>
                    </TableCell>
                    <TableCell class="py-3 text-sm text-foreground">
                      {{ o.codigoFuncionario ? (tecnicoMap[o.codigoFuncionario] ?? `Téc. #${o.codigoFuncionario}`) : '—' }}
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
