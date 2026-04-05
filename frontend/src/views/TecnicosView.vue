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
  Eye, Pencil, Search, MoreVertical, X
} from 'lucide-vue-next'

import { onMounted } from 'vue'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import TecnicoCadastro from '@/components/tecnicos/TecnicoCadastroPopup.vue'

/**
 * ESTADO GLOBAL E FILTRAGEM
 */
const isCadastroOpen = ref(false)
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

const stats = computed(() => [
  {
    label: 'Total de Técnicos',
    value: tecnicos.value.length.toString(),
    sub: 'Cadastrados no sistema',
    icon: Users,
    color: 'text-blue-400',
  },
  {
    label: 'Em Campo',
    value: tecnicos.value.filter(t => t.estado === 'EM CAMPO').length.toString(),
    sub: 'Ordens em andamento',
    icon: MapPin,
    color: 'text-green-400',
  },
  {
    label: 'Cert. Expirando',
    value: '0', 
    sub: 'Nos próximos 30 dias',
    icon: AlertTriangle,
    color: 'text-red-400',
  },
  {
    label: 'Disponíveis',
    value: tecnicos.value.filter(t => t.estado === 'DISPONÍVEL').length.toString(),
    sub: 'Prontos para acionamento',
    icon: ShieldCheck,
    color: 'text-purple-400',
  },
])

const tecnicos = ref<TecnicoResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')

const carregarTecnicos = async () => {
  loading.value = true
  try {
    tecnicos.value = await tecnicoService.listar()
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  carregarTecnicos()
})

// Lógica de busca reativa
const filteredTecnicos = computed(() => {
  return tecnicos.value.filter(t =>
    t.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    t.cargo?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    t.email?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

</script>

<template>
  <div class="p-6 space-y-6">

  <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
  <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

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
          placeholder="Buscar técnico..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button variant="outline" size="lg" class="h-12 font-bold uppercase text-[11px] px-6 border-border hover:bg-muted/20">
          <Download class="w-4 h-4 mr-2" /> Exportar Relatório
        </Button>
        <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <UserPlus class="w-4 h-4 mr-2" /> Novo Técnico
        </Button>
      </div>
    </div>

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

            <TableCell class="text-sm font-normal text-foreground">
              {{ t.cargo || 'Não informado' }}
            </TableCell>

            <TableCell class="text-sm font-normal text-muted-foreground">
              <span v-if="t.certificacao" class="px-2 py-1 rounded bg-blue-500/10 text-blue-400 border border-blue-500/20 text-[10px]">
                {{ t.certificacao }}
              </span>
              <span v-else class="text-muted-foreground/50">—</span>
            </TableCell>

            <TableCell>
              <div class="flex items-center gap-2">
                <div :class="['size-2 rounded-full', t.estado ? 'bg-emerald-500' : 'bg-slate-500']"></div>
                <span class="text-xs font-medium uppercase tracking-wider text-foreground">
                  {{ t.estado || 'Indisponível' }}
                </span>
              </div>
            </TableCell>

            <TableCell class="text-sm font-normal text-muted-foreground">
              {{ t.disponibilidade || 'Não informada' }}
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
                <h2 class="text-2xl font-bold tracking-tight">Novo Técnico</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados abaixo para cadastrar um novo técnico no sistema.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isCadastroOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>
            
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <TecnicoCadastro @fechar="isCadastroOpen = false" @cadastrado="carregarTecnicos" />
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