import { createRouter, createWebHistory } from 'vue-router'
import ArticleList from '@/pages/ArticleList.vue'
import ArticleDetail from '@/pages/ArticleDetail.vue'
import ArticleEdit from '@/pages/ArticleEdit.vue'
import FolderManage from '@/pages/FolderManage.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import AuthorLayout from '@/layouts/AuthorLayout.vue'
import Login from '@/pages/Login.vue'
import MyArticles from '@/pages/MyArticles.vue'
import PublicLayout from '@/layouts/PublicLayout.vue'
import Register from '@/pages/Register.vue'
import { useUserStore } from '@/stores/user'

const AUTHOR_HOME_PATH = '/me/articles'
const LOGIN_PATH = '/login'

/**
 * 规范化回跳地址，避免把用户带到登录页本身或非法路径。
 */
const normalizeRedirectPath = (target) => {
  if (typeof target !== 'string' || !target.startsWith('/')) {
    return AUTHOR_HOME_PATH
  }

  if (target.startsWith('/login') || target.startsWith('/register')) {
    return AUTHOR_HOME_PATH
  }

  return target
}

const routes = [
  {
    path: '/',
    component: PublicLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: ArticleList
      },
      {
        path: 'articles/:id',
        name: 'ArticleDetail',
        component: ArticleDetail
      }
    ]
  },
  {
    path: '/',
    component: AuthorLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'editor/new',
        name: 'ArticleCreate',
        component: ArticleEdit,
        meta: { requiresAuth: true }
      },
      {
        path: 'editor/:id',
        name: 'ArticleEdit',
        component: ArticleEdit,
        meta: { requiresAuth: true }
      },
      {
        path: 'me/articles',
        name: 'MyArticles',
        component: MyArticles,
        meta: { requiresAuth: true }
      },
      {
        path: 'me/folders',
        name: 'FolderManage',
        component: FolderManage,
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/',
    component: AuthLayout,
    children: [
      {
        path: 'login',
        name: 'Login',
        component: Login,
        meta: { guestOnly: true }
      },
      {
        path: 'register',
        name: 'Register',
        component: Register,
        meta: { guestOnly: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

/**
 * 全局前置守卫：统一处理登录态初始化、作者域访问控制和访客页回跳。
 */
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  // 首次进入路由前先恢复登录态，确保守卫判断基于真实状态。
  if (!userStore.initialized) {
    await userStore.initializeAuth()
  }

  // 作者域必须登录后访问，并记录来源地址以便登录后回跳。
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({
      path: LOGIN_PATH,
      query: {
        redirect: normalizeRedirectPath(to.fullPath),
        reason: 'auth'
      }
    })
    return
  }

  // 已登录用户不再停留在登录/注册页，直接引导进入作者域。
  if (to.meta.guestOnly && userStore.isLoggedIn) {
    next(normalizeRedirectPath(to.query.redirect))
    return
  }

  next()
})

export default router
