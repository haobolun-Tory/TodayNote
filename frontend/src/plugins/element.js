import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const elementOptions = {
  locale: zhCn,
  size: 'default',
  button: {
    autoInsertSpace: true,
  },
}

/**
 * 为应用安装 Element Plus，并统一注入全局配置。
 */
export const installElement = (app) => {
  app.use(ElementPlus, elementOptions)
}

/**
 * 获取 Element Plus 全局配置对象。
 */
export const getElementOptions = () => elementOptions
