# API 4º Semestre Banco de Dados

## Caramel Stray

<p align="center">
      <img src="../../assets/logo_v2 2.svg" width="50" align="center">
      <h2 align="center"> Tracker</h2>
</p>

## Documentação - Sprints


<p align="center">
  | <a href ="#desafio"> Desafio</a>  |
      <a href ="#dod"> DOD e DOR</a> |
  <a href ="#sprints"> Sprints Planejadas</a>  |
  <a href ="#equipe"> Equipe</a>  |
</p>

> Status do Projeto: Em progresso  🚧
---

## 🏅 Desafio <a id="desafio"></a>

A empresa opera cerca de 100 sistemas distribuídos mundialmente, cada um exigindo manutenções periódicas conforme suas 
horas de uso. Essas intervenções variam em duração conforme a localização - por exemplo, manutenções próximas, como em sistemas 
no Rio de Janeiro levam apenas um dia, enquanto operações em regiões distantes, como na Ásia, demandam mais tempo.

---

## ✅ Definition of Done (DoD) – Tracker <a id="dod"></a>

- Código backend desenvolvido em **Spring Boot** versionado no repositório principal.  
- Banco de dados (**PostgreSQL**) atualizado com **scripts versionados (Flyway)**.  
- Frontend implementado em **Vue.js + Vuetify**, responsivo e integrado ao backend.  
- Autenticação e autorização implementadas via **JWT**, validadas em **Postman**.  
- Testes unitários e de integração com cobertura mínima de **80%** nas regras críticas.  
- Endpoints REST documentados com **Swagger**.  
- Interfaces confirmadas no **Figma** antes da implementação.  
- Funcionalidade validada em ambiente de **homologação** com dados reais de teste.  
- Deploy automatizado testado (build + banco + API + frontend funcionando em conjunto).  
- Documentação de uso e técnica atualizada (README ou Wiki).  


---

## ✅ Definition of Ready (DoR) – Tracker

- User Story validada e compreendida pelo time técnico.  
- Critérios de aceitação claros, objetivos e documentados.  
- Protótipos/Wireframes definidos no **Figma** e revisados com o cliente.  
- Modelagem de entidades e relacionamentos definida em **PostgreSQL**.  
- Contratos de API descritos no **Swagger**.  
- Dependências externas identificadas (bibliotecas, integrações).  
- Estratégia de autenticação/autorização via **JWT** definida.  
- Dados de exemplo ou **mocks** prontos para testes.  
- Estimativa de esforço registrada pelo time.  
- Reunião de refinamento realizada com aprovação do cliente. 

---

## 📋 Sprints Planejadas <a id="sprints"></a>

# 🗓️ Sprints Planejadas — Sistema de Gestão de Manutenções

**Parceiro:** Altave  
**Curso:** 4º Banco de Dados  
**API:** 2026-1  
**Fatec SJC**



## 🚀 Sprint 1 – Planejada

| ID   | User Story | Prioridade | Status | Backlog Completo | DoR Atendido | DoD Atendido | Requisito Referenciado |
|------|------------|------------|--------|------------------|--------------|--------------|------------------------|
| S1-1 | Como gestor, quero criar uma ordem de manutenção vinculada a um sistema e a um contrato para iniciar o planejamento do atendimento | Alta | ✅ Concluída | ✅ | ✅ | ✅ | [1] Criação de ordem de manutenção |
| S1-2 | Como gestor, quero cadastrar sistemas instalados nos clientes para registrar os equipamentos sob contrato de manutenção | Média | ✅ Concluída | ✅ | ✅ | ✅ | [2] Cadastro de sistemas |
| S1-3 | Como gestor, quero cadastrar contratos de manutenção vinculados a clientes e sistemas para definir intervalos e SLAs de atendimento | Média | ✅ Concluída | ✅ | ✅ | ✅ | [3] Cadastro de contratos |
| S1-4 | Como gestor, quero cadastrar clientes com informações de localização para saber onde cada sistema está instalado | Baixa | ✅ Concluída | ✅ | ✅ | ✅ | [4] Cadastro de clientes |
| S1-5 | Como gestor, quero cadastrar técnicos com suas competências para saber quem está apto a atender cada chamado | Baixa | ✅ Concluída | ✅ | ✅ | ✅ | [5] Cadastro de técnicos |

---

## 🚀 Sprint 2 – Planejada

| ID   | User Story | Prioridade | Status | Backlog Completo | DoR Atendido | DoD Atendido | Requisito Referenciado |
|------|------------|------------|--------|------------------|--------------|--------------|------------------------|
| S2-1 | Como técnico, quero registrar os dados da execução da manutenção para documentar o que foi realizado na visita | Alta | ✅ Concluída | ✅ | ✅ | ✅ | [6] Registro de execução da manutenção |
| S2-2 | Como gestor, quero definir a criticidade de uma ordem de manutenção para organizar os atendimentos por impacto | Alta | ✅ Concluída | ✅ | ✅ | ✅ | [7] Criticidade de ordem de manutenção |
| S2-3 | Como gestor, quero registrar viagens de manutenção associadas a ordens para planejar o deslocamento dos técnicos | Alta | ✅ Concluída | ✅ | ✅ | ✅ | [8] Registro de viagens de manutenção |
| S2-4 | Como gestor, quero visualizar o dashboard de ordens de manutenção para acompanhar o status geral das atividades | Média | Concluída | ✅ | ✅ | ✅ | [9] Dashboard de ordens |
| S2-5 | Como gestor, quero aplicar um checklist de preparação antes da viagem para garantir que ferramentas e equipamentos corretos sejam levados | Média | Concluída | ✅ | ✅ | ✅ | [10] Checklist de preparação |
| S2-6 | Como técnico, quero acessar o checklist da manutenção durante a visita para garantir que todos os procedimentos foram seguidos | Média | Concluída | ✅ | ✅ | ✅ | [11] Checklist da manutenção |
| S2-7 | Como gestor, quero visualizar a disponibilidade dos técnicos em um calendário para evitar conflitos de agendamento | Baixa | Concluída | ✅ | ✅ | ✅ | [12] Calendário de disponibilidade |

---

## 🚀 Sprint 3 – Planejada

| ID   | User Story | Prioridade | Status | Backlog Completo | DoR Atendido | DoD Atendido | Requisito Referenciado |
|------|------------|------------|--------|------------------|--------------|--------------|------------------------|
| S3-1 | Como gestor, quero acessar o histórico de manutenções de um sistema para rastrear todas as intervenções realizadas | Alta | Concluída | ✅ | ✅ | ✅ | [13] Histórico de manutenções |
| S3-2 | Como gestor, quero visualizar um relatório de manutenções vencidas e próximas do vencimento para evitar descumprimento de contratos | Alta | Concluída | ✅ | ✅ | ✅ | [14] Relatório de manutenções |
| S3-3 | Como gestor, quero cadastrar as máquinas da empresa para ter um controle centralizado do inventário próprio | Média | Concluída | ✅ | ✅ | ✅ | [15] Cadastro de máquinas |
| S3-4 | Como gestor, quero registrar o ciclo de embarcações para planejar manutenções enquanto estão atracadas | Média | 🔜 Planejada | ✅ | ✅ | ❌ | [16] Registro de ciclo de embarcações |
| S3-5 | Como gestor, quero cadastrar os ativos da empresa para ter um controle centralizado do inventário próprio | Média | Concluída | ✅ | ✅ | ✅ | [17] Cadastro de ativos |
| S3-6 | Como gestor, quero visualizar no mapa a localização de todos os sistemas instalados para ter uma visão geográfica da operação | Baixa | 🔜 Planejada | ✅ | ✅ | ❌ | [18] Mapa de sistemas instalados |
| S3-7 | Como gestor, quero rastrear a localização dos técnicos no mapa para saber se estão em campo ou em terra | Baixa | Concluída | ✅ | ✅ | ✅ | [19] Rastreamento de técnicos no mapa |

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

