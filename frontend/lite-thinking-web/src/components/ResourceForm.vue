<script setup lang="ts">
import { Plus, X } from '@lucide/vue';
import type { FieldConfig, ResourceConfig, ResourceKey, SelectOption } from '../types';

const props = defineProps<{
  config: ResourceConfig;
  modelValue: Record<string, unknown>;
  mode: 'create' | 'edit';
  options: Partial<Record<ResourceKey, SelectOption[]>>;
  busy: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: Record<string, unknown>];
  submit: [];
  cancel: [];
}>();

function updateField(key: string, value: unknown) {
  emit('update:modelValue', { ...props.modelValue, [key]: value });
}

function textValue(key: string): string {
  const value = props.modelValue[key];
  return value === undefined || value === null ? '' : String(value);
}

function numberValue(key: string): number {
  const value = props.modelValue[key];
  return typeof value === 'number' ? value : Number(value ?? 0);
}

function booleanValue(key: string): boolean {
  return Boolean(props.modelValue[key]);
}

function arrayValue<T>(key: string): T[] {
  const value = props.modelValue[key];
  return Array.isArray(value) ? (value as T[]) : [];
}

function fieldOptions(field: FieldConfig): SelectOption[] {
  return field.optionsKey ? props.options[field.optionsKey] ?? [] : [];
}

function addPrice(key: string) {
  updateField(key, [...arrayValue<Record<string, unknown>>(key), { currency: 'COP', amount: 0 }]);
}

function updatePrice(key: string, index: number, field: string, value: unknown) {
  const next = [...arrayValue<Record<string, unknown>>(key)];
  next[index] = { ...next[index], [field]: field === 'amount' ? Number(value) : value };
  updateField(key, next);
}

function removeArrayItem(key: string, index: number) {
  const next = [...arrayValue<Record<string, unknown>>(key)];
  next.splice(index, 1);
  updateField(key, next);
}

function addOrderItem(key: string) {
  updateField(key, [...arrayValue<Record<string, unknown>>(key), { productCode: '', quantity: 1, currency: 'COP' }]);
}

function updateOrderItem(key: string, index: number, field: string, value: unknown) {
  const next = [...arrayValue<Record<string, unknown>>(key)];
  next[index] = { ...next[index], [field]: field === 'quantity' ? Number(value) : value };
  updateField(key, next);
}

function updateMultiselect(key: string, event: Event) {
  const select = event.target as HTMLSelectElement;
  const values = Array.from(select.selectedOptions).map((option) => Number(option.value));
  updateField(key, values);
}

function updateFile(key: string, event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) {
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    updateField(key, typeof reader.result === 'string' ? reader.result : '');
  };
  reader.readAsDataURL(file);
}
</script>

<template>
  <form class="resource-form" @submit.prevent="emit('submit')">
    <div
      v-for="field in config.fields"
      :key="field.key"
      class="form-field"
      :class="{ wide: field.type === 'textarea' || field.type === 'prices' || field.type === 'orderItems' || field.type === 'file' }"
    >
      <label :for="field.key">
        {{ field.label }}
        <span v-if="field.required">*</span>
      </label>

      <textarea
        v-if="field.type === 'textarea'"
        :id="field.key"
        :value="textValue(field.key)"
        :placeholder="field.placeholder"
        rows="4"
        @input="updateField(field.key, ($event.target as HTMLTextAreaElement).value)"
      />

      <input
        v-else-if="field.type === 'text' || field.type === 'password'"
        :id="field.key"
        :type="field.type"
        :value="textValue(field.key)"
        :readonly="mode === 'edit' && field.readonlyOnEdit"
        :placeholder="field.placeholder"
        :required="field.required"
        @input="updateField(field.key, ($event.target as HTMLInputElement).value)"
      />

      <input
        v-else-if="field.type === 'number'"
        :id="field.key"
        type="number"
        min="0"
        :value="numberValue(field.key)"
        :required="field.required"
        @input="updateField(field.key, Number(($event.target as HTMLInputElement).value))"
      />

      <div v-else-if="field.type === 'file'" class="file-editor">
        <input
          :id="field.key"
          type="file"
          accept="image/*"
          :required="field.required && !textValue(field.key)"
          @change="updateFile(field.key, $event)"
        />
        <div v-if="textValue(field.key)" class="avatar-preview">
          <img :src="textValue(field.key)" alt="Vista previa" />
          <button class="secondary-button compact" type="button" @click="updateField(field.key, '')">
            <X :size="16" /> Limpiar imagen
          </button>
        </div>
      </div>

      <label v-else-if="field.type === 'checkbox'" class="check-row">
        <input
          type="checkbox"
          :checked="booleanValue(field.key)"
          @change="updateField(field.key, ($event.target as HTMLInputElement).checked)"
        />
        Registro activo
      </label>

      <select
        v-else-if="field.type === 'select'"
        :id="field.key"
        :value="textValue(field.key)"
        :required="field.required"
        @change="updateField(field.key, ($event.target as HTMLSelectElement).value)"
      >
        <option value="">Seleccionar</option>
        <option v-for="option in fieldOptions(field)" :key="String(option.value)" :value="option.value">
          {{ option.label }}
        </option>
      </select>

      <select
        v-else-if="field.type === 'multiselect'"
        :id="field.key"
        multiple
        :value="arrayValue<number>(field.key).map(String)"
        @change="updateMultiselect(field.key, $event)"
      >
        <option v-for="option in fieldOptions(field)" :key="String(option.value)" :value="option.value">
          {{ option.label }}
        </option>
      </select>

      <div v-else-if="field.type === 'prices'" class="nested-editor">
        <div v-for="(price, index) in arrayValue<Record<string, unknown>>(field.key)" :key="index" class="nested-row">
          <input
            type="text"
            placeholder="Moneda"
            :value="price.currency"
            @input="updatePrice(field.key, index, 'currency', ($event.target as HTMLInputElement).value)"
          />
          <input
            type="number"
            min="0"
            placeholder="Monto"
            :value="price.amount"
            @input="updatePrice(field.key, index, 'amount', ($event.target as HTMLInputElement).value)"
          />
          <button class="icon-button danger" type="button" title="Quitar precio" @click="removeArrayItem(field.key, index)">
            <X :size="16" />
          </button>
        </div>
        <button class="secondary-button compact" type="button" @click="addPrice(field.key)">
          <Plus :size="16" /> Precio
        </button>
      </div>

      <div v-else-if="field.type === 'orderItems'" class="nested-editor">
        <div v-for="(item, index) in arrayValue<Record<string, unknown>>(field.key)" :key="index" class="nested-row order-row">
          <select
            :value="item.productCode"
            @change="updateOrderItem(field.key, index, 'productCode', ($event.target as HTMLSelectElement).value)"
          >
            <option value="">Producto</option>
            <option v-for="option in options.products ?? []" :key="String(option.value)" :value="option.value">
              {{ option.label }}
            </option>
          </select>
          <input
            type="number"
            min="1"
            :value="item.quantity"
            @input="updateOrderItem(field.key, index, 'quantity', ($event.target as HTMLInputElement).value)"
          />
          <input
            type="text"
            placeholder="Moneda"
            :value="item.currency"
            @input="updateOrderItem(field.key, index, 'currency', ($event.target as HTMLInputElement).value)"
          />
          <button class="icon-button danger" type="button" title="Quitar item" @click="removeArrayItem(field.key, index)">
            <X :size="16" />
          </button>
        </div>
        <button class="secondary-button compact" type="button" @click="addOrderItem(field.key)">
          <Plus :size="16" /> Item
        </button>
      </div>
    </div>

    <div class="form-actions">
      <button class="secondary-button" type="button" :disabled="busy" @click="emit('cancel')">Cancelar</button>
      <button class="primary-button" type="submit" :disabled="busy">
        {{ busy ? 'Guardando...' : 'Guardar' }}
      </button>
    </div>
  </form>
</template>
