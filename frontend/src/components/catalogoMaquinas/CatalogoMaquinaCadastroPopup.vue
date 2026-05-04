<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import {
  catalogoMaquinaService,
  type CatalogoMaquinaResponseDTO,
  type CatalogoMaquinaChecklistPadraoResponseDTO,
} from '@/services/catalogoMaquinaService'
import {
  catalogoTarefaService,
  type CatalogoTarefaResponseDTO,
} from '@/services/catalogoTarefaService'
import { Settings, ClipboardList, Plus, Trash2, Loader2, X } from 'lucide-vue-next'

const props = defineProps<{
  initialData?: CatalogoMaquinaResponseDTO | null
}>()

const emit = defineEmits<{
  fechar: []
  sucesso: [maquina: CatalogoMaquinaResponseDTO]
}>()

// --- Step state ---
const step = ref(1)
const maquinaSalva = ref<CatalogoMaquinaResponseDTO | null>(null)

// --- Step 2: checklist ---
const checklistPadrao = ref<CatalogoMaquinaChecklistPadraoResponseDTO[]>([])
const todasTarefas = ref<CatalogoTarefaResponseDTO[]>([])
const filtroTarefa = ref('')
const loadingChecklist = ref(false)
const salvandoChecklist = ref(false)

// Criar nova tarefa
const showCriarTarefa = ref(false)
const novaTarefaDescricao = ref('')
const novaTarefaCategoria = ref('')
const criandoTarefa = ref(false)

const codigosSelecionados = computed(() => new Set(checklistPadrao.value.map(c => c.codigoTarefa)))

const tarefasDisponiveis = computed(() => {
  return todasTarefas.value.filter(t => {
    if (codigosSelecionados.value.has(t.id)) return false
    if (!filtroTarefa.value) return true
    const q = filtroTarefa.value.toLowerCase()
    return t.descricao.toLowerCase().includes(q) || (t.categoria ?? '').toLowerCase().includes(q)
  })
})

// --- Step 1: form ---
const formSchema = toTypedSchema(z.object({
  descricao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  especificacao: z.string().optional().default(''),
  limiteManutencao: z.string().regex(/^\d*$/, 'Apenas números são permitidos').optional().default('')
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: { descricao: '', especificacao: '', limiteManutencao: '' }
})

watch(() => props.initialData, (data) => {
  if (data) {
    form.setValues({
      descricao: data.descricao,
      especificacao: data.especificacao || '',
      limiteManutencao: data.limiteManutencao || ''
    })
    maquinaSalva.value = data
  }
}, { immediate: true })

onMounted(async () => {
  todasTarefas.value = await catalogoTarefaService.listar()
  if (props.initialData) {
    await carregarChecklistPadrao(props.initialData.codigo)
  }
})

async function carregarChecklistPadrao(codigoMaquina: number) {
  loadingChecklist.value = true
  try {
    checklistPadrao.value = await catalogoMaquinaService.listarChecklistPadrao(codigoMaquina)
  } finally {
    loadingChecklist.value = false
  }
}

const onlyDigits = (e: KeyboardEvent) => {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End']
  if (!e.ctrlKey && !e.metaKey && !allowed.includes(e.key) && !/^\d$/.test(e.key)) {
    e.preventDefault()
  }
}

const salvarDados = form.handleSubmit(async (values) => {
  try {
    let maquina: CatalogoMaquinaResponseDTO
    if (props.initialData) {
      maquina = await catalogoMaquinaService.atualizar(props.initialData.codigo, {
        descricao: values.descricao,
        especificacao: values.especificacao,
        limiteManutencao: values.limiteManutencao,
      })
    } else {
      maquina = await catalogoMaquinaService.criar({
        descricao: values.descricao,
        especificacao: values.especificacao,
        limiteManutencao: values.limiteManutencao,
      })
    }
    maquinaSalva.value = maquina
    await carregarChecklistPadrao(maquina.codigo)
    step.value = 2
  } catch (e: any) {
    alert('Erro ao salvar: ' + (e.response?.data?.message || e.message))
  }
})

async function adicionarTarefa(tarefa: CatalogoTarefaResponseDTO) {
  if (!maquinaSalva.value) return
  salvandoChecklist.value = true
  try {
    const item = await catalogoMaquinaService.adicionarChecklistPadrao(maquinaSalva.value.codigo, tarefa.id)
    checklistPadrao.value.push(item)
  } catch (e: any) {
    alert('Erro ao adicionar tarefa: ' + (e.response?.data?.message || e.message))
  } finally {
    salvandoChecklist.value = false
  }
}

async function removerTarefa(codigoTarefa: number) {
  if (!maquinaSalva.value) return
  salvandoChecklist.value = true
  try {
    await catalogoMaquinaService.removerChecklistPadrao(maquinaSalva.value.codigo, codigoTarefa)
    checklistPadrao.value = checklistPadrao.value.filter(c => c.codigoTarefa !== codigoTarefa)
  } catch (e: any) {
    alert('Erro ao remover tarefa: ' + (e.response?.data?.message || e.message))
  } finally {
    salvandoChecklist.value = false
  }
}

async function criarEAdicionarTarefa() {
  if (!novaTarefaDescricao.value.trim() || !maquinaSalva.value) return
  criandoTarefa.value = true
  try {
    const tarefa = await catalogoTarefaService.criar({
      descricao: novaTarefaDescricao.value.trim(),
      categoria: novaTarefaCategoria.value.trim() || undefined,
    })
    todasTarefas.value.push(tarefa)
    await adicionarTarefa(tarefa)
    novaTarefaDescricao.value = ''
    novaTarefaCategoria.value = ''
    showCriarTarefa.value = false
  } catch (e: any) {
    alert('Erro ao criar tarefa: ' + (e.response?.data?.message || e.message))
  } finally {
    criandoTarefa.value = false
  }
}

function concluir() {
  if (!maquinaSalva.value) return
  emit('sucesso', maquinaSalva.value)
  emit('fechar')
}
</script>

<template>
  <div>
    <!-- Steps header -->
    <div class="flex items-center gap-2 mb-8 border-b border-border pb-6">
      <button
        type="button"
        class="flex items-center gap-2 transition-colors"
        :class="step === 1 ? 'text-blue-400 font-bold' : 'text-muted-foreground hover:text-foreground'"
        @click="step === 2 && maquinaSalva ? step = 1 : undefined"
      >
        <div
          class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm"
          :class="step === 1
            ? 'border-blue-500 bg-blue-500/20 text-blue-400'
            : step > 1
              ? 'border-green-500 bg-green-500/20 text-green-400'
              : 'border-border bg-muted text-muted-foreground'"
        >
          <Settings class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados da Máquina</span>
      </button>

      <div class="h-px flex-1 bg-border mx-2" />

      <div
        class="flex items-center gap-2 transition-colors"
        :class="step === 2 ? 'text-blue-400 font-bold' : 'text-muted-foreground'"
      >
        <div
          class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm"
          :class="step === 2
            ? 'border-blue-500 bg-blue-500/20 text-blue-400'
            : 'border-border bg-muted text-muted-foreground'"
        >
          <ClipboardList class="w-4 h-4" />
        </div>
        <span class="text-sm">Checklist Padrão</span>
      </div>
    </div>

    <!-- Step 1: Dados da Máquina -->
    <div v-show="step === 1">
      <form @submit="salvarDados">
        <div class="grid grid-cols-1 gap-y-6">

          <FormField v-slot="{ componentField }" name="descricao">
            <FormItem>
              <FormLabel class="flex items-center gap-1">
                Descrição da Máquina <span class="text-red-500 font-bold">*</span>
              </FormLabel>
              <FormControl>
                <Input type="text" placeholder="Ex: workstation, roteador, raspberry" v-bind="componentField" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="especificacao">
            <FormItem>
              <FormLabel>Especificação</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Detalhes técnicos, modelo, capacidade..."
                  class="resize-y min-h-[100px]"
                  v-bind="componentField"
                />
              </FormControl>
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="limiteManutencao">
            <FormItem>
              <FormLabel>Quantidade limite de Manutenção</FormLabel>
              <FormControl>
                <Input type="text" placeholder="Ex: 5" v-bind="componentField" @keydown="onlyDigits" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

        </div>

        <div class="flex items-center justify-end border-t mt-12 pt-6 gap-3">
          <Button type="button" variant="ghost" @click="emit('fechar')">Cancelar</Button>
          <Button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white px-8">
            {{ props.initialData ? 'Salvar e Continuar' : 'Criar e Continuar' }}
          </Button>
        </div>
      </form>
    </div>

    <!-- Step 2: Checklist Padrão -->
    <div v-show="step === 2" class="space-y-6">

      <!-- Tarefas já adicionadas -->
      <div>
        <h3 class="text-sm font-semibold text-foreground mb-3">
          Tarefas no checklist
          <span class="ml-1 text-muted-foreground font-normal">({{ checklistPadrao.length }})</span>
        </h3>
        <div v-if="loadingChecklist" class="flex items-center gap-2 text-sm text-muted-foreground">
          <Loader2 class="w-4 h-4 animate-spin" /> Carregando...
        </div>
        <div v-else-if="checklistPadrao.length === 0" class="text-sm text-muted-foreground italic">
          Nenhuma tarefa adicionada ainda.
        </div>
        <ul v-else class="space-y-2 max-h-48 overflow-y-auto pr-1">
          <li
            v-for="item in checklistPadrao"
            :key="item.codigoTarefa"
            class="flex items-center justify-between rounded-md border border-border bg-muted/30 px-3 py-2 text-sm"
          >
            <div>
              <span class="font-medium">{{ item.descricaoTarefa }}</span>
              <span v-if="item.categoria" class="ml-2 text-xs text-muted-foreground">({{ item.categoria }})</span>
            </div>
            <button
              type="button"
              class="text-destructive hover:text-destructive/80 ml-3 flex-shrink-0"
              :disabled="salvandoChecklist"
              @click="removerTarefa(item.codigoTarefa)"
            >
              <Trash2 class="w-4 h-4" />
            </button>
          </li>
        </ul>
      </div>

      <!-- Buscar e adicionar tarefas existentes -->
      <div class="border-t border-border pt-4">
        <h3 class="text-sm font-semibold text-foreground mb-3">Buscar tarefa do catálogo</h3>
        <Input
          v-model="filtroTarefa"
          type="text"
          placeholder="Filtrar por nome ou categoria..."
          class="mb-3"
        />
        <div v-if="tarefasDisponiveis.length === 0" class="text-sm text-muted-foreground italic">
          {{ filtroTarefa ? 'Nenhuma tarefa encontrada.' : 'Todas as tarefas do catálogo já foram adicionadas.' }}
        </div>
        <ul v-else class="space-y-2 max-h-44 overflow-y-auto pr-1">
          <li
            v-for="tarefa in tarefasDisponiveis"
            :key="tarefa.id"
            class="flex items-center justify-between rounded-md border border-border px-3 py-2 text-sm hover:bg-muted/40"
          >
            <div>
              <span class="font-medium">{{ tarefa.descricao }}</span>
              <span v-if="tarefa.categoria" class="ml-2 text-xs text-muted-foreground">({{ tarefa.categoria }})</span>
            </div>
            <button
              type="button"
              class="text-blue-400 hover:text-blue-300 ml-3 flex-shrink-0"
              :disabled="salvandoChecklist"
              @click="adicionarTarefa(tarefa)"
            >
              <Plus class="w-4 h-4" />
            </button>
          </li>
        </ul>
      </div>

      <!-- Criar nova tarefa -->
      <div class="border-t border-border pt-4">
        <div v-if="!showCriarTarefa">
          <Button
            type="button"
            variant="outline"
            class="w-full border-dashed border-border hover:border-blue-500/50 hover:bg-blue-500/5 text-muted-foreground hover:text-blue-400"
            @click="showCriarTarefa = true"
          >
            <Plus class="w-4 h-4 mr-2" /> Criar nova tarefa
          </Button>
        </div>

        <div
          v-else
          class="p-4 rounded-lg border border-blue-500/30 bg-blue-500/5 space-y-3"
        >
          <div class="flex items-center justify-between">
            <h4 class="text-sm font-semibold text-blue-400">Nova Tarefa</h4>
            <button type="button" class="text-muted-foreground hover:text-foreground" @click="showCriarTarefa = false; novaTarefaDescricao = ''; novaTarefaCategoria = ''">
              <X class="w-4 h-4" />
            </button>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground/80">
              Descrição <span class="text-red-500 font-bold">*</span>
            </label>
            <Input
              v-model="novaTarefaDescricao"
              placeholder="Ex: Verificar tensão elétrica"
            />
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground/80">Categoria</label>
            <Input
              v-model="novaTarefaCategoria"
              placeholder="Ex: Elétrica, Mecânica, Software..."
            />
          </div>

          <div class="flex justify-end gap-2 pt-1">
            <Button
              type="button"
              variant="ghost"
              size="sm"
              @click="showCriarTarefa = false; novaTarefaDescricao = ''; novaTarefaCategoria = ''"
            >
              Cancelar
            </Button>
            <Button
              type="button"
              size="sm"
              class="bg-blue-600 hover:bg-blue-500 text-white"
              :disabled="!novaTarefaDescricao.trim() || criandoTarefa"
              @click="criarEAdicionarTarefa"
            >
              <Loader2 v-if="criandoTarefa" class="w-3 h-3 mr-1.5 animate-spin" />
              Criar e Adicionar
            </Button>
          </div>
        </div>
      </div>

      <div class="flex items-center justify-end border-t pt-6 gap-3">
        <Button type="button" variant="ghost" @click="step = 1">Voltar</Button>
        <Button type="button" class="bg-blue-600 hover:bg-blue-700 text-white px-8" @click="concluir">
          Concluir
        </Button>
      </div>
    </div>

  </div>
</template>
