<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import FullCalendar from '@fullcalendar/vue3'
import type { CalendarOptions, EventInput, EventClickArg, DatesSetArg } from '@fullcalendar/core'
import type { DateClickArg } from '@fullcalendar/interaction'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import ptBrLocale from '@fullcalendar/core/locales/pt-br'

import { Button } from '@/components/ui/button'
import { CalendarDays, Loader2, X, ChevronRight, ClipboardList } from 'lucide-vue-next'

import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import { clienteService } from '@/services/clienteService'
import { tecnicoService } from '@/services/tecnicoService'
import { manutencaoService } from '@/services/manutencaoService'

const router = useRouter()

// ─── Dados ────────────────────────────────────────────────────────────────────
const ordens     = ref<OrdemServicoResponseDTO[]>([])
const clienteMap = ref(new Map<number, string>())
const tecnicoMap = ref(new Map<number, string>())
const loading    = ref(true)

// ─── Filtros ──────────────────────────────────────────────────────────────────
const filtroCrit   = ref<string[]>([])
const filtroStatus = ref<string[]>([])

const CRIT_OPTS = ['CRITICA', 'ALTA', 'MEDIA', 'BAIXA'] as const
const STATUS_OPTS = [
  { value: 'AGUARDANDO',  label: 'Aguardando'  },
  { value: 'AGENDADO',    label: 'Agendado'    },
  { value: 'EM_EXECUCAO', label: 'Em Execução' },
  { value: 'CONCLUIDA',   label: 'Concluída'   },
  { value: 'FINALIZADA',  label: 'Finalizada'  },
  { value: 'CANCELADA',   label: 'Cancelada'   },
]

function toggleCrit(c: string) {
  filtroCrit.value = filtroCrit.value.includes(c)
    ? filtroCrit.value.filter(x => x !== c)
    : [...filtroCrit.value, c]
}
function toggleStatus(s: string) {
  filtroStatus.value = filtroStatus.value.includes(s)
    ? filtroStatus.value.filter(x => x !== s)
    : [...filtroStatus.value, s]
}

// ─── Estilos de criticidade ───────────────────────────────────────────────────
const CRIT_COLOR: Record<string, string> = {
  CRITICA: '#ef4444',
  ALTA:    '#f97316',
  MEDIA:   '#f59e0b',
  BAIXA:   '#3b82f6',
}

const crit: Record<string, { dot: string; label: string; pill: string; chipActive: string }> = {
  CRITICA: { dot: 'bg-red-500',    label: 'Crítica', pill: 'bg-red-500/20 text-red-300 border border-red-500/40',       chipActive: 'bg-red-500/25 text-red-300 border-red-500/50 ring-1 ring-red-500/50' },
  ALTA:    { dot: 'bg-orange-500', label: 'Alta',    pill: 'bg-orange-500/20 text-orange-300 border border-orange-500/40', chipActive: 'bg-orange-500/25 text-orange-300 border-orange-500/50 ring-1 ring-orange-500/50' },
  MEDIA:   { dot: 'bg-amber-500',  label: 'Média',   pill: 'bg-amber-500/20 text-amber-300 border border-amber-500/40',  chipActive: 'bg-amber-500/25 text-amber-300 border-amber-500/50 ring-1 ring-amber-500/50' },
  BAIXA:   { dot: 'bg-blue-500',   label: 'Baixa',   pill: 'bg-blue-500/20 text-blue-300 border border-blue-500/40',    chipActive: 'bg-blue-500/25 text-blue-300 border-blue-500/50 ring-1 ring-blue-500/50' },
}

// ─── Ordens filtradas ─────────────────────────────────────────────────────────
const ordensFiltradas = computed(() =>
  ordens.value.filter(o => {
    const okCrit   = filtroCrit.value.length   === 0 || filtroCrit.value.includes(o.criticidade)
    const okStatus = filtroStatus.value.length === 0 || filtroStatus.value.includes(o.status)
    return okCrit && okStatus
  }),
)

// ─── Eventos FullCalendar — técnico principal + cliente no título ─────────────
const events = computed((): EventInput[] =>
  ordensFiltradas.value
    .filter(o => o.dataAgendamento)
    .map(o => {
      const nomeTec = o.codigoFuncionario ? (tecnicoMap.value.get(o.codigoFuncionario) ?? '') : ''
      const nomeCli = clienteMap.value.get(o.codigoCliente) ?? ''
      const titulo  = `${nomeTec ? nomeTec + ' - ' : ''}#${o.codigo}${nomeCli ? ' · ' + nomeCli : ''}`
      return {
        id:              String(o.codigo),
        title:           titulo,
        start:           o.dataAgendamento!.slice(0, 10),
        allDay:          true,
        backgroundColor: CRIT_COLOR[o.criticidade] ?? '#64748b',
        borderColor:     'transparent',
        textColor:       '#ffffff',
        extendedProps:   { ordem: o },
      }
    }),
)

// ─── Bottom-sheet (clique no dia) ─────────────────────────────────────────────
const sheetDay     = ref<string | null>(null)
// Técnicos por ordem (codigo → lista de IDs): inclui todos do historico
const tecnicosOrdem = ref(new Map<number, number[]>())

const sheetOrdens = computed(() =>
  sheetDay.value
    ? ordensFiltradas.value.filter(o => o.dataAgendamento?.slice(0, 10) === sheetDay.value)
    : [],
)

const sheetLabel = computed(() => {
  if (!sheetDay.value) return ''
  const [y, m, d] = sheetDay.value.split('-').map(Number)
  return new Date(y, m - 1, d).toLocaleDateString('pt-BR', {
    weekday: 'long', day: '2-digit', month: 'long',
  })
})

// Ao mudar o dia, busca todos os técnicos de cada ordem via historico
watch(sheetDay, async (dia) => {
  if (!dia) return
  for (const o of sheetOrdens.value) {
    if (tecnicosOrdem.value.has(o.codigo)) continue
    if (o.codigoHistoricoManutencao) {
      try {
        const ids = await manutencaoService.listarFuncionarios(o.codigoHistoricoManutencao)
        tecnicosOrdem.value.set(o.codigo, ids)
      } catch {
        // fallback: mostra apenas o técnico principal
        if (o.codigoFuncionario) tecnicosOrdem.value.set(o.codigo, [o.codigoFuncionario])
      }
    } else if (o.codigoFuncionario) {
      tecnicosOrdem.value.set(o.codigo, [o.codigoFuncionario])
    }
  }
})

// ─── Modal de detalhes ────────────────────────────────────────────────────────
const showModal        = ref(false)
const ordemSelecionada = ref<OrdemServicoResponseDTO | null>(null)

function abrirModal(o: OrdemServicoResponseDTO) {
  ordemSelecionada.value = o
  showModal.value = true
}

// ─── CalendarOptions ──────────────────────────────────────────────────────────
const currentDate = ref(new Date())

const calendarOptions = computed((): CalendarOptions => ({
  plugins:     [dayGridPlugin, interactionPlugin],
  locale:      ptBrLocale,
  initialView: 'dayGridMonth',
  headerToolbar: {
    left:   'prev,next',
    center: 'title',
    right:  'today',
  },
  buttonText: { today: 'Hoje' },
  events:       events.value,
  dayMaxEvents: 3,
  height:       'auto',
  eventClick: (info: EventClickArg) => {
    info.jsEvent.stopPropagation()
    const ordem = info.event.extendedProps['ordem'] as OrdemServicoResponseDTO
    abrirModal(ordem)
  },
  dateClick: (info: DateClickArg) => {
    sheetDay.value = info.dateStr
  },
  datesSet: (info: DatesSetArg) => {
    currentDate.value = info.view.currentStart
  },
}))

// ─── Helpers ──────────────────────────────────────────────────────────────────
function nomeCliente(id: number)             { return clienteMap.value.get(id) ?? `Cliente #${id}` }
function nomeTecnico(id: number | undefined) { return id ? (tecnicoMap.value.get(id) ?? `Técnico #${id}`) : '—' }
function formatarData(s: string | undefined) { if (!s) return '—'; const [y,m,d] = s.slice(0,10).split('-'); return `${d}/${m}/${y}` }
function labelTipo(tipo: string | undefined) {
  return ({ INSTALACAO:'Instalação', MANUTENCAO_PREVENTIVA:'Manutenção Preventiva', MANUTENCAO_CORRETIVA:'Manutenção Corretiva' })[tipo ?? ''] ?? tipo ?? '—'
}
function labelStatus(status: string) {
  return ({ AGUARDANDO:'Aguardando', AGENDADO:'Agendado', EM_EXECUCAO:'Em Execução', CONCLUIDA:'Concluída', FINALIZADA:'Finalizada', CANCELADA:'Cancelada' })[status] ?? status
}

// ─── Carga inicial ────────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    const [ordensData, clientesData, tecnicosData] = await Promise.all([
      ordemServicoService.listar(),
      clienteService.listar(),
      tecnicoService.listar(),
    ])
    ordens.value = ordensData
    clientesData.forEach(c => clienteMap.value.set(c.id, c.nomeEmpresa))
    tecnicosData.forEach(t => tecnicoMap.value.set(t.id, t.nome))
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="p-3 sm:p-6 space-y-4">

    <!-- Toggle de calendários -->
    <div class="flex items-center gap-1 p-1 rounded-lg bg-sidebar border border-border w-fit">
      <button
        class="px-4 py-1.5 rounded-md text-sm font-semibold transition-all bg-sidebar-primary text-white shadow-sm"
      >
        Calendário de Ordens
      </button>
      <button
        class="px-4 py-1.5 rounded-md text-sm font-semibold transition-all text-muted-foreground hover:text-foreground hover:bg-muted/40"
        @click="router.push('/calendario-tecnicos')"
      >
        Calendário de Técnicos
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-24 gap-2 text-muted-foreground">
      <Loader2 class="size-5 animate-spin" />
      <span class="text-sm">Carregando...</span>
    </div>

    <template v-else>

      <!-- Filtros de criticidade -->
      <div class="flex flex-wrap gap-2 items-center">
        <span class="text-xs text-muted-foreground font-medium shrink-0">Criticidade:</span>
        <button
          v-for="c in CRIT_OPTS" :key="c"
          :class="[
            'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-[11px] font-bold uppercase tracking-wide border transition-all',
            filtroCrit.includes(c) ? crit[c]?.chipActive : 'bg-muted/20 text-muted-foreground border-border hover:bg-muted/40',
          ]"
          @click="toggleCrit(c)"
        >
          <span :class="['size-1.5 rounded-full', crit[c]?.dot]" />
          {{ crit[c]?.label }}
        </button>
      </div>

      <!-- Filtros de status -->
      <div class="flex flex-wrap gap-2 items-center">
        <span class="text-xs text-muted-foreground font-medium shrink-0">Status:</span>
        <button
          v-for="s in STATUS_OPTS" :key="s.value"
          :class="[
            'px-3 py-1 rounded-full text-[11px] font-semibold border transition-all',
            filtroStatus.includes(s.value)
              ? 'bg-sidebar-primary/20 text-sidebar-primary border-sidebar-primary/50 ring-1 ring-sidebar-primary/40'
              : 'bg-muted/20 text-muted-foreground border-border hover:bg-muted/40',
          ]"
          @click="toggleStatus(s.value)"
        >
          {{ s.label }}
        </button>
      </div>

      <!-- Legenda de criticidade -->
      <div class="flex flex-wrap gap-1.5">
        <span
          v-for="(c, k) in crit" :key="k"
          :class="['inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wide', c.pill]"
        >
          <span :class="['size-1.5 rounded-full', c.dot]" />{{ c.label }}
        </span>
      </div>

      <!-- FullCalendar -->
      <div class="rounded-xl border border-border overflow-hidden">
        <FullCalendar :options="calendarOptions" />
      </div>

    </template>

    <!-- ───── Modal de detalhes ───── -->
    <Transition
      enter-active-class="transition-opacity duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="showModal && ordemSelecionada"
        class="fixed inset-0 z-50 bg-black/60 flex items-center justify-center p-4"
        @click="showModal = false"
      >
        <div
          class="w-full max-w-sm rounded-2xl border border-border bg-sidebar p-5 shadow-2xl"
          @click.stop
        >
          <div class="flex items-start justify-between mb-4">
            <div class="flex flex-col gap-1">
              <div class="flex items-center gap-2">
                <ClipboardList class="w-4 h-4 text-blue-400" />
                <span class="font-bold text-foreground text-base">#OM-{{ ordemSelecionada.codigo }}</span>
              </div>
              <span
                :class="[
                  'self-start px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wide',
                  crit[ordemSelecionada.criticidade]?.pill ?? 'bg-muted/30 text-muted-foreground border border-border',
                ]"
              >
                {{ crit[ordemSelecionada.criticidade]?.label ?? ordemSelecionada.criticidade }}
              </span>
            </div>
            <button class="p-1 rounded-md hover:bg-muted transition-colors" @click="showModal = false">
              <X class="size-4 text-muted-foreground" />
            </button>
          </div>

          <div class="space-y-3 text-sm">
            <div class="rounded-lg bg-blue-500/10 border border-blue-500/20 px-3 py-2">
              <p class="text-[10px] text-blue-400 font-semibold uppercase tracking-wide mb-0.5">Cliente</p>
              <p class="text-foreground font-medium">{{ nomeCliente(ordemSelecionada.codigoCliente) }}</p>
            </div>

            <div class="grid grid-cols-2 gap-2 text-xs">
              <div class="space-y-0.5">
                <p class="text-muted-foreground">Tipo</p>
                <p class="text-foreground font-medium">{{ labelTipo(ordemSelecionada.tipoOrdem) }}</p>
              </div>
              <div class="space-y-0.5">
                <p class="text-muted-foreground">Status</p>
                <p class="text-foreground font-medium">{{ labelStatus(ordemSelecionada.status) }}</p>
              </div>
              <div class="space-y-0.5">
                <p class="text-muted-foreground">Agendamento</p>
                <p class="text-foreground font-medium">{{ formatarData(ordemSelecionada.dataAgendamento) }}</p>
              </div>
              <div class="space-y-0.5">
                <p class="text-muted-foreground">Técnico resp.</p>
                <p class="text-foreground font-medium truncate">{{ nomeTecnico(ordemSelecionada.codigoFuncionario) }}</p>
              </div>
            </div>

            <div v-if="ordemSelecionada.observacaoGeral" class="text-xs text-muted-foreground bg-muted/30 rounded-lg px-3 py-2">
              {{ ordemSelecionada.observacaoGeral }}
            </div>
          </div>

          <Button
            class="w-full mt-4"
            @click="router.push(`/ordens/${ordemSelecionada.codigo}`); showModal = false"
          >
            Ver ordem completa
          </Button>
        </div>
      </div>
    </Transition>

    <!-- ───── Bottom-sheet overlay ───── -->
    <Transition
      enter-active-class="transition-opacity duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="sheetDay" class="fixed inset-0 z-40 bg-black/50" @click="sheetDay = null" />
    </Transition>

    <Transition
      enter-active-class="transition-transform duration-300 ease-out"
      enter-from-class="translate-y-full"
      enter-to-class="translate-y-0"
      leave-active-class="transition-transform duration-200 ease-in"
      leave-from-class="translate-y-0"
      leave-to-class="translate-y-full"
    >
      <div
        v-if="sheetDay"
        class="fixed bottom-0 left-0 right-0 z-50 rounded-t-2xl bg-sidebar border-t border-border shadow-2xl"
      >
        <div class="flex justify-center pt-3 pb-1">
          <div class="w-10 h-1 rounded-full bg-border" />
        </div>

        <div class="flex items-center justify-between px-4 py-3 border-b border-border">
          <div>
            <p class="font-semibold text-foreground text-sm capitalize">{{ sheetLabel }}</p>
            <p class="text-xs text-muted-foreground">{{ sheetOrdens.length }} ordem(ns) agendada(s)</p>
          </div>
          <button class="p-1.5 rounded-md hover:bg-muted text-muted-foreground transition-colors" @click="sheetDay = null">
            <X class="size-4" />
          </button>
        </div>

        <div class="flex flex-col gap-2 p-4 max-h-72 overflow-y-auto">
          <div v-if="sheetOrdens.length === 0" class="py-6 text-center text-sm text-muted-foreground">
            Nenhuma ordem agendada para este dia
          </div>
          <button
            v-for="o in sheetOrdens"
            :key="o.codigo"
            class="flex items-start gap-3 w-full text-left p-3 rounded-lg border border-border bg-background hover:bg-muted/40 transition-colors"
            @click="abrirModal(o); sheetDay = null"
          >
            <span
              class="size-2.5 rounded-full shrink-0 mt-1"
              :style="{ backgroundColor: CRIT_COLOR[o.criticidade] ?? '#64748b' }"
            />
            <div class="flex-1 min-w-0">
              <p class="font-mono font-bold text-foreground text-sm">#{{ o.codigo }}</p>
              <p class="text-xs text-muted-foreground truncate">{{ nomeCliente(o.codigoCliente) }}</p>
              <!-- Todos os técnicos da ordem -->
              <div v-if="tecnicosOrdem.get(o.codigo)?.length" class="flex flex-wrap gap-1 mt-1">
                <span
                  v-for="id in tecnicosOrdem.get(o.codigo)"
                  :key="id"
                  class="text-[10px] bg-sidebar-primary/15 text-sidebar-primary px-1.5 py-0.5 rounded font-medium"
                >
                  {{ nomeTecnico(id) }}
                </span>
              </div>
            </div>
            <span
              :class="[
                'text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wide shrink-0 mt-0.5',
                crit[o.criticidade]?.pill ?? 'bg-muted/30 text-muted-foreground border border-border',
              ]"
            >
              {{ crit[o.criticidade]?.label ?? o.criticidade }}
            </span>
            <ChevronRight class="size-4 text-muted-foreground shrink-0 mt-0.5" />
          </button>
        </div>
      </div>
    </Transition>

  </div>
</template>

<style>
/* ─── FullCalendar — variáveis do sistema (reagem ao toggle dark/light) ───── */
.fc {
  --fc-border-color:               hsl(var(--border));
  --fc-button-bg-color:            hsl(var(--sidebar-background));
  --fc-button-border-color:        hsl(var(--border));
  --fc-button-text-color:          hsl(var(--foreground));
  --fc-button-hover-bg-color:      hsl(var(--accent));
  --fc-button-hover-border-color:  hsl(var(--border));
  --fc-button-active-bg-color:     hsl(var(--sidebar-primary));
  --fc-button-active-border-color: hsl(var(--sidebar-primary));
  --fc-today-bg-color:             hsl(var(--sidebar-primary) / 0.1);
  --fc-page-bg-color:              hsl(var(--background));
  --fc-neutral-bg-color:           hsl(var(--sidebar-background));
  --fc-neutral-text-color:         hsl(var(--muted-foreground));
  font-family: inherit;
}

.fc-toolbar.fc-header-toolbar {
  padding: 12px 16px !important;
  margin-bottom: 0 !important;
  background: hsl(var(--sidebar-background));
  border-bottom: 1px solid hsl(var(--border));
}

.fc-toolbar-title {
  color: hsl(var(--foreground)) !important;
  font-size: 1rem !important;
  font-weight: 700 !important;
  text-transform: capitalize;
}

.fc-button {
  border-radius: 8px !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  padding: 5px 12px !important;
  box-shadow: none !important;
  text-transform: capitalize !important;
  transition: background 0.15s !important;
}

.fc-button-primary:not(:disabled):active,
.fc-button-primary:not(:disabled).fc-button-active {
  background: hsl(var(--sidebar-primary)) !important;
  border-color: hsl(var(--sidebar-primary)) !important;
}

.fc-button-group .fc-button:first-child { border-radius: 8px 0 0 8px !important; }
.fc-button-group .fc-button:last-child  { border-radius: 0 8px 8px 0 !important; }

.fc-col-header-cell {
  background: hsl(var(--sidebar-background)) !important;
  padding: 8px 0 !important;
}

.fc-col-header-cell-cushion {
  color: hsl(var(--muted-foreground)) !important;
  font-size: 11px !important;
  font-weight: 700 !important;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  text-decoration: none !important;
}

.fc-daygrid-day {
  background: hsl(var(--background)) !important;
  cursor: pointer;
}

.fc-daygrid-day:hover {
  background: hsl(var(--muted) / 0.4) !important;
}

.fc-daygrid-day-number {
  color: hsl(var(--foreground)) !important;
  font-size: 12px !important;
  font-weight: 500 !important;
  text-decoration: none !important;
  padding: 6px 8px !important;
}

.fc-day-today .fc-daygrid-day-number {
  background: hsl(var(--sidebar-primary));
  color: #fff !important;
  border-radius: 50%;
  width: 26px;
  height: 26px;
  display: flex !important;
  align-items: center;
  justify-content: center;
  margin: 4px !important;
  padding: 0 !important;
  font-weight: 700 !important;
}

.fc-daygrid-event-harness {
  margin: 1px 4px !important;
}

.fc-daygrid-event {
  border-radius: 6px !important;
  border: none !important;
  font-size: 11px !important;
  font-weight: 600 !important;
  padding: 2px 6px !important;
  cursor: pointer !important;
  transition: opacity 0.15s, transform 0.1s !important;
  background-image: none !important;
}

.fc-daygrid-event:hover {
  opacity: 0.82 !important;
  transform: scale(1.03) !important;
  box-shadow: 0 2px 8px rgba(0,0,0,0.25) !important;
  filter: none !important;
}

.fc-daygrid-event::after,
.fc-event::after {
  display: none !important;
}

.fc-event-title {
  text-align: center !important;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fc-daygrid-more-link {
  color: hsl(var(--muted-foreground)) !important;
  font-size: 10px !important;
  font-weight: 600 !important;
  padding: 0 4px !important;
  margin: 0 4px !important;
}
.fc-daygrid-more-link:hover {
  color: hsl(var(--foreground)) !important;
  background: transparent !important;
}

.fc-popover {
  background: hsl(var(--popover)) !important;
  border: 1px solid hsl(var(--border)) !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3) !important;
  overflow: hidden;
}
.fc-popover-header {
  background: hsl(var(--sidebar-background)) !important;
  padding: 10px 14px !important;
  border-bottom: 1px solid hsl(var(--border)) !important;
}
.fc-popover-title {
  color: hsl(var(--foreground)) !important;
  font-size: 12px !important;
  font-weight: 700 !important;
  text-transform: capitalize;
}
.fc-popover-close {
  color: hsl(var(--muted-foreground)) !important;
  font-size: 16px !important;
}
.fc-popover-body {
  padding: 8px !important;
}
</style>
