<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  ArrowLeft,
  CheckCircle2,
  ClipboardList,
  Clock,
  Loader2,
  MapPinned,
  Plus,
  Route,
  Trash2,
  Truck,
  X,
} from 'lucide-vue-next'

import {
  viagemService,
  type TipoViagemResponseDTO,
  type ViagemCreateDTO,
  type ViagemResponseDTO,
} from '@/services/viagemService'
import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import {
  ordemServicoService,
  type OrdemServicoResponseDTO,
} from '@/services/ordemServicoService'

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  close: []
  created: [viagem: ViagemResponseDTO]
}>()

interface ParadaForm {
  descricaoLocal: string
  endereco: string
  cidade: string
  estadoRegiao: string
  dataChegadaPrevista: string
  dataSaidaPrevista: string
  observacao: string
}

const step = ref(1)
const loadingOptions = ref(false)
const saving = ref(false)
const erro = ref('')

const clientes = ref<ClienteResponseDTO[]>([])
const tecnicos = ref<TecnicoResponseDTO[]>([])
const ordensServico = ref<OrdemServicoResponseDTO[]>([])
const tiposViagem = ref<TipoViagemResponseDTO[]>([])

const form = reactive({
  codigoCliente: null as number | null,
  codigoOrdemServico: null as number | null,
  codigoTipoViagem: null as number | null,
  codigoFuncionarioResponsavel: null as number | null,

  origem: '',
  destino: '',
  kmPrevisto: null as number | null,
  dataSaidaPrevista: '',
  dataRetornoPrevisto: '',
  observacao: '',

  duracaoManutencaoHoras: 0,
  duracaoManutencaoMinutos: 0,
  duracaoViagemHoras: 0,
  duracaoViagemMinutos: 0,

  paradas: [] as ParadaForm[],
})

const stepsList = [
  { id: 1, label: 'Identificação', icon: ClipboardList },
  { id: 2, label: 'Deslocamento', icon: Truck },
  { id: 3, label: 'Estimativas', icon: Clock },
  { id: 4, label: 'Paradas', icon: MapPinned },
  { id: 5, label: 'Revisão', icon: CheckCircle2 },
]

const tiposViagemAtivos = computed(() =>
  tiposViagem.value.filter(tipo => tipo.ativo)
)

const ordensDisponiveis = computed(() => {
  if (!form.codigoCliente) return ordensServico.value
  return ordensServico.value.filter(ordem => ordem.codigoCliente === form.codigoCliente)
})

const clienteSelecionado = computed(() =>
  clientes.value.find(cliente => cliente.id === form.codigoCliente)
)

const ordemSelecionada = computed(() =>
  ordensServico.value.find(ordem => ordem.codigo === form.codigoOrdemServico)
)

const tipoViagemSelecionado = computed(() =>
  tiposViagem.value.find(tipo => tipo.codigo === form.codigoTipoViagem)
)

const tecnicoSelecionado = computed(() =>
  tecnicos.value.find(tecnico => tecnico.id === form.codigoFuncionarioResponsavel)
)

const totalManutencaoMinutos = computed(() =>
  normalizarNumero(form.duracaoManutencaoHoras) * 60 +
  normalizarNumero(form.duracaoManutencaoMinutos)
)

const totalViagemMinutos = computed(() =>
  normalizarNumero(form.duracaoViagemHoras) * 60 +
  normalizarNumero(form.duracaoViagemMinutos)
)

const totalMissaoMinutos = computed(() =>
  totalManutencaoMinutos.value + totalViagemMinutos.value
)

const tempoTotalMissaoFormatado = computed(() =>
  formatarMinutos(totalMissaoMinutos.value)
)

watch(
  () => form.codigoCliente,
  () => {
    if (
      form.codigoOrdemServico &&
      !ordensDisponiveis.value.some(ordem => ordem.codigo === form.codigoOrdemServico)
    ) {
      form.codigoOrdemServico = null
    }
  },
)

function normalizarNumero(value: number | null | undefined) {
  const numero = Number(value)
  if (Number.isNaN(numero) || numero < 0) return 0
  return numero
}

function formatarMinutos(totalMinutos: number) {
  const horas = Math.floor(totalMinutos / 60)
  const minutos = totalMinutos % 60
  return `${String(horas).padStart(2, '0')}:${String(minutos).padStart(2, '0')}`
}

function formatDateTime(dt?: string | null) {
  if (!dt) return '-'
  const data = new Date(dt)
  if (Number.isNaN(data.getTime())) return '-'

  return data.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatarOrdem(ordem: OrdemServicoResponseDTO) {
  const data = ordem.dataAgendamento
    ? ` - ${formatDateTime(ordem.dataAgendamento)}`
    : ''

  return `#${ordem.codigo} - ${ordem.status ?? 'Sem status'}${data}`
}

function toApiDateTime(value?: string | null) {
  if (!value) return undefined
  if (value.length === 16) return `${value}:00`
  return value
}

function resetForm() {
  step.value = 1
  erro.value = ''

  form.codigoCliente = null
  form.codigoOrdemServico = null
  form.codigoTipoViagem = null
  form.codigoFuncionarioResponsavel = null

  form.origem = ''
  form.destino = ''
  form.kmPrevisto = null
  form.dataSaidaPrevista = ''
  form.dataRetornoPrevisto = ''
  form.observacao = ''

  form.duracaoManutencaoHoras = 0
  form.duracaoManutencaoMinutos = 0
  form.duracaoViagemHoras = 0
  form.duracaoViagemMinutos = 0

  form.paradas = []
}

function fecharModal() {
  if (saving.value) return
  resetForm()
  emit('close')
}

function validarStepAtual() {
  erro.value = ''

  if (step.value === 1) {
    if (!form.codigoCliente) {
      erro.value = 'Selecione um cliente.'
      return false
    }

    if (!form.codigoTipoViagem) {
      erro.value = 'Selecione o tipo de viagem.'
      return false
    }

    if (!form.codigoFuncionarioResponsavel) {
      erro.value = 'Selecione o técnico responsável.'
      return false
    }
  }

  if (step.value === 2) {
    if (!form.origem.trim()) {
      erro.value = 'Informe a origem.'
      return false
    }

    if (!form.destino.trim()) {
      erro.value = 'Informe o destino.'
      return false
    }

    if (!form.dataSaidaPrevista) {
      erro.value = 'Informe a data de saída prevista.'
      return false
    }
  }

  if (step.value === 3) {
    const campos = [
      form.duracaoManutencaoHoras,
      form.duracaoManutencaoMinutos,
      form.duracaoViagemHoras,
      form.duracaoViagemMinutos,
    ]

    if (campos.some(valor => Number(valor) < 0)) {
      erro.value = 'As durações não podem ter valores negativos.'
      return false
    }

    if (
      Number(form.duracaoManutencaoMinutos) > 59 ||
      Number(form.duracaoViagemMinutos) > 59
    ) {
      erro.value = 'Os minutos devem ficar entre 0 e 59.'
      return false
    }
  }

  return true
}

function proximoStep() {
  if (!validarStepAtual()) return
  if (step.value < 5) step.value += 1
}

function voltarStep() {
  erro.value = ''
  if (step.value > 1) step.value -= 1
}

function adicionarParada() {
  form.paradas.push({
    descricaoLocal: '',
    endereco: '',
    cidade: '',
    estadoRegiao: '',
    dataChegadaPrevista: '',
    dataSaidaPrevista: '',
    observacao: '',
  })
}

function removerParada(index: number) {
  form.paradas.splice(index, 1)
}

function selecionarOrdem() {
  const ordem = ordemSelecionada.value
  if (!ordem) return

  if (!form.codigoCliente) {
    form.codigoCliente = ordem.codigoCliente
  }

  if (!form.codigoFuncionarioResponsavel && ordem.codigoFuncionario) {
    form.codigoFuncionarioResponsavel = ordem.codigoFuncionario
  }
}

function montarObservacaoFinal() {
  const observacaoBase = form.observacao.trim()

  const resumoTempo = [
    '',
    '--- Estimativas da missão ---',
    `Duração da manutenção: ${formatarMinutos(totalManutencaoMinutos.value)}`,
    `Duração da viagem (ida + volta): ${formatarMinutos(totalViagemMinutos.value)}`,
    `Tempo total estimado da missão: ${tempoTotalMissaoFormatado.value}`,
  ].join('\n')

  return `${observacaoBase}${resumoTempo}`.trim()
}

async function salvar() {
  if (!validarStepAtual()) return

  if (!form.codigoCliente || !form.codigoTipoViagem || !form.codigoFuncionarioResponsavel) {
    erro.value = 'Preencha os campos obrigatórios antes de salvar.'
    return
  }

  saving.value = true
  erro.value = ''

  const dto: ViagemCreateDTO = {
    codigoCliente: form.codigoCliente,
    codigoTipoViagem: form.codigoTipoViagem,
    codigoFuncionarioResponsavel: form.codigoFuncionarioResponsavel,
    codigoOrdemServico: form.codigoOrdemServico ?? undefined,
    status: 'ABERTA',
    origem: form.origem.trim(),
    destino: form.destino.trim(),
    kmPrevisto: form.kmPrevisto ?? undefined,
    dataSaidaPrevista: toApiDateTime(form.dataSaidaPrevista),
    dataRetornoPrevisto: toApiDateTime(form.dataRetornoPrevisto),
    observacao: montarObservacaoFinal(),
    paradas: form.paradas.map((parada, index) => ({
      ordem: index + 1,
      descricaoLocal: parada.descricaoLocal,
      endereco: parada.endereco || undefined,
      cidade: parada.cidade || undefined,
      estadoRegiao: parada.estadoRegiao || undefined,
      dataChegadaPrevista: toApiDateTime(parada.dataChegadaPrevista),
      dataSaidaPrevista: toApiDateTime(parada.dataSaidaPrevista),
      observacao: parada.observacao || undefined,
    })),
  }

  try {
    const viagemCriada = await viagemService.criar(dto)
    emit('created', viagemCriada)
    resetForm()
    emit('close')
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao cadastrar preparação de viagem.'
  } finally {
    saving.value = false
  }
}

async function carregarOpcoes() {
  loadingOptions.value = true
  erro.value = ''

  try {
    const [clientesResponse, tecnicosResponse, ordensResponse, tiposResponse] = await Promise.all([
      clienteService.listar(),
      tecnicoService.listar(),
      ordemServicoService.listar(),
      viagemService.listarTipos(),
    ])

    clientes.value = clientesResponse
    tecnicos.value = tecnicosResponse
    ordensServico.value = ordensResponse
    tiposViagem.value = tiposResponse
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar opções do formulário.'
  } finally {
    loadingOptions.value = false
  }
}

onMounted(carregarOpcoes)
</script>

<template>
  <Teleport to="body">
    <div
      v-if="props.open"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4"
    >
      <div class="w-full max-w-5xl max-h-[92vh] overflow-hidden rounded-xl border border-border bg-background shadow-2xl">
        <div class="flex items-center justify-between border-b border-border bg-sidebar px-6 py-4">
          <div>
            <h2 class="text-lg font-bold text-foreground">Nova Preparação de Viagem</h2>
            <p class="text-xs text-muted-foreground">
              Cadastre uma nova missão em etapas
            </p>
          </div>

          <Button
            variant="ghost"
            size="icon"
            class="text-muted-foreground hover:text-foreground"
            :disabled="saving"
            @click="fecharModal"
          >
            <X class="size-5" />
          </Button>
        </div>

        <div class="border-b border-border bg-sidebar px-6 py-4 overflow-x-auto">
          <div class="flex items-center gap-2 min-w-max">
            <template v-for="(s, index) in stepsList" :key="s.id">
              <button
                type="button"
                :class="[
                  'flex items-center gap-2 rounded-lg px-3 py-2 text-xs font-semibold transition-colors',
                  step === s.id
                    ? 'bg-blue-500/15 text-blue-400 border border-blue-500/30'
                    : step > s.id
                      ? 'text-blue-400/70'
                      : 'text-muted-foreground',
                ]"
                @click="step > s.id ? step = s.id : null"
              >
                <div
                  :class="[
                    'flex size-7 items-center justify-center rounded-full border',
                    step === s.id
                      ? 'border-blue-500 bg-blue-500/20'
                      : step > s.id
                        ? 'border-blue-500/40 bg-blue-500/10'
                        : 'border-border bg-muted/20',
                  ]"
                >
                  <CheckCircle2 v-if="step > s.id" class="size-4" />
                  <component v-else :is="s.icon" class="size-4" />
                </div>
                {{ s.label }}
              </button>

              <div
                v-if="index < stepsList.length - 1"
                class="h-px w-8 bg-border"
              />
            </template>
          </div>
        </div>

        <div class="max-h-[62vh] overflow-y-auto p-6">
          <div v-if="loadingOptions" class="flex items-center justify-center gap-3 py-20 text-muted-foreground">
            <Loader2 class="size-5 animate-spin" />
            Carregando dados do formulário...
          </div>

          <template v-else>
            <div
              v-if="erro"
              class="mb-5 rounded-md border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300"
            >
              {{ erro }}
            </div>

            <section v-if="step === 1" class="space-y-5">
              <div>
                <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  Etapa 1 — Identificação
                </h3>
                <p class="text-xs text-muted-foreground mt-1">
                  Informe os dados principais da preparação.
                </p>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Cliente *</label>
                  <select
                    v-model.number="form.codigoCliente"
                    class="h-10 w-full rounded-md border border-border bg-sidebar px-3 text-sm text-foreground"
                  >
                    <option :value="null">Selecione um cliente</option>
                    <option
                      v-for="cliente in clientes"
                      :key="cliente.id"
                      :value="cliente.id"
                    >
                      {{ cliente.nomeEmpresa }}
                    </option>
                  </select>
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Ordem de Serviço vinculada</label>
                  <select
                    v-model.number="form.codigoOrdemServico"
                    class="h-10 w-full rounded-md border border-border bg-sidebar px-3 text-sm text-foreground"
                    @change="selecionarOrdem"
                  >
                    <option :value="null">Sem ordem vinculada</option>
                    <option
                      v-for="ordem in ordensDisponiveis"
                      :key="ordem.codigo"
                      :value="ordem.codigo"
                    >
                      {{ formatarOrdem(ordem) }}
                    </option>
                  </select>
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Tipo de Viagem *</label>
                  <select
                    v-model.number="form.codigoTipoViagem"
                    class="h-10 w-full rounded-md border border-border bg-sidebar px-3 text-sm text-foreground"
                  >
                    <option :value="null">Selecione um tipo</option>
                    <option
                      v-for="tipo in tiposViagemAtivos"
                      :key="tipo.codigo"
                      :value="tipo.codigo"
                    >
                      {{ tipo.descricao }}
                    </option>
                  </select>
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Técnico Responsável *</label>
                  <select
                    v-model.number="form.codigoFuncionarioResponsavel"
                    class="h-10 w-full rounded-md border border-border bg-sidebar px-3 text-sm text-foreground"
                  >
                    <option :value="null">Selecione um técnico</option>
                    <option
                      v-for="tecnico in tecnicos"
                      :key="tecnico.id"
                      :value="tecnico.id"
                    >
                      {{ tecnico.nome }} — {{ tecnico.email }}
                    </option>
                  </select>
                </div>
              </div>
            </section>

            <section v-if="step === 2" class="space-y-5">
              <div>
                <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  Etapa 2 — Deslocamento
                </h3>
                <p class="text-xs text-muted-foreground mt-1">
                  Informe origem, destino, quilometragem e datas previstas.
                </p>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Origem *</label>
                  <Input
                    v-model="form.origem"
                    placeholder="Ex: São José dos Campos - SP"
                    class="bg-sidebar"
                  />
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Destino *</label>
                  <Input
                    v-model="form.destino"
                    placeholder="Ex: Santos - SP"
                    class="bg-sidebar"
                  />
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Km Previsto</label>
                  <Input
                    v-model.number="form.kmPrevisto"
                    type="number"
                    min="0"
                    placeholder="Ex: 120"
                    class="bg-sidebar"
                  />
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Data de Saída Prevista *</label>
                  <Input
                    v-model="form.dataSaidaPrevista"
                    type="datetime-local"
                    class="bg-sidebar"
                  />
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Data de Retorno Previsto</label>
                  <Input
                    v-model="form.dataRetornoPrevisto"
                    type="datetime-local"
                    class="bg-sidebar"
                  />
                </div>

                <div class="space-y-2 md:col-span-2">
                  <label class="text-xs font-semibold text-muted-foreground">Observação</label>
                  <textarea
                    v-model="form.observacao"
                    rows="4"
                    placeholder="Observações gerais da preparação..."
                    class="w-full rounded-md border border-border bg-sidebar px-3 py-2 text-sm text-foreground outline-none focus:ring-1 focus:ring-sidebar-primary"
                  />
                </div>
              </div>
            </section>

            <section v-if="step === 3" class="space-y-5">
              <div>
                <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  Etapa 3 — Estimativas de Duração
                </h3>
                <p class="text-xs text-muted-foreground mt-1">
                  Informe a duração prevista da manutenção e da viagem.
                </p>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-4">
                  <div class="flex items-center gap-2">
                    <Clock class="size-4 text-blue-400" />
                    <h4 class="text-sm font-semibold text-foreground">Duração da Manutenção</h4>
                  </div>

                  <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Horas</label>
                      <Input
                        v-model.number="form.duracaoManutencaoHoras"
                        type="number"
                        min="0"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Minutos</label>
                      <Input
                        v-model.number="form.duracaoManutencaoMinutos"
                        type="number"
                        min="0"
                        max="59"
                        class="bg-background"
                      />
                    </div>
                  </div>
                </div>

                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-4">
                  <div class="flex items-center gap-2">
                    <Route class="size-4 text-cyan-400" />
                    <h4 class="text-sm font-semibold text-foreground">Duração da Viagem</h4>
                  </div>

                  <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Horas</label>
                      <Input
                        v-model.number="form.duracaoViagemHoras"
                        type="number"
                        min="0"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Minutos</label>
                      <Input
                        v-model.number="form.duracaoViagemMinutos"
                        type="number"
                        min="0"
                        max="59"
                        class="bg-background"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div class="rounded-xl border border-blue-500/30 bg-blue-500/10 p-5">
                <p class="text-xs font-bold uppercase tracking-wider text-blue-300">
                  Tempo Total Estimado da Missão
                </p>
                <p class="mt-2 text-4xl font-bold text-foreground">
                  {{ tempoTotalMissaoFormatado }}
                </p>
                <p class="mt-2 text-xs text-muted-foreground">
                  Soma da duração da manutenção com a duração da viagem de ida e volta.
                </p>
              </div>
            </section>

            <section v-if="step === 4" class="space-y-5">
              <div class="flex items-start justify-between gap-4">
                <div>
                  <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                    Etapa 4 — Paradas
                  </h3>
                  <p class="text-xs text-muted-foreground mt-1">
                    Cadastre paradas previstas, se necessário.
                  </p>
                </div>

                <Button
                  type="button"
                  class="bg-blue-600 hover:bg-blue-700 text-white"
                  @click="adicionarParada"
                >
                  <Plus class="size-4 mr-2" />
                  Adicionar parada
                </Button>
              </div>

              <div
                v-if="!form.paradas.length"
                class="rounded-md border border-dashed border-border bg-sidebar p-8 text-center text-sm text-muted-foreground"
              >
                Nenhuma parada cadastrada. Esta etapa é opcional.
              </div>

              <div v-else class="space-y-4">
                <div
                  v-for="(parada, index) in form.paradas"
                  :key="index"
                  class="rounded-lg border border-border bg-sidebar p-4 space-y-4"
                >
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                      <div class="flex size-8 items-center justify-center rounded-full border border-cyan-500/30 bg-cyan-500/10 text-xs font-bold text-cyan-300">
                        {{ index + 1 }}
                      </div>
                      <h4 class="text-sm font-semibold text-foreground">
                        Parada {{ index + 1 }}
                      </h4>
                    </div>

                    <Button
                      variant="ghost"
                      size="icon"
                      class="text-red-400 hover:text-red-300"
                      @click="removerParada(index)"
                    >
                      <Trash2 class="size-4" />
                    </Button>
                  </div>

                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Descrição</label>
                      <Input
                        v-model="parada.descricaoLocal"
                        placeholder="Ex: Porto, cliente, posto..."
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Endereço</label>
                      <Input
                        v-model="parada.endereco"
                        placeholder="Endereço da parada"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Cidade</label>
                      <Input
                        v-model="parada.cidade"
                        placeholder="Cidade"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Estado</label>
                      <Input
                        v-model="parada.estadoRegiao"
                        placeholder="Estado / região"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Chegada prevista</label>
                      <Input
                        v-model="parada.dataChegadaPrevista"
                        type="datetime-local"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Saída prevista</label>
                      <Input
                        v-model="parada.dataSaidaPrevista"
                        type="datetime-local"
                        class="bg-background"
                      />
                    </div>

                    <div class="space-y-2 md:col-span-2">
                      <label class="text-xs font-semibold text-muted-foreground">Observação</label>
                      <textarea
                        v-model="parada.observacao"
                        rows="3"
                        placeholder="Observação da parada..."
                        class="w-full rounded-md border border-border bg-background px-3 py-2 text-sm text-foreground outline-none focus:ring-1 focus:ring-sidebar-primary"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </section>

            <section v-if="step === 5" class="space-y-5">
              <div>
                <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  Etapa 5 — Revisão
                </h3>
                <p class="text-xs text-muted-foreground mt-1">
                  Confira os dados antes de cadastrar a preparação.
                </p>
              </div>

              <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-3">
                  <h4 class="text-sm font-semibold text-foreground">Identificação</h4>
                  <p class="text-sm text-muted-foreground">Cliente: {{ clienteSelecionado?.nomeEmpresa ?? '-' }}</p>
                  <p class="text-sm text-muted-foreground">Ordem de Serviço: {{ ordemSelecionada ? `#${ordemSelecionada.codigo}` : '-' }}</p>
                  <p class="text-sm text-muted-foreground">Tipo de Viagem: {{ tipoViagemSelecionado?.descricao ?? '-' }}</p>
                  <p class="text-sm text-muted-foreground">Técnico: {{ tecnicoSelecionado?.nome ?? '-' }}</p>
                </div>

                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-3">
                  <h4 class="text-sm font-semibold text-foreground">Deslocamento</h4>
                  <p class="text-sm text-muted-foreground">Origem: {{ form.origem || '-' }}</p>
                  <p class="text-sm text-muted-foreground">Destino: {{ form.destino || '-' }}</p>
                  <p class="text-sm text-muted-foreground">Km Previsto: {{ form.kmPrevisto ?? '-' }}</p>
                  <p class="text-sm text-muted-foreground">Saída: {{ formatDateTime(form.dataSaidaPrevista) }}</p>
                  <p class="text-sm text-muted-foreground">Retorno: {{ formatDateTime(form.dataRetornoPrevisto) }}</p>
                </div>

                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-3">
                  <h4 class="text-sm font-semibold text-foreground">Estimativas</h4>
                  <p class="text-sm text-muted-foreground">
                    Manutenção: {{ formatarMinutos(totalManutencaoMinutos) }}
                  </p>
                  <p class="text-sm text-muted-foreground">
                    Viagem: {{ formatarMinutos(totalViagemMinutos) }}
                  </p>
                  <p class="text-lg font-bold text-foreground">
                    Total: {{ tempoTotalMissaoFormatado }}
                  </p>
                </div>

                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-3">
                  <h4 class="text-sm font-semibold text-foreground">Paradas</h4>
                  <p class="text-sm text-muted-foreground">
                    {{ form.paradas.length }} parada(s) cadastrada(s)
                  </p>
                  <div v-if="form.paradas.length" class="space-y-2">
                    <p
                      v-for="(parada, index) in form.paradas"
                      :key="index"
                      class="text-xs text-muted-foreground"
                    >
                      {{ index + 1 }}. {{ parada.descricaoLocal || 'Sem descrição' }}
                      <span v-if="parada.cidade"> — {{ parada.cidade }}</span>
                    </p>
                  </div>
                </div>
              </div>

              <div
                v-if="form.observacao"
                class="rounded-lg border border-border bg-sidebar p-4"
              >
                <h4 class="text-sm font-semibold text-foreground mb-2">Observação</h4>
                <p class="text-sm text-muted-foreground whitespace-pre-line">{{ form.observacao }}</p>
              </div>
            </section>
          </template>
        </div>

        <div class="flex items-center justify-between border-t border-border bg-sidebar px-6 py-4">
          <Button
            variant="outline"
            :disabled="saving"
            @click="step === 1 ? fecharModal() : voltarStep()"
          >
            <ArrowLeft v-if="step > 1" class="size-4 mr-2" />
            {{ step === 1 ? 'Cancelar' : 'Anterior' }}
          </Button>

          <div class="flex items-center gap-3">
            <Button
              v-if="step < 5"
              class="bg-blue-600 hover:bg-blue-700 text-white"
              :disabled="loadingOptions || saving"
              @click="proximoStep"
            >
              Próximo
            </Button>

            <Button
              v-else
              class="bg-emerald-600 hover:bg-emerald-700 text-white"
              :disabled="saving"
              @click="salvar"
            >
              <Loader2 v-if="saving" class="size-4 mr-2 animate-spin" />
              <CheckCircle2 v-else class="size-4 mr-2" />
              Cadastrar Preparação
            </Button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>