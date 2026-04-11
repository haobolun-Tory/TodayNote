<template>
  <div class="auth-layout">
    <div class="container auth-layout__inner">
      <div class="auth-layout__brand">
        <router-link to="/" class="auth-layout__logo">TodayNote</router-link>
        <span class="domain-badge">账号入口</span>
      </div>

      <div class="auth-layout__content">
        <div class="auth-layout__intro card">
          <h1>开始你的内容创作</h1>
          <p>统一登录、注册入口，后续作者域页面都从这里进入。</p>
          <div class="auth-layout__actions">
            <router-link to="/login" class="auth-link" :class="{ 'auth-link--active': route.path === '/login' }">登录</router-link>
            <router-link to="/register" class="auth-link" :class="{ 'auth-link--active': route.path === '/register' }">注册</router-link>
            <router-link to="/" class="auth-back-link">返回广场</router-link>
          </div>
        </div>

        <div class="auth-layout__view">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>