<script setup>
import { ref, provide } from 'vue'
import FolderTree from './FolderTree.vue'
import BookmarkContent from './BookmarkContent.vue'

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
</script>

<template>
  <div class="bookmark-layout">
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
</template>

<style scoped>
.bookmark-layout {
  display: flex;
  height: 100vh;
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