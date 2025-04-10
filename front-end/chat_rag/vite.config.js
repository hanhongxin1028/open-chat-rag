import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'  // 用于解析路径别名

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src')  // 确保别名 @ 指向 src 目录
        }
    },
    server: {
        proxy: {
            // 配置代理解决跨域问题（可选）
            '/api': {
                target: 'http://localhost:8081',  // 后端接口地址
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
})