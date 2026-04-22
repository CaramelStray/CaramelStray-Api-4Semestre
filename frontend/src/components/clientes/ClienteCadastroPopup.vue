<script setup lang="ts">
import { ref, watch, computed, nextTick } from 'vue'
import { useForm, useFieldArray } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Plus, Trash2, ChevronRight, Building2, Users, ArrowRight, CheckCircle2 } from 'lucide-vue-next'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'

const props = defineProps<{
  initialData?: ClienteResponseDTO | null
}>()

const emit = defineEmits<{
  fechar: []
  sucesso: [cliente: ClienteResponseDTO]
}>()

const isEditMode = computed(() => !!props.initialData)

const step = ref(1)

const paises = [
  'Brasil',
  'Estados Unidos',
  'Colômbia',
  'Equador',
  'Peru',
  'Argentina',
  'Bolívia',
  'Trinidad e Tobago',
  'Arábia Saudita',
  'Emirados Árabes Unidos',
  'Kuwait',
  'Iraque',
  'Irã',
  'Qatar',
  'Omã',
  'Bahrein',
  'Noruega',
  'Reino Unido',
  'Rússia',
  'Cazaquistão',
  'Azerbaijão',
]

function blockNonNumeric(e: KeyboardEvent) {
  const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End']
  if (allowed.includes(e.key)) return
  if ((e.ctrlKey || e.metaKey) && ['a', 'c', 'v', 'x'].includes(e.key.toLowerCase())) return
  if (!/^\d$/.test(e.key)) e.preventDefault()
}

function blockNonNumericPaste(e: ClipboardEvent) {
  e.preventDefault()
  const text = e.clipboardData?.getData('text') ?? ''
  const digits = text.replace(/\D/g, '')
  document.execCommand('insertText', false, digits)
}

const applyPhoneMask = (val: any) => {
  const digits = String(val ?? '').replace(/\D/g, '').slice(0, 11)
  if (digits.length <= 2)  return `(${digits}`
  if (digits.length <= 6)  return `(${digits.slice(0,2)}) ${digits.slice(2)}`
  if (digits.length <= 10) return `(${digits.slice(0,2)}) ${digits.slice(2,6)}-${digits.slice(6)}`
  return `(${digits.slice(0,2)}) ${digits.slice(2,7)}-${digits.slice(7)}`
}

const applyCnpjMask = (val: any) => {
  const digits = String(val ?? '').replace(/\D/g, '').slice(0, 14)
  if (digits.length <= 2)  return digits
  if (digits.length <= 5)  return `${digits.slice(0,2)}.${digits.slice(2)}`
  if (digits.length <= 8)  return `${digits.slice(0,2)}.${digits.slice(2,5)}.${digits.slice(5)}`
  if (digits.length <= 12) return `${digits.slice(0,2)}.${digits.slice(2,5)}.${digits.slice(5,8)}/${digits.slice(8)}`
  return `${digits.slice(0,2)}.${digits.slice(2,5)}.${digits.slice(5,8)}/${digits.slice(8,12)}-${digits.slice(12)}`
}

// ─── Schema ───────────────────────────────────────────────────────────────────
const formSchema = toTypedSchema(z.object({
  internacional:           z.boolean().default(false),
  nomeEmpresa:             z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  documento:               z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  email:                   z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório').email('E-mail inválido'),
  telefone:                z.string({ required_error: 'Campo obrigatório' }).min(14, 'Telefone inválido'),
  responsavel:             z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  rua:                     z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  numero:                  z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  pais:                    z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  estado:                  z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  cidade:                  z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  fusoHorario:             z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  classificacaoDistancia:  z.enum(['Regional', 'Nacional', 'Internacional'], {
    required_error: 'Campo obrigatório',
    invalid_type_error: 'Selecione uma classificação',
  }),
  observacoes:             z.string().optional().default(''),
  contatos: z.array(z.object({
    nome:     z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    email:    z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório').email('E-mail inválido'),
    telefone: z.string({ required_error: 'Campo obrigatório' }).min(14, 'Telefone inválido'),
  })).optional().default([])
}).refine((data) => {
  if (!data.internacional) return data.documento.length === 18
  return true
}, {
  message: 'CNPJ inválido',
  path: ['documento'],
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    internacional: false,
    contatos: [{ nome: '', email: '', telefone: '' }],
    observacoes: '',
    documento: '',
    telefone: '',
  }
})

const { fields, push, remove, replace } = useFieldArray<{ nome: string; email: string; telefone: string }>('contatos')

const popularFormEdicao = async (data: ClienteResponseDTO) => {
  await nextTick()
  const contatosExistentes = ((data as any).contatos ?? []).map((c: any) => ({
    nome: c.nome || c.nomeContato || '',
    email: c.email || c.emailContato || '',
    telefone: c.telefone || c.telefoneContato || ''
  }))
  form.resetForm({
    values: {
      internacional: data.internacional,
      nomeEmpresa: data.nomeEmpresa,
      documento: data.documento,
      email: data.emailContato,
      telefone: data.telefoneContato,
      responsavel: data.nomeResponsavel,
      rua: data.rua,
      numero: data.numero,
      pais: data.pais,
      estado: data.estadoRegiao,
      cidade: data.cidade,
      fusoHorario: data.fusoHorario,
      classificacaoDistancia: data.classificacaoDistancia as any,
      observacoes: data.observacao || '',
      contatos: contatosExistentes,
    }
  })
  await nextTick()
  if (contatosExistentes.length > 0) replace(contatosExistentes)
}

watch(() => props.initialData, (data) => {
  if (data) popularFormEdicao(data)
}, { immediate: true })

watch(() => form.values.internacional, () => {
  form.setFieldValue('documento', '')
  form.setFieldError('documento', undefined)
})

// ─── Navegação ────────────────────────────────────────────────────────────────
const nextStep = async () => {
  const step1Fields = [
    'nomeEmpresa', 'documento', 'responsavel', 'email', 'telefone',
    'rua', 'numero', 'pais', 'estado', 'cidade', 'fusoHorario', 'classificacaoDistancia',
  ]
  const results = await Promise.all(
    step1Fields.map(field => form.validateField(field as any))
  )
  const hasErrors = results.some(r => !r.valid)
  if (!hasErrors) step.value = 2
}


const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  try {
    const payload = {
      nomeEmpresa:            values.nomeEmpresa,
      documento:              values.documento,
      emailContato:           values.email,
      telefoneContato:        values.telefone,
      nomeResponsavel:        values.responsavel,
      pais:                   values.pais,
      estadoRegiao:           values.estado,
      cidade:                 values.cidade,
      fusoHorario:            values.fusoHorario,
      rua:                    values.rua,
      numero:                 values.numero,
      internacional:          values.internacional,
      observacao:             values.observacoes,
      ativo:                  true,
      classificacaoDistancia: values.classificacaoDistancia,
    }

    let cliente: ClienteResponseDTO
    if (isEditMode.value && props.initialData) {
      cliente = await clienteService.atualizar(props.initialData.id, payload)
    } else {
      cliente = await clienteService.criar(payload)
      resetForm()
      step.value = 1
    }

    emit('sucesso', cliente)
    emit('fechar')
  } catch (error: any) {
    const data = error.response?.data
    const msg = (data?.message || data?.error || JSON.stringify(data) || '').toLowerCase()
    if (msg.includes('documento') || msg.includes('cnpj') || msg.includes('already exists')) {
      form.setFieldError('documento', 'Este documento já está cadastrado no sistema.')
      step.value = 1
    } else if (msg.includes('email') || msg.includes('e-mail')) {
      form.setFieldError('email', 'Este e-mail já está em uso por outro cliente.')
      step.value = 1
    } else {
      alert('Erro na validação: ' + (data?.message || 'Verifique os dados informados.'))
    }
  }
})
</script>

<template>
  <div>
    <!-- Steps Header -->
    <div class="flex items-center gap-2 mb-8 border-b border-border pb-6">
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
          <Building2 v-else class="w-4 h-4" />
        </div>
        <span class="text-sm hidden sm:inline-block">Dados da Empresa</span>
      </div>

      <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

      <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-400 font-bold' : 'text-muted-foreground']">
        <div
          class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
          :class="step === 2
            ? 'border-blue-500 bg-blue-500/20 text-blue-400'
            : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
        >
          <Users class="w-4 h-4" />
        </div>
        <span class="text-sm hidden sm:inline-block">Contatos da Empresa</span>
      </div>
    </div>

    <form @submit="onSubmit">

      <!-- STEP 1 -->
      <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">

        <!-- Internacional -->
        <FormField v-slot="{ value, handleChange }" name="internacional">
          <FormItem class="col-span-1 md:col-span-2 lg:col-span-3 flex flex-row items-center space-x-3 space-y-0 rounded-md border border-border p-4 bg-muted/10">
            <FormControl>
              <Checkbox :checked="value" @update:checked="handleChange" />
            </FormControl>
            <div class="space-y-1 leading-none">
              <FormLabel class="text-sm font-medium text-foreground/80">Empresa Internacional</FormLabel>
              <p class="text-xs text-muted-foreground">Marque se a empresa não possui CNPJ ou está localizada fora do Brasil.</p>
            </div>
          </FormItem>
        </FormField>

        <!-- Nome da Empresa -->
        <FormField v-slot="{ componentField }" name="nomeEmpresa">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Nome da Empresa <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                placeholder="Razão Social ou Nome Fantasia"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- CNPJ -->
        <FormField v-slot="{ componentField }" name="documento">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              {{ form.values.internacional ? 'Documento (Internacional)' : 'CNPJ' }}
              <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                v-if="!form.values.internacional"
                type="text"
                inputmode="numeric"
                placeholder="00.000.000/0000-00"
                maxlength="18"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @keydown="blockNonNumeric"
                @paste="blockNonNumericPaste"
                @update:model-value="val => form.setFieldValue('documento', applyCnpjMask(val))"
                @blur="componentField.onBlur"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
              />
              <Input
                v-else
                type="text"
                placeholder="Digite o documento"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Responsável -->
        <FormField v-slot="{ componentField }" name="responsavel">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Nome do Responsável <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                placeholder="Responsável pelo contrato"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- E-mail -->
        <FormField v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              E-mail Corporativo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="email"
                placeholder="contato@empresa.com"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Telefone -->
        <FormField v-slot="{ componentField }" name="telefone">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Telefone Principal <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="tel"
                inputmode="numeric"
                placeholder="(00) 00000-0000"
                maxlength="15"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @keydown="blockNonNumeric"
                @paste="blockNonNumericPaste"
                @update:model-value="val => form.setFieldValue('telefone', applyPhoneMask(val))"
                @blur="componentField.onBlur"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- País -->
        <FormField v-slot="{ value, handleChange }" name="pais">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              País <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl>
                <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione o país..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200] max-h-[220px]">
                <SelectGroup>
                  <SelectItem v-for="p in paises" :key="p" :value="p">{{ p }}</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Estado -->
        <FormField v-slot="{ componentField }" name="estado">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Estado / Região <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                placeholder="Ex: São Paulo (SP)"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Cidade -->
        <FormField v-slot="{ componentField }" name="cidade">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Cidade <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                placeholder="Ex: São José dos Campos"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Rua -->
        <FormField v-slot="{ componentField }" name="rua">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Rua / Avenida <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                placeholder="Nome da rua"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="numero">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Número <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                inputmode="numeric"
                placeholder="123"
                maxlength="10"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @keydown="blockNonNumeric"
                @paste="blockNonNumericPaste"
                @update:model-value="val => form.setFieldValue('numero', String(val ?? '').replace(/\D/g, ''))"
                @blur="componentField.onBlur"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ value, handleChange }" name="fusoHorario">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Fuso Horário <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl>
                <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200]">
                <SelectGroup>
                  <SelectItem value="utc-2">UTC -2 (Fernando de Noronha)</SelectItem>
                  <SelectItem value="utc-3">UTC -3 (Horário de Brasília)</SelectItem>
                  <SelectItem value="utc-4">UTC -4 (Manaus/Amazonas)</SelectItem>
                  <SelectItem value="utc-5">UTC -5 (Acre/Chicago)</SelectItem>
                  <SelectItem value="utc-6">UTC -6 (Cidade do México)</SelectItem>
                  <SelectItem value="utc-8">UTC -8 (Los Angeles)</SelectItem>
                  <SelectItem value="utc+0">UTC +0 (Londres/Lisboa)</SelectItem>
                  <SelectItem value="utc+1">UTC +1 (Europa Central)</SelectItem>
                  <SelectItem value="utc+3">UTC +3 (Riad/Kuwait)</SelectItem>
                  <SelectItem value="utc+4">UTC +4 (Dubai/Abu Dhabi)</SelectItem>
                  <SelectItem value="utc+9">UTC +9 (Tóquio)</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ value, handleChange }" name="classificacaoDistancia">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Classificação de Distância <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl>
                <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200]">
                <SelectGroup>
                  <SelectItem value="Regional">Regional</SelectItem>
                  <SelectItem value="Nacional">Nacional</SelectItem>
                  <SelectItem value="Internacional">Internacional</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Observações -->
        <FormField v-slot="{ componentField }" name="observacoes">
          <FormItem class="col-span-1 md:col-span-2 lg:col-span-3">
            <FormLabel class="text-sm font-medium text-foreground/80">Observações</FormLabel>
            <FormControl>
              <Textarea
                placeholder="Informações adicionais..."
                class="resize-y min-h-[100px] bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                v-bind="componentField"
              />
            </FormControl>
          </FormItem>
        </FormField>
      </div>

      <!-- STEP 2: Contatos -->
      <div v-show="step === 2" class="space-y-6">
        <div v-if="fields.length === 0" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/5">
          <Users class="w-8 h-8 mx-auto text-muted-foreground/40 mb-3" />
          <p class="text-sm text-muted-foreground">Nenhum contato adicional cadastrado.</p>
        </div>

        <div
          v-for="(field, index) in fields"
          :key="field.key"
          class="relative p-5 border border-border rounded-lg bg-muted/5 shadow-sm"
        >
          <div class="flex items-center justify-between mb-4 border-b border-border pb-2">
            <h4 class="text-sm font-semibold text-foreground/80">Contato {{ index + 1 }}</h4>
            <Button
              type="button"
              variant="ghost"
              size="icon"
              class="text-muted-foreground hover:text-red-400 hover:bg-red-500/10"
              @click="remove(index)"
            >
              <Trash2 class="w-4 h-4" />
            </Button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField :name="`contatos[${index}].nome`" v-slot="{ componentField }">
              <FormItem>
                <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                  Nome do Contato <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl>
                  <Input
                    placeholder="Ex: Maria"
                    class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                    v-bind="componentField"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField :name="`contatos[${index}].email`" v-slot="{ componentField }">
              <FormItem>
                <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                  E-mail <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl>
                  <Input
                    type="email"
                    placeholder="maria@empresa.com"
                    class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                    v-bind="componentField"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField :name="`contatos[${index}].telefone`" v-slot="{ componentField: dynamicField }">
              <FormItem>
                <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                  Telefone / Celular <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl>
                  <Input
                    type="tel"
                    inputmode="numeric"
                    placeholder="(00) 00000-0000"
                    maxlength="15"
                    :name="dynamicField.name"
                    :model-value="dynamicField.modelValue"
                    @keydown="blockNonNumeric"
                    @paste="blockNonNumericPaste"
                    @update:model-value="val => form.setFieldValue(`contatos[${index}].telefone`, applyPhoneMask(val))"
                    @blur="dynamicField.onBlur"
                    class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>
          </div>
        </div>

        <Button
          type="button"
          variant="outline"
          class="w-full border-dashed border-2 border-border bg-muted/5 hover:bg-muted/20"
          @click="push({ nome: '', email: '', telefone: '' })"
        >
          <Plus class="w-4 h-4 mr-2" /> Adicionar novo contato
        </Button>
      </div>

      <div class="flex items-center justify-end border-t border-border mt-10 pt-6">
        <div class="flex gap-3">
          <Button type="button" variant="ghost" class="hover:bg-muted/30" @click="emit('fechar')">
            Cancelar
          </Button>

          <template v-if="step === 1">
            <Button
              type="button"
              class="bg-blue-600 hover:bg-blue-500 text-white px-8 shadow-md shadow-blue-900/20"
              @click="nextStep"
            >
              Próximo <ArrowRight class="w-4 h-4 ml-2" />
            </Button>
          </template>

          <template v-if="step === 2">
            <Button type="button" variant="outline" class="border-border hover:bg-muted/30" @click="step = 1">
              Voltar
            </Button>
            <Button
              type="submit"
              class="bg-emerald-600 hover:bg-emerald-500 text-white px-8 shadow-md shadow-emerald-900/20"
            >
              {{ isEditMode ? 'Salvar Alterações' : 'Salvar Cliente' }}
            </Button>
          </template>
        </div>
      </div>

    </form>
  </div>
</template>