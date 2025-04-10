<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Icon } from '@iconify/vue'

// 组件状态
const isSidebarCollapsed = ref(false)
const newMessage = ref('')
const messages = ref([])
const activeConversation = ref(null)
const conversations = ref([])
const eventSource = ref(null)

// 用户信息
const userInitial = computed(() => {
  // 这里可以从用户信息中获取首字母
  return 'USER'
})

// 当前对话标题
const currentConversationTitle = computed(() => {
  const conv = conversations.value.find(c => c.id === activeConversation.value)
  return conv ? conv.title : '新对话'
})

// 分组对话
const groupedConversations = computed(() => {
  return [
    {
      title: '今天',
      conversations: conversations.value.filter(conv => conv.time === 'today')
    },
    {
      title: '昨天',
      conversations: conversations.value.filter(conv => conv.time === 'yesterday')
    },
    {
      title: '更早',
      conversations: conversations.value.filter(conv => conv.time === 'older')
    }
  ]
})

// 初始化数据
onMounted(() => {
  loadConversations()
  if (conversations.value.length > 0) {
    activeConversation.value = conversations.value[0].id
    loadMessages(activeConversation.value)
  }
})

// 清理 EventSource
onBeforeUnmount(() => {
  if (eventSource.value) {
    eventSource.value.close()
  }
})

// 加载对话列表
const loadConversations = () => {
  // 这里应该是从API或本地存储加载
  conversations.value = [
    { id: '1', title: '如何学习Vue', time: 'today' },
    { id: '2', title: 'Spring AI问题', time: 'yesterday' },
    { id: '3', title: '项目讨论', time: 'older' }
  ]
}

// 加载消息
const loadMessages = (conversationId) => {
  // 这里应该是从API或本地存储加载特定对话的消息
  messages.value = [
    { role: 'assistant', content: '你好！我是AI助手，有什么可以帮你的吗？' }
  ]
}

// 切换侧边栏
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

// 开始新对话
const startNewChat = () => {
  const newId = Date.now().toString()
  conversations.value.unshift({
    id: newId,
    title: '新对话',
    time: 'today'
  })
  activeConversation.value = newId
  messages.value = []
}

// 选择对话
const selectConversation = (conversationId) => {
  activeConversation.value = conversationId
  loadMessages(conversationId)
}

// 发送消息
const sendMessage = () => {
  const message = newMessage.value.trim()
  if (!message) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: message })
  newMessage.value = ''

  // 创建AI消息占位
  const aiMessageIndex = messages.value.push({ role: 'assistant', content: '' }) - 1

  // 关闭之前的连接（如果有）
  if (eventSource.value) {
    eventSource.value.close()
  }

  // 创建新的EventSource连接
  eventSource.value = new EventSource(`http://localhost:8081/chat/ollama/generate_stream?message=${encodeURIComponent(message)}`)

  eventSource.value.onmessage = (event) => {
    // 追加AI回复内容
    messages.value[aiMessageIndex].content += event.data
    // 滚动到底部
    scrollToBottom()
  }

  eventSource.value.onerror = (error) => {
    console.error("EventSource failed:", error)
    eventSource.value.close()
    if (messages.value[aiMessageIndex].content === '') {
      messages.value[aiMessageIndex].content = '抱歉，发生了一些错误。'
    }
  }
}

// 滚动到底部
const scrollToBottom = () => {
  const container = document.querySelector('.message-container')
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}
</script>

<template>
  <div class="chat-container" :class="{ 'sidebar-collapsed': isSidebarCollapsed }">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <!-- 折叠按钮 -->
        <button class="collapse-btn" @click="toggleSidebar" aria-label="折叠侧边栏">
          <span class="header-icon">
            {{ isSidebarCollapsed ? "→" : "≡" }} <!-- 用不同符号区分状态 -->
          </span>
        </button>

        <!-- 标题 -->
        <transition name="fade">
          <h2 v-show="!isSidebarCollapsed" class="sidebar-title">
            RAG智能助手
          </h2>
        </transition>
      </div>

      <!-- 新对话 -->
      <div class="new-chat-btn">
        <button @click="startNewChat">
          <Icon
              :icon="isSidebarCollapsed ? 'mdi:plus' : 'mdi:plus-circle-outline'"
              class="new-chat-icon"
          />
          <span v-show="!isSidebarCollapsed">开启新对话</span>
        </button>
      </div>

      <div class="conversation-list">
        <div
            class="time-group"
            v-for="group in groupedConversations"
            :key="group.title"
        >
          <!-- 时间分组标题 -->
          <h4 v-show="!isSidebarCollapsed" class="time-label">
            {{ group.title }}
          </h4>

          <!-- 对话项列表 -->
          <div
              v-for="conv in group.conversations"
              :key="conv.id"
              class="conversation-item"
              :class="{ active: activeConversation === conv.id }"
              @click="selectConversation(conv.id)"
          >
            <span v-show="!isSidebarCollapsed" class="conversation-title">
              {{ conv.title }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主聊天区 -->
    <div class="chat-area">
      <div class="chat-header">
        <h3>{{ currentConversationTitle }}</h3>
      </div>

      <div class="message-container" ref="messageContainer">
        <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message"
            :class="msg.role"
        >
          <div class="avatar">
            <img v-if="msg.role === 'assistant'" src="@/assets/ai-avatar.png" alt="AI头像"/>
            <span v-else class="user-avatar">{{ userInitial }}</span>
          </div>
          <div class="content">{{ msg.content }}</div>
        </div>
      </div>

      <div class="input-area">
        <input
            v-model="newMessage"
            placeholder="Ask Anything..."
            @keyup.enter="sendMessage"
        />
        <button @click="sendMessage">
          <Icon icon="mdi:send" />
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  background-color: white;
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  background-color: white;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.sidebar-header {
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.sidebar-header h2 {
  color: #646cff;
  font-size: 1.2rem;
}



.new-chat-btn {
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.new-chat-btn button {
  width: 100%;
  padding: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background-color: #646cff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
}

.new-chat-btn button:hover {
  background-color: #555bf2;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.time-group {
  margin-bottom: 16px;
}

.time-group h4 {
  color: #6b7280;
  font-size: 0.8rem;
  padding: 8px 12px;
  margin-bottom: 4px;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  color: #4b5563;
}

.conversation-item:hover {
  background-color: #f3f4f6;
}

.conversation-item.active {
  background-color: #e0e7ff;
  color: #646cff;
}

.conversation-item svg {
  color: #9ca3af;
}

/* 主聊天区样式 */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 12px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.message-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #ffffff;
  width: 100%; /* 占满所有可用宽度 */
  max-width: none; /* 移除任何最大宽度限制 */
}

.message {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  width: 100%; /* 占满容器宽度 */
  max-width: none; /* 移除之前的800px限制 */
}



.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #eeeeee;
  color: #cc3f3f;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.content {
  padding: 12px 16px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message.assistant .content {
  background-color: white;
  border: 1px solid #e5e7eb;
}

/* 用户消息靠右对齐 */
.message.user {
  flex-direction: row-reverse;
}

.message.user .content {
  background-color: #646cff;
  color: white;
}

.input-area {
  padding: 16px;
  display: flex;
  gap: 12px;
  border-top: 1px solid #e5e7eb;
}

.input-area input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  outline: none;
}

.input-area input:focus {
  border-color: #646cff;
}

.input-area button {
  padding: 0 16px;
  background-color: #646cff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* 侧边栏收缩状态 */
.sidebar-collapsed .sidebar {
  width: 60px;
}

.sidebar-collapsed .conversation-item {
  justify-content: center;
  padding: 8px;
}

/* 对话样式 */
.new-chat-btn {
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.new-chat-btn button {
  width: 100%;
  padding: 10px 16px;
  border-radius: 6px;
  background-color: #646cff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  border: none;
  transition: all 0.3s ease;
}

.new-chat-btn button:hover {
  background-color: #555bf2;
}

.new-chat-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  height: 60px; /* 固定高度 */
}

.header-icon {
  color: #646cff; /* 控制 SVG 颜色 */
  transition: transform 0.3s ease; /* 可选：添加动画 */
}

.collapse-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  margin-right: 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.collapse-btn:hover {
  background-color: #f3f4f6;
}



/* 侧边栏的样式 */
.sidebar-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}



</style>