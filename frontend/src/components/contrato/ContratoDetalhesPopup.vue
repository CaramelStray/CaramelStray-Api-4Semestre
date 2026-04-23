<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import {
  FileText, Info, Building2, Calendar, Wrench, Wifi, Hash, Activity,
  AlignLeft, Server, Layers, Package, CheckCircle2, XCircle,
} from 'lucide-vue-next'

import { type ContratoResponseDTO } from '@/services/contratoService'
import { maquinaContratoService, type MaquinaContratoResponseDTO } from '@/services/maquinaContratoService'
import { maquinaSoftwareInstaladoService, type MaquinaSoftwareInstaladoResponseDTO } from '@/services/maquinaSoftwareInstaladoService'
import { catalogoMaquinaService } from '@/services/catalogoMaquinaService'
import { catalogoSoftwareService } from '@/services/catalogoSoftwareService.ts'

const props = defineProps<{
  open: boolean
  contrato: ContratoResponseDTO | null
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
}>()

// ── Estado ────────────────────────────────────────────────────────────────────
const carregando = ref(false)
const maquinas = ref<MaquinaContratoResponseDTO[]>([])
const softwaresPorMaquina = ref<Record<number, MaquinaSoftwareInstaladoResponseDTO[]>>({})
const catalogoMaquinaMap = ref<Record<number, string>>({})
const catalogoSoftwareMap = ref<Record<number, string>>({})

// ── Formatadores ──────────────────────────────────────────────────────────────
const formatDate = (value?: string | null) => {
  if (!value) return '—'
  const d = new Date(`${value}T00:00:00`)
  return Number.isNaN(d.getTime()) ? value : new Intl.DateTimeFormat('pt-BR').format(d)
}

const getDias = (dataFim?: string | null): number | null => {
  if (!dataFim) return null
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const v = new Date(`${dataFim}T00:00:00`)
  if (Number.isNaN(v.getTime())) return null
  return Math.ceil((v.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
}

// ── Computed ──────────────────────────────────────────────────────────────────
const contratoId = computed(() => {
  if (!props.contrato) return '—'
  return props.contrato.descricao?.trim() || `CTR-${String(props.contrato.codigo).padStart(4, '0')}`
})

const dias = computed(() => getDias(props.contrato?.dataFim))

const statusLabel = computed(() => {
  if (!props.contrato) return '—'
  if (props.contrato.status === 'INATIVO') return 'Inativo'
  if (dias.value !== null && dias.value < 0) return 'Vencido'
  if (dias.value !== null && dias.value <= 30) return 'Expirando'
  return 'Ativo'
})

const statusBadgeClass = computed(() => {
  const s = statusLabel.value
  if (s === 'Ativo') return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30'
  if (s === 'Expirando') return 'bg-red-500/20 text-red-400 border-red-500/30'
  if (s === 'Vencido') return 'bg-amber-500/20 text-amber-400 border-amber-500/30'
  return 'bg-slate-500/20 text-slate-300 border-slate-500/30'
})

const statusDotClass = computed(() => {
  const s = statusLabel.value
  if (s === 'Ativo') return 'bg-emerald-500'
  if (s === 'Expirando') return 'bg-red-500'
  if (s === 'Vencido') return 'bg-amber-500'
  return 'bg-slate-500'
})

const vigenciaTexto = computed(() => {
  if (dias.value === null) return ''
  if (dias.value < 0) return `Venceu há ${Math.abs(dias.value)} dias`
  if (dias.value === 0) return 'Vence hoje'
  if (dias.value <= 30) return `Vence em ${dias.value} dias`
  return `${dias.value} dias restantes`
})

const vigenciaClass = computed(() => {
  if (dias.value === null) return 'text-muted-foreground'
  if (dias.value < 0) return 'text-amber-400'
  if (dias.value <= 30) return 'text-red-400'
  return 'text-emerald-400'
})

// ── Carga de dados ────────────────────────────────────────────────────────────
const carregarDados = async () => {
  if (!props.contrato) return
  carregando.value = true
  maquinas.value = []
  softwaresPorMaquina.value = {}

  try {
    const [catMaquinas, catSoftwares, maquinasContrato] = await Promise.all([
      catalogoMaquinaService.listarTodos().catch(() => []),
      catalogoSoftwareService.listar().catch(() => []),
      maquinaContratoService.buscarPorContrato(props.contrato.codigo).catch(() => []),
    ])

    catalogoMaquinaMap.value = Object.fromEntries(catMaquinas.map(m => [m.codigo, m.descricao]))
    catalogoSoftwareMap.value = Object.fromEntries(catSoftwares.map(s => [s.id, s.nomeSoftware]))
    maquinas.value = maquinasContrato

    const softwaresResultados = await Promise.all(
      maquinasContrato.map(m =>
        maquinaSoftwareInstaladoService.buscarPorMaquinaContrato(m.codigo).catch(() => [])
      )
    )
    const mapa: Record<number, MaquinaSoftwareInstaladoResponseDTO[]> = {}
    maquinasContrato.forEach((m, i) => { mapa[m.codigo] = softwaresResultados[i] })
    softwaresPorMaquina.value = mapa
  } finally {
    carregando.value = false
  }
}

watch(() => props.open, (val) => { if (val) carregarDados() })
</script>

<template>
  <Dialog :open="open" @update:open="emit('update:open', $event)">
    <DialogContent class="sm:max-w-[860px] max-h-[90vh] overflow-y-auto">

      <!-- Header -->
      <DialogHeader class="pb-5 border-b border-border">
        <div class="flex items-start gap-4">
          <div class="flex items-center justify-center w-12 h-12 rounded-xl bg-blue-500/10 border border-blue-500/20 shrink-0 mt-0.5">
            <FileText class="w-6 h-6 text-blue-400" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center flex-wrap gap-3">
              <DialogTitle class="text-xl font-bold text-foreground font-mono tracking-tight">
                {{ contratoId }}
              </DialogTitle>
              <span
                class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[10px] font-bold uppercase tracking-wide"
                :class="statusBadgeClass"
              >
                <CheckCircle2 v-if="statusLabel === 'Ativo'" class="w-3 h-3" />
                <XCircle v-else class="w-3 h-3" />
                {{ statusLabel }}
              </span>
            </div>
            <p class="text-sm text-muted-foreground mt-1">
              Cliente:
              <span class="text-foreground font-medium">{{ contrato?.nomeCliente ?? 'Não informado' }}</span>
              <span v-if="contrato?.emailContatoCliente" class="text-muted-foreground"> · {{ contrato.emailContatoCliente }}</span>
            </p>
          </div>
        </div>
      </DialogHeader>

      <!-- Loading -->
      <div v-if="carregando" class="flex flex-col items-center justify-center py-16 gap-3">
        <div class="w-8 h-8 border-2 border-blue-500/30 border-t-blue-400 rounded-full animate-spin" />
        <p class="text-sm text-muted-foreground">Carregando dados do contrato...</p>
      </div>

      <div v-else class="space-y-6 mt-1">

        <!-- Informações Gerais -->
        <section class="space-y-3">
          <div class="flex items-center gap-2">
            <Info class="w-3.5 h-3.5 text-muted-foreground" />
            <h3 class="text-[11px] font-bold uppercase tracking-widest text-muted-foreground">Informações Gerais</h3>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">

            <!-- Código -->
            <div class="flex flex-col gap-1.5 p-3 rounded-lg border border-border bg-muted/10">
              <div class="flex items-center gap-1.5 text-muted-foreground">
                <Hash class="w-3.5 h-3.5" />
                <span class="text-[10px] font-bold uppercase tracking-wider">Código</span>
              </div>
              <span class="font-mono text-sm font-semibold text-foreground">{{ contratoId }}</span>
            </div>

            <!-- Status -->
            <div class="flex flex-col gap-1.5 p-3 rounded-lg border border-border bg-muted/10">
              <div class="flex items-center gap-1.5 text-muted-foreground">
                <Activity class="w-3.5 h-3.5" />
                <span class="text-[10px] font-bold uppercase tracking-wider">Status</span>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full shrink-0" :class="statusDotClass" />
                <span class="text-sm font-medium text-foreground">{{ statusLabel }}</span>
              </div>
            </div>

            <!-- Conexão Internet -->
            <div class="flex flex-col gap-1.5 p-3 rounded-lg border border-border bg-muted/10">
              <div class="flex items-center gap-1.5 text-muted-foreground">
                <Wifi class="w-3.5 h-3.5" />
                <span class="text-[10px] font-bold uppercase tracking-wider">Internet</span>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full shrink-0" :class="contrato?.conexaoInternet ? 'bg-emerald-500' : 'bg-slate-500'" />
                <span class="text-sm font-medium text-foreground">
                  {{ contrato?.conexaoInternet ? 'Disponível' : 'Não disponível' }}
                </span>
              </div>
            </div>

            <!-- Vigência -->
            <div class="flex flex-col gap-1.5 p-3 rounded-lg border border-border bg-muted/10 sm:col-span-2">
              <div class="flex items-center gap-1.5 text-muted-foreground">
                <Calendar class="w-3.5 h-3.5" />
                <span class="text-[10px] font-bold uppercase tracking-wider">Vigência</span>
              </div>
              <div class="flex items-center gap-2 flex-wrap">
                <span class="text-sm font-medium text-foreground">
                  {{ formatDate(contrato?.dataInicio) }}
                </span>
                <span class="text-muted-foreground text-xs">→</span>
                <span class="text-sm font-medium text-foreground">
                  {{ formatDate(contrato?.dataFim) }}
                </span>
                <span v-if="vigenciaTexto" class="text-xs font-medium px-2 py-0.5 rounded-full bg-muted/40" :class="vigenciaClass">
                  {{ vigenciaTexto }}
                </span>
              </div>
            </div>

            <!-- Manutenção Preventiva -->
            <div class="flex flex-col gap-1.5 p-3 rounded-lg border border-border bg-muted/10">
              <div class="flex items-center gap-1.5 text-muted-foreground">
                <Wrench class="w-3.5 h-3.5" />
                <span class="text-[10px] font-bold uppercase tracking-wider">Manutenção Preventiva</span>
              </div>
              <span class="text-sm font-medium text-foreground">
                A cada {{ contrato?.periodoManutencaoPreventiva }} meses
              </span>
              <span class="text-xs text-muted-foreground">
                Próxima: {{ formatDate(contrato?.vencimentoManutencaoPreventiva) }}
              </span>
            </div>

          </div>
        </section>

        <!-- Descrição -->
        <section v-if="contrato?.descricao" class="space-y-3">
          <div class="flex items-center gap-2">
            <AlignLeft class="w-3.5 h-3.5 text-muted-foreground" />
            <h3 class="text-[11px] font-bold uppercase tracking-widest text-muted-foreground">Descrição do Contrato</h3>
          </div>
          <div class="p-4 rounded-lg border border-border bg-muted/10 text-sm text-foreground leading-relaxed whitespace-pre-wrap">
            {{ contrato.descricao }}
          </div>
        </section>

        <!-- Máquinas -->
        <section class="space-y-3">
          <div class="flex items-center gap-2">
            <Server class="w-3.5 h-3.5 text-muted-foreground" />
            <h3 class="text-[11px] font-bold uppercase tracking-widest text-muted-foreground">Máquinas Base</h3>
            <span class="px-1.5 py-0.5 rounded bg-muted/40 text-[10px] font-bold text-muted-foreground">
              {{ maquinas.length }}
            </span>
          </div>

          <div v-if="maquinas.length === 0"
            class="text-center py-10 border border-dashed border-border rounded-lg bg-muted/5">
            <Server class="w-8 h-8 mx-auto text-muted-foreground/30 mb-2" />
            <p class="text-sm text-muted-foreground">Nenhuma máquina cadastrada neste contrato.</p>
          </div>

          <div v-for="(maq, idx) in maquinas" :key="maq.codigo"
            class="border border-border rounded-lg bg-muted/5 overflow-hidden">

            <!-- Cabeçalho da máquina -->
            <div class="flex items-center justify-between px-4 py-3 border-b border-border bg-muted/20">
              <div class="flex items-center gap-3">
                <div class="flex items-center justify-center w-7 h-7 rounded-md bg-blue-500/10 border border-blue-500/20">
                  <Server class="w-3.5 h-3.5 text-blue-400" />
                </div>
                <div class="flex items-center gap-2 flex-wrap">
                  <span class="text-sm font-semibold text-foreground">
                    {{ catalogoMaquinaMap[maq.codigoCatalogoMaquina] ?? `Máquina ${idx + 1}` }}
                  </span>
                  <span class="text-[10px] text-muted-foreground font-mono bg-muted/40 px-1.5 py-0.5 rounded border border-border">
                    ID #{{ maq.codigo }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Metadados da máquina -->
            <div class="px-4 py-3 flex flex-wrap gap-x-6 gap-y-1.5">
              <div class="flex items-center gap-2 text-sm">
                <Hash class="w-3.5 h-3.5 text-muted-foreground shrink-0" />
                <span class="text-muted-foreground text-xs">Nº de Série:</span>
                <span class="text-foreground font-mono text-xs font-medium">{{ maq.numeroSerie || '—' }}</span>
              </div>
              <div class="flex items-center gap-2 text-sm">
                <Calendar class="w-3.5 h-3.5 text-muted-foreground shrink-0" />
                <span class="text-muted-foreground text-xs">Instalação:</span>
                <span class="text-foreground text-xs font-medium">{{ formatDate(maq.dataInstalacao) }}</span>
              </div>
            </div>

            <!-- Softwares instalados -->
            <div class="px-4 pb-4">
              <div class="flex items-center gap-2 mb-2.5">
                <Layers class="w-3.5 h-3.5 text-muted-foreground" />
                <span class="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">
                  Softwares Instalados
                </span>
                <span class="px-1.5 py-0.5 rounded bg-muted/40 text-[10px] font-bold text-muted-foreground">
                  {{ (softwaresPorMaquina[maq.codigo] ?? []).length }}
                </span>
              </div>

              <p v-if="(softwaresPorMaquina[maq.codigo] ?? []).length === 0"
                class="text-xs text-muted-foreground/60 italic">
                Nenhum software instalado nesta máquina.
              </p>

              <div v-else class="flex flex-wrap gap-2">
                <div
                  v-for="sw in softwaresPorMaquina[maq.codigo]"
                  :key="sw.codigo"
                  class="flex items-center gap-1.5 px-2.5 py-1.5 rounded-md border border-border bg-muted/20 text-xs group/sw"
                >
                  <Package class="w-3 h-3 text-purple-400 shrink-0" />
                  <span class="text-foreground font-medium">
                    {{ catalogoSoftwareMap[sw.codigoSoftware] ?? `Software #${sw.codigoSoftware}` }}
                  </span>
                  <span class="text-muted-foreground font-mono">v{{ sw.versaoInstalada }}</span>
                  <span v-if="sw.chaveLicenca"
                    class="ml-1 text-amber-400/80 font-mono text-[10px] border-l border-border pl-1.5">
                    {{ sw.chaveLicenca }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </section>

      </div>

      <!-- Footer -->
      <DialogFooter class="pt-4 border-t border-border mt-4">
        <Button variant="ghost" class="hover:bg-muted/30" @click="emit('update:open', false)">
          Fechar
        </Button>
      </DialogFooter>

    </DialogContent>
  </Dialog>
</template>
