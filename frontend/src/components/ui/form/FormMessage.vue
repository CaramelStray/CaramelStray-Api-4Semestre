<script lang="ts" setup>
import type { HTMLAttributes } from "vue"
import { ErrorMessage } from "vee-validate"
import { useFormField } from "./useFormField"
import { toValue } from "vue"
import { cn } from "@/lib/utils"
import { AlertCircle } from "lucide-vue-next"

const props = defineProps<{
  class?: HTMLAttributes["class"]
}>()

const { name, formMessageId } = useFormField()
</script>

<template>
  <!--
    Wrapper de altura fixa: sempre ocupa h-5 + mt-1.
    Quando há erro, mostra ícone + texto compacto.
    Quando não há erro, o slot não renderiza nada mas o wrapper vazio mantém o espaço.
  -->
  <div :id="formMessageId" data-slot="form-message" :class="cn('h-5 mt-1 overflow-hidden', props.class)">
    <ErrorMessage :name="toValue(name)" v-slot="{ message }">
      <div class="flex items-center gap-1">
        <AlertCircle class="w-3 h-3 shrink-0 text-red-500" />
        <p class="text-[11px] font-medium text-red-500 truncate leading-none">{{ message }}</p>
      </div>
    </ErrorMessage>
  </div>
</template>
