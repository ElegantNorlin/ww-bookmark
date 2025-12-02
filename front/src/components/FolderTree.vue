<script setup>
import { ref, inject, onMounted, watch, h } from 'vue'
import { NTree, NIcon } from 'naive-ui'
import { Folder as FolderIcon } from '@vicons/ionicons5'

// 注入状态和方法
const folders = inject('folders')
const selectedFolderId = inject('selectedFolderId')
const showFolderForm = inject('showFolderForm')
const folderForm = inject('folderForm')
const formMode = inject('formMode')
const handleDeleteFolder = inject('handleDeleteFolder')
const handleEditFolder = inject('handleEditFolder')

// 展开/折叠状态
const expandedKeys = ref([])

// 初始化时展开所有文件夹
const expandAllFolders = (folderList) => {
  const keys = []
  const traverse = (folders) => {
    folders.forEach(folder => {
      keys.push(folder.id)
      if (folder.children && folder.children.length > 0) {
        traverse(folder.children)
      }
    })
  }
  traverse(folderList)
  return keys
}

// 监听 folders 变化，重新计算展开的节点
watch(folders, (newFolders) => {
  expandedKeys.value = expandAllFolders(newFolders)
}, { immediate: true, deep: true })

// 处理节点展开/折叠变化
const handleExpandChange = (keys) => {
  expandedKeys.value = keys
}

// 处理节点选中变化
const handleSelectChange = (keys) => {
  if (keys.length > 0) {
    selectedFolderId.value = keys[0]
  }
}

// 处理右键菜单事件
const handleContextMenu = (e, data) => {
  e.preventDefault()
  const folder = data.node
  
  // 创建右键菜单
  const menu = document.createElement('div')
  menu.className = 'context-menu'
  menu.innerHTML = `
    <div class="menu-item" data-action="create">创建子文件夹</div>
    <div class="menu-item" data-action="edit">编辑文件夹</div>
    <div class="menu-item delete" data-action="delete">删除文件夹</div>
  `
  
  // 设置菜单位置
  menu.style.left = `${e.clientX}px`
  menu.style.top = `${e.clientY}px`
  
  // 添加到页面
  document.body.appendChild(menu)
  
  // 处理菜单点击
  menu.addEventListener('click', (event) => {
    const action = event.target.dataset.action
    if (action === 'create') {
      folderForm.value = {
        id: null,
        name: '',
        parentId: folder.id
      }
      formMode.value = 'add'
      showFolderForm.value = true
    } else if (action === 'edit') {
      handleEditFolder(folder)
    } else if (action === 'delete') {
      handleDeleteFolder(folder.id)
    }
    // 移除菜单
    document.body.removeChild(menu)
  })
  
  // 点击其他地方关闭菜单
  const closeMenu = () => {
    if (document.body.contains(menu)) {
      document.body.removeChild(menu)
    }
    document.removeEventListener('click', closeMenu)
  }
  
  setTimeout(() => {
    document.addEventListener('click', closeMenu)
  }, 0)
}

// 树节点渲染函数
const renderTreeNode = (node) => {
  return `${node.label}`
}

// 转换数据格式以适配 NTree
const transformData = (folderList) => {
  return folderList.map(folder => ({
    ...folder, // 保留原始数据，放在前面
    key: folder.id,
    label: `📁 ${folder.name}`,
    children: folder.children && folder.children.length > 0 ? transformData(folder.children) : undefined
  }))
}

// 转换后的树数据
const treeData = ref([])

// 监听 folders 变化，更新树数据
watch(folders, (newFolders) => {
  treeData.value = transformData(newFolders)
}, { immediate: true, deep: true })
</script>

<template>
  <div class="folder-tree">
    <NTree
      block-line
      :data="treeData"
      :expanded-keys="expandedKeys"
      :selected-keys="selectedFolderId ? [selectedFolderId] : []"
      :render="renderTreeNode"
      @update:expanded-keys="handleExpandChange"
      @update:selected-keys="handleSelectChange"
      @contextmenu="handleContextMenu"
    />
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

/* 增加文件夹树的字体大小为大半个号 */
.folder-tree :deep(.n-tree-node-content) {
  font-size: 15px;
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
  padding: 5px 0;
}

.menu-item {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
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