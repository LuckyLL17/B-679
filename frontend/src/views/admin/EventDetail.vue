<template>
	<div class="admin-event-detail">
		<div class="page-header">
			<router-link to="/admin/events" class="back-link">← 返回活动列表</router-link>
			<h1>{{ event?.title || '加载中...' }}</h1>
		</div>

		<div class="event-info card" v-if="event">
			<div class="info-grid">
				<div><strong>地点:</strong> {{ event.location }}</div>
				<div><strong>人数限制:</strong> {{ event.maxParticipants === 0 ? '不限' : event.maxParticipants + '人' }}</div>
				<div><strong>活动时间:</strong> {{ formatDate(event.startTime) }} ~ {{ formatDate(event.endTime) }}</div>
				<div>
					<strong>签到窗口:</strong> {{ formatDate(event.checkinStartTime) }} ~ {{ formatDate(event.checkinEndTime) }}
				</div>
			</div>
			<p class="description">{{ event.description || '暂无描述' }}</p>
		</div>

		<div class="tabs">
			<button :class="{ active: activeTab === 'registration' }" @click="activeTab = 'registration'">
				📋 报名名单 ({{ registrations.length }})
			</button>
			<button :class="{ active: activeTab === 'checkin' }" @click="activeTab = 'checkin'">
				✅ 签到名单 ({{ checkedInCount }})
			</button>
		</div>

		<div class="card roster-table">
			<table>
				<thead>
					<tr>
						<th>姓名</th>
						<th>报名时间</th>
						<th>签到状态</th>
						<th v-if="activeTab === 'checkin'">签到时间</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="r in filteredList" :key="r.id">
						<td>{{ r.fullName }}</td>
						<td>{{ formatDate(r.registrationTime) }}</td>
						<td>
							<span :class="r.checkinStatus ? 'status-ok' : 'status-pending'">
								{{ r.checkinStatus ? '✅ 已签到' : '❌ 未签到' }}
							</span>
						</td>
						<td v-if="activeTab === 'checkin'">
							{{ r.checkinTime ? formatDate(r.checkinTime) : '-' }}
						</td>
					</tr>
					<tr v-if="filteredList.length === 0">
						<td colspan="4" class="empty">暂无数据</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import request from '../../utils/request'
import toast from '../../utils/toast'

const route = useRoute()
const eventId = route.params.id
const event = ref(null)
const registrations = ref([])
const activeTab = ref('registration')

onMounted(async () => {
	await fetchEvent()
	await fetchRegistrations()
})

const fetchEvent = async () => {
	try {
		event.value = await request.get(`/admin/events/${eventId}`)
	} catch (e) {
		toast.error('加载活动失败')
	}
}

const fetchRegistrations = async () => {
	try {
		registrations.value = await request.get(`/admin/events/${eventId}/registrations`)
	} catch (e) {
		toast.error('加载名单失败')
	}
}

const checkedInCount = computed(() => registrations.value.filter((r) => r.checkinStatus).length)

const filteredList = computed(() => {
	if (activeTab.value === 'checkin') {
		return registrations.value.filter((r) => r.checkinStatus)
	}
	return registrations.value
})

const formatDate = (str) => {
	if (!str) return ''
	return new Date(str).toLocaleString()
}
</script>

<style scoped>
.page-header {
	margin-bottom: 20px;
}
.back-link {
	color: #667eea;
	text-decoration: none;
	font-size: 14px;
}
.back-link:hover {
	text-decoration: underline;
}
.page-header h1 {
	margin: 10px 0 0;
	color: #333;
}
.card {
	background: white;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	margin-bottom: 20px;
}
.info-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 12px;
	margin-bottom: 16px;
}
.description {
	color: #666;
	line-height: 1.6;
}
.tabs {
	display: flex;
	gap: 8px;
	margin-bottom: 16px;
}
.tabs button {
	padding: 10px 20px;
	border: none;
	background: #e8e8e8;
	border-radius: 4px;
	cursor: pointer;
	transition: all 0.2s;
}
.tabs button.active {
	background: #667eea;
	color: white;
}
.roster-table table {
	width: 100%;
	border-collapse: collapse;
}
.roster-table th,
.roster-table td {
	padding: 12px;
	text-align: left;
	border-bottom: 1px solid #f0f0f0;
}
.roster-table th {
	background: #fafafa;
	font-weight: 600;
}
.status-ok {
	color: #28a745;
}
.status-pending {
	color: #ffc107;
}
.empty {
	text-align: center;
	color: #999;
	padding: 40px !important;
}
</style>
