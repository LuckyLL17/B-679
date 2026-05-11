<template>
	<div>
		<h1 class="page-title">近期活动</h1>

		<div class="grid" v-if="events.length > 0">
			<div v-for="event in events" :key="event.id" class="card event-card">
				<h3>{{ event.title }}</h3>
				<p class="desc">{{ event.description || '暂无描述' }}</p>
				<div class="meta">
					📍 {{ event.location }} <br />
					🕒 {{ new Date(event.startTime).toLocaleDateString() }}
				</div>
				<div class="actions">
					<button class="btn-primary" @click="router.push(`/user/events/${event.id}`)">查看详情</button>
				</div>
			</div>
		</div>
		<div v-else class="empty-state">
			<p>暂无正在进行的活动</p>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const events = ref([])
const router = useRouter()

onMounted(async () => {
	try {
		const eventRes = await request.get('/events')
		console.log('Event List Response:', eventRes)
		events.value = eventRes?.content || []
	} catch (e) {
		console.log('Error loading events', e)
	}
})
</script>

<style scoped>
.page-title {
	color: #333;
	margin-bottom: 20px;
}
.grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 20px;
}
.card {
	background: white;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.event-card h3 {
	margin-top: 0;
	color: rgb(150, 30, 25);
}
.desc {
	color: #666;
	font-size: 14px;
	height: 40px;
	overflow: hidden;
}
.meta {
	font-size: 12px;
	color: #888;
	margin-bottom: 15px;
}
.actions {
	text-align: right;
}
.btn-primary {
	background: rgb(150, 30, 25);
	color: white;
	border: none;
	padding: 8px 16px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 14px;
	transition: background 0.2s;
}
.btn-primary:hover {
	background: rgb(120, 20, 18);
}
.empty-state {
	text-align: center;
	padding: 50px;
	color: #666;
	font-size: 16px;
}
</style>
