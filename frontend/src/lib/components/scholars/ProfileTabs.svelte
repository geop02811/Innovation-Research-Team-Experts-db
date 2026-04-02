<script lang="ts">
	import type { ScholarProfileSection } from '$lib/types/scholar';

	interface Props {
		sections: ScholarProfileSection[];
	}

	let { sections }: Props = $props();
	let activeTab = $state(0);
</script>

<div class="profile-tabs-container">
	<nav class="profile-tabs" aria-label="Profile sections">
		{#each sections as section, index}
			<button
				type="button"
				class="tab-button {activeTab === index ? 'active' : ''}"
				onclick={() => (activeTab = index)}
			>
				{section.title}
			</button>
		{/each}
	</nav>

	<div class="tabs-content">
		{#each sections as section, index}
			<div
				id={`tab-content-${index}`}
				class="tab-content {activeTab === index ? 'active' : ''}"
				role="tabpanel"
				aria-labelledby={`tab-button-${index}`}
			>
				<h2>{section.title}</h2>
				<p>{section.body}</p>
			</div>
		{/each}
	</div>
</div>

<style>
	.profile-tabs-container {
		display: flex;
		flex-direction: column;
		gap: 2rem;
	}

	.profile-tabs {
		display: flex;
		gap: 2rem;
		border-bottom: 1px solid #e0e0e0;
	}

	.tab-button {
		background: none;
		border: none;
		padding: 0.75rem 0;
		font-size: 1rem;
		font-weight: 500;
		color: #666;
		cursor: pointer;
		position: relative;
		transition: color 0.2s ease;
		border-bottom: 2px solid transparent;
		margin-bottom: -1px;
	}

	.tab-button:hover {
		color: #333;
	}

	.tab-button.active {
		color: #333;
		border-bottom-color: #333;
	}

	.tabs-content {
		position: relative;
	}

	.tab-content {
		opacity: 0;
		pointer-events: none;
		position: absolute;
		width: 100%;
		transition: opacity 0.3s ease;
	}

	.tab-content.active {
		opacity: 1;
		pointer-events: auto;
		position: relative;
	}

	.tab-content h2 {
		margin: 0 0 1rem 0;
		font-size: 1.25rem;
	}

	.tab-content p {
		line-height: 1.6;
		color: #555;
		margin: 0;
	}
</style>
