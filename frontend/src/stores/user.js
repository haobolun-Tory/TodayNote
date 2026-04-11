import { defineStore } from 'pinia'
import { loginAPI, meAPI, registerAPI } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    initialized: false
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || state.userInfo?.username || ''
  },
  actions: {
    /**
     * 统一维护 Token 的内存态和本地持久化。
     */
    setToken(token) {
      this.token = token || ''

      if (this.token) {
        localStorage.setItem('token', this.token)
        return
      }

      localStorage.removeItem('token')
    },

    /**
     * 统一维护当前用户信息的内存态和本地持久化。
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo || null

      if (this.userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        return
      }

      localStorage.removeItem('userInfo')
    },

    /**
     * 登录后保存 Token，并立即刷新当前用户信息。
     */
    async login(username, password) {
      try {
        const res = await loginAPI({ username, password })
        this.setToken(res.data)
        await this.fetchMe()
        return { success: true }
      } catch (err) {
        return { success: false }
      }
    },

    /**
     * 执行注册请求。
     */
    async register(username, password) {
      try {
        await registerAPI({ username, password })
        return { success: true, message: '注册成功' }
      } catch (err) {
        return { success: false }
      }
    },

    /**
     * 拉取当前登录用户信息。
     * `silent` 模式用于应用初始化阶段，避免重复弹错。
     */
    async fetchMe(options = {}) {
      if (!this.token) return null

      try {
        const res = await meAPI({
          skipErrorMessage: !!options.silent,
          skipAuthRedirect: !!options.silent
        })
        this.setUserInfo(res.data)
        return this.userInfo
      } catch (err) {
        this.logout()
        return null
      }
    },

    /**
     * 应用启动时恢复登录态。
     */
    async initializeAuth() {
      if (this.initialized) {
        return this.userInfo
      }

      this.initialized = true

      // 没有 Token 时直接清空用户信息，避免脏数据残留。
      if (!this.token) {
        this.setUserInfo(null)
        return null
      }

      return this.fetchMe({ silent: true })
    },

    /**
     * 主动退出登录。
     */
    logout() {
      this.setToken('')
      this.setUserInfo(null)
      this.initialized = true
    }
  }
})
