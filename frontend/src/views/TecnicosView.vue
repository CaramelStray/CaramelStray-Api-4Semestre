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
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

import {
  Download, UserPlus, Users, MapPin, AlertTriangle, ShieldCheck,
  Eye, Pencil, Search, MoreVertical,
} from 'lucide-vue-next'

/**
 * ESTADO GLOBAL E FILTRAGEM
 */
const searchQuery = ref('')

// Mapeamento de UI para os status dos técnicos
const statusMap = {
  'DISPONÍVEL': { dotClass: 'bg-emerald-500', textClass: 'text-foreground' },
  'EM CAMPO':   { dotClass: 'bg-blue-500',    textClass: 'text-foreground' },
  'FOLGA':      { dotClass: 'bg-slate-500',   textClass: 'text-foreground' },
} as Record<string, any>

// Mapeamento de UI para badges de certificação
// Cada certificação pode estar válida, expirando ou expirada
const certBadgeClass = (cert: { expirada?: boolean; expirando?: boolean }) => {
  if (cert.expirada)  return 'bg-red-500/20 text-red-400 border border-red-500/30'
  if (cert.expirando) return 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
  return 'bg-muted/40 text-muted-foreground border border-border'
}

// Gera cor de avatar baseada no comprimento do nome para consistência visual
const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500 text-white', 'bg-emerald-500 text-white', 'bg-rose-500 text-white', 'bg-indigo-500 text-white']
  return colors[name.length % colors.length]
}

/**
 * DADOS DE EXEMPLO (Mock Data)
 * Em produção, estes dados viriam de uma API
 */
const stats = [
  { label: 'Total de Técnicos', value: '14',  sub: '+2 este mês',          icon: Users,         color: 'text-blue-400'   },
  { label: 'Em Campo',          value: '05',  sub: 'Ordens em andamento',  icon: MapPin,         color: 'text-green-400'  },
  { label: 'Cert. Expirando',   value: '03',  sub: 'Nos próximos 30 dias', icon: AlertTriangle,  color: 'text-red-400'    },
  { label: 'Disponíveis',       value: '09',  sub: 'Prontos para acionamento', icon: ShieldCheck, color: 'text-purple-400' },
]

const tecnicos = ref([
  {
    nome: 'Ricardo Ferreira',
    email: 'ricardoferreira@gmail.com',
    especialidade: 'Eletromecânica Sênior',
    certificacoes: [
      { nome: 'NR-10', validade: '12/2025' },
      { nome: 'NR-35', validade: '08/2024' },
    ],
    status: 'DISPONÍVEL',
    disponibilidade: 'Imediata',
    detalhe: 'Base: Unidade-01',
  },
  {
    nome: 'André Luiz Santos',
    email: 'andreluiz@gmail.com',
    especialidade: 'Instrumentação',
    certificacoes: [
      { nome: 'NR-10', validade: '—', expirada: true },
      { nome: 'SEP',   validade: '05/2026' },
    ],
    status: 'EM CAMPO',
    disponibilidade: '18/06 – 17:00',
    detalhe: 'OS: #992612',
  },
  {
    nome: 'Mariana Costa',
    email: 'marianacosta@gmail.com',
    especialidade: 'Telecom e Redes',
    certificacoes: [
      { nome: 'NR-35', validade: '11/2025' },
    ],
    status: 'FOLGA',
    disponibilidade: '20/06 – 08:00',
    detalhe: 'Férias/Descanso',
  },
  {
    nome: 'Gabriel Souza',
    email: 'gabrielsouza@gmail.com',
    especialidade: 'Manutenção de Turbinas',
    certificacoes: [
      { nome: 'NR-10', validade: '—' },
      { nome: 'NR-35', validade: 'Expira em 20d', expirando: true },
    ],
    status: 'DISPONÍVEL',
    disponibilidade: 'Imediata',
    detalhe: 'Base: Unidade-04',
  },
])

// Lógica de busca reativa: filtra por nome, especialidade ou ID
const filteredTecnicos = computed(() => {
  return tecnicos.value.filter(t =>
    t.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    t.especialidade.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    t.id.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})
</script>

<template>
  <div class="p-6 space-y-6">

    <!-- Stats cards — mesmo padrão de Clientes -->
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

    <!-- Barra de busca + ações -->
    <div class="flex items-center justify-between gap-4 w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Buscar técnico..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button variant="outline" size="lg" class="h-12 font-bold uppercase text-[11px] px-6 border-border hover:bg-muted/20">
          <Download class="w-4 h-4 mr-2" /> Exportar Relatório
        </Button>
        <Button size="lg" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <UserPlus class="w-4 h-4 mr-2" /> Novo Técnico
        </Button>
      </div>
    </div>

    <!-- Tabela -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Técnicos cadastrados</h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Técnico</TableHead>
            <TableHead class="h-12">Especialidade</TableHead>
            <TableHead class="h-12">Certificações</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Disponibilidade</TableHead>
            <TableHead class="text-right pr-14 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="t in filteredTecnicos"
            :key="t.id"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <!-- Nome + avatar -->
            <TableCell class="pl-6 py-3">
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold', getAvatarColor(t.nome)]">
                  {{ t.nome.substring(0, 2).toUpperCase() }}
                </div>
                <div class="flex flex-col">
                  <span class="text-sm font-normal text-foreground">{{ t.nome }}</span>
                  <span class="text-[11px] text-muted-foreground font-mono">{{ t.email }}</span>
                </div>
              </div>
            </TableCell>

            <!-- Especialidade -->
            <TableCell class="text-sm font-normal text-foreground">{{ t.especialidade }}</TableCell>

            <!-- Certificações com tooltip de validade -->
            <TableCell>
              <div class="flex items-center gap-1.5 flex-wrap">
                <TooltipProvider v-for="cert in t.certificacoes" :key="cert.nome" :delay-duration="100">
                  <Tooltip>
                    <TooltipTrigger as-child>
                      <span
                        :class="['inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold uppercase cursor-default', certBadgeClass(cert)]"
                      >
                        {{ cert.nome }}
                      </span>
                    </TooltipTrigger>
                    <TooltipContent
                      side="top"
                      class="bg-[#e2e8f0] text-[#0f172a] border-none font-medium rounded-lg px-3 py-1.5 text-xs shadow-xl"
                    >
                      VAL: {{ cert.validade }}
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
              </div>
            </TableCell>

            <!-- Status -->
            <TableCell>
              <div class="flex items-center gap-2">
                <div class="size-2 rounded-full" :class="statusMap[t.status]?.dotClass" />
                <span class="text-sm" :class="statusMap[t.status]?.textClass">{{ t.status }}</span>
              </div>
            </TableCell>

            <!-- Disponibilidade com tooltip de detalhe -->
            <TableCell>
              <TooltipProvider :delay-duration="100">
                <Tooltip>
                  <TooltipTrigger as-child>
                    <span class="text-sm text-foreground hover:opacity-80 transition-opacity cursor-default font-normal">
                      {{ t.disponibilidade }}
                    </span>
                  </TooltipTrigger>
                  <TooltipContent
                    side="right"
                    class="bg-[#e2e8f0] text-[#0f172a] border-none font-medium rounded-lg px-3 py-1.5 text-xs shadow-xl"
                  >
                    {{ t.detalhe }}
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
            </TableCell>

            <!-- Ações -->
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

  </div>
</template>