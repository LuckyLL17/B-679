<template>
	<div class="login-container">
		<div class="card login-card">
			<h2 style="color: var(--primary-color)">注册新用户</h2>

			<div class="form-group" :class="{ 'has-error': errors.username }">
				<label>用户名</label>
				<input v-model="form.username" type="text" placeholder="4-20位字母或数字" @blur="validateUsername" />
				<span v-if="errors.username" class="error-text">{{ errors.username }}</span>
			</div>

			<div class="form-group" :class="{ 'has-error': errors.fullName }">
				<label>真实姓名</label>
				<input v-model="form.fullName" type="text" placeholder="请输入真实姓名" @blur="validateFullName" />
				<span v-if="errors.fullName" class="error-text">{{ errors.fullName }}</span>
			</div>

			<div class="form-group" :class="{ 'has-error': errors.password }">
				<label>密码</label>
				<input v-model="form.password" type="password" placeholder="至少6位字符" @blur="validatePassword" />
				<span v-if="errors.password" class="error-text">{{ errors.password }}</span>
			</div>

			<div class="button-group">
				<button class="btn-primary" @click="handleRegister" :disabled="isSubmitting">
					{{ isSubmitting ? '注册中...' : '注册' }}
				</button>
				<router-link to="/login" class="link">已有账号？去登录</router-link>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import request from '../../utils/request'
import toast from '../../utils/toast'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({ username: '', password: '', fullName: '' })
const errors = reactive({ username: '', password: '', fullName: '' })
const isSubmitting = ref(false)

const validateUsername = () => {
	const val = form.value.username.trim()
	if (!val) {
		errors.username = '请输入用户名'
	} else if (val.length < 4) {
		errors.username = '用户名至少4个字符'
	} else if (val.length > 20) {
		errors.username = '用户名不能超过20个字符'
	} else if (!/^[a-zA-Z0-9_]+$/.test(val)) {
		errors.username = '用户名只能包含字母、数字和下划线'
	} else {
		errors.username = ''
	}
	return !errors.username
}

const validateFullName = () => {
	const val = form.value.fullName.trim()
	if (!val) {
		errors.fullName = '请输入真实姓名'
	} else if (val.length < 2) {
		errors.fullName = '姓名至少2个字符'
	} else {
		errors.fullName = ''
	}
	return !errors.fullName
}

const validatePassword = () => {
	const val = form.value.password
	if (!val) {
		errors.password = '请设置密码'
	} else if (val.length < 6) {
		errors.password = '密码至少6位字符'
	} else {
		errors.password = ''
	}
	return !errors.password
}

const validateAll = () => {
	return validateUsername() && validateFullName() && validatePassword()
}

const handleRegister = async () => {
	if (!validateAll()) {
		toast.warning('请正确填写表单')
		return
	}

	isSubmitting.value = true
	try {
		await request.post('/auth/register', form.value)
		toast.success('注册成功，请登录')
		router.push('/login')
	} catch (e) {
		toast.error('注册失败: ' + (e.response?.data?.message || e.message || '网络错误'))
	} finally {
		isSubmitting.value = false
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
	width: 420px;
	text-align: center;
	background: white;
	padding: 48px 40px;
	border-radius: 8px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
}
.login-card h2 {
	color: rgb(150, 30, 25) !important;
	margin-bottom: 32px;
	font-size: 24px;
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
/* 错误状态样式 */
.has-error input {
	border-color: #dc3545;
}
.error-text {
	color: #dc3545;
	font-size: 12px;
	margin-top: 4px;
	display: block;
}
.btn-primary:disabled {
	background: #ccc;
	cursor: not-allowed;
}
</style>
