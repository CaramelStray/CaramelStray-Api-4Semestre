<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table'
import {
  CheckCircle2, Clock, Eye, MapPinned, Route, Search, Truck,
} from 'lucide-vue-next'
import { viagemService, type ViagemResponseDTO } from '@/services/viagemService'

const router = useRouter()

const viagens = ref<ViagemResponseDTO[]>([])
const searchQuery = ref('')
const loading = ref(false)
const erro = ref('')

const statusConfig: Record<string, { dot: string; label: string }> = {
  ABERTA: { dot: 'bg-amber-500', label: 'Aberta' },
  EM_ANDAMENTO: { dot: 'bg-blue-500', label: 'Em andamento' },
  FINALIZADA: { dot: 'bg-emerald-500', label: 'Finalizada' },
  CANCELADA: { dot: 'bg-red-500', label: 'Cancelada' },
}

const stats = computed(() => [
  {
    label: 'Viagens',
    value: viagens.value.length.toString(),
    sub: 'Registradas no sistema',
    icon: Route,
    color: 'text-blue-400',
  },
  {
    label: 'Abertas',
    value: viagens.value.filter(v => v.status === 'ABERTA').length.toString(),
    sub: 'Aguardando saida',
    icon: Clock,
    color: 'text-amber-400',
  },
  {
    label: 'Em campo',
    value: viagens.value.filter(v => v.status === 'EM_ANDAMENTO').length.toString(),
    sub: 'Em andamento',
    icon: Truck,
    color: 'text-cyan-400',
  },
  {
    label: 'Finalizadas',
    value: viagens.value.filter(v => v.status === 'FINALIZADA').length.toString(),
    sub: 'Preparacao concluida',
    icon: CheckCircle2,
    color: 'text-emerald-400',
  },
])

const filteredViagens = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return viagens.value

  return viagens.value.filter(v =>
    String(v.codigo).includes(q) ||
    v.nomeCliente?.toLowerCase().includes(q) ||
    v.nomeFuncionarioResponsavel?.toLowerCase().includes(q) ||
    v.status?.toLowerCase().includes(q) ||
    v.origem?.toLowerCase().includes(q) ||
    v.destino?.toLowerCase().includes(q)
  )
})

const formatDateTime = (dt?: string | null) => {
  if (!dt) return '-'
  const d = new Date(dt)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

async function carregarViagens() {
  loading.value = true
  erro.value = ''
  try {
    viagens.value = await viagemService.listar()
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar viagens'
  } finally {
    loading.value = false
  }
}

onMounted(carregarViagens)
</script>

<template>
  <div class="p-6 space-y-6">
    <div v-if="loading" class="text-center py-12 text-muted-foreground">Carregando...</div>
    <div v-if="erro" class="text-center py-12 text-red-400">{{ erro }}</div>

    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
      <Card v-for="stat in stats" :key="stat.label" class="bg-sidebar border-border">
        <CardHeader class="flex flex-row items-center justify-between pb-2">
          <CardTitle class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider">
            {{ stat.label }}
          </CardTitle>
          <component :is="stat.icon" class="w-4 h-4" :class="stat.color" />
        </CardHeader>
        <CardContent>
          <div class="text-3xl font-bold text-foreground">{{ stat.value }}</div>
          <p class="text-[10px] text-muted-foreground mt-1">{{ stat.sub }}</p>
        </CardContent>
      </Card>
    </div>

    <div class="relative">
      <Search class="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
      <Input
        v-model="searchQuery"
        placeholder="Buscar por codigo, cliente, tecnico, origem, destino ou status..."
        class="pl-11 bg-sidebar h-12 text-sm w-full border-border focus-visible:ring-1 focus-visible:ring-sidebar-primary"
      />
    </div>

    <div class="rounded-md border border-border bg-sidebar overflow-hidden">
      <div class="p-4 border-b border-border bg-muted/5">
        <h2 class="text-sm font-normal tracking-tight text-muted-foreground">Preparacoes de viagem</h2>
      </div>

      <Table>
        <TableHeader>
          <TableRow class="hover:bg-transparent border-border text-xs uppercase font-bold text-muted-foreground">
            <TableHead class="pl-6 h-12"># Viagem</TableHead>
            <TableHead class="h-12">Cliente</TableHead>
            <TableHead class="h-12">Rota</TableHead>
            <TableHead class="h-12">Saida prevista</TableHead>
            <TableHead class="h-12">Distancia</TableHead>
            <TableHead class="h-12">Status</TableHead>
            <TableHead class="text-right pr-6 h-12">Acoes</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          <TableRow
            v-for="viagem in filteredViagens"
            :key="viagem.codigo"
            class="border-border hover:bg-muted/30 transition-colors even:bg-muted/50"
          >
            <TableCell class="pl-6 py-3 font-mono text-sm font-medium text-foreground">
              #{{ viagem.codigo }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ viagem.nomeCliente ?? `Cliente #${viagem.codigoCliente}` }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              <div class="flex items-center gap-2 max-w-[320px]">
                <MapPinned class="size-4 text-cyan-400 shrink-0" />
                <span class="truncate">{{ viagem.origem ?? '-' }} -> {{ viagem.destino ?? '-' }}</span>
              </div>
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ formatDateTime(viagem.dataSaidaPrevista) }}
            </TableCell>
            <TableCell class="text-sm text-muted-foreground">
              {{ viagem.kmPrevisto ?? viagem.kmReal ?? '-' }}<span v-if="viagem.kmPrevisto || viagem.kmReal"> km</span>
            </TableCell>
            <TableCell>
              <div class="flex items-center gap-2">
                <span :class="['size-2 rounded-full', statusConfig[viagem.status]?.dot ?? 'bg-slate-500']" />
                <span class="text-sm text-foreground">{{ statusConfig[viagem.status]?.label ?? viagem.status }}</span>
              </div>
            </TableCell>
            <TableCell class="text-right pr-6">
              <Button
                variant="ghost"
                size="icon"
                class="h-9 w-9 text-muted-foreground hover:text-white transition-colors"
                @click="router.push(`/viagem-preparacao/${viagem.codigo}`)"
              >
                <Eye class="size-5" />
              </Button>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  </div>
</template>
