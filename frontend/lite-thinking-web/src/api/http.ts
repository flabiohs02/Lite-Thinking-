import axios from 'axios';
import router from '../router';
import { useAuthStore } from '../stores/auth';

export const api = axios.create({
  // baseURL: import.meta.env.VITE_API_BASE_URL ?? '/lite/api/v1',
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8686/api/v1',
  timeout: 15000
});

api.interceptors.request.use((config) => {
  const auth = useAuthStore();
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const auth = useAuthStore();
      auth.logout();
      router.push({ name: 'login' });
    }
    return Promise.reject(error);
  }
);

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === 'string') {
      return data;
    }
    if (data?.message) {
      return String(data.message);
    }
    if (data?.error) {
      return String(data.error);
    }
    if (error.response?.status === 403) {
      return 'No tienes permisos para esta accion.';
    }
    if (error.response?.status) {
      return `Error HTTP ${error.response.status}.`;
    }
  }
  return 'No se pudo completar la operacion.';
}
