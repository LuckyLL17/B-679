<template>
	<div class="admin-layout">
		<aside class="admin-sidebar">
			<div class="sidebar-logo">🛠 管理后台</div>
			<nav class="sidebar-nav">
				<router-link to="/admin/events" class="nav-item" active-class="active"> 📋 活动管理 </router-link>
			</nav>
		</aside>
		<div class="admin-content">
			<header class="admin-header">
				<span>管理员: {{ user?.fullName || '加载中...' }}</span>
				<button class="btn-logout" @click="handleLogout">退出登录</button>
			</header>
			<main class="admin-main">
				<router-view />
			</main>
		</div>
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
		// 权限校验：非管理员跳转到用户端
		if (user.value.role !== 'ADMIN') {
			router.push('/user/home')
		}
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
.admin-layout {
	display: flex;
	min-height: 100vh;
}
.admin-sidebar {
	width: 220px;
	background: linear-gradient(180deg, rgb(150, 30, 25) 0%, rgb(100, 20, 18) 100%);
	color: white;
	flex-shrink: 0;
}
.sidebar-logo {
	padding: 20px;
	font-size: 16px;
	font-weight: 600;
	letter-spacing: 1px;
	border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.sidebar-nav {
	padding: 16px 0;
}
.nav-item {
	display: block;
	padding: 12px 20px;
	color: rgba(255, 255, 255, 0.8);
	text-decoration: none;
	font-size: 14px;
	transition: all 0.2s;
}
.nav-item:hover {
	background: rgba(255, 255, 255, 0.1);
	color: white;
}
.nav-item.active {
	background: rgba(255, 255, 255, 0.2);
	color: white;
	border-left: 3px solid white;
}
.admin-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	background: var(--bg-color);
}
.admin-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 24px;
	height: 56px;
	background: white;
	border-bottom: 1px solid var(--border-color);
	font-size: 14px;
}
.btn-logout {
	background: rgb(150, 30, 25);
	color: white;
	border: none;
	padding: 8px 16px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 13px;
}
.btn-logout:hover {
	background: rgb(120, 20, 18);
}
.admin-main {
	flex: 1;
	padding: 24px;
	overflow: auto;
}
</style>
