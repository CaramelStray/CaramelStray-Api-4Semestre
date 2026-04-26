import { apiFetch } from './api'

export interface ManutencaoResponseDTO {
  codigo: number
  status: string
  criticidade: string
  vencimento: string
  dataAgendamento?: string
  observacaoGeral?: string

  tipoManutencao?: {
    descricao: string
  }

  maquinaContrato?: {
    codigo: number
  }
}

export const manutencaoService = {
  async listar(): Promise<ManutencaoResponseDTO[]> {
    return await apiFetch<ManutencaoResponseDTO[]>('/maquina-historico-manutencao')
  }
}