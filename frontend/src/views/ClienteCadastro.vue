<script setup lang="ts">
import { ref, watch } from 'vue'
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
} from '@/components/ui/form'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Plus, Trash2, ChevronRight, Building2, Users, ArrowRight } from 'lucide-vue-next'

const emit = defineEmits(['fechar'])

const step = ref(1)

// --- FUNÇÕES DE MÁSCARA NATIVAS ---
const applyPhoneMask = (val: any) => {
  if (!val) return ''
  const value = String(val)
  const digits = value.replace(/\D/g, '').slice(0, 11)
  if (digits.length <= 2) return digits.replace(/^(\d{0,2})/, '($1')
  if (digits.length <= 6) return digits.replace(/^(\d{2})(\d{0,4})/, '($1) $2')
  if (digits.length <= 10) return digits.replace(/^(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3')
  return digits.replace(/^(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3')
}

const applyCnpjMask = (val: any) => {
  if (!val) return ''
  const value = String(val)
  const digits = value.replace(/\D/g, '').slice(0, 14)
  if (digits.length <= 2) return digits
  if (digits.length <= 5) return digits.replace(/^(\d{2})(\d{0,3})/, '$1.$2')
  if (digits.length <= 8) return digits.replace(/^(\d{2})(\d{3})(\d{0,3})/, '$1.$2.$3')
  if (digits.length <= 12) return digits.replace(/^(\d{2})(\d{3})(\d{3})(\d{0,4})/, '$1.$2.$3/$4')
  return digits.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, '$1.$2.$3/$4-$5')
}

// Schema de validação
const formSchema = toTypedSchema(z.object({
  internacional: z.boolean().default(false),
  nomeEmpresa: z.string({ required_error: '*' }).min(1, '*'),
  documento: z.string({ required_error: '*' }).min(1, '*'), 
  email: z.string({ required_error: '*' }).min(1, '*').email('*'),
  telefone: z.string({ required_error: '*' }).min(14, '*'), 
  responsavel: z.string({ required_error: '*' }).min(1, '*'),
  rua: z.string({ required_error: '*' }).min(1, '*'),
  numero: z.string({ required_error: '*' }).min(1, '*'),
  pais: z.string({ required_error: '*' }).min(1, '*'),
  estado: z.string({ required_error: '*' }).min(1, '*'),
  cidade: z.string({ required_error: '*' }).min(1, '*'),
  fusoHorario: z.string({ required_error: '*' }).min(1, '*'),
  observacoes: z.string().optional().default(''),
  
  contatos: z.array(z.object({
    nome: z.string({ required_error: '*' }).min(1, '*'),
    email: z.string({ required_error: '*' }).min(1, '*').email('*'),
    telefone: z.string({ required_error: '*' }).min(14, '*') 
  })).optional().default([])
}).refine((data) => {
  if (!data.internacional) {
    return data.documento.length === 18; 
  }
  return true; 
}, {
  message: '*', 
  path: ['documento'],
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    internacional: false,
    contatos: [],
    observacoes: '',
    documento: '',
    telefone: ''
  }
})

const { fields, push, remove } = useFieldArray('contatos')

watch(() => form.values.internacional, () => {
  form.setFieldValue('documento', ''); 
  form.setFieldError('documento', undefined);
});

const nextStep = async () => {
  const step1Fields = [
    'nomeEmpresa', 'documento', 'responsavel', 'email', 'telefone', 
    'rua', 'numero', 'pais', 'estado', 'cidade', 'fusoHorario'
  ]
  
  const result = await form.validate();
  
  const hasStep1Errors = step1Fields.some(field => form.errors.value[field as keyof typeof form.errors.value]);

  if (!hasStep1Errors) {
    step.value = 2
  }
}

const onSubmit = form.handleSubmit((values) => {
  console.log('Dados prontos para envio:', values)
  emit('fechar')
})
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-8 border-b pb-6">
      <div :class="['flex items-center gap-2 transition-colors', step === 1 ? 'text-blue-600 font-bold' : 'text-muted-foreground']">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm" :class="step === 1 ? 'border-blue-600 bg-blue-50 text-blue-600' : 'border-muted-foreground bg-muted/20'">
          <Building2 class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados da Empresa</span>
      </div>
      <ChevronRight class="w-4 h-4 mx-2 text-muted-foreground/50" />
      <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-600 font-bold' : 'text-muted-foreground']">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm" :class="step === 2 ? 'border-blue-600 bg-blue-50 text-blue-600' : 'border-muted-foreground bg-muted/20'">
          <Users class="w-4 h-4" />
        </div>
        <span class="text-sm">Contatos da Empresa</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">
        
        <FormField v-slot="{ value, handleChange }" name="internacional">
          <FormItem class="col-span-1 md:col-span-2 lg:col-span-3 flex flex-row items-center space-x-3 space-y-0 rounded-md border p-4 bg-muted/20">
            <FormControl>
              <Checkbox :checked="value" @update:checked="handleChange" />
            </FormControl>
            <div class="space-y-1 leading-none">
              <FormLabel>Empresa Internacional</FormLabel>
              <p class="text-xs text-muted-foreground">Marque se a empresa não possui CNPJ ou está localizada fora do Brasil.</p>
            </div>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="nomeEmpresa">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Nome da Empresa <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Razão Social ou Nome Fantasia" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="documento">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              {{ form.values.internacional ? 'Documento (Internacional)' : 'CNPJ' }}
              <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input 
                v-if="!form.values.internacional"
                type="text" 
                placeholder="00.000.000/0000-00" 
                maxlength="18"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @update:model-value="(val) => form.setFieldValue('documento', applyCnpjMask(val))"
                @blur="componentField.onBlur"
              />
              <Input 
                v-else
                type="text" 
                placeholder="Digite o documento" 
                v-bind="componentField"
              />
            </FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="responsavel">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Nome do Responsável <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Responsável pelo contrato" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="rua">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Rua/Avenida <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Nome da rua" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="numero">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Número <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="123 ou S/N" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="email">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              E-mail Corporativo <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="email" placeholder="contato@empresa.com" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="telefone">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Telefone Principal <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input 
                type="tel" 
                placeholder="(00) 00000-0000" 
                maxlength="15"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @update:model-value="(val) => form.setFieldValue('telefone', applyPhoneMask(val))"
                @blur="componentField.onBlur"
              />
            </FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="pais">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              País <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Ex: Brasil" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="estado">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Estado / Região <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Ex: São Paulo (SP)" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField, errorMessage }" name="cidade">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Cidade <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Ex: São José dos Campos" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ value, handleChange, errorMessage }" name="fusoHorario">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Fuso Horário <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl><SelectTrigger><SelectValue placeholder="Selecione..." /></SelectTrigger></FormControl>
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
                  <SelectItem value="utc+9">UTC +9 (Tóquio)</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="observacoes">
          <FormItem class="col-span-1 md:col-span-2 lg:col-span-3">
            <FormLabel>Observações</FormLabel>
            <FormControl><Textarea placeholder="Informações adicionais..." class="resize-y min-h-[100px]" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>
      </div>

      <div v-show="step === 2" class="space-y-6">
        <div v-if="fields.length === 0" class="text-center p-8 border border-dashed rounded-lg bg-muted/10">
          <p class="text-sm text-muted-foreground">Nenhum contato adicional cadastrado.</p>
        </div>

        <div v-for="(field, index) in fields" :key="field.key" class="relative p-5 border rounded-lg bg-card shadow-sm group">
          <Button type="button" variant="ghost" size="icon" class="absolute top-3 right-3 text-muted-foreground hover:text-red-500 hover:bg-red-50" @click="remove(index)">
            <Trash2 class="w-4 h-4" />
          </Button>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 pr-10">
            <FormField :name="`contatos[${index}].nome`" v-slot="{ componentField, errorMessage }">
              <FormItem>
                <FormLabel class="flex items-center gap-1">
                  Nome do Contato <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl><Input placeholder="Ex: Maria" v-bind="componentField" /></FormControl>
              </FormItem>
            </FormField>

            <FormField :name="`contatos[${index}].email`" v-slot="{ componentField, errorMessage }">
              <FormItem>
                <FormLabel class="flex items-center gap-1">
                  E-mail <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl><Input type="email" placeholder="maria@empresa.com" v-bind="componentField" /></FormControl>
              </FormItem>
            </FormField>

            <FormField :name="`contatos[${index}].telefone`" v-slot="{ componentField: dynamicField, errorMessage }">
              <FormItem>
                <FormLabel class="flex items-center gap-1">
                  Telefone / Celular <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl>
                   <Input 
                    type="tel" 
                    placeholder="(00) 00000-0000" 
                    maxlength="15"
                    :name="dynamicField.name"
                    :model-value="dynamicField.modelValue"
                    @update:model-value="(val) => form.setFieldValue(`contatos[${index}].telefone`, applyPhoneMask(val))"
                    @blur="dynamicField.onBlur"
                  />
                </FormControl>
              </FormItem>
            </FormField>
          </div>
        </div>

        <Button type="button" variant="outline" class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20" @click="push({ nome: '', email: '', telefone: '' })">
          <Plus class="w-4 h-4 mr-2" /> Adicionar novo contato
        </Button>
      </div>
      
      <div class="flex items-center justify-between border-t mt-12 pt-6">
        <Button type="button" variant="ghost" @click="emit('fechar')">Cancelar</Button>
        <div class="flex gap-3">
          <template v-if="step === 1">
            <Button type="button" class="bg-blue-600 hover:bg-blue-700 text-white px-8" @click="nextStep">
              Próxima <ArrowRight class="w-4 h-4 ml-2" />
            </Button>
          </template>
          <template v-if="step === 2">
            <Button type="button" variant="outline" @click="step = 1">Voltar</Button>
            <Button type="submit" class="bg-green-600 hover:bg-green-700 text-white px-8">Salvar Cliente</Button>
          </template>
        </div>
      </div>
    </form>
  </div>
</template>