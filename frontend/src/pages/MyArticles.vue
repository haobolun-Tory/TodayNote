<template>
  <div class="container page-shell">
    <div class="page-header">
      <div>
        <h1>我的文章</h1>
        <p>管理草稿、已发布文章，并按日期、关键字和文件夹筛选。</p>
      </div>
      <button class="btn btn-primary" @click="goCreate">新建文章</button>
    </div>

    <div class="card filter-panel">
      <input v-model="filters.keyword" class="form-input" placeholder="搜索标题或摘要" />
      <select v-model="filters.status" class="form-input">
        <option value="">全部状态</option>
        <option value="DRAFT">草稿</option>
        <option value="PUBLISHED">已发布</option>
      </select>
      <select v-model="filters.folderId" class="form-input">
        <option value="">全部文件夹</option>
        <option v-for="folder in folders" :key="folder.id" :value="folder.id">{{ folder.name }}</option>
      </select>
      <input v-model="filters.startDate" type="date" class="form-input" />
      <input v-model="filters.endDate" type="date" class="form-input" />
      <button class="btn btn-primary" @click="loadArticles">查询</button>
    </div>

    <div v-if="loading" class="card">加载中...</div>

    <div v-else-if="articles.length === 0" class="card empty-state">
      还没有匹配的文章，去创建第一篇吧。
    </div>

    <div v-else class="article-grid">
      <div v-for="article in articles" :key="article.id" class="card article-card">
        <div class="article-top">
          <div>
            <div class="status" :class="article.status === 'PUBLISHED' ? 'published' : 'draft'">
              {{ article.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </div>
            <h3>{{ article.title }}</h3>
            <p class="summary">{{ article.summary || '暂无摘要' }}</p>
          </div>
          <div class="metrics">
            <span>👀 {{ article.viewCount }}</span>
            <span>👍 {{ article.likeCount }}</span>
            <span>💬 {{ article.commentCount }}</span>
          </div>
        </div>

        <div class="meta-row">
          <span>兴趣：{{ article.primaryInterestCode || 'general' }}</span>
          <span v-if="article.folderName">文件夹：{{ article.folderName }}</span>
          <span>更新于：{{ formatTime(article.updatedAt || article.createdAt) }}</span>
        </div>

        <div class="tag-row">
          <span v-for="tag in article.tags" :key="tag" class="tag-chip">#{{ tag }}</span>
        </div>

        <div class="action-row">
          <button class="btn" @click="viewDetail(article.id)">查看</button>
          <button class="btn" @click="editArticle(article.id)">编辑</button>
          <button
            class="btn btn-primary"
            v-if="article.status === 'DRAFT'"
            @click="togglePublish(article, true)"
          >发布</button>
          <button
            class="btn btn-muted"
            v-else
            @click="togglePublish(article, false)"
          >取消发布</button>
          <button class="btn btn-danger" @click="removeArticle(article.id)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteArticle, getMyArticles, publishArticle, unpublishArticle } from '@/api/article'
import { getMyFolders } from '@/api/folder'
import { showConfirmDialog, showSuccessMessage } from '@/utils/message'

const router = useRouter()
const loading = ref(false)
const articles = ref([])
const folders = ref([])
const filters = reactive({
  keyword: '',
  status: '',
  folderId: '',
  startDate: '',
  endDate: ''
})

/**
 * 加载作者可选文件夹，用于筛选和归档展示。
 */
const loadFolders = async () => {
  const res = await getMyFolders()
  folders.value = res.data
}

/**
 * 加载当前作者的文章列表。
 */
const loadArticles = async () => {
  loading.value = true
  try {
    // 仅把已输入的筛选条件传给后端，避免无效参数干扰查询。
    const res = await getMyArticles({
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      folderId: filters.folderId || undefined,
      startDate: filters.startDate || undefined,
      endDate: filters.endDate || undefined
    })
    articles.value = res.data
  } catch (error) {
    articles.value = []
  } finally {
    loading.value = false
  }
}

/** 进入新建文章页。 */
const goCreate = () => router.push('/editor/new')
/** 进入文章编辑页。 */
const editArticle = (id) => router.push(`/editor/${id}`)
/** 进入文章详情页。 */
const viewDetail = (id) => router.push(`/articles/${id}`)

/**
 * 切换文章发布状态。
 */
const togglePublish = async (article, publish) => {
  try {
    if (publish) {
      await publishArticle(article.id)
      showSuccessMessage('文章已发布')
    } else {
      await unpublishArticle(article.id)
      showSuccessMessage('文章已取消发布')
    }
    await loadArticles()
  } catch (error) {
  }
}

/**
 * 删除文章前展示确认框，删除后刷新列表。
 */
const removeArticle = async (id) => {
  try {
    await showConfirmDialog('确认删除这篇文章吗？', '删除确认')
  } catch {
    return
  }

  try {
    await deleteArticle(id)
    showSuccessMessage('文章已删除')
    await loadArticles()
  } catch (error) {
  }
}

/** 格式化时间展示。 */
const formatTime = (value) => new Date(value).toLocaleString()

// 页面进入时同时加载筛选文件夹和文章数据。
onMounted(async () => {
  await loadFolders()
  await loadArticles()
})
</script>

<style scoped>
.page-shell {
  padding-bottom: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.page-header p {
  color: var(--text-secondary);
  margin: 0.5rem 0 0;
}

.filter-panel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 0.75rem;
}

.article-grid {
  display: grid;
  gap: 1rem;
  margin-top: 1rem;
}

.article-card h3 {
  margin: 0.5rem 0;
}

.article-top {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.summary,
.meta-row {
  color: var(--text-secondary);
}

.meta-row,
.tag-row,
.action-row {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-top: 0.75rem;
}

.status {
  display: inline-flex;
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  font-size: 0.75rem;
}

.status.published {
  background: #dcfce7;
  color: #166534;
}

.status.draft {
  background: #fef3c7;
  color: #92400e;
}

.metrics {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  white-space: nowrap;
  color: var(--text-secondary);
}

.btn-muted {
  background: #eef2ff;
  color: #4338ca;
}

.btn-danger {
  background: #fee2e2;
  color: #b91c1c;
}

.tag-chip {
  background: #eff6ff;
  color: #1d4ed8;
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
}

.empty-state {
  text-align: center;
  color: var(--text-secondary);
}
</style>
