<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  ClipboardList, Clock, CheckCircle2, AlertTriangle, Search, Plus, Eye, Pencil,
} from 'lucide-vue-next'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'

const searchQuery = ref('')
const ordens = ref<OrdemServicoResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')

const stats = computed(() => [
  {
    label: 'Total de Ordens',
    value: ordens.value.length.toString(),
    sub: 'Cadastradas no sistema',
    icon: ClipboardList,
    color: 'text-blue-400',
  },
  {
    label: 'Aguardando',
    value: ordens.value.filter(o => o.status === 'AGUARDANDO').length.toString(),
    sub: 'Pendentes de execução',
    icon: Clock,
    color: 'text-amber-400',
  },
  {
    label: 'Em Execução',
    value: ordens.value.filter(o => o.status === 'EM_EXECUCAO').length.toString(),
    sub: 'Em andamento',
    icon: AlertTriangle,
    color: 'text-green-400',
  },
  {
    label: 'Concluídas',
    value: ordens.value.filter(o => o.status === 'CONCLUIDA').length.toString(),
    sub: 'Finalizadas',
    icon: CheckCircle2,
    color: 'text-purple-400',
  },
])

const filteredOrdens = computed(() =>
  ordens.value.filter(o =>
    o.codigo.toString().includes(searchQuery.value.toLowerCase()) ||
    o.status?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    o.criticidade?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
)

const statusClass = (status: string) => ({
  'AGUARDANDO': 'bg-amber-500',
  'EM_EXECUCAO': 'bg-blue-500',
  'CONCLUIDA': 'bg-emerald-500',
  'CANCELADA': 'bg-red-500',
} as Record<string, string>)[status] ?? 'bg-gray-500'

const criticidadeClass = (c: string) => ({
  'CRITICA': 'text-red-400 font-semibold',
  'ALTA': 'text-orange-400 font-semibold',
  'MEDIA': 'text-amber-400',
  'BAIXA': 'text-blue-400',
} as Record<string, string>)[c] ?? 'text-muted-foreground'

const formatDate = (d: string) =>
  d ? new Date(d).toLocaleDateString('pt-BR') : '—'

onMounted(async () => {
  loading.value = true
  try {
    ordens.value = await ordemServicoService.listar()
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="p-6 space-y-6">

    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <!-- Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
      <Card v-for="stat in stats" :key="stat.label" class="bg-sidebar border-border">
        <CardHeader class="flex flex-row items-center justify-between pb-2">
          <CardTitle class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider">
            {{ stat.label }}
          </CardTitle>
          <component :is="stat.icon" class="w-4 h-4" :class="stat.color" />
        </CardHeader>
        <CardContent>
          <div class="text-3xl font-bold text-foreground">{{ stat.value }}</div>
          <p class="text-[10px] text-muted-foreground mt-1">{{ stat.sub }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Busca + botão -->
    <div class="flex items-center justify-between gap-4 w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Buscar por código, status ou criticidade..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <Button size="lg" class="h-12 font-bold uppercase text-[11px] px-6 bg-blue-600 hover:bg-blue-700 text-white border-none">
        <Plus class="w-4 h-4 mr-2" /> Nova Ordem
      </Button>
    </div>

    <!-- Tabela -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Ordens de serviço cadastradas</h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12"># Ordem</TableHead>
            <TableHead class="h-12">Cliente</TableHead>
            <TableHead class="h-12">Técnico</TableHead>
            <TableHead class="h-12">Criticidade</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Abertura</TableHead>
            <TableHead class="text-right pr-14 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="o in filteredOrdens"
            :key="o.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-mono text-sm font-medium text-foreground">
              #{{ o.codigo }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ o.codigoCliente ?? '—' }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ o.codigoFuncionario ?? '—' }}
            </TableCell>
            <TableCell>
              <span class="text-sm" :class="criticidadeClass(o.criticidade)">
                {{ o.criticidade ?? '—' }}
              </span>
            </TableCell>
            <TableCell>
              <div class="flex items-center gap-2">
                <div class="size-2 rounded-full" :class="statusClass(o.status)" />
                <span class="text-sm text-foreground">{{ o.status ?? '—' }}</span>
              </div>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ formatDate(o.dataAbertura) }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ formatDate(o.dataAgendamento) }}
            </TableCell>
            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white">
                  <Eye class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white">
                  <Pencil class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

  </div>
</template>