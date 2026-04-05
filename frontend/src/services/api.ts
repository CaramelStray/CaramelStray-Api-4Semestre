/**
 * OBJETIVO: Centralizar as chamadas HTTP para o backend. 
 * Gerencia a URL base, injeta automaticamente o token de autenticação
 * e trata erros básicos de resposta em um único lugar.
 */

const BASE_URL = 'http://localhost:8080'

function getToken() {
  return localStorage.getItem('token') ?? ''
}

// Função para requisições à API
export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`,
      ...options.headers,
    },
  })

  if (!response.ok) {
    throw new Error(`Erro ${response.status}: ${response.statusText}`)
  }

  return response.json()
}