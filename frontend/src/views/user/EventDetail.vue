<template>
	<div class="container">
		<div class="card" v-if="event">
			<div class="header">
				<h1 class="title">{{ event.title }}</h1>
				<span :class="['status-badge', statusClass]">{{ statusText }}</span>
			</div>

			<div class="info-grid">
				<div class="info-item"><strong>地点：</strong> {{ event.location }}</div>
				<div class="info-item">
					<strong>时间：</strong> {{ formatDate(event.startTime) }} - {{ formatDate(event.endTime) }}
				</div>
				<div class="info-item">
					<strong>签到窗口：</strong> {{ formatDate(event.checkinStartTime) }} - {{ formatDate(event.checkinEndTime) }}
				</div>
				<div class="info-item">
					<strong>人数限制：</strong> {{ event.maxParticipants === 0 ? '不限' : event.maxParticipants + '人' }}
				</div>
			</div>

			<div class="description">
				<h3>活动介绍</h3>
				<p>{{ event.description || '暂无描述' }}</p>
			</div>

			<div class="action-area">
				<button v-if="!event.isRegistered" class="btn-primary btn-lg" :disabled="isEnded" @click="handleRegister">
					{{ isEnded ? '活动已结束' : '立即报名' }}
				</button>
				<div v-else class="registered-area">
					<p class="success-text">✅ 您已报名参加此活动</p>

					<div class="checkin-section">
						<button
							v-if="!event.hasCheckedIn"
							class="btn-warning btn-lg"
							:disabled="!canCheckIn"
							@click="handleCheckIn"
						>
							{{ checkInButtonText }}
						</button>
						<p v-else class="success-text">🎉 已签到</p>
					</div>

					<button class="btn-danger btn-outline" @click="showCancelModal = true" v-if="!event.hasCheckedIn">
						取消报名
					</button>
				</div>
			</div>
		</div>

		<!-- 二次确认弹窗 -->
		<div v-if="showCancelModal" class="modal-overlay" @click.self="showCancelModal = false">
			<div class="modal-dialog">
				<h3>确认取消报名</h3>
				<p>您确定要取消报名此活动吗？</p>
				<div class="modal-actions">
					<button class="btn-secondary" @click="showCancelModal = false">返回</button>
					<button class="btn-danger" @click="confirmCancel">确认取消</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../../utils/request'
import toast from '../../utils/toast'

const route = useRoute()
const router = useRouter()
const event = ref(null)
const eventId = route.params.id
const showCancelModal = ref(false)

onMounted(() => {
	fetchEvent()
})

const fetchEvent = async () => {
	try {
		const res = await request.get(`/events/${eventId}`)
		event.value = res
	} catch (e) {
		toast.error('加载活动失败')
	}
}

const handleRegister = async () => {
	try {
		await request.post(`/events/${eventId}/registrations`)
		toast.success('报名成功！')
		fetchEvent() // Refresh status
	} catch (e) {
		toast.error('报名失败: ' + (e.response?.data?.message || e.message))
	}
}

const handleCheckIn = async () => {
	try {
		await request.post(`/events/${eventId}/checkin`)
		toast.success('签到成功！')
		fetchEvent()
	} catch (e) {
		toast.error('签到失败: ' + (e.response?.data?.message || e.message))
	}
}

const isEnded = computed(() => {
	if (!event.value) return false
	return new Date() > new Date(event.value.endTime)
})

const canCheckIn = computed(() => {
	if (!event.value) return false
	const now = new Date()
	return now >= new Date(event.value.checkinStartTime) && now <= new Date(event.value.checkinEndTime)
})

const checkInButtonText = computed(() => {
	if (!event.value) return '签到'
	const now = new Date()
	if (now < new Date(event.value.checkinStartTime)) return '签到未开始'
	if (now > new Date(event.value.checkinEndTime)) return '签到已截止'
	return '立即签到'
})

const confirmCancel = async () => {
	showCancelModal.value = false
	try {
		await request.delete(`/events/${eventId}/registrations`)
		toast.success('已取消报名')
		fetchEvent()
	} catch (e) {
		toast.error('取消失败')
	}
}

const formatDate = (str) => {
	return new Date(str).toLocaleString()
}

const statusClass = computed(() => {
	if (!event.value) return ''
	const now = new Date()
	const start = new Date(event.value.startTime)
	const end = new Date(event.value.endTime)
	if (now < start) return 'status-upcoming'
	if (now > end) return 'status-ended'
	return 'status-ongoing'
})

const statusText = computed(() => {
	if (!event.value) return ''
	const now = new Date()
	const start = new Date(event.value.startTime)
	const end = new Date(event.value.endTime)
	if (now < start) return '即将开始'
	if (now > end) return '已结束'
	return '进行中'
})
</script>

<style scoped>
.title {
	color: var(--primary-color);
	margin: 0;
}
.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-bottom: 1px solid var(--gray-light);
	padding-bottom: 20px;
	margin-bottom: 20px;
}
.info-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 15px;
	background: #f8f9fa;
	padding: 15px;
	border-radius: 6px;
}
.description {
	margin-top: 20px;
	line-height: 1.6;
}
.action-area {
	margin-top: 30px;
	text-align: center;
	border-top: 1px solid var(--gray-light);
	padding-top: 20px;
}
.btn-lg {
	padding: 12px 40px;
	font-size: 18px;
}
.success-text {
	color: #28a745;
	font-weight: bold;
	font-size: 18px;
	margin-bottom: 10px;
}
.btn-outline {
	background: transparent;
	border: 1px solid #dc3545;
	color: #dc3545;
}
.status-badge {
	padding: 5px 10px;
	border-radius: 4px;
	color: white;
	font-size: 14px;
}
.status-upcoming {
	background: #17a2b8;
}
.status-ongoing {
	background: #28a745;
}
.status-ended {
	background: #6c757d;
}
.checkin-section {
	margin: 20px 0;
}
.btn-warning {
	background-color: #ffc107;
	color: #212529;
	border: none;
	cursor: pointer;
	font-weight: bold;
}
.btn-warning:disabled {
	background-color: #e0e0e0;
	color: #999;
	cursor: not-allowed;
}
.btn-primary:disabled {
	background-color: #e0e0e0;
	color: #999;
	cursor: not-allowed;
	border-color: #e0e0e0;
}
/* 确认弹窗样式 */
.modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 1000;
}
.modal-dialog {
	background: white;
	padding: 24px;
	border-radius: 8px;
	width: 360px;
	text-align: center;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}
.modal-dialog h3 {
	margin-top: 0;
	color: #333;
}
.modal-dialog p {
	color: #666;
	margin-bottom: 20px;
}
.modal-actions {
	display: flex;
	justify-content: center;
	gap: 12px;
}
.btn-secondary {
	background: #6c757d;
	color: white;
	border: none;
	padding: 10px 20px;
	border-radius: 4px;
	cursor: pointer;
}
.btn-danger {
	background: #dc3545;
	color: white;
	border: none;
	padding: 10px 20px;
	border-radius: 4px;
	cursor: pointer;
}
</style>
