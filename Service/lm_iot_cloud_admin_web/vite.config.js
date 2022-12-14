import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import WindiCSS from 'vite-plugin-windicss'
// https://vitejs.dev/config/
export default defineConfig({
  resolve: {
    // 配置路径别名
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    host:'0.0.0.0' ,//ip地址
    port: 80, // 设置服务启动端口号
    // 请求代理
    proxy: {
      '/api': {
        // 这里的地址是后端数据接口的地址
        target: 'http://localhost:8055/',
        //rewrite: (path) => path.replace(/^\/admin/, ''),
        // 允许跨域
        changeOrigin: true
      }
    }
  },
  plugins: [
    vue(),
    WindiCSS(),
  ]
})