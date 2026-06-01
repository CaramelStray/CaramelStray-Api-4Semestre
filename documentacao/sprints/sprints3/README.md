# API 4º Semestre Banco de Dados

## Caramel Stray

<p align="center">
      <img src="../../../assets/logo_v2 2.png" width="50" align="center">
      <h2 align="center"> Tracker</h2>
</p>

## Documentação - Sprints


<p align="center">
  | <a href ="#desafio"> Desafio</a>  |
  <a href ="#backlog"> Backlog</a>  |
 <a href ="#aceite"> Critérios de aceite</a>  |
 <a href ="#dor"> DOR e DOD da Sprint</a>  |
  <a href ="#equipe"> Equipe</a>  |
</p>

> Status do Projeto: Em progresso  🚧
---

## 🏅 Desafio <a id="desafio"></a>

A empresa opera cerca de 100 sistemas distribuídos mundialmente, cada um exigindo manutenções periódicas conforme suas 
horas de uso. Essas intervenções variam em duração conforme a localização - por exemplo, manutenções próximas, como em sistemas 
no Rio de Janeiro levam apenas um dia, enquanto operações em regiões distantes, como na Ásia, demandam mais tempo.

## 📋 Backlog da Sprint 3 <a id="backlog"></a>

# Sprint 3 - Backlog

## Objetivo da Sprint
Desenvolver funcionalidades de visualização e planejamento operacional para apoiar gestores no acompanhamento das ordens de manutenção, disponibilidade de técnicos e localização geográfica das operações.

---

# Backlog Sprint 3 — Sistema de Gestão de Manutenções

| Rank | Prioridade | User Story | Estimativa (Fibonacci) | Sprint | Meta da Sprint |
|------|------------|-----------|------------------------|--------|-------|
| 1 | Média | Como gestor, quero visualizar o dashboard de ordens de manutenção para acompanhar o status geral das atividades | 8 | 3 | ✅ |
| 2 | Média | Como gestor, quero visualizar a disponibilidade dos técnicos em um calendário para evitar conflitos de agendamento | 5 | 3 | ✅ |
| 3 | Baixa | Como gestor, quero rastrear a localização dos técnicos no mapa para saber se estão em campo ou em terra | 8 | 3 | ✅ |
| 4 | Baixa | Como gestor, quero registrar o ciclo de embarcações para planejar manutenções enquanto estão atracadas | 8 | 3 |  |
| 5 | Baixa | Como gestor, quero visualizar no mapa a localização de todos os sistemas instalados para ter uma visão geográfica da operação | 8 | 3 |  |

---

# Critérios de Aceitação <a id="aceite"></a>

## Dashboard de Ordens
- O gestor deve visualizar a quantidade de ordens por status.
- O dashboard deve apresentar atualização correta das informações.
- Os gráficos devem ser responsivos e legíveis.

## Disponibilidade dos Técnicos
- O calendário deve exibir horários disponíveis e ocupados.
- O gestor deve conseguir visualizar conflitos de agenda.

## Localização dos Técnicos
- O mapa deve exibir técnicos em campo e em terra.
- As localizações devem ser atualizadas corretamente.
- 

---

# DoR e DoD - Sprint 3 <a id="dor"></a>

| User Story | Definition of Ready (DoR) | Definition of Done (DoD) |
|---|---|---|
| US301 - Dashboard de ordens de manutenção | - User Story refinada e aprovada pelo cliente.<br>- Protótipo do dashboard validado no Figma.<br>- Definição dos indicadores e métricas necessárias.<br>- Estrutura das APIs de dashboard documentadas no Swagger.<br>- Dados de exemplo preparados para testes dos gráficos. | - Dashboard implementado em Vue.js + Vuetify.<br>- Endpoints desenvolvidos em Spring Boot.<br>- Dados integrados corretamente ao PostgreSQL.<br>- APIs documentadas no Swagger.<br>- Testes unitários e de integração realizados.<br>- Funcionalidade validada em homologação. |
| US302 - Disponibilidade dos técnicos em calendário | - Fluxo de agenda definido com o cliente.<br>- Protótipo do calendário validado.<br>- Estrutura de disponibilidade modelada no banco.<br>- APIs de agenda especificadas.<br>- Dados mockados preparados para testes. | - Calendário funcional implementado.<br>- Disponibilidade exibida corretamente.<br>- Backend integrado ao frontend.<br>- Regras de conflito de agenda testadas.<br>- Testes automatizados executados.<br>- Funcionalidade homologada. |
| US303 - Rastreamento de técnicos no mapa | - Requisitos de localização definidos.<br>- Biblioteca de mapas selecionada.<br>- Modelo de dados geográficos definido.<br>- APIs de localização documentadas.<br>- Critérios de atualização de posição aprovados. | - Mapa exibindo técnicos em tempo real.<br>- Backend fornecendo localização corretamente.<br>- Integração frontend/backend concluída.<br>- Testes de atualização de localização realizados.<br>- Documentação técnica atualizada.<br>- Deploy validado. |


## 🎓 Equipe <a id="equipe"></a>

<div align="center">
  <table>
    <tr>
      <th>Membro</th>
      <th>Função</th>
      <th>Github</th>
      <th>Linkedin</th>
    </tr>
    <tr>
      <td>Giovana Zucareli</td>
      <td>Product Owner</td>
      <td><a href="https://github.com/GiovanaZucareli"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/giovana-zucareli-1aa205202/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Patricia Moraes</td>
      <td>Scrum Master</td>
      <td><a href="https://github.com/Patriciamvs7"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/patricia-santos-bigdata/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Daniel Lima</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/DanielLimaCpy"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/daniel-lima-637648179/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Thomas Heinrich</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/HeinrichThomas"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/thomas-g-heinrich/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>William Honda</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/Lunix800"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/william-honda-965138191/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Pedro Ribeiro</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/pedrohenribeiro"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/pedrohenribeiro1?utm_source=share_via&utm_content=profile&utm_medium=member_ios"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>      
    </tr>
     <tr>
      <td>Thor Lyndgaard</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/thorlyndgaard"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/thor-lyndgaard-b2155826b/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>      
    </tr>
      <td>Guilherme Bezerra</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/GuilhermebJunqueira"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/guilherme-bezerra-a01035170/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td> 
  </table>
</div>
