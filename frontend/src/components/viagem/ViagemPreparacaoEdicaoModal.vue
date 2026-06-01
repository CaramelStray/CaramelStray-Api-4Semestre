<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  ArrowLeft, CheckCircle2, ClipboardList, Loader2, Lock,
  MapPinned, Plus, Trash2, Truck, X,
} from 'lucide-vue-next'
import { viagemService, type ViagemCreateDTO, type ViagemResponseDTO } from '@/services/viagemService'

const props = defineProps<{
  open: boolean
  codigoViagem: number
}>()

const emit = defineEmits<{
  close: []
  updated: [viagem: ViagemResponseDTO]
}>()

interface ParadaForm {
  ordem: number
  descricaoLocal: string
  endereco: string
  cidade: string
  estadoRegiao: string
  dataChegadaPrevista: string
  dataSaidaPrevista: string
  observacao: string
}

const step = ref(1)
const loading = ref(false)
const saving = ref(false)
const erro = ref('')
const viagem = ref<ViagemResponseDTO | null>(null)

const stepsList = [
  { id: 1, label: 'Identificação', icon: ClipboardList },
  { id: 2, label: 'Deslocamento', icon: Truck },
  { id: 3, label: 'Paradas', icon: MapPinned },
  { id: 4, label: 'Revisão', icon: CheckCircle2 },
]

const form = reactive({
  origem: '',
  destino: '',
  kmPrevisto: null as number | null,
  dataSaidaPrevista: '',
  dataRetornoPrevisto: '',
  observacao: '',
  paradas: [] as ParadaForm[],
})

function toInputDateTime(iso?: string | null): string {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return ''
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function toApiDateTime(value: string): string | undefined {
  if (!value) return undefined
  return value.length === 16 ? `${value}:00` : value
}

function formatDateTime(dt?: string | null) {
  if (!dt) return '-'
  const d = new Date(dt)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function populateForm(v: ViagemResponseDTO) {
  form.origem = v.origem ?? ''
  form.destino = v.destino ?? ''
  form.kmPrevisto = v.kmPrevisto ?? null
  form.dataSaidaPrevista = toInputDateTime(v.dataSaidaPrevista)
  form.dataRetornoPrevisto = toInputDateTime(v.dataRetornoPrevisto)
  form.observacao = v.observacao ?? ''
  form.paradas = [...(v.paradas ?? [])].sort((a, b) => a.ordem - b.ordem).map(p => ({
    ordem: p.ordem,
    descricaoLocal: p.descricaoLocal ?? '',
    endereco: p.endereco ?? '',
    cidade: p.cidade ?? '',
    estadoRegiao: p.estadoRegiao ?? '',
    dataChegadaPrevista: toInputDateTime(p.dataChegadaPrevista),
    dataSaidaPrevista: toInputDateTime(p.dataSaidaPrevista),
    observacao: p.observacao ?? '',
  }))
}

function resetModal() {
  step.value = 1
  erro.value = ''
  viagem.value = null
  form.origem = ''
  form.destino = ''
  form.kmPrevisto = null
  form.dataSaidaPrevista = ''
  form.dataRetornoPrevisto = ''
  form.observacao = ''
  form.paradas = []
}

function fechar() {
  if (saving.value) return
  resetModal()
  emit('close')
}

function proximoStep() {
  erro.value = ''
  if (step.value < stepsList.length) step.value++
}

function voltarStep() {
  erro.value = ''
  if (step.value > 1) step.value--
}

function adicionarParada() {
  form.paradas.push({
    ordem: form.paradas.length + 1,
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
  form.paradas.forEach((p, i) => { p.ordem = i + 1 })
}

function buildDTO(): ViagemCreateDTO | null {
  if (!viagem.value) return null
  return {
    codigoTipoViagem: viagem.value.codigoTipoViagem,
    codigoCliente: viagem.value.codigoCliente,
    codigoFuncionarioResponsavel: viagem.value.codigoFuncionarioResponsavel,
    codigoOrdemServico: viagem.value.codigoOrdemServico,
    status: viagem.value.status,
    origem: form.origem.trim() || undefined,
    destino: form.destino.trim() || undefined,
    kmPrevisto: form.kmPrevisto ?? undefined,
    dataSaidaPrevista: toApiDateTime(form.dataSaidaPrevista),
    dataRetornoPrevisto: toApiDateTime(form.dataRetornoPrevisto),
    observacao: form.observacao.trim() || undefined,
    paradas: form.paradas.map((p, i) => ({
      ordem: i + 1,
      descricaoLocal: p.descricaoLocal,
      endereco: p.endereco || undefined,
      cidade: p.cidade || undefined,
      estadoRegiao: p.estadoRegiao || undefined,
      dataChegadaPrevista: toApiDateTime(p.dataChegadaPrevista),
      dataSaidaPrevista: toApiDateTime(p.dataSaidaPrevista),
      observacao: p.observacao || undefined,
    })),
  }
}

async function salvar() {
  const dto = buildDTO()
  if (!dto || !viagem.value) return
  saving.value = true
  erro.value = ''
  try {
    const atualizada = await viagemService.atualizar(viagem.value.codigo, dto)
    emit('updated', atualizada)
    resetModal()
    emit('close')
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao salvar preparação.'
  } finally {
    saving.value = false
  }
}

async function carregarViagem() {
  if (!props.codigoViagem) return
  loading.value = true
  erro.value = ''
  try {
    viagem.value = await viagemService.buscarPorId(props.codigoViagem)
    populateForm(viagem.value)
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar preparação.'
  } finally {
    loading.value = false
  }
}

watch(() => props.open, (open) => {
  if (open) {
    step.value = 1
    carregarViagem()
  }
})

onMounted(() => {
  if (props.open) carregarViagem()
})
</script>

<template>
  <Teleport to="body">
    <div
      v-if="props.open"
      class="fixed inset-0 z-[100] flex items-center justify-center"
    >
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="fechar" />

      <!-- Modal card -->
      <div class="relative bg-background border border-border rounded-xl shadow-2xl flex flex-col w-[95vw] md:w-[70vw] max-h-[90vh] overflow-hidden">

        <!-- Modal header -->
        <div class="flex items-center justify-between px-6 py-5 border-b bg-muted/30">
          <div>
            <h2 class="text-2xl font-bold tracking-tight">
              Editar Preparação de Viagem{{ viagem ? ` #${viagem.codigo}` : '' }}
            </h2>
            <p class="text-sm text-muted-foreground mt-1">
              Preencha os detalhes da viagem em etapas
            </p>
          </div>
          <Button
            variant="ghost"
            size="icon"
            class="hover:bg-red-500/10 hover:text-red-500"
            :disabled="saving"
            @click="fechar"
          >
            <X class="w-6 h-6" />
          </Button>
        </div>

        <!-- Step tabs -->
        <div class="border-b border-border bg-muted/10 px-6 py-4 overflow-x-auto">
          <div class="flex items-center gap-2 min-w-max">
            <template v-for="(s, index) in stepsList" :key="s.id">
              <button
                type="button"
                :class="[
                  'flex items-center gap-2 rounded-lg px-3 py-2 text-xs font-semibold transition-colors',
                  step === s.id
                    ? 'bg-blue-500/15 text-blue-400 border border-blue-500/30'
                    : step > s.id
                      ? 'text-blue-400/70 cursor-pointer'
                      : 'text-muted-foreground',
                ]"
                @click="step > s.id ? step = s.id : undefined"
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
              <div v-if="index < stepsList.length - 1" class="h-px w-8 bg-border" />
            </template>
          </div>
        </div>

        <!-- Step content -->
        <div class="flex-1 overflow-y-auto p-6 md:p-8">
          <div v-if="loading" class="flex items-center justify-center gap-3 py-20 text-muted-foreground">
            <Loader2 class="size-5 animate-spin" />
            Carregando dados...
          </div>

          <template v-else>
            <div
              v-if="erro"
              class="mb-5 rounded-md border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300"
            >
              {{ erro }}
            </div>

            <!-- Step 1: Identificação (locked) -->
            <section v-if="step === 1" class="space-y-5">
              <div>
                <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground flex items-center gap-2">
                  <Lock class="size-4" /> Etapa 1 — Identificação
                </h3>
                <p class="text-xs text-muted-foreground mt-1">
                  Dados vinculados à preparação. Não podem ser alterados.
                </p>
              </div>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Cliente</p>
                  <p class="text-sm font-semibold text-foreground">{{ viagem?.nomeCliente ?? `#${viagem?.codigoCliente}` }}</p>
                </div>
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Técnico Responsável</p>
                  <p class="text-sm font-semibold text-foreground">{{ viagem?.nomeFuncionarioResponsavel ?? 'Não atribuído' }}</p>
                </div>
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Ordem de Serviço</p>
                  <p class="text-sm font-semibold text-foreground font-mono">{{ viagem?.codigoOrdemServico ? `#${viagem.codigoOrdemServico}` : '-' }}</p>
                </div>
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-1">
                  <p class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Tipo de Viagem</p>
                  <p class="text-sm font-semibold text-foreground">{{ viagem?.descricaoTipoViagem ?? '-' }}</p>
                </div>
              </div>
            </section>

            <!-- Step 2: Deslocamento -->
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
                  <label class="text-xs font-semibold text-muted-foreground">Origem</label>
                  <Input v-model="form.origem" placeholder="Ex: São José dos Campos - SP" class="bg-sidebar" />
                </div>
                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Destino</label>
                  <Input v-model="form.destino" placeholder="Ex: Santos - SP" class="bg-sidebar" />
                </div>
                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Km Previsto</label>
                  <Input
                    :model-value="form.kmPrevisto ?? undefined"
                    type="number"
                    min="0"
                    placeholder="Ex: 120"
                    class="bg-sidebar"
                    @update:model-value="v => form.kmPrevisto = v ? Number(v) : null"
                  />
                </div>
                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Data de Saída Prevista</label>
                  <Input v-model="form.dataSaidaPrevista" type="datetime-local" class="bg-sidebar" />
                </div>
                <div class="space-y-2">
                  <label class="text-xs font-semibold text-muted-foreground">Data de Retorno Previsto</label>
                  <Input v-model="form.dataRetornoPrevisto" type="datetime-local" class="bg-sidebar" />
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

            <!-- Step 3: Paradas -->
            <section v-if="step === 3" class="space-y-5">
              <div class="flex items-start justify-between gap-4">
                <div>
                  <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                    Etapa 3 — Paradas
                  </h3>
                  <p class="text-xs text-muted-foreground mt-1">
                    Cadastre paradas previstas, se necessário.
                  </p>
                </div>
                <Button type="button" class="bg-blue-600 hover:bg-blue-700 text-white" @click="adicionarParada">
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
                      <h4 class="text-sm font-semibold text-foreground">Parada {{ index + 1 }}</h4>
                    </div>
                    <Button variant="ghost" size="icon" class="text-red-400 hover:text-red-300" @click="removerParada(index)">
                      <Trash2 class="size-4" />
                    </Button>
                  </div>
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Descrição</label>
                      <Input v-model="parada.descricaoLocal" placeholder="Ex: Porto, cliente, posto..." class="bg-background" />
                    </div>
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Endereço</label>
                      <Input v-model="parada.endereco" placeholder="Endereço da parada" class="bg-background" />
                    </div>
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Cidade</label>
                      <Input v-model="parada.cidade" placeholder="Cidade" class="bg-background" />
                    </div>
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Estado</label>
                      <Input v-model="parada.estadoRegiao" placeholder="Estado / região" class="bg-background" />
                    </div>
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Chegada prevista</label>
                      <Input v-model="parada.dataChegadaPrevista" type="datetime-local" class="bg-background" />
                    </div>
                    <div class="space-y-2">
                      <label class="text-xs font-semibold text-muted-foreground">Saída prevista</label>
                      <Input v-model="parada.dataSaidaPrevista" type="datetime-local" class="bg-background" />
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

            <!-- Step 4: Revisão -->
            <section v-if="step === 4" class="space-y-5">
              <div>
                <h3 class="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  Etapa 4 — Revisão
                </h3>
                <p class="text-xs text-muted-foreground mt-1">
                  Confira os dados antes de salvar a preparação.
                </p>
              </div>
              <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <div class="rounded-lg border border-border bg-sidebar p-4 space-y-3">
                  <h4 class="text-sm font-semibold text-foreground">Identificação</h4>
                  <p class="text-sm text-muted-foreground">Cliente: {{ viagem?.nomeCliente ?? '-' }}</p>
                  <p class="text-sm text-muted-foreground">Técnico: {{ viagem?.nomeFuncionarioResponsavel ?? '-' }}</p>
                  <p class="text-sm text-muted-foreground">Ordem de Serviço: {{ viagem?.codigoOrdemServico ? `#${viagem.codigoOrdemServico}` : '-' }}</p>
                  <p class="text-sm text-muted-foreground">Tipo de Viagem: {{ viagem?.descricaoTipoViagem ?? '-' }}</p>
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
                  <h4 class="text-sm font-semibold text-foreground">Paradas</h4>
                  <p class="text-sm text-muted-foreground">{{ form.paradas.length }} parada(s)</p>
                  <div v-if="form.paradas.length" class="space-y-2">
                    <p v-for="(parada, index) in form.paradas" :key="index" class="text-xs text-muted-foreground">
                      {{ index + 1 }}. {{ parada.descricaoLocal || 'Sem descrição' }}<span v-if="parada.cidade"> — {{ parada.cidade }}</span>
                    </p>
                  </div>
                </div>
                <div v-if="form.observacao" class="rounded-lg border border-border bg-sidebar p-4 space-y-2">
                  <h4 class="text-sm font-semibold text-foreground">Observação</h4>
                  <p class="text-sm text-muted-foreground whitespace-pre-line">{{ form.observacao }}</p>
                </div>
              </div>
            </section>
          </template>
        </div>

        <!-- Footer navigation -->
        <div class="flex items-center justify-between border-t border-border bg-muted/30 px-6 py-4">
          <Button
            variant="outline"
            :disabled="saving"
            @click="step === 1 ? fechar() : voltarStep()"
          >
            <ArrowLeft v-if="step > 1" class="size-4 mr-2" />
            {{ step === 1 ? 'Cancelar' : 'Anterior' }}
          </Button>
          <div class="flex items-center gap-3">
            <Button
              v-if="step < stepsList.length"
              class="bg-blue-600 hover:bg-blue-700 text-white"
              :disabled="loading"
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
              Salvar Preparação
            </Button>
          </div>
        </div>

      </div>
    </div>
  </Teleport>
</template>
