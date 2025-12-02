<script setup>
import { ref, inject, onMounted } from 'vue'

// 注入状态和方法
const folders = inject('folders')
const selectedFolderId = inject('selectedFolderId')
const showFolderForm = inject('showFolderForm')
const folderForm = inject('folderForm')
const formMode = inject('formMode')
const handleDeleteFolder = inject('handleDeleteFolder')
const handleEditFolder = inject('handleEditFolder')

// 右键菜单状态
const contextMenuVisible = ref(false)
const contextMenuPosition = ref({ x: 0, y: 0 })
const selectedFolderForMenu = ref(null)

// 展开/折叠状态
const expandedFolders = ref(new Set())

// 初始化时展开所有文件夹
onMounted(() => {
  expandAllFolders(folders.value)
})

// 递归展开所有文件夹
const expandAllFolders = (folderList) => {
  folderList.forEach(folder => {
    expandedFolders.value.add(folder.id)
    if (folder.children && folder.children.length > 0) {
      expandAllFolders(folder.children)
    }
  })
}

// 切换文件夹展开/折叠
const toggleFolder = (folderId) => {
  if (expandedFolders.value.has(folderId)) {
    expandedFolders.value.delete(folderId)
  } else {
    expandedFolders.value.add(folderId)
  }
}

// 选择文件夹
const selectFolder = (folderId) => {
  selectedFolderId.value = folderId
}

// 处理右键菜单
const handleContextMenu = (event, folder) => {
  event.preventDefault()
  contextMenuVisible.value = true
  contextMenuPosition.value = { x: event.clientX, y: event.clientY }
  selectedFolderForMenu.value = folder
}

// 关闭右键菜单
const closeContextMenu = () => {
  contextMenuVisible.value = false
  selectedFolderForMenu.value = null
}

// 处理创建子文件夹
const handleCreateSubFolder = () => {
  if (selectedFolderForMenu.value) {
    folderForm.value = {
      id: null,
      name: '',
      parentId: selectedFolderForMenu.value.id
    }
    formMode.value = 'add'
    showFolderForm.value = true
    closeContextMenu()
  }
}

// 处理编辑文件夹
const handleEditContextMenu = () => {
  if (selectedFolderForMenu.value) {
    handleEditFolder(selectedFolderForMenu.value)
    closeContextMenu()
  }
}

// 处理删除文件夹
const handleDeleteContextMenu = () => {
  if (selectedFolderForMenu.value) {
    handleDeleteFolder(selectedFolderForMenu.value.id)
    closeContextMenu()
  }
}

// 点击其他区域关闭右键菜单
const handleClickOutside = (event) => {
  if (event.target.closest('.context-menu') === null) {
    closeContextMenu()
  }
}

// 添加全局点击事件监听
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="folder-tree">
    <ul class="folder-list">
      <!-- 递归渲染文件夹树 -->
      <li 
        v-for="folder in folders" 
        :key="folder.id"
        :class="['folder-item', { selected: selectedFolderId === folder.id }]"
        @click="selectFolder(folder.id)"
        @contextmenu="handleContextMenu($event, folder)"
      >
        <div class="folder-header">
          <!-- 展开/折叠图标 -->
          <span 
            v-if="folder.children && folder.children.length > 0" 
            class="expand-icon" 
            @click.stop="toggleFolder(folder.id)"
          >
            {{ expandedFolders.has(folder.id) ? '▼' : '►' }}
          </span>
          <span v-else class="expand-icon empty"></span>
          <!-- 文件夹名称 -->
          <span class="folder-name">{{ folder.name }}</span>
        </div>
        
        <!-- 递归渲染子文件夹 -->
        <ul v-if="folder.children && folder.children.length > 0 && expandedFolders.has(folder.id)" class="sub-folder-list">
          <li 
            v-for="child in folder.children" 
            :key="child.id"
            :class="['folder-item', { selected: selectedFolderId === child.id }]"
            @click="selectFolder(child.id)"
            @contextmenu="handleContextMenu($event, child)"
          >
            <div class="folder-header">
              <!-- 展开/折叠图标 -->
              <span 
                v-if="child.children && child.children.length > 0" 
                class="expand-icon" 
                @click.stop="toggleFolder(child.id)"
              >
                {{ expandedFolders.has(child.id) ? '▼' : '►' }}
              </span>
              <span v-else class="expand-icon empty"></span>
              <!-- 文件夹名称 -->
              <span class="folder-name">{{ child.name }}</span>
            </div>
            
            <!-- 递归渲染更深层次的子文件夹 -->
            <ul v-if="child.children && child.children.length > 0 && expandedFolders.has(child.id)" class="sub-folder-list">
              <li 
                v-for="grandchild in child.children" 
                :key="grandchild.id"
                :class="['folder-item', { selected: selectedFolderId === grandchild.id }]"
                @click="selectFolder(grandchild.id)"
                @contextmenu="handleContextMenu($event, grandchild)"
              >
                <div class="folder-header">
                  <!-- 展开/折叠图标 -->
                  <span 
                    v-if="grandchild.children && grandchild.children.length > 0" 
                    class="expand-icon" 
                    @click.stop="toggleFolder(grandchild.id)"
                  >
                    {{ expandedFolders.has(grandchild.id) ? '▼' : '►' }}
                  </span>
                  <span v-else class="expand-icon empty"></span>
                  <!-- 文件夹名称 -->
                  <span class="folder-name">{{ grandchild.name }}</span>
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </li>
    </ul>
    
    <!-- 右键菜单 -->
    <div 
      v-if="contextMenuVisible" 
      class="context-menu"
      :style="{
        left: `${contextMenuPosition.x}px`,
        top: `${contextMenuPosition.y}px`
      }"
    >
      <div class="menu-item" @click="handleCreateSubFolder">
        创建子文件夹
      </div>
      <div class="menu-item" @click="handleEditContextMenu">
        编辑文件夹
      </div>
      <div class="menu-item delete" @click="handleDeleteContextMenu">
        删除文件夹
      </div>
    </div>
  </div>
</template>

<style scoped>
.folder-tree {
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.folder-list, .sub-folder-list {
  list-style-type: none;
  padding-left: 0;
}

.sub-folder-list {
  padding-left: 20px;
}

.folder-item {
  margin-bottom: 5px;
  cursor: pointer;
}

.folder-header {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 4px;
  transition: background-color 0.2s;
  user-select: none;
}

.folder-header:hover {
  background-color: #e0e0e0;
}

.folder-item.selected .folder-header {
  background-color: #4CAF50;
  color: white;
}

.expand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  margin-right: 5px;
  cursor: pointer;
  transition: transform 0.2s;
}

.expand-icon.empty {
  visibility: hidden;
}

.folder-name {
  flex: 1;
}

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 150px;
}

.menu-item {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.menu-item:hover {
  background-color: #f0f0f0;
}

.menu-item.delete {
  color: #f44336;
}

.menu-item.delete:hover {
  background-color: #ffebee;
}
</style>