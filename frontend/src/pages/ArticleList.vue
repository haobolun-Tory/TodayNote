<template>
  <div class="container page-shell">
    <div class="hero card">
      <div>
        <h1>TodayNote 公开文章广场</h1>
        <p>面向访客与注册用户，支持按关键字、作者、标签、兴趣发现内容。</p>
      </div>
      <button v-if="userStore.isLoggedIn" class="btn btn-primary" @click="router.push('/editor/new')">开始写作</button>
    </div>

    <div class="card filter-panel">
      <input v-model="filters.q" class="form-input" placeholder="搜索文章标题或摘要" />
      <input v-model="filters.author" class="form-input" placeholder="搜索作者" />
      <input v-model="filters.tag" class="form-input" placeholder="按标签搜索，如 Vue" />
      <select v-model="filters.interest" class="form-input">
        <option value="">全部兴趣</option>
        <option value="java-backend">Java 后端</option>
        <option value="frontend">前端开发</option>
        <option value="reading">读书写作</option>
        <option value="life">生活复盘</option>
        <option value="other">其他</option>
      </select>
      <button class="btn btn-primary" @click="loadArticles">查询</button>
    </div>

    <div v-if="loading" class="card">加载中...</div>
    <div v-else-if="articles.length === 0" class="card empty-state">暂无符合条件的公开文章。</div>

    <div v-else class="article-list">
      <article v-for="article in articles" :key="article.id" class="card article-card">
        <div class="card-header">
          <div>
            <h2 class="article-title" @click="goToDetail(article.id)">{{ article.title }}</h2>
            <p class="article-summary">{{ article.summary }}</p>
          </div>
          <div class="metrics">
            <span>👀 {{ article.viewCount }}</span>
            <span>👍 {{ article.likeCount }}</span>
            <span>💬 {{ article.commentCount }}</span>
          </div>
        </div>

        <div class="meta-row">
          <span>作者：{{ article.authorName }}</span>
          <span>兴趣：{{ article.primaryInterestCode || 'general' }}</span>
          <span>发布时间：{{ formatTime(article.publishedAt || article.updatedAt) }}</span>
        </div>

        <div class="tag-row">
          <button v-for="tag in article.tags" :key="tag" class="tag-chip" @click="setTag(tag)">#{{ tag }}</button>
        </div>

        <div class="card-actions">
          <button class="btn btn-primary" @click="goToDetail(article.id)">阅读全文</button>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicArticles } from '@/api/article'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const articles = ref([])
const filters = reactive({ q: '', author: '', tag: '', interest: '' })

/**
 * 拉取公开文章列表。
 */
const loadArticles = async () => {
  loading.value = true
  try {
    // 仅提交用户实际选择的筛选条件，减少无效参数。
    const res = await getPublicArticles({
      q: filters.q || undefined,
      author: filters.author || undefined,
      tag: filters.tag || undefined,
      interest: filters.interest || undefined
    })
    articles.value = res.data
  } catch (error) {
    articles.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 点击标签后自动设置筛选条件并重新查询。
 */
const setTag = (tag) => {
  filters.tag = tag
  loadArticles()
}

/** 跳转到文章详情页。 */
const goToDetail = (id) => router.push(`/articles/${id}`)
/** 格式化时间显示。 */
const formatTime = (value) => new Date(value).toLocaleString()

// 页面初始化时加载公开文章。
onMounted(loadArticles)
</script>

<style scoped>
.page-shell {
  padding-bottom: 2rem;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.hero p,
.article-summary,
.meta-row,
.empty-state {
  color: var(--text-secondary);
}

.filter-panel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 0.75rem;
  margin-top: 1rem;
}

.article-list {
  display: grid;
  gap: 1rem;
  margin-top: 1rem;
}

.card-header,
.meta-row,
.tag-row,
.card-actions {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.article-title {
  margin: 0;
  cursor: pointer;
}

.article-title:hover {
  color: var(--primary-color);
}

.metrics {
  display: flex;
  gap: 0.75rem;
  color: var(--text-secondary);
}

.tag-chip {
  border: none;
  background: #eff6ff;
  color: #1d4ed8;
  border-radius: 999px;
  padding: 0.3rem 0.75rem;
  cursor: pointer;
}

.empty-state {
  text-align: center;
}
</style>
