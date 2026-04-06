<script setup lang="ts">
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
import { Plus, Trash2, CheckSquare } from 'lucide-vue-next'
import {
  catalogoSoftwareService,
  type CatalogoSoftwareResponseDTO,
} from '@/services/catalogoSoftwareService'

const emit = defineEmits<{
  fechar: []
  sucesso: [software: CatalogoSoftwareResponseDTO]
}>()

// ── Schema ────────────────────────────────────────────────────────────────────

const formSchema = toTypedSchema(z.object({
  nomeSoftware: z.string({ required_error: '*' }).min(1, '*'),
  versao: z.string({ required_error: '*' }).min(1, '*'),
  tipo: z.string({ required_error: '*' }).min(1, '*'),
  desenvolvedorFornecedor: z.string({ required_error: '*' }).min(1, '*'),
  urlDocumentacao: z.string().url('URL inválida').optional().or(z.literal('')),
  descricaoTecnica: z.string().optional().default(''),
  ativo: z.boolean().default(true),
  checklistPadrao: z.array(z.object({
    descricao: z.string({ required_error: '*' }).min(1, '*'),
    obrigatorio: z.boolean().default(false),
  })).optional().default([]),
}))

// ── Form ──────────────────────────────────────────────────────────────────────

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    ativo: true,
    checklistPadrao: [],
    descricaoTecnica: '',
    urlDocumentacao: '',
  },
})

const { fields, push, remove } = useFieldArray<{ descricao: string; obrigatorio: boolean }>('checklistPadrao')

const tiposDisponiveis = [
  'ERP', 'MES', 'SCADA', 'PLM', 'CAD/CAM',
  'BI / Analytics', 'CRM', 'Monitoramento',
  'Segurança', 'Infraestrutura', 'Outro',
]

// ── Submit ────────────────────────────────────────────────────────────────────

const onSubmit = form.handleSubmit(async (values, { resetForm }) => {
  try {
    const softwareCriado = await catalogoSoftwareService.criar({
      nomeSoftware: values.nomeSoftware,
      versao: values.versao,
      tipo: values.tipo,
      desenvolvedorFornecedor: values.desenvolvedorFornecedor,
      urlDocumentacao: values.urlDocumentacao || undefined,
      descricaoTecnica: values.descricaoTecnica || undefined,
      ativo: values.ativo,
      checklistPadrao: values.checklistPadrao?.length ? values.checklistPadrao : undefined,
    })

    resetForm()
    emit('sucesso', softwareCriado)
    emit('fechar')
  } catch (error: any) {
    const data = error.response?.data
    const msg = (data?.message || data?.error || JSON.stringify(data) || '').toLowerCase()

    if (msg.includes('nome') || msg.includes('already exists')) {
      form.setFieldError('nomeSoftware', 'Já existe um software com este nome e versão.')
    } else {
      alert('Erro ao cadastrar: ' + (data?.message || 'Verifique os dados informados.'))
    }
  }
})
</script>

<template>
  <div>
    <form @submit="onSubmit">

      <!-- ── Informações Básicas ─────────────────────────────────────────── -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-5">

        <!-- Status Ativo -->
        <FormField v-slot="{ value, handleChange }" name="ativo">
          <FormItem class="col-span-1 md:col-span-2 lg:col-span-3 flex flex-row items-center space-x-3 space-y-0 rounded-md border p-4 bg-muted/20">
            <FormControl>
              <Checkbox :checked="value" @update:checked="handleChange" />
            </FormControl>
            <div class="space-y-1 leading-none">
              <FormLabel>Sistema Ativo</FormLabel>
              <p class="text-xs text-muted-foreground">Disponível para uso e seleção em chamados de manutenção.</p>
            </div>
          </FormItem>
        </FormField>

        <!-- Nome -->
        <FormField v-slot="{ componentField, errorMessage }" name="nomeSoftware">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Nome do Software <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Oracle EBS, SAP R3..." v-bind="componentField" />
            </FormControl>
          </FormItem>
        </FormField>

        <!-- Versão -->
        <FormField v-slot="{ componentField, errorMessage }" name="versao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Versão <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: 12.2.10, 3.0.1..." v-bind="componentField" />
            </FormControl>
          </FormItem>
        </FormField>

        <!-- Tipo -->
        <FormField v-slot="{ value, handleChange, errorMessage }" name="tipo">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Tipo <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select :model-value="value" @update:model-value="handleChange">
              <FormControl>
                <SelectTrigger><SelectValue placeholder="Selecione o tipo..." /></SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200]">
                <SelectGroup>
                  <SelectItem v-for="t in tiposDisponiveis" :key="t" :value="t">{{ t }}</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
          </FormItem>
        </FormField>

        <!-- Fornecedor -->
        <FormField v-slot="{ componentField, errorMessage }" name="desenvolvedorFornecedor">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              Desenvolvedor / Fornecedor <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
            </FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Oracle, SAP, Interno..." v-bind="componentField" />
            </FormControl>
          </FormItem>
        </FormField>

        <!-- URL Documentação -->
        <FormField v-slot="{ componentField, errorMessage }" name="urlDocumentacao">
          <FormItem>
            <FormLabel class="flex items-center gap-1">
              URL da Documentação
              <span v-if="errorMessage" class="text-red-500 text-xs font-normal">{{ errorMessage }}</span>
            </FormLabel>
            <FormControl>
              <Input type="url" placeholder="https://docs.exemplo.com/..." v-bind="componentField" />
            </FormControl>
          </FormItem>
        </FormField>

        <!-- Descrição Técnica -->
        <FormField v-slot="{ componentField }" name="descricaoTecnica">
          <FormItem class="col-span-1 md:col-span-2 lg:col-span-3">
            <FormLabel>Descrição Técnica</FormLabel>
            <FormControl>
              <Textarea
                placeholder="Descreva a finalidade, integrações e características do sistema..."
                class="resize-y min-h-[88px]"
                v-bind="componentField"
              />
            </FormControl>
          </FormItem>
        </FormField>
      </div>

      <!-- ── Checklist Padrão ────────────────────────────────────────────── -->
      <div class="mt-8 space-y-4">
        <div class="flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-muted-foreground">
          <span class="h-px flex-1 bg-border"></span>
          <CheckSquare class="w-3.5 h-3.5 shrink-0" />
          Checklist Padrão
          <span class="h-px flex-1 bg-border"></span>
        </div>

        <p class="text-xs text-muted-foreground">
          Itens adicionados automaticamente ao abrir um chamado para este software.
        </p>

        <div v-if="fields.length === 0" class="text-center p-6 border border-dashed rounded-lg bg-muted/10">
          <p class="text-sm text-muted-foreground">Nenhum item de checklist adicionado.</p>
        </div>

        <div
          v-for="(field, index) in fields"
          :key="field.key"
          class="relative p-4 border rounded-lg bg-card shadow-sm"
        >
          <Button
            type="button"
            variant="ghost"
            size="icon"
            class="absolute top-3 right-3 text-muted-foreground hover:text-red-500 hover:bg-red-50"
            @click="remove(index)"
          >
            <Trash2 class="w-4 h-4" />
          </Button>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 pr-10">
            <!-- Descrição -->
            <FormField :name="`checklistPadrao[${index}].descricao`" v-slot="{ componentField, errorMessage }">
              <FormItem class="col-span-1 md:col-span-2">
                <FormLabel class="flex items-center gap-1">
                  Descrição <span v-if="errorMessage" class="text-red-500 font-bold">*</span>
                </FormLabel>
                <FormControl>
                  <Input placeholder="Ex: Verificar licença ativa..." v-bind="componentField" />
                </FormControl>
              </FormItem>
            </FormField>

            <!-- Obrigatório -->
            <FormField :name="`checklistPadrao[${index}].obrigatorio`" v-slot="{ value, handleChange }">
              <FormItem class="flex flex-col justify-end pb-1">
                <FormLabel>Obrigatório</FormLabel>
                <FormItem class="flex flex-row items-center space-x-3 space-y-0 rounded-md border p-3 bg-muted/20 h-10">
                  <FormControl>
                    <Checkbox :checked="value" @update:checked="handleChange" />
                  </FormControl>
                  <span class="text-sm text-muted-foreground">Item obrigatório</span>
                </FormItem>
              </FormItem>
            </FormField>
          </div>
        </div>

        <Button
          type="button"
          variant="outline"
          class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20"
          @click="push({ descricao: '', obrigatorio: false })"
        >
          <Plus class="w-4 h-4 mr-2" /> Adicionar item ao checklist
        </Button>
      </div>

      <!-- ── Rodapé ─────────────────────────────────────────────────────── -->
      <div class="flex items-center justify-between border-t mt-10 pt-6">
        <Button type="button" variant="ghost" @click="emit('fechar')">Cancelar</Button>
        <Button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white px-8">
          Cadastrar Software
        </Button>
      </div>

    </form>
  </div>
</template>
