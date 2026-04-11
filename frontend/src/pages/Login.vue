<template>
  <div class="login-page container">
    <div class="card login-form">
      <h2 class="title">登录 TodayNote</h2>
      <p v-if="showAuthTip" class="auth-tip">当前页面需要登录后访问，请先完成登录。</p>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input v-model="username" type="text" class="form-input" required />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input v-model="password" type="password" class="form-input" required />
        </div>
        <button type="submit" class="btn btn-primary btn-block">登录</button>
      </form>
      <div class="footer-links">
        <router-link to="/register">没有账号？去注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showSuccessMessage, showWarningMessage } from '@/utils/message'

const DEFAULT_LOGIN_REDIRECT = '/me/articles'

/**
 * 规范化登录成功后的回跳地址。
 */
const normalizeRedirectPath = (target) => {
  if (typeof target !== 'string' || !target.startsWith('/')) {
    return DEFAULT_LOGIN_REDIRECT
  }

  if (target.startsWith('/login') || target.startsWith('/register')) {
    return DEFAULT_LOGIN_REDIRECT
  }

  return target
}

const username = ref('')
const password = ref('')
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const showAuthTip = computed(() => route.query.reason === 'auth')

/**
 * 提交登录表单，并在成功后回跳到原始目标页。
 */
const handleLogin = async () => {
  if (!username.value || !password.value) {
    showWarningMessage('请输入用户名和密码')
    return
  }

  const result = await userStore.login(username.value, password.value)
  if (result.success) {
    showSuccessMessage('登录成功')
    router.push(normalizeRedirectPath(route.query.redirect))
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  padding-top: 4rem;
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.title {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--primary-color);
}

.auth-tip {
  margin: -0.5rem 0 1rem;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 0.875rem;
  line-height: 1.6;
}

.btn-block {
  width: 100%;
  margin-top: 1rem;
}

.footer-links {
  margin-top: 1rem;
  text-align: center;
  font-size: 0.875rem;
  color: var(--text-secondary);
}
</style>
