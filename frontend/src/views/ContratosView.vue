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
  Download, Plus, Users, FileText, AlertTriangle, TrendingUp,
  Pencil, Eye, Search, Trash2,
  Info, XCircle, CheckCircle2,
} from 'lucide-vue-next'

import ContratoCadastroPopup from '@/components/contrato/ContratoCadastroPopup.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'

const router = useRouter()
const searchQuery = ref('')
const showNovoContratoPopup = ref(false)
const showEditContratoPopup = ref(false)
const editingContrato = ref<ContratoResponseDTO | null>(null)
const contratos = ref<ContratoResponseDTO[]>([])
const confirmOpen = ref(false)
const confirmNome = ref('')
const confirmId = ref<number | null>(null)
const deletando = ref(false)

// Mapa: codigoContrato → ClienteResponseDTO
const contratoClienteMap = ref<Record<number, ClienteResponseDTO>>({})

const loading = ref(false)
const erro = ref('')

const criticidadeMap = {
  EXPIRANDO: { icon: XCircle,      class: 'bg-red-500/20 text-red-400 border-red-500/30' },
  PENDENTE:  { icon: Info,         class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' },
  INATIVO:   { icon: Info,         class: 'bg-slate-500/20 text-slate-300 border-slate-500/30' },
  ATIVO:     { icon: CheckCircle2, class: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30' },
  EM_VIGOR:  { icon: CheckCircle2, class: 'bg-blue-500/20 text-blue-300 border-blue-500/30' },
} as Record<string, any>

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500 text-white', 'bg-emerald-500 text-white', 'bg-rose-500 text-white', 'bg-indigo-500 text-white']
  return colors[name.length % colors.length]
}

const formatDate = (value?: string | null) => {
  if (!value) return '—'
  const date = new Date(`${value}T00:00:00`)
  return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat('pt-BR').format(date)
}

const buildContratoId = (contrato: ContratoResponseDTO) =>
  contrato.descricao?.trim() || `CTR-${String(contrato.codigo).padStart(4, '0')}`

const getDiasParaVencimento = (dataFim?: string | null) => {
  if (!dataFim) return null
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const vencimento = new Date(`${dataFim}T00:00:00`)
  if (Number.isNaN(vencimento.getTime())) return null
  return Math.ceil((vencimento.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
}

const getStatusApresentacao = (contrato: ContratoResponseDTO) => {
  const status = contrato.status || 'ATIVO'
  const dias = getDiasParaVencimento(contrato.dataFim)
  if (status === 'INATIVO') return 'INATIVO'
  if (dias !== null && dias < 0) return 'PENDENTE'
  if (dias !== null && dias <= 30) return 'EXPIRANDO'
  return status
}

// Helpers para acessar o cliente de um contrato
const getNomeCliente = (contrato: ContratoResponseDTO) =>
  contratoClienteMap.value[contrato.codigo]?.nomeEmpresa ?? null

const getEmailCliente = (contrato: ContratoResponseDTO) =>
  contratoClienteMap.value[contrato.codigo]?.emailContato ?? null

const carregarContratos = async () => {
  loading.value = true
  erro.value = ''

  try {
    // Busca contratos e clientes em paralelo
    const [lista, clientes] = await Promise.all([
      contratoService.listar(),
      clienteService.listar(),
    ])

    contratos.value = lista

    // Monta o mapa: para cada cliente, itera seus contratos embutidos
    const mapa: Record<number, ClienteResponseDTO> = {}
    for (const cliente of clientes) {
      const contratosDoCliente = (cliente as any).contratos as Array<{ codigo: number }> | undefined
      if (contratosDoCliente) {
        for (const c of contratosDoCliente) {
          mapa[c.codigo] = cliente
        }
      }
    }
    contratoClienteMap.value = mapa

  } catch (e: any) {
    erro.value = e.message || 'Erro ao carregar contratos.'
  } finally {
    loading.value = false
  }
}

const abrirDetalhesContrato = (contrato: ContratoResponseDTO) => {
  router.push(`/contratos/${contrato.codigo}`)
}

const abrirEdicaoContrato = (contrato: ContratoResponseDTO) => {
  editingContrato.value = contrato
  showEditContratoPopup.value = true
}

const removerContrato = (codigo: number) => {
  confirmId.value = codigo
  confirmNome.value = buildContratoId(contratos.value.find(c => c.codigo === codigo)!)
  confirmOpen.value = true
}

const confirmarExclusao = async () => {
  if (confirmId.value === null) return
  deletando.value = true
  try {
    await contratoService.remover(confirmId.value)
    confirmOpen.value = false
    await carregarContratos()
  } catch (e: any) {
    alert('Erro ao remover: ' + (e.response?.data?.message || e.message))
  } finally {
    deletando.value = false
  }
}

onMounted(carregarContratos)

const filteredContratos = computed(() => {
  const query = searchQuery.value.toLowerCase()
  return contratos.value.filter((contrato) => {
    const idContrato    = buildContratoId(contrato).toLowerCase()
    const nomeCliente   = (getNomeCliente(contrato) ?? '').toLowerCase()
    const emailContato  = (getEmailCliente(contrato) ?? '').toLowerCase()
    return idContrato.includes(query) || nomeCliente.includes(query) || emailContato.includes(query)
  })
})

const expiringSoonCount  = computed(() => contratos.value.filter(c => getStatusApresentacao(c) === 'EXPIRANDO').length)
const activeCount        = computed(() => contratos.value.filter(c => ['ATIVO', 'EM_VIGOR'].includes(getStatusApresentacao(c))).length)
const pendingCount       = computed(() => contratos.value.filter(c => getStatusApresentacao(c) === 'PENDENTE').length)
const uniqueClientsCount = computed(() => new Set(Object.values(contratoClienteMap.value).map(c => c.id)).size)

const stats = computed(() => [
  {
    label: 'Total de Contratos',
    value: contratos.value.length.toString(),
    sub: `${uniqueClientsCount.value} clientes vinculados`,
    icon: FileText,
    color: 'text-blue-400',
  },
  {
    label: 'Contratos Ativos',
    value: activeCount.value.toString(),
    sub: `${expiringSoonCount.value} vencem em breve`,
    icon: TrendingUp,
    color: 'text-green-400',
  },
  {
    label: 'Vencendo em Breve',
    value: expiringSoonCount.value.toString(),
    sub: 'Prazo de atenção nos próximos 30 dias',
    icon: AlertTriangle,
    color: 'text-red-400',
  },
  {
    label: 'Clientes Atendidos',
    value: uniqueClientsCount.value.toString(),
    sub: `${pendingCount.value} contratos pendentes ou vencidos`,
    icon: Users,
    color: 'text-purple-400',
  },
])
</script>

<template>
  <div class="p-6 space-y-6">
    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro"    class="text-center py-12 text-red-400">{{ erro }}</div>

    <!-- Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
      <Card v-for="stat in stats" :key="stat.label" class="bg-sidebar border-border">
        <CardHeader class="flex flex-row items-center justify-between pb-2">
          <CardTitle class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider">{{ stat.label }}</CardTitle>
          <component :is="stat.icon" class="w-4 h-4" :class="stat.color" />
        </CardHeader>
        <CardContent>
          <div class="text-3xl font-bold text-foreground">{{ stat.value }}</div>
          <p class="text-[10px] text-muted-foreground mt-1">{{ stat.sub }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Busca + botões -->
    <div class="flex items-center justify-between gap-4 w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Buscar contrato ou cliente..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button variant="outline" size="lg" class="h-12 font-bold uppercase text-[11px] px-6 border-border hover:bg-muted/20">
          <Download class="w-4 h-4 mr-2" /> Exportar Relatório
        </Button>
        <Button @click="showNovoContratoPopup = true" size="lg" class="h-12 font-bold uppercase text-[11px] px-6 bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <Plus class="w-4 h-4 mr-2" /> Novo Contrato
        </Button>
      </div>
    </div>

    <!-- Tabela -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Contratos cadastrados por cliente</h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">ID do Contrato</TableHead>
            <TableHead class="h-12">Cliente Associado</TableHead>
            <TableHead class="h-12">E-mail do Responsável</TableHead>
            <TableHead class="h-12">Data Início</TableHead>
            <TableHead class="h-12">Data Fim</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="text-right pr-14 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="contrato in filteredContratos"
            :key="contrato.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-mono text-sm font-normal text-foreground">
              {{ buildContratoId(contrato) }}
            </TableCell>

            <TableCell class="py-3">
              <div class="flex items-center gap-3">
                <div
                  :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold',
                    getAvatarColor(getNomeCliente(contrato) ?? 'CT')]"
                >
                  {{ (getNomeCliente(contrato) ?? 'CT').substring(0, 2).toUpperCase() }}
                </div>
                <span class="text-sm font-normal text-foreground">
                  {{ getNomeCliente(contrato) ?? 'Cliente não informado' }}
                </span>
              </div>
            </TableCell>

            <TableCell class="text-sm font-normal text-foreground">
              {{ getEmailCliente(contrato) ?? '—' }}
            </TableCell>

            <TableCell class="text-sm font-normal text-foreground">{{ formatDate(contrato.dataInicio) }}</TableCell>
            <TableCell class="text-sm font-normal text-foreground">{{ formatDate(contrato.dataFim) }}</TableCell>

            <TableCell>
              <div
                v-if="criticidadeMap[getStatusApresentacao(contrato)]"
                class="inline-flex items-center gap-2 px-3 py-1 rounded-full border text-[10px] font-bold uppercase tracking-tight"
                :class="criticidadeMap[getStatusApresentacao(contrato)].class"
              >
                <component :is="criticidadeMap[getStatusApresentacao(contrato)].icon" class="size-4" />
                {{ getStatusApresentacao(contrato) }}
              </div>
              <span v-else class="text-sm text-foreground">{{ getStatusApresentacao(contrato) }}</span>
            </TableCell>

            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-blue-400 transition-colors" @click="abrirDetalhesContrato(contrato)">
                  <Eye class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicaoContrato(contrato)">
                  <Pencil class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-red-500 hover:bg-red-500/10 transition-colors" @click="removerContrato(contrato.codigo)">
                  <Trash2 class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>

          <TableRow v-if="filteredContratos.length === 0">
            <TableCell colspan="7" class="h-24 text-center text-muted-foreground">
              Nenhum contrato encontrado.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <ContratoCadastroPopup v-model:open="showNovoContratoPopup" @success="carregarContratos" />
    <ContratoCadastroPopup v-model:open="showEditContratoPopup" :initialData="editingContrato" @success="carregarContratos" />

    <ConfirmDeleteDialog
      v-model:open="confirmOpen"
      titulo="Excluir Contrato"
      :descricao="`Tem certeza que deseja excluir o contrato &quot;${confirmNome}&quot;? Esta ação não pode ser desfeita.`"
      :carregando="deletando"
      @confirmar="confirmarExclusao"
    />
  </div>
</template>