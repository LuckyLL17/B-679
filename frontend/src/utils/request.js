import axios from 'axios'

const service = axios.create({
	baseURL: '/api',
	timeout: 5000,
	withCredentials: true,
})

const redirectTo = (path) => {
	if (window.location.pathname !== path) {
		window.location.href = path
	}
}

service.interceptors.response.use(
	(response) => {
		const res = response.data

		// 非 JSON 响应兜底 (如 HTML 302 重定向)
		if (typeof res === 'string' && res.includes('<!DOCTYPE html>')) {
			console.warn('Received HTML response, possibly session expired.')
			redirectTo('/login')
			return Promise.reject(new Error('Session expired'))
		}

		// 统一 ApiResponse 处理
		if (res && typeof res.code !== 'undefined') {
			if (res.code === 0) {
				return res.data
			} else {
				// 业务错误码处理
				if (res.code === 401) {
					redirectTo('/login')
				}
				if (res.code === 403) {
					redirectTo('/user/home')
				}
				const error = new Error(res.message || '未知错误')
				error.response = { data: res }
				return Promise.reject(error)
			}
		}
		return res
	},
	(error) => {
		console.error('API Error:', error)
		// HTTP 级别错误 (网络/服务器)
		if (error.response && error.response.status === 401) {
			redirectTo('/login')
		}
		if (error.response && error.response.status === 403) {
			redirectTo('/user/home')
		}
		return Promise.reject(error)
	},
)

export default service
