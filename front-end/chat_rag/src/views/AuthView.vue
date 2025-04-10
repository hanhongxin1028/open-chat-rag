<template>
  <div class="auth-view">
    <!-- 全景背景图 -->
    <div class="background">
      <img :src="backgroundImage" alt="Background" />
    </div>

    <!-- 左侧文字 -->
    <div class="left-text">
      <h1>RAG</h1>
      <h2>聊天智能助手</h2>
    </div>

    <!-- 右侧边栏 -->
    <div class="sidebar">
      <AuthForm />
    </div>
  </div>
</template>

<script>
import AuthForm from '@/components/AuthForm.vue'
import backgroundImage from '@/assets/ai-background.jpg'

export default {
  components: {
    AuthForm
  },
  data() {
    return {
      backgroundImage
    }
  }
}
</script>

<style scoped>
.auth-view {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.background {
  position: absolute;
  width: 100%;
  height: 100%;
}

.background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.left-text {
  position: absolute;
  left: 5%;
  top: 40%;
  transform: translateY(-50%);
  z-index: 1;
}

.left-text h1 {
  font-size: 5rem;  /* 统一字号 */
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #646cff;   /* 使用指定的蓝色 */
  border-bottom: 3px solid #646cff; /* 添加底部边框 */
  padding-bottom: 0.5rem; /* 为边框留出空间 */
  display: inline-block; /* 让边框只包裹文字 */
}

.left-text h2 {
  font-size: 5rem;  /* 统一字号 */
  font-weight: normal;
  color: #000000;     /* 白色文字 */
  margin-top: 0;    /* 移除默认间距 */
}

.sidebar {
  position: absolute;
  right: 0;
  top: 0;
  width: 33.33%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>