import request from './request'

/**
 * 提交登录请求。
 */
export const loginAPI = (data) => request.post('/auth/login', data)

/**
 * 提交注册请求。
 */
export const registerAPI = (data) => request.post('/auth/register', data)

/**
 * 获取当前登录用户信息。
 */
export const meAPI = (config = {}) => request.get('/auth/me', config)
