<script setup lang="ts">
import {
  Boxes,
  Building2,
  ClipboardList,
  LayoutDashboard,
  LogOut,
  Package,
  ShoppingBag,
  Shield,
  Tags,
  Users
} from '@lucide/vue';
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const auth = useAuthStore();
const router = useRouter();

const navItems = [
  { to: '/dashboard', label: 'Resumen', icon: LayoutDashboard },
  { to: '/dashboard/store', label: 'Tienda', icon: ShoppingBag },
  { to: '/dashboard/my-orders', label: 'Mis ordenes', icon: ClipboardList },
  { to: '/dashboard/companies', label: 'Empresas', icon: Building2 },
  { to: '/dashboard/products', label: 'Productos', icon: Package },
  { to: '/dashboard/categories', label: 'Categorias', icon: Tags },
  { to: '/dashboard/inventories', label: 'Inventario', icon: Boxes },
  { to: '/dashboard/orders', label: 'Ordenes', icon: ClipboardList },
  { to: '/dashboard/users', label: 'Usuarios', icon: Users },
  { to: '/dashboard/roles', label: 'Roles', icon: Shield }
];

const externalNavItems = [
  { to: '/dashboard/companies', label: 'Empresas', icon: Building2 },
];

const filteredNavItems = computed(() => {
  if (auth.isExternal) {
    return externalNavItems;
  }
  if (auth.isClient) {
    return navItems.filter((item) => item.to === '/dashboard/store' || item.to === '/dashboard/my-orders');
  }
  return navItems;
});

const initials = computed(() => {
  const name = auth.currentUser?.name ?? 'U';
  return name
    .split(' ')
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase();
});

async function logout() {
  auth.logout();
  await router.push({ name: 'public-store' });
}
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-dot">LT</div>
        <div>
          <strong>Lite Thinking</strong>
          <span>{{ auth.isClient ? 'Tienda online' : auth.isExternal ? 'Consulta externa' : 'Backoffice' }}</span>
        </div>
      </div>

      <nav class="nav-list">
        <RouterLink v-for="item in filteredNavItems" :key="item.to" :to="item.to" class="nav-item">
          <component :is="item.icon" :size="18" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>

    <div class="main-shell">
      <header class="topbar">
        <div>
          <span class="eyebrow">{{ auth.isClient ? 'Compra online' : auth.isExternal ? 'Consulta externa' : 'Panel operativo' }}</span>
          <h1>{{ auth.isClient ? 'Catalogo de productos' : auth.isExternal ? 'Empresas' : 'Gestion comercial' }}</h1>
        </div>
        <div class="user-area">
          <div class="avatar">{{ initials }}</div>
          <div class="user-copy">
            <strong>{{ auth.currentUser?.name ?? 'Usuario' }}</strong>
            <span>{{ auth.currentUser?.role?.name ?? 'Sin rol' }}</span>
          </div>
          <button class="icon-button" type="button" title="Cerrar sesion" @click="logout">
            <LogOut :size="18" />
          </button>
        </div>
      </header>

      <main class="content">
        <RouterView />
      </main>
    </div>
  </div>
</template>
