<template>
	<div class="user-layout">
		<header class="user-header">
			<div class="logo">🎓 校园活动</div>
			<div class="user-info" v-if="user">
				<span>欢迎, {{ user.fullName }}</span>
				<button class="btn-text" @click="handleLogout">退出登录</button>
			</div>
		</header>
		<main class="user-main">
			<router-view />
		</main>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const user = ref(null)

onMounted(async () => {
	try {
		user.value = await request.get('/auth/me')
	} catch (e) {
		router.push('/login')
	}
})

const handleLogout = async () => {
	try {
		await request.post('/auth/logout')
	} finally {
		router.push('/login')
	}
}
</script>

<style scoped>
.user-layout {
	min-height: 100vh;
	background: var(--bg-color);
}
.user-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 40px;
	height: 60px;
	background: rgb(150, 30, 25);
	color: white;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
.logo {
	font-size: 18px;
	font-weight: 600;
	letter-spacing: 1px;
}
.user-info {
	display: flex;
	align-items: center;
	gap: 16px;
	font-size: 14px;
}
.btn-text {
	background: rgba(255, 255, 255, 0.15);
	border: 1px solid rgba(255, 255, 255, 0.4);
	color: white;
	padding: 6px 14px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 13px;
	transition: background 0.2s;
}
.btn-text:hover {
	background: rgba(255, 255, 255, 0.25);
}
.user-main {
	max-width: 1200px;
	margin: 0 auto;
	padding: 32px 24px;
}
</style>
