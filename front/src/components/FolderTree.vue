<script setup>
import { inject } from 'vue'

const folders = inject('folders')
const selectedFolderId = inject('selectedFolderId')

// 处理文件夹点击事件
const handleFolderClick = (folderId) => {
  selectedFolderId.value = folderId
}
</script>

<template>
  <div class="folder-tree">
    <!-- 使用递归组件渲染文件夹树 -->
    <template v-for="folder in folders" :key="folder.id">
      <div class="folder-item">
        <div 
          :class="`folder-header ${selectedFolderId === folder.id ? 'active' : ''}`"
          @click="handleFolderClick(folder.id)"
        >
          <span class="folder-icon">📁</span>
          <span class="folder-name">{{ folder.name }}</span>
        </div>
        <div v-if="folder.children && folder.children.length > 0" class="folder-children">
          <!-- 递归调用 -->
          <div 
            v-for="child in folder.children" 
            :key="child.id"
            class="folder-item"
            style="margin-left: 20px"
          >
            <div 
              :class="`folder-header ${selectedFolderId === child.id ? 'active' : ''}`"
              @click="handleFolderClick(child.id)"
            >
              <span class="folder-icon">📁</span>
              <span class="folder-name">{{ child.name }}</span>
            </div>
            <div v-if="child.children && child.children.length > 0" class="folder-children">
              <!-- 再次递归调用 -->
              <div 
                v-for="grandchild in child.children" 
                :key="grandchild.id"
                class="folder-item"
                style="margin-left: 20px"
              >
                <div 
                :class="`folder-header ${selectedFolderId === grandchild.id ? 'active' : ''}`"
                @click="handleFolderClick(grandchild.id)"
              >
                  <span class="folder-icon">📁</span>
                  <span class="folder-name">{{ grandchild.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.folder-tree {
  font-size: 14px;
}

.folder-item {
  margin-bottom: 5px;
}

.folder-header {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.folder-header:hover {
  background-color: #f0f0f0;
}

.folder-header.active {
  background-color: #e3f2fd;
  color: #1976d2;
  font-weight: bold;
}

.folder-icon {
  margin-right: 8px;
}

.folder-name {
  flex: 1;
}

.folder-children {
  margin-top: 5px;
}
</style>