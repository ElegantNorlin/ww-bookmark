<script setup>
import { ref, provide, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import FolderTree from './FolderTree.vue'
import BookmarkContent from './BookmarkContent.vue'
import { logout } from '../utils/axios'
import api from '../utils/axios'

const router = useRouter()
const currentUser = ref(null)

// 状态管理
const folders = ref([])
const bookmarks = ref([])
const selectedFolderId = ref(null) // 默认未选中任何文件夹
const loading = ref(false)
const error = ref(null)

// 文件夹表单状态
const showFolderForm = ref(false)
const folderForm = ref({
  id: null,
  name: '',
  parentId: null
})
const formMode = ref('add') // 'add' 或 'edit'

// 创建文件夹（只支持根文件夹）
const handleCreateFolder = async () => {
  if (!folderForm.value.name.trim()) {
    alert('请输入文件夹名称')
    return
  }
  
  try {
    const response = await api.post('/folders', {
      name: folderForm.value.name,
      parentId: null // 只允许创建根文件夹
    })
    
    if (response.data.code === 200) {
      alert('文件夹创建成功')
      handleCancelFolderForm()
      await loadFolders() // 重新加载文件夹列表
    } else {
      alert(response.data.message || '创建文件夹失败')
    }
  } catch (err) {
    console.error('创建文件夹失败:', err)
    alert('创建文件夹失败，请稍后重试')
  }
}

// 准备编辑文件夹
const handleEditFolder = (folder) => {
  folderForm.value = {
    id: folder.id,
    name: folder.name,
    parentId: folder.parentId
  }
  formMode.value = 'edit'
  showFolderForm.value = true
}

// 更新文件夹
const handleUpdateFolder = async () => {
  if (!folderForm.value.name.trim()) {
    alert('请输入文件夹名称')
    return
  }
  
  try {
    const response = await api.put(`/folders/${folderForm.value.id}`, {
      name: folderForm.value.name,
      parentId: folderForm.value.parentId
    })
    
    if (response.data.code === 200) {
      alert('文件夹更新成功')
      handleCancelFolderForm()
      await loadFolders() // 重新加载文件夹列表
    } else {
      alert(response.data.message || '更新文件夹失败')
    }
  } catch (err) {
    console.error('更新文件夹失败:', err)
    alert('更新文件夹失败，请稍后重试')
  }
}

// 删除文件夹
const handleDeleteFolder = async (folderId) => {
  if (!confirm('确定要删除该文件夹吗？如果文件夹下有子文件夹，需要先删除子文件夹。')) {
    return
  }
  
  try {
    const response = await api.delete(`/folders/${folderId}`)
    
    if (response.data.code === 200) {
      alert('文件夹删除成功')
      await loadFolders() // 重新加载文件夹列表
      // 如果删除的是当前选中的文件夹，清除选中状态
      if (selectedFolderId.value === folderId) {
        selectedFolderId.value = folders.value.length > 0 ? folders.value[0].id : null
      }
    } else {
      alert(response.data.message || '删除文件夹失败')
    }
  } catch (err) {
    console.error('删除文件夹失败:', err)
    alert('删除文件夹失败，请稍后重试')
  }
}

// 取消文件夹表单（确保只支持根文件夹）
const handleCancelFolderForm = () => {
  showFolderForm.value = false
  folderForm.value = {
    id: null,
    name: '',
    parentId: null // 只允许创建根文件夹
  }
  formMode.value = 'add'
}

// 提供状态和方法给子组件
provide('folders', folders)
provide('bookmarks', bookmarks)
provide('selectedFolderId', selectedFolderId)
provide('showFolderForm', showFolderForm)
provide('folderForm', folderForm)
provide('formMode', formMode)
provide('handleCreateFolder', handleCreateFolder)
provide('handleUpdateFolder', handleUpdateFolder)
provide('handleDeleteFolder', handleDeleteFolder)
provide('handleEditFolder', handleEditFolder)
provide('handleCancelFolderForm', handleCancelFolderForm)

// 组件挂载时获取数据
onMounted(async () => {
  await loadUserData()
  await loadFolders()
})

// 加载用户信息
const loadUserData = () => {
  const userStr = localStorage.getItem('user')
  if (userStr && userStr !== 'undefined' && userStr !== 'null') {
    try {
      currentUser.value = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
      localStorage.removeItem('user')
    }
  }
}

// 加载文件夹数据
const loadFolders = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await api.get('/folders/tree')
    if (response.data.code === 200) {
      folders.value = response.data.data
      // 如果有文件夹，默认选中第一个根文件夹
      if (folders.value.length > 0) {
        selectedFolderId.value = folders.value[0].id
      }
    } else {
      error.value = response.data.message || '获取文件夹失败'
    }
  } catch (err) {
    console.error('获取文件夹失败:', err)
    error.value = '获取文件夹失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

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