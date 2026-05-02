<script setup lang="ts">
import { watch } from 'vue'
import { useForm } from 'vee-validate'
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { catalogoAtivoService, type CatalogoAtivoResponseDTO, type CatalogoAtivoCreateDTO } from '@/services/catalogoAtivoService'
import { Package } from 'lucide-vue-next'

const props = defineProps<{
  initialData?: CatalogoAtivoResponseDTO | null
}>()

const emit = defineEmits<{
  fechar: []
  sucesso: [item: CatalogoAtivoResponseDTO]
}>()

const formSchema = toTypedSchema(z.object({
  descricaoProduto: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  modelo: z.string().optional(),
  marca: z.string().optional(),
  tipo: z.enum(['FERRAMENTA', 'EQUIPAMENTO', 'COMPONENTE', 'PERIFERICO', 'EPI', 'CONSUMIVEL'], {
    required_error: 'Selecione um tipo',
  }),
  descricao: z.string().optional(),
  especificacao: z.string().optional(),
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    descricaoProduto: '',
    modelo: '',
    marca: '',
    tipo: undefined as any,
    descricao: '',
    especificacao: '',
  },
})

watch(() => props.initialData, (data) => {
  if (data) {
    form.setValues({
      descricaoProduto: data.descricaoProduto ?? '',
      modelo: data.modelo ?? '',
      marca: data.marca ?? '',
      tipo: (data.tipo as any) ?? undefined,
      descricao: data.descricao ?? '',
      especificacao: data.especificacao ?? '',
    })
  } else {
    form.resetForm()
  }
}, { immediate: true })

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  try {
    const dto: CatalogoAtivoCreateDTO = {
      descricaoProduto: values.descricaoProduto,
      modelo: values.modelo || undefined,
      marca: values.marca || undefined,
      tipo: values.tipo,
      descricao: values.descricao || undefined,
      especificacao: values.especificacao || undefined,
    }

    let item: CatalogoAtivoResponseDTO
    if (props.initialData) {
      item = await catalogoAtivoService.atualizar(props.initialData.codigo, dto)
    } else {
      item = await catalogoAtivoService.criar(dto)
      resetForm()
    }
    emit('sucesso', item)
    emit('fechar')
  } catch (e: any) {
    alert('Erro ao salvar: ' + e.message)
  }
})
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-8 border-b border-border pb-6">
      <div class="flex items-center gap-2 transition-colors text-blue-400 font-bold">
        <div class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm border-blue-500 bg-blue-500/20 text-blue-400">
          <Package class="w-4 h-4" />
        </div>
        <span class="text-sm">Dados do Produto</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">

        <FormField v-slot="{ componentField }" name="descricaoProduto" class="md:col-span-2">
          <FormItem class="md:col-span-2">
            <FormLabel class="flex items-center gap-1">
              Nome / Produto <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Notebook Dell Latitude" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="modelo">
          <FormItem>
            <FormLabel>Modelo</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: E5540" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="marca">
          <FormItem>
            <FormLabel>Marca</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Dell" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ value, handleChange }" name="tipo">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Tipo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="val => handleChange(String(val))">
              <FormControl>
                <SelectTrigger class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione um tipo..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200]">
                <SelectItem value="FERRAMENTA">Ferramenta</SelectItem>
                <SelectItem value="EQUIPAMENTO">Equipamento</SelectItem>
                <SelectItem value="COMPONENTE">Componente</SelectItem>
                <SelectItem value="PERIFERICO">Periférico</SelectItem>
                <SelectItem value="EPI">EPI</SelectItem>
                <SelectItem value="CONSUMIVEL">Consumível</SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="descricao">
          <FormItem>
            <FormLabel>Descrição</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Descrição resumida" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="especificacao" class="md:col-span-2">
          <FormItem class="md:col-span-2">
            <FormLabel>Especificação técnica</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Intel i7, 16GB RAM, SSD 512GB" v-bind="componentField" />
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
