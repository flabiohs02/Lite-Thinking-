<script setup lang="ts">
import { LockKeyhole } from '@lucide/vue';
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getErrorMessage } from '../api/http';
import { useAuthStore } from '../stores/auth';

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();

const identification = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

async function submit() {
  loading.value = true;
  error.value = '';
  try {
    await auth.signIn({ identification: identification.value, password: password.value });
    const fallback = auth.isClient ? '/dashboard/store' : auth.isExternal ? '/dashboard/companies' : '/dashboard';
    await router.push(String(route.query.redirect ?? fallback));
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    loading.value = false;
  }
}

</script>

<template>
  <main class="login-page">
    <section class="login-panel">
      <div class="brand-mark"><LockKeyhole :size="28" /></div>
      <h1>Lite Thinking</h1>
      <p>Ingresa con tu identificacion y contrasena para gestionar el sistema.</p>

      <form class="login-form" @submit.prevent="submit">
        <label for="identification">Identificacion</label>
        <input id="identification" v-model="identification" type="text" autocomplete="username" required />

        <label for="password">Contrasena</label>
        <input id="password" v-model="password" type="password" autocomplete="current-password" required />

        <p v-if="error" class="error-message">{{ error }}</p>

        <button class="primary-button full" type="submit" :disabled="loading">
          {{ loading ? 'Ingresando...' : 'Ingresar' }}
        </button>

      </form>
    </section>
  </main>
</template>
