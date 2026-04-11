<template>
  <div class="container detail-shell">
    <div class="header-row">
      <button class="btn" @click="router.push('/')">← 返回广场</button>
      <button v-if="article?.editable" class="btn btn-primary" @click="router.push(`/editor/${article.id}`)">编辑文章</button>
    </div>

    <div v-if="loading" class="card">正在加载文章内容...</div>
    <div v-else-if="!article" class="card">文章不存在或暂无权限查看。</div>

    <template v-else>
      <article class="card article-main">
        <div class="status-line">
          <span class="status-badge">{{ article.status === 'PUBLISHED' ? '已发布' : '草稿' }}</span>
          <span>兴趣：{{ article.primaryInterestCode || 'general' }}</span>
        </div>
        <h1>{{ article.title }}</h1>
        <p class="summary">{{ article.summary }}</p>
        <div class="meta-row">
          <span>作者：{{ article.authorName }}</span>
          <span>发布时间：{{ formatTime(article.publishedAt || article.createdAt) }}</span>
          <span>浏览：{{ article.viewCount }}</span>
        </div>
        <div class="tag-row">
          <span v-for="tag in article.tags" :key="tag" class="tag-chip">#{{ tag }}</span>
        </div>

        <div class="action-row">
          <button class="btn btn-primary" @click="toggleLike">
            {{ article.likedByCurrentUser ? '取消点赞' : '点赞' }}（{{ article.likeCount }}）
          </button>
          <span class="comment-count">评论 {{ comments.length }}</span>
        </div>

        <div class="markdown-body" v-html="renderedContent"></div>
      </article>

      <section class="card comment-section">
        <div class="section-header">
          <h2>留言区</h2>
          <p>登录后可以参与评论互动。</p>
        </div>

        <div v-if="userStore.isLoggedIn" class="comment-editor">
          <textarea v-model="commentForm.content" class="form-input comment-input" placeholder="写下你的想法..." />
          <button class="btn btn-primary" @click="submitComment">发布留言</button>
        </div>
        <div v-else class="login-tip">
          <router-link to="/login">登录后留言</router-link>
        </div>

        <div v-if="comments.length === 0" class="empty-comments">还没有评论，来抢沙发吧。</div>
        <div v-else class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-head">
              <strong>{{ comment.nickname || comment.username }}</strong>
              <span>{{ formatTime(comment.createdAt) }}</span>
            </div>
            <p>{{ comment.content }}</p>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { createComment, getArticleById, getComments, likeArticle, unlikeArticle } from '@/api/article'
import { useUserStore } from '@/stores/user'
import { showSuccessMessage, showWarningMessage } from '@/utils/message'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const md = new MarkdownIt({ html: false, linkify: true, breaks: true })

const loading = ref(true)
const article = ref(null)
const comments = ref([])
const commentForm = ref({ content: '' })

/**
 * 根据 Markdown 内容生成 HTML，用于详情页展示。
 */
const renderedContent = computed(() => md.render(article.value?.content || ''))

/**
 * 并行加载文章详情与评论列表。
 */
const loadArticle = async () => {
  loading.value = true
  try {
    const [articleRes, commentRes] = await Promise.all([
      getArticleById(route.params.id),
      getComments(route.params.id)
    ])
    article.value = articleRes.data
    comments.value = commentRes.data
  } catch (error) {
    article.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 切换点赞状态。未登录时先跳转到登录页。
 */
const toggleLike = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    // 前端先做本地数量回写，保证交互反馈及时。
    if (article.value.likedByCurrentUser) {
      await unlikeArticle(article.value.id)
      article.value.likeCount -= 1
      article.value.likedByCurrentUser = false
      showSuccessMessage('已取消点赞')
    } else {
      await likeArticle(article.value.id)
      article.value.likeCount += 1
      article.value.likedByCurrentUser = true
      showSuccessMessage('点赞成功')
    }
  } catch (error) {
  }
}

/**
 * 提交评论，成功后重新拉取文章数据与评论列表。
 */
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    showWarningMessage('请输入评论内容')
    return
  }
  try {
    await createComment(article.value.id, { content: commentForm.value.content })
    commentForm.value.content = ''
    await loadArticle()
    showSuccessMessage('留言成功')
  } catch (error) {
  }
}

/** 格式化时间显示。 */
const formatTime = (value) => new Date(value).toLocaleString()

// 页面进入时加载详情数据。
onMounted(loadArticle)
</script>

<style scoped>
.detail-shell {
  padding-bottom: 2rem;
}

.header-row,
.meta-row,
.tag-row,
.action-row,
.comment-head,
.section-header {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.article-main h1 {
  margin-bottom: 0.5rem;
}

.summary,
.meta-row,
.section-header p,
.login-tip,
.empty-comments,
.comment-head span {
  color: var(--text-secondary);
}

.status-line {
  display: flex;
  gap: 0.75rem;
  color: var(--text-secondary);
}

.status-badge,
.tag-chip {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 0.25rem 0.75rem;
}

.status-badge {
  background: #dbeafe;
  color: #1d4ed8;
}

.tag-chip {
  background: #eff6ff;
  color: #1d4ed8;
}

.markdown-body {
  line-height: 1.8;
  margin-top: 1.5rem;
}

.markdown-body :deep(pre) {
  background: #111827;
  color: #f9fafb;
  padding: 1rem;
  border-radius: 0.75rem;
  overflow: auto;
}

.markdown-body :deep(code) {
  background: #f3f4f6;
  padding: 0.15rem 0.35rem;
  border-radius: 0.25rem;
}

.comment-editor {
  display: grid;
  gap: 0.75rem;
  margin: 1rem 0;
}

.comment-input {
  min-height: 110px;
  resize: vertical;
}

.comment-list {
  display: grid;
  gap: 1rem;
}

.comment-item {
  border-top: 1px solid #e5e7eb;
  padding-top: 1rem;
}

.comment-item p {
  margin: 0.5rem 0 0;
}
</style>
