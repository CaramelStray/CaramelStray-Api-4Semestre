<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import { 
  Plus, Search, Pencil, Trash2, Settings, Wrench
} from 'lucide-vue-next'
import CatalogoMaquinaCadastroPopup from '@/components/catalogoMaquinas/CatalogoMaquinaCadastroPopup.vue'
import { catalogoMaquinaService, type CatalogoMaquinaResponseDTO } from '@/services/catalogoMaquinaService'

const isCadastroOpen = ref(false)
const searchQuery = ref('')

const maquinas = ref<CatalogoMaquinaResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')

const carregarMaquinas = async () => {
  loading.value = true
  erro.value = ''
  try {
    maquinas.value = await catalogoMaquinaService.listarTodos()
  } catch (e: any) {
    erro.value = e.message
  } finally {
    loading.value = false
  }
}

const removerMaquina = async (codigo: number) => {
  if (confirm('Tem certeza que deseja remover esta máquina?')) {
    try {
      await catalogoMaquinaService.remover(codigo)
      await carregarMaquinas()
    } catch (e: any) {
      alert('Erro ao remover: ' + e.message)
    }
  }
}

onMounted(() => {
  carregarMaquinas()
})

const stats = computed(() => [
  { 
    label: 'Total de Máquinas',
    value: maquinas.value.length.toString(),
    sub: 'Cadastradas no sistema',
    icon: Settings, 
    color: 'text-blue-400' 
  },
  { 
    label: 'Modelos Ativos',
    value: '—', 
    sub: 'Mapeamento futuro', 
    icon: Wrench, 
    color: 'text-purple-400' 
  }
])

const filteredMaquinas = computed(() => {
  return maquinas.value.filter(m => 
    m.descricao.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (m.especificacao && m.especificacao.toLowerCase().includes(searchQuery.value.toLowerCase()))
  )
})

const fecharModalERecarregar = () => {
  isCadastroOpen.value = false
  carregarMaquinas()
}

const getAvatarColor = (name: string) => {
  const colors = ['bg-blue-500 text-white', 'bg-emerald-500 text-white', 'bg-rose-500 text-white', 'bg-indigo-500 text-white']
  const index = name.length % colors.length
  return colors[index]
}
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
          placeholder="Buscar máquina..." 
          class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary" 
        />
      </div>
      <div class="flex gap-3 shrink-0">
        <Button size="lg" @click="isCadastroOpen = true" class="h-12 font-bold uppercase text-[11px] px-6 bg-[#2563eb] dark:bg-blue-600 hover:opacity-90 text-white border-none shadow-md">
          <Plus class="w-4 h-4 mr-2" /> Nova Máquina
        </Button>
      </div>
    </div>

    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Catálogo de Máquinas Cadastradas</h2>
      </div>
      
      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12 w-[100px]">Código</TableHead>
            <TableHead class="h-12">Descrição</TableHead>
            <TableHead class="h-12">Especificação</TableHead>
            <TableHead class="h-12">Limite Manutenção</TableHead>
            <TableHead class="text-right pr-14 h-12">Ações</TableHead>
          </TableRow>
        </TableHeader>
        
        <TableBody>
          <TableRow 
            v-for="m in filteredMaquinas" 
            :key="m.codigo" 
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-medium text-muted-foreground">
              #{{ m.codigo }}
            </TableCell>

            <TableCell class="py-3">
              <div class="flex items-center gap-3">
                <div :class="['flex items-center justify-center size-7 rounded-full text-xs font-bold', getAvatarColor(m.descricao)]">
                  {{ m.descricao.substring(0, 2).toUpperCase() }}
                </div>
                <span class="text-sm font-normal text-foreground">{{ m.descricao }}</span>
              </div>
            </TableCell>

            <TableCell>
              <span class="text-sm text-muted-foreground line-clamp-1">
                {{ m.especificacao || '—' }}
              </span>
            </TableCell>

            <TableCell>
              <span class="text-sm text-muted-foreground line-clamp-1">
                {{ m.limiteManutencao || '—' }}
              </span>
            </TableCell>

            <TableCell class="text-right pr-6">
              <div class="flex items-center justify-end gap-1">
                <Button variant="ghost" size="icon" class="h-9 w-9 text-muted-foreground hover:text-white transition-colors">
                  <Pencil class="size-5" />
                </Button>
                <Button variant="ghost" size="icon" @click="removerMaquina(m.codigo)" class="h-9 w-9 text-muted-foreground hover:text-red-500 hover:bg-red-500/10 transition-colors">
                  <Trash2 class="size-5" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
          <TableRow v-if="filteredMaquinas.length === 0">
             <TableCell colspan="5" class="h-24 text-center text-muted-foreground">
              Nenhuma máquina encontrada.
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
                <h2 class="text-2xl font-bold tracking-tight">Nova Máquina</h2>
                <p class="text-sm text-muted-foreground mt-1">Preencha os dados abaixo para adicionar um novo modelo ao catálogo.</p>
              </div>
            </div>
            
            <div class="flex-1 overflow-y-auto p-6 md:p-10">
              <CatalogoMaquinaCadastroPopup @fechar="fecharModalERecarregar" />
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
  transform: translateY(20px);
}
</style>