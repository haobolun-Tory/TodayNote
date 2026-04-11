<template>
  <el-container class="layout-shell">
    <Navbar />
    <el-main class="layout-main">
      <section class="container domain-banner domain-banner--author">
        <div>
          <span class="domain-badge">作者域</span>
          <h2 class="domain-title">创作工作台</h2>
          <p class="domain-description">围绕写作、归档与内容管理搭建统一作者工作区。</p>
        </div>
        <el-button type="primary" @click="router.push('/editor/new')">写新文章</el-button>
      </section>

      <div class="container workspace-shell">
        <aside class="workspace-sidebar">
          <router-link
            v-for="item in workspaceLinks"
            :key="item.to"
            :to="item.to"
            class="workspace-link"
            :class="{ 'workspace-link--active': isActive(item.match) }"
          >
            <span class="workspace-link__title">{{ item.label }}</span>
            <span class="workspace-link__desc">{{ item.description }}</span>
          </router-link>
        </aside>

        <section class="workspace-content">
          <router-view />
        </section>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()
const router = useRouter()

const workspaceLinks = [
  {
    to: '/me/articles',
    match: ['/me/articles'],
    label: '我的文章',
    description: '查看草稿、已发布文章与筛选结果'
  },
  {
    to: '/me/folders',
    match: ['/me/folders'],
    label: '文件夹管理',
    description: '统一维护归档结构与排序'
  },
  {
    to: '/editor/new',
    match: ['/editor'],
    label: '写作中心',
    description: '新建文章或继续编辑已有内容'
  }
]

const isActive = (matches) => matches.some((item) => route.path.startsWith(item))
</script>