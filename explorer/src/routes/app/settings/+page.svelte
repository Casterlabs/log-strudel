<script lang="ts">
	import { connectionUrl, connectionPassword, saveSettings } from '$lib/app/stores';
</script>

<!-- svelte-ignore a11y-label-has-associated-control -->
<div>
	<div>
		<label>Server URL:</label>
		<input
			class="px-1.5 py-1 block w-full text-base-12 rounded-md border transition hover:border-base-8 border-base-7 bg-base-1 shadow-sm focus:border-primary-7 focus:outline-none focus:ring-1 focus:ring-primary-7 text-sm"
			placeholder="https://example.com:10244"
			bind:value={$connectionUrl}
			on:change={saveSettings}
		/>
	</div>

	<div>
		<label>Token:</label>
		<input
			type="password"
			class="px-1.5 py-1 block w-full text-base-12 rounded-md border transition hover:border-base-8 border-base-7 bg-base-1 shadow-sm focus:border-primary-7 focus:outline-none focus:ring-1 focus:ring-primary-7 text-sm"
			bind:value={$connectionPassword}
			on:change={saveSettings}
		/>
	</div>
</div>
