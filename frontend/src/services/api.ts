const BASE_URL = 'http://localhost:8080'

function getToken() {
  return localStorage.getItem('token')
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)

  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    if (response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user_email')
      localStorage.removeItem('user_role')
      window.location.href = '/login'
      return undefined as T
    }
    let data: any = null
    try { data = await response.json() } catch { /* sem corpo */ }
    const error: any = new Error(`Erro ${response.status}: ${response.statusText}`)
    error.response = { status: response.status, data }
    throw error
  }

  if (response.status === 204) return undefined as T

  const text = await response.text()
  if (!text) return undefined as T

  return JSON.parse(text) as T
}