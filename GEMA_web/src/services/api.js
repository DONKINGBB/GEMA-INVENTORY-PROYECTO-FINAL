import axios from 'axios';

const api = axios.create({
    baseURL: 'https://gema-inventory-backend.onrender.com/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add auth token if needed
api.interceptors.request.use((config) => {
  // Don't add token to login/register requests
  if (config.url.includes('/auth/')) {
    return config;
  }

  const userStr = localStorage.getItem('user');
  if (userStr) {
      try {
          const user = JSON.parse(userStr);
          if (user && user.token) {
              config.headers.Authorization = `Bearer ${user.token}`;
          }
      } catch (e) {
          console.error("Local storage error:", e);
      }
  }
  return config;
});

// Response interceptor to handle expired tokens
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Check if it's an auth error (expired token)
            const message = error.response.data?.message;
            if (message === "Sesión expirada." || message === "Firma de sesión inválida. Inicia sesión de nuevo.") {
                localStorage.removeItem('user');
                // Optional: redirect to login if not already there
                if (!window.location.pathname.includes('/login')) {
                    window.location.href = '/login';
                }
            }
        }
        return Promise.reject(error);
    }
);

export default api;
