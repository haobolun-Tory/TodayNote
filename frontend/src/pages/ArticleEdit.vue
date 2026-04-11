<template>
  <div class="container editor-shell">
    <div class="header-row">
      <div>
        <h1>{{ isEdit ? '编辑文章' : '新建文章' }}</h1>
        <p>支持标题、摘要、正文、标签和文件夹归档。</p>
        <p class="editor-tip">当前保存草稿后仅作者自己可见，不会进入公开列表和搜索结果。</p>
      </div>
      <div class="header-actions">
        <button class="btn" @click="router.push('/me/articles')">返回管理</button>
        <button class="btn btn-primary" :disabled="saving" @click="saveArticle(false)">
          {{ saving ? '保存中...' : '保存草稿' }}
        </button>
        <button class="btn btn-success" :disabled="saving" @click="saveArticle(true)">
          {{ saving ? '处理中...' : '保存并发布' }}
        </button>
      </div>
    </div>

    <div class="card editor-card">
      <div class="meta-grid">
        <input v-model="article.title" class="form-input title-input" placeholder="请输入文章标题" />
        <textarea v-model="article.summary" class="form-input summary-input" placeholder="文章摘要（可选，不填将自动截取正文）" />
        <input v-model="article.tags" class="form-input" placeholder="标签，使用英文逗号分隔，如 Java,SpringBoot" />
        <select v-model="article.folderId" class="form-input">
          <option :value="null">未归档</option>
          <option v-for="folder in folders" :key="folder.id" :value="folder.id">{{ folder.name }}</option>
        </select>
      </div>

      <textarea v-model="article.content" class="content-input" placeholder="开始写作，支持 Markdown..." />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createArticle, getArticleById, publishArticle, updateArticle } from '@/api/article'
import { getMyFolders } from '@/api/folder'
import { showSuccessMessage, showWarningMessage } from '@/utils/message'

const route = useRoute()
const router = useRouter()
const saving = ref(false)
const folders = ref([])
const article = ref({ title: '', summary: '', tags: '', content: '', folderId: null })

/**
 * 判断当前页面是否处于编辑模式。
 */
const isEdit = computed(() => route.params.id && route.params.id !== 'new')

/**
 * 获取文件夹列表，供归档选择器使用。
 */
const loadFolders = async () => {
  const res = await getMyFolders()
  folders.value = res.data
}

/**
 * 编辑模式下加载文章详情，并将标签转换为输入框友好的逗号串。
 */
const loadArticle = async () => {
  if (!isEdit.value) return
  const res = await getArticleById(route.params.id)
  article.value = {
    title: res.data.title,
    summary: res.data.summary || '',
    tags: (res.data.tags || []).join(','),
    content: res.data.content,
    folderId: res.data.folderId || null
  }
}

/**
 * 提交前校验标题和正文是否已填写。
 */
const validate = () => {
  if (!article.value.title.trim() || !article.value.content.trim()) {
    showWarningMessage('标题和正文不能为空')
    return false
  }
  return true
}

/**
 * 保存文章；如 `publish=true`，则在保存后继续执行发布动作。
 */
const saveArticle = async (publish) => {
  if (!validate()) return
  saving.value = true
  try {
    // 统一整理请求体，避免空文件夹值被错误提交。
    const payload = {
      ...article.value,
      folderId: article.value.folderId || null
    }
    let articleId = route.params.id
    const isCreatingDraft = !isEdit.value

    if (isEdit.value) {
      await updateArticle(articleId, payload)
    } else {
      const res = await createArticle(payload)
      articleId = res.data.id
    }

    if (publish) {
      await publishArticle(articleId)
      showSuccessMessage('文章已保存并发布')
      router.push('/me/articles')
      return
    }

    if (isCreatingDraft) {
      showSuccessMessage('草稿已创建，可继续编辑')
      await router.replace(`/editor/${articleId}`)
      return
    }

    showSuccessMessage('草稿已更新')
  } catch (error) {
  } finally {
    saving.value = false
  }
}

// 页面进入时准备归档数据，编辑模式下再额外加载文章详情。
onMounted(async () => {
  await loadFolders()
  await loadArticle()
})
</script>

<style scoped>
.editor-shell {
  padding-bottom: 2rem;
}

.header-row {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.header-row p {
  color: var(--text-secondary);
}

.editor-tip {
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #1d4ed8;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: flex-start;
  flex-wrap: wrap;
}

.editor-card {
  display: grid;
  gap: 1rem;
}

.meta-grid {
  display: grid;
  gap: 0.75rem;
}

.title-input {
  font-size: 1.5rem;
  font-weight: 700;
}

.summary-input {
  min-height: 90px;
  resize: vertical;
}

.content-input {
  min-height: 60vh;
  resize: vertical;
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  padding: 1rem;
  box-sizing: border-box;
  line-height: 1.7;
  font-family: inherit;
}

.btn-success {
  background: #16a34a;
  color: white;
}
</style>