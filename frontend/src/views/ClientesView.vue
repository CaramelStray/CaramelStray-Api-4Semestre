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
  Download, Plus, Users, Cpu, AlertTriangle, TrendingUp,
  Pencil, Eye, Search,
  Info, XCircle, CheckCircle2, X
} from 'lucide-vue-next'
import ClienteCadastro from '@/components/clientes/ClienteCadastroPopup.vue'
import { onMounted } from 'vue'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'

const router = useRouter()
const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingCliente = ref<ClienteResponseDTO | null>(null)
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

const clientes = ref<ClienteResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')
const contratosPorCliente = ref<Record<number, ContratoResponseDTO[]>>({})
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


const stats = computed(() =>[
  { label: 'Total de Clientes',
    value: clientes.value.length.toString(),
    sub: 'Cadastrados no sistema',
    icon: Users, 
    color: 'text-blue-400' 
  },

  { label: 'Contratos Ativos',
    value: clientes.value.filter(c => c.ativo).length.toString(), 
    sub: 'Clientes ativos', 
    icon: TrendingUp, 
    color: 'text-purple-400' 
  },

  { label: 'Contratos Inativos', 
    value: clientes.value.filter(c => !c.ativo).length.toString(),
    sub: 'Fora de operação',
    icon: AlertTriangle, 
    color: 'text-green-400' 
  },

  { label: 'Alertas Críticos', 
    value: '—', 
    sub: 'Ordens atrasadas',
    icon: AlertTriangle, 
    color: 'text-red-400' 
  },

])

const filteredClientes = computed(() => {
  return clientes.value.filter(c => 
    c.nomeEmpresa.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.cidade?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.emailContato?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

onMounted(async () => {
  loading.value = true
  try {
    clientes.value = await clienteService.listar()

    // busca contratos de cada cliente em paralelo
    const resultados = await Promise.all(
      clientes.value.map(c =>
        contratoService.buscarPorCliente(c.id).catch(() => [])
      )
    )

    clientes.value.forEach((c, i) => {
      contratosPorCliente.value[c.id] = resultados[i] || []
    })
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
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

const abrirEdicao = async (cliente: ClienteResponseDTO) => {
  try {
    const dadosCompletos = await clienteService.buscarPorId(cliente.id)
    editingCliente.value = dadosCompletos
  } catch {
    editingCliente.value = cliente
  }
  isEditOpen.value = true
}

const onEdicaoSucesso = async (cliente: ClienteResponseDTO) => {
  mostrarSucessoCadastro(`Cliente "${cliente.nomeEmpresa}" atualizado com sucesso.`)
  loading.value = true
  try {
    clientes.value = await clienteService.listar()
    const resultados = await Promise.all(clientes.value.map(c => contratoService.buscarPorCliente(c.id).catch(() => [])))
    clientes.value.forEach((c, i) => { contratosPorCliente.value[c.id] = resultados[i] || [] })
  } catch (e: any) { erro.value = e.message } finally { loading.value = false }
}

const onCadastroSucesso = async (cliente: ClienteResponseDTO) => {
  mostrarSucessoCadastro(`Cliente "${cliente.nomeEmpresa}" cadastrado com sucesso.`)

  loading.value = true
  try {
    clientes.value = await clienteService.listar()

    const resultados = await Promise.all(
      clientes.value.map(c =>
        contratoService.buscarPorCliente(c.id).catch(() => [])
      )
    )

    clientes.value.forEach((c, i) => {
      contratosPorCliente.value[c.id] = resultados[i] || []
    })
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
}

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
    
    <div :class="['grid gap-4 xl:grid-cols-4', stats.length > 1 ? 'grid-cols-2' : 'grid-cols-1']">
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

    <div class="flex flex-col-reverse gap-3 sm:flex-row sm:items-center sm:justify-between w-full">
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
            <TableHead class="h-12">Alcance</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="text-right pr-6 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>
        
        <TableBody>
          <TableRow 
            v-for="c in filteredClientes" 
            :key="c.nomeEmpresa" 
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3">
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold', getAvatarColor(c.nomeEmpresa)]">
                  {{ c.nomeEmpresa.substring(0, 2).toUpperCase() }}
                </div>
                <span class="text-sm font-normal text-foreground">{{ c.nomeEmpresa }}</span>
              </div>
            </TableCell>

            <TableCell>
              <TooltipProvider :delay-duration="100">
                <Tooltip>
                  <TooltipTrigger as-child>
                    <span class="text-sm text-foreground hover:opacity-80 transition-opacity cursor-default font-normal">
                      {{ c.cidade }}, {{ c.estadoRegiao}}
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

            <TableCell>
              <span class="px-2 py-1 rounded text-xs font-medium"
                :class="c.classificacaoDistancia === 'Internacional'
                  ? 'bg-purple-900 text-purple-300'
                  : c.classificacaoDistancia === 'Nacional'
                    ? 'bg-blue-900 text-blue-300'
                    : 'bg-green-900 text-green-300'"
              >
                {{ c.classificacaoDistancia ?? '—' }}
              </span>
            </TableCell>

            <TableCell>
              <div class="flex items-center gap-2 text-foreground font-normal">
                <div class="size-2 rounded-full" :class="c.ativo ? 'bg-emerald-500' : 'bg-red-500'"></div>
                <span class="text-sm">{{ c.ativo ? 'Ativo' : 'Inativo' }}</span>
              </div>
            </TableCell>

            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="router.push(`/clientes/${c.id}`)">
                  <Eye class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicao(c)">
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
              <ClienteCadastro
                @fechar="isCadastroOpen = false"
                @sucesso="onCadastroSucesso"
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
                <h2 class="text-2xl font-bold tracking-tight">Editar Cliente</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados do cliente selecionado.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isEditOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>

            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <ClienteCadastro
                :initialData="editingCliente"
                @fechar="isEditOpen = false"
                @sucesso="onEdicaoSucesso"
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
