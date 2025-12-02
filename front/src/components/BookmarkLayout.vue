<script setup>
import { ref, provide, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import FolderTree from './FolderTree.vue'
import BookmarkContent from './BookmarkContent.vue'
import { logout } from '../utils/axios'

const router = useRouter()
const currentUser = ref(null)

// 状态管理
const folders = ref([
  {
    id: '1',
    name: '根目录',
    parentId: null,
    children: [
      {
        id: '2',
        name: '工作',
        parentId: '1',
        children: []
      },
      {
        id: '3',
        name: '学习',
        parentId: '1',
        children: [
          {
            id: '4',
            name: '前端',
            parentId: '3',
            children: []
          },
          {
            id: '5',
            name: '后端',
            parentId: '3',
            children: []
          }
        ]
      },
      {
        id: '6',
        name: '生活',
        parentId: '1',
        children: []
      }
    ]
  }
])

const bookmarks = ref([
  {
    id: '1',
    title: '百度',
    url: 'https://www.baidu.com',
    favicon: 'https://www.baidu.com/favicon.ico',
    folderId: '1'
  },
  {
    id: '1',
    title: 'B站',
    url: 'https://www.bilibili.com/',
    favicon: 'https://www.bilibili.com/favicon.ico',
    folderId: '1'
  },
  {
    id: '2',
    title: 'Google',
    url: 'https://www.google.com',
    favicon: 'https://www.google.com/favicon.ico',
    folderId: '1'
  },
  {
    id: '3',
    title: 'GitHub',
    url: 'https://github.com',
    favicon: 'https://github.githubassets.com/favicons/favicon.svg',
    folderId: '2'
  },
  {
    id: '4',
    title: 'Vue.js',
    url: 'https://vuejs.org',
    favicon: 'https://vuejs.org/favicon.ico',
    folderId: '4'
  }
])

const selectedFolderId = ref('1') // 默认选中根目录

// 提供状态给子组件
provide('folders', folders)
provide('bookmarks', bookmarks)
provide('selectedFolderId', selectedFolderId)

// 组件挂载时获取用户信息
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr && userStr !== 'undefined' && userStr !== 'null') {
    try {
      currentUser.value = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
      localStorage.removeItem('user')
    }
  }
})

// 退出登录处理函数
const handleLogout = async () => {
  await logout()
  router.push('/login')
}
</script>

<template>
  <div class="bookmark-layout">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <div class="logo">书签管理器</div>
      <div class="user-info">
        <span v-if="currentUser">{{ currentUser.username }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>
    
    <!-- 主内容区域 -->
    <div class="content-wrapper">
      <!-- 左侧文件夹树 -->
      <div class="sidebar">
        <h2>书签文件夹</h2>
        <FolderTree />
      </div>
    
      <!-- 右侧书签内容 -->
      <div class="main-content">
        <BookmarkContent />
      </div>
    </div>
  </div>
</template>

<style scoped>
.bookmark-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

/* 顶部导航栏 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #3f51b5;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #d32f2f;
}

/* 内容包装器 */
.content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 250px;
  background-color: #fff;
  border-right: 1px solid #e0e0e0;
  padding: 20px;
  overflow-y: auto;
}

.sidebar h2 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
}
</style>