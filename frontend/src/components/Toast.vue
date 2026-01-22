<template>
	<transition name="toast-fade">
		<div v-show="visible" :class="['toast', type]" :style="{ top: top + 'px' }">
			<i class="icon" v-if="type === 'success'">✅</i>
			<i class="icon" v-if="type === 'error'">❌</i>
			<i class="icon" v-if="type === 'warning'">⚠️</i>
			<span>{{ message }}</span>
		</div>
	</transition>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)
const message = ref('')
const type = ref('info') // success, error, warning, info
const top = ref(20)

const show = (msg, t = 'info') => {
	message.value = msg
	type.value = t
	visible.value = true
	setTimeout(() => {
		visible.value = false
	}, 3000)
}

defineExpose({ show })
</script>

<style scoped>
.toast {
	position: fixed;
	left: 50%;
	transform: translateX(-50%);
	padding: 10px 20px;
	border-radius: 4px;
	background: white;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
	display: flex;
	align-items: center;
	gap: 8px;
	z-index: 9999;
	font-size: 14px;
	min-width: 200px;
	justify-content: center;
}
.toast.success {
	color: #52c41a;
	border: 1px solid #b7eb8f;
	background: #f6ffed;
}
.toast.error {
	color: #f5222d;
	border: 1px solid #ffa39e;
	background: #fff1f0;
}
.toast.warning {
	color: #faad14;
	border: 1px solid #ffe58f;
	background: #fffbe6;
}

.toast-fade-enter-active,
.toast-fade-leave-active {
	transition: all 0.3s ease;
}
.toast-fade-enter-from,
.toast-fade-leave-to {
	opacity: 0;
	transform: translate(-50%, -20px);
}
</style>
