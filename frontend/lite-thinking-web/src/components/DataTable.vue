<script setup lang="ts">
import { Edit2, Trash2 } from '@lucide/vue';
import type { ResourceConfig, ResourceResponse } from '../types';
import { formatValue, getByPath, getResourceId } from '../utils/format';

defineProps<{
  config: ResourceConfig;
  rows: ResourceResponse[];
  loading: boolean;
  canMutate: boolean;
}>();

const emit = defineEmits<{
  edit: [row: ResourceResponse];
  delete: [row: ResourceResponse];
}>();
</script>

<template>
  <div class="table-shell">
    <table class="data-table">
      <thead>
        <tr>
          <th v-for="column in config.columns" :key="column.key">{{ column.label }}</th>
          <th class="actions-column">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td :colspan="config.columns.length + 1" class="empty-cell">Cargando datos...</td>
        </tr>
        <tr v-else-if="rows.length === 0">
          <td :colspan="config.columns.length + 1" class="empty-cell">No hay registros.</td>
        </tr>
        <tr v-for="row in rows" v-else :key="String(getResourceId(row, config.idKey))">
          <td v-for="column in config.columns" :key="column.key">
            <span v-if="column.format === 'status'" :class="['status-badge', getByPath(row, column.key) ? 'ok' : 'off']">
              {{ formatValue(getByPath(row, column.key), column.format) }}
            </span>
            <span v-else>{{ formatValue(getByPath(row, column.key), column.format) }}</span>
          </td>
          <td class="row-actions">
            <button
              class="icon-button"
              type="button"
              :disabled="!canMutate || config.allowUpdate === false"
              title="Editar"
              @click="emit('edit', row)"
            >
              <Edit2 :size="16" />
            </button>
            <button
              class="icon-button danger"
              type="button"
              :disabled="!canMutate"
              title="Eliminar"
              @click="emit('delete', row)"
            >
              <Trash2 :size="16" />
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
