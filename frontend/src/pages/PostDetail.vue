<template>
  <div class="post-detail container">
    <div class="card" v-if="post">
      <h1 class="title">{{ post.title }}</h1>
      <div class="meta">
        <span>{{ post.author }}</span> · <span>{{ post.date }}</span>
      </div>
      <div class="content markdown-body" v-html="renderedContent"></div>
    </div>
    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const post = ref(null)
const md = new MarkdownIt()

const renderedContent = computed(() => {
  return post.value ? md.render(post.value.content) : ''
})

onMounted(async () => {
  // 模拟根据 ID 获取文章详情
  const id = route.params.id
  post.value = {
    id,
    title: 'Vue 3 Composition API 深度解析',
    author: 'admin',
    date: '2023-10-05',
    content: `
# 什么是 Composition API?

Composition API 是一组 API，允许我们使用导入的函数而不是声明选项来编写 Vue 组件。

## 为什么使用它？

- 更好的逻辑复用
- 更灵活的代码组织
- 更好的类型推导
    `
  }
})
</script>

<style scoped>
.post-detail {
  padding-top: 2rem;
}

.title {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.meta {
  color: var(--text-secondary);
  margin-bottom: 2rem;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 1rem;
}

.content {
  line-height: 1.8;
  color: var(--text-main);
}
</style>
