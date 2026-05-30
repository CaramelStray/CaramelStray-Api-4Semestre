<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
    <Card
      v-for="card in cards"
      :key="card.label"
      class="bg-sidebar border-border rounded-2xl shadow-sm hover:border-primary/40 transition-all duration-300"
    >
      <CardContent class="p-5">
        <div class="flex items-start justify-between gap-4">
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <span
                class="w-2 h-2 rounded-full"
                :class="card.color?.replace('text-', 'bg-')"
              />
              
              <p class="text-xs uppercase tracking-wider text-muted-foreground font-semibold">
                {{ card.label }}
              </p>
            </div>

            <h2 class="text-4xl font-bold tracking-tight text-foreground">
              {{ card.value }}
            </h2>

            <p class="text-xs text-muted-foreground">
              {{ card.sub }}
            </p>
          </div>

          <div
            class="w-11 h-11 rounded-xl border border-border bg-muted/20 flex items-center justify-center"
          >
            <component
              :is="card.icon"
              class="w-5 h-5"
              :class="card.color"
            />
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { Card, CardContent } from '@/components/ui/card'

defineProps<{
  cards: {
    label: string
    value: string | number
    sub?: string
    icon: Component
    color?: string
  }[]
}>()
</script>