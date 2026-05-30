<script setup lang="ts">
import { useRouter } from 'vue-router'
import FullCalendar from '@fullcalendar/vue3'
import type { CalendarOptions } from '@fullcalendar/core'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import ptBrLocale from '@fullcalendar/core/locales/pt-br'

const router = useRouter()

const calendarOptions: CalendarOptions = {
  plugins:     [dayGridPlugin, interactionPlugin],
  locale:      ptBrLocale,
  initialView: 'dayGridMonth',
  headerToolbar: {
    left:   'prev,next',
    center: 'title',
    right:  'today',
  },
  buttonText:   { today: 'Hoje' },
  events:       [],
  dayMaxEvents: 3,
  height:       'auto',
}
</script>

<template>
  <div class="p-3 sm:p-6 space-y-4">

    <!-- Toggle de calendários -->
    <div class="flex items-center gap-1 p-1 rounded-lg bg-sidebar border border-border w-fit">
      <button
        class="px-4 py-1.5 rounded-md text-sm font-semibold transition-all text-muted-foreground hover:text-foreground hover:bg-muted/40"
        @click="router.push('/calendario-gerente')"
      >
        Calendário de Ordens
      </button>
      <button class="px-4 py-1.5 rounded-md text-sm font-semibold transition-all bg-sidebar-primary text-white shadow-sm">
        Calendário de Técnicos
      </button>
    </div>

    <!-- Calendário vazio -->
    <div class="rounded-xl border border-border overflow-hidden">
      <FullCalendar :options="calendarOptions" />
    </div>

  </div>
</template>
