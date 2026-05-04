<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  ArrowLeft, User, Phone, Mail, MapPin, Briefcase, Shield, Clock,
  ClipboardList, CheckCircle2, AlertTriangle, Activity, Calendar,
  BadgeCheck, Star, Loader2, ChevronRight,
} from 'lucide-vue-next'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import { clienteService } from '@/services/clienteService'

const route = useRoute()
const router = useRouter()

const tecnicoId = Number(route.params.id)

const tecnico = ref<TecnicoResponseDTO | null>(null)
const ordens = ref<OrdemServicoResponseDTO[]>([])
const clienteMap = ref<Record<number, string>>({})
const loading = ref(true)
const loadingOrdens = ref(true)
const erro = ref('')

const statusConfig: Record<string, { dot: string; badge: string; label: string }> = {
  DISPONÍVEL:    { dot: 'bg-emerald-500', badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Disponível' },
  'EM CAMPO':    { dot: 'bg-blue-500',    badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',          label: 'Em Campo'   },
  FOLGA:         { dot: 'bg-slate-500',   badge: 'bg-slate-500/15 text-slate-400 border-slate-500/30',       label: 'Folga'      },
}

const ordemStatusConfig: Record<string, { badge: string; label: string }> = {
  AGUARDANDO:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30',     label: 'Aguardando'   },
  AGENDADO:     { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30',        label: 'Agendado'     },
  EM_EXECUCAO:  { badge: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/30',  label: 'Em Execução'  },
  CONCLUIDA:    { badge: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30', label: 'Concluída'  },
  CANCELADA:    { badge: 'bg-red-500/15 text-red-400 border-red-500/30',           label: 'Cancelada'    },
}

const criticidadeConfig: Record<string, { badge: string }> = {
  CRITICA: { badge: 'bg-red-500/15 text-red-400 border-red-500/30' },
  ALTA:    { badge: 'bg-orange-500/15 text-orange-400 border-orange-500/30' },
  MEDIA:   { badge: 'bg-amber-500/15 text-amber-400 border-amber-500/30' },
  BAIXA:   { badge: 'bg-blue-500/15 text-blue-400 border-blue-500/30' },
}

const nivelLabel = (nivel: number) => {
  const labels = ['', 'Iniciante', 'Básico', 'Intermediário', 'Avançado', 'Especialista']
  return labels[nivel] ?? `Nível ${nivel}`
}

const nivelColor = (nivel: number) => {
  const colors = ['', 'text-slate-400', 'text-blue-400', 'text-amber-400', 'text-orange-400', 'text-emerald-400']
  return colors[nivel] ?? 'text-muted-foreground'
}

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500', 'bg-emerald-500', 'bg-rose-500', 'bg-indigo-500']
  return colors[name.length % colors.length]
}

const formatDate = (dt?: string) => {
  if (!dt) return '—'
  const d = new Date(dt)
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const isExpired = (dt: string) => new Date(dt) < new Date()
const isExpiring = (dt: string) => {
  const diff = new Date(dt).getTime() - Date.now()
  return diff > 0 && diff < 30 * 24 * 60 * 60 * 1000
}

const certBadge = (dt: string) => {
  if (isExpired(dt))   return 'bg-red-500/15 text-red-400 border-red-500/30'
  if (isExpiring(dt))  return 'bg-amber-500/15 text-amber-400 border-amber-500/30'
  return 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30'
}

const certLabel = (dt: string) => {
  if (isExpired(dt))   return 'Expirada'
  if (isExpiring(dt))  return 'Expirando'
  return 'Válida'
}


const stats = computed(() => [
  {
    label: 'Total de Ordens',
    value: ordens.value.length,
    icon: ClipboardList,
    color: 'text-blue-400',
    bg: 'bg-blue-500/10',
  },
  {
    label: 'Concluídas',
    value: ordens.value.filter((o: OrdemServicoResponseDTO) => o.status === 'CONCLUIDA').length,
    icon: CheckCircle2,
    color: 'text-emerald-400',
    bg: 'bg-emerald-500/10',
  },
  {
    label: 'Em Execução',
    value: ordens.value.filter((o: OrdemServicoResponseDTO) => o.status === 'EM_EXECUCAO').length,
    icon: Activity,
    color: 'text-indigo-400',
    bg: 'bg-indigo-500/10',
  },
  {
    label: 'Aguardando',
    value: ordens.value.filter((o: OrdemServicoResponseDTO) => o.status === 'AGUARDANDO' || o.status === 'AGENDADO').length,
    icon: AlertTriangle,
    color: 'text-amber-400',
    bg: 'bg-amber-500/10',
  },
])

onMounted(async () => {
  try {
    tecnico.value = await tecnicoService.buscarPorId(tecnicoId)
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar técnico'
  } finally {
    loading.value = false
  }

  try {
    ordens.value = await ordemServicoService.buscarPorFuncionario(tecnicoId)
    const clienteIds: number[] = Array.from(new Set(ordens.value.map((o: OrdemServicoResponseDTO) => o.codigoCliente)))
    const clientes = await Promise.all(clienteIds.map((id: number) => clienteService.buscarPorId(id).catch(() => null)))
    clientes.forEach((c, i) => {
      if (c) clienteMap.value[clienteIds[i]] = c.nomeEmpresa
    })
  } catch {
    // ordens ficam vazias se não houver
  } finally {
    loadingOrdens.value = false
  }
})
</script>

<template>
  <div class="p-6 space-y-6">

    <!-- Breadcrumb / Back -->
    <div class="flex items-center gap-2 text-sm text-muted-foreground">
      <button
        class="flex items-center gap-1.5 hover:text-foreground transition-colors cursor-pointer"
        @click="router.push('/tecnicos')"
      >
        <ArrowLeft class="size-4" />
        Técnicos
      </button>
      <ChevronRight class="size-3.5" />
      <span class="text-foreground font-medium">{{ tecnico?.nome ?? 'Detalhes' }}</span>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="flex items-center justify-center py-24 text-muted-foreground gap-3">
      <Loader2 class="size-5 animate-spin" />
      Carregando dados do técnico...
    </div>
    <div v-else-if="erro" class="py-24 text-center text-red-400">{{ erro }}</div>

    <template v-else-if="tecnico">

      <!-- Hero header -->
      <div class="rounded-xl border border-border bg-sidebar p-6 flex flex-col sm:flex-row items-start sm:items-center gap-5">
        <div :class="['flex items-center justify-center size-16 rounded-full text-2xl font-bold text-white shrink-0', getAvatarColor(tecnico.nome)]">
          {{ tecnico.nome.substring(0, 2).toUpperCase() }}
        </div>
        <div class="flex-1 min-w-0">
          <h1 class="text-2xl font-bold text-foreground truncate">{{ tecnico.nome }}</h1>
          <p class="text-sm text-muted-foreground mt-0.5">{{ tecnico.cargo || 'Cargo não informado' }}</p>
          <div class="flex flex-wrap items-center gap-3 mt-2">
            <span
              v-if="tecnico.estado"
              :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[11px] font-semibold uppercase tracking-wide', statusConfig[tecnico.estado]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
            >
              <span :class="['size-1.5 rounded-full', statusConfig[tecnico.estado]?.dot ?? 'bg-slate-400']" />
              {{ statusConfig[tecnico.estado]?.label ?? tecnico.estado }}
            </span>
            <span v-if="tecnico.disponibilidade" class="text-xs text-muted-foreground border border-border rounded-full px-2.5 py-1">
              {{ tecnico.disponibilidade }}
            </span>
          </div>
        </div>
        <Button variant="outline" class="shrink-0" @click="router.push('/tecnicos')">
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

        <!-- Left column: info cards -->
        <div class="xl:col-span-1 space-y-5">

          <!-- Dados pessoais -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <User class="size-4" /> Dados Pessoais
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <User class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Nome completo</p>
                  <p class="text-sm text-foreground mt-0.5">{{ tecnico.nome }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <BadgeCheck class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">CPF</p>
                  <p class="text-sm font-mono text-foreground mt-0.5">{{ tecnico.cpf || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Mail class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">E-mail</p>
                  <p class="text-sm text-foreground mt-0.5">{{ tecnico.email || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Phone class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Telefone</p>
                  <p class="text-sm text-foreground mt-0.5">{{ tecnico.telefone || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Briefcase class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Cargo</p>
                  <p class="text-sm text-foreground mt-0.5">{{ tecnico.cargo || '—' }}</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Clock class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Disponibilidade</p>
                  <p class="text-sm text-foreground mt-0.5">{{ tecnico.disponibilidade || '—' }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Localização -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <MapPin class="size-4" /> Localização
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="flex items-start gap-3">
                <MapPin class="size-4 text-muted-foreground mt-0.5 shrink-0" />
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Estado / UF</p>
                  <p class="text-sm text-foreground mt-0.5">{{ tecnico.estado || '—' }}</p>
                </div>
              </div>
              <div class="grid grid-cols-2 gap-3">
                <div class="rounded-lg border border-border bg-muted/20 p-3">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Latitude</p>
                  <p class="text-sm font-mono text-foreground mt-1">
                    {{ tecnico.latitude != null ? tecnico.latitude : '—' }}
                  </p>
                </div>
                <div class="rounded-lg border border-border bg-muted/20 p-3">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Longitude</p>
                  <p class="text-sm font-mono text-foreground mt-1">
                    {{ tecnico.longitude != null ? tecnico.longitude : '—' }}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Habilidades / Certificações -->
          <Card class="bg-sidebar border-border">
            <CardHeader class="pb-3">
              <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                <Shield class="size-4" /> Certificações e Habilidades
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div v-if="tecnico.habilidades && tecnico.habilidades.length > 0" class="space-y-3">
                <div
                  v-for="hab in tecnico.habilidades"
                  :key="hab.habilidadeId"
                  class="rounded-lg border border-border bg-muted/20 p-3"
                >
                  <div class="flex items-start justify-between gap-2">
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-medium text-foreground truncate">{{ hab.descricaoHabilidade }}</p>
                      <div class="flex items-center gap-2 mt-1.5">
                        <div class="flex items-center gap-0.5">
                          <Star
                            v-for="i in 5"
                            :key="i"
                            :class="['size-3', i <= hab.nivel ? nivelColor(hab.nivel) : 'text-muted-foreground/30']"
                            fill="currentColor"
                          />
                        </div>
                        <span :class="['text-[11px] font-medium', nivelColor(hab.nivel)]">
                          {{ nivelLabel(hab.nivel) }}
                        </span>
                      </div>
                    </div>
                    <div class="shrink-0 text-right">
                      <span :class="['inline-block px-2 py-0.5 rounded-full border text-[10px] font-semibold', certBadge(hab.dataValidade)]">
                        {{ certLabel(hab.dataValidade) }}
                      </span>
                      <p class="text-[10px] text-muted-foreground mt-1">
                        {{ formatDate(hab.dataValidade) }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="py-6 text-center text-muted-foreground/50 text-sm">
                Nenhuma certificação cadastrada
              </div>
            </CardContent>
          </Card>

        </div>

        <!-- Right column: ordens -->
        <div class="xl:col-span-2">
          <Card class="bg-sidebar border-border h-full">
            <CardHeader class="pb-3 border-b border-border">
              <div class="flex items-center justify-between">
                <CardTitle class="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  <ClipboardList class="size-4" /> Ordens de Manutenção
                </CardTitle>
                <span class="text-xs text-muted-foreground">
                  {{ ordens.length }} {{ ordens.length === 1 ? 'ordem' : 'ordens' }}
                </span>
              </div>
            </CardHeader>
            <CardContent class="p-0">

              <div v-if="loadingOrdens" class="flex items-center justify-center py-12 text-muted-foreground gap-2">
                <Loader2 class="size-4 animate-spin" /> Carregando ordens...
              </div>

              <div v-else-if="ordens.length === 0" class="py-12 text-center text-muted-foreground/50 text-sm">
                Nenhuma ordem de manutenção vinculada a este técnico
              </div>

              <Table v-else>
                <TableHeader>
                  <TableRow class="hover:bg-transparent border-border text-[10px] uppercase font-bold text-muted-foreground">
                    <TableHead class="pl-5 h-10 w-16">Nº</TableHead>
                    <TableHead class="h-10">Cliente</TableHead>
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
                      <span class="text-sm text-foreground">
                        {{ clienteMap[o.codigoCliente] ?? `Cliente #${o.codigoCliente}` }}
                      </span>
                    </TableCell>
                    <TableCell class="py-3">
                      <span
                        :class="['inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', ordemStatusConfig[o.status]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                      >
                        {{ ordemStatusConfig[o.status]?.label ?? o.status }}
                      </span>
                    </TableCell>
                    <TableCell class="py-3">
                      <span
                        :class="['inline-flex px-2 py-0.5 rounded-full border text-[10px] font-semibold uppercase tracking-wide', criticidadeConfig[o.criticidade]?.badge ?? 'bg-muted/30 text-muted-foreground border-border']"
                      >
                        {{ o.criticidade || '—' }}
                      </span>
                    </TableCell>
                    <TableCell class="py-3">
                      <div class="flex items-center gap-1.5 text-xs text-muted-foreground">
                        <Calendar class="size-3.5 shrink-0" />
                        {{ formatDate(o.dataAbertura) }}
                      </div>
                    </TableCell>
                    <TableCell class="py-3">
                      <span class="text-xs text-muted-foreground">{{ formatDate(o.dataAgendamento) }}</span>
                    </TableCell>
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
