import { apiFetch } from './api'

// Espelha exatamente o MaquinaHistoricoManutencaoResponseDTO do backend
export interface ManutencaoRelatorioDTO {
  codigo: number
  codigoMaquinaContrato: number | null
  codigoSoftwareInstalado: number | null
  codigoTipoManutencao: number | null
  status: string
  criticidade: string
  vencimento: string | null
  dataAgendamento: string | null
  dataInicioExecucao: string | null
  dataFimExecucao: string | null
  observacaoGeral: string | null
}

export const manutencaoService = {
  listarRelatorio: () => apiFetch<ManutencaoRelatorioDTO[]>('/maquinas-historicos-manutencao'),
}