<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  ClipboardList, Clock, CheckCircle2, AlertTriangle, Search, Plus, Eye, Pencil, X,
} from 'lucide-vue-next'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import { clienteService } from '@/services/clienteService'
import { tecnicoService } from '@/services/tecnicoService'
import OrdemServicoCadastroPopup from '@/components/ordemServico/OrdemServicoCadastroPopup.vue'

const isCadastroOpen = ref(false)
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
    label: 'Concluídas',
    value: ordens.value.filter(o => o.status === 'CONCLUIDA').length.toString(),
    sub: 'Finalizadas',
    icon: CheckCircle2,
    color: 'text-purple-400',
  },
])

const filteredOrdens = computed(() =>
  ordens.value.filter(o => {
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
)

const statusClass = (status: string) => ({
  'AGUARDANDO':  'bg-amber-500',
  'AGENDADO':    'bg-blue-400',
  'EM_EXECUCAO': 'bg-blue-500',
  'CONCLUIDA':   'bg-emerald-500',
  'CANCELADA':   'bg-red-500',
} as Record<string, string>)[status] ?? 'bg-gray-500'

const criticidadeClass = (c: string) => ({
  'CRITICA': 'text-red-400 font-semibold',
  'ALTA':    'text-orange-400 font-semibold',
  'MEDIA':   'text-amber-400',
  'BAIXA':   'text-blue-400',
} as Record<string, string>)[c] ?? 'text-muted-foreground'

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

onMounted(carregarOrdens)
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
            <TableHead class="h-12">Agendamento</TableHead>
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
              {{ clienteMap[o.codigoCliente] ?? '—' }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ o.codigoFuncionario ? (tecnicoMap[o.codigoFuncionario] ?? '—') : '—' }}
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

    <!-- Popup de Cadastro -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div
            class="absolute inset-0 bg-black/60 backdrop-blur-sm"
            @click="isCadastroOpen = false"
          />
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[70vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Nova Ordem de Serviço</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados abaixo para abrir uma nova ordem de serviço.</p>
              </div>
              <Button
                variant="ghost"
                size="icon"
                @click="isCadastroOpen = false"
                class="hover:bg-red-500/10 hover:text-red-500"
              >
                <X class="w-6 h-6" />
              </Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <OrdemServicoCadastroPopup
                @fechar="isCadastroOpen = false"
                @success="carregarOrdens"
              />
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