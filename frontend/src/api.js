const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8000';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw err;
  }
  return response.json();
}

export async function login(email) {
  return request('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email }),
  });
}

export async function fetchStats(apiKey, apiSecret) {
  return request('/api/v1/dashboard/stats', {
    headers: {
      'X-Api-Key': apiKey,
      'X-Api-Secret': apiSecret,
    },
  });
}

export async function fetchPayments(apiKey, apiSecret) {
  return request('/api/v1/dashboard/payments', {
    headers: {
      'X-Api-Key': apiKey,
      'X-Api-Secret': apiSecret,
    },
  });
}
