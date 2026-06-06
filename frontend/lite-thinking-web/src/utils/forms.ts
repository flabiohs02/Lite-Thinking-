import type {
  CategoryResponse,
  CompanyResponse,
  InventoryResponse,
  OrderResponse,
  ProductResponse,
  ResourceKey,
  ResourceResponse,
  RoleResponse,
  SelectOption,
  UserResponse
} from '../types';

export function cloneForm<T extends Record<string, unknown>>(form: T): T {
  return JSON.parse(JSON.stringify(form)) as T;
}

export function activeValue(item: { isActive?: boolean; active?: boolean }): boolean {
  return item.isActive ?? item.active ?? true;
}

export function normalizeActive<T extends ResourceResponse>(item: T): T {
  const record = item as T & { isActive?: boolean; active?: boolean };
  record.isActive = activeValue(record);
  return record;
}

export function optionLabel(resource: ResourceKey, item: ResourceResponse): string {
  if (resource === 'companies') {
    const company = item as CompanyResponse;
    return `${company.name} (${company.nit})`;
  }

  if (resource === 'products') {
    const product = item as ProductResponse;
    return `${product.name} (${product.code})`;
  }

  if (resource === 'categories') {
    return (item as CategoryResponse).name;
  }

  if (resource === 'users') {
    const user = item as UserResponse;
    return `${user.name} (${user.identification})`;
  }

  if (resource === 'roles') {
    return (item as RoleResponse).name;
  }

  if (resource === 'inventories') {
    const inventory = item as InventoryResponse;
    return `${inventory.product.name} - ${inventory.company.name}`;
  }

  const order = item as OrderResponse;
  return `Orden #${order.id}`;
}

export function optionValue(resource: ResourceKey, item: ResourceResponse): string | number {
  if (resource === 'companies') {
    return (item as CompanyResponse).nit;
  }
  if (resource === 'products') {
    return (item as ProductResponse).code;
  }
  return (item as { id: number }).id;
}

export function mapOptions(resource: ResourceKey, items: ResourceResponse[]): SelectOption[] {
  return items.map((item) => normalizeActive(item)).map((item) => ({
    label: optionLabel(resource, item),
    value: optionValue(resource, item)
  }));
}

export function formFromResponse(resource: ResourceKey, item: ResourceResponse): Record<string, unknown> {
  const normalized = normalizeActive(item);

  if (resource === 'companies') {
    const company = normalized as CompanyResponse;
    return {
      nit: company.nit,
      name: company.name,
      address: company.address ?? '',
      phone: company.phone ?? '',
      isActive: activeValue(company)
    };
  }

  if (resource === 'products') {
    const product = normalized as ProductResponse;
    return {
      code: product.code,
      name: product.name,
      characteristics: product.characteristics ?? '',
      avatar: product.avatar ?? '',
      companyNit: product.company?.nit ?? '',
      categoryIds: product.categories?.map((category) => category.id) ?? [],
      prices: product.prices?.length ? product.prices : [{ currency: 'COP', amount: 0 }],
      isActive: activeValue(product)
    };
  }

  if (resource === 'categories') {
    const category = normalized as CategoryResponse;
    return {
      name: category.name,
      description: category.description ?? '',
      isActive: activeValue(category)
    };
  }

  if (resource === 'inventories') {
    const inventory = normalized as InventoryResponse;
    return {
      productCode: inventory.product?.code ?? '',
      companyNit: inventory.company?.nit ?? '',
      stock: inventory.stock,
      isActive: activeValue(inventory)
    };
  }

  if (resource === 'users') {
    const user = normalized as UserResponse;
    return {
      identification: user.identification,
      name: user.name,
      email: user.email ?? '',
      phone: user.phone ?? '',
      password: '',
      roleId: user.role?.id ?? '',
      isActive: activeValue(user)
    };
  }

  if (resource === 'roles') {
    const role = normalized as RoleResponse;
    return {
      name: role.name,
      isActive: activeValue(role)
    };
  }

  const order = normalized as OrderResponse;
  return {
    userId: order.user?.id ?? '',
    status: order.status,
    items: order.items.map((entry) => ({
      productCode: entry.productCode,
      quantity: entry.quantity,
      currency: entry.currency
    })),
    isActive: activeValue(order)
  };
}
