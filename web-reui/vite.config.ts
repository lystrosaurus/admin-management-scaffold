import path from 'path'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(import.meta.dirname, './src'),
    },
  },
  server: {
    // 专属端口：3000/3001 已被本机其他服务（Metabase / antd 版 frontend dev）占用
    port: 3100,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id: string) {
          if (
            id.includes('node_modules/react') ||
            id.includes('node_modules/react-dom') ||
            id.includes('node_modules/scheduler') ||
            id.includes('node_modules/react-router')
          ) {
            return 'vendor-react'
          }
          if (
            id.includes('node_modules/@tanstack') ||
            id.includes('node_modules/recharts') ||
            id.includes('node_modules/d3-') ||
            id.includes('node_modules/victory')
          ) {
            return 'vendor-dataviz'
          }
          if (id.includes('node_modules/axios') || id.includes('node_modules/dayjs')) {
            return 'vendor-utils'
          }
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
})
