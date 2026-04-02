<script lang="ts">
	interface Props {
		label: string;
		selected: string[];
		options: string[];
		onchange: (selected: string[]) => void;
	}

	let { label, selected, options, onchange }: Props = $props();

	const toggleTag = (tag: string) => {
		const newSelected = selected.includes(tag)
			? selected.filter((t) => t !== tag)
			: [...selected, tag];
		onchange(newSelected);
	};

	const removeTag = (tag: string) => {
		const newSelected = selected.filter((t) => t !== tag);
		onchange(newSelected);
	};
</script>

<div class="tag-filter">
	<div class="label-container"><span class="label-text">{label}</span></div>
	<div class="available-tags">
		{#each options as option}
			<button
				type="button"
				class="tag {selected.includes(option) ? 'selected' : ''}"
				onclick={() => toggleTag(option)}
			>
				{option}
				{#if selected.includes(option)}
					<span class="remove-icon">×</span>
				{/if}
			</button>
		{/each}
	</div>
	{#if selected.length > 0}
		<div class="selected-tags">
			<p class="selected-label">Selected:</p>
			<div class="tags-list">
				{#each selected as tag}
					<div class="selected-tag">
						<span>{tag}</span>
						<button type="button" class="remove-btn" onclick={() => removeTag(tag)}>×</button>
					</div>
				{/each}
			</div>
		</div>
	{/if}
</div>

<style>
	.tag-filter {
		display: contents;
	}

	.label-container {
		margin-bottom: 0.5rem;
		grid-column: 1 / -1;
	}

	.label-text {
		font-weight: 500;
		font-size: 0.875rem;
	}

	.available-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
		align-items: center;
		width: 100%;
		grid-column: 1 / -1;
	}

	.tag {
		flex: 0 0 fit-content;
		width: fit-content;
		max-width: none;
		min-width: 0;
		padding: 0.5rem 1rem;
		border: 1px solid #d0d0d0;
		border-radius: 20px;
		background-color: #f5f5f5;
		font-size: 0.875rem;
		white-space: nowrap;
		cursor: pointer;
		transition: all 0.2s ease;
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		line-height: 1.2;
		vertical-align: top;
		margin: 0;
		border: none;
		font-family: inherit;
	}

	.tag:hover {
		border-color: #999;
		background-color: #efefef;
	}

	.tag.selected {
		background-color: #333;
		color: white;
		border-color: #333;
	}

	.tag.selected:hover {
		background-color: #555;
		border-color: #555;
	}

	.remove-icon {
		font-weight: bold;
		font-size: 1.2rem;
		margin-left: 0.25rem;
	}

	.selected-tags {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
		margin-top: 0.5rem;
		padding-top: 0.75rem;
		border-top: 1px solid #eee;
		grid-column: 1 / -1;
	}

	.selected-label {
		font-size: 0.75rem;
		font-weight: 600;
		color: #666;
		margin: 0;
	}

	.tags-list {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
		align-items: center;
	}

	.selected-tag {
		flex: 0 0 auto;
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.4rem 0.75rem;
		background-color: #333;
		color: white;
		border-radius: 16px;
		font-size: 0.8rem;
		white-space: nowrap;
		line-height: 1.2;
	}

	.remove-btn {
		background: none;
		border: none;
		color: white;
		cursor: pointer;
		font-size: 1.1rem;
		padding: 0;
		display: flex;
		align-items: center;
		justify-content: center;
		line-height: 1;
	}

	.remove-btn:hover {
		opacity: 0.8;
	}
</style>
