<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { CalendarDate, getLocalTimeZone, today } from '@internationalized/date'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'
import { CalendarIcon } from 'lucide-vue-next'
import { cn } from '@/lib/utils'

const props = defineProps<{
  modelValue?: string
  minValue?: CalendarDate
  class?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const calendarOpen = ref(false)
const inputValue = ref('')

const maxYear = computed(() => new Date().getFullYear() + 30)
const maxValue = computed(() => today(getLocalTimeZone()).add({ years: 30 }))

const calendarValue = computed((): CalendarDate | undefined => {
  if (!props.modelValue) return undefined
  const parts = props.modelValue.split('-').map(Number)
  if (parts.length !== 3 || parts.some(isNaN)) return undefined
  const [year, month, day] = parts
  try { return new CalendarDate(year, month, day) } catch { return undefined }
})

watch(
  () => props.modelValue,
  (newVal: string | undefined) => {
    if (!newVal) { inputValue.value = ''; return }
    const parts = newVal.split('-')
    if (parts.length === 3) {
      const [year, month, day] = parts
      const formatted = `${day}/${month}/${year}`
      if (formatted !== inputValue.value) inputValue.value = formatted
    }
  },
  { immediate: true }
)

function isValidDate(day: number, month: number, year: number): boolean {
  if (month < 1 || month > 12 || day < 1 || year < 1000) return false
  const d = new Date(year, month - 1, day)
  return d.getFullYear() === year && d.getMonth() === month - 1 && d.getDate() === day
}

function onInput(e: Event) {
  const input = e.target as HTMLInputElement
  let digits = input.value.replace(/\D/g, '').slice(0, 8)

  // Cap day to 01-31 as soon as 2 digits are present
  if (digits.length >= 2) {
    let day = Number(digits.slice(0, 2))
    if (day === 0) day = 1
    else if (day > 31) day = 31
    digits = String(day).padStart(2, '0') + digits.slice(2)
  }

  // Cap month to 01-12 as soon as 4 digits are present
  if (digits.length >= 4) {
    let month = Number(digits.slice(2, 4))
    if (month === 0) month = 1
    else if (month > 12) month = 12
    digits = digits.slice(0, 2) + String(month).padStart(2, '0') + digits.slice(4)
  }

  // Cap year to maxYear and validate the full date when all 8 digits are present
  if (digits.length === 8) {
    let year = Number(digits.slice(4, 8))
    if (year > maxYear.value) year = maxYear.value
    digits = digits.slice(0, 4) + String(year).padStart(4, '0')

    const day = Number(digits.slice(0, 2))
    const month = Number(digits.slice(2, 4))
    if (isValidDate(day, month, year)) {
      const iso = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      emit('update:modelValue', iso)
    }
  }

  // Format as DD/MM/YYYY
  let formatted = digits.slice(0, 2)
  if (digits.length > 2) formatted += '/' + digits.slice(2, 4)
  if (digits.length > 4) formatted += '/' + digits.slice(4, 8)

  input.value = formatted
  inputValue.value = formatted
}

function onBlur() {
  const digits = inputValue.value.replace(/\D/g, '')
  if (digits.length === 0) {
    emit('update:modelValue', '')
  } else if (digits.length < 8) {
    // Incomplete date — revert display to last valid model value
    if (!props.modelValue) {
      inputValue.value = ''
    } else {
      const [year, month, day] = props.modelValue.split('-')
      inputValue.value = `${day}/${month}/${year}`
    }
  }
}

function onCalendarSelect(val: CalendarDate | undefined) {
  if (!val) return
  emit('update:modelValue', val.toString())
  calendarOpen.value = false
}
</script>

<template>
  <div :class="cn('relative flex items-center', props.class)">
    <input
      :value="inputValue"
      placeholder="DD/MM/AAAA"
      maxlength="10"
      data-slot="input"
      :class="cn(
        'file:text-foreground placeholder:text-muted-foreground border-input h-9 w-full min-w-0 rounded-md border bg-transparent px-3 py-1 pr-9 text-base shadow-xs transition-[color,box-shadow] outline-none md:text-sm',
        'focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]',
        'aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive'
      )"
      @input="onInput"
      @blur="onBlur"
    />
    <Popover v-model:open="calendarOpen">
      <PopoverTrigger as-child>
        <button
          type="button"
          class="absolute right-2 flex items-center justify-center text-muted-foreground hover:text-foreground transition-colors focus-visible:outline-none"
        >
          <CalendarIcon class="h-4 w-4" />
        </button>
      </PopoverTrigger>
      <PopoverContent class="w-auto p-0 z-[200]" align="end">
        <Calendar
          layout="month-and-year"
          locale="pt-BR"
          :week-starts-on="0"
          :min-value="minValue"
          :max-value="maxValue"
          :model-value="calendarValue"
          @update:model-value="onCalendarSelect"
        />
      </PopoverContent>
    </Popover>
  </div>
</template>
