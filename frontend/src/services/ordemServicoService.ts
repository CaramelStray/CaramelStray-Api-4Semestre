import { apiFetch } from './api'

export interface OrdemServicoResponseDTO {
  codigo: number
  codigoCliente: number
  codigoFuncionario: number
  codigoSoftwareInstalado: number
  codigoContrato: number
  codigoMaquinaContrato: number
  status: string
  criticidade: string
  dataAbertura: string
  dataAgendamento: string
  dataInicioExecucao: string
  dataFimExecucao: string
  observacaoGeral: string
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
  listar: () => apiFetch<OrdemServicoResponseDTO[]>('/ordens-servico'),
  buscarPorId: (id: number) => apiFetch<OrdemServicoResponseDTO>(`/ordens-servico/${id}`),
  buscarPorCliente: (id: number) => apiFetch<OrdemServicoResponseDTO[]>(`/ordens-servico/cliente/${id}`),
  criar: (dto: OrdemServicoCreateDTO) => apiFetch<OrdemServicoResponseDTO>('/ordens-servico', {
    method: 'POST',
    body: JSON.stringify(dto),
  }),
  atualizar: (id: number, dto: OrdemServicoCreateDTO) => apiFetch<OrdemServicoResponseDTO>(`/ordens-servico/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  }),
  deletar: (id: number) => apiFetch<void>(`/ordens-servico/${id}`, {
    method: 'DELETE',
  }),
}