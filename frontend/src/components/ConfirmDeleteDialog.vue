<script setup lang="ts">
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Trash2, Loader2 } from 'lucide-vue-next'

defineProps<{
  open: boolean
  titulo: string
  descricao: string
  carregando?: boolean
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  confirmar: []
}>()
</script>

<template>
  <Dialog :open="open" @update:open="emit('update:open', $event)">
    <DialogContent class="sm:max-w-[420px]">
      <DialogHeader>
        <div class="flex items-center gap-3 mb-1">
          <div class="flex items-center justify-center w-10 h-10 rounded-full bg-red-500/15 shrink-0">
            <Trash2 class="w-5 h-5 text-red-400" />
          </div>
          <DialogTitle class="text-lg font-bold">{{ titulo }}</DialogTitle>
        </div>
        <DialogDescription class="text-sm text-muted-foreground pl-[52px]">
          {{ descricao }}
        </DialogDescription>
      </DialogHeader>

      <DialogFooter class="mt-4 gap-2">
        <Button
          variant="outline"
          class="border-border hover:bg-muted/30"
          :disabled="carregando"
          @click="emit('update:open', false)"
        >
          Cancelar
        </Button>
        <Button
          class="bg-red-600 hover:bg-red-500 text-white"
          :disabled="carregando"
          @click="emit('confirmar')"
        >
          <Loader2 v-if="carregando" class="w-4 h-4 mr-2 animate-spin" />
          <Trash2 v-else class="w-4 h-4 mr-2" />
          Excluir
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
