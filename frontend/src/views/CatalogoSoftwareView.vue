<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  Tooltip, TooltipContent, TooltipProvider, TooltipTrigger,
} from '@/components/ui/tooltip'
import {
  Download, Plus, Monitor, Layers, AlertCircle, TerminalSquare,
  Pencil, Eye, Search, ExternalLink, Trash2, X, CheckCircle2
} from 'lucide-vue-next'

import { catalogoSoftwareService, type CatalogoSoftwareResponseDTO } from '@/services/catalogoSoftwareService'
import SoftwareCadastroForm from '@/components/softwares/SoftwareCadastroPopup.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'

const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingSoftware = ref<CatalogoSoftwareResponseDTO | null>(null)
const searchQuery = ref('')
const confirmOpen = ref(false)
const confirmNome = ref('')
const confirmId = ref<number | null>(null)
const deletando = ref(false)

const softwares = ref<CatalogoSoftwareResponseDTO[]>([])
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

const getAvatarColor = (name: string) => {
  if (!name) return 'bg-indigo-500 text-white'
  const colors = ['bg-indigo-500 text-white', 'bg-violet-500 text-white', 'bg-cyan-500 text-white', 'bg-teal-500 text-white']
  const index = name.length % colors.length
  return colors[index]
}

const stats = computed(() => [
  { 
    label: 'Total de Softwares',
    value: softwares.value.length.toString(),
    sub: 'Catálogo completo',
    icon: Monitor, 
    color: 'text-indigo-400' 
  },
  { 
    label: 'Sistemas Ativos',
    value: softwares.value.filter(s => s.ativo).length.toString(), 
    sub: 'Disponíveis para uso', 
    icon: TerminalSquare, 
    color: 'text-emerald-400' 
  },
  { 
    label: 'Sistemas Inativos', 
    value: softwares.value.filter(s => !s.ativo).length.toString(),
    sub: 'Descontinuados ou suspensos',
    icon: AlertCircle, 
    color: 'text-rose-400' 
  },
  { 
    label: 'Tipos Distintos', 
    value: new Set(softwares.value.map(s => s.tipo)).size.toString(), 
    sub: 'Categorias cadastradas',
    icon: Layers, 
    color: 'text-amber-400' 
  },
])

const filteredSoftwares = computed(() => {
  if (!searchQuery.value) return softwares.value
  
  const query = searchQuery.value.toLowerCase()
  return softwares.value.filter(s => 
    (s.nomeSoftware && s.nomeSoftware.toLowerCase().includes(query)) ||
    (s.tipo && s.tipo.toLowerCase().includes(query)) ||
    (s.desenvolvedorFornecedor && s.desenvolvedorFornecedor.toLowerCase().includes(query))
  )
})

const carregarSoftwares = async () => {
  loading.value = true
  erro.value = ''
  try {
    softwares.value = await catalogoSoftwareService.listar()
  } catch (e: any) {
    erro.value = e.message || 'Erro ao carregar o catálogo de softwares.'
  } finally {
    loading.value = false
  }
}

const fecharSucessoCadastro = () => {
  sucessoCadastro.value = ''

  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
    sucessoTimeout = null
  }
}

const abrirEdicao = (software: CatalogoSoftwareResponseDTO) => {
  editingSoftware.value = software
  isEditOpen.value = true
}

const onCadastroSucesso = (software: CatalogoSoftwareResponseDTO) => {
  mostrarSucessoCadastro(`Software "${software.nomeSoftware}" v${software.versao} cadastrado com sucesso.`)
  carregarSoftwares()
}

const onEdicaoSucesso = (software: CatalogoSoftwareResponseDTO) => {
  mostrarSucessoCadastro(`Software "${software.nomeSoftware}" v${software.versao} atualizado com sucesso.`)
  carregarSoftwares()
}

const removerSoftware = (id: number, nome: string) => {
  confirmId.value = id
  confirmNome.value = nome
  confirmOpen.value = true
}

const confirmarExclusao = async () => {
  if (confirmId.value === null) return
  deletando.value = true
  try {
    await catalogoSoftwareService.remover(confirmId.value)
    confirmOpen.value = false
    mostrarSucessoCadastro(`Software "${confirmNome.value}" removido com sucesso.`)
    await carregarSoftwares()
  } catch (e: any) {
    alert('Erro ao remover: ' + (e.response?.data?.message || e.message))
  } finally {
    deletando.value = false
  }
}

const abrirDocumentacao = (url: string) => {
  if (url) window.open(url, '_blank')
}

onMounted(() => {
  carregarSoftwares()
})

onBeforeUnmount(() => {
  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
  }
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

    <div v-if="loading" class="text-center py-12 text-muted-foreground flex flex-col items-center gap-2">
      <div class="size-6 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
      Carregando catálogo...
    </div>
    <div v-else-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>
    
    <template v-else>
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

      <div class="flex flex-col sm:flex-row items-center justify-between gap-4 w-full">
        <div class="relative flex-1 w-full">
          <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
          <Input 
            v-model="searchQuery" 
            placeholder="Buscar por nome, tipo ou fornecedor..." 
            class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary" 
          />
        </div>
        <div class="flex gap-3 shrink-0 w-full sm:w-auto">
          <Button variant="outline" size="lg" class="h-12 w-full sm:w-auto font-bold uppercase text-[11px] px-6 border-border hover:bg-muted/20">
            <Download class="w-4 h-4 mr-2" /> Exportar Relatório
          </Button>
          
          <Button size="lg" @click="isCadastroOpen = true" class="h-12 w-full sm:w-auto font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
            <Plus class="w-4 h-4 mr-2" /> Novo Software
          </Button>
        </div>
      </div>

      <div class="rounded-md border border-border bg-sidebar overflow-hidden">
        <div class="p-4 border-b border-border bg-muted/5 flex justify-between items-center">
          <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Catálogo de Sistemas</h2>
          <span class="text-xs text-muted-foreground">Exibindo {{ filteredSoftwares.length }} registros</span>
        </div>
        
        <Table>
          <TableHeader>
            <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
              <TableHead class="pl-6 h-12">Software / Versão</TableHead>
              <TableHead class="h-12">ID</TableHead>
              <TableHead class="h-12">Tipo</TableHead>
              <TableHead class="h-12">Fornecedor</TableHead>
              <TableHead class="h-12 text-center">Status</TableHead>
              <TableHead class="h-12 text-center">Docs</TableHead>
              <TableHead class="text-right pr-14 h-12">Ações</TableHead>
            </TableRow>
          </TableHeader>
          
          <TableBody>
            <TableRow 
              v-for="s in filteredSoftwares" 
              :key="s.id" 
              class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
            >
              <TableCell class="pl-6 py-3">
                <div class="flex items-center gap-3">
                  <div :class="['flex items-center justify-center size-8 rounded-md text-xs font-bold', getAvatarColor(s.nomeSoftware)]">
                    {{ s.nomeSoftware ? s.nomeSoftware.substring(0, 2).toUpperCase() : 'SW' }}
                  </div>
                  <div class="flex flex-col">
                    <span class="text-sm font-medium text-foreground">{{ s.nomeSoftware }}</span>
                    <span class="text-[11px] text-muted-foreground">v{{ s.versao }}</span>
                  </div>
                </div>
              </TableCell>

              <TableCell>
                <span class="font-mono text-xs text-muted-foreground">{{ s.identificador }}</span>
              </TableCell>

              <TableCell>
                <span class="px-2.5 py-1 rounded-full text-[11px] font-medium bg-muted/50 border border-border text-foreground">
                  {{ s.tipo }}
                </span>
              </TableCell>

              <TableCell>
                <TooltipProvider :delay-duration="100" v-if="s.descricaoTecnica">
                  <Tooltip>
                    <TooltipTrigger as-child>
                      <span class="text-sm text-foreground hover:opacity-80 transition-opacity cursor-help font-normal border-b border-dashed border-muted-foreground/50 pb-0.5">
                        {{ s.desenvolvedorFornecedor || 'Interno' }}
                      </span>
                    </TooltipTrigger>
                    <TooltipContent side="top" class="bg-[#e2e8f0] text-[#0f172a] border-none font-medium rounded-lg px-3 py-1.5 text-xs shadow-xl max-w-xs text-center">
                      {{ s.descricaoTecnica }}
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
                <span v-else class="text-sm text-foreground font-normal">
                  {{ s.desenvolvedorFornecedor || 'Interno' }}
                </span>
              </TableCell>

              <TableCell class="text-center">
                <div class="flex items-center justify-center gap-1.5 text-foreground font-normal">
                  <div class="size-2 rounded-full shadow-sm" :class="s.ativo ? 'bg-emerald-500 shadow-emerald-500/50' : 'bg-rose-500 shadow-rose-500/50'"></div>
                  <span class="text-sm">{{ s.status }}</span>
                </div>
              </TableCell>

              <TableCell class="text-center">
                <Button 
                  v-if="s.urlDocumentacao"
                  variant="ghost" 
                  size="icon" 
                  class="h-8 w-8 text-blue-400 hover:text-blue-300 hover:bg-blue-500/10 transition-colors"
                  @click="abrirDocumentacao(s.urlDocumentacao)"
                  title="Acessar documentação"
                >
                  <ExternalLink class="size-4" />
                </Button>
                <span v-else class="text-muted-foreground text-xs">—</span>
              </TableCell>

              <TableCell class="text-right pr-6">
                <div class="flex items-center justify-end gap-1">
                  <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors">
                    <Eye class="size-4.5" />
                  </Button>
                  <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicao(s)">
                    <Pencil class="size-4.5" />
                  </Button>
                  <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-red-500 hover:bg-red-500/10 transition-colors" @click="removerSoftware(s.id, s.nomeSoftware)">
                    <Trash2 class="size-4.5" />
                  </Button>
                </div>
              </TableCell>
            </TableRow>
            
            <TableRow v-if="filteredSoftwares.length === 0">
              <TableCell colspan="7" class="h-24 text-center text-muted-foreground">
                Nenhum sistema encontrado com os filtros atuais.
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>
    </template>

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isCadastroOpen = false"></div>

          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[60vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Novo Software</h2>
                <p class="text-sm text-muted-foreground mt-1">Adicione um novo sistema ao catálogo tecnológico.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isCadastroOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>

            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <SoftwareCadastroForm
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

          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[60vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Editar Software</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados do sistema selecionado.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isEditOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>

            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <SoftwareCadastroForm
                :initialData="editingSoftware"
                @fechar="isEditOpen = false"
                @sucesso="onEdicaoSucesso"
              />
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <ConfirmDeleteDialog
      v-model:open="confirmOpen"
      titulo="Excluir Software"
      :descricao="`Tem certeza que deseja excluir o software &quot;${confirmNome}&quot;? Esta ação não pode ser desfeita.`"
      :carregando="deletando"
      @confirmar="confirmarExclusao"
    />

  </div>
</template>

<style scoped>
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
  transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.95) translateY(20px);
  opacity: 0;
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
