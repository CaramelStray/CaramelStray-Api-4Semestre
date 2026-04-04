import { apiFetch } from './api'

export interface TecnicoResponseDTO {
  id: number
  usuarioId: number
  email: string
  nome: string
  cpf: string
  cargo: string
  telefone: string
  latitude: number
  longitude: number
  certificacao: string | null
  estado: string | null
  disponibilidade: string | null
}

export interface TecnicoCreateDTO {
  email: string
  senha: string
  nome: string
  cpf?: string
  cargo?: string
  telefone?: string
  latitude?: number
  longitude?: number
}

export const tecnicoService = {
  listar: () => apiFetch<TecnicoResponseDTO[]>('/tecnicos'),
  buscarPorId: (id: number) => apiFetch<TecnicoResponseDTO>(`/tecnicos/${id}`),
  criar: (dto: TecnicoCreateDTO) => apiFetch<TecnicoResponseDTO>('/tecnicos', {
    method: 'POST',
    body: JSON.stringify(dto),
  }),
  atualizar: (id: number, dto: TecnicoCreateDTO) => apiFetch<TecnicoResponseDTO>(`/tecnicos/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  }),
  deletar: (id: number) => apiFetch<void>(`/tecnicos/${id}`, {
    method: 'DELETE',
  }),
}