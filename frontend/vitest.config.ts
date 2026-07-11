import { defineConfig } from 'vitest/config';
import path from 'path';

export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    globals: true,
    css: true,
    // e2e/ 是 Playwright 专属（依赖 @playwright/test，离线未安装），不被 vitest 收集
    exclude: ['**/node_modules/**', '**/dist/**', '**/e2e/**', '**/.git/**'],
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
