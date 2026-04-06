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
import {
  catalogoMaquinaService,
  type CatalogoMaquinaResponseDTO,
} from '@/services/catalogoMaquinaService'
import { Settings } from 'lucide-vue-next'

const emit = defineEmits<{
  fechar: []
  sucesso: [maquina: CatalogoMaquinaResponseDTO]
}>()

const formSchema = toTypedSchema(z.object({
  descricao: z.string({ required_error: '*' }).min(1, '*'),
  especificacao: z.string().optional().default(''),
  limiteManutencao: z.string().optional().default('')
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    descricao: '',
    especificacao: '',
    limiteManutencao: ''
  }
})

const onSubmit = form.handleSubmit(async (values, { resetForm }) =>  {
  try {
    const maquinaCriada = await catalogoMaquinaService.criar({
      descricao: values.descricao,
      especificacao: values.especificacao,
      limiteManutencao: values.limiteManutencao,
    })
    resetForm()
    emit('sucesso', maquinaCriada)
    emit('fechar')
  } catch (e: any) {
    console.error('Erro ao cadastrar máquina:', e.message)
    alert('Erro ao cadastrar: ' + (e.response?.data?.message || e.message))
  }
})
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-8 border-b pb-6">
      <div class="flex items-center gap-2 transition-colors text-blue-600 font-bold">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm border-blue-600 bg-blue-50 text-blue-600">
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
              <Input type="text" placeholder="Ex: Torno CNC, Fresadora, etc" v-bind="componentField" />
            </FormControl>
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
            <FormLabel>Limite de Manutenção</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: 500h, 6 meses, 10.000 peças" v-bind="componentField" />
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
