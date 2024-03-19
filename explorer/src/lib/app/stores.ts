import { goto } from '$app/navigation';
import { get, writable } from 'svelte/store';

export const connectionUrl = writable('');
export const connectionPassword = writable('');

export function checkSettings() {
	if (get(connectionUrl).length == 0 || get(connectionPassword).length == 0) {
		goto('/app/settings');
	}
}

export function loadSettings() {
	connectionUrl.set(localStorage.getItem('casterlabs:log_strudel:url') || '');
	connectionPassword.set(localStorage.getItem('casterlabs:log_strudel:password') || '');
}

export function saveSettings() {
	localStorage.setItem('casterlabs:log_strudel:url', get(connectionUrl));
	localStorage.setItem('casterlabs:log_strudel:password', get(connectionPassword));
}
