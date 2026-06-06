export type Id = string | number;

export type ResourceKey =
  | 'companies'
  | 'products'
  | 'categories'
  | 'inventories'
  | 'users'
  | 'roles'
  | 'orders';

export interface BaseResponse {
  isActive: boolean;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface RoleResponse extends BaseResponse {
  id: number;
  name: string;
}

export interface CompanyResponse extends BaseResponse {
  nit: string;
  name: string;
  address?: string;
  phone?: string;
}

export interface CategoryResponse extends BaseResponse {
  id: number;
  name: string;
  description?: string;
}

export interface ProductPrice {
  currency: string;
  amount: number;
}

export interface ProductResponse extends BaseResponse {
  code: string;
  name: string;
  characteristics?: string;
  avatar?: string;
  prices: ProductPrice[];
  company: CompanyResponse;
  categories: CategoryResponse[];
}

export interface CatalogProductResponse extends ProductResponse {
  stockTotal: number;
}

export interface UserResponse extends BaseResponse {
  id: number;
  identification: string;
  name: string;
  email?: string;
  phone?: string;
  role: RoleResponse;
}

export interface InventoryResponse extends BaseResponse {
  id: number;
  product: ProductResponse;
  company: CompanyResponse;
  stock: number;
}

export interface OrderItemRequest {
  productCode: string;
  quantity: number;
  currency: string;
}

export interface OrderItemResponse extends BaseResponse {
  id: number;
  productCode: string;
  productName: string;
  quantity: number;
  price: number;
  currency: string;
}

export interface OrderResponse extends BaseResponse {
  id: number;
  user: UserResponse;
  orderDate: string;
  status: string;
  total: number;
  items: OrderItemResponse[];
}

export type ResourceResponse =
  | CompanyResponse
  | ProductResponse
  | CategoryResponse
  | InventoryResponse
  | UserResponse
  | RoleResponse
  | OrderResponse;

export interface AuthRequest {
  identification: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export interface JwtPayload {
  sub?: string;
  exp?: number;
  iat?: number;
  [key: string]: unknown;
}

export interface SelectOption {
  label: string;
  value: Id;
}

export type FieldType =
  | 'text'
  | 'textarea'
  | 'number'
  | 'password'
  | 'checkbox'
  | 'file'
  | 'select'
  | 'multiselect'
  | 'prices'
  | 'orderItems';

export interface FieldConfig {
  key: string;
  label: string;
  type: FieldType;
  required?: boolean;
  readonlyOnEdit?: boolean;
  placeholder?: string;
  optionsKey?: ResourceKey;
}

export interface ResourceConfig {
  key: ResourceKey;
  title: string;
  endpoint: string;
  idKey: string;
  description: string;
  createLabel: string;
  allowUpdate?: boolean;
  columns: Array<{
    key: string;
    label: string;
    format?: 'status' | 'currency' | 'date' | 'list';
  }>;
  fields: FieldConfig[];
  emptyForm: Record<string, unknown>;
}
