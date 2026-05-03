<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import { ClipboardList, AlertTriangle, Clock, CheckCircle2, Search, FileDown } from 'lucide-vue-next'
import { manutencaoService, type ManutencaoRelatorioDTO } from '@/services/manutencaoService'

const busca = ref('')
const manutencoes = ref<ManutencaoRelatorioDTO[]>([])
const loading = ref(false)
const erro = ref('')

onMounted(async () => {
  loading.value = true
  try {
    manutencoes.value = await manutencaoService.listarRelatorio()
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
})

const hoje = new Date()
const limite = new Date()
limite.setDate(hoje.getDate() + 30)

const vencidas = computed(() =>
  manutencoes.value.filter((item: ManutencaoRelatorioDTO) => item.vencimento && new Date(item.vencimento) < hoje)
)

const proximas = computed(() =>
  manutencoes.value.filter((item: ManutencaoRelatorioDTO) => {
    if (!item.vencimento) return false
    const data = new Date(item.vencimento)
    return data >= hoje && data <= limite
  })
)

const concluidas = computed(() =>
  manutencoes.value.filter((item: ManutencaoRelatorioDTO) => item.status === 'CONCLUIDA')
)

const vencidasFiltradas = computed(() => filtrar(vencidas.value))
const proximasFiltradas = computed(() => filtrar(proximas.value))

function filtrar(lista: ManutencaoRelatorioDTO[]) {
  const termo = busca.value.toLowerCase()
  return lista.filter(item =>
    String(item.codigo).includes(termo) ||
    item.status.toLowerCase().includes(termo) ||
    item.criticidade.toLowerCase().includes(termo)
  )
}

function formatarData(data: string | null) {
  if (!data) return '—'
  return new Date(data).toLocaleDateString('pt-BR')
}

function formatCriticidade(c: string) {
  switch (c) {
    case 'CRITICA': return { label: 'Crítica', class: 'bg-red-500/10 text-red-500 border-red-500/30' }
    case 'ALTA':   return { label: 'Alta',    class: 'bg-orange-500/10 text-orange-500 border-orange-500/30' }
    case 'MEDIA':  return { label: 'Média',   class: 'bg-amber-500/10 text-amber-500 border-amber-500/30' }
    case 'BAIXA':  return { label: 'Baixa',   class: 'bg-blue-500/10 text-blue-500 border-blue-500/30' }
    default:       return { label: c || '—',  class: 'bg-muted/20 text-muted-foreground border-border' }
  }
}

function formatStatus(s: string) {
  switch (s) {
    case 'AGUARDANDO':   return { label: 'Aguardando',   class: 'bg-amber-500/15 text-amber-500 border-amber-500/20' }
    case 'ABERTA':       return { label: 'Aberta',       class: 'bg-amber-500/15 text-amber-500 border-amber-500/20' }
    case 'AGENDADO':     return { label: 'Agendado',     class: 'bg-blue-500/15 text-blue-500 border-blue-500/20' }
    case 'EM_EXECUCAO':  return { label: 'Em Execução',  class: 'bg-indigo-500/15 text-indigo-400 border-indigo-500/20' }
    case 'CONCLUIDA':
    case 'FINALIZADA':   return { label: 'Finalizada',   class: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/20' }
    case 'CANCELADA':    return { label: 'Cancelada',    class: 'bg-red-500/15 text-red-500 border-red-500/20' }
    default:             return { label: s || '—',       class: 'bg-muted/20 text-muted-foreground border-border' }
  }
}

function gerarPdfItem(item: ManutencaoRelatorioDTO) {
  const token = localStorage.getItem('token')
  const url = `http://localhost:8080/maquinas-historicos-manutencao/relatorio/pdf?codigoMaquinaContrato=${item.codigoMaquinaContrato ?? ''}`
  fetch(url, { headers: { Authorization: `Bearer ${token}` } })
    .then(res => res.blob())
    .then(blob => {
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `relatorio-manutencao-${item.codigo}.pdf`
      link.click()
    })
    .catch(() => alert('Erro ao gerar PDF'))
}

function gerarRelatorioGeral() {
  const token = localStorage.getItem('token')
  fetch('http://localhost:8080/maquinas-historicos-manutencao/relatorio/pdf', {
    headers: { Authorization: `Bearer ${token}` },
  })
    .then(res => {
      if (!res.ok) throw new Error('Erro ao gerar PDF')
      return res.blob()
    })
    .then(blob => {
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = 'relatorio-manutencoes.pdf'
      link.click()
    })
    .catch(() => alert('Erro ao gerar relatório PDF'))
}

const stats = computed(() => [
  {
    label: 'Total de Manutenções',
    value: manutencoes.value.length,
    sub: 'Cadastradas no relatório',
    icon: ClipboardList,
    color: 'text-blue-400',
  },
  {
    label: 'Vencidas',
    value: vencidas.value.length,
    sub: 'Prazo expirado',
    icon: AlertTriangle,
    color: 'text-red-400',
  },
  {
    label: 'Próximas do Vencimento',
    value: proximas.value.length,
    sub: 'Dentro dos próximos 30 dias',
    icon: Clock,
    color: 'text-amber-400',
  },
  {
    label: 'Concluídas',
    value: concluidas.value.length,
    sub: 'Finalizadas',
    icon: CheckCircle2,
    color: 'text-purple-400',
  },
])
</script>

<template>
  <div class="p-6 space-y-6">
    <!-- Stats cards -->
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

    <!-- Loading / error -->
    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <!-- Toolbar -->
    <div class="flex flex-col-reverse gap-3 sm:flex-row sm:items-center sm:justify-between w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="busca"
          placeholder="Buscar por código, status ou criticidade..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <Button
        size="lg"
        @click="gerarRelatorioGeral"
        class="h-12 font-bold uppercase text-[11px] px-6 bg-blue-600 hover:opacity-90 text-white border-none shadow-md shrink-0"
      >
        <FileDown class="w-4 h-4 mr-2" />
        Gerar PDF
      </Button>
    </div>

    <!-- Manutenções Vencidas -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5 flex items-center gap-2">
        <AlertTriangle class="w-4 h-4 text-red-400" />
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Manutenções Vencidas</h2>
      </div>
      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Código</TableHead>
            <TableHead class="h-12">Tipo Manutenção</TableHead>
            <TableHead class="h-12">Criticidade</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Vencimento</TableHead>
            <TableHead class="h-12">Observação</TableHead>
            <TableHead class="h-12 text-right pr-6">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow
            v-for="item in vencidasFiltradas"
            :key="item.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-medium text-foreground">#{{ item.codigo }}</TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ item.codigoTipoManutencao ?? '—' }}</TableCell>
            <TableCell>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider" :class="formatCriticidade(item.criticidade).class">
                {{ formatCriticidade(item.criticidade).label }}
              </span>
            </TableCell>
            <TableCell>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider" :class="formatStatus(item.status).class">
                {{ formatStatus(item.status).label }}
              </span>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ formatarData(item.vencimento) }}</TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ item.observacaoGeral ?? '—' }}</TableCell>
            <TableCell class="text-right pr-6">
              <Button variant="outline" size="sm" class="gap-1.5 border-border" @click="gerarPdfItem(item)">
                <FileDown class="w-3.5 h-3.5" />
                PDF
              </Button>
            </TableCell>
          </TableRow>
          <TableRow v-if="vencidasFiltradas.length === 0">
            <TableCell colspan="7" class="text-center text-muted-foreground py-10">
              Nenhuma manutenção vencida encontrada.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <!-- Próximas do vencimento -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5 flex items-center gap-2">
        <Clock class="w-4 h-4 text-amber-400" />
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Próximas do Vencimento — 30 dias</h2>
      </div>
      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Código</TableHead>
            <TableHead class="h-12">Tipo Manutenção</TableHead>
            <TableHead class="h-12">Criticidade</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Vencimento</TableHead>
            <TableHead class="h-12">Observação</TableHead>
            <TableHead class="h-12 text-right pr-6">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow
            v-for="item in proximasFiltradas"
            :key="item.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-medium text-foreground">#{{ item.codigo }}</TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ item.codigoTipoManutencao ?? '—' }}</TableCell>
            <TableCell>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider" :class="formatCriticidade(item.criticidade).class">
                {{ formatCriticidade(item.criticidade).label }}
              </span>
            </TableCell>
            <TableCell>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider" :class="formatStatus(item.status).class">
                {{ formatStatus(item.status).label }}
              </span>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ formatarData(item.vencimento) }}</TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ item.observacaoGeral ?? '—' }}</TableCell>
            <TableCell class="text-right pr-6">
              <Button variant="outline" size="sm" class="gap-1.5 border-border" @click="gerarPdfItem(item)">
                <FileDown class="w-3.5 h-3.5" />
                PDF
              </Button>
            </TableCell>
          </TableRow>
          <TableRow v-if="proximasFiltradas.length === 0">
            <TableCell colspan="7" class="text-center text-muted-foreground py-10">
              Nenhuma manutenção próxima encontrada.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  </div>
</template>
