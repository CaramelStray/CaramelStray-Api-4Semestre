import { apiFetch } from './api'

export const authService = {
  async login(dados: any) {
    return apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify(dados)
    })
  }
}