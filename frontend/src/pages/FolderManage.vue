<template>
  <div class="container page-shell">
    <div class="page-header">
      <div>
        <h1>文件夹管理</h1>
        <p>为你的文章建立归档结构，删除文件夹后文章会回到未归档状态。</p>
      </div>
    </div>

    <div class="card editor-card">
      <div class="form-grid">
        <input v-model="form.name" class="form-input" placeholder="文件夹名称" />
        <input v-model="form.description" class="form-input" placeholder="描述（可选）" />
        <input v-model.number="form.sortOrder" class="form-input" type="number" placeholder="排序" />
      </div>
      <div class="action-row">
        <button class="btn btn-primary" @click="submitFolder">{{ form.id ? '保存修改' : '创建文件夹' }}</button>
        <button v-if="form.id" class="btn" @click="resetForm">取消编辑</button>
      </div>
    </div>

    <div class="folder-list">
      <div v-for="folder in folders" :key="folder.id" class="card folder-card">
        <div>
          <h3>{{ folder.name }}</h3>
          <p>{{ folder.description || '暂无描述' }}</p>
        </div>
        <div class="folder-actions">
          <span>排序：{{ folder.sortOrder || 0 }}</span>
          <button class="btn" @click="editFolder(folder)">编辑</button>
          <button class="btn btn-danger" @click="removeFolder(folder.id)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { createFolder, deleteFolder, getMyFolders, updateFolder } from '@/api/folder'
import { showConfirmDialog, showSuccessMessage, showWarningMessage } from '@/utils/message'

const folders = ref([])
const form = reactive({ id: null, name: '', description: '', sortOrder: 0 })

/**
 * 获取当前用户文件夹列表。
 */
const loadFolders = async () => {
  const res = await getMyFolders()
  folders.value = res.data
}

/**
 * 重置文件夹编辑表单。
 */
const resetForm = () => {
  form.id = null
  form.name = ''
  form.description = ''
  form.sortOrder = 0
}

/**
 * 提交文件夹创建或编辑请求。
 */
const submitFolder = async () => {
  if (!form.name.trim()) {
    showWarningMessage('请输入文件夹名称')
    return
  }

  // 提交前先记录是否为编辑模式，用于成功提示文案。
  const isEditMode = !!form.id
  try {
    const payload = {
      name: form.name,
      description: form.description,
      sortOrder: form.sortOrder
    }
    if (form.id) {
      await updateFolder(form.id, payload)
    } else {
      await createFolder(payload)
    }
    resetForm()
    await loadFolders()
    showSuccessMessage(isEditMode ? '文件夹已更新' : '文件夹已创建')
  } catch (error) {
  }
}

/**
 * 把指定文件夹数据回填到表单中。
 */
const editFolder = (folder) => {
  form.id = folder.id
  form.name = folder.name
  form.description = folder.description || ''
  form.sortOrder = folder.sortOrder || 0
}

/**
 * 删除文件夹，并在成功后刷新列表。
 */
const removeFolder = async (id) => {
  try {
    await showConfirmDialog('确认删除该文件夹吗？删除后文章会回到未归档状态。', '删除确认')
  } catch {
    return
  }

  try {
    await deleteFolder(id)
    await loadFolders()
    if (form.id === id) resetForm()
    showSuccessMessage('文件夹已删除')
  } catch (error) {
  }
}

// 页面初始化时加载文件夹列表。
onMounted(loadFolders)
</script>

<style scoped>
.page-shell {
  padding-bottom: 2rem;
}

.page-header {
  margin-bottom: 1rem;
}

.page-header p,
.folder-card p {
  color: var(--text-secondary);
}

.editor-card {
  margin-bottom: 1rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0.75rem;
}

.folder-list {
  display: grid;
  gap: 1rem;
}

.folder-card,
.folder-actions,
.action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn-danger {
  background: #fee2e2;
  color: #b91c1c;
}
</style>
