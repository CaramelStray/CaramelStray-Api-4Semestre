import { apiFetch } from './api'

export interface TecnicosOrdensResponseDTO {
  codigo: number
  nomeCliente: string
  status: string
  criticidade: string
  dataAbertura?: string
  dataAgendamento?: string
}

export interface MaquinaChecklistManutencaoResponseDTO {
  codigo: number
  codigoHistoricoManutencao?: number
  codigoTarefa?: number
  descricaoTarefa?: string
  categoriaTarefa?: string
  realizado?: boolean | null
  observacaoTarefa?: string
}

export interface TecnicoOrdemDetalhesResponseDTO {
  // Ordem
  codigo: number
  status: string
  criticidade: string
  tipoOrdem?: string
  dataAbertura?: string
  dataAgendamento?: string
  dataInicioExecucao?: string
  dataFimExecucao?: string
  observacaoGeral?: string
  // Cliente
  nomeCliente?: string
  nomeResponsavelCliente?: string
  emailCliente?: string
  telefoneCliente?: string
  cidadeCliente?: string
  estadoRegiaoCliente?: string
  paisCliente?: string
  fusoHorarioCliente?: string
  internacionalCliente?: boolean
  // Contrato
  codigoContrato?: number
  conexaoInternet?: boolean
  // Máquina
  descricaoMaquina?: string
  especificacaoMaquina?: string
  limiteManutencaoMaquina?: string
  numeroSerieMaquina?: string
  trabalhoEmAltura?: boolean
  manutencaoFeitaMaquina?: string
  // Checklist ativos
  checklistAtivos?: {
    codigo: number
    descricaoAtivo?: string
    descricaoProduto?: string
    modelo?: string
    marca?: string
    numeroSerie?: string
    levado?: boolean
    devolvido?: boolean
    observacao?: string
  }[]
  // Checklist máquina
  checklistMaquina?: MaquinaChecklistManutencaoResponseDTO[]
}

export interface OrdemServicoResponseDTO {
  codigo: number
  codigoCliente: number
  codigoFuncionario?: number
  codigoSoftwareInstalado?: number
  codigoContrato: number
  codigoMaquinaContrato: number
  status: string
  criticidade: string
  tipoOrdem?: string
  codigoHistoricoManutencao?: number
  dataAbertura: string
  dataAgendamento?: string
  dataInicioExecucao?: string
  dataFimExecucao?: string
  observacaoGeral?: string
}

export interface OrdemServicoChecklistAtivoResponseDTO {
  codigo: number
  codigoOrdemServico: number
  codigoFuncionario?: number
  codigoAtivo: number
  codigoCatalogoAtivo?: number
  descricaoAtivo?: string
  descricaoProduto?: string
  modelo?: string
  marca?: string
  numeroSerie?: string
  lote?: string
  statusAtivo?: string
  levado: boolean
  devolvido: boolean
  observacao?: string
}

export interface OrdemServicoCreateDTO {
  codigoCliente: number
  codigoFuncionario?: number
  codigoSoftwareInstalado?: number
  codigoContrato: number
  codigoMaquinaContrato: number
  status?: string
  criticidade?: string
  tipoOrdem?: string
  dataAbertura?: string
  dataAgendamento?: string
  dataInicioExecucao?: string
  dataFimExecucao?: string
  observacaoGeral?: string
}

export const ordemServicoService = {
  listar: () =>
    apiFetch<OrdemServicoResponseDTO[]>('/ordens-servico'),

  buscarPorId: (id: number) =>
    apiFetch<OrdemServicoResponseDTO>(`/ordens-servico/${id}`),

  listarChecklistAtivos: (id: number) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO[]>(`/ordens-servico/${id}/checklist-ativos`),

  listarChecklistMaquina: (id: number) =>
    apiFetch<MaquinaChecklistManutencaoResponseDTO[]>(`/ordens-servico/${id}/checklist-maquina`),

  buscarPorCliente: (codigoCliente: number) =>
    apiFetch<OrdemServicoResponseDTO[]>(`/ordens-servico/cliente/${codigoCliente}`),

  buscarPorFuncionario: (codigoFuncionario: number) =>
    apiFetch<OrdemServicoResponseDTO[]>(`/ordens-servico/funcionario/${codigoFuncionario}`),

  buscarPorSoftwareInstalado: (codigoSoftwareInstalado: number) =>
    apiFetch<OrdemServicoResponseDTO[]>(`/ordens-servico/software-instalado/${codigoSoftwareInstalado}`),

  buscarPorContrato: (codigoContrato: number) =>
    apiFetch<OrdemServicoResponseDTO[]>(`/ordens-servico/contrato/${codigoContrato}`),

  buscarPorMaquinaContrato: (codigoMaquinaContrato: number) =>
    apiFetch<OrdemServicoResponseDTO[]>(`/ordens-servico/maquina-contrato/${codigoMaquinaContrato}`),

  criar: (dto: OrdemServicoCreateDTO) =>
    apiFetch<OrdemServicoResponseDTO>('/ordens-servico', {
      method: 'POST',
      body: JSON.stringify(dto),
    }),

  atualizar: (id: number, dto: OrdemServicoCreateDTO) =>
    apiFetch<OrdemServicoResponseDTO>(`/ordens-servico/${id}`, {
      method: 'PUT',
      body: JSON.stringify(dto),
    }),

  deletar: (id: number) =>
    apiFetch<void>(`/ordens-servico/${id}`, {
      method: 'DELETE',
    }),

  substituirChecklistAtivos: (id: number, items: Array<{ codigoAtivo: number; codigoFuncionario?: number; descricaoAtivo?: string }>) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO[]>(`/ordens-servico/${id}/checklist-ativos`, {
      method: 'PUT',
      body: JSON.stringify(items),
    }),

  tecnicosOrdens: () =>
    apiFetch<TecnicosOrdensResponseDTO[]>('/ordens-servico/tecnicos-ordens'),

  tecnicoOrdemPorId: (id: number) =>
    apiFetch<TecnicoOrdemDetalhesResponseDTO>(`/ordens-servico/tecnico-ordens/${id}`),

  adicionarChecklistAtivo: (id: number, dto: { codigoAtivo: number; codigoFuncionario?: number; descricaoAtivo?: string }) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO>(`/ordens-servico/${id}/checklist-ativos`, {
      method: 'POST',
      body: JSON.stringify(dto),
    }),

  marcarAtivoLevado: (codigoOrdem: number, codigoItem: number) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO>(`/ordens-servico/minhas-ordens/${codigoOrdem}/checklist-ativos/${codigoItem}/levar`, {
      method: 'PATCH',
    }),

  desmarcarAtivoLevado: (codigoOrdem: number, codigoItem: number) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO>(`/ordens-servico/minhas-ordens/${codigoOrdem}/checklist-ativos/${codigoItem}/deslevar`, {
      method: 'PATCH',
    }),

  marcarAtivoDevolvido: (codigoOrdem: number, codigoItem: number) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO>(`/ordens-servico/minhas-ordens/${codigoOrdem}/checklist-ativos/${codigoItem}/devolver`, {
      method: 'PATCH',
    }),

  desmarcarAtivoDevolvido: (codigoOrdem: number, codigoItem: number) =>
    apiFetch<OrdemServicoChecklistAtivoResponseDTO>(`/ordens-servico/minhas-ordens/${codigoOrdem}/checklist-ativos/${codigoItem}/desdevolver`, {
      method: 'PATCH',
    }),

  atualizarStatus: (codigoOrdem: number, status: string) =>
    apiFetch<OrdemServicoResponseDTO>(`/ordens-servico/minhas-ordens/${codigoOrdem}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),

  adicionarChecklistMaquinaItem: (codigoHistoricoManutencao: number, codigoTarefa: number) =>
    apiFetch<MaquinaChecklistManutencaoResponseDTO>(
      `/maquinas-historicos-manutencao/${codigoHistoricoManutencao}/checklist`,
      { method: 'POST', body: JSON.stringify({ codigoTarefa }) },
    ),

  atualizarChecklistMaquinaItem: (
    codigoHistoricoManutencao: number,
    codigoChecklist: number,
    dto: { realizado: boolean; observacaoTarefa?: string },
  ) =>
    apiFetch<MaquinaChecklistManutencaoResponseDTO>(
      `/maquinas-historicos-manutencao/${codigoHistoricoManutencao}/checklist/${codigoChecklist}`,
      { method: 'PUT', body: JSON.stringify(dto) },
    ),
}
