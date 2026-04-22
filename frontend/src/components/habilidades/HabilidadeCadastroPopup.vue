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
import { habilidadeService, type HabilidadeResponseDTO } from '@/services/habilidadeService'
import { Code2 } from 'lucide-vue-next'

const props = defineProps<{
  initialData?: HabilidadeResponseDTO | null
}>()

const emit = defineEmits<{
  fechar: []
  sucesso: [habilidade: HabilidadeResponseDTO]
}>()

const formSchema = toTypedSchema(z.object({
  descricao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  observacao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório')
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    descricao: '',
    observacao: ''
  }
})

watch(() => props.initialData, (data) => {
  if (data) {
    form.setValues({
      descricao: data.descricao,
      observacao: data.observacao || ''
    })
  }
}, { immediate: true })

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  try {
    let habilidade: HabilidadeResponseDTO
    if (props.initialData) {
      habilidade = await habilidadeService.atualizar(props.initialData.codigo, {
        descricao: values.descricao,
        observacao: values.observacao,
      })
    } else {
      habilidade = await habilidadeService.criar({
        descricao: values.descricao,
        observacao: values.observacao,
      })
      resetForm()
    }
    emit('sucesso', habilidade)
    emit('fechar')
  } catch (e: any) {
    console.error('Erro ao salvar certificação:', e.message)
    alert('Erro ao salvar: ' + e.message)
  }
})
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-8 border-b border-border pb-6">
      <div class="flex items-center gap-2 transition-colors text-blue-400 font-bold">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm border-blue-500 bg-blue-500/20 text-blue-400">
          <Code2 class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados da Certificação</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div class="grid grid-cols-1 gap-y-6">

        <FormField v-slot="{ componentField }" name="descricao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Descrição da Certificação <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: NR-37 NR-10" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="observacao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Observações adicionais <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Textarea
                placeholder="Detalhes ou níveis referentes a essa certificação..."
                class="resize-y min-h-[100px]"
                v-bind="componentField"
              />
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
