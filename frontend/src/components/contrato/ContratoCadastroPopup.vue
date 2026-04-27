<template>
  <Dialog :open="open" @update:open="closeDialog">
    <DialogContent class="sm:max-w-[850px] max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle class="text-xl font-bold text-foreground">{{ isEditMode ? 'Editar Contrato' : 'Novo Contrato' }}</DialogTitle>
      </DialogHeader>

      <div class="flex items-center gap-2 mb-6 border-b border-border pb-6 mt-2">
        <div :class="['flex items-center gap-2 transition-colors', step === 1 ? 'text-blue-400 font-bold' : step > 1 ? 'text-blue-400/60' : 'text-muted-foreground']">
          <div
            class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
            :class="step === 1 ? 'border-blue-500 bg-blue-500/20 text-blue-400' : step > 1 ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60' : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
          >
            <FileText class="w-4 h-4" />
          </div>
          <span class="text-sm hidden sm:inline-block">Dados do Contrato</span>
        </div>

        <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

        <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-400 font-bold' : step > 2 ? 'text-blue-400/60' : 'text-muted-foreground']">
          <div
            class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
            :class="step === 2 ? 'border-blue-500 bg-blue-500/20 text-blue-400' : step > 2 ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60' : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
          >
            <Server class="w-4 h-4" />
          </div>
          <span class="text-sm hidden sm:inline-block">Máquinas Base</span>
        </div>

        <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

        <div :class="['flex items-center gap-2 transition-colors', step === 3 ? 'text-blue-400 font-bold' : 'text-muted-foreground']">
          <div
            class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
            :class="step === 3 ? 'border-blue-500 bg-blue-500/20 text-blue-400' : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
          >
            <Layers class="w-4 h-4" />
          </div>
          <span class="text-sm hidden sm:inline-block">Softwares (Opcional)</span>
        </div>
      </div>

      <form @submit="onSubmit">
        
        <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">
          <FormField v-slot="{ value, handleChange }" name="codigoCliente">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Cliente <span class="text-red-500 font-bold">*</span></FormLabel>
              <Select :model-value="value" @update:model-value="handleChange">
                <FormControl><SelectTrigger><SelectValue placeholder="Selecione um cliente..." /></SelectTrigger></FormControl>
                <SelectContent class="z-[200] max-h-[200px]">
                  <SelectItem v-for="c in clientes" :key="c.id" :value="c.id.toString()">
                    {{ c.nomeEmpresa }} ({{ c.documento }})
                  </SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ value, handleChange }" name="status">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Status do Contrato <span class="text-red-500 font-bold">*</span></FormLabel>
              <Select :model-value="value" @update:model-value="handleChange">
                <FormControl><SelectTrigger><SelectValue placeholder="Status" /></SelectTrigger></FormControl>
                <SelectContent class="z-[200]">
                  <SelectItem value="ATIVO">Ativo</SelectItem>
                  <SelectItem value="INATIVO">Inativo</SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ value, handleChange }" name="dataInicio">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Data de Início <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl>
                <DatePickerInput :model-value="value" @update:model-value="handleChange" :min-value="dataInicioMinValue" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ value, handleChange }" name="dataFim">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Data de Fim <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl>
                <DatePickerInput :model-value="value" @update:model-value="handleChange" :min-value="dataFimMinValue" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="periodoManutencaoPreventiva">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Período de Manutenção (meses) <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Input type="number" placeholder="Ex: 6" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="descricao">
            <FormItem class="md:col-span-2">
              <FormLabel class="flex items-center gap-1">Descrição do Contrato <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Textarea placeholder="Detalhes do contrato, SLA, etc..." class="resize-y min-h-[80px]" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <label class="md:col-span-2 flex flex-row items-center gap-3 cursor-pointer rounded-md border border-border p-4 bg-muted/20 hover:bg-muted/30 transition-colors">
            <div class="relative flex items-center justify-center w-4 h-4 shrink-0">
              <input
                v-model="conexaoInternet"
                type="checkbox"
                class="appearance-none w-4 h-4 rounded-[4px] border border-input bg-background checked:bg-blue-600 checked:border-blue-600 cursor-pointer transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              />
              <svg v-if="conexaoInternet" class="absolute w-2.5 h-2.5 text-white pointer-events-none" viewBox="0 0 10 10" fill="none">
                <path d="M1.5 5l2.5 2.5 4.5-4.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="space-y-1 leading-none">
              <span class="text-sm font-medium text-foreground">Acesso à Internet</span>
              <p class="text-xs text-muted-foreground">Marque se a infraestrutura deste contrato possui uma boa conexão com a internet.</p>
            </div>
          </label>
        </div>

        <div v-show="step === 2" class="space-y-6">
          <div v-if="maquinasFields.length === 0" class="text-center p-8 border border-dashed rounded-lg bg-muted/10">
            <p class="text-sm text-red-500 font-bold">Você precisa adicionar pelo menos uma máquina.</p>
          </div>

          <div v-for="(field, index) in maquinasFields" :key="field.key" class="relative p-5 border rounded-lg bg-card shadow-sm group">
            <div class="flex justify-between items-center mb-4 border-b pb-2">
              <h4 class="font-bold text-sm">Máquina {{ index + 1 }}</h4>
              <Button v-if="maquinasFields.length > 1" type="button" variant="ghost" size="icon" class="text-muted-foreground hover:text-red-500 hover:bg-red-50" @click="removeMaquina(index)">
                <Trash2 class="w-4 h-4" />
              </Button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4">
              <FormField :name="`maquinas[${index}].codigoMaquina`" v-slot="{ value, handleChange }">
                <FormItem class="md:col-span-2">
                  <FormLabel class="flex items-center gap-1">Modelo do Catálogo <span class="text-red-500 font-bold">*</span></FormLabel>
                  <Select :model-value="value" @update:model-value="handleChange">
                    <FormControl><SelectTrigger><SelectValue placeholder="Selecione um modelo no catálogo" /></SelectTrigger></FormControl>
                    <SelectContent class="z-[200] max-h-[200px]">
                      <SelectItem v-for="m in catalogoMaquinas" :key="m.codigo" :value="m.codigo.toString()">
                        {{ m.descricao }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField :name="`maquinas[${index}].numeroSerie`" v-slot="{ componentField }">
                <FormItem>
                  <FormLabel class="text-sm font-medium">Número de Série / Patrimônio</FormLabel>
                  <FormControl><Input placeholder="Ex: INV-2023-441" v-bind="componentField" /></FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField :name="`maquinas[${index}].dataInstalacao`" v-slot="{ componentField }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Data de Instalação <span class="text-red-500 font-bold">*</span></FormLabel>
                  <FormControl><Input type="date" v-bind="componentField" /></FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>
            </div>
          </div>

          <Button type="button" variant="outline" class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20" @click="pushMaquina({ codigoMaquina: '', numeroSerie: '', dataInstalacao: '' })">
            <Plus class="w-4 h-4 mr-2" /> Adicionar Outra Máquina
          </Button>
        </div>

        <div v-show="step === 3" class="space-y-6">
          <div v-if="softwaresFields.length === 0" class="text-center p-8 border border-dashed rounded-lg bg-muted/10">
            <p class="text-sm text-muted-foreground">Nenhum software vinculado. Você pode adicionar as licenças instaladas nas máquinas.</p>
          </div>

          <div v-for="(field, index) in softwaresFields" :key="field.key" class="relative p-5 border rounded-lg bg-card shadow-sm group">
            <Button type="button" variant="ghost" size="icon" class="absolute top-3 right-3 text-muted-foreground hover:text-red-500 hover:bg-red-50" @click="removeSoftware(index)">
              <Trash2 class="w-4 h-4" />
            </Button>

            <div class="grid grid-cols-1 gap-4 pr-10">
              <FormField :name="`softwares[${index}].maquinaIndex`" v-slot="{ value, handleChange }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Em qual máquina será instalado? <span class="text-red-500 font-bold">*</span></FormLabel>
                  <Select :model-value="value !== undefined ? String(value) : undefined" @update:model-value="v => handleChange(Number(v))">
                    <FormControl><SelectTrigger><SelectValue placeholder="Selecione a máquina da lista..." /></SelectTrigger></FormControl>
                    <SelectContent class="z-[200]">
                      <SelectItem v-for="(maq, maqIdx) in formValues.maquinas" :key="maqIdx" :value="String(maqIdx)">
                        {{ maq.apelido || `Máquina ${maqIdx + 1}` }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              </FormField>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <FormField :name="`softwares[${index}].codigoSoftware`" v-slot="{ value, handleChange }">
                  <FormItem>
                    <FormLabel class="flex items-center gap-1">Software <span class="text-red-500 font-bold">*</span></FormLabel>
                    <Select :model-value="value" @update:model-value="handleChange">
                      <FormControl><SelectTrigger><SelectValue placeholder="Selecione o software..." /></SelectTrigger></FormControl>
                      <SelectContent class="z-[200] max-h-[200px]">
                        <SelectItem v-for="s in catalogoSoftwares" :key="s.id" :value="s.id.toString()">
                          {{ s.nomeSoftware }} - {{ s.versao }}
                        </SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                </FormField>

                <FormField :name="`softwares[${index}].versaoInstalada`" v-slot="{ componentField }">
                  <FormItem>
                    <FormLabel class="flex items-center gap-1">Versão / Chave <span class="text-red-500 font-bold">*</span></FormLabel>
                    <FormControl><Input placeholder="Ex: 2.1.0 ou Key-123" v-bind="componentField" /></FormControl>
                    <FormMessage />
                  </FormItem>
                </FormField>
              </div>
            </div>
          </div>

          <Button type="button" variant="outline" class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20" @click="pushSoftware({ maquinaIndex: undefined, codigoSoftware: '', versaoInstalada: '' })">
            <Plus class="w-4 h-4 mr-2" /> Adicionar Software
          </Button>
        </div>

        <div class="border-t border-border mt-12 pt-6 space-y-3">
          <p v-if="erroValidacao" class="text-sm text-red-400 bg-red-500/10 border border-red-500/30 rounded-md px-4 py-2">
            {{ erroValidacao }}
          </p>
          <div class="flex items-center justify-between">
            <Button type="button" variant="ghost" class="hover:bg-muted/30" @click="closeDialog(false)">Cancelar</Button>

            <div class="flex gap-3">
              <Button v-if="step > 1" type="button" variant="outline" class="border-border hover:bg-muted/30" @click="step--">Voltar</Button>

              <Button v-if="step < totalSteps" type="button" class="bg-blue-600 hover:bg-blue-500 text-white px-8 shadow-md shadow-blue-900/20" @click="nextStep">
                Próximo <ArrowRight class="w-4 h-4 ml-2" />
              </Button>

              <Button v-if="step === totalSteps" type="submit" class="bg-emerald-600 hover:bg-emerald-500 text-white px-8 shadow-md shadow-emerald-900/20" :disabled="loading">
                {{ loading ? 'Salvando...' : isEditMode ? 'Salvar Alterações' : 'Finalizar Contrato' }}
              </Button>
            </div>
          </div>
        </div>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useForm, useFieldArray } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { CalendarDate } from '@internationalized/date'

import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { DatePickerInput } from '@/components/ui/date-picker'
import { ChevronRight, FileText, Server, Layers, Plus, Trash2, ArrowRight } from 'lucide-vue-next'

import { clienteService } from '@/services/clienteService'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'
import { catalogoMaquinaService } from '@/services/catalogoMaquinaService'
import { maquinaContratoService } from '@/services/maquinaContratoService'
import { catalogoSoftwareService } from '@/services/catalogoSoftwareService.ts'
import { maquinaSoftwareInstaladoService } from '@/services/maquinaSoftwareInstaladoService'

const props = defineProps<{
  open: boolean
  initialData?: ContratoResponseDTO | null
}>()
const emit = defineEmits<{
  'update:open': [value: boolean]
  success: [contrato: ContratoResponseDTO]
}>()

const isEditMode = computed(() => !!props.initialData)

const step = ref(1)
const loading = ref(false)
const erroValidacao = ref('')
const conexaoInternet = ref(false)

// IDs das máquinas/softwares existentes em modo edição (índice paralelo ao fieldArray)
const maquinasCodigos = ref<Array<number | null>>([])
const softwaresCodigos = ref<Array<number | null>>([])

const clientes = ref<any[]>([])
const catalogoMaquinas = ref<any[]>([])
const catalogoSoftwares = ref<any[]>([])

onMounted(async () => {
  try {
    clientes.value = await clienteService.listar()
    catalogoMaquinas.value = await catalogoMaquinaService.listarTodos()
    catalogoSoftwares.value = await catalogoSoftwareService.listar()
  } catch (error) {
    console.error("Erro ao carregar dados:", error)
  }
})

const formSchema = toTypedSchema(z.object({
  codigoCliente: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  status: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  dataInicio: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  dataFim: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  periodoManutencaoPreventiva: z.coerce.number({ required_error: 'Campo obrigatório' }).min(1, 'Informe um período válido'),
  descricao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),

  maquinas: z.array(z.object({
    codigoMaquina: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    numeroSerie: z.string().optional().default(''),
    dataInstalacao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  })).min(1, 'Adicione pelo menos uma máquina'),

  softwares: z.array(z.object({
    maquinaIndex: z.number({ required_error: 'Campo obrigatório' }),
    codigoSoftware: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    versaoInstalada: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório')
  })).optional().default([])
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    status: 'ATIVO',
    periodoManutencaoPreventiva: 6,
    descricao: '',
    maquinas: [{ codigoMaquina: '', numeroSerie: '', dataInstalacao: '' }],
    softwares: []
  }
})

const formValues = form.values

const dataInicioMinValue = new CalendarDate(2024, 1, 1)

const dataFimMinValue = computed(() => {
  const inicio = form.values.dataInicio
  if (!inicio) return undefined
  const [year, month, day] = inicio.split('-').map(Number)
  return new CalendarDate(year, month, day)
})

const {
  fields: maquinasFields,
  push: pushMaquinaBase,
  remove: removeMaquinaBase,
  replace: replaceMaquinas
} = useFieldArray('maquinas')

const {
  fields: softwaresFields,
  push: pushSoftwareBase,
  remove: removeSoftwareBase,
  replace: replaceSoftwares
} = useFieldArray('softwares')

function pushMaquina(data: any) {
  pushMaquinaBase(data)
  maquinasCodigos.value.push(null)
}

function removeMaquina(index: number) {
  removeMaquinaBase(index)
  maquinasCodigos.value.splice(index, 1)
}

function pushSoftware(data: any) {
  pushSoftwareBase(data)
  softwaresCodigos.value.push(null)
}

function removeSoftware(index: number) {
  removeSoftwareBase(index)
  softwaresCodigos.value.splice(index, 1)
}

const totalSteps = 3

const popularFormEdicao = async (data: ContratoResponseDTO) => {
  await nextTick()
  maquinasCodigos.value = []
  softwaresCodigos.value = []

  let maquinasFormData: any[] = [{ codigoMaquina: '', numeroSerie: '', dataInstalacao: '' }]
  let softwaresFormData: any[] = []

  try {
    const maquinas = await maquinaContratoService.buscarPorContrato(data.codigo)
    if (maquinas.length > 0) {
      maquinasCodigos.value = maquinas.map(m => m.codigo)
      maquinasFormData = maquinas.map(m => ({
        codigoMaquina: String(m.codigoCatalogoMaquina ?? ''),
        numeroSerie: m.numeroSerie ?? '',
        dataInstalacao: m.dataInstalacao ?? ''
      }))

      const softwaresPorMaquina = await Promise.all(
        maquinas.map(m => maquinaSoftwareInstaladoService.buscarPorMaquinaContrato(m.codigo).catch(() => []))
      )
      softwaresPorMaquina.forEach((softwares, maqIndex) => {
        softwares.forEach(sw => {
          softwaresCodigos.value.push(sw.codigo)
          softwaresFormData.push({
            maquinaIndex: maqIndex,
            codigoSoftware: String(sw.codigoSoftware),
            versaoInstalada: sw.versaoInstalada
          })
        })
      })
    }
  } catch (error) {
    console.error('Erro ao carregar máquinas do contrato:', error)
  }

  conexaoInternet.value = data.conexaoInternet === true

  form.resetForm({
    values: {
      codigoCliente: data.codigoCliente?.toString() ?? '',
      status: data.status ?? 'ATIVO',
      dataInicio: data.dataInicio ?? '',
      dataFim: data.dataFim ?? '',
      periodoManutencaoPreventiva: data.periodoManutencaoPreventiva ?? 6,
      descricao: data.descricao ?? '',
      maquinas: maquinasFormData,
      softwares: softwaresFormData
    }
  })
  await nextTick()
  replaceMaquinas(maquinasFormData)
  if (softwaresFormData.length > 0) replaceSoftwares(softwaresFormData)
}

watch(() => props.initialData, (data) => {
  if (data) popularFormEdicao(data)
}, { immediate: true })

watch(() => props.open, (val) => {
  if (val && props.initialData) popularFormEdicao(props.initialData)
  if (!val) erroValidacao.value = ''
})

const nextStep = async () => {
  erroValidacao.value = ''
  let fieldsToValidate: string[] = []

  if (step.value === 1) {
    fieldsToValidate = ['codigoCliente', 'status', 'dataInicio', 'dataFim', 'periodoManutencaoPreventiva', 'descricao']
  } else if (step.value === 2) {
    fieldsToValidate = maquinasFields.value.flatMap((_, i) => [
      `maquinas[${i}].codigoMaquina`,
      `maquinas[${i}].dataInstalacao`,
    ])
  }

  const results = await Promise.all(
    fieldsToValidate.map(field => form.validateField(field as any))
  )
  const hasErrors = results.some(r => !r.valid)

  if (hasErrors) {
    erroValidacao.value = 'Corrija os campos destacados antes de continuar.'
    return
  }

  if (step.value === 1) {
    const { dataInicio, dataFim, periodoManutencaoPreventiva } = form.values
    if (dataInicio && dataFim && dataFim < dataInicio) {
      erroValidacao.value = 'A data de fim não pode ser anterior à data de início.'
      return
    }
    if (dataInicio && dataFim && periodoManutencaoPreventiva) {
      const inicio = new Date(`${dataInicio}T00:00:00`)
      const fim = new Date(`${dataFim}T00:00:00`)
      const mesesContrato = (fim.getFullYear() - inicio.getFullYear()) * 12 + (fim.getMonth() - inicio.getMonth())
      if (periodoManutencaoPreventiva > mesesContrato) {
        erroValidacao.value = `O período de manutenção (${periodoManutencaoPreventiva} meses) não pode ser maior que a duração do contrato (${mesesContrato} meses).`
        return
      }
    }
  }

  step.value++
}

function closeDialog(val: boolean) {
  emit('update:open', val)
  if (!val) {
    step.value = 1
    erroValidacao.value = ''
    conexaoInternet.value = false
    form.resetForm()
    maquinasCodigos.value = []
    softwaresCodigos.value = []
  }
}

const onSubmit = form.handleSubmit(async (values) => {
  erroValidacao.value = ''
  loading.value = true
  try {
    console.log('[DEBUG] conexaoInternet.value no submit:', conexaoInternet.value)
    const payload = {
      codigoCliente: Number(values.codigoCliente),
      dataInicio: values.dataInicio,
      dataFim: values.dataFim,
      status: values.status,
      periodoManutencaoPreventiva: values.periodoManutencaoPreventiva,
      conexaoInternet: conexaoInternet.value,
    }
    console.log('[DEBUG] payload.conexaoInternet:', payload.conexaoInternet)

    let contratoSalvo: ContratoResponseDTO

    if (isEditMode.value && props.initialData) {
      contratoSalvo = await contratoService.atualizar(props.initialData.codigo, payload)

      const maquinasGeradasIds: number[] = []
      for (let i = 0; i < values.maquinas.length; i++) {
        const maq = values.maquinas[i]
        const existingId = maquinasCodigos.value[i] ?? null

        if (existingId) {
          await maquinaContratoService.atualizar(existingId, {
            codigoContrato: contratoSalvo.codigo,
            codigoCatalogoMaquina: Number(maq.codigoMaquina),
            numeroSerie: maq.numeroSerie || undefined,
            dataInstalacao: maq.dataInstalacao,
          })
          maquinasGeradasIds.push(existingId)
        } else {
          const maquinaSalva = await maquinaContratoService.criar({
            codigoContrato: contratoSalvo.codigo,
            codigoCatalogoMaquina: Number(maq.codigoMaquina),
            numeroSerie: maq.numeroSerie || undefined,
            dataInstalacao: maq.dataInstalacao,
          })
          maquinasGeradasIds.push(maquinaSalva.codigo)
        }
      }

      // Deletar máquinas removidas pelo usuário
      for (const id of maquinasCodigos.value) {
        if (id && !maquinasGeradasIds.includes(id)) {
          await maquinaContratoService.deletar(id).catch(() => {})
        }
      }

      // Deletar todos os softwares existentes e recriar
      await Promise.all(
        softwaresCodigos.value
          .filter(Boolean)
          .map(id => maquinaSoftwareInstaladoService.deletar(id!).catch(() => {}))
      )

      if (values.softwares && values.softwares.length > 0) {
        await Promise.all(values.softwares.map(soft => {
          const idRealMaquina = maquinasGeradasIds[soft.maquinaIndex]
          if (idRealMaquina === undefined) return
          return maquinaSoftwareInstaladoService.criar({
            codigoMaquinaContrato: idRealMaquina,
            codigoSoftware: Number(soft.codigoSoftware),
            versaoInstalada: soft.versaoInstalada,
          } as any)
        }))
      }
    } else {
      contratoSalvo = await contratoService.criar(payload)
      const contratoId = contratoSalvo.codigo

      const maquinasGeradasIds: number[] = []
      for (const maq of values.maquinas) {
        const maquinaSalva = await maquinaContratoService.criar({
          codigoContrato: contratoId,
          codigoCatalogoMaquina: Number(maq.codigoMaquina),
          numeroSerie: maq.numeroSerie || undefined,
          dataInstalacao: maq.dataInstalacao,
        })
        maquinasGeradasIds.push(maquinaSalva.codigo)
      }

      if (values.softwares && values.softwares.length > 0) {
        await Promise.all(values.softwares.map(soft => {
          const idRealMaquina = maquinasGeradasIds[soft.maquinaIndex]
          if (idRealMaquina === undefined) throw new Error('Máquina vinculada ao software não encontrada.')
          return maquinaSoftwareInstaladoService.criar({
            codigoMaquinaContrato: idRealMaquina,
            codigoSoftware: Number(soft.codigoSoftware),
            versaoInstalada: soft.versaoInstalada,
          } as any)
        }))
      }
    }

    closeDialog(false)
    emit('success', contratoSalvo)
  } catch (error) {
    console.error("Erro ao salvar contrato:", error)
    erroValidacao.value = 'Ocorreu um erro na comunicação com o servidor. Verifique o console.'
  } finally {
    loading.value = false
  }
}, () => {
  erroValidacao.value = 'Preencha todos os campos obrigatórios antes de salvar.'
})
</script>
