<script setup lang="ts">
import { ref, onMounted, watch, computed, nextTick } from 'vue'
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
import { DatePickerInput } from '@/components/ui/date-picker'
import { Plus, Trash2, ChevronRight, User, Wrench, ArrowRight, CheckCircle2 } from 'lucide-vue-next'

import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { habilidadeService, type HabilidadeResponseDTO } from '@/services/habilidadeService'
import { apiFetch } from '@/services/api'

const props = defineProps<{
  initialData?: TecnicoResponseDTO | null
}>()

const emit = defineEmits<{
  fechar: []
  cadastrado: [tecnico: TecnicoResponseDTO]
}>()

const isEditMode = computed(() => !!props.initialData)

const step = ref(1)
const erroValidacao = ref('')
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
  const digits = String(val).replace(/\D/g, '').slice(0, 11)
  if (digits.length <= 2) return digits.replace(/^(\d{0,2})/, '($1')
  if (digits.length <= 6) return digits.replace(/^(\d{2})(\d{0,4})/, '($1) $2')
  if (digits.length <= 10) return digits.replace(/^(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3')
  return digits.replace(/^(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3')
}

const applyCpfMask = (val: any) => {
  if (!val) return ''
  const digits = String(val).replace(/\D/g, '').slice(0, 11)
  if (digits.length <= 3) return digits
  if (digits.length <= 6) return digits.replace(/^(\d{3})(\d{0,3})/, '$1.$2')
  if (digits.length <= 9) return digits.replace(/^(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3')
  return digits.replace(/^(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4')
}

// Schema de Validação
const formSchema = toTypedSchema(z.object({
  nome: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  cpf: z.string({ required_error: 'Campo obrigatório' }).min(14, 'CPF inválido'),
  email: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório').email('E-mail inválido'),
  senha: z.string().optional().default(''),
  cargo: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  telefone: z.string({ required_error: 'Campo obrigatório' }).min(14, 'Telefone inválido'),
  estado: z.string({ required_error: 'Campo obrigatório' }).min(2, 'Campo obrigatório'),

  habilidades: z.array(z.object({
    habilidadeId: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    nivel: z.number({ required_error: 'Campo obrigatório', invalid_type_error: 'Informe um número entre 1 e 5' }).min(1, 'Mínimo 1').max(5, 'Máximo 5'),
    dataValidade: z.string().refine(v => !v || /^\d{4}-\d{2}-\d{2}$/.test(v), 'Data inválida').optional()
  })).optional().default([])
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    nome: '', cpf: '', email: '', senha: '',
    cargo: '', telefone: '', estado: '',
    habilidades: []
  }
})

const { fields, push, remove, replace } = useFieldArray<{ habilidadeId: string; nivel: number; dataValidade: string }>('habilidades')

const popularFormEdicao = async (data: TecnicoResponseDTO) => {
  await nextTick()
  form.resetForm({
    values: {
      nome: data.nome,
      cpf: applyCpfMask(data.cpf),
      email: data.email,
      senha: '',
      cargo: data.cargo || '',
      telefone: applyPhoneMask(data.telefone),
      estado: data.estado || '',
      habilidades: (data.habilidades ?? []).map(h => ({
        habilidadeId: String(h.habilidadeId),
        nivel: h.nivel,
        dataValidade: h.dataValidade || ''
      }))
    }
  })
  await nextTick()
  replace((data.habilidades ?? []).map(h => ({
    habilidadeId: String(h.habilidadeId),
    nivel: h.nivel,
    dataValidade: h.dataValidade || ''
  })))
}

watch(() => props.initialData, (data) => {
  if (data) popularFormEdicao(data)
}, { immediate: true })

const nextStep = async () => {
  const step1Fields = isEditMode.value
    ? ['nome', 'cpf', 'cargo', 'telefone', 'estado']
    : ['nome', 'cpf', 'email', 'cargo', 'telefone', 'estado']
  const results = await Promise.all(
    step1Fields.map(field => form.validateField(field as any))
  )
  const hasErrors = results.some(r => !r.valid)
  if (hasErrors) {
    erroValidacao.value = 'Corrija os campos destacados antes de continuar.'
  } else {
    erroValidacao.value = ''
    step.value = 2
  }
}

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  erroValidacao.value = ''
  try {
    const payload: any = {
      email: values.email,
      nome: values.nome,
      cpf: values.cpf.replace(/\D/g, ''),
      cargo: values.cargo,
      telefone: values.telefone.replace(/\D/g, ''),
      estado: values.estado,
      disponibilidade: isEditMode.value
        ? (props.initialData?.disponibilidade ?? 'DISPONIVEL')
        : 'DISPONIVEL',
      certificacao: 'NR 10 NR 12 NR 35',
      latitude: -23.223700,
      longitude: -45.900900
    }

    if (values.senha) payload.senha = values.senha

    let tecnico: TecnicoResponseDTO

    if (isEditMode.value && props.initialData) {
      tecnico = await tecnicoService.atualizar(props.initialData.id, payload)

      const habilidadesAntigas = props.initialData.habilidades ?? []
      if (habilidadesAntigas.length > 0) {
        await Promise.all(
          habilidadesAntigas.map(hab =>
            apiFetch(`/tecnico-habilidades?tecnicoId=${props.initialData!.id}&habilidadeId=${hab.habilidadeId}`, {
              method: 'DELETE'
            }).catch(() => {})
          )
        )
      }

      if (values.habilidades && values.habilidades.length > 0) {
        await Promise.all(
          values.habilidades.map(hab =>
            apiFetch('/tecnico-habilidades', {
              method: 'POST',
              body: JSON.stringify({
                tecnicoId: props.initialData!.id,
                habilidadeId: Number(hab.habilidadeId),
                nivel: hab.nivel,
                dataValidade: hab.dataValidade || null
              })
            })
          )
        )
      }
    } else {
      if (!values.senha) {
        form.setFieldError('senha', 'Senha é obrigatória para novo técnico')
        return
      }
      payload.senha = values.senha
      tecnico = await tecnicoService.criar(payload)

      if (values.habilidades && values.habilidades.length > 0) {
        const promessasHabilidades = values.habilidades.map(hab =>
          apiFetch('/tecnico-habilidades', {
            method: 'POST',
            body: JSON.stringify({
              tecnicoId: tecnico.id,
              habilidadeId: Number(hab.habilidadeId),
              nivel: hab.nivel,
              dataValidade: hab.dataValidade || null
            })
          })
        )
        await Promise.all(promessasHabilidades)
      }

      resetForm()
      step.value = 1
    }

    emit('cadastrado', tecnico)
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
      erroValidacao.value = 'Ocorreu um erro ao salvar o técnico. Verifique os dados.'
    }
  }
}, () => {
  erroValidacao.value = 'Preencha todos os campos obrigatórios antes de salvar.'
})
</script>

<template>
  <div class="p-4 sm:p-6 bg-background rounded-lg">
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
          <User v-else class="w-4 h-4" />
        </div>
        <span class="text-sm hidden sm:inline-block">Dados do Técnico</span>
      </div>

      <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

      <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-400 font-bold' : 'text-muted-foreground']">
        <div
          class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
          :class="step === 2
            ? 'border-blue-500 bg-blue-500/20 text-blue-400'
            : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
        >
          <Wrench class="w-4 h-4" />
        </div>
        <span class="text-sm hidden sm:inline-block">Vincular Certificações (Opcional)</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">

        <FormField v-slot="{ componentField }" name="nome">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Nome Completo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: João Silva" class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="cpf">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              CPF <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="text"
                placeholder="000.000.000-00"
                maxlength="14"
                :disabled="isEditMode"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @update:model-value="(val) => !isEditMode && form.setFieldValue('cpf', applyCpfMask(val))"
                @blur="componentField.onBlur"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-if="!isEditMode" v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              E-mail de Acesso <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="email" placeholder="joao@email.com" class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-if="!isEditMode" v-slot="{ componentField }" name="senha">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Senha Inicial <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="password" placeholder="Mínimo 6 caracteres" class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="cargo">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Cargo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Fullstack, Técnico de Campo" class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="telefone">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Telefone / Celular <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input
                type="tel"
                placeholder="(00) 00000-0000"
                maxlength="15"
                class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
                :name="componentField.name"
                :model-value="componentField.modelValue"
                @update:model-value="(val) => form.setFieldValue('telefone', applyPhoneMask(val))"
                @blur="componentField.onBlur"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ value, handleChange }" name="estado">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Estado (UF) <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl>
                <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione o Estado" />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200]">
                <SelectGroup>
                  <SelectItem value="SP">São Paulo</SelectItem>
                  <SelectItem value="RJ">Rio de Janeiro</SelectItem>
                  <SelectItem value="MG">Minas Gerais</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>
      </div>

      <div v-show="step === 2" class="space-y-6">
        <div class="mb-4">
          <h3 class="text-lg font-medium">Certificações do Técnico</h3>
          <p class="text-sm text-muted-foreground">Selecione as certificações do catálogo e informe o nível de proficiência (opcional).</p>
        </div>

        <div v-if="fields.length === 0" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/10">
          <p class="text-sm text-muted-foreground">Nenhuma certificação vinculada ainda.</p>
        </div>

        <div v-for="(field, index) in fields" :key="field.key" class="relative p-5 border border-border rounded-lg bg-muted/5 shadow-sm">
          <Button type="button" variant="ghost" size="icon" class="absolute top-3 right-3 text-muted-foreground hover:text-red-400 hover:bg-red-500/10" @click="remove(index)">
            <Trash2 class="w-4 h-4" />
          </Button>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 pr-10">
            <FormField :name="`habilidades[${index}].habilidadeId`" v-slot="{ value, handleChange }">
              <FormItem>
                <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                  Certificação do Catálogo <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <Select :model-value="value" @update:model-value="handleChange">
                  <FormControl>
                    <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                      <SelectValue placeholder="Selecione a habilidade" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent class="z-[200]">
                    <SelectGroup>
                      <SelectItem v-for="hab in habilidadesDisponiveis" :key="hab.codigo" :value="String(hab.codigo)">
                        {{ hab.descricao }}
                      </SelectItem>
                    </SelectGroup>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField :name="`habilidades[${index}].nivel`" v-slot="{ componentField }">
              <FormItem>
                <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                  Nível (1 a 5) <span class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl>
                  <Input type="number" min="1" max="5" placeholder="Ex: 5" class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors" v-bind="componentField" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField :name="`habilidades[${index}].dataValidade`" v-slot="{ value, handleChange }">
              <FormItem>
                <FormLabel class="text-sm font-medium text-foreground/80">
                  Data de Validade
                </FormLabel>
                <FormControl>
                  <DatePickerInput :model-value="value" @update:model-value="handleChange" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>
          </div>
        </div>

        <Button type="button" variant="outline" class="w-full border-dashed border-2 border-border bg-muted/5 hover:bg-muted/20" @click="push({ habilidadeId: '', nivel: 1, dataValidade: '' })">
          <Plus class="w-4 h-4 mr-2" /> Vincular Nova Certificação
        </Button>
      </div>

      <div class="border-t border-border mt-12 pt-6 space-y-3">
        <p v-if="erroValidacao" class="text-sm text-red-400 bg-red-500/10 border border-red-500/30 rounded-md px-4 py-2">
          {{ erroValidacao }}
        </p>
        <div class="flex items-center justify-end gap-3">
          <template v-if="step === 1">
            <Button type="button" variant="ghost" class="hover:bg-muted/30" @click="emit('fechar')">Cancelar</Button>
            <Button type="button" class="bg-blue-600 hover:bg-blue-500 text-white px-8 shadow-md shadow-blue-900/20" @click="nextStep">
              Próxima <ArrowRight class="w-4 h-4 ml-2" />
            </Button>
          </template>
          <template v-if="step === 2">
            <Button type="button" variant="outline" class="border-border hover:bg-muted/30" @click="step = 1">Voltar</Button>
            <Button type="submit" class="bg-emerald-600 hover:bg-emerald-500 text-white px-8 shadow-md shadow-emerald-900/20">
              {{ isEditMode ? 'Salvar Alterações' : 'Concluir Cadastro' }}
            </Button>
          </template>
        </div>
      </div>
    </form>
  </div>
</template>
