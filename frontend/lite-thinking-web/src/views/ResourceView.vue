<script setup lang="ts">
import { Download, Mail, Plus, RefreshCw } from '@lucide/vue';
import { computed, onMounted, ref, watch } from 'vue';
import { prepareInventoryPdfEmail } from '../api/reports';
import { createResource, deleteResource, listResource, updateResource } from '../api/resources';
import { getErrorMessage } from '../api/http';
import ConfirmDialog from '../components/ConfirmDialog.vue';
import DataTable from '../components/DataTable.vue';
import ResourceForm from '../components/ResourceForm.vue';
import { resourceConfigs } from '../resourceConfig';
import { useAuthStore } from '../stores/auth';
import type { ResourceKey, ResourceResponse, SelectOption } from '../types';
import { cloneForm, formFromResponse, mapOptions, normalizeActive } from '../utils/forms';
import { getResourceId } from '../utils/format';
import { downloadFilteredInventoryPdf, inventoryPdfBlob, type InventoryPdfOptions } from '../utils/inventoryPdf';

const props = defineProps<{
  resource: ResourceKey;
}>();

const auth = useAuthStore();
const rows = ref<ResourceResponse[]>([]);
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const error = ref('');
const formOpen = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const form = ref<Record<string, unknown>>({});
const editingId = ref<string | number | null>(null);
const deleteTarget = ref<ResourceResponse | null>(null);
const options = ref<Partial<Record<ResourceKey, SelectOption[]>>>({});
const emailDialogOpen = ref(false);
const reportEmail = ref('');
const sendingReport = ref(false);
const reportMessage = ref('');
const selectedInventoryCompany = ref('');
const selectedInventoryMonth = ref('');
const selectedInventoryYear = ref('');

const config = computed(() => resourceConfigs[props.resource]);
const canUseInventoryReports = computed(() => props.resource === 'inventories' && auth.isAdmin);
const monthOptions = [
  { value: '1', label: 'Enero' },
  { value: '2', label: 'Febrero' },
  { value: '3', label: 'Marzo' },
  { value: '4', label: 'Abril' },
  { value: '5', label: 'Mayo' },
  { value: '6', label: 'Junio' },
  { value: '7', label: 'Julio' },
  { value: '8', label: 'Agosto' },
  { value: '9', label: 'Septiembre' },
  { value: '10', label: 'Octubre' },
  { value: '11', label: 'Noviembre' },
  { value: '12', label: 'Diciembre' }
];

const availableInventoryCompanies = computed(() => {
  const companies = new Map<string, string>();
  rows.value.forEach((row) => {
    const inventory = normalizeActive(row) as { company?: { nit?: string; name?: string } };
    if (inventory.company?.nit) {
      companies.set(inventory.company.nit, inventory.company.name ?? inventory.company.nit);
    }
  });
  return Array.from(companies.entries())
    .map(([nit, name]) => ({ nit, name }))
    .sort((a, b) => a.name.localeCompare(b.name));
});

const availableInventoryYears = computed(() => {
  const years = new Set<string>();
  rows.value.forEach((row) => {
    const createdAt = (row as { createdAt?: string }).createdAt;
    if (createdAt) {
      years.add(String(new Date(createdAt).getFullYear()));
    }
  });
  return Array.from(years).sort((a, b) => Number(b) - Number(a));
});

const filteredInventoryRows = computed(() =>
  rows.value.filter((row) => {
    const inventory = row as { company?: { nit?: string }; createdAt?: string };
    const createdAt = inventory.createdAt ? new Date(inventory.createdAt) : null;
    const companyMatch = !selectedInventoryCompany.value || inventory.company?.nit === selectedInventoryCompany.value;
    const monthMatch = !selectedInventoryMonth.value || (createdAt && createdAt.getMonth() + 1 === Number(selectedInventoryMonth.value));
    const yearMatch = !selectedInventoryYear.value || (createdAt && createdAt.getFullYear() === Number(selectedInventoryYear.value));
    return companyMatch && monthMatch && yearMatch;
  })
);

const displayRows = computed(() => (canUseInventoryReports.value ? filteredInventoryRows.value : rows.value));

async function loadRows() {
  loading.value = true;
  error.value = '';
  try {
    rows.value = (await listResource(props.resource)).map((item) => normalizeActive(item));
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    loading.value = false;
  }
}

async function loadOptions() {
  const optionKeys = [...new Set(config.value.fields.map((field) => field.optionsKey).filter(Boolean))] as ResourceKey[];
  const loaded: Partial<Record<ResourceKey, SelectOption[]>> = {};

  await Promise.all(
    optionKeys.map(async (key) => {
      const items = await listResource(key);
      loaded[key] = mapOptions(key, items);
    })
  );

  options.value = loaded;
}

function openCreate() {
  formMode.value = 'create';
  editingId.value = null;
  form.value = cloneForm(config.value.emptyForm);
  formOpen.value = true;
}

function openEdit(row: ResourceResponse) {
  if (config.value.allowUpdate === false) {
    return;
  }
  formMode.value = 'edit';
  editingId.value = getResourceId(row, config.value.idKey);
  form.value = formFromResponse(props.resource, row);
  formOpen.value = true;
}

function closeForm() {
  formOpen.value = false;
}

function normalizePayload(payload: Record<string, unknown>) {
  const next = cloneForm(payload);

  if ('active' in next && !('isActive' in next)) {
    next.isActive = Boolean(next.active);
  }
  delete next.active;

  if ('isActive' in next) {
    next.isActive = Boolean(next.isActive);
  }

  if (props.resource === 'users' || props.resource === 'orders') {
    next.roleId = next.roleId === undefined ? next.roleId : Number(next.roleId);
    next.userId = next.userId === undefined ? next.userId : Number(next.userId);
  }

  if (props.resource === 'inventories') {
    next.stock = Number(next.stock ?? 0);
  }

  if (props.resource === 'products') {
    next.categoryIds = Array.isArray(next.categoryIds) ? next.categoryIds.map(Number) : [];
  }

  if (props.resource === 'orders' && Array.isArray(next.items)) {
    next.items = next.items.map((item) => ({
      ...(item as Record<string, unknown>),
      quantity: Number((item as Record<string, unknown>).quantity ?? 1)
    }));
  }

  return next;
}

async function save() {
  if (!auth.isAdmin) {
    error.value = 'Solo el rol ADMIN puede guardar cambios.';
    return;
  }

  saving.value = true;
  error.value = '';
  try {
    const payload = normalizePayload(form.value);
    if (formMode.value === 'create') {
      await createResource(props.resource, payload);
    } else if (editingId.value !== null) {
      await updateResource(props.resource, editingId.value, payload);
    }
    closeForm();
    await loadRows();
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    saving.value = false;
  }
}

async function confirmDelete() {
  if (!deleteTarget.value || !auth.isAdmin) {
    return;
  }

  deleting.value = true;
  error.value = '';
  try {
    await deleteResource(props.resource, getResourceId(deleteTarget.value, config.value.idKey));
    deleteTarget.value = null;
    await loadRows();
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    deleting.value = false;
  }
}

async function refresh() {
  await Promise.all([loadRows(), loadOptions()]);
}

function inventoryPdfOptions(): InventoryPdfOptions {
  return {
    companyName:
      availableInventoryCompanies.value.find((company) => company.nit === selectedInventoryCompany.value)?.name ?? 'Todas',
    monthName: monthOptions.find((month) => month.value === selectedInventoryMonth.value)?.label ?? 'Todos',
    year: selectedInventoryYear.value || 'Todos'
  };
}

function inventoryPdfFileName() {
  const company = selectedInventoryCompany.value || 'todas';
  const year = selectedInventoryYear.value || '';
  const month = selectedInventoryMonth.value ? selectedInventoryMonth.value.padStart(2, '0') : '';
  const suffix = [company, year, month].filter(Boolean).join('-');
  return `inventario-${suffix || 'todas'}.pdf`;
}

async function downloadInventoryReport() {
  reportMessage.value = '';
  if (filteredInventoryRows.value.length === 0) {
    reportMessage.value = 'No hay inventario para los filtros seleccionados.';
    return;
  }
  await downloadFilteredInventoryPdf(filteredInventoryRows.value, inventoryPdfOptions(), inventoryPdfFileName());
}

function openEmailDialog() {
  reportEmail.value = '';
  reportMessage.value = '';
  emailDialogOpen.value = true;
}

function isValidEmail(email: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

async function prepareEmailReport() {
  if (!isValidEmail(reportEmail.value)) {
    reportMessage.value = 'Ingresa un correo valido.';
    return;
  }

  sendingReport.value = true;
  reportMessage.value = '';
  try {
    if (filteredInventoryRows.value.length === 0) {
      reportMessage.value = 'No hay inventario para los filtros seleccionados.';
      return;
    }
    const response = await prepareInventoryPdfEmail(
      reportEmail.value,
      await inventoryPdfBlob(filteredInventoryRows.value, inventoryPdfOptions())
    );
    reportMessage.value = response.message;
  } catch (err) {
    reportMessage.value = getErrorMessage(err);
  } finally {
    sendingReport.value = false;
  }
}

onMounted(refresh);

watch(
  () => props.resource,
  async () => {
    formOpen.value = false;
    deleteTarget.value = null;
    await refresh();
  }
);
</script>

<template>
  <section class="page-section">
    <div class="section-heading">
      <div>
        <h2>{{ config.title }}</h2>
        <p>{{ config.description }}</p>
      </div>
      <div class="heading-actions">
        <button
          v-if="canUseInventoryReports"
          class="secondary-button"
          type="button"
          :disabled="rows.length === 0"
          @click="downloadInventoryReport"
        >
          <Download :size="16" /> Descargar PDF
        </button>
        <button
          v-if="canUseInventoryReports"
          class="secondary-button"
          type="button"
          :disabled="rows.length === 0"
          @click="openEmailDialog"
        >
          <Mail :size="16" /> Enviar por correo
        </button>
        <button class="secondary-button" type="button" @click="refresh">
          <RefreshCw :size="16" /> Actualizar
        </button>
        <button class="primary-button" type="button" :disabled="!auth.isAdmin" @click="openCreate">
          <Plus :size="16" /> {{ config.createLabel }}
        </button>
      </div>
    </div>

    <p v-if="!auth.isAdmin" class="notice">Tu rol permite consultar datos. Crear, actualizar y eliminar requiere ADMIN.</p>
    <p v-if="error" class="error-message">{{ error }}</p>
    <p v-if="reportMessage" class="notice">{{ reportMessage }}</p>

    <div v-if="canUseInventoryReports" class="report-filter-panel">
      <label>
        Empresa
        <select v-model="selectedInventoryCompany">
          <option value="">Todas</option>
          <option v-for="company in availableInventoryCompanies" :key="company.nit" :value="company.nit">
            {{ company.name }}
          </option>
        </select>
      </label>
      <label>
        Mes
        <select v-model="selectedInventoryMonth">
          <option value="">Todos</option>
          <option v-for="month in monthOptions" :key="month.value" :value="month.value">
            {{ month.label }}
          </option>
        </select>
      </label>
      <label>
        Año
        <select v-model="selectedInventoryYear">
          <option value="">Todos</option>
          <option v-for="year in availableInventoryYears" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
      </label>
      <span class="report-filter-count">{{ filteredInventoryRows.length }} registros filtrados</span>
    </div>

    <div v-if="formOpen" class="form-panel">
      <div class="form-panel-header">
        <h3>{{ formMode === 'create' ? config.createLabel : `Editar ${config.title}` }}</h3>
      </div>
      <ResourceForm
        v-model="form"
        :config="config"
        :mode="formMode"
        :options="options"
        :busy="saving"
        @submit="save"
        @cancel="closeForm"
      />
    </div>

    <DataTable
      :config="config"
      :rows="displayRows"
      :loading="loading"
      :can-mutate="auth.isAdmin"
      @edit="openEdit"
      @delete="deleteTarget = $event"
    />

    <ConfirmDialog
      :open="Boolean(deleteTarget)"
      title="Eliminar registro"
      message="Esta accion no se puede deshacer."
      :busy="deleting"
      @cancel="deleteTarget = null"
      @confirm="confirmDelete"
    />

    <div v-if="emailDialogOpen" class="modal-backdrop" role="dialog" aria-modal="true">
      <section class="confirm-dialog email-dialog">
        <div class="dialog-icon"><Mail :size="22" /></div>
        <h2>Preparar PDF por correo</h2>
        <p>Ingresa el correo destino. El PDF se prepara localmente; falta configurar el proveedor para envio real.</p>
        <label class="email-field" for="inventory-report-email">
          Correo destino
          <input
            id="inventory-report-email"
            v-model="reportEmail"
            type="email"
            placeholder="correo@empresa.com"
            :disabled="sendingReport"
          />
        </label>
        <p v-if="reportMessage" class="notice">{{ reportMessage }}</p>
        <div class="dialog-actions">
          <button class="secondary-button" type="button" :disabled="sendingReport" @click="emailDialogOpen = false">
            Cerrar
          </button>
          <button class="primary-button" type="button" :disabled="sendingReport" @click="prepareEmailReport">
            {{ sendingReport ? 'Preparando...' : 'Preparar PDF' }}
          </button>
        </div>
      </section>
    </div>
  </section>
</template>
