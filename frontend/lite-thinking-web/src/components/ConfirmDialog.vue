<script setup lang="ts">
import { AlertTriangle } from '@lucide/vue';

defineProps<{
  open: boolean;
  title: string;
  message: string;
  busy?: boolean;
}>();

const emit = defineEmits<{
  cancel: [];
  confirm: [];
}>();
</script>

<template>
  <div v-if="open" class="modal-backdrop" role="dialog" aria-modal="true">
    <section class="confirm-dialog">
      <div class="dialog-icon"><AlertTriangle :size="22" /></div>
      <h2>{{ title }}</h2>
      <p>{{ message }}</p>
      <div class="dialog-actions">
        <button class="secondary-button" type="button" :disabled="busy" @click="emit('cancel')">Cancelar</button>
        <button class="danger-button" type="button" :disabled="busy" @click="emit('confirm')">
          {{ busy ? 'Eliminando...' : 'Eliminar' }}
        </button>
      </div>
    </section>
  </div>
</template>
