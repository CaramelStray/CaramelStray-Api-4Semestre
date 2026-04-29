<template>
  <main class="page">
    <header class="page-header">
      <div class="header-left">
        <span class="divider"></span>
        <h1>Relatório de Manutenções</h1>
      </div>
    </header>

    <section class="cards">
      <div class="card">
        <div class="card-top">
          <span>TOTAL DE MANUTENÇÕES</span>
          <span class="icon blue">▣</span>
        </div>
        <strong>{{ manutencoes.length }}</strong>
        <small>Cadastradas no relatório</small>
      </div>

      <div class="card">
        <div class="card-top">
          <span>VENCIDAS</span>
          <span class="icon red">⚠</span>
        </div>
        <strong>{{ vencidas.length }}</strong>
        <small>Prazo expirado</small>
      </div>

      <div class="card">
        <div class="card-top">
          <span>PRÓXIMAS DO VENCIMENTO</span>
          <span class="icon yellow">◷</span>
        </div>
        <strong>{{ proximas.length }}</strong>
        <small>Dentro dos próximos 30 dias</small>
      </div>

      <div class="card">
        <div class="card-top">
          <span>CONCLUÍDAS</span>
          <span class="icon purple">✓</span>
        </div>
        <strong>{{ concluidas.length }}</strong>
        <small>Finalizadas</small>
      </div>
    </section>

    <section class="toolbar">
      <div class="search-box">
        <span>⌕</span>
        <input
          v-model="busca"
          type="text"
          placeholder="Buscar por código, tipo, status ou criticidade..."
        />
      </div>

      <button class="pdf-button">
        <span>＋</span>
        GERAR PDF
      </button>
    </section>

    <section class="table-card">
      <div class="table-title">Manutenções Vencidas</div>

      <table>
        <thead>
          <tr>
            <th>CÓDIGO</th>
            <th>TIPO</th>
            <th>CRITICIDADE</th>
            <th>STATUS</th>
            <th>VENCIMENTO</th>
            <th>OBSERVAÇÃO</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in vencidasFiltradas" :key="item.codigo">
            <td>#{{ item.codigo }}</td>
            <td>{{ item.tipo }}</td>
            <td>
              <span :class="['badge', classeCriticidade(item.criticidade)]">
                {{ item.criticidade }}
              </span>
            </td>
            <td>
              <span class="status-dot"></span>
              {{ item.status }}
            </td>
            <td>{{ formatarData(item.vencimento) }}</td>
            <td>{{ item.observacao }}</td>
          </tr>

          <tr v-if="vencidasFiltradas.length === 0">
            <td colspan="6" class="empty">Nenhuma manutenção vencida encontrada.</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="table-card">
      <div class="table-title">Próximas do Vencimento - 30 dias</div>

      <table>
        <thead>
          <tr>
            <th>CÓDIGO</th>
            <th>TIPO</th>
            <th>CRITICIDADE</th>
            <th>STATUS</th>
            <th>VENCIMENTO</th>
            <th>OBSERVAÇÃO</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in proximasFiltradas" :key="item.codigo">
            <td>#{{ item.codigo }}</td>
            <td>{{ item.tipo }}</td>
            <td>
              <span :class="['badge', classeCriticidade(item.criticidade)]">
                {{ item.criticidade }}
              </span>
            </td>
            <td>
              <span class="status-dot"></span>
              {{ item.status }}
            </td>
            <td>{{ formatarData(item.vencimento) }}</td>
            <td>{{ item.observacao }}</td>
          </tr>

          <tr v-if="proximasFiltradas.length === 0">
            <td colspan="6" class="empty">Nenhuma manutenção próxima encontrada.</td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface ManutencaoRelatorio {
  codigo: number
  tipo: string
  criticidade: string
  status: string
  vencimento: string
  observacao: string
}

const busca = ref('')

const manutencoes = ref<ManutencaoRelatorio[]>([
  {
    codigo: 1,
    tipo: 'Preventiva',
    criticidade: 'ALTA',
    status: 'PENDENTE',
    vencimento: '2026-04-10',
    observacao: 'Checklist preventivo pendente'
  },
  {
    codigo: 2,
    tipo: 'Corretiva',
    criticidade: 'MEDIA',
    status: 'AGENDADO',
    vencimento: '2026-05-05',
    observacao: 'Verificar componentes da máquina'
  },
  {
    codigo: 3,
    tipo: 'Preventiva',
    criticidade: 'BAIXA',
    status: 'CONCLUIDA',
    vencimento: '2026-04-20',
    observacao: 'Manutenção finalizada'
  }
])

const hoje = new Date()
const limite = new Date()
limite.setDate(hoje.getDate() + 30)

const vencidas = computed(() =>
  manutencoes.value.filter(item => new Date(item.vencimento) < hoje)
)

const proximas = computed(() =>
  manutencoes.value.filter(item => {
    const data = new Date(item.vencimento)
    return data >= hoje && data <= limite
  })
)

const concluidas = computed(() =>
  manutencoes.value.filter(item => item.status === 'CONCLUIDA')
)

const vencidasFiltradas = computed(() =>
  filtrar(vencidas.value)
)

const proximasFiltradas = computed(() =>
  filtrar(proximas.value)
)

function filtrar(lista: ManutencaoRelatorio[]) {
  const termo = busca.value.toLowerCase()

  return lista.filter(item =>
    String(item.codigo).includes(termo) ||
    item.tipo.toLowerCase().includes(termo) ||
    item.status.toLowerCase().includes(termo) ||
    item.criticidade.toLowerCase().includes(termo)
  )
}

function formatarData(data: string) {
  return new Date(data).toLocaleDateString('pt-BR')
}

function classeCriticidade(criticidade: string) {
  if (criticidade === 'ALTA') return 'danger'
  if (criticidade === 'MEDIA') return 'warning'
  return 'success'
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #060b14;
  color: #e5eefc;
  padding: 32px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 48px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.divider {
  width: 1px;
  height: 28px;
  background: #22314a;
}

h1 {
  font-size: 16px;
  font-weight: 700;
  margin: 0;
}

.cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 26px;
}

.card {
  background: #0d1320;
  border: 1px solid #243755;
  border-radius: 10px;
  padding: 28px;
  min-height: 120px;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #8fb0dc;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.7px;
  margin-bottom: 34px;
}

.card strong {
  display: block;
  font-size: 32px;
  color: #ffffff;
  margin-bottom: 8px;
}

.card small {
  color: #8fb0dc;
  font-size: 12px;
}

.icon.blue {
  color: #3b82f6;
}

.icon.red {
  color: #ef4444;
}

.icon.yellow {
  color: #fbbf24;
}

.icon.purple {
  color: #a855f7;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.search-box {
  flex: 1;
  height: 54px;
  background: #0d1320;
  border: 1px solid #243755;
  border-radius: 6px;
  display: flex;
  align-items: center;
  padding: 0 18px;
  gap: 12px;
  color: #8fb0dc;
}

.search-box input {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  color: #dbeafe;
  font-size: 15px;
}

.search-box input::placeholder {
  color: #8fb0dc;
}

.pdf-button {
  height: 54px;
  padding: 0 28px;
  border: none;
  border-radius: 6px;
  background: #2563eb;
  color: white;
  font-size: 12px;
  font-weight: 800;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-card {
  border: 1px solid #243755;
  background: #0d1320;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 26px;
}

.table-title {
  padding: 20px;
  color: #9bb7df;
  border-bottom: 1px solid #243755;
  font-size: 14px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  border-bottom: 1px solid #243755;
}

th {
  text-align: left;
  padding: 18px 26px;
  font-size: 12px;
  color: #ffffff;
  font-weight: 800;
}

td {
  padding: 20px 26px;
  color: #9bb7df;
  font-size: 14px;
  border-bottom: 1px solid #162235;
}

tbody tr:hover {
  background: #101a2b;
}

.badge {
  font-weight: 800;
  font-size: 12px;
}

.badge.danger {
  color: #ef4444;
}

.badge.warning {
  color: #fbbf24;
}

.badge.success {
  color: #22c55e;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #3b82f6;
  border-radius: 50%;
  display: inline-block;
  margin-right: 8px;
}

.empty {
  text-align: center;
  color: #8fb0dc;
  padding: 42px;
}
</style>