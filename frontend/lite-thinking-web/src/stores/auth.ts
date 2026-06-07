import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { getCurrentUser, login } from '../api/resources';
import type { AuthRequest, JwtPayload, UserResponse } from '../types';

const TOKEN_KEY = 'lite-thinking-token';

function decodeJwt(token: string): JwtPayload {
  const payload = token.split('.')[1];
  if (!payload) {
    return {};
  }
  const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
  const json = decodeURIComponent(
    atob(base64)
      .split('')
      .map((char) => `%${char.charCodeAt(0).toString(16).padStart(2, '0')}`)
      .join('')
  );
  return JSON.parse(json) as JwtPayload;
}

function isExpired(token: string): boolean {
  const payload = decodeJwt(token);
  return Boolean(payload.exp && payload.exp * 1000 <= Date.now());
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY));
  const currentUser = ref<UserResponse | null>(null);
  const ready = ref(false);

  const isAuthenticated = computed(() => Boolean(token.value));
  const isAdmin = computed(() => currentUser.value?.role?.name?.toUpperCase() === 'ADMIN');
  const isClient = computed(() => currentUser.value?.role?.name?.toUpperCase() === 'CLIENT');
  const isExternal = computed(() => currentUser.value?.role?.name?.toUpperCase() === 'EXTERNAL');

  async function bootstrap() {
    if (!token.value || isExpired(token.value)) {
      logout();
      ready.value = true;
      return;
    }

    const subject = decodeJwt(token.value).sub;
    if (subject) {
      try {
        currentUser.value = await getCurrentUser(subject);
      } catch {
        logout();
      }
    }

    ready.value = true;
  }

  async function signIn(credentials: AuthRequest) {
    const response = await login(credentials);
    token.value = response.token;
    localStorage.setItem(TOKEN_KEY, response.token);
    ready.value = false;
    await bootstrap();
  }

  function logout() {
    token.value = null;
    currentUser.value = null;
    localStorage.removeItem(TOKEN_KEY);
  }

  return {
    token,
    currentUser,
    ready,
    isAuthenticated,
    isAdmin,
    isClient,
    isExternal,
    bootstrap,
    signIn,
    logout
  };
});
