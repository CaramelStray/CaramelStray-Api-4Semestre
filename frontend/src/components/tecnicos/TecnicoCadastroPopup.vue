<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useForm, useFieldArray } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
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
import { Plus, Trash2, ChevronRight, User, Wrench, ArrowRight } from 'lucide-vue-next'

import { tecnicoService } from '@/services/tecnicoService'
import { habilidadeService, type HabilidadeResponseDTO } from '@/services/habilidadeService'
import { apiFetch } from '@/services/api'

const emit = defineEmits(['fechar'])

const step = ref(1)
const habilidadesDisponiveis = ref<HabilidadeResponseDTO[]>([])

onMounted(async () => {
  try {
    habilidadesDisponiveis.value = await habilidadeService.listar()
  } catch (error) {
    console.error('Erro ao carregar habilidades disponíveis', error)
  }
})

// Máscaras
const applyPhoneMask = (val: any) => {
  if (!val) return ''
  const value = String(val)
  const digits = value.replace(/\D/g, '').slice(0, 11)
  if (digits.length <= 2) return digits.replace(/^(\d{0,2})/, '($1')
  if (digits.length <= 6) return digits.replace(/^(\d{2})(\d{0,4})/, '($1) $2')
  if (digits.length <= 10) return digits.replace(/^(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3')
  return digits.replace(/^(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3')
}

const applyCpfMask = (val: any) => {
  if (!val) return ''
  const value = String(val)
  const digits = value.replace(/\D/g, '').slice(0, 11)
  if (digits.length <= 3) return digits
  if (digits.length <= 6) return digits.replace(/^(\d{3})(\d{0,3})/, '$1.$2')
  if (digits.length <= 9) return digits.replace(/^(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3')
  return digits.replace(/^(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4')
}

// Schema de Validação
const formSchema = toTypedSchema(z.object({
  // Passo 1 (removidos latitude e longitude daqui, pois não vão mais na tela)
  nome: z.string({ required_error: '*' }).min(1, '*'),
  cpf: z.string({ required_error: '*' }).min(14, '*'),
  email: z.string({ required_error: '*' }).min(1, '*').email('*'),
  senha: z.string({ required_error: '*' }).min(6, '*'),
  cargo: z.string({ required_error: '*' }).min(1, '*'),
  telefone: z.string({ required_error: '*' }).min(14, '*'),
  estado: z.string({ required_error: '*' }).min(2, '*'),
  disponibilidade: z.string({ required_error: '*' }).min(1, '*'),
  certificacao: z.string().optional().default(''),
  
  // Passo 2
  habilidades: z.array(z.object({
    habilidadeId: z.string({ required_error: '*' }).min(1, '*'),
    nivel: z.number({ required_error: '*', invalid_type_error: '*' }).min(1).max(5),
    dataValidade: z.string().optional()
  })).optional().default([])
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    nome: '', cpf: '', email: '', senha: '',
    cargo: '', telefone: '', estado: '', disponibilidade: '',
    certificacao: '',
    habilidades: []
  }
})

const { fields, push, remove } = useFieldArray<{ habilidadeId: string; nivel: number; dataValidade: string }>('habilidades')

const nextStep = async () => {
  const step1Fields = [
    'nome', 'cpf', 'email', 'senha', 'cargo', 
    'telefone', 'estado', 'disponibilidade'
  ]
  
  await form.validate()
  const hasStep1Errors = step1Fields.some(field => form.errors.value[field as keyof typeof form.errors.value])

  if (!hasStep1Errors) {
    step.value = 2
  }
}

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  try {
    // 1. Cria o Técnico
    const tecnicoCriado = await tecnicoService.criar({
      email: values.email,
      senha: values.senha,
      nome: values.nome,
      cpf: values.cpf.replace(/\D/g, ''), 
      cargo: values.cargo,
      telefone: values.telefone.replace(/\D/g, ''), 
      certificacao: values.certificacao,
      estado: values.estado,
      disponibilidade: values.disponibilidade,
      latitude: -23.223700, // Passado internamente sem estar na tela
      longitude: -45.900900 // Passado internamente sem estar na tela
    })

    const tecnicoId = tecnicoCriado.id

    // 2. Adiciona as Habilidades 
    if (values.habilidades && values.habilidades.length > 0) {
      const promessasHabilidades = values.habilidades.map(hab => {
        return apiFetch('/tecnico-habilidades', {
          method: 'POST',
          body: JSON.stringify({
            tecnicoId: tecnicoId,
            habilidadeId: Number(hab.habilidadeId),
            nivel: hab.nivel,
            dataValidade: hab.dataValidade || null
          })
        })
      })

      await Promise.all(promessasHabilidades)
    }

    alert('Técnico e Habilidades cadastrados com sucesso!')
    resetForm()
    step.value = 1
    emit('fechar')

  } catch (error: any) {
    console.error('Erro ao salvar técnico:', error)
    const msg = error.response?.data?.message || JSON.stringify(error.response?.data) || ''
    
    if (msg.toLowerCase().includes('cpf') || msg.toLowerCase().includes('documento')) {
      form.setFieldError('cpf', 'Este CPF já está cadastrado.')
      step.value = 1
    } else if (msg.toLowerCase().includes('email')) {
      form.setFieldError('email', 'Este e-mail já está em uso.')
      step.value = 1
    } else {
      alert('Ocorreu um erro ao cadastrar o técnico. Verifique os dados.')
    }
  }
})
</script>

<template>
  <div class="p-4 sm:p-6 bg-background rounded-lg">
    <div class="flex items-center gap-2 mb-8 border-b pb-6">
      <div :class="['flex items-center gap-2 transition-colors', step === 1 ? 'text-blue-600 font-bold' : 'text-muted-foreground']">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm" :class="step === 1 ? 'border-blue-600 bg-blue-50 text-blue-600' : 'border-muted-foreground bg-muted/20'">
          <User class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados do Técnico</span>
      </div>
      <ChevronRight class="w-4 h-4 mx-2 text-muted-foreground/50" />
      <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-600 font-bold' : 'text-muted-foreground']">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm" :class="step === 2 ? 'border-blue-600 bg-blue-50 text-blue-600' : 'border-muted-foreground bg-muted/20'">
          <Wrench class="w-4 h-4" />
        </div>
        <span class="text-sm">Vincular Habilidades (Opcional)</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">
        
        <FormField v-slot="{ componentField }" name="nome">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Nome Completo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Ex: João Silva" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="cpf">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              CPF <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input 
                type="text" 
                placeholder="000.000.000-00" 
                maxlength="14"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @update:model-value="(val) => form.setFieldValue('cpf', applyCpfMask(val))"
                @blur="componentField.onBlur"
              />
            </FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              E-mail de Acesso <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="email" placeholder="joao@email.com" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="senha">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Senha Inicial <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="password" placeholder="Mínimo 6 caracteres" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="cargo">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Cargo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Ex: Fullstack, Técnico de Campo" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="telefone">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Telefone / Celular <span class="text-red-500 font-bold">*</span>
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

        <FormField v-slot="{ value, handleChange }" name="estado">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Estado (UF) <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl><SelectTrigger><SelectValue placeholder="Selecione o Estado" /></SelectTrigger></FormControl>
              <SelectContent class="z-[200]">
                <SelectGroup>
                  <SelectItem value="SP">São Paulo</SelectItem>
                  <SelectItem value="RJ">Rio de Janeiro</SelectItem>
                  <SelectItem value="MG">Minas Gerais</SelectItem>
                  </SelectGroup>
              </SelectContent>
            </Select>
          </FormItem>
        </FormField>

        <FormField v-slot="{ value, handleChange }" name="disponibilidade">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Disponibilidade <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl><SelectTrigger><SelectValue placeholder="Selecione..." /></SelectTrigger></FormControl>
              <SelectContent class="z-[200]">
                <SelectGroup>
                  <SelectItem value="DISPONÍVEL">Disponível</SelectItem>
                  <SelectItem value="EM MANUTENÇÃO">Em manutenção</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="certificacao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Certificações Principais <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl><Input type="text" placeholder="Ex: NR 10 NR 12 NR 35" v-bind="componentField" /></FormControl>
          </FormItem>
        </FormField>
      </div>

      <div v-show="step === 2" class="space-y-6">
        <div class="mb-4">
          <h3 class="text-lg font-medium">Habilidades do Técnico</h3>
          <p class="text-sm text-muted-foreground">Selecione as habilidades do catálogo e informe o nível de proficiência (opcional).</p>
        </div>

        <div v-if="fields.length === 0" class="text-center p-8 border border-dashed rounded-lg bg-muted/10">
          <p class="text-sm text-muted-foreground">Nenhuma habilidade vinculada ainda.</p>
        </div>

        <div v-for="(field, index) in fields" :key="field.key" class="relative p-5 border rounded-lg bg-card shadow-sm group">
          <Button type="button" variant="ghost" size="icon" class="absolute top-3 right-3 text-muted-foreground hover:text-red-500 hover:bg-red-50" @click="remove(index)">
            <Trash2 class="w-4 h-4" />
          </Button>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 pr-10">
            <FormField :name="`habilidades[${index}].habilidadeId`" v-slot="{ value, handleChange }">
              <FormItem>
                <FormLabel class="flex items-center gap-1">
                  Habilidade do Catálogo <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <Select :model-value="value" @update:model-value="handleChange">
                  <FormControl><SelectTrigger><SelectValue placeholder="Selecione a habilidade" /></SelectTrigger></FormControl>
                  <SelectContent class="z-[200]">
                    <SelectGroup>
                      <SelectItem v-for="hab in habilidadesDisponiveis" :key="hab.codigo" :value="String(hab.codigo)">
                        {{ hab.descricao }}
                      </SelectItem>
                    </SelectGroup>
                  </SelectContent>
                </Select>
              </FormItem>
            </FormField>

            <FormField :name="`habilidades[${index}].nivel`" v-slot="{ componentField }">
              <FormItem>
                <FormLabel class="flex items-center gap-1">
                  Nível (1 a 5) <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl><Input type="number" min="1" max="5" placeholder="Ex: 5" v-bind="componentField" /></FormControl>
              </FormItem>
            </FormField>

            <FormField :name="`habilidades[${index}].dataValidade`" v-slot="{ componentField }">
              <FormItem>
                <FormLabel class="flex items-center gap-1">
                  Data de Validade <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl><Input type="date" v-bind="componentField" /></FormControl>
              </FormItem>
            </FormField>
          </div>
        </div>

        <Button type="button" variant="outline" class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20" @click="push({ habilidadeId: '', nivel: 1, dataValidade: '' })">
          <Plus class="w-4 h-4 mr-2" /> Vincular Nova Habilidade
        </Button>
      </div>
      
      <div class="flex items-center justify-end border-t mt-12 pt-6 gap-3">
        <template v-if="step === 1">
          <Button type="button" class="bg-blue-600 hover:bg-blue-700 text-white px-8" @click="nextStep">
            Próxima <ArrowRight class="w-4 h-4 ml-2" />
          </Button>
        </template>
        <template v-if="step === 2">
          <Button type="button" variant="outline" @click="step = 1">Voltar</Button>
          <Button type="submit" class="bg-green-600 hover:bg-green-700 text-white px-8">Concluir Cadastro</Button>
        </template>
      </div>
    </form>
  </div>
</template>