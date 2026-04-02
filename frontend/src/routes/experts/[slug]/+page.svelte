<script lang="ts">
	import ScholarProfile from '$lib/components/scholars/ScholarProfile.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
</script>

<svelte:head>
	<title>{data.scholar.fullName} | Expert Profile</title>
	<meta name="description" content={data.scholar.shortBio} />
</svelte:head>

<main class="page-shell page-shell-profile">
	<p class="back-link"><a href="/experts">Back to all experts</a></p>
	<ScholarProfile scholar={data.scholar} />
</main>
