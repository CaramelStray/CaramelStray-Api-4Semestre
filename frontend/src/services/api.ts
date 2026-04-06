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
    const error: any = new Error(`Erro ${response.status}: ${response.statusText}`)
    error.response = response 
    throw error
  }

  return response.json()
}