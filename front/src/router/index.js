import { createRouter, createWebHistory } from 'vue-router'
import BookmarkLayout from '../components/BookmarkLayout.vue'
import Login from '../views/Login.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: BookmarkLayout,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  // 检查是否已登录
  const isLoggedIn = localStorage.getItem('access_token') !== null
  
  if (requiresAuth && !isLoggedIn) {
    // 需要认证但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    // 已登录且访问登录页，跳转到首页
    next('/')
  } else {
    // 其他情况，正常访问
    next()
  }
})

export default router