# 🪴 Estrutura de branchs

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

### Branchs de Tasks (feature/ ou rebrand/)
Propósito: Isolamento de novas funcionalidades ou correções.

Fluxo: 1. Uma nova branch é criada a partir da dev.
2. O trabalho é realizado de forma isolada para evitar conflitos.
3. Após a conclusão e revisão (Pull Request), a branch é mergiada de volta na dev e, em seguida, deletada.


