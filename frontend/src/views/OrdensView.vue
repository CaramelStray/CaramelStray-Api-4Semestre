<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  ClipboardList, Clock, CheckCircle2, AlertTriangle, Search, Plus, Eye, Pencil, X, Wrench,
} from 'lucide-vue-next'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import { clienteService } from '@/services/clienteService'
import { tecnicoService } from '@/services/tecnicoService'
import OrdemServicoCadastroPopup from '@/components/ordemServico/OrdemServicoCadastroPopup.vue'

const router = useRouter()
const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingOrdem = ref<OrdemServicoResponseDTO | null>(null)
const searchQuery = ref('')
const ordens = ref<OrdemServicoResponseDTO[]>([])
const clienteMap = ref<Record<number, string>>({})
const tecnicoMap = ref<Record<number, string>>({})
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
    label: 'Finalizadas',
    value: ordens.value.filter(o => o.status === 'CONCLUIDA' || o.status === 'FINALIZADA').length.toString(),
    sub: 'Ordens fechadas',
    icon: CheckCircle2,
    color: 'text-purple-400',
  },
])

const ordensInstalacaoPendentes = computed(() =>
  ordens.value.filter(o => o.tipoOrdem === 'INSTALACAO' && o.status === 'ABERTA')
)

const filteredOrdens = computed(() => {
  const filtered = ordens.value.filter(o => {
    if (o.tipoOrdem === 'INSTALACAO' && o.status === 'ABERTA') return false
    const nomeCliente = clienteMap.value[o.codigoCliente]?.toLowerCase() ?? ''
    const nomeTecnico = o.codigoFuncionario
      ? tecnicoMap.value[o.codigoFuncionario]?.toLowerCase() ?? ''
      : ''
    const q = searchQuery.value.toLowerCase()
    return (
      o.codigo.toString().includes(q) ||
      o.status?.toLowerCase().includes(q) ||
      o.criticidade?.toLowerCase().includes(q) ||
      nomeCliente.includes(q) ||
      nomeTecnico.includes(q)
    )
  })

  const criticidadeWeight = (c: string) => {
    switch (c) {
      case 'CRITICA': return 4
      case 'ALTA': return 3
      case 'MEDIA': return 2
      case 'BAIXA': return 1
      default: return 0
    }
  }

  const isFinalizada = (s: string) => s === 'CONCLUIDA' || s === 'FINALIZADA' || s === 'CANCELADA'

  return filtered.sort((a, b) => {
    const aFin = isFinalizada(a.status) ? 1 : 0
    const bFin = isFinalizada(b.status) ? 1 : 0
    
    if (aFin !== bFin) return aFin - bFin

    const aCrit = criticidadeWeight(a.criticidade)
    const bCrit = criticidadeWeight(b.criticidade)
    if (aCrit !== bCrit) return bCrit - aCrit

    return (b.codigo || 0) - (a.codigo || 0)
  })
})

const formatStatus = (s: string) => {
  switch(s) {
    case 'AGUARDANDO': return { label: 'Aguardando', class: 'bg-amber-500/15 text-amber-500 border-amber-500/20' }
    case 'AGENDADO': return { label: 'Agendado', class: 'bg-blue-500/15 text-blue-500 border-blue-500/20' }
    case 'EM_EXECUCAO': return { label: 'Em Execução', class: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/20' }
    case 'CONCLUIDA':
    case 'FINALIZADA': return { label: 'Finalizada', class: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/20' }
    case 'CANCELADA': return { label: 'Cancelada', class: 'bg-red-500/15 text-red-500 border-red-500/20' }
    default: return { label: s || '—', class: 'bg-muted/20 text-muted-foreground border-border' }
  }
}

const formatCriticidade = (c: string) => {
  switch(c) {
    case 'CRITICA': return { label: 'Crítica', class: 'bg-red-500/10 text-red-500 border-red-500/30' }
    case 'ALTA': return { label: 'Alta', class: 'bg-orange-500/10 text-orange-500 border-orange-500/30' }
    case 'MEDIA': return { label: 'Média', class: 'bg-amber-500/10 text-amber-500 border-amber-500/30' }
    case 'BAIXA': return { label: 'Baixa', class: 'bg-blue-500/10 text-blue-500 border-blue-500/30' }
    default: return { label: c || '—', class: 'bg-muted/20 text-muted-foreground border-border' }
  }
}

const formatDate = (d: string) =>
  d ? new Date(d).toLocaleDateString('pt-BR') : '—'

async function carregarOrdens() {
  loading.value = true
  erro.value = ''
  try {
    const lista = await ordemServicoService.listar()
    ordens.value = lista

    const clienteIds = [...new Set(lista.map(o => o.codigoCliente))]
    const tecnicoIds = [...new Set(lista.map(o => o.codigoFuncionario).filter(Boolean) as number[])]

    const [clientes, tecnicos] = await Promise.all([
      Promise.all(clienteIds.map(id => clienteService.buscarPorId(id))),
      Promise.all(tecnicoIds.map(id => tecnicoService.buscarPorId(id))),
    ])

    clienteMap.value = Object.fromEntries(clientes.map(c => [c.id, c.nomeEmpresa]))
    tecnicoMap.value = Object.fromEntries(tecnicos.map(t => [t.id, t.nome]))

  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
}

const abrirEdicaoOrdem = (ordem: OrdemServicoResponseDTO) => {
  editingOrdem.value = ordem
  isEditOpen.value = true
}

onMounted(carregarOrdens)
</script>

<template>
  <div class="p-6 space-y-6">

    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <!-- Stats -->
    <div :class="['grid gap-4 xl:grid-cols-4', stats.length > 1 ? 'grid-cols-2' : 'grid-cols-1']">
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
    <div class="flex flex-col-reverse gap-3 sm:flex-row sm:items-center sm:justify-between w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Buscar por código, cliente, técnico, status ou criticidade..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <Button
        size="lg"
        @click="isCadastroOpen = true"
        class="h-12 font-bold uppercase text-[11px] px-6 bg-blue-600 hover:opacity-90 text-white border-none shadow-md"
      >
        <Plus class="w-4 h-4 mr-2" /> Nova Ordem
      </Button>
    </div>

    <!-- Ordens de Instalação Pendentes -->
    <div v-if="ordensInstalacaoPendentes.length > 0" class="rounded-xl overflow-hidden border-2 border-amber-400 dark:border-amber-500/70">
      <!-- Header -->
      <div class="flex items-center gap-3 px-5 py-4 border-b-2 border-amber-300 dark:border-amber-500/50 bg-amber-200 dark:bg-amber-500/30">
        <AlertTriangle class="w-5 h-5 text-amber-700 dark:text-amber-300 shrink-0 animate-pulse" />
        <div class="flex-1">
          <p class="text-sm font-bold text-amber-900 dark:text-amber-200">Ordens de Instalação Pendentes</p>
          <p class="text-xs text-amber-700 dark:text-amber-300/80">Estas ordens foram geradas automaticamente ao criar o contrato e precisam ser preenchidas.</p>
        </div>
        <span class="inline-flex items-center text-[11px] font-bold border uppercase tracking-wider px-2.5 py-1 rounded-full bg-amber-500 text-black dark:bg-amber-400/30 dark:text-amber-200 border-amber-600 dark:border-amber-400/60">
          {{ ordensInstalacaoPendentes.length }} pendente{{ ordensInstalacaoPendentes.length > 1 ? 's' : '' }}
        </span>
      </div>
      <!-- Body -->
      <div class="divide-y divide-amber-200 dark:divide-amber-500/20 bg-amber-100 dark:bg-amber-500/10">
        <div
          v-for="o in ordensInstalacaoPendentes"
          :key="o.codigo"
          class="flex items-center gap-4 px-5 py-3 hover:bg-amber-200 dark:hover:bg-amber-500/20 transition-colors"
        >
          <Wrench class="w-4 h-4 text-amber-700 dark:text-amber-300 shrink-0" />
          <span class="font-mono text-sm font-medium text-amber-950 dark:text-amber-100 w-12">#{{ o.codigo }}</span>
          <span class="text-sm text-amber-900 dark:text-amber-200/90 flex-1 font-medium">{{ clienteMap[o.codigoCliente] ?? '—' }}</span>
          <span class="inline-flex items-center gap-1.5 text-[11px] font-bold border uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-amber-200 text-amber-900 dark:bg-amber-400/20 dark:text-amber-200 border-amber-400 dark:border-amber-400/50">
            <span class="w-1.5 h-1.5 rounded-full bg-amber-600 dark:bg-amber-300 opacity-90"></span>
            Instalação · Aguardando
          </span>
          <div class="flex items-center gap-1 shrink-0">
            <Button variant="ghost" size="icon" class="h-8 w-8 text-amber-700 dark:text-amber-300 hover:text-amber-900 hover:bg-amber-200 dark:hover:text-amber-100 dark:hover:bg-amber-500/20 transition-colors" @click="router.push(`/ordens/${o.codigo}`)">
              <Eye class="size-4" />
            </Button>
            <Button variant="ghost" size="icon" class="h-8 w-8 text-amber-700 dark:text-amber-300 hover:text-amber-900 hover:bg-amber-200 dark:hover:text-amber-100 dark:hover:bg-amber-500/20" @click="abrirEdicaoOrdem(o)">
              <Pencil class="size-4" />
            </Button>
          </div>
        </div>
      </div>
    </div>

    <!-- Tabela -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Ordens de serviço cadastradas</h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Cliente</TableHead>
            <TableHead class="h-12">Técnico</TableHead>
            <TableHead class="h-12">Criticidade</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Abertura</TableHead>
            <TableHead class="h-12">Agendamento</TableHead>
            <TableHead class="text-right pr-[54px] h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="o in filteredOrdens"
            :key="o.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3">
              <div class="flex items-center gap-2 flex-wrap">
                <span class="text-sm text-foreground font-medium">{{ clienteMap[o.codigoCliente] ?? '—' }}</span>
                <span
                  v-if="o.tipoOrdem === 'INSTALACAO'"
                  class="inline-flex items-center gap-1 text-[10px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded bg-blue-500/15 border border-blue-500/30 text-blue-700 dark:text-blue-400"
                >
                  <Wrench class="size-2.5" /> Instalação
                </span>
                <span
                  v-else-if="o.tipoOrdem === 'MANUTENCAO_PREVENTIVA'"
                  class="inline-flex items-center gap-1 text-[10px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded bg-emerald-500/15 border border-emerald-500/30 text-emerald-700 dark:text-emerald-400"
                >
                  <Wrench class="size-2.5" /> Prev.
                </span>
                <span
                  v-else-if="o.tipoOrdem === 'MANUTENCAO_CORRETIVA'"
                  class="inline-flex items-center gap-1 text-[10px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded bg-red-500/15 border border-red-500/30 text-red-700 dark:text-red-400"
                >
                  <Wrench class="size-2.5" /> Corr.
                </span>
              </div>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ o.codigoFuncionario ? (tecnicoMap[o.codigoFuncionario] ?? '—') : '—' }}
            </TableCell>
            <TableCell>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider" :class="formatCriticidade(o.criticidade).class">
                {{ formatCriticidade(o.criticidade).label }}
              </span>
            </TableCell>
            <TableCell>
              <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider" :class="formatStatus(o.status).class">
                <span class="w-1.5 h-1.5 rounded-full bg-current opacity-80"></span>
                {{ formatStatus(o.status).label }}
              </span>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ formatDate(o.dataAbertura) }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ formatDate(o.dataAgendamento) }}
            </TableCell>
            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="router.push(`/ordens/${o.codigo}`)">
                  <Eye class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicaoOrdem(o)">
                  <Pencil class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <!-- Modais -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isCadastroOpen = false" />
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[70vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Nova Ordem de Serviço</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados abaixo para abrir uma nova ordem de serviço.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isCadastroOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <OrdemServicoCadastroPopup @fechar="isCadastroOpen = false" @success="carregarOrdens" />
            </div>
          </div>
        </div>
      </Transition>

      <Transition name="modal">
        <div v-if="isEditOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isEditOpen = false" />
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[70vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Editar Ordem de Serviço</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados da ordem selecionada.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isEditOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <OrdemServicoCadastroPopup :initialData="editingOrdem" @fechar="isEditOpen = false" @success="carregarOrdens" />
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>


  </div>
</template>

<style scoped>
.modal-enter-from,
.modal-leave-to { opacity: 0; }
.modal-enter-active .modal-content,
.modal-leave-active .modal-content { transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1); }
.modal-enter-from .modal-content,
.modal-leave-to .modal-content { transform: translateX(100vw); }
</style>