import axios from 'axios'
import router from '../router'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 10000
})

// 刷新token函数
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refresh_token')
  
  try {
    const response = await api.post('/refresh', {
      refresh_token: refreshToken
    })
    
    // 更新localStorage中的access_token
    localStorage.setItem('access_token', response.data.access_token)
    
    return response.data.access_token
  } catch (error) {
    // 刷新token失败，跳转到登录页
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('user')
    router.push('/login')
    return Promise.reject(error)
  }
}

let isRefreshing = false // 标记是否正在刷新token
let refreshSubscribers = [] // 存储等待刷新token的请求

// 通知所有等待刷新token的请求
const onRefreshed = (newToken) => {
  refreshSubscribers.forEach(callback => callback(newToken))
  refreshSubscribers = []
}

// 添加请求到等待队列
const addSubscriber = (callback) => {
  refreshSubscribers.push(callback)
}

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取access_token
    const accessToken = localStorage.getItem('access_token')
    
    // 检查是否为登录或注册请求
    const isLoginRequest = config.url === '/login' || config.url === 'login'
    const isRegisterRequest = config.url === '/register' || config.url === 'register'
    
    // 除了登录和注册请求外，其他请求都需要携带Authorization头
    if (!isLoginRequest && !isRegisterRequest) {
      // 添加Authorization请求头
      config.headers.Authorization = `Bearer ${accessToken}`
      console.log('添加Authorization头到请求:', config.url)
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    const originalRequest = error.config
    
    // 处理401未授权错误
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      // 检查是否已经在刷新token
      if (!isRefreshing) {
        isRefreshing = true
        
        // 刷新token
        return refreshToken()
          .then(newToken => {
            isRefreshing = false
            // 更新请求头的token
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`
            // 通知所有等待的请求
            onRefreshed(newToken)
            // 重新发送原请求
            return api(originalRequest)
          })
          .catch(err => {
            isRefreshing = false
            return Promise.reject(err)
          })
      } else {
        // 如果已经在刷新token，则将请求添加到等待队列
        return new Promise(resolve => {
          addSubscriber(newToken => {
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`
            resolve(api(originalRequest))
          })
        })
      }
    }
    
    return Promise.reject(error)
  }
)

// 退出登录函数
export const logout = async () => {
  try {
    // 显式获取token并记录日志
    const accessToken = localStorage.getItem('access_token')
    console.log('执行退出登录，token值:', accessToken)
    
    // 发送退出登录请求，不检查token有效性
    const response = await api.post('/logout')
    console.log('退出登录成功:', response)
    return response
  } catch (error) {
    // 即使后端请求失败，也要清除本地存储
    console.error('退出登录请求失败:', error.response || error.message || error)
  } finally {
    // 清除本地存储中的token和用户信息
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('user')
    console.log('本地令牌已清除')
  }
}

export default api
