import { apiFetch } from './api';

export interface MaquinaSoftwareInstaladoResponseDTO {
  codigo: number;
  codigoMaquinaContrato: number;
  codigoSoftware: number;
  versaoInstalada: string;
  chaveLicenca?: string;
}

export interface MaquinaSoftwareInstaladoCreateDTO {
  codigoMaquinaContrato: number;
  codigoSoftware: number;
  versaoInstalada: string;
  chaveLicenca?: string;
}

export const maquinaSoftwareInstaladoService = {
  listar: () =>
    apiFetch<MaquinaSoftwareInstaladoResponseDTO[]>('/maquinas-softwares-instalados'),

  buscarPorMaquinaContrato: (codigoMaquinaContrato: number) =>
    apiFetch<MaquinaSoftwareInstaladoResponseDTO[]>(
      `/maquinas-softwares-instalados?codigoMaquinaContrato=${codigoMaquinaContrato}`
    ),

  buscarPorSoftware: (codigoSoftware: number) =>
    apiFetch<MaquinaSoftwareInstaladoResponseDTO[]>(
      `/maquinas-softwares-instalados?codigoSoftware=${codigoSoftware}`
    ),

  buscarPorId: (id: number) =>
    apiFetch<MaquinaSoftwareInstaladoResponseDTO>(`/maquinas-softwares-instalados/${id}`),

  criar: (data: MaquinaSoftwareInstaladoCreateDTO) =>
    apiFetch<MaquinaSoftwareInstaladoResponseDTO>('/maquinas-softwares-instalados', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  atualizar: (id: number, data: MaquinaSoftwareInstaladoCreateDTO) =>
    apiFetch<MaquinaSoftwareInstaladoResponseDTO>(`/maquinas-softwares-instalados/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  deletar: (id: number) =>
    apiFetch<void>(`/maquinas-softwares-instalados/${id}`, {
      method: 'DELETE',
    }),
};