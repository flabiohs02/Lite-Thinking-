import type { ResourceResponse } from '../types';

export function getByPath(item: unknown, path: string): unknown {
  return path.split('.').reduce<unknown>((value, part) => {
    if (value && typeof value === 'object' && part in value) {
      return (value as Record<string, unknown>)[part];
    }
    return undefined;
  }, item);
}

export function formatValue(value: unknown, format?: string): string {
  if (format === 'status') {
    return value ? 'Activo' : 'Inactivo';
  }

  if (format === 'currency') {
    const amount = typeof value === 'number' ? value : Number(value ?? 0);
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0
    }).format(amount);
  }

  if (format === 'date' && typeof value === 'string') {
    return new Intl.DateTimeFormat('es-CO', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  if (format === 'list' && Array.isArray(value)) {
    return value
      .map((entry) => {
        if (entry && typeof entry === 'object' && 'name' in entry) {
          return String((entry as { name: unknown }).name);
        }
        return String(entry);
      })
      .join(', ');
  }

  if (value === null || value === undefined || value === '') {
    return '-';
  }

  return String(value);
}

export function getResourceId(item: ResourceResponse, idKey: string): string | number {
  const value = getByPath(item, idKey);
  return typeof value === 'number' || typeof value === 'string' ? value : '';
}
