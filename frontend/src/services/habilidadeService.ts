import { apiFetch } from './api'

export interface HabilidadeResponseDTO {
  codigo: number
  descricao: string
  observacao: string
}

export interface HabilidadeCreateDTO {
  descricao: string
  observacao?: string
}

export const habilidadeService = {
  listar: () => apiFetch<HabilidadeResponseDTO[]>('/habilidades'),
  
  buscarPorId: (id: number) => apiFetch<HabilidadeResponseDTO>(`/habilidades/${id}`),
  
  buscarPorDescricao: (descricao: string) => apiFetch<HabilidadeResponseDTO>(`/habilidades/descricao?descricao=${descricao}`),
  
  criar: (dto: HabilidadeCreateDTO) => apiFetch<HabilidadeResponseDTO>('/habilidades', {
    method: 'POST',
    body: JSON.stringify(dto),
  }),
  
  atualizar: (id: number, dto: HabilidadeCreateDTO) => apiFetch<HabilidadeResponseDTO>(`/habilidades/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  }),
  
  remover: (id: number) => apiFetch<void>(`/habilidades/${id}`, {
    method: 'DELETE',
  }),
}