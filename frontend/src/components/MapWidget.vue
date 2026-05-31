<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

import { clienteService, type ClienteResponseDTO } from '@/services/clienteService'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

// ─── Tiles CARTO (gratuito, sem API key, sem WebGL) ──────────────────────────
const DARK_TILES = 'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'
const LIGHT_TILES = 'https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png'
const ATTRIBUTION = '&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a> &copy; <a href="https://carto.com">CARTO</a>'

const POLLING_INTERVAL_SECONDS = 30
const SEM_SINAL_SECONDS = 60

type StatusTecnicoMapa = 'DISPONIVEL' | 'EM_CAMPO' | 'FOLGA' | 'SEM_SINAL' | 'DESCONHECIDO'

type TecnicoMapa = TecnicoResponseDTO & {
  ultimaAtualizacao?: string
  atualizadoEm?: string
  ultimaLocalizacaoEm?: string
  dataHoraLocalizacao?: string
  lastSeenAt?: string
}

const mapContainer = ref<HTMLDivElement | null>(null)
let mapInstance: L.Map | null = null
let tileLayer: L.TileLayer | null = null

const clientes = ref<ClienteResponseDTO[]>([])
const tecnicos = ref<TecnicoMapa[]>([])

let pollingTimer: ReturnType<typeof setInterval> | null = null

const markersClientes: L.Marker[] = []
const markersTecnicos: Record<number, L.Marker> = {}
const geocodeCache = new Map<string, [number, number] | null>()

async function geocodeEndereco(rua: string, numero: string, cidade: string, estado: string, pais: string): Promise<[number, number] | null> {
  const partes = [rua && numero ? `${rua} ${numero}` : rua, cidade, estado, pais].filter(Boolean).join(', ')
  const key = partes.toLowerCase()
  if (geocodeCache.has(key)) return geocodeCache.get(key)!
  try {
    const query = encodeURIComponent(partes)
    const res = await fetch(`https://nominatim.openstreetmap.org/search?q=${query}&format=json&limit=1`, { headers: { 'Accept-Language': 'pt-BR' } })
    const data = await res.json()
    if (data.length > 0) {
      const coords: [number, number] = [parseFloat(data[0].lat), parseFloat(data[0].lon)]
      geocodeCache.set(key, coords)
      return coords
    }
  } catch {}
  geocodeCache.set(key, null)
  return null
}

function obterDataUltimoSinal(t: TecnicoMapa): Date | null {
  const valor = t.ultimaAtualizacao ?? t.atualizadoEm ?? t.ultimaLocalizacaoEm ?? t.dataHoraLocalizacao ?? t.lastSeenAt
  if (!valor) return null
  const data = new Date(valor)
  return Number.isNaN(data.getTime()) ? null : data
}

function estaSemSinal(t: TecnicoMapa): boolean {
  const dataUltimoSinal = obterDataUltimoSinal(t)
  if (!dataUltimoSinal) return false
  const diferencaSegundos = Math.floor((Date.now() - dataUltimoSinal.getTime()) / 1000)
  return diferencaSegundos > SEM_SINAL_SECONDS
}

function statusTecnicoMapa(t: TecnicoMapa): StatusTecnicoMapa {
  if (!t.latitude || !t.longitude) return 'DESCONHECIDO'
  if (estaSemSinal(t)) return 'SEM_SINAL'
  const estado = t.estado?.toUpperCase()
  const disponibilidade = t.disponibilidade?.toUpperCase()
  if (estado === 'EM CAMPO' || disponibilidade === 'EM CAMPO') return 'EM_CAMPO'
  if (estado === 'FOLGA' || disponibilidade === 'FOLGA') return 'FOLGA'
  if (estado === 'DISPONÍVEL' || estado === 'DISPONIVEL') return 'DISPONIVEL'
  if (disponibilidade === 'DISPONÍVEL' || disponibilidade === 'DISPONIVEL') return 'DISPONIVEL'
  return 'DESCONHECIDO'
}

function textoStatusTecnico(status: StatusTecnicoMapa): string {
  const textos: Record<StatusTecnicoMapa, string> = { DISPONIVEL: 'Disponível', EM_CAMPO: 'Em campo', FOLGA: 'De folga', SEM_SINAL: 'Sem sinal', DESCONHECIDO: 'Desconhecido' }
  return textos[status]
}

function corStatusTecnico(status: StatusTecnicoMapa): string {
  const cores: Record<StatusTecnicoMapa, string> = { DISPONIVEL: '#10b981', EM_CAMPO: '#3b82f6', FOLGA: '#64748b', SEM_SINAL: '#ef4444', DESCONHECIDO: '#f59e0b' }
  return cores[status]
}

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
    iconSize: [30, 38], iconAnchor: [15, 38], popupAnchor: [0, -42],
  })
}

function iniciais(nome: string): string {
  const partes = nome.trim().split(/\s+/).filter(Boolean)
  if (partes.length === 0) return '?'
  const primeiro = partes[0]!
  if (partes.length === 1) return primeiro.substring(0, 2).toUpperCase()
  const ultimo = partes[partes.length - 1]!
  return (primeiro[0]! + ultimo[0]!).toUpperCase()
}

function iconeTecnico(t: TecnicoMapa): L.DivIcon {
  const status = statusTecnicoMapa(t)
  const cor = corStatusTecnico(status)
  const sombra = 'filter:drop-shadow(0 3px 6px rgba(0,0,0,0.35));'
  const txt = iniciais(t.nome)
  const semSinalIcon = status === 'SEM_SINAL' ? `<circle cx="37" cy="12" r="7" fill="#ef4444" stroke="white" stroke-width="2"/><text x="37" y="13" text-anchor="middle" dominant-baseline="central" fill="white" font-size="10" font-weight="800" font-family="system-ui,-apple-system,sans-serif">!</text>` : ''
  const desconhecidoIcon = status === 'DESCONHECIDO' ? `<circle cx="37" cy="12" r="7" fill="#f59e0b" stroke="white" stroke-width="2"/><text x="37" y="13" text-anchor="middle" dominant-baseline="central" fill="white" font-size="10" font-weight="800" font-family="system-ui,-apple-system,sans-serif">?</text>` : ''
  return L.divIcon({
    html: `<svg width="48" height="64" viewBox="0 0 48 64" xmlns="http://www.w3.org/2000/svg" style="${sombra}" overflow="visible">
      <circle cx="24" cy="25" r="22" fill="white"/>
      <polygon points="13,44 35,44 24,64" fill="white"/>
      <polygon points="16,43 32,43 24,61" fill="${cor}"/>
      <circle cx="24" cy="25" r="20" fill="${cor}"/>
      <text x="24" y="25" text-anchor="middle" dominant-baseline="central" fill="white" font-size="14" font-weight="700" font-family="system-ui,-apple-system,sans-serif" letter-spacing="0.5">${txt}</text>
      ${semSinalIcon}
      ${desconhecidoIcon}
    </svg>`,
    className: 'leaflet-div-icon-custom',
    iconSize: [48, 64], iconAnchor: [24, 64], popupAnchor: [0, -68],
  })
}

function popupCliente(c: ClienteResponseDTO): string {
  const statusBg = c.ativo ? '#d1fae5' : '#fee2e2'
  const statusTxt = c.ativo ? '#065f46' : '#991b1b'
  const endereco = [c.rua, c.numero, c.cidade, c.estadoRegiao, c.pais].filter(Boolean).join(', ')
  return `<div style="min-width:200px;font-size:13px">
    <div style="display:flex;align-items:center;gap:7px;margin-bottom:8px">
      <div style="width:9px;height:9px;border-radius:50%;background:${c.ativo ? '#10b981' : '#ef4444'};flex-shrink:0"></div>
      <b style="font-size:14px">${c.nomeEmpresa}</b>
    </div>
    <p style="margin:3px 0;opacity:0.8">📍 ${endereco || '—'}</p>
    <div style="margin-top:8px;display:flex;gap:5px;flex-wrap:wrap">
      <span style="padding:2px 8px;border-radius:4px;font-size:10px;font-weight:600;background:${statusBg};color:${statusTxt}">${c.ativo ? 'Ativo' : 'Inativo'}</span>
    </div>
  </div>`
}

function popupTecnico(t: TecnicoMapa): string {
  const status = statusTecnicoMapa(t)
  const cor = corStatusTecnico(status)
  const texto = textoStatusTecnico(status)
  return `<div style="min-width:200px;font-size:13px">
    <b style="font-size:14px;display:block;margin-bottom:8px">${t.nome}</b>
    <p style="margin:3px 0;opacity:0.8">💼 ${t.cargo || 'Técnico'}</p>
    <span style="display:inline-block;margin-top:8px;padding:2px 8px;border-radius:4px;font-size:10px;font-weight:600;background:${cor}22;color:${cor}">${texto}</span>
  </div>`
}

function limparMarcadores() {
  markersClientes.forEach(m => m.remove()); markersClientes.splice(0)
  Object.values(markersTecnicos).forEach(m => m.remove())
  for (const key in markersTecnicos) delete markersTecnicos[key]
}

async function carregarDados() {
  limparMarcadores()
  try {
    const [clientesData, tecnicosData] = await Promise.all([clienteService.listar(), tecnicoService.listar()])
    clientes.value = clientesData
    tecnicos.value = tecnicosData as TecnicoMapa[]

    if (!mapInstance) return

    for (const c of clientes.value) {
      if (!c.cidade) continue
      const coords = await geocodeEndereco(c.rua, c.numero, c.cidade, c.estadoRegiao, c.pais)
      if (!coords) continue
      const popup = L.popup({ className: 'mapa-popup', offset: [0, -4] }).setContent(popupCliente(c))
      const marker = L.marker(coords, { icon: iconeCliente(c.ativo) }).bindPopup(popup).addTo(mapInstance)
      markersClientes.push(marker)
      await new Promise(r => setTimeout(r, 100)) // delay to avoid rate limit
    }

    adicionarMarcadoresTecnicos()
    
    // Fit bounds
    const bounds: [number, number][] = []
    markersClientes.forEach(m => bounds.push([m.getLatLng().lat, m.getLatLng().lng]))
    Object.values(markersTecnicos).forEach(m => bounds.push([m.getLatLng().lat, m.getLatLng().lng]))
    if (bounds.length && mapInstance) {
        mapInstance.fitBounds(bounds as any, { padding: [50, 50], maxZoom: 13 })
    }

  } catch (e) {
    console.warn('Erro ao carregar mapa:', e)
  }
}

function adicionarMarcadoresTecnicos() {
  if (!mapInstance) return
  Object.values(markersTecnicos).forEach(m => m.remove())
  for (const key in markersTecnicos) delete markersTecnicos[key]

  for (const t of tecnicos.value) {
    if (!t.latitude || !t.longitude) continue
    const popup = L.popup({ className: 'mapa-popup', offset: [0, -4] }).setContent(popupTecnico(t))
    const marker = L.marker([t.latitude, t.longitude], { icon: iconeTecnico(t) }).bindPopup(popup).addTo(mapInstance)
    markersTecnicos[t.id] = marker
  }
}

async function atualizarPosicoesTecnicos() {
  try {
    const tecnicosData = await tecnicoService.listar()
    tecnicos.value = tecnicosData as TecnicoMapa[]
    adicionarMarcadoresTecnicos()
  } catch (e) {}
}

watch(() => themeStore.isDark, (isDark) => {
  if (!mapInstance || !tileLayer) return
  tileLayer.remove()
  tileLayer = L.tileLayer(isDark ? DARK_TILES : LIGHT_TILES, { attribution: ATTRIBUTION, maxZoom: 19, subdomains: 'abcd' }).addTo(mapInstance)
})

onMounted(() => {
  if (!mapContainer.value) return
  mapInstance = L.map(mapContainer.value, { center: [-15.7801, -47.9292], zoom: 4, zoomControl: false })
  tileLayer = L.tileLayer(themeStore.isDark ? DARK_TILES : LIGHT_TILES, { attribution: ATTRIBUTION, maxZoom: 19, subdomains: 'abcd' }).addTo(mapInstance)
  L.control.zoom({ position: 'bottomright' }).addTo(mapInstance)

  setTimeout(() => mapInstance?.invalidateSize(), 100)

  carregarDados()
  pollingTimer = setInterval(atualizarPosicoesTecnicos, POLLING_INTERVAL_SECONDS * 1000)
})

onUnmounted(() => {
  if (pollingTimer) clearInterval(pollingTimer)
  limparMarcadores()
  mapInstance?.remove()
  mapInstance = null
})
</script>

<template>
  <div class="map-widget-root">
    <div ref="mapContainer" class="map-widget-canvas"></div>
    <div class="map-widget-legend" aria-hidden>
      <div class="legend-title">Legenda</div>
      <div class="legend-item">
        <svg width="13" height="17" viewBox="0 0 30 38" class="shrink-0"><path d="M15 0C6.716 0 0 6.716 0 15c0 10.314 15 23 15 23S30 25.314 30 15C30 6.716 23.284 0 15 0z" fill="#3b82f6" /><circle cx="15" cy="15" r="6.5" fill="white" /><circle cx="15" cy="15" r="3.5" fill="#3b82f6" /></svg>
        <span>Cliente ativo</span>
      </div>
      <div class="legend-item">
        <svg width="13" height="17" viewBox="0 0 30 38" class="shrink-0"><path d="M15 0C6.716 0 0 6.716 0 15c0 10.314 15 23 15 23S30 25.314 30 15C30 6.716 23.284 0 15 0z" fill="#94a3b8" /><circle cx="15" cy="15" r="6.5" fill="white" /><circle cx="15" cy="15" r="3.5" fill="#94a3b8" /></svg>
        <span>Cliente inativo</span>
      </div>
      <div class="legend-sep" />
      <div class="legend-item"><span class="dot dot-available shrink-0" /> Disponível</div>
      <div class="legend-item"><span class="dot dot-field shrink-0" /> Em campo</div>
      <div class="legend-item"><span class="dot dot-off shrink-0" /> De folga</div>
      <div class="legend-item"><span class="dot dot-nosignal shrink-0" /> Sem sinal</div>
      <div class="legend-item"><span class="dot dot-unknown shrink-0" /> Desconhecido</div>
    </div>
  </div>
</template>

<style scoped>
.map-widget-root {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 400px;
  border-radius: 8px;
  overflow: hidden;
}
.map-widget-canvas {
  width: 100%;
  height: 100%;
  z-index: 10;
}
.map-widget-legend {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
  background: hsl(var(--card));
  color: hsl(var(--foreground));
  padding: 12px 14px;
  border-radius: 10px;
  font-size: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  border: 1px solid hsl(var(--border));
  width: auto;
  min-width: 140px;
}
.map-widget-legend .legend-title {
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 8px;
  color: hsl(var(--muted-foreground));
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.map-widget-legend .legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 6px 0;
  font-size: 12px;
}
.map-widget-legend .legend-sep {
  height: 1px;
  background: hsl(var(--border));
  margin: 8px 0;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}
.dot-available { background: #10b981; }
.dot-field { background: #3b82f6; }
.dot-off { background: #64748b; }
.dot-nosignal { background: #ef4444; }
.dot-unknown { background: #f59e0b; }
</style>

<style>
.leaflet-div-icon-custom {
  background: transparent !important;
  border: none !important;
}

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

.leaflet-control-zoom {
  border: 1px solid hsl(var(--border)) !important;
  border-radius: 8px !important;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2) !important;
}

.leaflet-control-zoom a {
  background: hsl(var(--card)) !important;
  color: hsl(var(--foreground)) !important;
  border-bottom: 1px solid hsl(var(--border)) !important;
  width: 32px !important;
  height: 32px !important;
  line-height: 32px !important;
}

.leaflet-control-zoom a:hover {
  background: hsl(var(--muted)) !important;
}

.leaflet-control-attribution {
  background: hsl(var(--card)) !important;
  color: hsl(var(--muted-foreground)) !important;
  border-radius: 6px !important;
  font-size: 10px !important;
  border: 1px solid hsl(var(--border)) !important;
  padding: 2px 6px !important;
}

.leaflet-control-attribution a {
  color: hsl(var(--primary)) !important;
}
</style>
