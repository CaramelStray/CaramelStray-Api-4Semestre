<template>
  <!-- Steps Header -->
  <div class="flex items-center gap-2 mb-6 border-b border-border pb-6 mt-2">
    <div :class="['flex items-center gap-2 transition-colors', step === 1 ? 'text-blue-400 font-bold' : step > 1 ? 'text-blue-400/60' : 'text-muted-foreground']">
      <div
        class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
        :class="step === 1
          ? 'border-blue-500 bg-blue-500/20 text-blue-400'
          : step > 1
            ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60'
            : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
      >
        <CheckCircle2 v-if="step > 1" class="w-4 h-4 text-blue-400/60" />
        <FileText v-else class="w-4 h-4" />
      </div>
      <span class="text-sm hidden sm:inline-block">Identificação</span>
    </div>

    <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

    <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-400 font-bold' : step > 2 ? 'text-blue-400/60' : 'text-muted-foreground']">
      <div
        class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
        :class="step === 2
          ? 'border-blue-500 bg-blue-500/20 text-blue-400'
          : step > 2
            ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60'
            : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
      >
        <CheckCircle2 v-if="step > 2" class="w-4 h-4 text-blue-400/60" />
        <Server v-else class="w-4 h-4" />
      </div>
      <span class="text-sm hidden sm:inline-block">Máquina</span>
    </div>

    <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

    <div :class="['flex items-center gap-2 transition-colors', step === 3 ? 'text-blue-400 font-bold' : 'text-muted-foreground']">
      <div
        class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
        :class="step === 3
          ? 'border-blue-500 bg-blue-500/20 text-blue-400'
          : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
      >
        <UserCheck class="w-4 h-4" />
      </div>
      <span class="text-sm hidden sm:inline-block">Técnico</span>
    </div>
  </div>

  <form @submit="onSubmit">

    <!-- STEP 1: Identificação -->
    <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">

      <!-- Cliente -->
      <FormField v-slot="{ value, handleChange }" name="codigoCliente">
        <FormItem class="md:col-span-2">
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Cliente <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            @update:model-value="val => { handleChange(String(val)); onClienteChange(String(val)) }"
          >
            <FormControl>
              <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                <SelectValue placeholder="Selecione um cliente..." />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200] max-h-[200px]">
              <SelectItem v-for="c in clientes" :key="c.id" :value="c.id.toString()">
                {{ c.nomeEmpresa }} ({{ c.documento }})
              </SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Contrato -->
      <FormField v-slot="{ value, handleChange }" name="codigoContrato">
        <FormItem class="md:col-span-2">
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Contrato <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            :disabled="!selectedClienteId"
            @update:model-value="val => { handleChange(String(val)); onContratoChange(String(val)) }"
          >
            <FormControl>
              <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors disabled:opacity-50">
                <SelectValue :placeholder="selectedClienteId ? 'Selecione um contrato...' : 'Selecione um cliente primeiro'" />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200] max-h-[200px]">
              <SelectItem v-for="ct in contratosFiltrados" :key="ct.codigo" :value="ct.codigo.toString()">
                {{ ct.descricao }} — {{ ct.status }} ({{ ct.dataInicio }} até {{ ct.dataFim }})
              </SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Criticidade -->
      <FormField v-slot="{ value, handleChange }" name="criticidade">
        <FormItem>
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Criticidade <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            @update:model-value="val => handleChange(String(val))"
          >
            <FormControl>
              <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                <SelectValue placeholder="Selecione a criticidade..." />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200]">
              <SelectItem value="CRITICA">Crítica</SelectItem>
              <SelectItem value="ALTA">Alta</SelectItem>
              <SelectItem value="MEDIA">Média</SelectItem>
              <SelectItem value="BAIXA">Baixa</SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Data de Abertura — input nativo readonly -->
      <div class="space-y-2">
        <label class="flex items-center gap-1 text-sm font-medium text-foreground/80">
          Data de Abertura
        </label>
        <input
          :value="todayDisplayString"
          readonly
          class="flex h-9 w-full rounded-md border border-border/50 bg-muted/10 px-3 py-1 text-sm text-muted-foreground shadow-sm cursor-not-allowed"
        />
      </div>

      <!-- Data de Agendamento com Popover -->
      <FormField v-slot="{ value, handleChange }" name="dataAgendamento">
        <FormItem class="md:col-span-2">
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Data de Agendamento <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Popover v-model:open="calendarOpen">
            <PopoverTrigger as-child>
              <FormControl>
                <Button
                  variant="outline"
                  :class="['w-full justify-start text-left font-normal bg-muted/20 border-border hover:border-blue-500/50 transition-colors', !value && 'text-muted-foreground']"
                >
                  <CalendarIcon class="mr-2 h-4 w-4 text-muted-foreground" />
                  {{ value ? formatDateDisplay(value) : 'Selecione uma data...' }}
                </Button>
              </FormControl>
            </PopoverTrigger>
            <PopoverContent class="w-auto p-0 z-[200]" align="start">
              <Calendar
                :min-value="amanhaToCal"
                @update:model-value="val => { handleChange(calToString(val)); calendarOpen = false }"
              />
            </PopoverContent>
          </Popover>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Observação Geral -->
      <FormField v-slot="{ componentField }" name="observacaoGeral">
        <FormItem class="md:col-span-2">
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Observação Geral <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <FormControl>
            <Textarea
              placeholder="Descreva o problema ou motivo da ordem de serviço..."
              class="resize-y min-h-[90px] bg-muted/20 border-border hover:border-blue-500/50 transition-colors focus:border-blue-500"
              v-bind="componentField"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

    </div>

    <!-- STEP 2: Máquina -->
    <div v-show="step === 2" class="space-y-6">

      <div v-if="!selectedContratoId" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/5">
        <Server class="w-8 h-8 mx-auto text-muted-foreground/40 mb-3" />
        <p class="text-sm text-muted-foreground">Selecione um contrato no passo anterior para ver as máquinas disponíveis.</p>
      </div>

      <div v-else>
        <FormField v-slot="{ value, handleChange }" name="codigoMaquinaContrato">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Máquina do Contrato <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select
              :model-value="value"
              @update:model-value="val => { handleChange(String(val)); onMaquinaChange(String(val)) }"
            >
              <FormControl>
                <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione uma máquina..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200] max-h-[200px]">
                <SelectItem v-for="m in maquinasFiltradas" :key="m.codigo" :value="m.codigo.toString()">
                  Máquina #{{ m.codigo }} — Cat. {{ m.codigoCatalogoMaquina }} (instalada em {{ m.dataInstalacao }})
                </SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Painel de Software Instalado -->
        <div v-if="selectedMaquinaId" class="mt-5">
          <div v-if="loadingSoftware" class="flex items-center gap-3 text-sm text-muted-foreground p-4 border border-border rounded-lg bg-muted/10">
            <Loader2 class="w-4 h-4 animate-spin text-blue-400" />
            Verificando software instalado...
          </div>

          <div v-else-if="softwareInstalado" class="p-5 border rounded-lg bg-blue-500/10 border-blue-500/25">
            <div class="flex items-center gap-2 mb-4">
              <div class="flex items-center justify-center w-8 h-8 rounded-full bg-blue-500/20">
                <PackageCheck class="w-4 h-4 text-blue-400" />
              </div>
              <h4 class="font-semibold text-sm text-blue-400">Software Instalado Detectado</h4>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div class="bg-blue-500/5 rounded-md p-3 border border-blue-500/15">
                <p class="text-[10px] uppercase tracking-wider text-muted-foreground font-medium mb-1">Software</p>
                <p class="text-sm font-semibold text-foreground">{{ softwareInstalado.nomeSoftware ?? `#${softwareInstalado.codigoSoftware}` }}</p>
              </div>
              <div class="bg-blue-500/5 rounded-md p-3 border border-blue-500/15">
                <p class="text-[10px] uppercase tracking-wider text-muted-foreground font-medium mb-1">Versão Instalada</p>
                <p class="text-sm font-semibold text-foreground">{{ softwareInstalado.versaoInstalada }}</p>
              </div>
              <div class="bg-blue-500/5 rounded-md p-3 border border-blue-500/15">
                <p class="text-[10px] uppercase tracking-wider text-muted-foreground font-medium mb-1">Código</p>
                <p class="text-sm font-semibold text-foreground font-mono">#{{ softwareInstalado.codigo }}</p>
              </div>
            </div>
            <p class="text-xs text-blue-400/70 mt-3 flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 rounded-full bg-blue-400/70 inline-block"></span>
              Este software será vinculado automaticamente à ordem de serviço.
            </p>
          </div>

          <div v-else class="p-5 border border-dashed border-border rounded-lg bg-muted/5">
            <div class="flex items-center gap-3">
              <div class="flex items-center justify-center w-8 h-8 rounded-full bg-muted/30">
                <PackageX class="w-4 h-4 text-muted-foreground/60" />
              </div>
              <div>
                <p class="text-sm font-medium text-foreground/70">Sem software instalado</p>
                <p class="text-xs text-muted-foreground">Nenhum software encontrado para esta máquina.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- STEP 3: Técnico -->
    <div v-show="step === 3" class="space-y-4">

      <div v-if="loadingTecnicos" class="flex items-center gap-3 text-sm text-muted-foreground p-4 border border-border rounded-lg bg-muted/10">
        <Loader2 class="w-4 h-4 animate-spin text-blue-400" />
        Carregando técnicos disponíveis...
      </div>

      <div v-else-if="tecnicosDisponiveis.length === 0" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/5">
        <UserX class="w-8 h-8 mx-auto text-muted-foreground/40 mb-3" />
        <p class="text-sm font-medium text-foreground/70">Nenhum técnico disponível</p>
        <p class="text-xs text-muted-foreground mt-1">Não há técnicos disponíveis no momento.</p>
      </div>

      <div v-else>
        <FormField v-slot="{ value, handleChange }" name="codigoFuncionario">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Técnico Responsável <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-3 mt-2">
              <div
                v-for="tec in tecnicosDisponiveis"
                :key="tec.id"
                :class="[
                  'flex items-start gap-4 p-4 border rounded-lg cursor-pointer transition-all',
                  value === tec.id.toString()
                    ? 'border-blue-500/60 bg-blue-500/10 ring-1 ring-blue-500/40'
                    : 'border-border bg-muted/10 hover:border-blue-500/30 hover:bg-muted/20'
                ]"
                @click="handleChange(tec.id.toString())"
              >
                <div
                  :class="[
                    'flex items-center justify-center w-10 h-10 rounded-full font-bold text-sm shrink-0 transition-all',
                    value === tec.id.toString()
                      ? 'bg-blue-500/30 text-blue-300'
                      : 'bg-muted/40 text-foreground/70'
                  ]"
                >
                  {{ getInitials(tec.nome) }}
                </div>
                <div class="min-w-0">
                  <p class="font-semibold text-sm truncate text-foreground">{{ tec.nome }}</p>
                  <p class="text-xs text-muted-foreground truncate">{{ tec.cargo || 'Técnico' }}</p>
                  <p class="text-xs text-muted-foreground/70 truncate">{{ tec.email }}</p>
                  <span class="inline-flex items-center gap-1.5 mt-1.5 text-[10px] font-semibold uppercase tracking-wider text-emerald-400 bg-emerald-500/15 border border-emerald-500/20 px-2 py-0.5 rounded-full">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 inline-block"></span>
                    {{ tec.disponibilidade ?? 'Disponível' }}
                  </span>
                </div>
              </div>
            </div>
            <FormMessage />
          </FormItem>
        </FormField>
      </div>
    </div>

    <!-- Footer Buttons -->
    <div class="flex items-center justify-end border-t border-border mt-10 pt-6">
      <div class="flex gap-3">
        <Button v-if="step > 1" type="button" variant="outline" class="border-border hover:bg-muted/30" @click="step--">
          Voltar
        </Button>

        <Button
          v-if="step < 3"
          type="button"
          class="bg-blue-600 hover:bg-blue-500 text-white px-8 shadow-md shadow-blue-900/20"
          @click="nextStep"
        >
          Próximo <ArrowRight class="w-4 h-4 ml-2" />
        </Button>

        <Button
          v-if="step === 3"
          type="submit"
          class="bg-emerald-600 hover:bg-emerald-500 text-white px-8 shadow-md shadow-emerald-900/20"
          :disabled="loading"
        >
          <Loader2 v-if="loading" class="w-4 h-4 mr-2 animate-spin" />
          {{ loading ? 'Salvando...' : 'Abrir Ordem de Serviço' }}
        </Button>
      </div>
    </div>

  </form>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { CalendarDate } from '@internationalized/date'

import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'
import {
  ChevronRight, FileText, Server, UserCheck, ArrowRight,
  CalendarIcon, Loader2, PackageCheck, PackageX, UserX, CheckCircle2
} from 'lucide-vue-next'

import { clienteService } from '@/services/clienteService'
import { maquinaSoftwareInstaladoService } from '@/services/maquinaSoftwareInstaladoService'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { ordemServicoService } from '@/services/ordemServicoService'

// ─── Emits ────────────────────────────────────────────────────────────────────
const emit = defineEmits(['fechar', 'success'])

// ─── State ────────────────────────────────────────────────────────────────────
const step = ref(1)
const loading = ref(false)
const loadingSoftware = ref(false)
const loadingTecnicos = ref(false)
const calendarOpen = ref(false)

const clientes = ref<any[]>([])
const tecnicosDisponiveis = ref<TecnicoResponseDTO[]>([])
const softwareInstalado = ref<any | null>(null)

const selectedClienteId = ref<string | null>(null)
const selectedContratoId = ref<string | null>(null)
const selectedMaquinaId = ref<string | null>(null)

// ─── Computed ─────────────────────────────────────────────────────────────────
const clienteSelecionado = computed(() =>
  clientes.value.find(c => c.id.toString() === selectedClienteId.value) ?? null
)
const contratosFiltrados = computed(() =>
  clienteSelecionado.value?.contratos ?? []
)
const contratoSelecionado = computed(() =>
  contratosFiltrados.value.find((ct: any) => ct.codigo.toString() === selectedContratoId.value) ?? null
)
const maquinasFiltradas = computed(() =>
  contratoSelecionado.value?.maquinas ?? []
)

// ─── Helpers de Data ─────────────────────────────────────────────────────────
const todayDisplayString = computed(() => {
  const now = new Date()
  const day   = String(now.getDate()).padStart(2, '0')
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const year  = now.getFullYear()
  return `${day}/${month}/${year}`
})

function toLocalDateTimeString(isoDate: string): string {
  if (!isoDate) return ''
  if (isoDate.includes('T')) return isoDate
  return `${isoDate}T00:00:00`
}

function todayLocalDateTime(): string {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

const amanhaToCal = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate())
})

function calToString(val: any): string {
  if (!val) return ''
  return val.toString()
}

function formatDateDisplay(isoDate: string): string {
  if (!isoDate) return ''
  const datePart = isoDate.split('T')[0]
  const [year, month, day] = datePart.split('-')
  return `${day}/${month}/${year}`
}

// ─── Zod Schema ───────────────────────────────────────────────────────────────
const formSchema = toTypedSchema(z.object({
  codigoCliente:         z.string({ required_error: '*' }).min(1, 'Selecione um cliente'),
  codigoContrato:        z.string({ required_error: '*' }).min(1, 'Selecione um contrato'),
  criticidade:           z.string({ required_error: '*' }).min(1, 'Selecione a criticidade'),
  dataAgendamento:       z.string({ required_error: '*' }).min(1, 'Selecione a data de agendamento'),
  observacaoGeral:       z.string({ required_error: '*' }).min(1, 'Informe a observação geral'),
  codigoMaquinaContrato: z.string({ required_error: '*' }).min(1, 'Selecione uma máquina'),
  codigoFuncionario:     z.string({ required_error: '*' }).min(1, 'Selecione um técnico'),
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    codigoCliente: '',
    codigoContrato: '',
    criticidade: '',
    dataAgendamento: '',
    observacaoGeral: '',
    codigoMaquinaContrato: '',
    codigoFuncionario: '',
  }
})

// ─── Carregamento inicial ─────────────────────────────────────────────────────
onMounted(async () => {
  try {
    clientes.value = await clienteService.listar()
  } catch (error) {
    console.error('Erro ao carregar clientes:', error)
  }
})

// ─── Handlers encadeados ─────────────────────────────────────────────────────
function onClienteChange(val: string) {
  selectedClienteId.value = val
  selectedContratoId.value = null
  selectedMaquinaId.value = null
  softwareInstalado.value = null
  form.setFieldValue('codigoContrato', '')
  form.setFieldValue('codigoMaquinaContrato', '')
}

function onContratoChange(val: string) {
  selectedContratoId.value = val
  selectedMaquinaId.value = null
  softwareInstalado.value = null
  form.setFieldValue('codigoMaquinaContrato', '')
}

async function onMaquinaChange(val: string) {
  selectedMaquinaId.value = val
  softwareInstalado.value = null
  if (!val) return

  loadingSoftware.value = true
  try {
    const resultados = await maquinaSoftwareInstaladoService.buscarPorMaquinaContrato(Number(val))
    softwareInstalado.value = resultados.length > 0 ? resultados[0] : null
  } catch (error: any) {
    const is404 = error?.message?.includes('404') || error?.status === 404
    if (!is404) console.error('Erro ao buscar software instalado:', error)
    softwareInstalado.value = null
  } finally {
    loadingSoftware.value = false
  }
}

// ─── Técnicos (lazy load no step 3) ──────────────────────────────────────────
watch(step, async (newStep) => {
  if (newStep === 3 && tecnicosDisponiveis.value.length === 0) {
    loadingTecnicos.value = true
    try {
      const todos = await tecnicoService.listar()
      tecnicosDisponiveis.value = todos.filter(
        t => t.disponibilidade !== null && t.disponibilidade !== undefined && t.disponibilidade !== ''
      )
    } catch (error) {
      console.error('Erro ao carregar técnicos:', error)
    } finally {
      loadingTecnicos.value = false
    }
  }
})

// ─── Validação por step ───────────────────────────────────────────────────────
const STEP_FIELDS: Record<number, string[]> = {
  1: ['codigoCliente', 'codigoContrato', 'criticidade', 'dataAgendamento', 'observacaoGeral'],
  2: ['codigoMaquinaContrato'],
}

const nextStep = async () => {
  const currentFields = STEP_FIELDS[step.value] ?? []
  const results = await Promise.all(
    currentFields.map(field => form.validateField(field as any))
  )
  const hasErrors = results.some(r => !r.valid)
  if (!hasErrors) step.value++
}

// ─── Helper ───────────────────────────────────────────────────────────────────
function getInitials(nome: string): string {
  return nome.split(' ').filter(Boolean).slice(0, 2).map(n => n[0].toUpperCase()).join('')
}

// ─── Submit ───────────────────────────────────────────────────────────────────
const onSubmit = form.handleSubmit(async (values) => {
  loading.value = true
  try {
    await ordemServicoService.criar({
      codigoCliente:           Number(values.codigoCliente),
      codigoContrato:          Number(values.codigoContrato),
      codigoMaquinaContrato:   Number(values.codigoMaquinaContrato),
      codigoFuncionario:       Number(values.codigoFuncionario),
      codigoSoftwareInstalado: softwareInstalado.value?.codigo ?? undefined,
      status:                  'AGENDADO',
      criticidade:             values.criticidade,
      dataAbertura:            todayLocalDateTime(),
      dataAgendamento:         toLocalDateTimeString(values.dataAgendamento ?? ''),
      observacaoGeral:         values.observacaoGeral,
    })

    emit('fechar')
    emit('success')
  } catch (error: any) {
    console.error('Erro ao abrir ordem de serviço:', error)
    alert('Ocorreu um erro ao salvar a ordem de serviço. Verifique o console.')
  } finally {
    loading.value = false
  }
})
</script>