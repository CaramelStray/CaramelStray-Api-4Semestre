<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  Package, Plus, Search, X, CheckCircle2, Pencil, Trash2,
} from 'lucide-vue-next'

import { catalogoAtivoService, type CatalogoAtivoResponseDTO } from '@/services/catalogoAtivoService'
import CatalogoAtivoFormPopup from '@/components/catalogoAtivos/CatalogoAtivoFormPopup.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'

const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingItem = ref<CatalogoAtivoResponseDTO | null>(null)
const searchQuery = ref('')

const catalogo = ref<CatalogoAtivoResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')
const sucessoMsg = ref('')
let sucessoTimeout: ReturnType<typeof setTimeout> | null = null

const deleteDialogOpen = ref(false)
const deletingItem = ref<CatalogoAtivoResponseDTO | null>(null)
const deletingLoading = ref(false)

const stats = computed(() => [
  {
    label: 'Total no Catálogo',
    value: catalogo.value.length.toString(),
    sub: 'Produtos cadastrados',
    color: 'text-blue-400',
  },
  {
    label: 'Tipos distintos',
    value: new Set(catalogo.value.map(c => c.tipo).filter(Boolean)).size.toString(),
    sub: 'Categorias de produto',
    color: 'text-violet-400',
  },
  {
    label: 'Marcas distintas',
    value: new Set(catalogo.value.map(c => c.marca).filter(Boolean)).size.toString(),
    sub: 'Fabricantes diferentes',
    color: 'text-emerald-400',
  },
])

const mostrarSucesso = (msg: string) => {
  sucessoMsg.value = msg
  if (sucessoTimeout) clearTimeout(sucessoTimeout)
  sucessoTimeout = setTimeout(() => {
    sucessoMsg.value = ''
    sucessoTimeout = null
  }, 4000)
}

const fecharSucesso = () => {
  sucessoMsg.value = ''
  if (sucessoTimeout) { clearTimeout(sucessoTimeout); sucessoTimeout = null }
}

const carregar = async () => {
  loading.value = true
  erro.value = ''
  try {
    catalogo.value = await catalogoAtivoService.listar()
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(carregar)
onBeforeUnmount(() => { if (sucessoTimeout) clearTimeout(sucessoTimeout) })

const abrirEdicao = async (item: CatalogoAtivoResponseDTO) => {
  try {
    editingItem.value = await catalogoAtivoService.buscarPorId(item.codigo)
  } catch {
    editingItem.value = item
  }
  isEditOpen.value = true
}

const onCadastroSucesso = async (item: CatalogoAtivoResponseDTO) => {
  mostrarSucesso(`"${item.descricaoProduto}" cadastrado com sucesso.`)
  await carregar()
}

const onEdicaoSucesso = async (item: CatalogoAtivoResponseDTO) => {
  mostrarSucesso(`"${item.descricaoProduto}" atualizado com sucesso.`)
  await carregar()
}

const abrirExclusao = (item: CatalogoAtivoResponseDTO) => {
  deletingItem.value = item
  deleteDialogOpen.value = true
}

const confirmarExclusao = async () => {
  if (!deletingItem.value) return
  deletingLoading.value = true
  try {
    await catalogoAtivoService.deletar(deletingItem.value.codigo)
    mostrarSucesso(`"${deletingItem.value.descricaoProduto}" excluído com sucesso.`)
    deleteDialogOpen.value = false
    deletingItem.value = null
    await carregar()
  } catch (e: any) {
    alert('Erro ao excluir: ' + e.message)
  } finally {
    deletingLoading.value = false
  }
}

const filteredCatalogo = computed(() => {
  const q = searchQuery.value.toLowerCase()
  if (!q) return catalogo.value
  return catalogo.value.filter(c =>
    c.descricaoProduto?.toLowerCase().includes(q) ||
    c.modelo?.toLowerCase().includes(q) ||
    c.marca?.toLowerCase().includes(q) ||
    c.tipo?.toLowerCase().includes(q) ||
    c.descricao?.toLowerCase().includes(q)
  )
})
</script>

<template>
  <div class="p-6 space-y-6">
    <Transition name="toast">
      <div
        v-if="sucessoMsg"
        class="fixed top-6 right-6 z-[120] max-w-md rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 shadow-2xl backdrop-blur-md"
      >
        <div class="flex items-start gap-3">
          <div class="mt-0.5 rounded-full bg-emerald-500/20 p-1.5 text-emerald-300">
            <CheckCircle2 class="size-4" />
          </div>
          <div class="flex-1">
            <p class="text-sm font-semibold text-emerald-200">Operação concluída</p>
            <p class="mt-1 text-sm text-emerald-100/90">{{ sucessoMsg }}</p>
          </div>
          <Button
            variant="ghost" size="icon"
            class="size-8 shrink-0 text-emerald-100/80 hover:bg-emerald-500/10 hover:text-emerald-50"
            @click="fecharSucesso"
          >
            <X class="size-4" />
          </Button>
        </div>
      </div>
    </Transition>

    <div class="flex items-start justify-between gap-4">
      <div class="flex items-center gap-3">
        <div class="flex items-center justify-center w-10 h-10 rounded-full border border-blue-500 bg-blue-500/20 text-blue-400">
          <Package class="w-5 h-5" />
        </div>
        <div>
          <h1 class="text-2xl font-bold tracking-tight">Catálogo de Ativos</h1>
          <p class="text-sm text-muted-foreground">Tipos de produtos disponíveis no inventário</p>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <Card v-for="stat in stats" :key="stat.label" class="bg-sidebar border-border">
        <CardHeader class="flex flex-row items-center justify-between pb-2">
          <CardTitle class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider">{{ stat.label }}</CardTitle>
          <Package class="w-4 h-4" :class="stat.color" />
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
          placeholder="Buscar produto, marca, tipo..."
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <Plus class="w-4 h-4 mr-2" /> Novo Produto
        </Button>
      </div>
    </div>

    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Produtos cadastrados</h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Produto</TableHead>
            <TableHead class="h-12">Tipo</TableHead>
            <TableHead class="h-12">Especificação</TableHead>
            <TableHead class="text-right pr-6 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="item in filteredCatalogo"
            :key="item.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3">
              <div class="flex flex-col">
                <span class="text-sm font-normal text-foreground">{{ item.descricaoProduto }}</span>
                <span v-if="item.modelo || item.marca" class="text-[11px] text-muted-foreground">
                  {{ [item.modelo, item.marca].filter(Boolean).join(' · ') }}
                </span>
              </div>
            </TableCell>

            <TableCell class="text-sm font-normal text-muted-foreground">
              {{ item.tipo || '—' }}
            </TableCell>

            <TableCell class="text-sm font-normal text-muted-foreground max-w-xs truncate">
              {{ item.especificacao || item.descricao || '—' }}
            </TableCell>

            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicao(item)">
                  <Pencil class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-red-400 transition-colors" @click="abrirExclusao(item)">
                  <Trash2 class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <ConfirmDeleteDialog
      :open="deleteDialogOpen"
      titulo="Excluir Produto do Catálogo"
      :descricao="`Tem certeza que deseja excluir '${deletingItem?.descricaoProduto ?? ''}'? Esta ação não pode ser desfeita.`"
      :carregando="deletingLoading"
      @update:open="deleteDialogOpen = $event"
      @confirmar="confirmarExclusao"
    />

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isCadastroOpen = false"></div>
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[60vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Novo Produto</h2>
                <p class="text-sm text-muted-foreground mt-1">Cadastre um novo tipo de produto no catálogo.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isCadastroOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <CatalogoAtivoFormPopup @fechar="isCadastroOpen = false" @sucesso="onCadastroSucesso" />
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
                <h2 class="text-2xl font-bold tracking-tight">Editar Produto</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados do produto selecionado.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isEditOpen = false" class="hover:bg-red-500/10 hover:text-red-500">
                <X class="w-6 h-6" />
              </Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <CatalogoAtivoFormPopup :initialData="editingItem" @fechar="isEditOpen = false" @sucesso="onEdicaoSucesso" />
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
