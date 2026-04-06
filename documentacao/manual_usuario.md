# 📘 Manual do Usuário - Tracker

## 📑 Índice
- [O que é o Tracker](#o-que-é-o-tracker)
- [Módulo de Clientes](#módulo-de-clientes)
  - [Indicadores e Funcionalidades](#indicadores-e-funcionalidades)
  - [Cadastro de Novo Cliente](#cadastro-de-novo-cliente)
  - [Contatos da Empresa](#contatos-da-empresa)
- [Módulo de Certificações](#módulo-de-certificações)
  - [Cadastro de Nova Habilidade](#cadastro-de-nova-habilidade)
- [Módulo de Técnicos](#módulo-de-técnicos)
  - [Cadastro de Novo Técnico](#cadastro-de-novo-técnico)
  - [Vincular Certificações](#vincular-certificações-opcional)
- [Módulo de Máquinas](#módulo-de-máquinas)
- [Módulo de Sistemas](#módulo-de-sistemas)
  - [Cadastro de Novo Software](#cadastro-de-novo-software)
- [Módulo de Contratos](#módulo-de-contratos)
  - [Cadastro de Novo Contrato](#cadastro-de-novo-contrato)
  - [Máquinas Base](#máquinas-base)
  - [Softwares](#softwares-opcional)
- [Cadastro de Ordem de Serviço](#cadastro-de-ordem-de-serviço)
- [Seleção de Máquina (Ordem de Serviço)](#seleção-de-máquina-ordem-de-serviço)
- [Seleção de Técnico (Ordem de Serviço)](#seleção-de-técnico-ordem-de-serviço)

---


## O que é o Tracker 

O **Tracker** consiste em um Sistema de Gestão de Manutenções que centraliza o cadastro de clientes, sistemas, contratos, ativos e técnicos.

Através dele, é possível:
- Planejar e registrar ordens de manutenção com base no uso dos equipamentos
- Organizar deslocamentos e atendimentos técnicos
- Monitorar prazos, SLAs e contratos
- Visualizar dashboards e relatórios operacionais

O sistema permite priorizar atendimentos, evitar atrasos contratuais e melhorar a eficiência da operação.

---

## Módulo de Clientes

Este módulo permite a gestão centralizada das empresas parceiras e o acompanhamento de seus contratos.

![Visualização de Clientes](../assets/cli_visu.png)

### Indicadores e Funcionalidades

#### 📊 Indicadores (Dashboard)
- **Total de Clientes**: quantidade total cadastrada
- **Contratos Ativos/Inativos**: status operacional
- **Alertas Críticos**: pendências ou atrasos relevantes

#### 🔍 Pesquisa e Ações
- Busca por nome da empresa
- Exportação de relatórios
- Botão **Novo Cliente** para cadastro

#### 📋 Listagem
- Nome da empresa
- Localização e abrangência (regional/nacional/internacional)
- Status (Ativo/Inativo)
- Ações: visualizar ou editar

---

## Cadastro de Novo Cliente

Permite registrar uma nova empresa no sistema.

![Cadastro de Cliente](../assets/cad_clientes_passo1.png)

### Etapas do Cadastro

1. **Dados da Empresa**
2. **Contatos da Empresa**

### Dados da Empresa

- Nome da empresa (razão social ou fantasia)
- CNPJ (ou opção internacional)
- Nome do responsável
- Endereço completo
- E-mail corporativo
- Telefone
- Fuso horário
- Observações (opcional)

### Controles

- Cancelar cadastro
- Navegação entre etapas

---

## Contatos da Empresa

Permite adicionar contatos vinculados ao cliente.

- Nome, e-mail e telefone
- Adição de múltiplos contatos
- Remoção de contatos

![Contatos](../assets/contatos_cliente.png)

---

## Módulo de Certificações

Gerencia habilidades e competências.

### Funcionalidades:
- Cadastro de certificações
- Busca e edição
- Exclusão controlada

![Certificações](../assets/visu_certifica.png)

---

## Cadastro de Nova Habilidade

- Nome da certificação
- Observações
- Salvamento no catálogo

![Cadastro de Certificação](../assets/cad_certifica.png)

---

## Módulo de Técnicos

Gerencia equipe operacional.

### Funcionalidades:
- Controle de disponibilidade
- Certificações
- Exportação de dados

![Técnicos](../assets/tecnico_visu.png)

---

## Cadastro de Novo Técnico

### Dados obrigatórios:
- Nome e CPF
- Email e senha
- Cargo e telefone
- Estado e disponibilidade

![Cadastro Técnico](../assets/cad_tecnico.png)

---

## Vincular Certificações (Opcional)

- Selecionar certificação
- Definir nível (1 a 5)
- Inserir validade

![Certificações Técnico](../assets/visu_certifica.png)

---

## Módulo de Máquinas

Gerencia ativos físicos.

### Funcionalidades:
- Cadastro de modelos
- Especificações técnicas
- Limite de manutenção

![Máquinas](../assets/visu_maquina.png)

---

## Módulo de Sistemas

Gerencia softwares e licenças.

### Funcionalidades:
- Cadastro de sistemas
- Controle de versões
- Fornecedores

![Sistemas](../assets/visu_sistema.png)

---

## Cadastro de Novo Software

- Nome, versão e tipo
- Fornecedor
- Documentação
- Checklist técnico

![Cadastro Software](../assets/cad_sistema.png)

---

## Módulo de Contratos

Centraliza contratos e vínculos.

### Funcionalidades:
- Indicadores financeiros
- Alertas
- Controle de vigência

![Contratos](../assets/contrato.png)

---

## Cadastro de Novo Contrato

### Etapas:
1. Dados do contrato
2. Máquinas base
3. Softwares

- Vigência
- SLA
- Manutenção

![Cadastro Contrato](../assets/cad_contrato.png)

---

## Máquinas Base

- Seleção de equipamentos
- Identificação e localização
- Inclusão múltipla

![Máquinas Contrato](../assets/contrato_maquinas.png)

---

## Softwares (Opcional)

- Vincular softwares ao contrato
- Etapa opcional

![Software Contrato](../assets/contrato_softw.png)

---

## Cadastro de Ordem de Serviço

Permite abrir chamados técnicos vinculando cliente, contrato e técnico.

### Identificação
- Cliente  
- Contrato  
- Criticidade  
- Data de abertura (automática)  
- Data de agendamento  
- Observação  

### Etapas
1. Identificação  
2. Máquina  
3. Técnico  

### Ações
- **Próximo**: avançar  
- **Fechar**: cancelar

![Cadastro Ordem Serviço](../assets/ordem_cad.png)

---

## Seleção de Máquina (Ordem de Serviço)

Nesta etapa, é definido o equipamento que será atendido na ordem de serviço.

### Informações

- **Modelo**: Seleção do equipamento cadastrado  
- **Identificação**: Nome ou apelido do ativo  
- **Localização**: Onde o equipamento está instalado  
- **Data de Instalação**: Registro da instalação  

---

### Ações

- **Adicionar Máquina**: incluir mais ativos  
- **Voltar**: retornar à etapa anterior  
- **Próximo**: avançar para seleção do técnico  
- **Cancelar**: encerrar sem salvar

![Maquinas Serviço](../assets/ordem_maq.png)

--- 

## Seleção de Técnico (ordem de serviço)

- Vincular técnico ao contrato

![Maquinas Serviço](../assets/ordem_tec.png)

