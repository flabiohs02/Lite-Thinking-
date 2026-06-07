import { describe, it, expect, vi, beforeEach } from 'vitest';
import { setActivePinia, createPinia } from 'pinia';
import { useAuthStore } from '../stores/auth';

// Mock de API
vi.mock('../api/resources', () => ({
  login: vi.fn(),
  getCurrentUser: vi.fn()
}));

// Mock de localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: vi.fn((key: string) => store[key] ?? null),
    setItem: vi.fn((key: string, value: string) => { store[key] = value; }),
    removeItem: vi.fn((key: string) => { delete store[key]; }),
    clear: vi.fn(() => { store = {}; })
  };
})();
Object.defineProperty(globalThis, 'localStorage', { value: localStorageMock });

// Generar un JWT fake con payload configurable
function createFakeJwt(payload: Record<string, unknown>): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const body = btoa(JSON.stringify(payload));
  return `${header}.${body}.fake-signature`;
}

describe('Auth Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    localStorageMock.clear();
    vi.clearAllMocks();
  });

  it('debe iniciar sin autenticación', () => {
    const auth = useAuthStore();
    expect(auth.isAuthenticated).toBe(false);
    expect(auth.currentUser).toBeNull();
    expect(auth.token).toBeNull();
  });

  it('isAdmin debe ser false cuando no hay usuario', () => {
    const auth = useAuthStore();
    expect(auth.isAdmin).toBe(false);
  });

  it('isClient debe ser false cuando no hay usuario', () => {
    const auth = useAuthStore();
    expect(auth.isClient).toBe(false);
  });

  it('isExternal debe ser false cuando no hay usuario', () => {
    const auth = useAuthStore();
    expect(auth.isExternal).toBe(false);
  });

  it('isAdmin debe ser true cuando el usuario tiene rol ADMIN', () => {
    const auth = useAuthStore();
    auth.currentUser = {
      id: 1,
      identification: 'admin',
      name: 'Admin',
      isActive: true,
      role: { id: 1, name: 'ADMIN', isActive: true }
    };
    expect(auth.isAdmin).toBe(true);
    expect(auth.isClient).toBe(false);
    expect(auth.isExternal).toBe(false);
  });

  it('isClient debe ser true cuando el usuario tiene rol CLIENT', () => {
    const auth = useAuthStore();
    auth.currentUser = {
      id: 2,
      identification: 'cliente',
      name: 'Cliente',
      isActive: true,
      role: { id: 2, name: 'CLIENT', isActive: true }
    };
    expect(auth.isClient).toBe(true);
    expect(auth.isAdmin).toBe(false);
  });

  it('isExternal debe ser true cuando el usuario tiene rol EXTERNAL', () => {
    const auth = useAuthStore();
    auth.currentUser = {
      id: 3,
      identification: 'external',
      name: 'Externo',
      isActive: true,
      role: { id: 3, name: 'EXTERNAL', isActive: true }
    };
    expect(auth.isExternal).toBe(true);
    expect(auth.isAdmin).toBe(false);
    expect(auth.isClient).toBe(false);
  });

  it('signIn debe almacenar el token y actualizar isAuthenticated', async () => {
    const { login } = await import('../api/resources');
    const fakeToken = createFakeJwt({ sub: 'admin', exp: Math.floor(Date.now() / 1000) + 3600 });

    (login as ReturnType<typeof vi.fn>).mockResolvedValue({ token: fakeToken });

    const { getCurrentUser } = await import('../api/resources');
    (getCurrentUser as ReturnType<typeof vi.fn>).mockResolvedValue({
      id: 1,
      identification: 'admin',
      name: 'Admin',
      isActive: true,
      role: { id: 1, name: 'ADMIN', isActive: true }
    });

    const auth = useAuthStore();
    await auth.signIn({ identification: 'admin', password: '12345678' });

    expect(auth.token).toBe(fakeToken);
    expect(auth.isAuthenticated).toBe(true);
    expect(localStorageMock.setItem).toHaveBeenCalledWith('lite-thinking-token', fakeToken);
  });

  it('logout debe limpiar token, usuario y localStorage', () => {
    const auth = useAuthStore();
    auth.token = 'some-token';
    auth.currentUser = {
      id: 1,
      identification: 'admin',
      name: 'Admin',
      isActive: true,
      role: { id: 1, name: 'ADMIN', isActive: true }
    };

    auth.logout();

    expect(auth.token).toBeNull();
    expect(auth.currentUser).toBeNull();
    expect(auth.isAuthenticated).toBe(false);
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('lite-thinking-token');
  });

  it('bootstrap debe hacer logout si el token está expirado', async () => {
    const expiredToken = createFakeJwt({ sub: 'admin', exp: Math.floor(Date.now() / 1000) - 100 });

    const auth = useAuthStore();
    auth.token = expiredToken;

    await auth.bootstrap();

    expect(auth.token).toBeNull();
    expect(auth.isAuthenticated).toBe(false);
    expect(auth.ready).toBe(true);
  });

  it('bootstrap debe hacer logout si no hay token', async () => {
    const auth = useAuthStore();
    auth.token = null;

    await auth.bootstrap();

    expect(auth.isAuthenticated).toBe(false);
    expect(auth.ready).toBe(true);
  });

  it('bootstrap debe cargar el usuario si el token es válido', async () => {
    const { getCurrentUser } = await import('../api/resources');
    const validToken = createFakeJwt({ sub: 'admin', exp: Math.floor(Date.now() / 1000) + 3600 });

    (getCurrentUser as ReturnType<typeof vi.fn>).mockResolvedValue({
      id: 1,
      identification: 'admin',
      name: 'Admin',
      isActive: true,
      role: { id: 1, name: 'ADMIN', isActive: true }
    });

    const auth = useAuthStore();
    auth.token = validToken;

    await auth.bootstrap();

    expect(auth.currentUser).not.toBeNull();
    expect(auth.currentUser?.identification).toBe('admin');
    expect(auth.ready).toBe(true);
  });

  it('bootstrap debe hacer logout si getCurrentUser falla', async () => {
    const { getCurrentUser } = await import('../api/resources');
    const validToken = createFakeJwt({ sub: 'admin', exp: Math.floor(Date.now() / 1000) + 3600 });

    (getCurrentUser as ReturnType<typeof vi.fn>).mockRejectedValue(new Error('Network error'));

    const auth = useAuthStore();
    auth.token = validToken;

    await auth.bootstrap();

    expect(auth.token).toBeNull();
    expect(auth.currentUser).toBeNull();
    expect(auth.ready).toBe(true);
  });
});
