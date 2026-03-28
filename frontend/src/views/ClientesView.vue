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
  Info, XCircle, CheckCircle2, X
} from 'lucide-vue-next'
import ClienteCadastro from '@/components/clientes/ClienteCadastro.vue'

const isCadastroOpen = ref(false)
const searchQuery = ref('')

const criticidadeMap = {
  'CRÍTICO': { icon: XCircle, class: 'bg-red-500/20 text-red-400 border-red-500/30' },
  'ALERTA': { icon: Info, class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' },
  'ÓTIMO': { icon: CheckCircle2, class: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30' },
} as Record<string, any>

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500 text-white', 'bg-emerald-500 text-white', 'bg-rose-500 text-white', 'bg-indigo-500 text-white']
  const index = name.length % colors.length
  return colors[index]
}

const stats = [
  { label: 'Total de Clientes', value: '38', sub: '+3 este mês', icon: Users, color: 'text-blue-400' },
  { label: 'Sistemas em Operação', value: '124', sub: 'Entre todos os clientes', icon: Cpu, color: 'text-green-400' },
  { label: 'Alertas Críticos', value: '07', sub: 'Ordens atrasadas', icon: AlertTriangle, color: 'text-red-400' },
  { label: 'Contratos Ativos', value: '34', sub: '4 vencem em breve', icon: TrendingUp, color: 'text-purple-400' },
]

const clientes = ref([
  { nome: 'Petrobras', cidade: 'Rio de Janeiro', estado: 'RJ', pais: 'Brasil', contrato: 'MER-2024-089', statusContrato: 'Ativo', proximaManutencao: '12/04/2026', criticidade: 'CRÍTICO', sistemas: 18 },
  { nome: 'Vale S.A.', cidade: 'Nova Lima', estado: 'MG', pais: 'Brasil', contrato: 'NAV-2023-441', statusContrato: 'Vence em breve', proximaManutencao: '03/04/2026', criticidade: 'ALERTA', sistemas: 9 },
  { nome: 'Embraer', cidade: 'São José dos Campos', estado: 'SP', pais: 'Brasil', contrato: 'MER-2024-112', statusContrato: 'Ativo', proximaManutencao: '28/04/2026', criticidade: 'ÓTIMO', sistemas: 14 },
  { nome: 'Raizen Energia', cidade: 'Piracicaba', estado: 'SP', pais: 'Brasil', contrato: 'MER-2023-001', statusContrato: 'Ativo', proximaManutencao: '15/05/2026', criticidade: 'ÓTIMO', sistemas: 22 },
  { nome: 'Suzano Papel', cidade: 'Mucuri', estado: 'BA', pais: 'Brasil', contrato: 'MER-2024-201', statusContrato: 'Vencido', proximaManutencao: '—', criticidade: 'CRÍTICO', sistemas: 6 },
])

const filteredClientes = computed(() => {
  return clientes.value.filter(c => 
    c.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.cidade.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.contrato.toLowerCase().includes(searchQuery.value.toLowerCase())
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
          placeholder="Buscar cliente..." 
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary" 
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button variant="outline" size="lg" class="h-12 font-bold uppercase text-[11px] px-6 border-border hover:bg-muted/20">
          <Download class="w-4 h-4 mr-2" /> Exportar Relatório
        </Button>
        
        <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <Plus class="w-4 h-4 mr-2" /> Novo Cliente
        </Button>
      </div>
    </div>

    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Clientes cadastrados por contrato</h2>
      </div>
      
      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Cliente</TableHead>
            <TableHead class="h-12">Localização</TableHead>
            <TableHead class="h-12">Sistemas</TableHead>
            <TableHead class="h-12">Contrato</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Próxima Manutenção</TableHead>
            <TableHead class="h-12">Criticidade</TableHead>
            <TableHead class="text-right pr-14 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>
        
        <TableBody>
          <TableRow 
            v-for="c in filteredClientes" 
            :key="c.nome" 
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3">
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold', getAvatarColor(c.nome)]">
                  {{ c.nome.substring(0, 2).toUpperCase() }}
                </div>
                <span class="text-sm font-normal text-foreground">{{ c.nome }}</span>
              </div>
            </TableCell>

            <TableCell>
              <TooltipProvider :delay-duration="100">
                <Tooltip>
                  <TooltipTrigger as-child>
                    <span class="text-sm text-foreground hover:opacity-80 transition-opacity cursor-default font-normal">
                      {{ c.cidade }}, {{ c.estado }}
                    </span>
                  </TooltipTrigger>
                  <TooltipContent 
                    side="right" 
                    class="bg-[#e2e8f0] text-[#0f172a] border-none font-medium rounded-lg px-3 py-1.5 text-xs shadow-xl"
                  >
                    {{ c.pais }}
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
            </TableCell>

            <TableCell class="text-sm font-normal text-foreground">{{ c.sistemas }} sistemas</TableCell>
            <TableCell class="font-mono text-sm font-normal text-foreground">{{ c.contrato }}</TableCell>

            <TableCell>
              <div class="flex items-center gap-2 text-foreground font-normal">
                <div class="size-2 rounded-full" :class="c.statusContrato === 'Ativo' ? 'bg-emerald-500' : c.statusContrato === 'Vence em breve' ? 'bg-amber-500' : 'bg-red-500'"></div>
                <span class="text-sm">{{ c.statusContrato }}</span>
              </div>
            </TableCell>

            <TableCell class="text-sm font-normal text-foreground">{{ c.proximaManutencao }}</TableCell>

            <TableCell>
              <div v-if="criticidadeMap[c.criticidade]" 
                class="inline-flex items-center gap-2 px-3 py-1 rounded-full border text-[10px] font-bold uppercase tracking-tight" 
                :class="criticidadeMap[c.criticidade].class"
              >
                <component :is="criticidadeMap[c.criticidade].icon" class="size-4" />
                {{ c.criticidade }}
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

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isCadastroOpen = false"></div>
          
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[70vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Novo Cliente</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados abaixo para cadastrar um novo cliente no sistema.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isCadastroOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>
            
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <ClienteCadastro @fechar="isCadastroOpen = false" />
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<style scoped>
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: translateX(100vw);
}
</style>