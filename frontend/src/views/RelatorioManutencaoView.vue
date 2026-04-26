<template>
  <div class="container">
    <h1>Relatório de Manutenções</h1>

    <!-- 🔷 CARDS -->
    <div class="cards">
      <div class="card">
        <span>Total</span>
        <h2>{{ todas.length }}</h2>
      </div>

      <div class="card danger">
        <span>Vencidas</span>
        <h2>{{ vencidas.length }}</h2>
      </div>

      <div class="card warning">
        <span>Próximas</span>
        <h2>{{ proximas.length }}</h2>
      </div>
    </div>

    <!-- 🔴 VENCIDAS -->
    <section>
      <h2 class="vencidas">Manutenções Vencidas</h2>

      <table v-if="vencidas.length">
        <thead>
          <tr>
            <th>Código</th>
            <th>Tipo</th>
            <th>Status</th>
            <th>Criticidade</th>
            <th>Vencimento</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in vencidas" :key="item.codigo">
            <td>#{{ item.codigo }}</td>
            <td>{{ item.tipoManutencao?.descricao }}</td>
            <td><span class="badge status">{{ item.status }}</span></td>
            <td><span class="badge crit">{{ item.criticidade }}</span></td>
            <td>{{ formatarData(item.vencimento) }}</td>
          </tr>
        </tbody>
      </table>

      <p v-else class="empty">Nenhuma manutenção vencida</p>
    </section>

    <!-- 🟡 PRÓXIMAS -->
    <section>
      <h2 class="proximas">Próximas do Vencimento (30 dias)</h2>

      <table v-if="proximas.length">
        <thead>
          <tr>
            <th>Código</th>
            <th>Tipo</th>
            <th>Status</th>
            <th>Criticidade</th>
            <th>Vencimento</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in proximas" :key="item.codigo">
            <td>#{{ item.codigo }}</td>
            <td>{{ item.tipoManutencao?.descricao }}</td>
            <td><span class="badge status">{{ item.status }}</span></td>
            <td><span class="badge crit">{{ item.criticidade }}</span></td>
            <td>{{ formatarData(item.vencimento) }}</td>
          </tr>
        </tbody>
      </table>

      <p v-else class="empty">Nenhuma manutenção próxima</p>
    </section>
  </div>
</template>

<style scoped>
.container {
  padding: 24px;
  background: #f5f6fa;
  min-height: 100vh;
}

h1 {
  margin-bottom: 20px;
}

/* 🔷 CARDS */
.cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.card {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.card span {
  color: #666;
}

.card h2 {
  margin-top: 10px;
  font-size: 28px;
}

.card.danger {
  border-left: 5px solid red;
}

.card.warning {
  border-left: 5px solid orange;
}

/* 🔴 TÍTULOS */
.vencidas {
  color: red;
}

.proximas {
  color: goldenrod;
}

/* 📊 TABELA */
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
  background: white;
  border-radius: 10px;
  overflow: hidden;
}

th {
  background: #f1f2f6;
  text-align: left;
  padding: 12px;
}

td {
  padding: 12px;
  border-top: 1px solid #eee;
}

tr:hover {
  background: #fafafa;
}

/* 🏷️ BADGES */
.badge {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: bold;
}

.status {
  background: #dfe6e9;
}

.crit {
  background: #ffeaa7;
}

/* ❌ EMPTY */
.empty {
  margin-top: 10px;
  color: #999;
}
</style>