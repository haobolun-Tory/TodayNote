import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
        // 删除了 rewrite: path => path.replace(/^\/api/, '')
        // 这样前端请求 /api/auth/login 时，发送给后端的也是 /api/auth/login
      }
    }
  }
})
