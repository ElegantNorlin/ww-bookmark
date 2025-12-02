<script setup>
import { ref, inject, defineComponent, h } from 'vue'

// 从父组件注入状态和方法
const folders = inject('folders')
const selectedFolderId = inject('selectedFolderId')
const showFolderForm = inject('showFolderForm')
const folderForm = inject('folderForm')
const formMode = inject('formMode')
const handleCreateFolder = inject('handleCreateFolder')
const handleUpdateFolder = inject('handleUpdateFolder')
const handleDeleteFolder = inject('handleDeleteFolder')
const handleEditFolder = inject('handleEditFolder')
const handleCancelFolderForm = inject('handleCancelFolderForm')

// 展开/折叠状态管理
const expandedFolders = ref(new Set())

// 右键菜单状态
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  folder: null
})

// 处理文件夹点击事件
const handleFolderClick = (folderId) => {
  selectedFolderId.value = folderId
}

// 处理文件夹展开/折叠
const toggleFolder = (event, folder) => {
  event.stopPropagation() // 阻止冒泡，避免触发点击事件
  
  if (expandedFolders.value.has(folder.id)) {
    expandedFolders.value.delete(folder.id)
  } else {
    expandedFolders.value.add(folder.id)
  }
}

// 显示文件夹右键菜单
const showContextMenu = (event, folder) => {
  event.preventDefault()
  event.stopPropagation()
  
  contextMenu.value = {
    visible: true,
    x: event.clientX,
    y: event.clientY,
    folder
  }
}

// 隐藏右键菜单
const hideContextMenu = () => {
  contextMenu.value.visible = false
}

// 删除了createSubFolder方法，不再支持创建子文件夹

// 编辑文件夹
const editFolder = (folder) => {
  hideContextMenu()
  handleEditFolder(folder)
}

// 删除文件夹
const deleteFolder = (folder) => {
  hideContextMenu()
  handleDeleteFolder(folder.id)
}

// 添加根文件夹
const addRootFolder = () => {
  folderForm.value = {
    id: null,
    name: '',
    parentId: null
  }
  formMode.value = 'add'
  showFolderForm.value = true
}

// 点击页面其他地方隐藏右键菜单
const handleClickOutside = () => {
  hideContextMenu()
}

// 注册全局点击事件
import { onMounted, onUnmounted } from 'vue'
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 递归渲染文件夹项的函数
const renderFolderItem = (folder) => {
  return h('div', { class: 'folder-item' }, [
    h('div', {
      class: `folder-header ${selectedFolderId.value === folder.id ? 'active' : ''}`,
      onClick: () => handleFolderClick(folder.id),
      onContextmenu: (event) => showContextMenu(event, folder)
    }, [
      folder.children && folder.children.length > 0 ? 
        h('span', {
          class: 'toggle-icon',
          onClick: (event) => toggleFolder(event, folder)
        }, expandedFolders.value.has(folder.id) ? '▼' : '▶') :
        h('span', { class: 'toggle-icon-placeholder' }),
      h('span', { class: 'folder-icon' }, '📁'),
      h('span', { class: 'folder-name' }, folder.name)
    ]),
    folder.children && folder.children.length > 0 && expandedFolders.value.has(folder.id) ?
      h('div', { class: 'folder-children' },
        folder.children.map(child => renderFolderItem(child))
      ) : null
  ])
}
</script>

<template>
  <div class="folder-tree">
    <!-- 添加根文件夹按钮 -->
    <div class="add-folder-btn-container">
      <button class="add-root-folder-btn" @click="addRootFolder">
        ➕ 添加根文件夹
      </button>
    </div>
    
    <!-- 使用渲染函数渲染文件夹树 -->
    <div v-if="folders && folders.length > 0">
      <div v-for="folder in folders" :key="folder.id">
        <component :is="{ render: () => renderFolderItem(folder) }" />
      </div>
    </div>
    
    <!-- 文件夹表单弹窗 -->
    <div v-if="showFolderForm" class="folder-form-overlay">
      <div class="folder-form">
        <h3>{{ formMode === 'add' ? '创建文件夹' : '编辑文件夹' }}</h3>
        <div class="form-group">
          <label for="folderName">文件夹名称:</label>
          <input 
            id="folderName"
            v-model="folderForm.name" 
            type="text" 
            placeholder="请输入文件夹名称"
          />
        </div>
        <div class="form-actions">
          <button @click="formMode === 'add' ? handleCreateFolder() : handleUpdateFolder()">
            {{ formMode === 'add' ? '创建' : '更新' }}
          </button>
          <button @click="handleCancelFolderForm()" class="cancel-btn">
            取消
          </button>
        </div>
      </div>
    </div>
    
    <!-- 右键菜单 -->
    <div 
      v-if="contextMenu.visible" 
      class="context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
    >
      <div class="context-menu-item" @click="editFolder(contextMenu.folder)">
        ✏️ 编辑文件夹
      </div>
      <div class="context-menu-item delete" @click="deleteFolder(contextMenu.folder)">
        🗑️ 删除文件夹
      </div>
    </div>
  </div>
</template>

<style scoped>
.folder-tree {
  font-size: 14px;
  position: relative;
}

.folder-item {
  margin-bottom: 2px;
}

.folder-header {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.folder-header:hover {
  background-color: #f0f0f0;
}

.folder-header.active {
  background-color: #e3f2fd;
  color: #1976d2;
  font-weight: 500;
}

.toggle-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
  font-size: 12px;
  cursor: pointer;
  color: #666;
}

.toggle-icon-placeholder {
  width: 16px;
  margin-right: 4px;
}

.folder-icon {
  margin-right: 6px;
  font-size: 16px;
}

.folder-name {
  flex: 1;
}

.folder-children {
  margin-left: 20px;
  margin-top: 2px;
}

/* 添加按钮样式 */
.add-folder-btn-container {
  margin-bottom: 15px;
  text-align: center;
}

.add-root-folder-btn {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px;
  width: 100%;
}

.add-root-folder-btn:hover {
  background-color: #45a049;
}

/* 表单样式 */
.folder-form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.folder-form {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 300px;
}

.folder-form h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.form-actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.form-actions button:first-child {
  background-color: #1976d2;
  color: white;
}

.form-actions button:first-child:hover {
  background-color: #1565c0;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  min-width: 150px;
}

.context-menu-item {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
}

.context-menu-item:hover {
  background-color: #f0f0f0;
}

.context-menu-item.delete {
  color: #d32f2f;
}

.context-menu-item.delete:hover {
  background-color: #ffebee;
}
</style>