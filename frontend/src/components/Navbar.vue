<template>
  <el-header class="navbar-shell">
    <div class="container navbar-content">
      <router-link to="/" class="brand">TodayNote</router-link>

      <el-space wrap>
        <router-link to="/" class="nav-link" :class="{ 'nav-link--active': isActive('/') }">发现文章</router-link>
        <template v-if="userStore.isLoggedIn">
          <router-link to="/me/articles" class="nav-link" :class="{ 'nav-link--active': isActive('/me/articles') }">我的文章</router-link>
          <router-link to="/me/folders" class="nav-link" :class="{ 'nav-link--active': isActive('/me/folders') }">文件夹</router-link>
          <router-link to="/editor/new" class="nav-link nav-link-primary" :class="{ 'nav-link--active': isActive('/editor') }">写文章</router-link>
          <span class="user-name">{{ userStore.nickname }}</span>
          <el-button text type="primary" @click="logout">退出</el-button>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link" :class="{ 'nav-link--active': isActive('/login') }">登录</router-link>
          <router-link to="/register" class="nav-link nav-link-primary" :class="{ 'nav-link--active': isActive('/register') }">注册</router-link>
        </template>
      </el-space>
    </div>
  </el-header>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const userStore = useUserStore()
const router = useRouter()

/**
 * 判断导航项是否处于激活状态。
 */
const isActive = (path) => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

/**
 * 退出登录后返回公开广场。
 */
const logout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.navbar-shell {
  height: auto;
  padding: 16px 0;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(14px);
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
}

.nav-link {
  color: var(--text-secondary);
  font-weight: 500;
}

.nav-link--active,
.nav-link:hover {
  color: var(--primary-color);
}

.nav-link-primary {
  color: var(--primary-color);
}

.user-name {
  color: var(--text-main);
  font-weight: 600;
}

@media (max-width: 768px) {
  .navbar-content {
    gap: 12px;
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
