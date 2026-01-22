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

	// 根路径重定向到用户首页
	{ path: '/', redirect: '/user/home' },
]

const router = createRouter({
	history: createWebHistory(),
	routes,
})

router.beforeEach(async (to) => {
	if (!to.matched.some((record) => record.meta.requiresAdmin)) return true
	try {
		const user = await request.get('/auth/me')
		if (user?.role !== 'ADMIN') return '/user/home'
		return true
	} catch {
		return '/login'
	}
})

export default router
