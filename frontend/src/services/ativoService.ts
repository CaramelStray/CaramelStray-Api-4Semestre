import { apiFetch } from './api'

export interface CatalogoAtivoDTO {
  codigo: number
  descricaoProduto: string
  modelo?: string
  marca?: string
  tipo?: string
}

export interface AtivoResponseDTO {
  codigo: number
  codigoCatalogoAtivo?: number
  descricaoProduto?: string
  modelo?: string
  marca?: string
  tipo?: string
  numeroSerie?: string
  lote?: string
  descricao?: string
  codigoFuncionarioResponsavel?: number
  nomeFuncionarioResponsavel?: string
  status?: string
}

export interface AtivoCreateDTO {
  codigoCatalogoAtivo: number
  numeroSerie?: string
  lote?: string
  descricao?: string
  codigoFuncionarioResponsavel?: number
  status?: string
}

export const ativoService = {
  listar: () => apiFetch<AtivoResponseDTO[]>('/ativos'),
  buscarPorId: (id: number) => apiFetch<AtivoResponseDTO>(`/ativos/${id}`),
  criar: (dto: AtivoCreateDTO) => apiFetch<AtivoResponseDTO>('/ativos', { method: 'POST', body: JSON.stringify(dto) }),
  atualizar: (id: number, dto: AtivoCreateDTO) => apiFetch<AtivoResponseDTO>(`/ativos/${id}`, { method: 'PUT', body: JSON.stringify(dto) }),
  deletar: (id: number) => apiFetch<void>(`/ativos/${id}`, { method: 'DELETE' }),
  listarCatalogos: () => apiFetch<CatalogoAtivoDTO[]>('/catalogo-ativos'),
}
