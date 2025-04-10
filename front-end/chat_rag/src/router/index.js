import { createRouter, createWebHistory } from 'vue-router'
import AuthView from '@/views/AuthView.vue'
import ChatView from "@/views/ChatView.vue";

const routes = [
    {
        path: '/',
        name: 'auth',
        component: AuthView
    },
    // 2. 新增聊天页路由
    {
        path: '/chat',
        name: 'chat',
        component: ChatView,
        meta: {
            requiresAuth: true // 需要登录才能访问
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    // 1. 检查目标路由是否需要认证
    if (to.meta.requiresAuth) {
        // 2. 检查localStorage中是否有token
        const token = localStorage.getItem('token')

        if (token) {
            // 3. (可选) 验证token有效性
            // 可以在这里添加API验证token的逻辑

            next() // 放行
        } else {
            // 4. 无token则重定向到登录页
            next('/')
        }
    } else {
        next() // 不需要认证的路由直接放行
    }
})

export default router