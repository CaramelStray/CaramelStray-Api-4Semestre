<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useLocalStorage } from '@vueuse/core'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useRouter } from 'vue-router'
import {
  ClipboardList, Clock, Wrench, AlertTriangle,
  CalendarDays, CalendarClock, CalendarRange,
  TrendingUp, MapPin, ChevronLeft, ChevronRight,
} from 'lucide-vue-next'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import MapWidget from '@/components/MapWidget.vue'
import { clienteService } from '@/services/clienteService'
import { contratoService } from '@/services/contratoService'
import {
  Chart, LineElement, PointElement, LineController,
  DoughnutController, ArcElement, CategoryScale, LinearScale, Filler, Tooltip,
} from 'chart.js'
import type { Chart as ChartInstance } from 'chart.js'

Chart.register(LineElement, PointElement, LineController, DoughnutController, ArcElement, CategoryScale, LinearScale, Filler, Tooltip)

// ─── Router & state ───────────────────────────────────────────────────────────
const router = useRouter()
const userName = useLocalStorage('user_name', '')
const orders        = ref<OrdemServicoResponseDTO[]>([])
const clientMap     = ref<Record<number, string>>({})
const clientCount   = ref(0)
const contractCount = ref(0)
const expiringContractsCount = ref(0)
const overdueOrdersCount     = ref(0)
const loading       = ref(false)
const errorMessage  = ref('')

const today = new Date()

// ─── Chart.js ─────────────────────────────────────────────────────────────────
let lineChartInstance: ChartInstance | null = null

const isDark    = window.matchMedia('(prefers-color-scheme: dark)').matches
const textColor = isDark ? '#9c9a92' : '#73726c'
const gridColor = isDark ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.06)'

// ─── Status helpers ───────────────────────────────────────────────────────────
const STATUS_MAP: Record<string, { bg: string; label: string; badgeCls: string }> = {
  AGUARDANDO:  { bg: '#F59E0B', label: 'Aguardando',  badgeCls: 'badge-amber'   },
  ABERTA:      { bg: '#F59E0B', label: 'Aberta',      badgeCls: 'badge-amber'   },
  AGENDADO:    { bg: '#3B82F6', label: 'Agendado',    badgeCls: 'badge-blue'    },
  AGENDADA:    { bg: '#3B82F6', label: 'Agendada',    badgeCls: 'badge-blue'    },
  EM_EXECUCAO: { bg: '#10B981', label: 'Em execução', badgeCls: 'badge-emerald' },
  CONCLUIDA:   { bg: '#8B5CF6', label: 'Finalizada',  badgeCls: 'badge-violet'  },
  FINALIZADA:  { bg: '#8B5CF6', label: 'Finalizada',  badgeCls: 'badge-violet'  },
  CANCELADA:   { bg: '#E11D48', label: 'Cancelada',   badgeCls: 'badge-red'     },
}
const statusInfo = (key?: string) =>
  STATUS_MAP[key ?? ''] ?? { bg: '#94A3B8', label: key ?? '—', badgeCls: '' }

// ─── Computed ─────────────────────────────────────────────────────────────────
const upcomingOrders = computed(() => {
  const now = new Date(today.getFullYear(), today.getMonth(), today.getDate())
  const closed = ['CONCLUIDA', 'FINALIZADA', 'CANCELADA']
  return orders.value
    .filter(o => {
      if (!o.dataAgendamento) return false
      const orderDate = new Date(o.dataAgendamento)
      // Inclui ordens futuras OU ordens que estão no passado mas ainda não foram fechadas (atrasadas)
      return orderDate >= now || !closed.includes(o.status ?? '')
    })
    .sort((a, b) => new Date(a.dataAgendamento!).getTime() - new Date(b.dataAgendamento!).getTime())
    .slice(0, 6)
})

const chartEndDate = ref(new Date().toISOString().slice(0, 10))

const ordersByDayAndStatus = computed(() => {
  const points = []
  const end = new Date(chartEndDate.value + 'T12:00:00') // evita fuso
  for (let i = 6; i >= 0; i--) {
    const d = new Date(end)
    d.setDate(end.getDate() - i)
    const key = d.toISOString().slice(0, 10)
    const dayOrders = orders.value.filter(o => o.dataAgendamento?.slice(0, 10) === key)
    points.push({
      label: d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }),
      key,
      aguardando: dayOrders.filter(o => o.status === 'AGUARDANDO' || o.status === 'ABERTA').length,
      agendado: dayOrders.filter(o => o.status === 'AGENDADO' || o.status === 'AGENDADA').length,
      emExecucao: dayOrders.filter(o => o.status === 'EM_EXECUCAO').length,
      finalizadas: dayOrders.filter(o => o.status === 'FINALIZADA' || o.status === 'CONCLUIDA').length,
    })
  }
  return points
})

const calendarDate = ref(new Date())

const statusSummary = computed(() => {
  const s = orders.value.reduce((acc, o) => {
    if (!o.status) return acc
    acc[o.status] = (acc[o.status] ?? 0) + 1
    return acc
  }, {} as Record<string, number>)
  return {
    aguardando:  (s.AGUARDANDO ?? 0) + (s.ABERTA ?? 0),
    agendado:    (s.AGENDADO ?? 0) + (s.AGENDADA ?? 0),
    emExecucao:  s.EM_EXECUCAO ?? 0,
    finalizadas: (s.CONCLUIDA ?? 0) + (s.FINALIZADA ?? 0),
  }
})

const calendarEventDays = computed(() => {
  const set = new Set<string>()
  orders.value.forEach(o => { if (o.dataAgendamento) set.add(o.dataAgendamento.slice(0, 10)) })
  return set
})

const calendarDays = computed(() => {
  const yr = calendarDate.value.getFullYear(), mo = calendarDate.value.getMonth()
  const firstWeekDay = new Date(yr, mo, 1).getDay()
  const daysInMonth  = new Date(yr, mo + 1, 0).getDate()
  const cells: Array<{ day: number | null; key: string; isToday: boolean; hasEvent: boolean }> = []
  for (let i = 0; i < firstWeekDay; i++) cells.push({ day: null, key: '', isToday: false, hasEvent: false })
  for (let d = 1; d <= daysInMonth; d++) {
    const key = `${yr}-${String(mo + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const isToday = d === today.getDate() && mo === today.getMonth() && yr === today.getFullYear()
    cells.push({ day: d, key, isToday, hasEvent: calendarEventDays.value.has(key) })
  }
  return cells
})

const calMonthLabel = computed(() => {
  const names = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro']
  return `${names[calendarDate.value.getMonth()]} de ${calendarDate.value.getFullYear()}`
})

function prevMonth() {
  calendarDate.value = new Date(calendarDate.value.getFullYear(), calendarDate.value.getMonth() - 1, 1)
}
function nextMonth() {
  calendarDate.value = new Date(calendarDate.value.getFullYear(), calendarDate.value.getMonth() + 1, 1)
}
function resetMonth() {
  calendarDate.value = new Date()
}

const greetingLabel = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Bom dia'
  if (h < 18) return 'Boa tarde'
  return 'Boa noite'
})

// ─── Formatters ───────────────────────────────────────────────────────────────
const formatDate = (d?: string) =>
  d ? new Date(d).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }) : '—'

const formatTipo = (t?: string) => {
  if (t === 'INSTALACAO')            return 'Instalação'
  if (t === 'MANUTENCAO_PREVENTIVA') return 'Preventiva'
  if (t === 'MANUTENCAO_CORRETIVA')  return 'Corretiva'
  return t ?? 'Ordem'
}

const todayFormatted = computed(() =>
  today.toLocaleDateString('pt-BR', { weekday: 'long', day: '2-digit', month: 'long', year: 'numeric' })
)

// ─── Modal Calendário ────────────────────────────────────────────────────────
const selectedDateKey = ref('')
const selectedDateOrders = ref<OrdemServicoResponseDTO[] | null>(null)

function openDate(key: string) {
  if (!key) return
  selectedDateKey.value = key
  selectedDateOrders.value = orders.value.filter(o => o.dataAgendamento?.slice(0, 10) === key)
}
const formatDateString = (k: string) => {
  if (!k) return ''
  const [y,m,d] = k.split('-')
  return `${d}/${m}/${y}`
}

// ─── Chart ────────────────────────────────────────────────────────────────────
function buildStatusChart() {
  const canvas = document.getElementById('statusChartCanvas') as HTMLCanvasElement
  if (!canvas) return
  if (lineChartInstance) lineChartInstance.destroy()

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const makeGradient = (colorRGB: string) => {
    const grad = ctx.createLinearGradient(0, 0, 0, canvas.height)
    grad.addColorStop(0, `rgba(${colorRGB}, ${isDark ? '0.45' : '0.35'})`)
    grad.addColorStop(1, `rgba(${colorRGB}, 0)`)
    return grad
  }

  const gradAguardando = makeGradient('245, 158, 11')
  const gradAgendado = makeGradient('59, 130, 246')
  const gradExecucao = makeGradient('16, 185, 129')
  const gradFinalizada = makeGradient('139, 92, 246')

  lineChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: ordersByDayAndStatus.value.map(p => p.label),
      datasets: [
        {
          label: 'Aguardando',
          data: ordersByDayAndStatus.value.map(p => p.aguardando),
          borderColor: '#F59E0B',
          backgroundColor: gradAguardando,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          tension: 0.4,
          fill: true,
        },
        {
          label: 'Agendado',
          data: ordersByDayAndStatus.value.map(p => p.agendado),
          borderColor: '#3B82F6',
          backgroundColor: gradAgendado,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          tension: 0.4,
          fill: true,
        },
        {
          label: 'Em Execução',
          data: ordersByDayAndStatus.value.map(p => p.emExecucao),
          borderColor: '#10B981',
          backgroundColor: gradExecucao,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          tension: 0.4,
          fill: true,
        },
        {
          label: 'Finalizadas',
          data: ordersByDayAndStatus.value.map(p => p.finalizadas),
          borderColor: '#8B5CF6',
          backgroundColor: gradFinalizada,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          tension: 0.4,
          fill: true,
        }
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false,
      },
      plugins: {
        legend: {
          position: 'top',
          align: 'end',
          labels: { color: textColor, font: { size: 10, family: 'Inter, sans-serif' }, boxWidth: 8, usePointStyle: true, pointStyle: 'circle' }
        },
        tooltip: {
          backgroundColor: isDark ? '#1e1b4b' : '#fff',
          borderColor: isDark ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.08)',
          borderWidth: 1,
          titleColor: textColor,
          bodyColor: textColor,
          padding: 10,
        },
      },
      scales: {
        x: { ticks: { color: textColor, font: { size: 10 } }, grid: { color: isDark ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)' } },
        y: { ticks: { color: textColor, font: { size: 10 }, precision: 0, stepSize: 1 }, grid: { color: isDark ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)' }, min: 0 },
      },
    },
  })
}

watch([orders, chartEndDate], () => { buildStatusChart() })

// ─── Load ─────────────────────────────────────────────────────────────────────
const loadDashboard = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const [allOrders, clients, contracts] = await Promise.all([
        ordemServicoService.listar(),
      clienteService.listar(),
      contratoService.listar(),
    ])

    orders.value        = allOrders
    clientCount.value   = clients.length
    contractCount.value = contracts.length
    clientMap.value     = Object.fromEntries(clients.map(c => [c.id, c.nomeEmpresa]))

    const now    = new Date(today.getFullYear(), today.getMonth(), today.getDate())
    const next30 = new Date(now); next30.setDate(now.getDate() + 30)

    expiringContractsCount.value = contracts.filter(c => {
      if (!c.dataFim) return false
      const d = new Date(c.dataFim)
      return d >= now && d <= next30
    }).length

    overdueOrdersCount.value = allOrders.filter(o => {
      if (!o.dataAgendamento) return false
      const closed = ['CONCLUIDA', 'FINALIZADA', 'CANCELADA']
      return !closed.includes(o.status ?? '') && new Date(o.dataAgendamento) < now
    }).length
  } catch (error: any) {
    errorMessage.value = error?.message ?? 'Erro ao buscar dados do dashboard.'
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<template>
  <div class="dash-root">

    <!-- ── Hero / Welcome Banner ──────────────────────────────────────────── -->
    <div class="hero-banner">
      <div class="hero-glow hero-glow-1" />
      <div class="hero-glow hero-glow-2" />
      <div class="hero-glow hero-glow-3" />
      <div class="hero-content">
        <div class="hero-left">
          <p class="hero-eyebrow">Bem-vindo de volta, {{ userName || 'Administrador' }} 👋</p>
          <h1 class="hero-title">Painel de Controle</h1>
          <p class="hero-sub">{{ todayFormatted }}</p>
        </div>
        <Button class="hero-btn" @click="router.push('/ordens')">
          <CalendarRange class="w-4 h-4" />
          Ver ordens
        </Button>
      </div>
    </div>

    <!-- ── KPI Cards ──────────────────────────────────────────────────────── -->
    <div class="kpi-row">
      <div class="kpi kpi-blue">
        <div class="kpi-icon"><ClipboardList class="w-5 h-5" /></div>
        <div class="kpi-val">{{ orders.length }}</div>
        <div class="kpi-label">Total de ordens</div>
        <div class="kpi-sub">cadastradas no sistema</div>
      </div>
      <div class="kpi kpi-amber">
        <div class="kpi-icon"><Clock class="w-5 h-5" /></div>
        <div class="kpi-val">{{ statusSummary.aguardando }}</div>
        <div class="kpi-label">Aguardando</div>
        <div class="kpi-sub">pendentes de agendamento</div>
      </div>
      <div class="kpi kpi-emerald">
        <div class="kpi-icon"><Wrench class="w-5 h-5" /></div>
        <div class="kpi-val">{{ statusSummary.emExecucao }}</div>
        <div class="kpi-label">Em execução</div>
        <div class="kpi-sub">em andamento agora</div>
      </div>
      <div class="kpi kpi-rose">
        <div class="kpi-icon"><AlertTriangle class="w-5 h-5" /></div>
        <div class="kpi-val">{{ overdueOrdersCount }}</div>
        <div class="kpi-label">Atrasadas</div>
        <div class="kpi-sub">necessitam atenção</div>
      </div>
    </div>

    <!-- ── Row 1: Gráfico + Mapa ─────────────────────────────────────────── -->
    <div class="two-col-row">

      <!-- Gráfico de Status -->
      <div class="panel">
        <div class="panel-header">
          <span class="panel-title"><TrendingUp class="w-4 h-4" />Status ao Longo do Tempo</span>
          <div class="flex items-center gap-2">
            <span class="panel-meta">até</span>
            <input type="date" v-model="chartEndDate" class="text-[11px] bg-muted border border-border rounded px-2 py-1 text-foreground focus:outline-none focus:ring-1 focus:ring-blue-500" />
          </div>
        </div>
        <div class="panel-body">
          <div class="chart-wrap">
            <canvas id="statusChartCanvas" role="img" aria-label="Gráfico de linha dos status das ordens">Sem dados.</canvas>
          </div>
        </div>
      </div>

      <!-- Mapa placeholder -->
      <div class="panel">
        <div class="panel-header">
          <span class="panel-title"><MapPin class="w-4 h-4" />Mapa de Cobertura</span>
          <span class="panel-meta">Distribuição Geográfica</span>
        </div>
        <div class="panel-body map-body" style="padding: 0;">
          <MapWidget />
        </div>
      </div>

    </div>

    <!-- ── Row 2: Próximas Ordens + Calendário ────────────────────────────── -->
    <div class="two-col-row row-compact">

      <!-- Próximas ordens -->
      <div class="panel">
        <div class="panel-header">
          <span class="panel-title"><CalendarClock class="w-4 h-4" />Ordens Pendentes</span>
          <Badge class="badge-pill">{{ upcomingOrders.length }}</Badge>
        </div>
        <div class="panel-body p-0">
          <div v-if="!upcomingOrders.length" class="empty-state m-3">Nenhuma ordem agendada</div>
          <div v-else class="overflow-x-auto pb-1">
            <table class="w-full text-left border-collapse">
              <thead>
                <tr class="border-b border-border text-[10px] uppercase tracking-wider text-muted-foreground bg-muted/10">
                  <th class="py-2.5 px-4 font-semibold">Ordem / Tipo</th>
                  <th class="py-2.5 px-4 font-semibold">Cliente</th>
                  <th class="py-2.5 px-4 font-semibold text-right">Data</th>
                  <th class="py-2.5 px-4 font-semibold text-right">Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="ordem in upcomingOrders" :key="ordem.codigo" class="border-b border-border/40 last:border-0 hover:bg-muted/20 transition-colors">
                  <td class="py-2.5 px-4">
                    <div class="flex items-center gap-2">
                      <span class="w-2 h-2 rounded-full shrink-0" :style="{ background: statusInfo(ordem.status).bg }" />
                      <div class="font-medium text-[12px] text-foreground">#{{ ordem.codigo }} <span class="text-[11px] font-normal text-muted-foreground">· {{ formatTipo(ordem.tipoOrdem) }}</span></div>
                    </div>
                  </td>
                  <td class="py-2.5 px-4 text-[11px] text-muted-foreground truncate max-w-[120px]">
                    {{ clientMap[ordem.codigoCliente] ?? 'Cliente não definido' }}
                  </td>
                  <td class="py-2.5 px-4 text-[11px] text-muted-foreground text-right whitespace-nowrap">
                    {{ formatDate(ordem.dataAgendamento) }}
                  </td>
                  <td class="py-2.5 px-4 text-right">
                    <span :class="['badge', statusInfo(ordem.status).badgeCls]">
                      {{ statusInfo(ordem.status).label }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Calendário -->
      <div class="panel relative overflow-hidden">
        <div class="panel-header">
          <span class="panel-title"><CalendarDays class="w-4 h-4" />Calendário</span>
          <div class="header-pills">
            <Button variant="outline" size="icon" class="h-6 w-6" @click="prevMonth"><ChevronLeft class="w-3 h-3" /></Button>
            <Button variant="outline" size="sm" class="h-6 text-[10px] px-2" @click="resetMonth">Hoje</Button>
            <Button variant="outline" size="icon" class="h-6 w-6" @click="nextMonth"><ChevronRight class="w-3 h-3" /></Button>
          </div>
        </div>
        <div class="panel-body">
          <div class="cal-month-label">{{ calMonthLabel }}</div>
          <div class="cal-grid">
            <div v-for="name in ['D','S','T','Q','Q','S','S']" :key="name" class="cal-day-name">{{ name }}</div>
            <template v-for="cell in calendarDays" :key="cell.key || Math.random()">
              <div
                v-if="cell.day"
                @click="openDate(cell.key)"
                :class="['cal-day cursor-pointer hover:opacity-80 transition-opacity', { 'cal-today': cell.isToday, 'cal-event': cell.hasEvent && !cell.isToday }]"
              >{{ cell.day }}</div>
              <div v-else class="cal-day cal-empty" />
            </template>
          </div>
          <div class="cal-legend">
            <span class="cal-legend-item"><span class="cal-legend-dot cal-today" />Hoje</span>
            <span class="cal-legend-item"><span class="cal-legend-dot cal-event" />Com ordens</span>
          </div>
        </div>

        <!-- Popup de Ordens do Dia (Sobreposto ao calendário) -->
        <transition name="fade">
          <div v-if="selectedDateOrders" class="absolute inset-0 z-20 flex flex-col bg-popover">
            <div class="flex items-center justify-between p-3 border-b border-border">
              <h3 class="font-semibold text-[13px] text-foreground">Ordens agendadas - {{ formatDateString(selectedDateKey) }}</h3>
              <Button variant="ghost" size="icon" class="h-6 w-6" @click="selectedDateOrders = null"><span class="text-lg leading-none">&times;</span></Button>
            </div>
            <div class="p-3 overflow-y-auto flex-1">
              <div v-if="!selectedDateOrders.length" class="text-xs text-muted-foreground text-center py-4">
                Nenhuma ordem agendada para este dia.
              </div>
              <div v-else class="space-y-2">
                 <div v-for="ordem in selectedDateOrders" :key="ordem.codigo" class="border border-border rounded-lg p-2.5 text-xs bg-muted/10 hover:bg-muted/30 transition-colors">
                    <div class="flex justify-between items-start mb-1">
                      <strong class="text-foreground text-[12px]">#{{ ordem.codigo }} · {{ formatTipo(ordem.tipoOrdem) }}</strong>
                      <span :class="['badge m-0', statusInfo(ordem.status).badgeCls]">{{ statusInfo(ordem.status).label }}</span>
                    </div>
                    <div class="text-muted-foreground text-[11px] flex items-center justify-between">
                      <span class="truncate pr-2">{{ clientMap[ordem.codigoCliente] ?? 'Cliente não definido' }}</span>
                      <span v-if="ordem.dataAgendamento && ordem.dataAgendamento.includes('T')" class="text-[10px] opacity-70 whitespace-nowrap">
                        {{ new Date(ordem.dataAgendamento).toLocaleTimeString('pt-BR', {hour: '2-digit', minute:'2-digit'}) }}
                      </span>
                    </div>
                 </div>
              </div>
            </div>
          </div>
        </transition>

      </div>

    </div>

    <!-- States -->
    <div v-if="loading"       class="state-msg">Carregando dados do dashboard...</div>
    <div v-if="errorMessage"  class="state-msg state-error">{{ errorMessage }}</div>

  </div>
</template>

<style scoped>
/* ─── Root ──────────────────────────────────────────────────────────────────── */
.dash-root {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px 24px;
  min-height: 100vh;
  box-sizing: border-box;
  background: hsl(var(--background));
  line-height: 1.6; /* aumentar espaçamento entre linhas */
}

/* ─── Hero Banner ───────────────────────────────────────────────────────────── */
.hero-banner {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  padding: 28px 32px;
  background: linear-gradient(135deg, rgba(37,99,235,0.95) 0%, rgba(59,130,246,0.85) 50%, rgba(14,165,233,0.7) 100%);
  isolation: isolate;
}

.hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(56px);
  pointer-events: none;
}
.hero-glow-1 { width: 240px; height: 240px; background: #60A5FA; top: -60px; left: -40px; opacity: 0.3 }
.hero-glow-2 { width: 180px; height: 180px; background: #38BDF8; bottom: -40px; right: 60px; opacity: 0.25 }
.hero-glow-3 { width: 140px; height: 140px; background: #93C5FD; top: 10px; right: 160px; opacity: 0.2 }

.hero-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.hero-left { display: flex; flex-direction: column; gap: 4px; }

.hero-eyebrow {
  font-size: 13px;
  color: rgba(255,255,255,0.75);
  margin: 0;
  font-weight: 400;
}
.hero-title {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  line-height: 1.2;
}
.hero-sub {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
  margin: 0;
  text-transform: capitalize;
}

.hero-btn {
  background: rgba(255,255,255,0.15);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.3);
  backdrop-filter: blur(8px);
  font-size: 13px;
  font-weight: 500;
  padding: 0 18px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 7px;
  flex-shrink: 0;
  transition: background 0.2s;
}
.hero-btn:hover { background: rgba(255,255,255,0.25); }

/* ─── KPI Row ───────────────────────────────────────────────────────────────── */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}
.kpi {
  border-radius: 16px;
  padding: 18px 20px;
  border-width: 1px;
  border-style: solid;
  /* usar o mesmo fundo dos cards das outras páginas */
  background: hsl(var(--card));
  border-color: hsl(var(--border));
}
.kpi-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  background: transparent;
}
.kpi-val   { font-size: 28px; font-weight: 700; line-height: 1; color: hsl(var(--foreground)); }
.kpi-label { font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.1em; margin-top: 4px; color: hsl(var(--muted-foreground)); }
.kpi-sub   { font-size: 11px; margin-top: 3px; color: hsl(var(--muted-foreground)); }

.kpi-blue    .kpi-icon  { color: #3b82f6; }
.kpi-amber   .kpi-icon  { color: #f59e0b; }
.kpi-emerald .kpi-icon  { color: #10b981; }
.kpi-rose    .kpi-icon  { color: #f43f5e; }

@media (prefers-color-scheme: dark) {
  .kpi-blue    .kpi-icon  { color: #60a5fa; }
  .kpi-amber   .kpi-icon  { color: #fbbf24; }
  .kpi-emerald .kpi-icon  { color: #34d399; }
  .kpi-rose    .kpi-icon  { color: #fb7185; }
}

/* ─── Two-col rows ──────────────────────────────────────────────────────────── */
.two-col-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

/* ─── Panels ────────────────────────────────────────────────────────────────── */
.panel {
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid hsl(var(--border));
  flex-shrink: 0;
  gap: 8px;
}
.panel-title {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 14px;
  font-weight: 600;
  color: hsl(var(--foreground));
  flex-shrink: 0;
}
.panel-title svg { color: hsl(var(--muted-foreground)); flex-shrink: 0; }
.panel-meta { font-size: 11px; color: hsl(var(--muted-foreground)); }

.panel-body {
  padding: 16px 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ─── Header pills ──────────────────────────────────────────────────────────── */
.header-pills { display: flex; gap: 6px; }
.hpill {
  font-size: 11px;
  padding: 3px 9px;
  border-radius: 20px;
  background: hsl(var(--muted) / 0.6);
  color: hsl(var(--muted-foreground));
  display: flex;
  align-items: center;
  gap: 4px;
}
.hpill strong { color: hsl(var(--foreground)); font-weight: 600; }
.hpill-rose strong { color: #E11D48; }

/* ─── Line chart ────────────────────────────────────────────────────────────── */
.chart-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 300px;
  flex: 1;
}

/* Linha compacta: próximas ordens maior, calendário menor */
.row-compact { grid-template-columns: 1.4fr 0.6fr; }
.row-compact .cal-day { aspect-ratio: 0.85; }
.row-compact .panel-body { padding: 12px 14px; }

.order-item { padding: 12px 0; }

/* ─── Map placeholder ───────────────────────────────────────────────────────── */
.map-body { justify-content: center; align-items: center; min-height: 200px; }
.map-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  opacity: 0.35;
}
.map-icon { color: hsl(var(--muted-foreground)); }
.map-label { font-size: 13px; color: hsl(var(--muted-foreground)); }

/* Orders map */
.orders-map { width: 100%; height: 220px; border-radius: 8px; }

/* ─── Order items ───────────────────────────────────────────────────────────── */
.order-item { display: flex; align-items: flex-start; gap: 10px; padding: 10px 0; border-bottom: 1px solid hsl(var(--border)); }
.order-item:last-child { border-bottom: none; }
.order-dot  { width: 8px; height: 8px; border-radius: 50%; margin-top: 5px; flex-shrink: 0; }
.order-body { flex: 1; min-width: 0; }
.order-code   { font-size: 13px; font-weight: 500; color: hsl(var(--foreground)); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.order-client { font-size: 12px; color: hsl(var(--muted-foreground)); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.order-right  { text-align: right; flex-shrink: 0; }
.order-date   { font-size: 11px; color: hsl(var(--muted-foreground)); }

/* ─── Badges ────────────────────────────────────────────────────────────────── */
.badge-pill {
  background: #EDE9FE;
  color: #4C1D95;
  font-size: 11px;
  padding: 2px 9px;
  border-radius: 20px;
  font-weight: 600;
}
@media (prefers-color-scheme: dark) { .badge-pill { background: #2E1065; color: #C4B5FD; } }

.badge {
  font-size: 10px;
  font-weight: 500;
  padding: 2px 7px;
  border-radius: 6px;
  display: inline-block;
  margin-top: 3px;
}
.badge-amber   { background: #FEF3C7; color: #92400E; }
.badge-blue    { background: #DBEAFE; color: #1E40AF; }
.badge-emerald { background: #DCFCE7; color: #166534; }
.badge-violet  { background: #EDE9FE; color: #4C1D95; }
.badge-red     { background: #FFE4E6; color: #9F1239; }

@media (prefers-color-scheme: dark) {
  .badge-amber   { background: #451A03; color: #FCD34D; }
  .badge-blue    { background: #1E3A8A; color: #93C5FD; }
  .badge-emerald { background: #14532D; color: #86EFAC; }
  .badge-violet  { background: #2E1065; color: #C4B5FD; }
  .badge-red     { background: #4C0519; color: #FDA4AF; }
}

/* ─── Calendar ──────────────────────────────────────────────────────────────── */
.cal-month-label { font-size: 13px; font-weight: 600; text-align: center; color: hsl(var(--foreground)); }
.cal-grid        { display: grid; grid-template-columns: repeat(7, 1fr); gap: 3px; }
.cal-day-name    { text-align: center; font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; color: hsl(var(--muted-foreground)); padding: 4px 0; }
.cal-day         { aspect-ratio: 1; display: flex; align-items: center; justify-content: center; border-radius: 6px; font-size: 12px; color: hsl(var(--muted-foreground)); }
.cal-empty       { opacity: 0; }
.cal-event       { background: #FEF3C7; color: #92400E; font-weight: 500; }
.cal-today       { background: #3B82F6; color: #fff; font-weight: 700; }
@media (prefers-color-scheme: dark) {
  .cal-event { background: #78350F; color: #FDE68A; }
}
.cal-legend      { display: flex; gap: 14px; margin-top: 10px; justify-content: center; }
.cal-legend-item { display: flex; align-items: center; gap: 5px; font-size: 11px; color: hsl(var(--muted-foreground)); }
.cal-legend-dot  { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }

/* ─── States ────────────────────────────────────────────────────────────────── */
.empty-state {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
  text-align: center;
  padding: 20px;
  border: 1px dashed hsl(var(--border));
  border-radius: 10px;
}
.state-msg {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
  text-align: center;
  padding: 12px;
  border-radius: 10px;
  border: 1px solid hsl(var(--border));
}
.state-error { color: #E11D48; border-color: #FECDD3; background: #FFF1F2; }

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>