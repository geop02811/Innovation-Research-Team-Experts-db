<script lang="ts">
	import { goto } from '$app/navigation';
	import type { Scholar } from '$lib/types/scholar';

	interface Props {
		scholar: Scholar;
		onTagClick?: (expertise: string) => void;
	}

	let { scholar, onTagClick }: Props = $props();

	const cardTags = $derived(
		scholar.areasOfExpertise && scholar.areasOfExpertise.length > 0
			? scholar.areasOfExpertise
			: scholar.researchAreas
	);

	const openProfile = async () => {
		await goto(`/experts/${scholar.slug}`);
	};

	const handleTagClick = (event: MouseEvent, tag: string) => {
		event.preventDefault();
		event.stopPropagation();
		onTagClick?.(tag);
	};
</script>

<div class="scholar-card-link">
	<article
		class="scholar-card"
		onclick={openProfile}
		onkeydown={(event) => {
			if (event.key === 'Enter' || event.key === ' ') {
				event.preventDefault();
				openProfile();
			}
		}}
		role="link"
		tabindex="0"
		aria-label={`View ${scholar.fullName} profile`}
	>
		<img src={scholar.avatarUrl} alt={scholar.fullName} loading="lazy" />
		<div class="card-content">
			<p class="meta">{scholar.cohort}</p>
			<h2>{scholar.fullName}</h2>
			<p class="headline">{scholar.headline}</p>
			<p class="bio">{scholar.shortBio}</p>
			<div class="tags">
				{#each cardTags as area}
					<button type="button" onclick={(event) => handleTagClick(event, area)}>{area}</button>
				{/each}
			</div>
		</div>
	</article>
</div>
