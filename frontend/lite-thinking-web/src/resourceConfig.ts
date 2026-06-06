import type { ResourceConfig } from './types';

export const resourceConfigs: Record<string, ResourceConfig> = {
  companies: {
    key: 'companies',
    title: 'Empresas',
    endpoint: '/companies',
    idKey: 'nit',
    description: 'Gestion de proveedores o empresas asociadas a productos e inventario.',
    createLabel: 'Nueva empresa',
    columns: [
      { key: 'nit', label: 'NIT' },
      { key: 'name', label: 'Nombre' },
      { key: 'phone', label: 'Telefono' },
      { key: 'address', label: 'Direccion' },
      { key: 'isActive', label: 'Estado', format: 'status' }
    ],
    fields: [
      { key: 'nit', label: 'NIT', type: 'text', required: true, readonlyOnEdit: true },
      { key: 'name', label: 'Nombre', type: 'text', required: true },
      { key: 'address', label: 'Direccion', type: 'text' },
      { key: 'phone', label: 'Telefono', type: 'text' },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: { nit: '', name: '', address: '', phone: '', isActive: true }
  },
  products: {
    key: 'products',
    title: 'Productos',
    endpoint: '/products',
    idKey: 'code',
    description: 'Catalogo de productos con precios, empresa y categorias.',
    createLabel: 'Nuevo producto',
    columns: [
      { key: 'code', label: 'Codigo' },
      { key: 'name', label: 'Nombre' },
      { key: 'company.name', label: 'Empresa' },
      { key: 'categories', label: 'Categorias', format: 'list' },
      { key: 'isActive', label: 'Estado', format: 'status' }
    ],
    fields: [
      { key: 'code', label: 'Codigo', type: 'text', required: true, readonlyOnEdit: true },
      { key: 'name', label: 'Nombre', type: 'text', required: true },
      { key: 'characteristics', label: 'Caracteristicas', type: 'textarea' },
      { key: 'avatar', label: 'Imagen', type: 'file' },
      { key: 'companyNit', label: 'Empresa', type: 'select', required: true, optionsKey: 'companies' },
      { key: 'categoryIds', label: 'Categorias', type: 'multiselect', optionsKey: 'categories' },
      { key: 'prices', label: 'Precios', type: 'prices', required: true },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: {
      code: '',
      name: '',
      characteristics: '',
      avatar: '',
      companyNit: '',
      categoryIds: [],
      prices: [{ currency: 'COP', amount: 0 }],
      isActive: true
    }
  },
  categories: {
    key: 'categories',
    title: 'Categorias',
    endpoint: '/categories',
    idKey: 'id',
    description: 'Clasificacion de productos.',
    createLabel: 'Nueva categoria',
    columns: [
      { key: 'name', label: 'Nombre' },
      { key: 'description', label: 'Descripcion' },
      { key: 'isActive', label: 'Estado', format: 'status' }
    ],
    fields: [
      { key: 'name', label: 'Nombre', type: 'text', required: true },
      { key: 'description', label: 'Descripcion', type: 'textarea' },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: { name: '', description: '', isActive: true }
  },
  inventories: {
    key: 'inventories',
    title: 'Inventario',
    endpoint: '/inventories',
    idKey: 'id',
    description: 'Stock de productos por empresa.',
    createLabel: 'Nuevo inventario',
    columns: [
      { key: 'product.name', label: 'Producto' },
      { key: 'company.name', label: 'Empresa' },
      { key: 'stock', label: 'Stock' },
      { key: 'isActive', label: 'Estado', format: 'status' }
    ],
    fields: [
      { key: 'productCode', label: 'Producto', type: 'select', required: true, optionsKey: 'products' },
      { key: 'companyNit', label: 'Empresa', type: 'select', required: true, optionsKey: 'companies' },
      { key: 'stock', label: 'Stock', type: 'number', required: true },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: { productCode: '', companyNit: '', stock: 0, isActive: true }
  },
  users: {
    key: 'users',
    title: 'Usuarios',
    endpoint: '/users',
    idKey: 'id',
    description: 'Usuarios del sistema y asignacion de roles.',
    createLabel: 'Nuevo usuario',
    columns: [
      { key: 'identification', label: 'Identificacion' },
      { key: 'name', label: 'Nombre' },
      { key: 'email', label: 'Correo' },
      { key: 'role.name', label: 'Rol' },
      { key: 'isActive', label: 'Estado', format: 'status' }
    ],
    fields: [
      { key: 'identification', label: 'Identificacion', type: 'text', required: true },
      { key: 'name', label: 'Nombre', type: 'text', required: true },
      { key: 'email', label: 'Correo', type: 'text' },
      { key: 'phone', label: 'Telefono', type: 'text' },
      { key: 'password', label: 'Contrasena', type: 'password', required: true },
      { key: 'roleId', label: 'Rol', type: 'select', required: true, optionsKey: 'roles' },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: { identification: '', name: '', email: '', phone: '', password: '', roleId: '', isActive: true }
  },
  roles: {
    key: 'roles',
    title: 'Roles',
    endpoint: '/roles',
    idKey: 'id',
    description: 'Roles usados para permisos de la aplicacion.',
    createLabel: 'Nuevo rol',
    columns: [
      { key: 'name', label: 'Nombre' },
      { key: 'isActive', label: 'Estado', format: 'status' }
    ],
    fields: [
      { key: 'name', label: 'Nombre', type: 'text', required: true },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: { name: '', isActive: true }
  },
  orders: {
    key: 'orders',
    title: 'Ordenes',
    endpoint: '/orders',
    idKey: 'id',
    description: 'Ordenes de usuarios con productos, cantidades y moneda.',
    createLabel: 'Nueva orden',
    allowUpdate: false,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'user.name', label: 'Usuario' },
      { key: 'status', label: 'Estado' },
      { key: 'total', label: 'Total', format: 'currency' },
      { key: 'orderDate', label: 'Fecha', format: 'date' }
    ],
    fields: [
      { key: 'userId', label: 'Usuario', type: 'select', required: true, optionsKey: 'users' },
      { key: 'status', label: 'Estado', type: 'text', required: true },
      { key: 'items', label: 'Items', type: 'orderItems', required: true },
      { key: 'isActive', label: 'Activo', type: 'checkbox' }
    ],
    emptyForm: {
      userId: '',
      status: 'PENDING',
      items: [{ productCode: '', quantity: 1, currency: 'COP' }],
      isActive: true
    }
  }
};
