import { apiFetch } from './api'

export interface OrdemServicoResponseDTO {
  codigo: number
  codigoCliente: number
  codigoFuncionario?: number
  codigoSoftwareInstalado?: number
  codigoContrato: number
  codigoMaquinaContrato: number
  status: string
  criticidade: string
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
}
