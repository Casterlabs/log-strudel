<script lang="ts">
	import { connectionUrl, connectionPassword, checkSettings } from '$lib/app/stores';
	import { onMount } from 'svelte';

	const LEVEL_COLORS: { [key: string]: string } = {
		FATAL: 'red',
		SEVERE: 'darkred',
		WARNING: 'yellow',
		INFO: 'blue',
		DEBUG: 'springgreen',
		TRACE: 'greenyellow'
	};

	let executingQuery = false;

	let keyTree = {};
	let currentPath: string[] = [];

	let lines: any[] = [];

	$: remainingPartsThatCanBeNavigatedTo = (() => {
		let root = keyTree;
		for (const part of currentPath) {
			// @ts-ignore
			root = root[part] || {};
		}
		return Object.keys(root);
	})();

	$: currentPath,
		(async () => {
			if (currentPath.length == 0) {
				lines = [];
				return;
			}
			executingQuery = true;
			lines =
				(await fetch(`${$connectionUrl}/lines/by-key/${currentPath.join('.')}`, {
					method: 'GET',
					headers: new Headers({
						Authorization: 'Bearer ' + $connectionPassword
					})
				})
					.then((response) => response.json())
					.then((json) => {
						if (json.errors.length > 0) throw json.errors.join(', ');
						return json.data.lines;
					})
					.catch(alert)
					.finally(() => (executingQuery = false))) || [];
		})();

	function rebuildTree() {
		executingQuery = true;
		fetch(`${$connectionUrl}/key-tree`, {
			method: 'GET',
			headers: new Headers({
				Authorization: 'Bearer ' + $connectionPassword
			})
		})
			.then((response) => response.json())
			.then((json) => {
				if (json.errors.length > 0) throw json.errors.join(', ');
				keyTree = json.data;
			})
			.catch(alert)
			.finally(() => (executingQuery = false));
	}

	onMount(() => {
		checkSettings();
		rebuildTree();
	});
</script>

<div class="-mx-4 px-2 mt-3 mb-4 border-b border-base-8 pb-4">
	<nav class="flex" aria-label="Breadcrumb">
		<ol role="list" class="flex w-full space-x-4 px-4 sm:px-6 lg:px-8">
			<li class="flex">
				<div class="flex items-center">
					<button on:click={() => (currentPath = [])} class="text-base-12 hover:text-base-11">
						<icon data-icon="home" />
						<span class="sr-only">Home</span>
					</button>
				</div>
			</li>
			{#each currentPath as part, idx}
				<li class="flex">
					<div class="flex items-center">
						<icon class="h-full w-6 flex-shrink-0 text-base-12" data-icon="chevron-right" />
						<button
							on:click={() => {
								currentPath = currentPath.slice(0, idx + 1);
							}}
							class="ml-4 text-sm font-medium text-base-12 hover:text-base-11 underline"
						>
							{part}
						</button>
					</div>
				</li>
			{/each}
			<li class="flex">
				<div class="flex items-center">
					<button
						on:click={() => {
							rebuildTree();
							currentPath = currentPath;
						}}
						class="text-base-12 hover:text-base-11"
					>
						<icon data-icon="arrow-path" />
						<span class="sr-only">Refresh</span>
					</button>
				</div>
			</li>
		</ol>
	</nav>
</div>

{#if remainingPartsThatCanBeNavigatedTo.length > 0}
	<ul class="mb-4">
		{#each remainingPartsThatCanBeNavigatedTo as part}
			<li class="flex">
				<div class="flex items-center text-base-12 hover:text-base-11">
					<icon class="w-4 h-4" data-icon="arrow-right" />
					<button
						on:click={() => {
							currentPath.push(part);
							currentPath = currentPath;
						}}
						class="ml-1.5 text-sm font-medium underline"
					>
						{part}
					</button>
				</div>
			</li>
		{/each}
	</ul>
{/if}
{#if lines.length == 0}
	<p class="text-base-11">No lines to view</p>
{:else}
	<div
		class="overflow-hidden text-base-12 rounded-md border border-base-6 bg-base-2 shadow-sm text-sm align-bottom"
		style="overflow-x: auto;"
	>
		<table class="relative min-w-full">
			<thead class="bg-base-6">
				<tr>
					<th scope="col" class="py-3.5 px-3 text-left text-sm font-semibold text-base-12 w-1">
						Timestamp
					</th>
					<th scope="col" class="py-3.5 px-3 text-left text-sm font-semibold text-base-12 w-1">
						Level
					</th>
					<th scope="col" class="py-3.5 px-3 text-left text-sm font-semibold text-base-12">
						Line
					</th>
				</tr>
			</thead>
			<tbody class="text-sm text-base-11 font-mono">
				{#each lines as { id, timestamp, level, line }}
					{@const date = new Date(timestamp)}
					<tr class="border-t border-base-6">
						<td class="whitespace-nowrap px-3 py-4 relative pr-7 text-base-11">
							{date.toLocaleDateString()}
							{date.toLocaleTimeString()}
						</td>
						<td
							class="whitespace-nowrap px-3 py-4 relative pr-7"
							style:color={LEVEL_COLORS[level] || ''}
						>
							{level}
						</td>
						<td class="whitespace-nowrap px-3 py-4 relative pr-7">
							{line}
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
{/if}
