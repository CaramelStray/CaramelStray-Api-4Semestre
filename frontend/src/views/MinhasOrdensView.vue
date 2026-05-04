<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  CalendarDays,
  ClipboardList,
  Clock,
  Eye,
  Search,
  UserCheck,
  AlertTriangle,
  CheckCircle2,
} from 'lucide-vue-next'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

import {
  ordemServicoService,
  type TecnicosOrdensResponseDTO,
} from '@/services/ordemServicoService'

const router = useRouter()

const ordens = ref<TecnicosOrdensResponseDTO[]>([])
const searchQuery = ref('')
const loading = ref(false)
const erro = ref('')

const ordensFiltradas = computed(() => {
  const q = searchQuery.value.toLowerCase()

  return ordens.value.filter((ordem: TecnicosOrdensResponseDTO) =>
    ordem.codigo.toString().includes(q) ||
    ordem.nomeCliente?.toLowerCase().includes(q) ||
    ordem.status?.toLowerCase().includes(q) ||
    ordem.criticidade?.toLowerCase().includes(q) ||
    formatDate(ordem.dataAbertura).toLowerCase().includes(q) ||
    formatDate(ordem.dataAgendamento).toLowerCase().includes(q)
  )
})

const stats = computed(() => [
  {
    label: 'Minhas Ordens',
    value: ordens.value.length,
    sub: 'Ordens vinculadas ao técnico',
    icon: ClipboardList,
    color: 'text-blue-400',
  },
  {
    label: 'Agendadas',
    value: ordens.value.filter((o) => o.status === 'AGENDADO').length,
    sub: 'Com data de agendamento',
    icon: CalendarDays,
    color: 'text-amber-400',
  },
  {
    label: 'Em Execução',
    value: ordens.value.filter((o) => o.status === 'EM_EXECUCAO').length,
    sub: 'Atendimento em andamento',
    icon: Clock,
    color: 'text-green-400',
  },
  {
    label: 'Concluídas',
    value: ordens.value.filter((o) => o.status === 'CONCLUIDA').length,
    sub: 'Finalizadas pelo técnico',
    icon: CheckCircle2,
    color: 'text-purple-400',
  },
])

function statusClass(status?: string) {
  const classes: Record<string, string> = {
    AGUARDANDO: 'bg-amber-500',
    AGENDADO: 'bg-blue-400',
    EM_EXECUCAO: 'bg-blue-500',
    CONCLUIDA: 'bg-emerald-500',
    CANCELADA: 'bg-red-500',
  }

  return classes[status ?? ''] ?? 'bg-gray-500'
}

function criticidadeClass(criticidade?: string) {
  const classes: Record<string, string> = {
    CRITICA: 'text-red-400 font-semibold',
    ALTA: 'text-orange-400 font-semibold',
    MEDIA: 'text-amber-400',
    BAIXA: 'text-blue-400',
  }

  return classes[criticidade ?? ''] ?? 'text-muted-foreground'
}

function formatDate(data?: string) {
  if (!data) return '—'

  return new Date(data).toLocaleDateString('pt-BR')
}

async function carregarOrdensDoTecnico() {
  loading.value = true
  erro.value = ''

  try {
    ordens.value = await ordemServicoService.tecnicosOrdens()
  } catch (error: any) {
    erro.value = error?.message ?? 'Erro ao carregar ordens do técnico.'
  } finally {
    loading.value = false
  }
}

onMounted(carregarOrdensDoTecnico)
</script>

<template>
  <div class="p-6 space-y-6">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-foreground">
          Minhas Ordens
        </h1>
        <p class="text-sm text-muted-foreground mt-1">
          Visualize as ordens de serviço vinculadas ao técnico e suas datas.
        </p>
      </div>

      <div class="hidden md:flex items-center gap-2 text-sm text-muted-foreground">
        <UserCheck class="w-4 h-4 text-blue-400" />
        Minhas ordens
      </div>
    </div>

    <div :class="['grid gap-4 xl:grid-cols-4', stats.length > 1 ? 'grid-cols-2' : 'grid-cols-1']">
      <Card
        v-for="stat in stats"
        :key="stat.label"
        class="bg-sidebar border-border"
      >
        <CardHeader class="flex flex-row items-center justify-between pb-2">
          <CardTitle class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider">
            {{ stat.label }}
          </CardTitle>

          <component :is="stat.icon" class="w-4 h-4" :class="stat.color" />
        </CardHeader>

        <CardContent>
          <div class="text-3xl font-bold text-foreground">
            {{ stat.value }}
          </div>
          <p class="text-[10px] text-muted-foreground mt-1">
            {{ stat.sub }}
          </p>
        </CardContent>
      </Card>
    </div>

    <div class="flex items-center justify-between gap-4 w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Buscar por código, status, criticidade ou data..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
    </div>

    <div
      v-if="loading"
      class="rounded-md border border-border bg-sidebar p-10 text-center text-muted-foreground"
    >
      Carregando ordens do técnico...
    </div>

    <div
      v-else-if="erro"
      class="rounded-md border border-red-500/30 bg-red-500/10 p-10 text-center text-red-400"
    >
      <AlertTriangle class="w-8 h-8 mx-auto mb-3" />
      {{ erro }}
    </div>

    <div
      v-else
      class="rounded-md border border-border bg-sidebar overflow-hidden"
    >
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">
          Ordens atribuídas ao técnico
        </h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12"># Ordem</TableHead>
            <TableHead class="h-12">Cliente</TableHead>
            <TableHead class="h-12">Criticidade</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Data de abertura</TableHead>
            <TableHead class="h-12">Data de agendamento</TableHead>
            <TableHead class="text-right pr-6 h-12">Visualizar</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="ordem in ordensFiltradas"
            :key="ordem.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-mono text-sm font-medium text-foreground">
              #{{ ordem.codigo }}
            </TableCell>

            <TableCell class="text-sm text-foreground">
              {{ ordem.nomeCliente ?? '—' }}
            </TableCell>

            <TableCell>
              <span class="text-sm" :class="criticidadeClass(ordem.criticidade)">
                {{ ordem.criticidade ?? '—' }}
              </span>
            </TableCell>

            <TableCell>
              <div class="flex items-center gap-2">
                <div class="size-2 rounded-full" :class="statusClass(ordem.status)" />
                <span class="text-sm text-foreground">
                  {{ ordem.status ?? '—' }}
                </span>
              </div>
            </TableCell>

            <TableCell class="text-sm text-muted-foreground">
              {{ formatDate(ordem.dataAbertura) }}
            </TableCell>

            <TableCell class="text-sm text-muted-foreground">
              {{ formatDate(ordem.dataAgendamento) }}
            </TableCell>

            <TableCell class="text-right pr-6">
              <Button
                variant="ghost"
                size="icon"
                class="h-9 w-9 text-muted-foreground hover:text-white transition-colors"
                @click="router.push(`/minhas-ordens/${ordem.codigo}`)"
              >
                <Eye class="size-5" />
              </Button>
            </TableCell>
          </TableRow>

          <TableRow v-if="ordensFiltradas.length === 0">
            <TableCell colspan="7" class="py-12 text-center text-muted-foreground">
              Nenhuma ordem encontrada para este técnico.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  </div>
</template>
