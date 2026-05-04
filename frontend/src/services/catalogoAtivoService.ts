import { apiFetch } from './api'

export interface CatalogoAtivoResponseDTO {
  codigo: number
  descricaoProduto: string
  modelo?: string
  marca?: string
  descricao?: string
  especificacao?: string
  tipo?: string
}

export interface CatalogoAtivoCreateDTO {
  descricaoProduto: string
  modelo?: string
  marca?: string
  descricao?: string
  especificacao?: string
  tipo?: string
}

export const catalogoAtivoService = {
  listar: () => apiFetch<CatalogoAtivoResponseDTO[]>('/catalogo-ativos'),
  buscarPorId: (id: number) => apiFetch<CatalogoAtivoResponseDTO>(`/catalogo-ativos/${id}`),
  criar: (dto: CatalogoAtivoCreateDTO) =>
    apiFetch<CatalogoAtivoResponseDTO>('/catalogo-ativos', { method: 'POST', body: JSON.stringify(dto) }),
  atualizar: (id: number, dto: CatalogoAtivoCreateDTO) =>
    apiFetch<CatalogoAtivoResponseDTO>(`/catalogo-ativos/${id}`, { method: 'PUT', body: JSON.stringify(dto) }),
  deletar: (id: number) => apiFetch<void>(`/catalogo-ativos/${id}`, { method: 'DELETE' }),
}
