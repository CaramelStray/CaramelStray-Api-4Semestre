<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
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
  Eye, Pencil, Search, X, CheckCircle2
} from 'lucide-vue-next'

import { onMounted } from 'vue'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import TecnicoCadastro from '@/components/tecnicos/TecnicoCadastroPopup.vue'

const router = useRouter()
const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingTecnico = ref<TecnicoResponseDTO | null>(null)
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
    value: tecnicos.value.filter(t => t.estado === 'DISPONIVEL').length.toString(),
    sub: 'Prontos para acionamento',
    icon: ShieldCheck,
    color: 'text-purple-400',
  },
])

const tecnicos = ref<TecnicoResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')
const sucessoCadastro = ref('')
let sucessoTimeout: ReturnType<typeof setTimeout> | null = null

const mostrarSucessoCadastro = (mensagem: string) => {
  sucessoCadastro.value = mensagem

  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
  }

  sucessoTimeout = setTimeout(() => {
    sucessoCadastro.value = ''
    sucessoTimeout = null
  }, 4000)
}

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

onBeforeUnmount(() => {
  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
  }
})

const fecharSucessoCadastro = () => {
  sucessoCadastro.value = ''

  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
    sucessoTimeout = null
  }
}

const abrirEdicao = async (tecnico: TecnicoResponseDTO) => {
  try {
    const dadosCompletos = await tecnicoService.buscarPorId(tecnico.id)
    editingTecnico.value = dadosCompletos
  } catch {
    editingTecnico.value = tecnico
  }
  isEditOpen.value = true
}

const onEdicaoSucesso = async (tecnico: TecnicoResponseDTO) => {
  mostrarSucessoCadastro(`Técnico "${tecnico.nome}" atualizado com sucesso.`)
  await carregarTecnicos()
}

const onCadastroSucesso = async (tecnico: TecnicoResponseDTO) => {
  mostrarSucessoCadastro(`Técnico "${tecnico.nome}" cadastrado com sucesso.`)
  await carregarTecnicos()
}

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
    <Transition name="toast">
      <div
        v-if="sucessoCadastro"
        class="fixed top-6 right-6 z-[120] max-w-md rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 shadow-2xl backdrop-blur-md"
      >
        <div class="flex items-start gap-3">
          <div class="mt-0.5 rounded-full bg-emerald-500/20 p-1.5 text-emerald-300">
            <CheckCircle2 class="size-4" />
          </div>
          <div class="flex-1">
            <p class="text-sm font-semibold text-emerald-200">Cadastro concluído</p>
            <p class="mt-1 text-sm text-emerald-100/90">{{ sucessoCadastro }}</p>
          </div>
          <Button
            variant="ghost"
            size="icon"
            class="size-8 shrink-0 text-emerald-100/80 hover:bg-emerald-500/10 hover:text-emerald-50"
            @click="fecharSucessoCadastro"
          >
            <X class="size-4" />
          </Button>
        </div>
      </div>
    </Transition>

    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <div class="grid grid-cols-2 xl:grid-cols-4 gap-4">
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

    <div class="flex flex-col gap-4 sm:flex-row sm:items-center w-full">
      <div class="grid grid-cols-2 gap-4 w-full sm:w-auto sm:flex sm:gap-4 sm:shrink-0 sm:order-2">
        <Button variant="outline" size="lg" class="h-12 font-bold uppercase text-[11px] px-4 sm:px-6 border-border hover:bg-muted/20">
          <Download class="w-4 h-4 mr-2 shrink-0" /> Exportar Relatório
        </Button>
        <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-4 sm:px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <UserPlus class="w-4 h-4 mr-2 shrink-0" /> Novo Técnico
        </Button>
      </div>
      <div class="relative w-full sm:flex-1 sm:order-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input
          v-model="searchQuery"
          placeholder="Buscar técnico..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
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
            <TableHead class="h-12">Estado</TableHead>
            <TableHead class="h-12">Disponibilidade</TableHead>
            <TableHead class="text-right pr-6 h-12">Ações</TableHead>
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
              <div v-if="t.habilidades && t.habilidades.length > 0" class="flex flex-wrap gap-1">
                <span v-for="hab in t.habilidades" :key="hab.habilidadeId" class="px-2 py-1 rounded bg-blue-500/10 text-blue-400 border border-blue-500/20 text-[10px]">
                  {{ hab.descricaoHabilidade }}
                </span>
              </div>
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
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="router.push(`/tecnicos/${t.id}`)">
                  <Eye class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicao(t)">
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
              <TecnicoCadastro
                @fechar="isCadastroOpen = false"
                @cadastrado="onCadastroSucesso"
              />
            </div>
          </div>
        </div>
      </Transition>

      <Transition name="modal">
        <div v-if="isEditOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isEditOpen = false"></div>

          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[70vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Editar Técnico</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados do técnico selecionado.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isEditOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>

            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <TecnicoCadastro
                :initialData="editingTecnico"
                @fechar="isEditOpen = false"
                @cadastrado="onEdicaoSucesso"
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

.toast-enter-active,
.toast-leave-active {
  transition: all 0.25s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-12px) scale(0.98);
}
</style>
