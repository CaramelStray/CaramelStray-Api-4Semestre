<script setup lang="ts">
import { watch } from 'vue'
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
} from '@/services/catalogoMaquinaService'
import { Settings } from 'lucide-vue-next'

const props = defineProps<{
  initialData?: CatalogoMaquinaResponseDTO | null
}>()

const emit = defineEmits<{
  fechar: []
  sucesso: [maquina: CatalogoMaquinaResponseDTO]
}>()

const formSchema = toTypedSchema(z.object({
  descricao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  especificacao: z.string().optional().default(''),
  limiteManutencao: z.string().regex(/^\d*$/, 'Apenas números são permitidos').optional().default('')
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    descricao: '',
    especificacao: '',
    limiteManutencao: ''
  }
})

watch(() => props.initialData, (data) => {
  if (data) {
    form.setValues({
      descricao: data.descricao,
      especificacao: data.especificacao || '',
      limiteManutencao: data.limiteManutencao || ''
    })
  }
}, { immediate: true })

const onlyDigits = (e: KeyboardEvent) => {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End']
  if (!e.ctrlKey && !e.metaKey && !allowed.includes(e.key) && !/^\d$/.test(e.key)) {
    e.preventDefault()
  }
}

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
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
      resetForm()
    }
    emit('sucesso', maquina)
    emit('fechar')
  } catch (e: any) {
    console.error('Erro ao salvar máquina:', e.message)
    alert('Erro ao salvar: ' + (e.response?.data?.message || e.message))
  }
})
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-8 border-b border-border pb-6">
      <div class="flex items-center gap-2 transition-colors text-blue-400 font-bold">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm border-blue-500 bg-blue-500/20 text-blue-400">
          <Settings class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados da Máquina</span>
      </div>
    </div>

    <form @submit="onSubmit">
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
          {{ props.initialData ? 'Salvar Alterações' : 'Salvar' }}
        </Button>
      </div>
    </form>
  </div>
</template>
