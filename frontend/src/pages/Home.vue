<template>
  <div class="home-page container">
    <div class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-item card">
        <h2 class="post-title">
          <router-link :to="'/post/' + post.id">{{ post.title }}</router-link>
        </h2>
        <div class="post-meta">
          <span>{{ post.author }}</span> · <span>{{ post.date }}</span>
        </div>
        <p class="post-summary">{{ post.summary }}</p>
      </div>
      
      <div v-if="posts.length === 0" class="empty-state">
        暂无文章
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const posts = ref([])

onMounted(async () => {
  // 模拟数据
  // const res = await request.get('/posts')
  posts.value = [
    { id: 1, title: '第一篇博客', summary: '这是我的第一篇博客文章摘要...', author: 'admin', date: '2023-10-01' },
    { id: 2, title: 'Vue 3composition API 简介', summary: 'composition API 是 Vue 3 的核心特性...', author: 'user1', date: '2023-10-05' }
  ]
})
</script>

<style scoped>
.post-item {
  transition: transform 0.2s;
}

.post-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.post-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
}

.post-title a {
  color: var(--text-main);
}

.post-title a:hover {
  color: var(--primary-color);
}

.post-meta {
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin-bottom: 0.75rem;
}

.post-summary {
  color: var(--text-secondary);
  line-height: 1.6;
}
</style>
