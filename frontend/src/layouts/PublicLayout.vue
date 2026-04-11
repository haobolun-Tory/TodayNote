<template>
  <el-container class="layout-shell">
    <Navbar />
    <el-main class="layout-main">
      <section class="container domain-banner domain-banner--public">
        <div>
          <span class="domain-badge">公开域</span>
          <h2 class="domain-title">内容广场</h2>
          <p class="domain-description">面向访客和注册用户，负责浏览、搜索与阅读已发布文章。</p>
        </div>
      </section>

      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'
</script>