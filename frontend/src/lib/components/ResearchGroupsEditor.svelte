<script lang="ts">
	import type { ResearchGroup } from '$lib/auth/types';

	interface Props {
		groups: ResearchGroup[];
		onchange: (groups: ResearchGroup[]) => void;
	}

	let { groups, onchange }: Props = $props();
	let activeIndex = $state<number | null>(null);
	let editorElement: HTMLDivElement | undefined;

	const createGroup = (): ResearchGroup => ({ name: '', organization: '' });

	const updateGroup = (index: number, patch: Partial<ResearchGroup>) => {
		onchange(groups.map((group, itemIndex) => (itemIndex === index ? { ...group, ...patch } : group)));
	};

	const removeGroup = (index: number) => {
		onchange(groups.filter((_, itemIndex) => itemIndex !== index));
		if (activeIndex === index) activeIndex = null;
		else if (activeIndex !== null && activeIndex > index) activeIndex -= 1;
	};

	const addGroup = () => {
		const nextGroups = [...groups, createGroup()];
		onchange(nextGroups);
		activeIndex = nextGroups.length - 1;
	};

	const toggleCard = (index: number) => {
		activeIndex = activeIndex === index ? null : index;
	};

	const closeWhenClickOutside = (event: MouseEvent) => {
		if (!editorElement || !(event.target instanceof Node)) return;
		if (!editorElement.contains(event.target)) activeIndex = null;
	};

	const groupSummary = (group: ResearchGroup) =>
		[group.name || 'Research group', group.organization].filter(Boolean).join(' - ');
</script>

<svelte:window onclick={closeWhenClickOutside} />

<div class="cards-editor" bind:this={editorElement}>
	{#each groups as group, index}
		<div class="entry-card" class:collapsed={activeIndex !== index}>
			<div class="entry-card-header">
				<button
					type="button"
					class="card-toggle"
					aria-expanded={activeIndex === index}
					onclick={() => toggleCard(index)}
				>
					<span class="card-title">Research group {index + 1}</span>
					<span class="card-summary">{groupSummary(group)}</span>
				</button>
				<button type="button" class="remove-btn" onclick={() => removeGroup(index)}>Remove</button>
			</div>

			{#if activeIndex === index}
				<label>
					Name of group
					<input
						value={group.name}
						placeholder="e.g. Machine Learning Research Group"
						oninput={(event) => updateGroup(index, { name: event.currentTarget.value })}
					/>
				</label>

				<label>
					Associated organization
					<input
						value={group.organization}
						placeholder="e.g. University of Zimbabwe AI Lab"
						oninput={(event) => updateGroup(index, { organization: event.currentTarget.value })}
					/>
				</label>
			{/if}
		</div>
	{/each}

	<button type="button" class="add-btn" onclick={addGroup}>+ Add research group</button>
</div>

<style>
	.cards-editor {
		display: grid;
		gap: 1rem;
		min-width: 0;
	}

	.entry-card {
		display: grid;
		gap: 0.9rem;
		border: 1px solid #d8dee9;
		border-radius: 8px;
		padding: 1rem;
		background: #fff;
		min-width: 0;
	}

	.entry-card.collapsed {
		gap: 0;
	}

	.entry-card-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
	}

	.card-toggle {
		border: 0;
		background: transparent;
		padding: 0;
		display: grid;
		gap: 0.22rem;
		flex: 1;
		min-width: 0;
		font: inherit;
		color: inherit;
		text-align: left;
		cursor: pointer;
	}

	.card-title {
		font-size: 1rem;
		font-weight: 800;
	}

	.card-summary {
		font-size: 0.86rem;
		font-weight: 600;
		color: var(--ink-soft);
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	label {
		display: grid;
		gap: 0.35rem;
		font-weight: 700;
		min-width: 0;
	}

	input {
		box-sizing: border-box;
		width: 100%;
		min-width: 0;
		border: 1px solid #cfd4de;
		border-radius: 10px;
		background: #fff;
		padding: 0.62rem 0.75rem;
		font: inherit;
		font-size: 0.95rem;
		color: var(--ink);
	}

	input:focus,
	.card-toggle:focus-visible {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.add-btn,
	.remove-btn {
		border: 0;
		border-radius: 999px;
		font: inherit;
		font-weight: 800;
		cursor: pointer;
	}

	.add-btn {
		justify-self: start;
		background: #0a3a8d;
		color: #fff;
		padding: 0.7rem 1rem;
	}

	.remove-btn {
		background: #f5e8e8;
		color: #9b1c1c;
		padding: 0.45rem 0.75rem;
	}
</style>