<script setup lang="ts">
import { useThemeStore } from '@/stores/theme'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import { Moon, Sun } from 'lucide-vue-next'

const themeStore = useThemeStore()
</script>

<template>
  <div class="flex-1 space-y-4 p-8 pt-6">
    <div class="flex items-center justify-between space-y-2">
      <h2 class="text-3xl font-bold tracking-tight">Configurações</h2>
    </div>

    <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-7">
      <Card class="col-span-4 shadow-sm border-border/50">
        <CardHeader>
          <CardTitle class="flex items-center gap-2">
            <Sun v-if="!themeStore.isDark" class="h-5 w-5 text-orange-500" />
            <Moon v-else class="h-5 w-5 text-blue-400" />
            Aparência
          </CardTitle>
          <CardDescription>
            Personalize o visual da sua plataforma para melhor conforto.
          </CardDescription>
        </CardHeader>
        <CardContent class="space-y-6">
          <div class="flex items-center justify-between p-4 rounded-lg bg-muted/30 border border-border/40">
            <div class="flex flex-col space-y-1">
              <Label class="text-base font-semibold">Tema do Sistema</Label>
              <span class="text-sm text-muted-foreground">
                Alternar entre os modos claro e escuro.
              </span>
            </div>
            <button
              @click="themeStore.toggleTheme()"
              class="relative inline-flex h-10 w-20 items-center rounded-full bg-muted shadow-inner transition-all hover:bg-muted/80 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 focus-visible:ring-offset-background"
            >
              <div
                class="absolute flex w-full justify-between px-3 text-muted-foreground pointer-events-none"
              >
                <Sun class="h-4 w-4" />
                <Moon class="h-4 w-4" />
              </div>
              <span
                :class="[
                  themeStore.isDark ? 'translate-x-11' : 'translate-x-1',
                  'inline-flex items-center justify-center h-8 w-8 transform rounded-full bg-background shadow-md ring-0 transition duration-300 ease-in-out'
                ]"
              >
                <Sun v-if="!themeStore.isDark" class="h-4 w-4 text-orange-500" />
                <Moon v-else class="h-4 w-4 text-blue-400" />
              </span>
            </button>
          </div>
        </CardContent>
      </Card>

      <Card class="col-span-3 shadow-sm border-border/50">
        <CardHeader>
          <CardTitle>Informativo</CardTitle>
          <CardDescription>
            Informações sobre o sistema.
          </CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="flex flex-col space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-muted-foreground">Versão</span>
              <span class="font-medium">1.2.0-stable</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-muted-foreground">Ambiente</span>
              <span class="font-medium px-2 py-0.5 rounded-full bg-blue-500/10 text-blue-500 text-[10px] uppercase font-bold">Produção</span>
            </div>
            <div class="flex justify-between text-sm pt-2 border-t">
              <span class="text-muted-foreground">Último Acesso</span>
              <span class="font-medium">Hoje, 14:20</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<style scoped>
/* Adicione estilos específicos se necessário */
</style>
