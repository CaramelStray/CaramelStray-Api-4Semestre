<script setup lang="ts">
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ClipboardList } from 'lucide-vue-next'
</script>

<template>
  <div class="p-6 space-y-6">
    <div class="flex items-center gap-3">
      <div class="flex items-center justify-center w-10 h-10 rounded-lg border border-blue-500 bg-blue-500/20 text-blue-400">
        <ClipboardList class="w-5 h-5" />
      </div>
      <div>
        <h1 class="text-xl font-bold text-foreground">Minhas Ordens de Serviço</h1>
        <p class="text-sm text-muted-foreground">Ordens atribuídas a você</p>
      </div>
    </div>

    <Card class="border-dashed">
      <CardHeader>
        <CardTitle class="text-base text-muted-foreground font-normal text-center">Em breve</CardTitle>
      </CardHeader>
      <CardContent class="flex flex-col items-center gap-2 py-10 text-muted-foreground">
        <ClipboardList class="w-12 h-12 opacity-30" />
        <p class="text-sm">A visualização das ordens estará disponível em breve.</p>
      </CardContent>
    </Card>
  </div>
</template>
