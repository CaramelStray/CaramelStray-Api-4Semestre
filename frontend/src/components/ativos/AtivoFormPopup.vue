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
import { ativoService, type AtivoResponseDTO, type AtivoCreateDTO, type CatalogoAtivoDTO } from '@/services/ativoService'
import { Package } from 'lucide-vue-next'

const props = defineProps<{
  initialData?: AtivoResponseDTO | null
  catalogos: CatalogoAtivoDTO[]
}>()

const emit = defineEmits<{
  fechar: []
  sucesso: [ativo: AtivoResponseDTO]
}>()

const formSchema = toTypedSchema(z.object({
  codigoCatalogoAtivo: z.number({ required_error: 'Campo obrigatório', invalid_type_error: 'Selecione um tipo de ativo' }).min(1, 'Selecione um tipo de ativo'),
  descricao: z.string().optional(),
  numeroSerie: z.string().optional(),
  lote: z.string().optional(),
  status: z.string().optional(),
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    codigoCatalogoAtivo: 0,
    descricao: '',
    numeroSerie: '',
    lote: '',
    status: '',
  }
})

watch(() => props.initialData, (data) => {
  if (data) {
    form.setValues({
      codigoCatalogoAtivo: data.codigoCatalogoAtivo ?? 0,
      descricao: data.descricao ?? '',
      numeroSerie: data.numeroSerie ?? '',
      lote: data.lote ?? '',
      status: data.status ?? '',
    })
  } else {
    form.resetForm()
  }
}, { immediate: true })

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  try {
    const dto: AtivoCreateDTO = {
      codigoCatalogoAtivo: values.codigoCatalogoAtivo,
      descricao: values.descricao || undefined,
      numeroSerie: values.numeroSerie || undefined,
      lote: values.lote || undefined,
      status: values.status || undefined,
    }

    let ativo: AtivoResponseDTO
    if (props.initialData) {
      ativo = await ativoService.atualizar(props.initialData.codigo, dto)
    } else {
      ativo = await ativoService.criar(dto)
      resetForm()
    }
    emit('sucesso', ativo)
    emit('fechar')
  } catch (e: any) {
    console.error('Erro ao salvar ativo:', e.message)
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
        <span class="text-sm">Dados do Ativo</span>
      </div>
    </div>

    <form @submit="onSubmit">
      <div class="grid grid-cols-1 gap-y-6">

        <FormField v-slot="{ field, errorMessage }" name="codigoCatalogoAtivo">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Tipo de Ativo <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <select
                v-bind="field"
                :value="field.value"
                @change="(e) => field.onChange(Number((e.target as HTMLSelectElement).value))"
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                <option value="0" disabled>Selecione um tipo de ativo...</option>
                <option
                  v-for="cat in catalogos"
                  :key="cat.codigo"
                  :value="cat.codigo"
                >
                  {{ cat.descricaoProduto }}{{ cat.modelo ? ` — ${cat.modelo}` : '' }}{{ cat.marca ? ` (${cat.marca})` : '' }}
                </option>
              </select>
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="descricao">
          <FormItem>
            <FormLabel>Descrição / Identificação</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Notebook da sala de reuniões" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="numeroSerie">
          <FormItem>
            <FormLabel>Número de Série</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: SN-1234567890" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="lote">
          <FormItem>
            <FormLabel>Lote</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: LOTE-2024-001" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ field, errorMessage }" name="status">
          <FormItem>
            <FormLabel>Status</FormLabel>
            <FormControl>
              <select
                v-bind="field"
                :value="field.value"
                @change="(e) => field.onChange((e.target as HTMLSelectElement).value)"
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                <option value="">Selecione o status...</option>
                <option value="DISPONIVEL">Disponível</option>
                <option value="EM_MANUTENCAO">Em Manutenção</option>
                <option value="EM_USO">Em Uso</option>
              </select>
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
