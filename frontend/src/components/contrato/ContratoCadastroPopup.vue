<template>
  <Dialog :open="open" @update:open="closeDialog">
    <DialogContent class="sm:max-w-[850px] max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle class="text-xl font-bold text-foreground">{{ isEditMode ? 'Editar Contrato' : 'Novo Contrato' }}</DialogTitle>
      </DialogHeader>

      <div class="flex items-center gap-2 mb-6 border-b border-border pb-6 mt-2">
        <div :class="['flex items-center gap-2 transition-colors', step === 1 ? 'text-blue-400 font-bold' : step > 1 ? 'text-blue-400/60' : 'text-muted-foreground']">
          <div
            class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
            :class="step === 1 ? 'border-blue-500 bg-blue-500/20 text-blue-400' : step > 1 ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60' : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
          >
            <FileText class="w-4 h-4" />
          </div>
          <span class="text-sm hidden sm:inline-block">Dados do Contrato</span>
        </div>

        <template v-if="!isEditMode">
          <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

          <div :class="['flex items-center gap-2 transition-colors', step === 2 ? 'text-blue-400 font-bold' : step > 2 ? 'text-blue-400/60' : 'text-muted-foreground']">
            <div
              class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
              :class="step === 2 ? 'border-blue-500 bg-blue-500/20 text-blue-400' : step > 2 ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60' : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
            >
              <Server class="w-4 h-4" />
            </div>
            <span class="text-sm hidden sm:inline-block">Máquinas Base</span>
          </div>

          <ChevronRight class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />

          <div :class="['flex items-center gap-2 transition-colors', step === 3 ? 'text-blue-400 font-bold' : 'text-muted-foreground']">
            <div
              class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
              :class="step === 3 ? 'border-blue-500 bg-blue-500/20 text-blue-400' : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
            >
              <Layers class="w-4 h-4" />
            </div>
            <span class="text-sm hidden sm:inline-block">Softwares (Opcional)</span>
          </div>
        </template>
      </div>

      <form @submit="onSubmit">
        
        <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">
          <FormField v-slot="{ value, handleChange }" name="codigoCliente">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Cliente <span class="text-red-500 font-bold">*</span></FormLabel>
              <Select :model-value="value" @update:model-value="handleChange">
                <FormControl><SelectTrigger><SelectValue placeholder="Selecione um cliente..." /></SelectTrigger></FormControl>
                <SelectContent class="z-[200] max-h-[200px]">
                  <SelectItem v-for="c in clientes" :key="c.id" :value="c.id.toString()">
                    {{ c.nomeEmpresa }} ({{ c.documento }})
                  </SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ value, handleChange }" name="status">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Status do Contrato <span class="text-red-500 font-bold">*</span></FormLabel>
              <Select :model-value="value" @update:model-value="handleChange">
                <FormControl><SelectTrigger><SelectValue placeholder="Status" /></SelectTrigger></FormControl>
                <SelectContent class="z-[200]">
                  <SelectItem value="ATIVO">Ativo</SelectItem>
                  <SelectItem value="INATIVO">Inativo</SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="dataInicio">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Data de Início <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Input type="date" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="dataFim">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Data de Fim <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Input type="date" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="periodoManutencaoPreventiva">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Período de Manutenção (meses) <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Input type="number" placeholder="Ex: 6" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="vencimentoManutencaoPreventiva">
            <FormItem>
              <FormLabel class="flex items-center gap-1">Próxima Manutenção <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Input type="date" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="descricao">
            <FormItem class="md:col-span-2">
              <FormLabel class="flex items-center gap-1">Descrição do Contrato <span class="text-red-500 font-bold">*</span></FormLabel>
              <FormControl><Textarea placeholder="Detalhes do contrato, SLA, etc..." class="resize-y min-h-[80px]" v-bind="componentField" /></FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ value, handleChange }" name="conexaoInternet">
            <FormItem class="md:col-span-2 flex flex-row items-center space-x-3 space-y-0 rounded-md border p-4 bg-muted/20">
              <FormControl>
                <Checkbox :checked="value" @update:checked="handleChange" />
              </FormControl>
              <div class="space-y-1 leading-none">
                <FormLabel>Acesso à Internet</FormLabel>
                <p class="text-xs text-muted-foreground">Marque se a infraestrutura deste contrato possui uma boa conexão com a internet.</p>
              </div>
            </FormItem>
          </FormField>
        </div>

        <div v-show="step === 2" class="space-y-6">
          <div v-if="maquinasFields.length === 0" class="text-center p-8 border border-dashed rounded-lg bg-muted/10">
            <p class="text-sm text-red-500 font-bold">Você precisa adicionar pelo menos uma máquina.</p>
          </div>

          <div v-for="(field, index) in maquinasFields" :key="field.key" class="relative p-5 border rounded-lg bg-card shadow-sm group">
            <div class="flex justify-between items-center mb-4 border-b pb-2">
              <h4 class="font-bold text-sm">Máquina {{ index + 1 }}</h4>
              <Button v-if="maquinasFields.length > 1" type="button" variant="ghost" size="icon" class="text-muted-foreground hover:text-red-500 hover:bg-red-50" @click="removeMaquina(index)">
                <Trash2 class="w-4 h-4" />
              </Button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4">
              <FormField :name="`maquinas[${index}].codigoMaquina`" v-slot="{ value, handleChange }">
                <FormItem class="md:col-span-2">
                  <FormLabel class="flex items-center gap-1">Modelo do Catálogo <span class="text-red-500 font-bold">*</span></FormLabel>
                  <Select :model-value="value" @update:model-value="handleChange">
                    <FormControl><SelectTrigger><SelectValue placeholder="Selecione um modelo no catálogo" /></SelectTrigger></FormControl>
                    <SelectContent class="z-[200] max-h-[200px]">
                      <SelectItem v-for="m in catalogoMaquinas" :key="m.codigo" :value="m.codigo.toString()">
                        {{ m.descricao }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField :name="`maquinas[${index}].apelido`" v-slot="{ componentField }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Apelido <span class="text-red-500 font-bold">*</span></FormLabel>
                  <FormControl><Input placeholder="Ex: Servidor Principal" v-bind="componentField" /></FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField :name="`maquinas[${index}].numeroPatrimonio`" v-slot="{ componentField }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Patrimônio <span class="text-red-500 font-bold">*</span></FormLabel>
                  <FormControl><Input placeholder="Ex: INV-2023-441" v-bind="componentField" /></FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField :name="`maquinas[${index}].localizacaoFisica`" v-slot="{ componentField }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Localização <span class="text-red-500 font-bold">*</span></FormLabel>
                  <FormControl><Input placeholder="Ex: Sala 2 - Rack A" v-bind="componentField" /></FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField :name="`maquinas[${index}].dataInstalacao`" v-slot="{ componentField }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Instalação <span class="text-red-500 font-bold">*</span></FormLabel>
                  <FormControl><Input type="date" v-bind="componentField" /></FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>
            </div>
          </div>

          <Button type="button" variant="outline" class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20" @click="pushMaquina({ codigoMaquina: '', apelido: '', numeroPatrimonio: '', localizacaoFisica: '', dataInstalacao: '' })">
            <Plus class="w-4 h-4 mr-2" /> Adicionar Outra Máquina
          </Button>
        </div>

        <div v-show="step === 3" class="space-y-6">
          <div v-if="softwaresFields.length === 0" class="text-center p-8 border border-dashed rounded-lg bg-muted/10">
            <p class="text-sm text-muted-foreground">Nenhum software vinculado. Você pode adicionar as licenças instaladas nas máquinas.</p>
          </div>

          <div v-for="(field, index) in softwaresFields" :key="field.key" class="relative p-5 border rounded-lg bg-card shadow-sm group">
            <Button type="button" variant="ghost" size="icon" class="absolute top-3 right-3 text-muted-foreground hover:text-red-500 hover:bg-red-50" @click="removeSoftware(index)">
              <Trash2 class="w-4 h-4" />
            </Button>

            <div class="grid grid-cols-1 gap-4 pr-10">
              <FormField :name="`softwares[${index}].maquinaIndex`" v-slot="{ value, handleChange }">
                <FormItem>
                  <FormLabel class="flex items-center gap-1">Em qual máquina será instalado? <span class="text-red-500 font-bold">*</span></FormLabel>
                  <Select :model-value="value !== undefined ? String(value) : undefined" @update:model-value="v => handleChange(Number(v))">
                    <FormControl><SelectTrigger><SelectValue placeholder="Selecione a máquina da lista..." /></SelectTrigger></FormControl>
                    <SelectContent class="z-[200]">
                      <SelectItem v-for="(maq, maqIdx) in formValues.maquinas" :key="maqIdx" :value="String(maqIdx)">
                        {{ maq.apelido || `Máquina ${maqIdx + 1}` }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              </FormField>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <FormField :name="`softwares[${index}].codigoSoftware`" v-slot="{ value, handleChange }">
                  <FormItem>
                    <FormLabel class="flex items-center gap-1">Software <span class="text-red-500 font-bold">*</span></FormLabel>
                    <Select :model-value="value" @update:model-value="handleChange">
                      <FormControl><SelectTrigger><SelectValue placeholder="Selecione o software..." /></SelectTrigger></FormControl>
                      <SelectContent class="z-[200] max-h-[200px]">
                        <SelectItem v-for="s in catalogoSoftwares" :key="s.id" :value="s.id.toString()">
                          {{ s.nomeSoftware }} - {{ s.versao }}
                        </SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                </FormField>

                <FormField :name="`softwares[${index}].versaoInstalada`" v-slot="{ componentField }">
                  <FormItem>
                    <FormLabel class="flex items-center gap-1">Versão / Chave <span class="text-red-500 font-bold">*</span></FormLabel>
                    <FormControl><Input placeholder="Ex: 2.1.0 ou Key-123" v-bind="componentField" /></FormControl>
                    <FormMessage />
                  </FormItem>
                </FormField>
              </div>
            </div>
          </div>

          <Button type="button" variant="outline" class="w-full border-dashed border-2 bg-muted/5 hover:bg-muted/20" @click="pushSoftware({ maquinaIndex: undefined, codigoSoftware: '', versaoInstalada: '' })">
            <Plus class="w-4 h-4 mr-2" /> Adicionar Software
          </Button>
        </div>

        <div class="flex items-center justify-between border-t border-border mt-12 pt-6">
          <Button type="button" variant="ghost" class="hover:bg-muted/30" @click="closeDialog(false)">Cancelar</Button>

          <div class="flex gap-3">
            <Button v-if="step > 1" type="button" variant="outline" class="border-border hover:bg-muted/30" @click="step--">Voltar</Button>

            <Button v-if="step < totalSteps" type="button" class="bg-blue-600 hover:bg-blue-500 text-white px-8 shadow-md shadow-blue-900/20" @click="nextStep">
              Próximo <ArrowRight class="w-4 h-4 ml-2" />
            </Button>

            <Button v-if="step === totalSteps" type="submit" class="bg-emerald-600 hover:bg-emerald-500 text-white px-8 shadow-md shadow-emerald-900/20" :disabled="loading">
              {{ loading ? 'Salvando...' : isEditMode ? 'Salvar Alterações' : 'Finalizar Contrato' }}
            </Button>
          </div>
        </div>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useForm, useFieldArray } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'

import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ChevronRight, FileText, Server, Layers, Plus, Trash2, ArrowRight } from 'lucide-vue-next'

import { clienteService } from '@/services/clienteService'
import { contratoService, type ContratoResponseDTO } from '@/services/contratoService'
import { catalogoMaquinaService } from '@/services/catalogoMaquinaService'
import { maquinaContratoService } from '@/services/maquinaContratoService'
import { catalogoSoftwareService } from '@/services/catalogoSoftwareService.ts'
import { maquinaSoftwareInstaladoService } from '@/services/maquinaSoftwareInstaladoService'

const props = defineProps<{
  open: boolean
  initialData?: ContratoResponseDTO | null
}>()
const emit = defineEmits<{
  'update:open': [value: boolean]
  success: [contrato: ContratoResponseDTO]
}>()

const isEditMode = computed(() => !!props.initialData)

const step = ref(1)
const loading = ref(false)

const clientes = ref<any[]>([])
const catalogoMaquinas = ref<any[]>([])
const catalogoSoftwares = ref<any[]>([])

onMounted(async () => {
  try {
    clientes.value = await clienteService.listar()
    catalogoMaquinas.value = await catalogoMaquinaService.listarTodos()
    catalogoSoftwares.value = await catalogoSoftwareService.listar()
  } catch (error) {
    console.error("Erro ao carregar dados:", error)
  }
})

// Schema com suporte a arrays para máquinas e softwares
const formSchema = toTypedSchema(z.object({
  codigoCliente: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  status: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  dataInicio: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  dataFim: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  periodoManutencaoPreventiva: z.coerce.number({ required_error: 'Campo obrigatório' }).min(1, 'Informe um período válido'),
  vencimentoManutencaoPreventiva: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  descricao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  conexaoInternet: z.boolean().default(false),

  maquinas: z.array(z.object({
    codigoMaquina: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    apelido: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    numeroPatrimonio: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    localizacaoFisica: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    dataInstalacao: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
  })).min(1, 'Adicione pelo menos uma máquina'),

  softwares: z.array(z.object({
    maquinaIndex: z.number({ required_error: 'Campo obrigatório' }),
    codigoSoftware: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório'),
    versaoInstalada: z.string({ required_error: 'Campo obrigatório' }).min(1, 'Campo obrigatório')
  })).optional().default([])
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    status: 'ATIVO',
    conexaoInternet: false,
    periodoManutencaoPreventiva: 6,
    descricao: '', // Inicializando vazio para disparar erro se não preenchido
    maquinas: [{
      codigoMaquina: '', apelido: '', numeroPatrimonio: '', localizacaoFisica: '', dataInstalacao: ''
    }],
    softwares: []
  }
})

// form.values é necessário para popular o select condicional de máquinas
const formValues = form.values

// Separando as instâncias de FieldArray
const { fields: maquinasFields, push: pushMaquina, remove: removeMaquina } = useFieldArray('maquinas')
const { fields: softwaresFields, push: pushSoftware, remove: removeSoftware } = useFieldArray('softwares')

// No modo de edição, só há 1 step (dados do contrato)
const totalSteps = computed(() => isEditMode.value ? 1 : 3)

const popularFormEdicao = async (data: ContratoResponseDTO) => {
  await nextTick()
  form.resetForm({
    values: {
      codigoCliente: data.codigoCliente?.toString() ?? '',
      status: data.status ?? 'ATIVO',
      dataInicio: data.dataInicio ?? '',
      dataFim: data.dataFim ?? '',
      periodoManutencaoPreventiva: data.periodoManutencaoPreventiva ?? 6,
      vencimentoManutencaoPreventiva: data.vencimentoManutencaoPreventiva ?? '',
      descricao: data.descricao ?? '',
      conexaoInternet: (data as any).conexaoInternet ?? false,
      maquinas: [{ codigoMaquina: '', apelido: '', numeroPatrimonio: '', localizacaoFisica: '', dataInstalacao: '' }],
      softwares: []
    }
  })
}

watch(() => props.initialData, (data) => {
  if (data) popularFormEdicao(data)
}, { immediate: true })

watch(() => props.open, (val) => {
  if (val && props.initialData) popularFormEdicao(props.initialData)
})

// Validação por Steps
const nextStep = async () => {
  let fieldsToValidate: string[] = []

  if (step.value === 1) {
    fieldsToValidate = ['codigoCliente', 'status', 'dataInicio', 'dataFim', 'periodoManutencaoPreventiva', 'vencimentoManutencaoPreventiva', 'descricao']
  } else if (step.value === 2) {
    fieldsToValidate = maquinasFields.value.flatMap((_, i) => [
      `maquinas[${i}].codigoMaquina`,
      `maquinas[${i}].apelido`,
      `maquinas[${i}].numeroPatrimonio`,
      `maquinas[${i}].localizacaoFisica`,
      `maquinas[${i}].dataInstalacao`,
    ])
  }

  const results = await Promise.all(
    fieldsToValidate.map(field => form.validateField(field as any))
  )
  const hasErrors = results.some(r => !r.valid)

  if (!hasErrors) step.value++
}

function closeDialog(val: boolean) {
  emit('update:open', val)
  if (!val) {
    step.value = 1
    form.resetForm()
  }
}

const onSubmit = form.handleSubmit(async (values) => {
  loading.value = true
  try {
    const payload = {
      codigoCliente: Number(values.codigoCliente),
      descricao: values.descricao,
      dataInicio: values.dataInicio,
      dataFim: values.dataFim,
      status: values.status,
      periodoManutencaoPreventiva: values.periodoManutencaoPreventiva,
      conexaoInternet: values.conexaoInternet,
      vencimentoManutencaoPreventiva: values.vencimentoManutencaoPreventiva
    }

    let contratoSalvo: ContratoResponseDTO

    if (isEditMode.value && props.initialData) {
      contratoSalvo = await contratoService.atualizar(props.initialData.codigo, payload)
    } else {
      contratoSalvo = await contratoService.criar(payload)
      const contratoId = contratoSalvo.codigo

      const maquinasGeradasIds: number[] = []
      for (const maq of values.maquinas) {
        const maquinaSalva = await maquinaContratoService.criar({
          codigoContrato: contratoId,
          codigoCatalogoMaquina: Number(maq.codigoMaquina),
          apelido: maq.apelido,
          numeroPatrimonio: maq.numeroPatrimonio,
          localizacaoFisica: maq.localizacaoFisica,
          dataInstalacao: maq.dataInstalacao,
          status: 'ATIVO'
        })
        maquinasGeradasIds.push(maquinaSalva.codigo)
      }

      if (values.softwares && values.softwares.length > 0) {
        await Promise.all(values.softwares.map(soft => {
          const idRealMaquina = maquinasGeradasIds[soft.maquinaIndex]
          if (idRealMaquina === undefined) throw new Error('Máquina vinculada ao software não encontrada.')
          return maquinaSoftwareInstaladoService.criar({
            codigoMaquinaContrato: idRealMaquina,
            codigoSoftware: Number(soft.codigoSoftware),
            versaoInstalada: soft.versaoInstalada,
            status: 'ATIVO'
          })
        }))
      }
    }

    closeDialog(false)
    emit('success', contratoSalvo)
  } catch (error) {
    console.error("Erro ao salvar contrato:", error)
    alert('Ocorreu um erro na comunicação com o servidor. Verifique o console.')
  } finally {
    loading.value = false
  }
})
</script>
