import axios from 'axios';

const api = axios.create({
    baseURL: 'https://gema-inventory-backend.onrender.com/api',
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
