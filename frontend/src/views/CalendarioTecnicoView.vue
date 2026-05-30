<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { TecnicoOrdemDetalhesResponseDTO } from '@/services/ordemServicoService'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { CalendarDays, ChevronLeft, ChevronRight, Loader2, X } from 'lucide-vue-next'
import { ordemServicoService, type TecnicosOrdensResponseDTO} from '@/services/ordemServicoService'

const router = useRouter()
const ordens = ref<TecnicosOrdensResponseDTO[]>([])
const loading = ref(true)

const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth())

// Bottom-sheet (mobile)
const showModal = ref(false)

const ordemSelecionada = ref<TecnicoOrdemDetalhesResponseDTO | null>(null)

const loadingModal = ref(false)

const MONTH_NAMES = [
  'Janeiro','Fevereiro','Março','Abril','Maio','Junho',
  'Julho','Agosto','Setembro','Outubro','Novembro','Dezembro',
]
const WEEKDAYS_LONG  = ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado']
const WEEKDAYS_SHORT = ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb']

const crit: Record<string, { pill: string; dot: string; label: string }> = {
  CRITICA: { pill: 'bg-red-500/20 text-red-300 border border-red-500/40',       dot: 'bg-red-500',    label: 'Crítica' },
  ALTA:    { pill: 'bg-orange-500/20 text-orange-300 border border-orange-500/40', dot: 'bg-orange-500', label: 'Alta'    },
  MEDIA:   { pill: 'bg-amber-500/20 text-amber-300 border border-amber-500/40',  dot: 'bg-amber-500',  label: 'Média'   },
  BAIXA:   { pill: 'bg-blue-500/20 text-blue-300 border border-blue-500/40',    dot: 'bg-blue-500',   label: 'Baixa'   },
}

const byDate = computed(() => {
  const m = new Map<string, TecnicosOrdensResponseDTO[]>()
  for (const o of ordens.value) {
    if (!o.dataAgendamento) continue
    const k = o.dataAgendamento.slice(0, 10)
    if (!m.has(k)) m.set(k, [])
    m.get(k)!.push(o)
  }
  return m
})

type CalCell = { date: Date; key: string } | null
const weeks = computed((): CalCell[][] => {
  const y = currentYear.value
  const mo = currentMonth.value
  const firstDow = new Date(y, mo, 1).getDay()
  const total = new Date(y, mo + 1, 0).getDate()
  const cells: CalCell[] = []
  for (let i = 0; i < firstDow; i++) cells.push(null)
  for (let d = 1; d <= total; d++) {
    const date = new Date(y, mo, d)
    const key = `${y}-${String(mo + 1).padStart(2,'0')}-${String(d).padStart(2,'0')}`
    cells.push({ date, key })
  }
  while (cells.length % 7) cells.push(null)
  const rows: CalCell[][] = []
  for (let i = 0; i < cells.length; i += 7) rows.push(cells.slice(i, i + 7))
  return rows
})

function isToday(d: Date) {
  return d.getFullYear() === now.getFullYear()
      && d.getMonth()    === now.getMonth()
      && d.getDate()     === now.getDate()
}

function prevMonth() {
  if (currentMonth.value === 0) { currentMonth.value = 11; currentYear.value-- }
  else currentMonth.value--
}
function nextMonth() {
  if (currentMonth.value === 11) { currentMonth.value = 0; currentYear.value++ }
  else currentMonth.value++
}
function goToday() {
  currentMonth.value = now.getMonth()
  currentYear.value  = now.getFullYear()
}

const sheetOrdens = computed(() => sheetDay.value ? (byDate.value.get(sheetDay.value) ?? []) : [])
const sheetLabel  = computed(() => {
  if (!sheetDay.value) return ''
  const [y, m, d] = sheetDay.value.split('-').map(Number)
  return new Date(y, m - 1, d).toLocaleDateString('pt-BR', {
    weekday: 'long', day: '2-digit', month: 'long',
  })
})

async function abrirDetalhesOrdem(codigo: number) {
  try {
    loadingModal.value = true
    showModal.value = true

    ordemSelecionada.value =
      await ordemServicoService.tecnicoOrdemPorId(codigo)
  } finally {
    loadingModal.value = false
  }
}

onMounted(async () => {
  try { ordens.value = await ordemServicoService.tecnicosOrdens() }
  finally { loading.value = false }
})
</script>

<template>
  <div class="p-3 sm:p-6 space-y-4">

    <!-- Header -->
    <div class="flex items-center gap-3">
      <div class="flex items-center justify-center w-9 h-9 rounded-lg border border-blue-500 bg-blue-500/20 text-blue-400 shrink-0">
        <CalendarDays class="w-4 h-4 sm:w-5 sm:h-5" />
      </div>
      <div>
        <h1 class="text-lg sm:text-xl font-bold text-foreground">Calendário</h1>
        <p class="text-xs sm:text-sm text-muted-foreground">Suas ordens agendadas</p>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-24 gap-2 text-muted-foreground">
      <Loader2 class="size-5 animate-spin" />
      <span class="text-sm">Carregando...</span>
    </div>

    <template v-else>

      <!-- Toolbar: prev / título / next / hoje -->
      <div class="flex items-center gap-2">
        <button
          class="p-1.5 rounded-md hover:bg-muted transition-colors text-muted-foreground hover:text-foreground"
          @click="prevMonth"
        >
          <ChevronLeft class="size-4" />
        </button>
        <button
          class="p-1.5 rounded-md hover:bg-muted transition-colors text-muted-foreground hover:text-foreground"
          @click="nextMonth"
        >
          <ChevronRight class="size-4" />
        </button>
        <span class="font-semibold text-foreground text-sm sm:text-base min-w-[160px]">
          {{ MONTH_NAMES[currentMonth] }} {{ currentYear }}
        </span>
        <Button variant="outline" size="sm" class="h-7 px-3 text-xs ml-auto" @click="goToday">
          Hoje
        </Button>
      </div>

      <!-- Legenda -->
      <div class="flex flex-wrap gap-1.5">
        <span
          v-for="(c, k) in crit" :key="k"
          :class="['inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wide', c.pill]"
        >
          <span :class="['size-1.5 rounded-full', c.dot]" />{{ k }}
        </span>
      </div>

      <!-- Calendar table -->
      <div class="rounded-xl border border-border overflow-hidden">
        <table class="w-full border-collapse table-fixed">
          <thead>
            <tr class="bg-sidebar border-b border-border">
              <th
                v-for="(d, i) in WEEKDAYS_LONG" :key="i"
                class="py-2 text-center font-semibold text-muted-foreground text-[10px] sm:text-xs uppercase tracking-wide border-r border-border last:border-r-0"
              >
                <span class="hidden sm:inline">{{ d }}</span>
                <span class="sm:hidden">{{ WEEKDAYS_SHORT[i] }}</span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(week, wi) in weeks" :key="wi">
              <td
                v-for="(cell, di) in week" :key="di"
                class="border-r border-b border-border last:border-r-0 align-top"
                :class="[
                  cell ? 'bg-background' : 'bg-sidebar/40',
                  cell && byDate.get(cell.key)?.length ? 'cursor-pointer sm:cursor-default' : '',
                ]"
                style="width: 14.2857%"
                @click.stop="cell && byDate.get(cell.key)?.length ? (sheetDay = cell.key) : null"
              >
                <!-- Empty padding cell -->
                <template v-if="!cell">
                  <div class="h-16 sm:h-28" />
                </template>

                <!-- Day cell -->
                <template v-else>
                  <div class="p-1 sm:p-1.5 h-16 sm:h-28 flex flex-col overflow-hidden">

                    <!-- Day number -->
                    <div class="mb-1 shrink-0">
                      <span
                        :class="[
                          'inline-flex items-center justify-center w-6 h-6 sm:w-7 sm:h-7 text-xs sm:text-sm font-medium rounded-full',
                          isToday(cell.date)
                            ? 'bg-blue-600 text-white font-bold'
                            : 'text-foreground',
                        ]"
                      >
                        {{ cell.date.getDate() }}
                      </span>
                    </div>

                    <!-- DESKTOP: event pills -->
                    <div class="hidden sm:flex flex-col gap-0.5 overflow-hidden flex-1">
                      <button
                        v-for="o in (byDate.get(cell.key) ?? []).slice(0, 3)"
                        :key="o.codigo"
                        :class="['w-full text-left text-[10px] font-semibold px-1.5 py-0.5 rounded truncate transition-opacity hover:opacity-75', crit[o.criticidade]?.pill ?? 'bg-muted/30 text-muted-foreground border border-border']"
                        @click.stop="abrirDetalhesOrdem(o.codigo)"
                      >
                        #{{ o.codigo }} {{ o.nomeCliente ? '· ' + o.nomeCliente : '' }}
                      </button>
                      <span
                        v-if="(byDate.get(cell.key) ?? []).length > 3"
                        class="text-[9px] text-muted-foreground pl-1 pt-0.5"
                      >
                        +{{ (byDate.get(cell.key) ?? []).length - 3 }} mais
                      </span>
                    </div>

                    <!-- MOBILE: colored dots -->
                    <div class="sm:hidden flex flex-wrap gap-0.5 mt-0.5">
                      <span
                        v-for="o in (byDate.get(cell.key) ?? []).slice(0, 5)"
                        :key="o.codigo"
                        :class="['size-1.5 rounded-full shrink-0', crit[o.criticidade]?.dot ?? 'bg-slate-400']"
                      />
                      <span
                        v-if="(byDate.get(cell.key) ?? []).length > 5"
                        class="text-[8px] leading-none text-muted-foreground"
                      >+{{ (byDate.get(cell.key) ?? []).length - 5 }}</span>
                    </div>

                  </div>
                </template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </template>

    <!-- Modal detalhes -->
<Transition
  enter-active-class="transition-opacity duration-200"
  enter-from-class="opacity-0"
  enter-to-class="opacity-100"
  leave-active-class="transition-opacity duration-150"
  leave-from-class="opacity-100"
  leave-to-class="opacity-0"
>
  <div
    v-if="showModal"
    class="fixed inset-0 z-50 bg-black/60 flex items-center justify-center p-4"
    @click="showModal = false"
  >
    <div
      class="w-full max-w-sm rounded-2xl border border-border bg-sidebar p-5 shadow-2xl"
      @click.stop
    >
      <div class="flex items-start justify-between mb-4">
        <div>
          <h2 class="text-lg font-bold text-foreground">
            Detalhe da ordem
          </h2>
        </div>

        <button
          class="p-1 rounded-md hover:bg-muted transition-colors"
          @click="showModal = false"
        >
          <X class="size-4 text-muted-foreground" />
        </button>
      </div>

      <div v-if="loadingModal" class="py-10 flex justify-center">
        <Loader2 class="size-5 animate-spin text-muted-foreground" />
      </div>

      <div
        v-else-if="ordemSelecionada"
        class="space-y-3 text-sm"
      >
        <div
          class="inline-flex items-center rounded-full bg-blue-500/20 text-blue-300 px-3 py-1 text-xs font-semibold"
        >
          {{ ordemSelecionada.nomeCliente }}
        </div>

        <div class="space-y-2 text-muted-foreground">
          <p>
            <span class="font-bold text-foreground">
              #OM-{{ ordemSelecionada.codigo }}
            </span>
            · {{ ordemSelecionada.tipoOrdem ?? 'Ordem de serviço' }}
          </p>

          <p>
            {{ ordemSelecionada.cidadeCliente ?? '—' }}
          </p>

          <p>
            {{ ordemSelecionada.dataAgendamento }}
          </p>

          <!-- conflito mockado -->
          <p class="text-red-400 font-medium">
            Conflito com OM-2039 no mesmo período
          </p>
        </div>

        <Button
          class="w-full mt-3"
          variant="outline"
          @click="router.push(`/minhas-ordens/${ordemSelecionada.codigo}`)"
        >
          Ver ordem completa
        </Button>
      </div>
    </div>
  </div>
</Transition>

    <!-- ───── Bottom sheet overlay ───── -->
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
        <!-- Pull handle -->
        <div class="flex justify-center pt-3 pb-1">
          <div class="w-10 h-1 rounded-full bg-border" />
        </div>

        <!-- Sheet header -->
        <div class="flex items-center justify-between px-4 py-3 border-b border-border">
          <div>
            <p class="font-semibold text-foreground text-sm capitalize">{{ sheetLabel }}</p>
            <p class="text-xs text-muted-foreground">{{ sheetOrdens.length }} ordem(ns) agendada(s)</p>
          </div>
          <button
            class="p-1.5 rounded-md hover:bg-muted text-muted-foreground transition-colors"
            @click="sheetDay = null"
          >
            <X class="size-4" />
          </button>
        </div>

        <!-- Orders list -->
        <div class="flex flex-col gap-2 p-4 max-h-64 overflow-y-auto pb-safe">
          <button
            v-for="o in sheetOrdens"
            :key="o.codigo"
            class="flex items-center gap-3 w-full text-left p-3 rounded-lg border border-border bg-background hover:bg-muted/40 transition-colors"
            @click="router.push(`/minhas-ordens/${o.codigo}`); sheetDay = null"
          >
            <span :class="['size-2.5 rounded-full shrink-0', crit[o.criticidade]?.dot ?? 'bg-slate-400']" />
            <div class="flex-1 min-w-0">
              <p class="font-mono font-bold text-foreground text-sm">#{{ o.codigo }}</p>
              <p class="text-xs text-muted-foreground truncate">{{ o.nomeCliente ?? '—' }}</p>
            </div>
            <span :class="['text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wide shrink-0', crit[o.criticidade]?.pill ?? 'bg-muted/30 text-muted-foreground border border-border']">
              {{ o.criticidade }}
            </span>
            <ChevronRight class="size-4 text-muted-foreground shrink-0" />
          </button>
        </div>
      </div>
    </Transition>

  </div>
</template>
