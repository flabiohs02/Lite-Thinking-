import { describe, it, expect, vi, beforeEach } from 'vitest';
import { endpoints } from '../api/resources';
import type { ResourceKey } from '../types';

// Mock de Axios instance completa
vi.mock('../api/http', () => {
  const mockApi = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() }
    }
  };
  return {
    api: mockApi,
    getErrorMessage: vi.fn(() => 'Error mock')
  };
});

// Mock del router
vi.mock('../router', () => ({
  default: { push: vi.fn() }
}));

// Mock del store
vi.mock('../stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    token: null,
    logout: vi.fn()
  }))
}));

describe('API Resources - endpoints', () => {
  it('debe tener los endpoints definidos para todos los recursos', () => {
    const keys: ResourceKey[] = ['companies', 'products', 'categories', 'inventories', 'users', 'roles', 'orders'];
    keys.forEach((key) => {
      expect(endpoints[key]).toBeDefined();
      expect(endpoints[key].startsWith('/')).toBe(true);
    });
  });

  it('los endpoints deben coincidir con los esperados', () => {
    expect(endpoints.companies).toBe('/companies');
    expect(endpoints.products).toBe('/products');
    expect(endpoints.categories).toBe('/categories');
    expect(endpoints.inventories).toBe('/inventories');
    expect(endpoints.users).toBe('/users');
    expect(endpoints.roles).toBe('/roles');
    expect(endpoints.orders).toBe('/orders');
  });
});

describe('API Resources - CRUD functions', () => {
  let api: any;

  beforeEach(async () => {
    vi.clearAllMocks();
    const httpModule = await import('../api/http');
    api = httpModule.api;
  });

  it('login debe hacer POST a /auth/login', async () => {
    const { login } = await import('../api/resources');
    api.post.mockResolvedValue({ data: { token: 'abc123' } });

    const result = await login({ identification: 'admin', password: '12345678' });

    expect(api.post).toHaveBeenCalledWith('/auth/login', { identification: 'admin', password: '12345678' });
    expect(result.token).toBe('abc123');
  });

  it('getCurrentUser debe hacer GET a /users/identification/:id', async () => {
    const { getCurrentUser } = await import('../api/resources');
    const mockUser = { id: 1, identification: 'admin', name: 'Admin', isActive: true, role: { id: 1, name: 'ADMIN', isActive: true } };
    api.get.mockResolvedValue({ data: mockUser });

    const result = await getCurrentUser('admin');

    expect(api.get).toHaveBeenCalledWith('/users/identification/admin');
    expect(result.identification).toBe('admin');
  });

  it('listResource debe hacer GET al endpoint del recurso', async () => {
    const { listResource } = await import('../api/resources');
    api.get.mockResolvedValue({ data: [{ nit: '123', name: 'Test' }] });

    const result = await listResource('companies');

    expect(api.get).toHaveBeenCalledWith('/companies');
    expect(result).toHaveLength(1);
  });

  it('createResource debe hacer POST al endpoint del recurso', async () => {
    const { createResource } = await import('../api/resources');
    const payload = { nit: '123', name: 'New Company' };
    api.post.mockResolvedValue({ data: { ...payload, isActive: true } });

    const result = await createResource('companies', payload);

    expect(api.post).toHaveBeenCalledWith('/companies', payload);
    expect(result.isActive).toBe(true);
  });

  it('updateResource debe hacer PUT al endpoint con el ID', async () => {
    const { updateResource } = await import('../api/resources');
    const payload = { name: 'Updated Company' };
    api.put.mockResolvedValue({ data: { nit: '123', ...payload, isActive: true } });

    const result = await updateResource('companies', '123', payload);

    expect(api.put).toHaveBeenCalledWith('/companies/123', payload);
  });

  it('deleteResource debe hacer DELETE al endpoint con el ID', async () => {
    const { deleteResource } = await import('../api/resources');
    api.delete.mockResolvedValue({});

    await deleteResource('companies', '123');

    expect(api.delete).toHaveBeenCalledWith('/companies/123');
  });

  it('listCatalogProducts debe hacer GET a /catalog/products', async () => {
    const { listCatalogProducts } = await import('../api/resources');
    api.get.mockResolvedValue({ data: [{ code: 'P1', name: 'Producto', stockTotal: 10 }] });

    const result = await listCatalogProducts();

    expect(api.get).toHaveBeenCalledWith('/catalog/products');
    expect(result[0].stockTotal).toBe(10);
  });

  it('listOrdersByUser debe hacer GET a /orders/user/:userId', async () => {
    const { listOrdersByUser } = await import('../api/resources');
    api.get.mockResolvedValue({ data: [{ id: 1, status: 'PENDING' }] });

    const result = await listOrdersByUser(5);

    expect(api.get).toHaveBeenCalledWith('/orders/user/5');
    expect(result[0].status).toBe('PENDING');
  });

  it('createOrder debe hacer POST a /orders con el payload completo', async () => {
    const { createOrder } = await import('../api/resources');
    const payload = {
      userId: 1,
      status: 'PENDING',
      items: [{ productCode: 'P1', quantity: 2, currency: 'COP' }],
      isActive: true
    };
    api.post.mockResolvedValue({ data: { id: 1, ...payload, total: 50000 } });

    const result = await createOrder(payload);

    expect(api.post).toHaveBeenCalledWith('/orders', payload);
    expect(result.total).toBe(50000);
  });
});
