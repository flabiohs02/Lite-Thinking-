import { describe, it, expect, vi, beforeEach } from 'vitest';
import { resourceConfigs } from '../resourceConfig';
import type { ResourceKey } from '../types';

describe('resourceConfigs', () => {
  const allKeys: ResourceKey[] = ['companies', 'products', 'categories', 'inventories', 'users', 'roles', 'orders'];

  it('debe tener configuración para todos los recursos esperados', () => {
    allKeys.forEach((key) => {
      expect(resourceConfigs[key]).toBeDefined();
      expect(resourceConfigs[key].key).toBe(key);
    });
  });

  it('cada recurso debe tener titulo, endpoint, idKey y description', () => {
    allKeys.forEach((key) => {
      const config = resourceConfigs[key];
      expect(config.title).toBeTruthy();
      expect(config.endpoint).toBeTruthy();
      expect(config.idKey).toBeTruthy();
      expect(config.description).toBeTruthy();
    });
  });

  it('cada recurso debe tener al menos una columna', () => {
    allKeys.forEach((key) => {
      expect(resourceConfigs[key].columns.length).toBeGreaterThan(0);
    });
  });

  it('cada recurso debe tener al menos un campo', () => {
    allKeys.forEach((key) => {
      expect(resourceConfigs[key].fields.length).toBeGreaterThan(0);
    });
  });

  it('cada recurso debe tener un emptyForm definido', () => {
    allKeys.forEach((key) => {
      expect(resourceConfigs[key].emptyForm).toBeDefined();
      expect(typeof resourceConfigs[key].emptyForm).toBe('object');
    });
  });

  it('companies debe usar nit como idKey', () => {
    expect(resourceConfigs.companies.idKey).toBe('nit');
  });

  it('products debe usar code como idKey', () => {
    expect(resourceConfigs.products.idKey).toBe('code');
  });

  it('orders debe tener allowUpdate en false', () => {
    expect(resourceConfigs.orders.allowUpdate).toBe(false);
  });

  it('products debe incluir campo de categorías multiselect', () => {
    const categoriesField = resourceConfigs.products.fields.find((f) => f.key === 'categoryIds');
    expect(categoriesField).toBeDefined();
    expect(categoriesField?.type).toBe('multiselect');
    expect(categoriesField?.optionsKey).toBe('categories');
  });

  it('products debe incluir campo de precios', () => {
    const pricesField = resourceConfigs.products.fields.find((f) => f.key === 'prices');
    expect(pricesField).toBeDefined();
    expect(pricesField?.type).toBe('prices');
    expect(pricesField?.required).toBe(true);
  });

  it('users debe incluir campo de password', () => {
    const passwordField = resourceConfigs.users.fields.find((f) => f.key === 'password');
    expect(passwordField).toBeDefined();
    expect(passwordField?.type).toBe('password');
    expect(passwordField?.required).toBe(true);
  });

  it('orders emptyForm debe iniciar con status PENDING', () => {
    expect(resourceConfigs.orders.emptyForm.status).toBe('PENDING');
  });

  it('todas las columnas deben tener key y label', () => {
    allKeys.forEach((key) => {
      resourceConfigs[key].columns.forEach((col) => {
        expect(col.key).toBeTruthy();
        expect(col.label).toBeTruthy();
      });
    });
  });

  it('todos los campos deben tener key, label y type', () => {
    allKeys.forEach((key) => {
      resourceConfigs[key].fields.forEach((field) => {
        expect(field.key).toBeTruthy();
        expect(field.label).toBeTruthy();
        expect(field.type).toBeTruthy();
      });
    });
  });

  it('los endpoints deben comenzar con /', () => {
    allKeys.forEach((key) => {
      expect(resourceConfigs[key].endpoint.startsWith('/')).toBe(true);
    });
  });
});
