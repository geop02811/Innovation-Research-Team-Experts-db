<script lang="ts">
	interface Props {
		label: string;
		selected: string[];
		options: string[];
		onchange: (selected: string[]) => void;
	}

	let { label, selected, options, onchange }: Props = $props();
	let customTag = $state('');

	const normalizeTag = (tag: string) => tag.trim().replace(/\s+/g, ' ');
	const tagKey = (tag: string) => normalizeTag(tag).toLowerCase();
	const uniqueTags = (tags: readonly string[]) => {
		const seen = new Set<string>();

		return tags.map(normalizeTag).filter((tag) => {
			if (!tag) return false;
			const key = tagKey(tag);
			if (seen.has(key)) return false;
			seen.add(key);
			return true;
		});
	};

	const visibleOptions = $derived(uniqueTags([...options, ...selected]));
	const normalizedCustomTag = $derived(normalizeTag(customTag));
	const isSelected = (tag: string) => selected.some((item) => tagKey(item) === tagKey(tag));

	const toggleTag = (tag: string) => {
		const selectedKey = tagKey(tag);
		const newSelected = isSelected(tag)
			? selected.filter((item) => tagKey(item) !== selectedKey)
			: [...selected, tag];
		onchange(newSelected);
	};

	const removeTag = (tag: string) => {
		const selectedKey = tagKey(tag);
		const newSelected = selected.filter((item) => tagKey(item) !== selectedKey);
		onchange(newSelected);
	};

	const addCustomTag = () => {
		if (!normalizedCustomTag) return;
		if (!isSelected(normalizedCustomTag)) {
			onchange([...selected, normalizedCustomTag]);
		}
		customTag = '';
	};
</script>

<div class="tag-filter">
	<div class="label-container"><span class="label-text">{label}</span></div>
	<div class="available-tags">
		{#each visibleOptions as option}
			<button
				type="button"
				class="tag"
				class:selected={isSelected(option)}
				onclick={() => toggleTag(option)}
			>
				{option}
				{#if isSelected(option)}
					<span class="remove-icon">×</span>
				{/if}
			</button>
		{/each}
	</div>
	<div class="custom-tag-entry">
		<input
			class="custom-tag-input"
			bind:value={customTag}
			placeholder={`Add custom ${label.toLowerCase()}`}
			aria-label={`Add custom ${label}`}
			onkeydown={(event) => {
				if (event.key === 'Enter') {
					event.preventDefault();
					addCustomTag();
				}
			}}
		/>
		<button
			type="button"
			class="custom-tag-add"
			disabled={!normalizedCustomTag}
			onclick={addCustomTag}
		>
			Add
		</button>
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
		min-width: 0;
		grid-column: 1 / -1;
	}

	.tag {
		flex: 0 1 auto;
		max-width: 100%;
		min-width: 0;
		padding: 0.5rem 1rem;
		border: 1px solid #d0d0d0;
		border-radius: 20px;
		background-color: #f5f5f5;
		font-size: 0.875rem;
		white-space: normal;
		overflow-wrap: anywhere;
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

	.custom-tag-entry {
		display: grid;
		grid-template-columns: minmax(0, 1fr) auto;
		gap: 0.5rem;
		align-items: center;
		width: 100%;
		min-width: 0;
		grid-column: 1 / -1;
		margin-top: 0.25rem;
	}

	.custom-tag-input {
		box-sizing: border-box;
		width: 100%;
		min-width: 0;
		border: 1px dashed #cfd4de;
		border-radius: 999px;
		background: #fff;
		padding: 0.55rem 1rem;
		font: inherit;
		font-size: 0.875rem;
		font-weight: 500;
		color: var(--ink);
	}

	.custom-tag-input:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.custom-tag-add {
		border: 0;
		border-radius: 999px;
		background: #0a3a8d;
		color: #fff;
		cursor: pointer;
		font: inherit;
		font-size: 0.875rem;
		font-weight: 700;
		padding: 0.58rem 1rem;
		white-space: nowrap;
	}

	.custom-tag-add:disabled {
		cursor: not-allowed;
		opacity: 0.55;
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
		min-width: 0;
	}

	.selected-tag {
		flex: 0 1 auto;
		max-width: 100%;
		min-width: 0;
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.4rem 0.75rem;
		background-color: #333;
		color: white;
		border-radius: 16px;
		font-size: 0.8rem;
		white-space: normal;
		overflow-wrap: anywhere;
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

	@media (max-width: 420px) {
		.custom-tag-entry {
			grid-template-columns: 1fr;
		}

		.custom-tag-add {
			width: 100%;
		}
	}
</style>
