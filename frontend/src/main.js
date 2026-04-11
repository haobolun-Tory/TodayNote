import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'element-plus/dist/index.css'
import App from './App.vue'
import { installElement } from './plugins/element'
import router from './router'
import { useUserStore } from './stores/user'
import './assets/style.css'

/**
 * 启动前端应用，并在挂载前恢复登录态与当前用户信息。
 */
const bootstrap = async () => {
	const app = createApp(App)
	const pinia = createPinia()

	// 先注册状态管理与 UI 插件，确保后续初始化逻辑可用。
	app.use(pinia)
	installElement(app)

	const userStore = useUserStore(pinia)
	// 应用启动阶段优先恢复登录态，避免页面闪动和重复重定向。
	await userStore.initializeAuth()

	app.use(router)
	await router.isReady()

	app.mount('#app')
}

// 统一从启动函数进入，便于后续扩展启动前逻辑。
bootstrap()
