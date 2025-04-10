import { createApp } from 'vue'
import App from './App.vue'
import router from './router'  // 确保路径正确

const app = createApp(App)
app.use(router)  // 挂载路由
app.mount('#app')

// 在main.js或专门的auth模块中
function getUserInfo() {
    const token = localStorage.getItem('token')
    if (token) {
        // 解析token或调用API获取用户信息
        // 示例：假设token包含用户ID
        const userId = token// 从"login:token:007ce62f..."中提取UUID
        return { id: userId, name: `用户${userId.slice(0, 4)}` }
    }
    return null
}