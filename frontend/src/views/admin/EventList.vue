<template>
	<div class="container">
		<div class="header-row">
			<h1 class="page-title">活动管理</h1>
			<button class="btn-primary" @click="openModal()">+ 发布新活动</button>
		</div>

		<!-- Event List -->
		<div class="card">
			<table class="data-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>标题</th>
						<th>活动时间</th>
						<th>签到窗口</th>
						<th>地点</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="event in events" :key="event.id">
						<td>{{ event.id }}</td>
						<td>{{ event.title }}</td>
						<td>{{ formatDate(event.startTime) }} <br />~ {{ formatDate(event.endTime) }}</td>
						<td>{{ formatDate(event.checkinStartTime) }} <br />~ {{ formatDate(event.checkinEndTime) }}</td>
						<td>{{ event.location }}</td>
						<td>
							<button class="btn-sm btn-info" @click="router.push(`/admin/events/${event.id}`)">详情</button>
							<button
								class="btn-sm btn-edit"
								:class="{ 'btn-disabled': isEventEnded(event.endTime) }"
								:disabled="isEventEnded(event.endTime)"
								@click="openModal(event)"
								:title="isEventEnded(event.endTime) ? '活动已结束，不可编辑' : ''"
							>
								编辑
							</button>
							<button class="btn-sm btn-danger" @click="deleteEvent(event.id)">删除</button>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<!-- Edit Modal -->
		<div v-if="showModal" class="modal-overlay">
			<div class="modal">
				<h3>{{ isEdit ? '编辑活动' : '发布新活动' }}</h3>
				<form @submit.prevent="saveEvent">
					<div class="form-group">
						<label>活动标题</label>
						<input v-model="form.title" required />
					</div>
					<div class="form-group">
						<label>描述</label>
						<textarea v-model="form.description" rows="3"></textarea>
					</div>
					<div class="row">
						<div class="form-group col">
							<label>开始时间</label>
							<input v-model="form.startTime" type="datetime-local" required />
						</div>
						<div class="form-group col">
							<label>结束时间</label>
							<input v-model="form.endTime" type="datetime-local" required />
						</div>
					</div>
					<div class="row">
						<div class="form-group col">
							<label>签到开始</label>
							<input v-model="form.checkinStartTime" type="datetime-local" required />
						</div>
						<div class="form-group col">
							<label>签到结束</label>
							<input v-model="form.checkinEndTime" type="datetime-local" required />
						</div>
					</div>
					<div class="row">
						<div class="form-group col">
							<label>地点</label>
							<input v-model="form.location" />
						</div>
						<div class="form-group col">
							<label>最大人数 (0不限)</label>
							<input v-model="form.maxParticipants" type="number" />
						</div>
					</div>
					<div class="modal-actions">
						<button type="button" class="btn-secondary" @click="showModal = false">取消</button>
						<button type="submit" class="btn-primary">保存</button>
					</div>
				</form>
			</div>
		</div>

		<!-- Roster Modal -->
		<div v-if="showRoster" class="modal-overlay">
			<div class="modal modal-lg">
				<h3>活动名单: {{ currentEvent?.title }}</h3>
				<table class="data-table">
					<thead>
						<tr>
							<th>姓名</th>
							<th>报名时间</th>
							<th>签到状态</th>
							<th>签到时间</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="r in rosterData" :key="r.id">
							<td>{{ r.fullName }}</td>
							<td>{{ new Date(r.registrationTime).toLocaleString() }}</td>
							<td>
								<span :class="r.checkinStatus ? 'status-ok' : 'status-pending'">
									{{ r.checkinStatus ? '已签到' : '未签到' }}
								</span>
							</td>
							<td>{{ r.checkinTime ? new Date(r.checkinTime).toLocaleString() : '-' }}</td>
						</tr>
					</tbody>
				</table>
				<div class="modal-actions">
					<button class="btn-secondary" @click="showRoster = false">关闭</button>
				</div>
			</div>
		</div>

		<!-- Delete Confirmation Modal -->
		<div v-if="showDeleteModal" class="modal-overlay">
			<div class="modal" style="width: 400px">
				<h3>确认删除</h3>
				<p>您确定要删除此活动吗？此操作无法撤销。</p>
				<div class="modal-actions">
					<button class="btn-secondary" @click="showDeleteModal = false">取消</button>
					<button class="btn-danger" @click="confirmDelete">确认删除</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import toast from '../../utils/toast'

const router = useRouter()

const events = ref([])
const showModal = ref(false)
const showRoster = ref(false)
const rosterData = ref([])
const currentEvent = ref(null)

const isEdit = ref(false)
const form = ref({})

onMounted(() => {
	fetchEvents()
})

const openRoster = async (event) => {
	currentEvent.value = event
	rosterData.value = []
	showRoster.value = true
	try {
		const res = await request.get(`/admin/events/${event.id}/registrations`)
		rosterData.value = res
	} catch (e) {
		toast.error('获取名单失败')
	}
}

const fetchEvents = async () => {
	const res = await request.get('/admin/events')
	events.value = res.content
}

const openModal = (event = null) => {
	if (event) {
		isEdit.value = true
		form.value = { ...event }
	} else {
		isEdit.value = false
		form.value = { maxParticipants: 0 }
	}
	showModal.value = true
}

const saveEvent = async () => {
	try {
		if (isEdit.value) {
			await request.put(`/admin/events/${form.value.id}`, form.value)
		} else {
			await request.post('/admin/events', form.value)
		}
		showModal.value = false
		toast.success('保存成功')
		fetchEvents()
	} catch (e) {
		toast.error('保存失败: ' + e.message)
	}
}

const showDeleteModal = ref(false)
const deleteId = ref(null)

const deleteEvent = (id) => {
	deleteId.value = id
	showDeleteModal.value = true
}

const confirmDelete = async () => {
	if (!deleteId.value) return
	try {
		await request.delete(`/admin/events/${deleteId.value}`)
		toast.success('删除成功')
		fetchEvents()
	} catch (e) {
		toast.error('删除失败')
	} finally {
		showDeleteModal.value = false
		deleteId.value = null
	}
}

const formatDate = (str) => {
	if (!str) return ''
	return new Date(str).toLocaleString()
}

const isEventEnded = (endTime) => {
	return new Date() > new Date(endTime)
}
</script>

<style scoped>
.header-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}
.page-title {
	color: var(--primary-color);
}
.data-table {
	width: 100%;
	border-collapse: collapse;
}
.data-table th,
.data-table td {
	padding: 12px;
	text-align: left;
	border-bottom: 1px solid var(--gray-light);
}
.data-table th {
	background-color: #f8f9fa;
	font-weight: 600;
}
.btn-sm {
	padding: 6px 12px;
	margin-right: 6px;
	cursor: pointer;
	font-size: 13px;
	border-radius: 4px;
	border: none;
	transition: all 0.2s;
}
.btn-sm:last-child {
	margin-right: 0;
}
/* 详情按钮 - 轻绿色 */
.btn-info {
	background: #28a745;
	color: white;
}
.btn-info:hover {
	background: #218838;
}
/* 编辑按钮 - 蓝色 */
.btn-edit {
	background: #007bff;
	color: white;
}
.btn-edit:hover {
	background: #0056b3;
}
.btn-edit:disabled,
.btn-edit.btn-disabled {
	background: #e9ecef;
	color: #adb5bd;
	cursor: not-allowed;
	border: 1px solid #e9ecef;
}
/* 删除按钮 - 红色警告 */
.btn-danger {
	background: #fff;
	color: #dc3545;
	border: 1px solid #dc3545;
}
.btn-danger:hover {
	background: #dc3545;
	color: white;
}
/* Modal Styles */
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
}
.modal {
	background: white;
	padding: 20px;
	border-radius: 8px;
	width: 600px;
	max-width: 90%;
}
.row {
	display: flex;
	gap: 15px;
}
.col {
	flex: 1;
}
.form-group {
	margin-bottom: 15px;
	display: flex;
	flex-direction: column;
}
.form-group input,
.form-group textarea {
	padding: 8px;
	border: 1px solid var(--gray-light);
	border-radius: 4px;
	margin-top: 5px;
}
.modal-actions {
	display: flex;
	justify-content: flex-end;
	gap: 10px;
	margin-top: 20px;
}
.btn-secondary {
	background: #6c757d;
	color: white;
	border: none;
	padding: 8px 16px;
	border-radius: 4px;
	cursor: pointer;
}
.btn-info {
	background: #17a2b8;
	color: white;
	border: none;
	border-radius: 4px;
}
.modal-lg {
	width: 800px;
}
.status-ok {
	color: #28a745;
	font-weight: bold;
}
.status-pending {
	color: #ffc107;
}
.btn-disabled {
	background-color: #e9ecef;
	color: #adb5bd;
	cursor: not-allowed;
	border: 1px solid #e9ecef;
}
</style>
