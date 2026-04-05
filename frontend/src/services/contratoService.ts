import { apiFetch } from './api'

export interface ContratoResponseDTO {
  codigo: number
  codigoCliente?: number | null
  nomeCliente?: string | null
  emailContatoCliente?: string | null
  dataInicio: string
  dataFim: string
  status: string
  periodoManutencaoPreventiva: number
  conexaoInternet: boolean
  vencimentoManutencaoPreventiva: string
  descricao?: string | null
}

export interface ContratoRequestDTO {
  codigoCliente: number
  dataInicio: string
  dataFim?: string | null
  status: string
  periodoManutencaoPreventiva?: number | null
  conexaoInternet?: boolean | null
  vencimentoManutencaoPreventiva?: string | null
  descricao?: string | null
}

export const contratoService = {
  listar: () => apiFetch<ContratoResponseDTO[]>('/contratos'),
  buscarPorId: (id: number) => apiFetch<ContratoResponseDTO>(`/contratos/${id}`),
  buscarPorCliente: (clienteId: number) => apiFetch<ContratoResponseDTO[]>(`/contratos/cliente/${clienteId}`),
  criar: (dto: ContratoRequestDTO) => apiFetch<ContratoResponseDTO>('/contratos', {
    method: 'POST',
    body: JSON.stringify(dto),
  }),
}
