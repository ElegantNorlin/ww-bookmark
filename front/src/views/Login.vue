<template>
  <div class="login-container">
    <div class="login-box">
      <h2>{{ isLogin ? '登录' : '注册' }}</h2>
      
      <form @submit.prevent="handleSubmit">
        <!-- 用户名输入 -->
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="form.username"
            placeholder="请输入用户名"
            required
          />
        </div>
        
        <!-- 邮箱输入（仅注册时显示） -->
        <div class="form-group" v-if="!isLogin">
          <label for="email">邮箱</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            placeholder="请输入邮箱"
            required
          />
        </div>
        
        <!-- 密码输入 -->
        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="form.password"
            placeholder="请输入密码"
            required
          />
        </div>
        
        <!-- 登录/注册按钮 -->
        <button type="submit" class="submit-btn">
          {{ isLogin ? '登录' : '注册' }}
        </button>
      </form>
      
      <!-- 切换登录/注册模式 -->
      <div class="toggle-mode">
        <span v-if="isLogin">还没有账号？</span>
        <span v-else>已有账号？</span>
        <button class="toggle-btn" @click="toggleMode">
          {{ isLogin ? '立即注册' : '立即登录' }}
        </button>
      </div>
      
      <!-- 错误信息 -->
      <div class="error-message" v-if="error">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 状态管理
const isLogin = ref(true) // true: 登录模式，false: 注册模式
const error = ref('')
const form = ref({
  username: '',
  email: '',
  password: ''
})

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  error.value = '' // 清除错误信息
  // 重置表单
  form.value = {
    username: '',
    email: '',
    password: ''
  }
}

// 处理表单提交
const handleSubmit = async () => {
  error.value = ''
  
  try {
    if (isLogin.value) {
      // 登录请求
      const response = await axios.post('http://localhost:8080/api/login', {
        username: form.value.username,
        password: form.value.password
      })
      
      // 保存token和用户信息到localStorage
      localStorage.setItem('access_token', response.data.access_token)
      localStorage.setItem('refresh_token', response.data.refresh_token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      
      // 跳转到首页
      router.push('/')
    } else {
      // 注册请求
      const response = await axios.post('http://localhost:8080/api/register', {
        username: form.value.username,
        email: form.value.email,
        password: form.value.password
      })
      
      // 注册成功后切换到登录模式
      toggleMode()
    }
  } catch (err) {
    // 处理错误信息
    error.value = err.response?.data?.message || '操作失败，请稍后重试'
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #1565c0;
}

.toggle-mode {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.toggle-btn {
  background: none;
  border: none;
  color: #1976d2;
  cursor: pointer;
  font-size: 14px;
  text-decoration: underline;
  padding: 0;
  margin-left: 5px;
}

.toggle-btn:hover {
  color: #1565c0;
}

.error-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #ffebee;
  color: #c62828;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}
</style>
