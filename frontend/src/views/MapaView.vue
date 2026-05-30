<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  MapPin, Users, UserCog, RefreshCw, Layers,
  CheckCircle2, XCircle,
} from 'lucide-vue-next'

import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { useThemeStore } from '@/stores/theme'

// ─── Tiles CARTO (gratuito, sem API key, sem WebGL) ──────────────────────────
const DARK_TILES  = 'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'
const LIGHT_TILES = 'https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png'
const ATTRIBUTION = '&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a> &copy; <a href="https://carto.com">CARTO</a>'

// ─── Refs e estado ────────────────────────────────────────────────────────────
const themeStore = useThemeStore()
const mapContainer = ref<HTMLDivElement | null>(null)
let map: L.Map | null = null
let tileLayer: L.TileLayer | null = null

const clientes = ref<ClienteResponseDTO[]>([])
const tecnicos = ref<TecnicoResponseDTO[]>([])
const loading = ref(false)
const erro = ref('')
const tecnicoSelecionadoId = ref<number | null>(null)
const mostrarClientes = ref(true)
const mostrarTecnicos = ref(true)

// Marcadores (imperativo Leaflet)
const markersClientes: L.Marker[] = []
const markersTecnicos: Record<number, L.Marker> = {}

// ─── Geocoding cache ──────────────────────────────────────────────────────────
const geocodeCache = new Map<string, [number, number] | null>()

async function geocodeEndereco(
  rua: string,
  numero: string,
  cidade: string,
  estado: string,
  pais: string,
): Promise<[number, number] | null> {
  const partes = [rua && numero ? `${rua} ${numero}` : rua, cidade, estado, pais]
    .filter(Boolean)
    .join(', ')
  const key = partes.toLowerCase()
  if (geocodeCache.has(key)) return geocodeCache.get(key)!
  try {
    const query = encodeURIComponent(partes)
    const res = await fetch(
      `https://nominatim.openstreetmap.org/search?q=${query}&format=json&limit=1`,
      { headers: { 'Accept-Language': 'pt-BR' } },
    )
    const data = await res.json()
    if (data.length > 0) {
      const coords: [number, number] = [parseFloat(data[0].lat), parseFloat(data[0].lon)]
      geocodeCache.set(key, coords)
      return coords
    }
  } catch { /* falha silenciosa */ }
  geocodeCache.set(key, null)
  return null
}

// ─── Ícones SVG com DivIcon ───────────────────────────────────────────────────

/** Pin para clientes — azul ativo / cinza inativo */
function iconeCliente(ativo: boolean): L.DivIcon {
  const cor = ativo ? '#3b82f6' : '#94a3b8'
  const sombra = ativo ? 'drop-shadow(0 3px 5px rgba(59,130,246,0.4))' : 'drop-shadow(0 2px 3px rgba(0,0,0,0.3))'
  return L.divIcon({
    html: `<svg width="30" height="38" viewBox="0 0 30 38" xmlns="http://www.w3.org/2000/svg" style="filter:${sombra}">
      <path d="M15 0C6.716 0 0 6.716 0 15c0 10.314 15 23 15 23S30 25.314 30 15C30 6.716 23.284 0 15 0z" fill="${cor}"/>
      <circle cx="15" cy="15" r="6.5" fill="white" fill-opacity="0.95"/>
      <circle cx="15" cy="15" r="3.5" fill="${cor}"/>
    </svg>`,
    className: 'leaflet-div-icon-custom',
    iconSize: [30, 38],
    iconAnchor: [15, 38],
    popupAnchor: [0, -42],
  })
}

/** Extrai iniciais do nome: "Pedro Henrique" → "PH" */
function iniciais(nome: string): string {
  const partes = nome.trim().split(/\s+/).filter(Boolean)
  if (partes.length === 0) return '?'
  const primeiro = partes[0]!
  if (partes.length === 1) return primeiro.substring(0, 2).toUpperCase()
  const ultimo = partes[partes.length - 1]!
  return (primeiro[0]! + ultimo[0]!).toUpperCase()
}

/** Balão estilo Google com iniciais do técnico */
function iconeTecnico(estado: string | null, nome: string, selecionado = false): L.DivIcon {
  const corMap: Record<string, string> = {
    'DISPONÍVEL': '#10b981',
    'EM CAMPO':   '#3b82f6',
    'FOLGA':      '#64748b',
  }
  const cor = corMap[estado ?? ''] ?? '#10b981'
  const glowColor = cor + '55'
  // Selecionado: glow mais forte + borda branca dupla
  const sombra = selecionado
    ? `filter:drop-shadow(0 0 8px ${glowColor}) drop-shadow(0 4px 10px rgba(0,0,0,0.4));`
    : `filter:drop-shadow(0 3px 6px rgba(0,0,0,0.35));`
  // Borda extra quando selecionado (anel azul)
  const ringExtra = selecionado
    ? `<circle cx="22" cy="20" r="23" fill="none" stroke="${cor}" stroke-width="2" opacity="0.5"/>`
    : ''
  const txt = iniciais(nome)
  return L.divIcon({
    html: `<svg width="48" height="64" viewBox="0 0 48 64" xmlns="http://www.w3.org/2000/svg" style="${sombra}" overflow="visible">
      <!-- Borda branca (2px de separação entre ícones sobrepostos) -->
      <circle cx="24" cy="25" r="22" fill="white"/>
      <!-- Triângulo branco (borda da cauda) -->
      <polygon points="13,44 35,44 24,64" fill="white"/>
      <!-- Cauda colorida -->
      <polygon points="16,43 32,43 24,61" fill="${cor}"/>
      <!-- Círculo colorido principal -->
      <circle cx="24" cy="25" r="20" fill="${cor}"/>
      ${ringExtra}
      <!-- Iniciais -->
      <text x="24" y="25" text-anchor="middle" dominant-baseline="central"
        fill="white" font-size="14" font-weight="700"
        font-family="system-ui,-apple-system,sans-serif" letter-spacing="0.5">
        ${txt}
      </text>
    </svg>`,
    className: 'leaflet-div-icon-custom',
    iconSize: [48, 64],
    iconAnchor: [24, 64],
    popupAnchor: [0, -68],
  })
}

// ─── HTML dos popups ─────────────────────────────────────────────────────────

function popupCliente(c: ClienteResponseDTO): string {
  const statusBg  = c.ativo ? '#d1fae5' : '#fee2e2'
  const statusTxt = c.ativo ? '#065f46' : '#991b1b'
  const endereco  = [c.rua, c.numero, c.cidade, c.estadoRegiao, c.pais].filter(Boolean).join(', ')
  const dist = c.classificacaoDistancia
    ? `<span style="padding:2px 8px;border-radius:4px;font-size:10px;font-weight:600;background:#dbeafe;color:#1e40af">${c.classificacaoDistancia}</span>`
    : ''
  return `<div style="min-width:200px;font-size:13px">
    <div style="display:flex;align-items:center;gap:7px;margin-bottom:8px">
      <div style="width:9px;height:9px;border-radius:50%;background:${c.ativo ? '#10b981' : '#ef4444'};flex-shrink:0"></div>
      <b style="font-size:14px">${c.nomeEmpresa}</b>
    </div>
    <p style="margin:3px 0;opacity:0.8">📍 ${endereco || '—'}</p>
    <p style="margin:3px 0;opacity:0.8">📧 ${c.emailContato || '—'}</p>
    <p style="margin:3px 0;opacity:0.8">📱 ${c.telefoneContato || '—'}</p>
    <div style="margin-top:8px;display:flex;gap:5px;flex-wrap:wrap">
      <span style="padding:2px 8px;border-radius:4px;font-size:10px;font-weight:600;background:${statusBg};color:${statusTxt}">${c.ativo ? 'Ativo' : 'Inativo'}</span>
      ${dist}
    </div>
  </div>`
}

function popupTecnico(t: TecnicoResponseDTO): string {
  const corMap: Record<string, { bg: string; txt: string }> = {
    'DISPONÍVEL': { bg: '#d1fae5', txt: '#065f46' },
    'EM CAMPO':   { bg: '#dbeafe', txt: '#1e40af' },
    'FOLGA':      { bg: '#f1f5f9', txt: '#475569' },
  }
  const badge = corMap[t.estado ?? ''] ?? { bg: '#f1f5f9', txt: '#475569' }
  return `<div style="min-width:200px;font-size:13px">
    <b style="font-size:14px;display:block;margin-bottom:8px">${t.nome}</b>
    <p style="margin:3px 0;opacity:0.8">💼 ${t.cargo || 'Técnico'}</p>
    <p style="margin:3px 0;opacity:0.8">📧 ${t.email}</p>
    <p style="margin:3px 0;opacity:0.8">📱 ${t.telefone || '—'}</p>
    <span style="display:inline-block;margin-top:8px;padding:2px 8px;border-radius:4px;font-size:10px;font-weight:600;background:${badge.bg};color:${badge.txt}">${t.estado || 'N/D'}</span>
  </div>`
}

// ─── Gerenciamento de marcadores ─────────────────────────────────────────────

function limparMarcadoresClientes() {
  markersClientes.forEach(m => m.remove())
  markersClientes.splice(0)
}

function limparMarcadoresTecnicos() {
  Object.values(markersTecnicos).forEach(m => m.remove())
  for (const key in markersTecnicos) delete markersTecnicos[key]
}

function limparMarcadores() {
  limparMarcadoresClientes()
  limparMarcadoresTecnicos()
}

async function adicionarMarcadoresClientes() {
  if (!map || !mostrarClientes.value) return
  for (const c of clientes.value) {
    if (!c.cidade) continue
    const coords = await geocodeEndereco(c.rua, c.numero, c.cidade, c.estadoRegiao, c.pais)
    if (!coords) continue
    const popup = L.popup({ className: 'mapa-popup', offset: [0, -4] }).setContent(popupCliente(c))
    const marker = L.marker(coords, { icon: iconeCliente(c.ativo) })
      .bindPopup(popup)
      .addTo(map!)
    markersClientes.push(marker)
    await new Promise(r => setTimeout(r, 150)) // respeita rate-limit do Nominatim
  }
}

function adicionarMarcadoresTecnicos() {
  if (!map || !mostrarTecnicos.value) return
  for (const t of tecnicos.value) {
    if (!t.latitude || !t.longitude) continue
    const selecionado = tecnicoSelecionadoId.value === t.id
    const popup = L.popup({ className: 'mapa-popup', offset: [0, -4] }).setContent(popupTecnico(t))
    const marker = L.marker([t.latitude, t.longitude], { icon: iconeTecnico(t.estado, t.nome, selecionado) })
      .bindPopup(popup)
      .addTo(map!)
    markersTecnicos[t.id] = marker
  }
}

async function adicionarMarcadores() {
  await adicionarMarcadoresClientes()
  adicionarMarcadoresTecnicos()
}

// ─── Ação: focar técnico no mapa (fly-to) ────────────────────────────────────
function focarTecnico(t: TecnicoResponseDTO) {
  if (!map || !t.latitude || !t.longitude) return
  tecnicoSelecionadoId.value = t.id
  // Atualiza ícone para indicar selecionado
  const marker = markersTecnicos[t.id]
  if (marker) {
    marker.setIcon(iconeTecnico(t.estado, t.nome, true))
    map.flyTo([t.latitude, t.longitude], 13, { duration: 1.2, easeLinearity: 0.25 })
    map.once('moveend', () => marker.openPopup())
  }
}

// ─── Toggle de camadas ────────────────────────────────────────────────────────
watch(mostrarClientes, (val) => {
  if (val) adicionarMarcadoresClientes()
  else limparMarcadoresClientes()
})

watch(mostrarTecnicos, (val) => {
  if (val) adicionarMarcadoresTecnicos()
  else limparMarcadoresTecnicos()
})

// ─── Troca de tema: troca o tile layer ───────────────────────────────────────
watch(() => themeStore.isDark, (isDark) => {
  if (!map || !tileLayer) return
  tileLayer.remove()
  tileLayer = L.tileLayer(isDark ? DARK_TILES : LIGHT_TILES, {
    attribution: ATTRIBUTION,
    maxZoom: 19,
    subdomains: 'abcd',
  }).addTo(map)
})

// ─── Carregar dados ───────────────────────────────────────────────────────────
const clientesNoMapa = ref(0)

async function carregarDados() {
  loading.value = true
  erro.value = ''
  limparMarcadores()
  clientesNoMapa.value = 0

  try {
    const [clientesData, tecnicosData] = await Promise.all([
      clienteService.listar(),
      tecnicoService.listar(),
    ])
    clientes.value = clientesData
    tecnicos.value = tecnicosData
    await adicionarMarcadores()
    clientesNoMapa.value = markersClientes.length
  } catch (e: any) {
    erro.value = e.message ?? 'Erro ao carregar dados do mapa'
  } finally {
    loading.value = false
  }
}

// ─── Computados para stats ────────────────────────────────────────────────────
const tecnicosComCoords = computed(() =>
  tecnicos.value.filter(t => t.latitude && t.longitude),
)

const stats = computed(() => [
  {
    label: 'Clientes no mapa',
    value: clientesNoMapa.value,
    sub: `de ${clientes.value.length} cadastrados`,
    icon: Users,
    color: 'text-blue-400',
  },
  {
    label: 'Técnicos em campo',
    value: tecnicosComCoords.value.filter(t => t.estado === 'EM CAMPO').length,
    sub: 'com localização ativa',
    icon: MapPin,
    color: 'text-emerald-400',
  },
  {
    label: 'Técnicos disponíveis',
    value: tecnicosComCoords.value.filter(t => t.estado === 'DISPONÍVEL').length,
    sub: 'prontos para acionamento',
    icon: CheckCircle2,
    color: 'text-purple-400',
  },
  {
    label: 'Total no mapa',
    value: clientesNoMapa.value + tecnicosComCoords.value.length,
    sub: 'clientes + técnicos',
    icon: Layers,
    color: 'text-amber-400',
  },
])

// ─── Helpers visuais ─────────────────────────────────────────────────────────
const estadoDot: Record<string, string> = {
  'DISPONÍVEL': '#10b981',
  'EM CAMPO':   '#3b82f6',
  'FOLGA':      '#64748b',
}
function dotColor(estado: string | null): string {
  return estadoDot[estado ?? ''] ?? '#10b981'
}

// ─── Ciclo de vida ────────────────────────────────────────────────────────────
onMounted(() => {
  if (!mapContainer.value) return

  map = L.map(mapContainer.value, {
    center: [-15.7801, -47.9292], // Brasília
    zoom: 4,
    zoomControl: false,
  })

  // Tile layer inicial de acordo com o tema atual
  tileLayer = L.tileLayer(themeStore.isDark ? DARK_TILES : LIGHT_TILES, {
    attribution: ATTRIBUTION,
    maxZoom: 19,
    subdomains: 'abcd',
  }).addTo(map)

  // Controles de zoom no canto inferior direito
  L.control.zoom({ position: 'bottomright' }).addTo(map)

  carregarDados()
})

onUnmounted(() => {
  limparMarcadores()
  map?.remove()
  map = null
})
</script>

<template>
  <div class="p-6 space-y-5 h-full flex flex-col">

    <!-- Cabeçalho -->
    <div class="flex items-center justify-between shrink-0">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2">
          <MapPin class="w-6 h-6 text-blue-400" />
          Mapa de Cobertura
        </h1>
        <p class="text-sm text-muted-foreground mt-1">
          Localização geográfica de clientes e técnicos em tempo real
        </p>
      </div>
      <Button
        variant="outline"
        size="lg"
        class="h-10 border-border hover:bg-muted/20 gap-2"
        :disabled="loading"
        @click="carregarDados"
      >
        <RefreshCw class="w-4 h-4" :class="{ 'animate-spin': loading }" />
        Atualizar
      </Button>
    </div>

    <!-- Cards de estatísticas -->
    <div class="grid grid-cols-2 xl:grid-cols-4 gap-4 shrink-0">
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

    <!-- Layout principal: Sidebar + Mapa -->
    <div class="flex gap-4 flex-1 min-h-0">

      <!-- Painel lateral -->
      <div class="w-64 shrink-0 flex flex-col gap-3 min-h-0">

        <!-- Toggle de camadas -->
        <Card class="bg-sidebar border-border shrink-0">
          <CardHeader class="pb-3">
            <CardTitle class="text-xs font-bold text-muted-foreground uppercase tracking-wider flex items-center gap-2">
              <Layers class="w-3.5 h-3.5" /> Camadas
            </CardTitle>
          </CardHeader>
          <CardContent class="space-y-3 pt-0">
            <label class="flex items-center gap-3 cursor-pointer">
              <button
                role="switch"
                :aria-checked="mostrarClientes"
                class="relative inline-flex h-5 w-9 rounded-full transition-colors focus:outline-none shrink-0"
                :class="mostrarClientes ? 'bg-blue-600' : 'bg-muted/60'"
                @click="mostrarClientes = !mostrarClientes"
              >
                <span
                  class="pointer-events-none inline-block h-4 w-4 rounded-full bg-white shadow transition-transform mt-0.5"
                  :class="mostrarClientes ? 'translate-x-4' : 'translate-x-0.5'"
                />
              </button>
              <span class="text-sm text-foreground flex items-center gap-2">
                <Users class="w-3.5 h-3.5 text-blue-400" />
                Clientes
              </span>
            </label>

            <label class="flex items-center gap-3 cursor-pointer">
              <button
                role="switch"
                :aria-checked="mostrarTecnicos"
                class="relative inline-flex h-5 w-9 rounded-full transition-colors focus:outline-none shrink-0"
                :class="mostrarTecnicos ? 'bg-emerald-600' : 'bg-muted/60'"
                @click="mostrarTecnicos = !mostrarTecnicos"
              >
                <span
                  class="pointer-events-none inline-block h-4 w-4 rounded-full bg-white shadow transition-transform mt-0.5"
                  :class="mostrarTecnicos ? 'translate-x-4' : 'translate-x-0.5'"
                />
              </button>
              <span class="text-sm text-foreground flex items-center gap-2">
                <UserCog class="w-3.5 h-3.5 text-emerald-400" />
                Técnicos
              </span>
            </label>
          </CardContent>
        </Card>

        <!-- Legenda -->
        <Card class="bg-sidebar border-border shrink-0">
          <CardHeader class="pb-3">
            <CardTitle class="text-xs font-bold text-muted-foreground uppercase tracking-wider">
              Legenda
            </CardTitle>
          </CardHeader>
          <CardContent class="pt-0 space-y-1.5">
            <div class="flex items-center gap-2 text-sm text-foreground">
              <svg width="13" height="17" viewBox="0 0 30 38"><path d="M15 0C6.716 0 0 6.716 0 15c0 10.314 15 23 15 23S30 25.314 30 15C30 6.716 23.284 0 15 0z" fill="#3b82f6"/><circle cx="15" cy="15" r="6.5" fill="white"/><circle cx="15" cy="15" r="3.5" fill="#3b82f6"/></svg>
              Cliente ativo
            </div>
            <div class="flex items-center gap-2 text-sm text-foreground">
              <svg width="13" height="17" viewBox="0 0 30 38"><path d="M15 0C6.716 0 0 6.716 0 15c0 10.314 15 23 15 23S30 25.314 30 15C30 6.716 23.284 0 15 0z" fill="#94a3b8"/><circle cx="15" cy="15" r="6.5" fill="white"/><circle cx="15" cy="15" r="3.5" fill="#94a3b8"/></svg>
              Cliente inativo
            </div>
            <div class="border-t border-border my-1.5" />
            <div class="flex items-center gap-2 text-sm text-foreground">
              <svg width="13" height="17" viewBox="0 0 32 40"><path d="M16 0C7.163 0 0 7.163 0 16c0 5.516 2.805 10.367 7.086 13.255L16 40l8.914-10.745C29.195 26.367 32 21.516 32 16 32 7.163 24.837 0 16 0z" fill="#10b981"/><circle cx="16" cy="12" r="4.8" fill="white"/></svg>
              Disponível
            </div>
            <div class="flex items-center gap-2 text-sm text-foreground">
              <svg width="13" height="17" viewBox="0 0 32 40"><path d="M16 0C7.163 0 0 7.163 0 16c0 5.516 2.805 10.367 7.086 13.255L16 40l8.914-10.745C29.195 26.367 32 21.516 32 16 32 7.163 24.837 0 16 0z" fill="#3b82f6"/><circle cx="16" cy="12" r="4.8" fill="white"/></svg>
              Em campo
            </div>
            <div class="flex items-center gap-2 text-sm text-foreground">
              <svg width="13" height="17" viewBox="0 0 32 40"><path d="M16 0C7.163 0 0 7.163 0 16c0 5.516 2.805 10.367 7.086 13.255L16 40l8.914-10.745C29.195 26.367 32 21.516 32 16 32 7.163 24.837 0 16 0z" fill="#64748b"/><circle cx="16" cy="12" r="4.8" fill="white"/></svg>
              De folga
            </div>
          </CardContent>
        </Card>

        <!-- Lista de técnicos (clicável → fly-to) -->
        <Card class="bg-sidebar border-border flex-1 overflow-hidden flex flex-col min-h-0">
          <CardHeader class="pb-3 shrink-0">
            <CardTitle class="text-xs font-bold text-muted-foreground uppercase tracking-wider flex items-center gap-2">
              <UserCog class="w-3.5 h-3.5" />
              Técnicos ({{ tecnicosComCoords.length }})
            </CardTitle>
          </CardHeader>
          <CardContent class="pt-0 overflow-y-auto flex-1 space-y-0.5 px-3 pb-3">
            <div
              v-if="tecnicosComCoords.length === 0"
              class="text-xs text-muted-foreground/60 text-center py-6"
            >
              Nenhum técnico<br>com localização
            </div>
            <button
              v-for="t in tecnicosComCoords"
              :key="t.id"
              class="w-full flex items-center gap-2.5 py-2 px-2 rounded-lg transition-all text-left group"
              :class="[
                tecnicoSelecionadoId === t.id
                  ? 'bg-blue-500/15 ring-1 ring-blue-500/40'
                  : 'hover:bg-muted/40',
              ]"
              @click="focarTecnico(t)"
            >
              <div
                class="w-2 h-2 rounded-full shrink-0"
                :style="{ backgroundColor: dotColor(t.estado), boxShadow: `0 0 0 2px ${dotColor(t.estado)}30` }"
              />
              <div class="min-w-0 flex-1">
                <p class="text-xs font-semibold text-foreground truncate group-hover:text-blue-400 transition-colors">
                  {{ t.nome }}
                </p>
                <p class="text-[10px] text-muted-foreground truncate">{{ t.cargo || 'Técnico' }}</p>
              </div>
              <MapPin class="w-3 h-3 text-muted-foreground/40 group-hover:text-blue-400 shrink-0 transition-colors" />
            </button>
          </CardContent>
        </Card>
      </div>

      <!-- Container do mapa -->
      <div class="flex-1 rounded-xl border border-border overflow-hidden relative min-h-0">

        <!-- Loading overlay -->
        <Transition name="fade">
          <div
            v-if="loading"
            class="absolute inset-0 z-[500] flex flex-col items-center justify-center bg-background/75 backdrop-blur-sm"
          >
            <RefreshCw class="w-8 h-8 text-blue-400 animate-spin mb-3" />
            <p class="text-sm font-medium text-foreground">Carregando localizações...</p>
            <p class="text-xs text-muted-foreground mt-1">Geocodificando endereços dos clientes</p>
          </div>
        </Transition>

        <!-- Erro -->
        <Transition name="fade">
          <div
            v-if="erro && !loading"
            class="absolute inset-0 z-[400] flex items-center justify-center"
          >
            <div class="text-center">
              <XCircle class="w-10 h-10 text-red-400 mx-auto mb-2" />
              <p class="text-sm text-red-400">{{ erro }}</p>
            </div>
          </div>
        </Transition>

        <!-- Div do mapa (Leaflet renderiza aqui) -->
        <div ref="mapContainer" class="w-full h-full" />
      </div>
    </div>

  </div>
</template>

<style>
/* Remove borda padrão do DivIcon do Leaflet */
.leaflet-div-icon-custom {
  background: transparent !important;
  border: none !important;
}

/* Popups com variáveis CSS do sistema (dark/light automático) */
.mapa-popup .leaflet-popup-content-wrapper {
  background: hsl(var(--popover)) !important;
  color: hsl(var(--foreground)) !important;
  border: 1px solid hsl(var(--border)) !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  padding: 0 !important;
}

.mapa-popup .leaflet-popup-content {
  margin: 14px 16px !important;
  font-family: inherit !important;
}

.mapa-popup .leaflet-popup-tip {
  background: hsl(var(--popover)) !important;
}

.mapa-popup .leaflet-popup-close-button {
  color: hsl(var(--muted-foreground)) !important;
  top: 6px !important;
  right: 8px !important;
}

/* Controles de zoom */
.leaflet-control-zoom {
  border: 1px solid hsl(var(--border)) !important;
  border-radius: 8px !important;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2) !important;
}

.leaflet-control-zoom a {
  background: hsl(var(--sidebar-background)) !important;
  color: hsl(var(--foreground)) !important;
  border-bottom: 1px solid hsl(var(--border)) !important;
  width: 32px !important;
  height: 32px !important;
  line-height: 32px !important;
}

.leaflet-control-zoom a:hover {
  background: hsl(var(--muted)) !important;
}

/* Atribuição discreta */
.leaflet-control-attribution {
  background: hsl(var(--sidebar-background)) !important;
  color: hsl(var(--muted-foreground)) !important;
  border-radius: 6px !important;
  font-size: 10px !important;
  border: 1px solid hsl(var(--border)) !important;
  padding: 2px 6px !important;
}

.leaflet-control-attribution a {
  color: hsl(var(--sidebar-primary)) !important;
}
</style>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
