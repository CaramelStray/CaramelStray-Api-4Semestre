/**
 * OBJETIVO: Definir a estrutura de dados (Contratos/Interfaces) e os serviços 
 * para comunicação com o endpoint de Clientes. Separa o que a API envia (Response)
 * do que o sistema envia para criar (Create).
 */

// Estrutura de dados que o Backend retorna
export interface ClienteResponseDTO {
  id: number
  emailUsuarioCadastro: string
  nomeEmpresa: string
  documento: string
  emailContato: string
  telefoneContato: string
  nomeResponsavel: string
  pais: string
  estadoRegiao: string
  cidade: string
  classificacaoDistancia: string
  fusoHorario: string
  ativo: boolean
  dataCadastro: string
}

// Estrutura de dados para enviar ao criar um novo cliente (campos opcionais com ?)
export interface ClienteCreateDTO {
  nomeEmpresa: string
  documento?: string
  emailContato?: string
  telefoneContato?: string
  nomeResponsavel?: string
  pais?: string
  estadoRegiao?: string
  cidade?: string
  classificacaoDistancia?: string
  fusoHorario?: string
  ativo?: boolean
}

import { apiFetch } from './api'

export const clienteService = {
  listar: () => apiFetch<ClienteResponseDTO[]>('/clientes'),
  buscarPorId: (id: number) => apiFetch<ClienteResponseDTO>(`/clientes/${id}`),
  criar: (dto: ClienteCreateDTO) => apiFetch<ClienteResponseDTO>('/clientes', {
    method: 'POST',
    body: JSON.stringify(dto),
  }),
}