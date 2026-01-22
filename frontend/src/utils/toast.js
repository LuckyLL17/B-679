import { createVNode, render } from 'vue'
import ToastComponent from '../components/Toast.vue'

const div = document.createElement('div')
document.body.appendChild(div)

const vnode = createVNode(ToastComponent)
render(vnode, div)

const toast = {
	success(msg) {
		vnode.component.exposed.show(msg, 'success')
	},
	error(msg) {
		vnode.component.exposed.show(msg, 'error')
	},
	warning(msg) {
		vnode.component.exposed.show(msg, 'warning')
	},
	info(msg) {
		vnode.component.exposed.show(msg, 'info')
	},
}

export default toast
