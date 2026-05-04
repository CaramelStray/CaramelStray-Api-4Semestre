import { apiFetch } from './api'

export interface CatalogoMaquinaResponseDTO {
  codigo: number
  descricao: string
  especificacao?: string
  limiteManutencao?: string
}

export interface CatalogoMaquinaCreateDTO {
  descricao: string
  especificacao?: string
  limiteManutencao?: string
}

export interface CatalogoMaquinaChecklistPadraoResponseDTO {
  codigoCatalogoMaquina: number
  codigoTarefa: number
  descricaoTarefa: string
  categoria?: string
}

export const catalogoMaquinaService = {
  listarTodos: () => apiFetch<CatalogoMaquinaResponseDTO[]>('/catalogo-maquinas'),
  
  buscarPorId: (id: number) => apiFetch<CatalogoMaquinaResponseDTO>(`/catalogo-maquinas/${id}`),
  
  criar: (dto: CatalogoMaquinaCreateDTO) => apiFetch<CatalogoMaquinaResponseDTO>('/catalogo-maquinas', {
    method: 'POST',
    body: JSON.stringify(dto),
  }),
  
  atualizar: (id: number, dto: CatalogoMaquinaCreateDTO) => apiFetch<CatalogoMaquinaResponseDTO>(`/catalogo-maquinas/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  }),
  
  remover: (id: number) => apiFetch<void>(`/catalogo-maquinas/${id}`, {
    method: 'DELETE',
  }),

  listarChecklistPadrao: (codigoCatalogoMaquina: number) =>
    apiFetch<CatalogoMaquinaChecklistPadraoResponseDTO[]>(`/catalogo-maquinas/${codigoCatalogoMaquina}/checklist`),

  adicionarChecklistPadrao: (codigoCatalogoMaquina: number, codigoTarefa: number) =>
    apiFetch<CatalogoMaquinaChecklistPadraoResponseDTO>(`/catalogo-maquinas/${codigoCatalogoMaquina}/checklist`, {
      method: 'POST',
      body: JSON.stringify({ codigoTarefa }),
    }),

  removerChecklistPadrao: (codigoCatalogoMaquina: number, codigoTarefa: number) =>
    apiFetch<void>(`/catalogo-maquinas/${codigoCatalogoMaquina}/checklist/${codigoTarefa}`, {
      method: 'DELETE',
    }),
}