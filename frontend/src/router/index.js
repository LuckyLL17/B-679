import { createRouter, createWebHistory } from 'vue-router'
import request from '../utils/request'

// Layouts
import UserLayout from '../layouts/UserLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

// Auth Pages (No Layout)
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'

// User Pages
import UserHome from '../views/user/Home.vue'
import UserEventDetail from '../views/user/EventDetail.vue'

// Admin Pages
import AdminEventList from '../views/admin/EventList.vue'
import AdminEventDetail from '../views/admin/EventDetail.vue'

const routes = [
	// 公共页面 (无布局)
	{ path: '/login', component: Login },
	{ path: '/register', component: Register },

	// 用户端 /user/**
	{
		path: '/user',
		component: UserLayout,
		redirect: '/user/home',
		children: [
			{ path: 'home', component: UserHome },
			{ path: 'events/:id', component: UserEventDetail },
		],
	},

	// 管理员端 /admin/**
	{
		path: '/admin',
		component: AdminLayout,
		redirect: '/admin/events',
		meta: { requiresAdmin: true },
		children: [
			{ path: 'events', component: AdminEventList },
			{ path: 'events/:id', component: AdminEventDetail },
		],
	},

	// 根路径：未登录默认进登录页（避免首屏等待鉴权接口导致白屏）
	{ path: '/', redirect: '/login' },
]

const router = createRouter({
	history: createWebHistory(),
	routes,
})

router.beforeEach(async (to) => {
	const publicPages = ['/', '/login', '/register']
	const authRequired = !publicPages.includes(to.path)

	if (authRequired) {
		try {
			// 尝试获取用户信息
			const user = await request.get('/auth/me')

			// 未登录 -> 跳转登录
			if (!user) {
				return '/login'
			}

			// 已登录但访问 /user 且是管理员 -> 跳转后台
			if (user.role === 'ADMIN' && to.path.startsWith('/user')) {
				return '/admin/events'
			}

			// 已登录但访问 /admin 且不是管理员 -> 跳转前台
			if (to.path.startsWith('/admin') && user.role !== 'ADMIN') {
				return '/user/home'
			}

			// 访问后台且是管理员 -> 放行
			if (to.meta.requiresAdmin && user.role === 'ADMIN') {
				return true
			}
		} catch (error) {
			console.log('Auth check failed:', error)
			return '/login'
		}
	}
	return true
})

export default router
