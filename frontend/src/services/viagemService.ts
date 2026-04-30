import { apiFetch } from './api'

export interface TipoViagemResponseDTO {
  codigo: number
  descricao: string
  observacao?: string
  ativo: boolean
  dataCadastro?: string
}

export interface ViagemParadaResponseDTO {
  codigo: number
  codigoViagem: number
  ordem: number
  descricaoLocal: string
  endereco?: string
  cidade?: string
  estadoRegiao?: string
  latitude?: number
  longitude?: number
  dataChegadaPrevista?: string
  dataChegadaReal?: string
  dataSaidaPrevista?: string
  dataSaidaReal?: string
  observacao?: string
}

export interface ViagemResponseDTO {
  codigo: number
  codigoTipoViagem: number
  descricaoTipoViagem?: string
  codigoCliente: number
  nomeCliente?: string
  codigoFuncionarioResponsavel?: number
  nomeFuncionarioResponsavel?: string
  codigoOrdemServico?: number
  status: string
  dataCadastro: string
  dataSaidaPrevista?: string
  dataSaidaReal?: string
  dataRetornoPrevisto?: string
  dataRetornoReal?: string
  origem?: string
  destino?: string
  kmPrevisto?: number
  kmReal?: number
  observacao?: string
  paradas?: ViagemParadaResponseDTO[]
}

export interface ViagemCreateDTO {
  codigoTipoViagem: number
  codigoCliente: number
  codigoFuncionarioResponsavel?: number
  codigoOrdemServico?: number
  status?: string
  dataSaidaPrevista?: string
  dataSaidaReal?: string
  dataRetornoPrevisto?: string
  dataRetornoReal?: string
  origem?: string
  destino?: string
  kmPrevisto?: number
  kmReal?: number
  observacao?: string
  paradas?: Omit<ViagemParadaResponseDTO, 'codigo' | 'codigoViagem'>[]
}

export const viagemService = {
  listar: () =>
    apiFetch<ViagemResponseDTO[]>('/viagens'),

  listarTipos: () =>
    apiFetch<TipoViagemResponseDTO[]>('/tipos-viagem'),

  buscarPorId: (id: number) =>
    apiFetch<ViagemResponseDTO>(`/viagens/${id}`),

  buscarPorCliente: (codigoCliente: number) =>
    apiFetch<ViagemResponseDTO[]>(`/viagens/cliente/${codigoCliente}`),

  buscarPorOrdemServico: (codigoOrdemServico: number) =>
    apiFetch<ViagemResponseDTO[]>(`/viagens/ordem-servico/${codigoOrdemServico}`),

  buscarPorStatus: (status: string) =>
    apiFetch<ViagemResponseDTO[]>(`/viagens/status?status=${encodeURIComponent(status)}`),

  criar: (dto: ViagemCreateDTO) =>
    apiFetch<ViagemResponseDTO>('/viagens', {
      method: 'POST',
      body: JSON.stringify(dto),
    }),

  atualizar: (id: number, dto: ViagemCreateDTO) =>
    apiFetch<ViagemResponseDTO>(`/viagens/${id}`, {
      method: 'PUT',
      body: JSON.stringify(dto),
    }),

  deletar: (id: number) =>
    apiFetch<void>(`/viagens/${id}`, {
      method: 'DELETE',
    }),
}
