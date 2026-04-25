import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // Proxy handles redirection to http://localhost:8080
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add auth token if needed
api.interceptors.request.use((config) => {
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

export default api;
