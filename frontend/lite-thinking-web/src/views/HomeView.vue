<script setup lang="ts">
import { Building2, Package, ShoppingCart, Users } from '@lucide/vue';
import { onMounted, ref } from 'vue';
import { listCompanies, listOrders, listProducts, listUsers } from '../api/resources';

const loading = ref(true);
const cards = ref([
  { label: 'Empresas', value: 0, icon: Building2 },
  { label: 'Productos', value: 0, icon: Package },
  { label: 'Usuarios', value: 0, icon: Users },
  { label: 'Ordenes', value: 0, icon: ShoppingCart }
]);

onMounted(async () => {
  try {
    const [companies, products, users, orders] = await Promise.all([
      listCompanies(),
      listProducts(),
      listUsers(),
      listOrders()
    ]);
    cards.value = [
      { label: 'Empresas', value: companies.length, icon: Building2 },
      { label: 'Productos', value: products.length, icon: Package },
      { label: 'Usuarios', value: users.length, icon: Users },
      { label: 'Ordenes', value: orders.length, icon: ShoppingCart }
    ];
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <section class="page-section">
    <div class="section-heading">
      <div>
        <h2>Resumen</h2>
        <p>Estado general de los modulos principales.</p>
      </div>
    </div>

    <div class="metric-grid">
      <article v-for="card in cards" :key="card.label" class="metric-card">
        <component :is="card.icon" :size="22" />
        <span>{{ card.label }}</span>
        <strong>{{ loading ? '-' : card.value }}</strong>
      </article>
    </div>
  </section>
</template>
