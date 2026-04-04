<script setup lang="ts">
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
} from '@/components/ui/form'
import { habilidadeService } from '@/services/habilidadeService'
import { Code2 } from 'lucide-vue-next'

const emit = defineEmits(['fechar'])

const formSchema = toTypedSchema(z.object({
  descricao: z.string({ required_error: '*' }).min(1, '*'),
  observacao: z.string({ required_error: '*' }).min(1, '*')
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    descricao: '',
    observacao: ''
  }
})

const onSubmit = form.handleSubmit(async (values) =>  {
  try {
    await habilidadeService.criar({
      descricao: values.descricao,
      observacao: values.observacao,
    })
    emit('fechar')
  } catch (e: any) {
    console.error('Erro ao cadastrar habilidade:', e.message)
    alert('Erro ao cadastrar: ' + e.message)
  }
})
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-8 border-b pb-6">
      <div class="flex items-center gap-2 transition-colors text-blue-600 font-bold">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm border-blue-600 bg-blue-50 text-blue-600">
          <Code2 class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados da Habilidade</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div class="grid grid-cols-1 gap-y-6">
        
        <FormField v-slot="{ componentField }" name="descricao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Descrição da Habilidade <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Java, Vue 3, SQL" v-bind="componentField" />
            </FormControl>
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="observacao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Observações adicionais <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Textarea 
                placeholder="Detalhes ou níveis referentes a essa habilidade..." 
                class="resize-y min-h-[100px]" 
                v-bind="componentField" 
              />
            </FormControl>
          </FormItem>
        </FormField>

      </div>
      
      <div class="flex items-center justify-end border-t mt-12 pt-6 gap-3">
        <Button type="button" variant="ghost" @click="emit('fechar')">Cancelar</Button>
        <Button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white px-8">Salvar</Button>
      </div>
    </form>
  </div>
</template>