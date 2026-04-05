import { apiFetch } from './api';

export interface MaquinaSoftwareInstaladoCreateDTO {
  codigoMaquinaContrato: number;
  codigoSoftware: number;
  dataInstalacao?: string;
  versaoInstalada: string;
  chaveLicenca?: string;
  status: string;
  notasAdicionais?: string;
}

export const maquinaSoftwareInstaladoService = {
  criar: async (data: MaquinaSoftwareInstaladoCreateDTO) => {
    return await apiFetch<any>('/maquinas-softwares-instalados', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }
};