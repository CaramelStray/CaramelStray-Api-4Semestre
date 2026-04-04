# API 4º Semestre Banco de Dados

## Caramel Stray

<p align="center">
      <img src="./assets/logo_v2 2.png" width="50" align="center">
      <h2 align="center"> Tracker</h2>
</p>

<p align="center">
  | <a href ="#desafio"> Desafio</a>  |
  <a href ="#solucao"> Solução</a>  |
  <a href ="#backlog"> Backlog do Produto</a>  |
  <a href ="#sprint"> Cronograma de Sprints</a>  | <a href ="#sprint">Product backlog -> DOR e DOD</a> |
  <a href ="#tecnologias">Tecnologias</a> |  <a href ="#figma">Figma</a> |
  <a href ="#branchs">Estrutura de Branchs</a>  |
  <a href ="#equipe"> Equipe</a> | <a href ="#manual"> Manual do usuário</a>
</p>

> Status do Projeto: Em progresso  🚧
>
>
> Pasta de Documentação:  📄
>
> Video do Projeto:  Em progresso  📽️

## 🏅 Desafio <a id="desafio"></a>

A empresa opera cerca de 100 sistemas distribuídos mundialmente, cada um exigindo manutenções periódicas conforme suas horas de uso. Essas intervenções variam em duração conforme a localização - por exemplo, manutenções próximas, como em sistemas no Rio de Janeiro levam apenas um dia, enquanto operações em regiões distantes, como na Ásia, demandam mais tempo.

## 🏅 Solução <a id="solucao"></a>

A solução consiste em um Sistema de Gestão de Manutenções que centraliza o cadastro de clientes, sistemas, contratos, ativos e técnicos, permitindo planejar e registrar ordens de manutenção com base nas horas de uso dos equipamentos. O sistema organiza viagens e checklists para otimizar o atendimento considerando a localização global dos sistemas. Também oferece dashboard, histórico e relatórios para acompanhar prazos e SLAs. Dessa forma, possibilita priorizar atendimentos, evitar atrasos contratuais e melhorar a eficiência operacional da equipe técnica.

---

## 📋 Backlog do Produto <a id="backlog"></a>

**Parceiro:** Altave  
**Curso:** 4º Banco de Dados  
**API:** 2026-1  
**Fatec SJC**

| Rank | Prioridade | User Story | Estimativa | Sprint |
|------|------------|-----------|-----------|--------|
| 1 | Alta | Como gestor, quero criar uma ordem de manutenção vinculada a um sistema e a um contrato para iniciar o planejamento do atendimento | 50 | 1 |
| 2 | Média | Como gestor, quero cadastrar sistemas instalados nos clientes para registrar os equipamentos sob contrato de manutenção | 40 | 1 |
| 3 | Média | Como gestor, quero cadastrar contratos de manutenção vinculados a clientes e sistemas para definir intervalos e SLAs de atendimento | 40 | 1 |
| 4 | Média | Como gestor, quero cadastrar as máquinas da empresa para ter um controle centralizado do inventário próprio | 40 | 3 |
| 5 | Baixa | Como gestor, quero cadastrar clientes com informações de localização para saber onde cada sistema está instalado | 40 | 1 |
| 6 | Baixa | Como gestor, quero cadastrar técnicos com suas competências para saber quem está apto a atender cada chamado | 40 | 1 |
| 7 | Alta | Como técnico, quero registrar os dados da execução da manutenção para documentar o que foi realizado na visita | 30 | 2 |
| 8 | Alta | Como gestor, quero definir a criticidade de uma ordem de manutenção para organizar os atendimentos por impacto | 40 | 2 |
| 9 | Alta | Como gestor, quero registrar viagens de manutenção associadas a ordens para planejar o deslocamento dos técnicos | 50 | 2 |
| 10 | Média | Como gestor, quero visualizar o dashboard de ordens de manutenção para acompanhar o status geral das atividades | 60 | 2 |
| 11 | Média | Como gestor, quero aplicar um checklist de preparação antes da viagem para garantir que ferramentas e equipamentos corretos sejam levados | 40 | 2 |
| 12 | Média | Como técnico, quero acessar o checklist da manutenção durante a visita para garantir que todos os procedimentos foram seguidos | 40 | 2 |
| 13 | Baixa | Como gestor, quero visualizar a disponibilidade dos técnicos em um calendário para evitar conflitos de agendamento | 60 | 2 |
| 14 | Alta | Como gestor, quero acessar o histórico de manutenções de um sistema para rastrear todas as intervenções realizadas | 50 | 3 |
| 15 | Alta | Como gestor, quero visualizar um relatório de manutenções vencidas e próximas do vencimento para evitar descumprimento de contratos | 40 | 3 |
| 16 | Média | Como gestor, quero registrar o ciclo de embarcações para planejar manutenções enquanto estão atracadas | 40 | 3 |
| 17 | Média | Como gestor, quero cadastrar os ativos da empresa para ter um controle centralizado do inventário próprio | 40 | 3 |
| 18 | Baixa | Como gestor, quero visualizar no mapa a localização de todos os sistemas instalados para ter uma visão geográfica da operação | 60 | 3 |
| 19 | Baixa | Como gestor, quero rastrear a localização dos técnicos no mapa para saber se estão em campo ou em terra | 60 | 3 |

---

## 📅 Cronograma de Sprints <a id="sprint"></a>
| Sprint          |    Período    | Documentação                                     |            Alocação das tasks               | Vídeo Entrega                                  |
| --------------- | :-----------: | ------------------------------------------------ | ------------------------------------------- |----------------------------------------------- |
| 🔖 **SPRINT 1** | 08/09 - 28/09 | [Sprint 1 Docs](./documentacao/sprints/sprint-1) | [Sprint 1 Tasks](#)  |[Sprint 1 entrega](./documentacao/sprints/sprint-1/README.md) |


## 💻 Tecnologias <a id="tecnologias"></a>

<h4 align="center">
 <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"></a>
 <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"></a>
 <a href="https://vuejs.org/"><img src="https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vue.js&logoColor=4FC08D"/></a>
 <a href="https://www.javascript.com/"><img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"></a>
 <a href="https://www.typescriptlang.org/"><img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"></a>
 <a href="https://www.postgresql.org/"><img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white"></a>
 <a href="https://github.com/"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>
 <a href="https://git-scm.com/"><img src="https://img.shields.io/badge/Git-E34F26?style=for-the-badge&logo=git&logoColor=white"/></a>
 <a href="https://jwt.io/"><img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white"></a>
 <a href="https://www.figma.com/"><img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/></a>
</h4>

# 🪵 Estrutura de branchs <a id="branchs"></a>

[📄 Estrutura de branchs](./documentacao/estrutura_de_branch.md)

# 📚 Manual de usuário <a id="manual"></a>
[Manual do usuário]

## 📦 Desing do Projeto <a id="figma"></a>

[Figma](https://stitch.withgoogle.com/projects/8232220029481373226)

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
      <td>Pedro</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/pedrohenribeiro"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/pedrohenribeiro1?utm_source=share_via&utm_content=profile&utm_medium=member_ios"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>      
    </tr>
     <tr>
      <td>Thor</td>
      <td>Desenvolvedor</td>
      <td><a href="https://github.com/thorlyndgaard"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/thor-lyndgaard-b2155826b/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>      
    </tr>
  </table>
</div>

