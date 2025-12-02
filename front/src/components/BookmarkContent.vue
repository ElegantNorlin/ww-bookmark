<script setup>
import { inject, computed } from 'vue'

const bookmarks = inject('bookmarks')
const selectedFolderId = inject('selectedFolderId')
const folders = inject('folders')

// 处理子文件夹点击事件
const handleFolderClick = (folderId) => {
  selectedFolderId.value = folderId
}

// 找到当前选中的文件夹名称
const findFolderName = (folderId, foldersList) => {
  for (const folder of foldersList) {
    if (folder.id === folderId) {
      return folder.name
    }
    if (folder.children && folder.children.length > 0) {
      const found = findFolderName(folderId, folder.children)
      if (found) {
        return found
      }
    }
  }
  return ''
}

// 计算当前选中文件夹的名称
const currentFolderName = computed(() => {
  return findFolderName(selectedFolderId.value, folders.value)
})

// 找到当前选中的文件夹对象
const findCurrentFolder = (folderId, foldersList) => {
  for (const folder of foldersList) {
    if (folder.id === folderId) {
      return folder
    }
    if (folder.children && folder.children.length > 0) {
      const found = findCurrentFolder(folderId, folder.children)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 计算当前文件夹下的子文件夹
const currentSubfolders = computed(() => {
  const currentFolder = findCurrentFolder(selectedFolderId.value, folders.value)
  return currentFolder ? currentFolder.children || [] : []
})

// 计算当前文件夹下的书签
const currentBookmarks = computed(() => {
  return bookmarks.value.filter(bookmark => bookmark.folderId === selectedFolderId.value)
})

// 计算当前文件夹是否为空
const isEmpty = computed(() => {
  return currentSubfolders.value.length === 0 && currentBookmarks.value.length === 0
})

// 处理书签点击事件
const handleBookmarkClick = (bookmark) => {
  // 确保URL格式正确，没有协议时添加http://
  let url = bookmark.url
  if (url && !url.startsWith('http://') && !url.startsWith('https://')) {
    url = 'http://' + url
  }
  // 在新标签页中打开书签URL
  window.open(url, '_blank')
}
</script>

<template>
  <div class="bookmark-content">
    <div class="content-header">
      <h2>{{ currentFolderName }}</h2>
      <div class="header-actions">
        <button class="add-btn">添加书签</button>
        <button class="add-folder-btn">添加子文件夹</button>
      </div>
    </div>
    
    <div class="bookmark-list">
      <!-- 空状态 -->
      <div v-if="isEmpty" class="empty-state">
        <p>当前文件夹下没有内容</p>
      </div>
      
      <!-- 内容列表 -->
      <div v-else class="content-list">
        <!-- 子文件夹 -->
        <div 
          v-for="subfolder in currentSubfolders" 
          :key="subfolder.id"
          class="content-item"
          @click="handleFolderClick(subfolder.id)"
        >
          <div class="content-info">
            <span class="content-icon folder-icon">📁</span>
            <span class="content-name">{{ subfolder.name }}</span>
          </div>
          <div class="content-actions">
            <button class="action-btn">•••</button>
          </div>
        </div>
        
        <!-- 书签 -->
        <div 
          v-for="bookmark in currentBookmarks" 
          :key="bookmark.id"
          class="content-item"
          @click="handleBookmarkClick(bookmark)"
        >
          <div class="content-info">
            <img 
              v-if="bookmark.favicon" 
              :src="bookmark.favicon" 
              :alt="bookmark.title" 
              class="content-icon bookmark-favicon"
              onerror="this.style.display='none'"
            />
            <span v-else class="content-icon link-icon">🔗</span>
            <span class="content-name">{{ bookmark.title }}</span>
          </div>
          <div class="content-actions">
            <button class="action-btn">•••</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.bookmark-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.content-header h2 {
  font-size: 20px;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.add-btn, .add-folder-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
}

.add-btn:hover {
  background-color: #45a049;
}

.add-folder-btn {
  background-color: #2196F3;
  color: white;
}

.add-folder-btn:hover {
  background-color: #1976D2;
}

.bookmark-list {
  flex: 1;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: white;
  border: 1px dashed #e0e0e0;
  border-radius: 8px;
  color: #999;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.content-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  transition: background-color 0.2s ease;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.content-item:last-child {
  border-bottom: none;
}

.content-item:hover {
  background-color: #f8f9fa;
}

.content-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.content-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.bookmark-favicon {
  width: 16px;
  height: 16px;
  border-radius: 2px;
  object-fit: contain;
}

.folder-icon {
  color: #1976d2;
}

.link-icon {
  color: #4caf50;
}

.content-name {
  font-size: 14px;
  color: #333;
}

.content-actions {
  display: flex;
  align-items: center;
}

.action-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
  line-height: 1;
}

.action-btn:hover {
  background-color: #e0e0e0;
  color: #333;
}
</style>