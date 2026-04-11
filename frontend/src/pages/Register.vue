<template>
  <div class="register-page container">
    <div class="card register-form">
      <h2 class="title">注册 TodayNote</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input v-model="username" type="text" class="form-input" required />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input v-model="password" type="password" class="form-input" required />
        </div>
        <div class="form-group">
          <label class="form-label">确认密码</label>
          <input v-model="confirmPassword" type="password" class="form-input" required />
        </div>
        <button type="submit" class="btn btn-primary btn-block">注册</button>
      </form>
      <div class="footer-links">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showSuccessMessage, showWarningMessage } from '@/utils/message'

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const router = useRouter()
const userStore = useUserStore()

/**
 * 提交注册表单，并在成功后引导到登录页。
 */
const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    showWarningMessage('两次密码不一致')
    return
  }

  const result = await userStore.register(username.value, password.value)
  if (result.success) {
    showSuccessMessage('注册成功，请登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  padding-top: 4rem;
}

.register-form {
  width: 100%;
  max-width: 400px;
}

.title {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--primary-color);
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
