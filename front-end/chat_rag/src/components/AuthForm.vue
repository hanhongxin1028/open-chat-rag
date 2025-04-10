<template>
  <div class="auth-form">
    <div class="form-container">
      <h2>{{ isLoginMode ? '登录' : '注册' }}</h2>

      <form @submit.prevent="handleSubmit">
        <!-- 手机号输入 -->
        <div class="form-group">
          <label for="phone">手机号</label>
          <input
              id="phone"
              v-model="form.phone"
              type="text"
              placeholder="请输入手机号"
          />
        </div>

        <!-- 密码输入 -->
        <div class="form-group">
          <label for="password">密码</label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
          />
        </div>

        <!-- 确认密码输入 (注册时显示) -->
        <div class="form-group" v-if="!isLoginMode">
          <label for="confirmPassword">确认密码</label>
          <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
          />
        </div>

        <!-- 错误信息 -->
        <div class="error-message" v-if="errorMsg">
          {{ errorMsg }}
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <button type="submit" class="primary-btn">
            {{ isLoginMode ? '登录' : '注册' }}
          </button>
          <button type="button" class="secondary-btn" @click="toggleMode">
            {{ isLoginMode ? '去注册' : '去登录' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLoginMode: true,
      form: {
        phone: '',
        password: '',
        confirmPassword: ''
      },
      errorMsg: ''
    }
  },
  methods: {
    toggleMode() {
      this.isLoginMode = !this.isLoginMode
      this.errorMsg = ''
      this.form = {  // 重置整个表单
        phone: '',
        password: '',
        confirmPassword: ''
      }
    },
    async handleSubmit() {
      console.log('当前模式:', this.isLoginMode ? '登录' : '注册')
      console.log('表单数据:', JSON.stringify(this.form))
      // 验证输入
      if (!this.form.phone || !this.form.password) {
        this.errorMsg = '手机号或密码不能为空'
        return
      }

      if (!this.isLoginMode && this.form.password !== this.form.confirmPassword) {
        this.errorMsg = '两次输入密码不一致'
        return
      }

      try {
        if (this.isLoginMode) {
          await this.login()
        } else {
          await this.register()
        }
      } catch (error) {
        console.error('操作失败:', error)
        this.errorMsg = error.message || '操作失败，请重试'
      }
    },
    async login() {
      const response = await fetch('http://localhost:8081/user/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          phone: this.form.phone,
          password: this.form.password
        })
      })

      const data = await response.json()

      if (data.success) {
        // 登录成功，保存token并跳转
        localStorage.setItem('token', data.data)
        // 2. 跳转到受保护的聊天页
        this.$router.push('/chat')
      } else {
        throw new Error(data.errorMsg)
      }
    },
    async register() {
      const response = await fetch('http://localhost:8081/user/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          phone: this.form.phone,
          password: this.form.password,
          confirmPassword: this.form.confirmPassword
        })
      })

      const data = await response.json()

      if (data.success) {
        // 注册成功，切换到登录模式
        this.errorMsg = data.data
        this.isLoginMode = true
      } else {
        throw new Error(data.errorMsg)
      }
    }
  }
}
</script>

<style scoped>
.auth-form {
  width: 100%;
  padding: 2rem;
}

.form-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

input:focus {
  border-color: #646cff;
  outline: none;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
}

.primary-btn {
  flex: 1;
  background-color: #646cff;
  color: white;
  border: none;
  padding: 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.3s;
}

.primary-btn:hover {
  background-color: #535bf2;
}

.secondary-btn {
  flex: 1;
  background-color: transparent;
  color: #646cff;
  border: 1px solid #646cff;
  padding: 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.secondary-btn:hover {
  background-color: rgba(100, 108, 255, 0.1);
}
</style>