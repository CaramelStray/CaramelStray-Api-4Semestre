# API 4º Semestre Banco de Dados

## Caramel Stray

<p align="center">
      <img src="../assets/logo_v2 2.svg" width="50" align="center">
      <h2 align="center"> Tracker</h2>
</p>

## Documentação - Sprints


<p align="center">
  | <a href ="#branch"> Branchs</a>  |
      <a href ="#commit"> Commit</a> | 
</p>

> Status do Projeto: Em progresso  🚧
---
# 🪴 Estrutura de Branchs <a id="branch"></a>

## 📋 Visão Geral
Este documento descreve a estratégia de branches utilizando Git Flow adaptado para integração com GitHub Projects.

---

## 🌿 Estrutura de Branches

### Branch `main`
Propósito: Contém o código em estado de produção (estável).

Regra: Nunca realizamos commits diretos nesta branch. Ela recebe apenas merges vindos da dev quando uma versão está pronta
para ser "lançada".

---

### Branch `dev`
Propósito: É a branch principal de desenvolvimento.

Funcionamento: Serve como ponto de integração para todas as novas funcionalidades. Todas as tarefas concluídas e revisadas são 
mescladas aqui antes de seguirem para a main.

---

### Branchs de Tasks (feature/ ou fix/)
Propósito: Isolamento de novas funcionalidades ou correções.

Fluxo:
1. Uma nova branch é criada a partir da dev.
2. O trabalho é realizado de forma isolada para evitar conflitos.
3. Após a conclusão e revisão (Pull Request), a branch é mergiada de volta na dev e, em seguida, deletada.

---

## 💬 Estratégia de Commits <a id="commit"></a>

Os commits seguem o padrão **Conventional Commits**, garantindo um histórico claro, padronizado e fácil de rastrear.

### Formato

```
[<tipo>] <descrição curta no imperativo>
```

> **Exemplo:** `[feat] adicionar tela de listagem de ordens do técnico`

---

### Tipos de Commit

| Tipo | Quando usar |
|------|-------------|
| `feat` | Uma nova funcionalidade para o usuário |
| `fix` | Correção de um erro (bug) em produção ou teste |
| `refactor` | Alteração no código que não corrige erro nem adiciona funcionalidade |
| `docs` | Alterações apenas na documentação (README, comentários, Wiki) |
| `test` | Adição de testes que faltavam ou correção de testes existentes |




