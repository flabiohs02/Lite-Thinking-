import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from './stores/auth';
import DashboardLayout from './views/DashboardLayout.vue';
import LoginView from './views/LoginView.vue';
import ResourceView from './views/ResourceView.vue';
import HomeView from './views/HomeView.vue';
import StoreView from './views/StoreView.vue';
import MyOrdersView from './views/MyOrdersView.vue';
import type { ResourceKey } from './types';

const resources: ResourceKey[] = ['companies', 'products', 'categories', 'inventories', 'users', 'roles', 'orders'];

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'public-store', component: StoreView, props: { publicMode: true } },
    { path: '/login', name: 'login', component: LoginView },
    {
      path: '/dashboard',
      component: DashboardLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'home', component: HomeView },
        { path: 'store', name: 'store', component: StoreView },
        { path: 'my-orders', name: 'my-orders', component: MyOrdersView },
        ...resources.map((resource) => ({
          path: resource,
          name: resource,
          component: ResourceView,
          props: { resource }
        }))
      ]
    }
  ]
});

router.beforeEach(async (to) => {
  const auth = useAuthStore();

  if (!auth.ready) {
    await auth.bootstrap();
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }

  if (auth.isVisitor && to.path !== '/dashboard/companies' && to.name !== 'login') {
    return { name: 'companies' };
  }

  if (auth.isClient && !['store', 'my-orders', 'login'].includes(String(to.name))) {
    return { name: 'store' };
  }

  if (to.name === 'login' && auth.isAuthenticated) {
    return auth.isClient ? { name: 'store' } : { name: 'home' };
  }

  return true;
});

export default router;
