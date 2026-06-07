import { describe, it, expect, vi, beforeEach } from 'vitest';
import axios from 'axios';
import { getErrorMessage } from '../api/http';

describe('getErrorMessage', () => {
  it('debe retornar el mensaje string del response data', () => {
    const error = new axios.AxiosError(
      'Error',
      '400',
      undefined,
      undefined,
      { status: 400, data: 'Datos invalidos', headers: {}, statusText: 'Bad Request', config: {} as any }
    );
    expect(getErrorMessage(error)).toBe('Datos invalidos');
  });

  it('debe retornar data.message si existe', () => {
    const error = new axios.AxiosError(
      'Error',
      '400',
      undefined,
      undefined,
      { status: 400, data: { message: 'Campo requerido' }, headers: {}, statusText: 'Bad Request', config: {} as any }
    );
    expect(getErrorMessage(error)).toBe('Campo requerido');
  });

  it('debe retornar data.error si existe y no hay message', () => {
    const error = new axios.AxiosError(
      'Error',
      '500',
      undefined,
      undefined,
      { status: 500, data: { error: 'Internal server error' }, headers: {}, statusText: 'Error', config: {} as any }
    );
    expect(getErrorMessage(error)).toBe('Internal server error');
  });

  it('debe retornar mensaje de permisos para status 403', () => {
    const error = new axios.AxiosError(
      'Forbidden',
      '403',
      undefined,
      undefined,
      { status: 403, data: {}, headers: {}, statusText: 'Forbidden', config: {} as any }
    );
    expect(getErrorMessage(error)).toBe('No tienes permisos para esta accion.');
  });

  it('debe retornar mensaje HTTP generico si no hay data interpretable', () => {
    const error = new axios.AxiosError(
      'Error',
      '502',
      undefined,
      undefined,
      { status: 502, data: null, headers: {}, statusText: 'Bad Gateway', config: {} as any }
    );
    expect(getErrorMessage(error)).toBe('Error HTTP 502.');
  });

  it('debe retornar mensaje por defecto para errores no-Axios', () => {
    expect(getErrorMessage(new Error('algo'))).toBe('No se pudo completar la operacion.');
    expect(getErrorMessage('string error')).toBe('No se pudo completar la operacion.');
    expect(getErrorMessage(null)).toBe('No se pudo completar la operacion.');
  });
});
