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

import { ativoService, type AtivoResponseDTO, type CatalogoAtivoDTO } from '@/services/ativoService'
import AtivoFormPopup from '@/components/ativos/AtivoFormPopup.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'

// ── Status ─────────────────────────────────────────────────────────────────
const STATUS_LIST = [
  { value: 'DISPONIVEL',    label: 'Disponível',     dotClass: 'bg-emerald-500', tabClass: 'text-emerald-400 border-emerald-500' },
  { value: 'EM_MANUTENCAO', label: 'Em Manutenção',  dotClass: 'bg-amber-500',   tabClass: 'text-amber-400 border-amber-500'   },
  { value: 'EM_USO',        label: 'Em Uso',          dotClass: 'bg-blue-500',    tabClass: 'text-blue-400 border-blue-500'     },
] as const

type StatusValue = typeof STATUS_LIST[number]['value']

const statusInfo = (val?: string) =>
  STATUS_LIST.find(s => s.value === val) ?? { label: val ?? 'Sem status', dotClass: 'bg-slate-500', tabClass: '' }

// ── State ───────────────────────────────────────────────────────────────────
const ativos = ref<AtivoResponseDTO[]>([])
const catalogos = ref<CatalogoAtivoDTO[]>([])
const loading = ref(false)
const erro = ref('')

const isCadastroOpen = ref(false)
const isEditOpen = ref(false)
const editingAtivo = ref<AtivoResponseDTO | null>(null)
const searchQuery = ref('')
const activeTab = ref<StatusValue | 'TODOS'>('TODOS')

const deleteDialogOpen = ref(false)
const deletingAtivo = ref<AtivoResponseDTO | null>(null)
const deletingLoading = ref(false)

const sucessoMsg = ref('')
let sucessoTimeout: ReturnType<typeof setTimeout> | null = null

// ── Tabs ────────────────────────────────────────────────────────────────────
const tabs = computed(() => [
  { key: 'TODOS' as const, label: 'Todos', count: ativos.value.length, dotClass: 'bg-slate-400' },
  ...STATUS_LIST.map(s => ({
    key: s.value as StatusValue | 'TODOS',
    label: s.label,
    count: ativos.value.filter(a => a.status === s.value).length,
    dotClass: s.dotClass,
  })),
])

// ── Filtered list ────────────────────────────────────────────────────────────
const filtered = computed(() => {
  let list = ativos.value
  if (activeTab.value !== 'TODOS') list = list.filter(a => a.status === activeTab.value)
  const q = searchQuery.value.toLowerCase()
  if (!q) return list
  return list.filter(a =>
    a.descricaoProduto?.toLowerCase().includes(q) ||
    a.descricao?.toLowerCase().includes(q) ||
    a.numeroSerie?.toLowerCase().includes(q) ||
    a.nomeFuncionarioResponsavel?.toLowerCase().includes(q) ||
    a.marca?.toLowerCase().includes(q)
  )
})

// ── Stats cards ──────────────────────────────────────────────────────────────
const stats = computed(() => [
  { label: 'Total de Ativos',   value: ativos.value.length,                                              sub: 'Cadastrados no sistema',  color: 'text-slate-400'   },
  { label: 'Disponíveis',       value: ativos.value.filter(a => a.status === 'DISPONIVEL').length,       sub: 'Prontos para uso',         color: 'text-emerald-400' },
  { label: 'Em Manutenção',     value: ativos.value.filter(a => a.status === 'EM_MANUTENCAO').length,    sub: 'Aguardando reparo',        color: 'text-amber-400'   },
  { label: 'Em Uso',            value: ativos.value.filter(a => a.status === 'EM_USO').length,           sub: 'Atualmente alocados',      color: 'text-blue-400'    },
])

// ── Data ─────────────────────────────────────────────────────────────────────
const carregar = async () => {
  loading.value = true
  erro.value = ''
  try { ativos.value = await ativoService.listar() }
  catch (e: any) { erro.value = e.message }
  finally { loading.value = false }
}

onMounted(async () => {
  await carregar()
  try { catalogos.value = await ativoService.listarCatalogos() }
  catch (e: any) { console.error('Erro ao carregar catálogos:', e.message) }
})

onBeforeUnmount(() => { if (sucessoTimeout) clearTimeout(sucessoTimeout) })

// ── Toast ────────────────────────────────────────────────────────────────────
const mostrarSucesso = (msg: string) => {
  sucessoMsg.value = msg
  if (sucessoTimeout) clearTimeout(sucessoTimeout)
  sucessoTimeout = setTimeout(() => { sucessoMsg.value = ''; sucessoTimeout = null }, 4000)
}
const fecharSucesso = () => {
  sucessoMsg.value = ''
  if (sucessoTimeout) { clearTimeout(sucessoTimeout); sucessoTimeout = null }
}

// ── CRUD handlers ─────────────────────────────────────────────────────────────
const abrirEdicao = async (a: AtivoResponseDTO) => {
  try { editingAtivo.value = await ativoService.buscarPorId(a.codigo) }
  catch { editingAtivo.value = a }
  isEditOpen.value = true
}

const onCadastroSucesso = async (a: AtivoResponseDTO) => {
  mostrarSucesso(`Ativo "${a.descricaoProduto ?? 'novo'}" cadastrado com sucesso.`)
  await carregar()
}

const onEdicaoSucesso = async (a: AtivoResponseDTO) => {
  mostrarSucesso(`Ativo "${a.descricaoProduto ?? ''}" atualizado com sucesso.`)
  await carregar()
}

const abrirExclusao = (a: AtivoResponseDTO) => {
  deletingAtivo.value = a
  deleteDialogOpen.value = true
}

const confirmarExclusao = async () => {
  if (!deletingAtivo.value) return
  deletingLoading.value = true
  try {
    await ativoService.deletar(deletingAtivo.value.codigo)
    mostrarSucesso(`Ativo "${deletingAtivo.value.descricaoProduto ?? ''}" excluído com sucesso.`)
    deleteDialogOpen.value = false
    deletingAtivo.value = null
    await carregar()
  } catch (e: any) {
    alert('Erro ao excluir: ' + e.message)
  } finally {
    deletingLoading.value = false
  }
}
</script>

<template>
  <div class="p-6 space-y-6">

    <!-- Toast -->
    <Transition name="toast">
      <div v-if="sucessoMsg" class="fixed top-6 right-6 z-[120] max-w-md rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 shadow-2xl backdrop-blur-md">
        <div class="flex items-start gap-3">
          <div class="mt-0.5 rounded-full bg-emerald-500/20 p-1.5 text-emerald-300"><CheckCircle2 class="size-4" /></div>
          <div class="flex-1">
            <p class="text-sm font-semibold text-emerald-200">Operação concluída</p>
            <p class="mt-1 text-sm text-emerald-100/90">{{ sucessoMsg }}</p>
          </div>
          <Button variant="ghost" size="icon" class="size-8 shrink-0 text-emerald-100/80 hover:bg-emerald-500/10" @click="fecharSucesso">
            <X class="size-4" />
          </Button>
        </div>
      </div>
    </Transition>

    <!-- Header -->
    <div class="flex items-center gap-3">
      <div class="flex items-center justify-center w-10 h-10 rounded-full border border-blue-500 bg-blue-500/20 text-blue-400">
        <Package class="w-5 h-5" />
      </div>
      <div>
        <h1 class="text-2xl font-bold tracking-tight">Ativos</h1>
        <p class="text-sm text-muted-foreground">Inventário de ativos e responsáveis</p>
      </div>
    </div>

    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <!-- Stats -->
    <div class="grid grid-cols-2 xl:grid-cols-4 gap-4">
      <Card v-for="s in stats" :key="s.label" class="bg-sidebar border-border">
        <CardHeader class="flex flex-row items-center justify-between pb-2">
          <CardTitle class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider">{{ s.label }}</CardTitle>
          <Package class="w-4 h-4" :class="s.color" />
        </CardHeader>
        <CardContent>
          <div class="text-3xl font-bold text-foreground">{{ s.value }}</div>
        </CardContent>
      </Card>
    </div>

    <!-- Search + Button -->
    <div class="flex items-center gap-4 w-full">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
        <Input v-model="searchQuery" placeholder="Buscar ativo, técnico, nº série..." class="pl-11 bg-sidebar h-12 text-sm border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary" />
      </div>
      <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] hover:opacity-90 text-white border-none shadow-md shrink-0">
        <Plus class="w-4 h-4 mr-2" /> Novo Ativo
      </Button>
    </div>

    <!-- Tabs por status -->
    <div class="flex gap-1 border-b border-border">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="activeTab = tab.key"
        class="flex items-center gap-2 px-4 py-2.5 text-xs font-semibold uppercase tracking-wider border-b-2 transition-colors"
        :class="activeTab === tab.key
          ? 'border-blue-500 text-blue-400'
          : 'border-transparent text-muted-foreground hover:text-foreground'"
      >
        <div class="size-2 rounded-full" :class="tab.dotClass"></div>
        {{ tab.label }}
        <span class="ml-1 rounded-full bg-muted px-1.5 py-0.5 text-[10px] font-bold text-muted-foreground">{{ tab.count }}</span>
      </button>
    </div>

    <!-- Table -->
    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12">Produto</TableHead>
            <TableHead class="h-12">Identificação</TableHead>
            <TableHead class="h-12">Nº Série / Lote</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="h-12">Responsável</TableHead>
            <TableHead class="text-right pr-6 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow
            v-for="a in filtered"
            :key="a.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3">
              <div class="flex flex-col">
                <span class="text-sm font-normal text-foreground">{{ a.descricaoProduto || '—' }}</span>
                <span v-if="a.modelo || a.marca" class="text-[11px] text-muted-foreground">{{ [a.modelo, a.marca].filter(Boolean).join(' · ') }}</span>
              </div>
            </TableCell>
            <TableCell class="text-sm text-foreground">{{ a.descricao || '—' }}</TableCell>
            <TableCell class="text-sm text-muted-foreground">
              <div class="flex flex-col">
                <span v-if="a.numeroSerie">{{ a.numeroSerie }}</span>
                <span v-if="a.lote" class="text-[11px]">{{ a.lote }}</span>
                <span v-if="!a.numeroSerie && !a.lote">—</span>
              </div>
            </TableCell>
            <TableCell>
              <div class="flex items-center gap-2">
                <div :class="['size-2 rounded-full', statusInfo(a.status).dotClass]"></div>
                <span class="text-xs font-medium tracking-wide text-foreground">{{ statusInfo(a.status).label }}</span>
              </div>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">{{ a.nomeFuncionarioResponsavel || '—' }}</TableCell>
            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors" @click="abrirEdicao(a)">
                  <Pencil class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-red-400 transition-colors" @click="abrirExclusao(a)">
                  <Trash2 class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>

          <TableRow v-if="filtered.length === 0 && !loading">
            <TableCell colspan="6" class="text-center py-12 text-muted-foreground text-sm">
              Nenhum ativo encontrado.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>

    <!-- Delete confirm -->
    <ConfirmDeleteDialog
      :open="deleteDialogOpen"
      titulo="Excluir Ativo"
      :descricao="`Tem certeza que deseja excluir '${deletingAtivo?.descricaoProduto ?? ''}'? Esta ação não pode ser desfeita.`"
      :carregando="deletingLoading"
      @update:open="deleteDialogOpen = $event"
      @confirmar="confirmarExclusao"
    />

    <!-- Modals -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="isCadastroOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isCadastroOpen = false"></div>
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[55vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Novo Ativo</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados para cadastrar um ativo.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isCadastroOpen = false" class="hover:bg-red-500/10 hover:text-red-500"><X class="w-6 h-6" /></Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <AtivoFormPopup :catalogos="catalogos" @fechar="isCadastroOpen = false" @sucesso="onCadastroSucesso" />
            </div>
          </div>
        </div>
      </Transition>

      <Transition name="modal">
        <div v-if="isEditOpen" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isEditOpen = false"></div>
          <div class="modal-content relative bg-background border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[55vw] max-h-[90vh] overflow-hidden">
            <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
              <div>
                <h2 class="text-2xl font-bold tracking-tight">Editar Ativo</h2>
                <p class="text-sm text-muted-foreground mt-1">Altere os dados do ativo selecionado.</p>
              </div>
              <Button variant="ghost" size="icon" @click="isEditOpen = false" class="hover:bg-red-500/10 hover:text-red-500"><X class="w-6 h-6" /></Button>
            </div>
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <AtivoFormPopup :initialData="editingAtivo" :catalogos="catalogos" @fechar="isEditOpen = false" @sucesso="onEdicaoSucesso" />
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<style scoped>
.modal-enter-from, .modal-leave-to { opacity: 0; }
.modal-enter-active .modal-content, .modal-leave-active .modal-content {
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
}
.modal-enter-from .modal-content, .modal-leave-to .modal-content {
  transform: translateX(100vw);
}
.toast-enter-active, .toast-leave-active { transition: all 0.25s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateY(-12px) scale(0.98); }
</style>
