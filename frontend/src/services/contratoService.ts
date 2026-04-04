import { apiFetch } from './api'

export interface ContratoResponseDTO {
  codigo: number
  dataInicio: string
  dataFim: string
  status: string
  periodoManutencaoPreventiva: number
  conexaoInternet: boolean
  vencimentoManutencaoPreventiva: string
}

export const contratoService = {
  listar: () => apiFetch<ContratoResponseDTO[]>('/contratos'),
  buscarPorId: (id: number) => apiFetch<ContratoResponseDTO>(`/contratos/${id}`),
  buscarPorCliente: (clienteId: number) => apiFetch<ContratoResponseDTO[]>(`/contratos/cliente/${clienteId}`),
}