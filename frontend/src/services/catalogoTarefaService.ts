import { apiFetch } from './api'

export interface CatalogoTarefaResponseDTO {
  id: number
  descricao: string
  categoria?: string
}

export interface CatalogoTarefaCreateDTO {
  descricao: string
  categoria?: string
}

export const catalogoTarefaService = {
  listar: () => apiFetch<CatalogoTarefaResponseDTO[]>('/catalogo-tarefas'),

  criar: (dto: CatalogoTarefaCreateDTO) =>
    apiFetch<CatalogoTarefaResponseDTO>('/catalogo-tarefas', {
      method: 'POST',
      body: JSON.stringify(dto),
    }),
}
