<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { listOrdersByUser } from '../api/resources';
import { getErrorMessage } from '../api/http';
import { useAuthStore } from '../stores/auth';
import type { OrderResponse } from '../types';
import { normalizeActive } from '../utils/forms';

const auth = useAuthStore();
const orders = ref<OrderResponse[]>([]);
const loading = ref(false);
const error = ref('');

function formatCurrency(value: number) {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0
  }).format(value);
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('es-CO', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}

async function loadOrders() {
  if (!auth.currentUser?.id) {
    return;
  }

  loading.value = true;
  error.value = '';
  try {
    orders.value = (await listOrdersByUser(auth.currentUser.id)).map((order) => normalizeActive(order));
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    loading.value = false;
  }
}

onMounted(loadOrders);
</script>

<template>
  <section class="page-section">
    <div class="section-heading">
      <div>
        <h2>Mis ordenes</h2>
        <p>Historial de compras realizadas desde la tienda.</p>
      </div>
      <button class="secondary-button" type="button" @click="loadOrders">Actualizar</button>
    </div>

    <p v-if="error" class="error-message">{{ error }}</p>
    <div v-if="loading" class="shop-empty">Cargando ordenes...</div>
    <div v-else-if="orders.length === 0" class="shop-empty">Aun no tienes ordenes.</div>

    <div v-else class="orders-list">
      <article v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-card-header">
          <div>
            <span class="product-code">Orden #{{ order.id }}</span>
            <h3>{{ order.status }}</h3>
          </div>
          <strong>{{ formatCurrency(Number(order.total)) }}</strong>
        </div>
        <span class="muted">{{ formatDate(order.orderDate) }}</span>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item-row">
            <span>{{ item.productName }}</span>
            <span>{{ item.quantity }} x {{ formatCurrency(Number(item.price)) }} {{ item.currency }}</span>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>
