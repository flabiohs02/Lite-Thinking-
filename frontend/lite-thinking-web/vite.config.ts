import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// export default defineConfig({
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173
  }
});
// export default defineConfig({
//   plugins: [vue()],
//   base: '/lite/'
// })