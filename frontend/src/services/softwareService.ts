import { apiFetch } from './api'

export interface SoftwareChecklistItemResponseDTO {
  id?: number;
  descricao: string;
  obrigatorio: boolean;
}

export interface SoftwareChecklistItemCreateDTO {
  descricao: string;
  obrigatorio: boolean;
}

export interface CatalogoSoftwareResponseDTO {
  id: number;
  identificador: string;
  status: string;
  nomeSoftware: string;
  versao: string;
  tipo: string;
  desenvolvedorFornecedor: string;
  urlDocumentacao: string;
  descricaoTecnica: string;
  ativo: boolean;
  quantidadeChecklistPadrao: number;
  quantidadeItensObrigatorios: number;
  dataCadastro: string;
  checklistPadrao: SoftwareChecklistItemResponseDTO[];
}

export interface CatalogoSoftwareCreateDTO {
  nomeSoftware: string;
  versao: string;
  tipo: string;
  desenvolvedorFornecedor?: string;
  urlDocumentacao?: string;
  descricaoTecnica?: string;
  ativo: boolean;
  checklistPadrao?: SoftwareChecklistItemCreateDTO[];
}

export const softwareService = {
  async listar(termo?: string, tipo?: string, ativo?: boolean): Promise<CatalogoSoftwareResponseDTO[]> {
    const params = new URLSearchParams()
    if (termo) params.append('termo', termo)
    if (tipo) params.append('tipo', tipo)
    if (ativo !== undefined) params.append('ativo', String(ativo))

    const queryString = params.toString() ? `?${params.toString()}` : ''
    
    return await apiFetch<CatalogoSoftwareResponseDTO[]>(`/catalogo-softwares${queryString}`)
  },

  async buscarPorId(id: number): Promise<CatalogoSoftwareResponseDTO> {
    return await apiFetch<CatalogoSoftwareResponseDTO>(`/catalogo-softwares/${id}`)
  },

  async criar(data: CatalogoSoftwareCreateDTO): Promise<CatalogoSoftwareResponseDTO> {
    return await apiFetch<CatalogoSoftwareResponseDTO>('/catalogo-softwares', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },

  async atualizar(id: number, data: CatalogoSoftwareCreateDTO): Promise<CatalogoSoftwareResponseDTO> {
    return await apiFetch<CatalogoSoftwareResponseDTO>(`/catalogo-softwares/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    })
  },

  async remover(id: number): Promise<void> {
    await apiFetch<void>(`/catalogo-softwares/${id}`, {
      method: 'DELETE'
    })
  }
}