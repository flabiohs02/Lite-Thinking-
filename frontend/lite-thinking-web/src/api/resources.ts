import { api } from './http';
import type {
  AuthRequest,
  AuthResponse,
  CompanyResponse,
  ProductResponse,
  CatalogProductResponse,
  CategoryResponse,
  InventoryResponse,
  UserResponse,
  RoleResponse,
  OrderResponse,
  ResourceKey,
  ResourceResponse,
  OrderItemRequest
} from '../types';

export const endpoints: Record<ResourceKey, string> = {
  companies: '/companies',
  products: '/products',
  categories: '/categories',
  inventories: '/inventories',
  users: '/users',
  roles: '/roles',
  orders: '/orders'
};

export async function login(payload: AuthRequest): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/auth/login', payload);
  return data;
}

export async function getCurrentUser(identification: string): Promise<UserResponse> {
  const { data } = await api.get<UserResponse>(`/users/identification/${encodeURIComponent(identification)}`);
  return data;
}

export async function listResource<T extends ResourceResponse>(resource: ResourceKey): Promise<T[]> {
  const { data } = await api.get<T[]>(endpoints[resource]);
  return data;
}

export async function createResource<T extends ResourceResponse>(
  resource: ResourceKey,
  payload: Record<string, unknown>
): Promise<T> {
  const { data } = await api.post<T>(endpoints[resource], payload);
  return data;
}

export async function updateResource<T extends ResourceResponse>(
  resource: ResourceKey,
  id: string | number,
  payload: Record<string, unknown>
): Promise<T> {
  const { data } = await api.put<T>(`${endpoints[resource]}/${encodeURIComponent(String(id))}`, payload);
  return data;
}

export async function deleteResource(resource: ResourceKey, id: string | number): Promise<void> {
  await api.delete(`${endpoints[resource]}/${encodeURIComponent(String(id))}`);
}

export async function listCompanies(): Promise<CompanyResponse[]> {
  return listResource<CompanyResponse>('companies');
}

export async function listProducts(): Promise<ProductResponse[]> {
  return listResource<ProductResponse>('products');
}

export async function listCatalogProducts(): Promise<CatalogProductResponse[]> {
  const { data } = await api.get<CatalogProductResponse[]>('/catalog/products');
  return data;
}

export async function listCategories(): Promise<CategoryResponse[]> {
  return listResource<CategoryResponse>('categories');
}

export async function listInventories(): Promise<InventoryResponse[]> {
  return listResource<InventoryResponse>('inventories');
}

export async function listUsers(): Promise<UserResponse[]> {
  return listResource<UserResponse>('users');
}

export async function listRoles(): Promise<RoleResponse[]> {
  return listResource<RoleResponse>('roles');
}

export async function listOrders(): Promise<OrderResponse[]> {
  return listResource<OrderResponse>('orders');
}

export async function listOrdersByUser(userId: number): Promise<OrderResponse[]> {
  const { data } = await api.get<OrderResponse[]>(`/orders/user/${encodeURIComponent(String(userId))}`);
  return data;
}

export async function createOrder(payload: {
  userId: number;
  status?: string;
  items: OrderItemRequest[];
  isActive?: boolean;
}): Promise<OrderResponse> {
  const { data } = await api.post<OrderResponse>('/orders', payload);
  return data;
}
