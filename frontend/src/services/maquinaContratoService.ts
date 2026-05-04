import { apiFetch } from './api';

export interface MaquinaContratoCreateDTO {
  codigoContrato: number;
  codigoCatalogoMaquina: number;
  numeroSerie?: string;
  dataInstalacao: string;
  trabalhoEmAltura?: boolean;
}

export interface MaquinaContratoResponseDTO {
  codigo: number;
  codigoContrato: number;
  codigoCatalogoMaquina: number;
  numeroSerie: string | null;
  dataInstalacao: string | null;
  manutencaoFeita: string | null;
  trabalhoEmAltura: boolean | null;
  latitude: number | null;
  longitude: number | null;
}

export const maquinaContratoService = {
  criar: async (data: MaquinaContratoCreateDTO) => {
    return await apiFetch<MaquinaContratoResponseDTO>('/maquinas-contrato', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  },

  buscarPorId: (id: number) =>
    apiFetch<MaquinaContratoResponseDTO>(`/maquinas-contrato/${id}`),

  buscarPorContrato: (codigoContrato: number) =>
    apiFetch<MaquinaContratoResponseDTO[]>(`/maquinas-contrato/contrato/${codigoContrato}`),

  atualizar: (codigo: number, data: MaquinaContratoCreateDTO) =>
    apiFetch<MaquinaContratoResponseDTO>(`/maquinas-contrato/${codigo}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }),

  deletar: (codigo: number) =>
    apiFetch<void>(`/maquinas-contrato/${codigo}`, {
      method: 'DELETE'
    }),
};
