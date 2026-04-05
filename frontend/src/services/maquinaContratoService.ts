import { apiFetch } from './api';

export interface MaquinaContratoCreateDTO {
  codigoContrato: number;
  codigoCatalogoMaquina?: number;
  codigoMaquina?: number;
  apelido: string;
  numeroPatrimonio: string;
  localizacaoFisica: string;
  status: string;
  dataInstalacao: string;
  notasAdicionais?: string;
}

export const maquinaContratoService = {
  criar: async (data: MaquinaContratoCreateDTO) => {
    return await apiFetch<any>('/maquinas-contrato', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }
};