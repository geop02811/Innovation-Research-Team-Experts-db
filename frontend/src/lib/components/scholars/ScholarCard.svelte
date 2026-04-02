<script lang="ts">
	import type { Scholar } from '$lib/types/scholar';

	let { scholar }: { scholar: Scholar } = $props();
</script>

<a class="scholar-card-link" href={`/experts/${scholar.slug}`} aria-label={`View ${scholar.fullName} profile`}>
	<article class="scholar-card">
		<img src={scholar.avatarUrl} alt={scholar.fullName} loading="lazy" />
		<div class="card-content">
			<p class="meta">{scholar.cohort}</p>
			<h2>{scholar.fullName}</h2>
			<p class="headline">{scholar.headline}</p>
			<p class="bio">{scholar.shortBio}</p>
			<div class="tags">
				{#each scholar.researchAreas as area}
					<span>{area}</span>
				{/each}
			</div>
		</div>
	</article>
</a>
