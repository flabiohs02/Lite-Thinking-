<script setup lang="ts">
import { Minus, Plus, ShoppingCart, Search, Tag, DollarSign } from '@lucide/vue';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createOrder, listCatalogProducts } from '../api/resources';
import { getErrorMessage } from '../api/http';
import { useAuthStore } from '../stores/auth';
import type { CatalogProductResponse, CategoryResponse, ProductPrice, ProductResponse } from '../types';
import { activeValue, normalizeActive } from '../utils/forms';

interface StoreProduct extends CatalogProductResponse {
  stock: number;
  selectedPrice: ProductPrice;
}

defineProps<{
  publicMode?: boolean;
}>();

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const products = ref<StoreProduct[]>([]);
const categories = ref<CategoryResponse[]>([]);
const loading = ref(false);
const ordering = ref(false);
const error = ref('');
const success = ref('');
const selectedCategory = ref('');
const minPrice = ref('');
const maxPrice = ref('');
const search = ref('');

const showOrderModal = ref(false);
const selectedProduct = ref<StoreProduct | null>(null);
const orderQuantity = ref(1);

function openOrderModal(product: StoreProduct) {
  selectedProduct.value = product;
  orderQuantity.value = 1;
  showOrderModal.value = true;
}

function closeOrderModal() {
  showOrderModal.value = false;
  selectedProduct.value = null;
}

function updateOrderQuantity(amount: number) {
  const target = orderQuantity.value + amount;
  if (selectedProduct.value && target >= 1 && target <= selectedProduct.value.stock) {
    orderQuantity.value = target;
  }
}

const filteredProducts = computed(() => {
  const min = minPrice.value === '' ? null : Number(minPrice.value);
  const max = maxPrice.value === '' ? null : Number(maxPrice.value);
  const text = search.value.trim().toLowerCase();

  return products.value.filter((product) => {
    const price = Number(product.selectedPrice.amount);
    const categoryMatch =
      !selectedCategory.value ||
      product.categories?.some((category) => String(category.id) === selectedCategory.value);
    const minMatch = min === null || price >= min;
    const maxMatch = max === null || price <= max;
    const textMatch =
      !text ||
      product.name.toLowerCase().includes(text) ||
      product.code.toLowerCase().includes(text) ||
      (product.characteristics ?? '').toLowerCase().includes(text);

    return categoryMatch && minMatch && maxMatch && textMatch;
  });
});

function formatCurrency(value: number, currency = 'COP') {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency,
    maximumFractionDigits: 0
  }).format(value);
}

function fallbackImage(product: ProductResponse) {
  const initials = product.name
    .split(' ')
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase();
  return `data:image/svg+xml;utf8,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="600" height="420"><rect width="100%" height="100%" fill="#e6edf3"/><text x="50%" y="52%" text-anchor="middle" font-family="Arial" font-size="72" fill="#0f6b67" font-weight="700">${initials}</text></svg>`
  )}`;
}

async function loadStore() {
  loading.value = true;
  error.value = '';
  try {
    const productRows = await listCatalogProducts();
    const categoryMap = new Map<number, CategoryResponse>();

    products.value = productRows
      .map((product) => normalizeActive(product))
      .filter((product) => activeValue(product))
      .map((product) => ({
        ...product,
        stock: Number(product.stockTotal ?? 0),
        selectedPrice: product.prices?.[0] ?? { currency: 'COP', amount: 0 }
      }))
      .filter((product) => product.stock > 1 && product.prices?.length);

    products.value.forEach((product) => {
      product.categories?.forEach((category) => {
        if (activeValue(category)) {
          categoryMap.set(category.id, category);
        }
      });
    });
    categories.value = Array.from(categoryMap.values()).sort((a, b) => a.name.localeCompare(b.name));
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    loading.value = false;
  }
}

async function submitDirectOrder() {
  if (!auth.currentUser?.id || !auth.isAuthenticated || auth.isVisitor) {
    await router.push({ name: 'login', query: { redirect: route.fullPath } });
    return;
  }

  if (!auth.isClient && !auth.isAdmin) {
    error.value = 'Debes iniciar sesión con rol CLIENT para comprar.';
    return;
  }

  if (!selectedProduct.value) return;

  ordering.value = true;
  error.value = '';
  success.value = '';

  try {
    await createOrder({
      userId: auth.currentUser.id,
      status: 'PENDING',
      isActive: true,
      items: [
        {
          productCode: selectedProduct.value.code,
          quantity: orderQuantity.value,
          currency: selectedProduct.value.selectedPrice.currency
        }
      ]
    });
    success.value = `Orden creada correctamente para ${selectedProduct.value.name}.`;
    closeOrderModal();
    await loadStore();
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    ordering.value = false;
  }
}

onMounted(async () => {
  await loadStore();
});
</script>

<template>
  <section class="store-page">
    <div v-if="publicMode" class="store-hero">
      <div>
        <span class="eyebrow">Tienda online</span>
        <h2>Productos disponibles</h2>
        <p>Explora el catálogo, arma tu carrito y accede para completar tu compra.</p>
      </div>
      <div class="heading-actions">
        <RouterLink v-if="!auth.isAuthenticated || auth.isVisitor" class="secondary-button" to="/login">Iniciar sesión</RouterLink>
        <button class="secondary-button" type="button" @click="loadStore">Actualizar</button>
      </div>
    </div>

    <p v-if="error" class="error-message">{{ error }}</p>
    <p v-if="success" class="success-message">{{ success }}</p>

    <div class="store-layout">
      <div class="store-main-content">
        <aside class="shop-filters-horizontal">
          <div class="filter-item search-box">
            <Search :size="18" />
            <input v-model="search" type="text" placeholder="Buscar por nombre o código..." />
          </div>

          <div class="filter-item select-box">
            <Tag :size="18" />
            <select v-model="selectedCategory">
              <option value="">Todas las categorías</option>
              <option v-for="category in categories" :key="category.id" :value="String(category.id)">
                {{ category.name }}
              </option>
            </select>
          </div>

          <div class="filter-item price-box">
            <DollarSign :size="18" />
            <div class="price-inputs">
              <input v-model="minPrice" type="number" placeholder="Mínimo" min="0" />
              <span class="price-separator">-</span>
              <input v-model="maxPrice" type="number" placeholder="Máximo" min="0" />
            </div>
          </div>
        </aside>

        <div class="product-grid">
          <article v-if="loading" class="shop-empty">Cargando productos...</article>
          <article v-else-if="filteredProducts.length === 0" class="shop-empty">No hay productos disponibles.</article>
          <article v-for="product in filteredProducts" v-else :key="product.code" class="product-card">
            <div class="product-image-container">
              <img class="product-image" :src="product.avatar || fallbackImage(product)" :alt="product.name" />
            </div>
            <div class="product-card-body">
              <div>
                <span class="product-code">{{ product.code }}</span>
                <h3>{{ product.name }}</h3>
                <p>{{ product.characteristics || 'Sin caracteristicas registradas.' }}</p>
              </div>
              <div class="category-pills">
                <span v-for="category in product.categories" :key="category.id">{{ category.name }}</span>
              </div>
              <div class="product-meta">
                <strong>{{ formatCurrency(Number(product.selectedPrice.amount), product.selectedPrice.currency) }}</strong>
                <span>Stock: {{ product.stock }}</span>
              </div>
              <button class="primary-button full" type="button" @click="openOrderModal(product)">
                <ShoppingCart :size="16" /> Agregar
              </button>
            </div>
          </article>
        </div>
      </div>
    </div>

    <!-- Modal de Crear Orden -->
    <div v-if="showOrderModal && selectedProduct" class="modal-backdrop" role="dialog" aria-modal="true" @click.self="closeOrderModal">
      <section class="order-dialog">
        <div class="order-dialog-header">
          <h3>Confirmar orden de compra</h3>
          <p>Revisa la cantidad y los detalles antes de enviar tu orden.</p>
        </div>

        <div class="order-dialog-product">
          <div class="dialog-product-img">
            <img :src="selectedProduct.avatar || fallbackImage(selectedProduct)" :alt="selectedProduct.name" />
          </div>
          <div class="dialog-product-info">
            <span class="dialog-product-code">{{ selectedProduct.code }}</span>
            <h4>{{ selectedProduct.name }}</h4>
            <p class="dialog-product-price">
              Precio unitario: <strong>{{ formatCurrency(Number(selectedProduct.selectedPrice.amount), selectedProduct.selectedPrice.currency) }}</strong>
            </p>
            <p class="dialog-product-stock">Stock disponible: {{ selectedProduct.stock }}</p>
          </div>
        </div>

        <div class="order-dialog-body">
          <div class="dialog-quantity-section">
            <span>Cantidad:</span>
            <div class="quantity-control">
              <button class="icon-button" type="button" :disabled="orderQuantity <= 1" @click="updateOrderQuantity(-1)">
                <Minus :size="14" />
              </button>
              <span>{{ orderQuantity }}</span>
              <button class="icon-button" type="button" :disabled="orderQuantity >= selectedProduct.stock" @click="updateOrderQuantity(1)">
                <Plus :size="14" />
              </button>
            </div>
          </div>

          <div class="dialog-total-section">
            <span>Total a pagar:</span>
            <strong>{{ formatCurrency(Number(selectedProduct.selectedPrice.amount) * orderQuantity, selectedProduct.selectedPrice.currency) }}</strong>
          </div>
        </div>

        <div class="dialog-actions">
          <button class="secondary-button" type="button" :disabled="ordering" @click="closeOrderModal">Cancelar</button>
          
          <RouterLink v-if="!auth.isAuthenticated || auth.isVisitor" to="/login" class="primary-button" style="text-decoration: none; text-align: center; justify-content: center; display: inline-flex;">
            Iniciar sesión para comprar
          </RouterLink>
          <button v-else class="primary-button" type="button" :disabled="ordering" @click="submitDirectOrder">
            {{ ordering ? 'Enviando...' : 'Crear orden' }}
          </button>
        </div>
      </section>
    </div>
  </section>
</template>
