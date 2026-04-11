import axios from 'axios'
import { showErrorMessage } from '@/utils/message'

const SUCCESS_CODE = 0
const UNAUTHORIZED_CODE = 40100
const DEFAULT_ERROR_MESSAGE = '请求失败，请稍后重试'

/**
 * 构建统一的前端错误对象，便于页面层按一致方式处理异常。
 */
const createAppError = ({ message, code, status, raw }) => {
  const error = new Error(message || DEFAULT_ERROR_MESSAGE)
  error.code = code
  error.status = status
  error.raw = raw
  return error
}

/**
 * 登录失效时统一清理本地登录态并跳转到登录页。
 */
const redirectToLogin = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')

  if (window.location.pathname === '/login') {
    return
  }

  const redirect = encodeURIComponent(`${window.location.pathname}${window.location.search}`)
  window.location.href = `/login?redirect=${redirect}`
}

/**
 * 判断当前请求是否需要自动跳转登录页。
 */
const shouldRedirectToLogin = (config, code, status) => {
  if (config?.skipAuthRedirect) {
    return false
  }

  const hasToken = !!localStorage.getItem('token')
  if (!hasToken) {
    return false
  }

  if (config?.url?.includes('/auth/login') || config?.url?.includes('/auth/register')) {
    return false
  }

  return code === UNAUTHORIZED_CODE || status === 401
}

/**
 * 判断当前请求是否展示默认错误提示。
 */
const shouldShowError = (config) => !config?.skipErrorMessage

/**
 * 兜底解析 HTTP 异常文案。
 */
const resolveHttpErrorMessage = (error) => {
  if (error.response?.data?.message) {
    return error.response.data.message
  }

  if (error.code === 'ECONNABORTED') {
    return '请求超时，请稍后重试'
  }

  if (!error.response) {
    return '网络异常，请检查网络连接'
  }

  switch (error.response.status) {
    case 400:
      return '请求参数有误'
    case 401:
      return '登录已失效，请重新登录'
    case 403:
      return '当前操作无权限执行'
    case 404:
      return '请求的资源不存在'
    case 500:
      return '服务器异常，请稍后重试'
    default:
      return DEFAULT_ERROR_MESSAGE
  }
}

const service = axios.create({
  baseURL: '/api', // 这里配合 vite.config.js 的 proxy
  timeout: 5000
})

// 请求拦截器：统一注入 Bearer Token。
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器：统一识别业务码、提示错误并处理登录失效。
service.interceptors.response.use(
  response => {
    const res = response.data
    if (!res || typeof res.code === 'undefined') {
      return res
    }

    if (res.code !== SUCCESS_CODE) {
      const appError = createAppError({
        message: res.message || DEFAULT_ERROR_MESSAGE,
        code: res.code,
        status: response.status,
        raw: res
      })

      if (shouldShowError(response.config)) {
        showErrorMessage(appError.message)
      }

      if (shouldRedirectToLogin(response.config, res.code, response.status)) {
        redirectToLogin()
      }

      return Promise.reject(appError)
    }

    return res
  },
  error => {
    const message = resolveHttpErrorMessage(error)
    const appError = createAppError({
      message,
      code: error.response?.data?.code,
      status: error.response?.status,
      raw: error.response?.data || error
    })

    if (shouldShowError(error.config)) {
      showErrorMessage(message)
    }

    if (shouldRedirectToLogin(error.config, appError.code, appError.status)) {
      redirectToLogin()
    }

    return Promise.reject(appError)
  }
)

export default service
