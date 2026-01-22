<template>
	<div class="login-container">
		<div class="card login-card">
			<h2 style="color: var(--primary-color)">校园活动系统</h2>
			<div class="form-group">
				<label>用户名</label>
				<input v-model="form.username" type="text" placeholder="admin" />
			</div>
			<div class="form-group">
				<label>密码</label>
				<input v-model="form.password" type="password" placeholder="admin123" />
			</div>
			<div class="button-group">
				<button class="btn-primary" @click="handleLogin">登录</button>
				<router-link to="/register" class="link">注册新账号</router-link>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue'
import request from '../../utils/request'
import toast from '../../utils/toast'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
	try {
		const res = await request.post('/auth/login', new URLSearchParams(form.value))
		toast.success('登录成功')

		// 获取用户信息以判断角色
		const user = await request.get('/auth/me')
		if (user.role === 'ADMIN') {
			router.push('/admin/events')
		} else {
			router.push('/user/home')
		}
	} catch (e) {
		toast.error('登录失败: ' + (e.response?.data?.message || e.message || '未知错误'))
	}
}
</script>

<style scoped>
.login-container {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
	background: linear-gradient(135deg, rgb(150, 30, 25) 0%, rgb(80, 15, 12) 100%);
}
.login-card {
	width: 400px;
	text-align: center;
	background: white;
	padding: 48px 40px;
	border-radius: 8px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
}
.login-card h2 {
	color: rgb(150, 30, 25) !important;
	margin-bottom: 32px;
}
.form-group {
	margin-bottom: 20px;
	text-align: left;
}
.form-group label {
	font-weight: 600;
	color: #333;
	font-size: 14px;
}
.form-group input {
	width: 100%;
	padding: 12px;
	margin-top: 8px;
	border: 1px solid #e0e0e0;
	border-radius: 4px;
	box-sizing: border-box;
	font-size: 14px;
}
.form-group input:focus {
	outline: none;
	border-color: rgb(150, 30, 25);
}
.button-group {
	display: flex;
	flex-direction: column;
	gap: 12px;
	margin-top: 24px;
}
.btn-primary {
	background: rgb(150, 30, 25);
	color: white;
	border: none;
	padding: 12px;
	border-radius: 4px;
	font-size: 15px;
	font-weight: 500;
	cursor: pointer;
	transition: background 0.2s;
}
.btn-primary:hover {
	background: rgb(120, 20, 18);
}
.link {
	color: rgb(150, 30, 25);
	text-decoration: none;
	font-size: 14px;
}
.link:hover {
	text-decoration: underline;
}
</style>
