<script lang="ts" setup>
import type { HTMLAttributes } from "vue"
import { ErrorMessage } from "vee-validate"
import { toValue } from "vue"
import { cn } from "@/lib/utils"
import { useFormField } from "./useFormField"
import { AlertCircle } from "lucide-vue-next"

const props = defineProps<{
  class?: HTMLAttributes["class"]
}>()

const { name, formMessageId } = useFormField()
</script>

<template>
  <ErrorMessage
    :id="formMessageId"
    data-slot="form-message"
    :name="toValue(name)"
    v-slot="{ message }"
  >
    <div :class="cn('flex items-center gap-2 mt-1 text-sm font-medium bg-red-500/10 text-red-500 px-3 py-2 rounded-md border border-red-500/20', props.class)">
      <AlertCircle class="w-4 h-4 shrink-0" />
      <p>{{ message }}</p>
    </div>
  </ErrorMessage>
</template>
