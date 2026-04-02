<script lang="ts">
	import ScholarCard from '$lib/components/scholars/ScholarCard.svelte';
	import type { Scholar } from '$lib/types/scholar';

	let { scholars }: { scholars: Scholar[] } = $props();
</script>

<section class="grid-shell" aria-label="Scholars list">
	{#if scholars.length === 0}
		<p class="empty-state">No scholars found.</p>
	{:else}
		<div class="grid">
			{#each scholars as scholar (scholar.id)}
				<ScholarCard {scholar} />
			{/each}
		</div>
	{/if}
</section>
