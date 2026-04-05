<script setup lang="ts">
import { ref, computed } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'

import { 
  Download, Plus, Users, Cpu, AlertTriangle, TrendingUp,
  Pencil, Eye, Search,
  Info, XCircle, CheckCircle2 
} from 'lucide-vue-next'

import ContratoCadastroPopup from '@/components/contrato/ContratoCadastroPopup.vue'

/**
 * ESTADO GLOBAL E FILTRAGEM
 */
const searchQuery = ref('')
const showNovoContratoPopup = ref(false) // Controla a exibição do popup

// Mapeamento de UI para os níveis de status conforme a imagem
const criticidadeMap = {
  'EXPIRANDO': { icon: XCircle, class: 'bg-red-500/20 text-red-400 border-red-500/30' },
  'PENDENTE': { icon: Info, class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' },
  'ATIVO': { icon: CheckCircle2, class: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30' },
} as Record<string, any>

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500 text-white', 'bg-emerald-500 text-white', 'bg-rose-500 text-white', 'bg-indigo-500 text-white']
  const index = name.length % colors.length
  return colors[index]
}

/**
 * DADOS DE EXEMPLO (Mock Data)
 */
const stats = [
  { label: 'Contratos Ativos', value: '142', sub: '4 vencem em breve', icon: Users, color: 'text-blue-400' },
  { label: 'Valor Anual Total', value: 'R$1.8M', sub: '+52% esse ano', icon: Cpu, color: 'text-green-400' },
  { label: 'Alertas Críticos', value: '07', sub: 'Ordens atrasadas', icon: AlertTriangle, color: 'text-red-400' },
  { label: 'Contratos Ativos', value: '34', sub: '4 vencem em breve', icon: TrendingUp, color: 'text-purple-400' },
]

// Campo valorAnual substituído por emailResponsavel
const contratos = ref([
  { idContrato: 'MER-2024-089', cliente: 'Petrobras', emailResponsavel: 'contato@petrobras.com.br', dataInicio: '12/04/2024', dataFim: '12/04/2026', status: 'ATIVO' },
  { idContrato: 'NAV-2023-441', cliente: 'Vale S.A.', emailResponsavel: 'gestao@vale.com', dataInicio: '01/01/2023', dataFim: '03/04/2026', status: 'EXPIRANDO' },
  { idContrato: 'MER-2024-112', cliente: 'Embraer', emailResponsavel: 'suporte@embraer.com.br', dataInicio: '15/02/2024', dataFim: '15/02/2025', status: 'PENDENTE' },
])

// Filtra por ID do contrato ou Cliente
const filteredClientes = computed(() => {
  return contratos.value.filter(c => 
    c.cliente.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.idContrato.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})
</script>

<template>
  <div class="p-6 space-y-6">
    
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
        
        <Button @click="showNovoContratoPopup = true" size="lg" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <Plus class="w-4 h-4 mr-2" /> Novo Contrato
        </Button>
      </div>
    </div>

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
            v-for="c in filteredClientes" 
            :key="c.idContrato" 
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-mono text-sm font-normal text-foreground">
              {{ c.idContrato }}
            </TableCell>

            <TableCell class="py-3">
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold', getAvatarColor(c.cliente)]">
                  {{ c.cliente.substring(0, 2).toUpperCase() }}
                </div>
                <span class="text-sm font-normal text-foreground">{{ c.cliente }}</span>
              </div>
            </TableCell>

            <TableCell class="text-sm font-normal text-foreground">{{ c.emailResponsavel }}</TableCell>
            
            <TableCell class="text-sm font-normal text-foreground">{{ c.dataInicio }}</TableCell>
            
            <TableCell class="text-sm font-normal text-foreground">{{ c.dataFim }}</TableCell>

            <TableCell>
              <div v-if="criticidadeMap[c.status]" 
                class="inline-flex items-center gap-2 px-3 py-1 rounded-full border text-[10px] font-bold uppercase tracking-tight" 
                :class="criticidadeMap[c.status].class"
              >
                <component :is="criticidadeMap[c.status].icon" class="size-4" />
                {{ c.status }}
              </div>
            </TableCell>

            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors">
                  <Eye class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors">
                  <Pencil class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <ContratoCadastroPopup v-model:open="showNovoContratoPopup" />

  </div>
</template>