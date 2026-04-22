<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  Plus, Search, Pencil, Trash2, Code2, Layers, X, CheckCircle2
} from 'lucide-vue-next'
import HabilidadeCadastro from '@/components/habilidades/HabilidadeCadastroPopup.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
import { habilidadeService, type HabilidadeResponseDTO } from '@/services/habilidadeService'

const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingHabilidade = ref<HabilidadeResponseDTO | null>(null)
const searchQuery = ref('')
const confirmOpen = ref(false)
const confirmNome = ref('')
const confirmId = ref<number | null>(null)
const deletando = ref(false)

const habilidades = ref<HabilidadeResponseDTO[]>([])
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

const carregarHabilidades = async () => {
  loading.value = true
  erro.value = ''
  try {
    habilidades.value = await habilidadeService.listar()
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
}

const removerHabilidade = (id: number, nome: string) => {
  confirmId.value = id
  confirmNome.value = nome
  confirmOpen.value = true
}

const confirmarExclusao = async () => {
  if (confirmId.value === null) return
  deletando.value = true
  try {
    await habilidadeService.remover(confirmId.value)
    confirmOpen.value = false
    mostrarSucessoCadastro(`Certificação "${confirmNome.value}" removida com sucesso.`)
    await carregarHabilidades()
  } catch (e: any) {
    alert('Erro ao remover: ' + (e.response?.data?.message || e.message))
  } finally {
    deletando.value = false
  }
}

onMounted(() => {
  carregarHabilidades()
})

onBeforeUnmount(() => {
  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
  }
})

const stats = computed(() =>[
  { 
    label: 'Total de Habilidades',
    value: habilidades.value.length.toString(),
    sub: 'Cadastradas no sistema',
    icon: Code2, 
    color: 'text-blue-400' 
  },
])

const filteredHabilidades = computed(() => {
  return habilidades.value.filter(h => 
    h.descricao.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (h.observacao && h.observacao.toLowerCase().includes(searchQuery.value.toLowerCase()))
  )
})

const fecharSucessoCadastro = () => {
  sucessoCadastro.value = ''

  if (sucessoTimeout) {
    clearTimeout(sucessoTimeout)
    sucessoTimeout = null
  }
}

const abrirEdicao = (habilidade: HabilidadeResponseDTO) => {
  editingHabilidade.value = habilidade
  isEditOpen.value = true
}

const onCadastroSucesso = (habilidade: HabilidadeResponseDTO) => {
  mostrarSucessoCadastro(`Certificação "${habilidade.descricao}" cadastrada com sucesso.`)
  carregarHabilidades()
}

const onEdicaoSucesso = (habilidade: HabilidadeResponseDTO) => {
  mostrarSucessoCadastro(`Certificação "${habilidade.descricao}" atualizada com sucesso.`)
  carregarHabilidades()
}

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500 text-white', 'bg-emerald-500 text-white', 'bg-rose-500 text-white', 'bg-indigo-500 text-white']
  const index = name.length % colors.length
  return colors[index]
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
          placeholder="Buscar certificação..." 
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary" 
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <Plus class="w-4 h-4 mr-2" /> Nova Certificação
        </Button>
      </div>
    </div>

    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Certificações Cadastradas</h2>
      </div>
      
      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12 w-[100px]">Código</TableHead>
            <TableHead class="h-12">Descrição</TableHead>
            <TableHead class="h-12">Observações</TableHead>
            <TableHead class="text-right pr-14 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>
        
        <TableBody>
          <TableRow 
            v-for="h in filteredHabilidades" 
            :key="h.codigo" 
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-medium text-muted-foreground">
              #{{ h.codigo }}
            </TableCell>

            <TableCell class="py-3">
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold', getAvatarColor(h.descricao)]">
                  {{ h.descricao.substring(0, 2).toUpperCase() }}
                </div>
                <span class="text-sm font-normal text-foreground">{{ h.descricao }}</span>
              </div>
            </TableCell>

            <TableCell>
              <span class="text-sm text-muted-foreground line-clamp-1">
                {{ h.observacao || '—' }}
              </span>
            </TableCell>

            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicao(h)">
                  <Pencil class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" @click="removerHabilidade(h.codigo, h.descricao)" class="h-9 w-9 text-muted-foreground hover:text-red-500 hover:bg-red-500/10 transition-colors">
                  <Trash2 class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
          <TableRow v-if="filteredHabilidades.length === 0">
             <TableCell colspan="4" class="h-24 text-center text-muted-foreground">
              Nenhuma certificação encontrada.
             </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isCadastroOpen = false"></div>

          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[50vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Nova Certificação</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados abaixo para adicionar uma nova certificação.</p>
              </div>
            </div>

            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <HabilidadeCadastro
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

          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[50vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Editar Certificação</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados da certificação selecionada.</p>
              </div>
            </div>

            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <HabilidadeCadastro
                :initialData="editingHabilidade"
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
      titulo="Excluir Certificação"
      :descricao="`Tem certeza que deseja excluir a certificação &quot;${confirmNome}&quot;? Esta ação não pode ser desfeita.`"
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
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: translateY(20px);
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
