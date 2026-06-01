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
  AlertTriangle, CheckCircle2, Clock, Eye, MapPinned, Pencil, Route, Search, Truck,
} from 'lucide-vue-next'
import { viagemService, type ViagemResponseDTO } from '@/services/viagemService'
import ViagemPreparacaoEdicaoModal from '@/components/viagem/ViagemPreparacaoEdicaoModal.vue'

const router = useRouter()

const editandoViagemId = ref<number | null>(null)

function abrirEdicao(codigo: number) {
  editandoViagemId.value = codigo
}

function onViagemAtualizada(atualizada: ViagemResponseDTO) {
  const index = viagens.value.findIndex(v => v.codigo === atualizada.codigo)
  if (index !== -1) viagens.value[index] = atualizada
  editandoViagemId.value = null
}

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

// Viagens auto-criadas ainda sem preenchimento (sem rota e sem data de saída)
const viagensPendentes = computed(() =>
  viagens.value.filter(v =>
    v.status === 'ABERTA' && !v.origem && !v.dataSaidaPrevista
  )
)

const filteredViagens = computed(() => {
  const pendentesIds = new Set(viagensPendentes.value.map(v => v.codigo))
  const q = searchQuery.value.trim().toLowerCase()
  const base = viagens.value.filter(v => !pendentesIds.has(v.codigo))
  if (!q) return base

  return base.filter(v =>
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
    <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-foreground">Preparação de Viagem</h1>
        <p class="text-sm text-muted-foreground">
          Gerencie as preparações de viagem cadastradas no sistema.
        </p>
      </div>

    </div>

    <div v-if="loading" class="text-center py-12 text-muted-foreground">
      Carregando...
    </div>

    <div v-if="erro" class="text-center py-12 text-red-400">
      {{ erro }}
    </div>

    <template v-if="!loading">
      <div class="grid grid-cols-2 xl:grid-cols-4 gap-4">
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

      <!-- Seção: Preparações pendentes de preenchimento -->
      <div v-if="viagensPendentes.length" class="rounded-xl border border-amber-500/40 bg-amber-500/5 overflow-hidden">
        <div class="flex items-center gap-3 px-5 py-4 border-b border-amber-500/30 bg-amber-500/10">
          <AlertTriangle class="size-5 text-amber-500 dark:text-amber-400 shrink-0" />
          <div>
            <p class="text-sm font-semibold text-amber-700 dark:text-amber-300">
              {{ viagensPendentes.length === 1 ? '1 preparação pendente de preenchimento' : `${viagensPendentes.length} preparações pendentes de preenchimento` }}
            </p>
            <p class="text-xs text-amber-600/80 dark:text-amber-400/70 mt-0.5">
              Criadas automaticamente ao agendar a ordem de serviço. Clique em Editar para preencher os detalhes da viagem.
            </p>
          </div>
        </div>
        <div class="divide-y divide-amber-500/20">
          <div
            v-for="viagem in viagensPendentes"
            :key="viagem.codigo"
            class="flex items-center justify-between px-5 py-3.5 hover:bg-amber-500/5 transition-colors"
          >
            <div class="flex items-center gap-4">
              <span class="font-mono text-sm font-semibold text-foreground">#{{ viagem.codigo }}</span>
              <div>
                <p class="text-sm font-medium text-foreground">{{ viagem.nomeCliente ?? `Cliente #${viagem.codigoCliente}` }}</p>
                <p class="text-xs text-muted-foreground">
                  Técnico: {{ viagem.nomeFuncionarioResponsavel ?? 'Não atribuído' }}
                  <span v-if="viagem.descricaoTipoViagem"> · {{ viagem.descricaoTipoViagem }}</span>
                </p>
              </div>
            </div>
            <Button
              size="sm"
              class="bg-amber-600 hover:bg-amber-700 text-white text-xs"
              @click="abrirEdicao(viagem.codigo)"
            >
              Preencher dados
            </Button>
          </div>
        </div>
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
          <h2 class="text-sm font-normal tracking-tight text-muted-foreground">
            Preparacoes de viagem
          </h2>
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
                  <span class="truncate">
                    {{ viagem.origem ?? '-' }} -> {{ viagem.destino ?? '-' }}
                  </span>
                </div>
              </TableCell>

              <TableCell class="text-sm text-muted-foreground">
                {{ formatDateTime(viagem.dataSaidaPrevista) }}
              </TableCell>

              <TableCell class="text-sm text-muted-foreground">
                {{ viagem.kmPrevisto ?? viagem.kmReal ?? '-' }}
                <span v-if="viagem.kmPrevisto || viagem.kmReal"> km</span>
              </TableCell>

              <TableCell>
                <div class="flex items-center gap-2">
                  <span :class="['size-2 rounded-full', statusConfig[viagem.status]?.dot ?? 'bg-slate-500']" />
                  <span class="text-sm text-foreground">
                    {{ statusConfig[viagem.status]?.label ?? viagem.status }}
                  </span>
                </div>
              </TableCell>

              <TableCell class="text-right pr-6">
                <div class="flex items-center justify-end gap-1">
                  <Button
                    variant="ghost"
                    size="icon"
                    class="h-9 w-9 text-muted-foreground hover:text-white transition-colors"
                    @click="abrirEdicao(viagem.codigo)"
                  >
                    <Pencil class="size-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="icon"
                    class="h-9 w-9 text-muted-foreground hover:text-white transition-colors"
                    @click="router.push(`/viagem-preparacao/${viagem.codigo}`)"
                  >
                    <Eye class="size-5" />
                  </Button>
                </div>
              </TableCell>
            </TableRow>

            <TableRow v-if="!filteredViagens.length">
              <TableCell colspan="7" class="py-10 text-center text-sm text-muted-foreground">
                Nenhuma preparação de viagem encontrada.
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>
    </template>

  </div>

  <!-- Edit modal -->
  <ViagemPreparacaoEdicaoModal
    :open="editandoViagemId !== null"
    :codigoViagem="editandoViagemId ?? 0"
    @close="editandoViagemId = null"
    @updated="onViagemAtualizada"
  />
</template>