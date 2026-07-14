<script lang="ts">
	import type { ProfileLink } from '$lib/auth/types';

	interface Props {
		links: ProfileLink[];
		typeOptions: readonly string[];
		onchange: (links: ProfileLink[]) => void;
		error?: string;
		onfocusout?: () => void;
	}

	let { links, typeOptions, onchange, error = '', onfocusout }: Props = $props();
	let activeIndex = $state<number | null>(null);
	let editorElement: HTMLDivElement | undefined;

	const createLink = (): ProfileLink => ({ type: 'LinkedIn', label: '', url: '' });

	const updateLink = (index: number, patch: Partial<ProfileLink>) => {
		onchange(links.map((link, itemIndex) => (itemIndex === index ? { ...link, ...patch } : link)));
	};

	const removeLink = (index: number) => {
		onchange(links.filter((_, itemIndex) => itemIndex !== index));
		if (activeIndex === index) activeIndex = null;
		else if (activeIndex !== null && activeIndex > index) activeIndex -= 1;
	};

	const addLink = () => {
		const nextLinks = [...links, createLink()];
		onchange(nextLinks);
		activeIndex = nextLinks.length - 1;
	};

	const toggleCard = (index: number) => {
		activeIndex = activeIndex === index ? null : index;
	};

	const closeWhenClickOutside = (event: MouseEvent) => {
		if (!editorElement || !(event.target instanceof Node)) return;
		if (!editorElement.contains(event.target)) activeIndex = null;
	};

	const closeWhenFocusLeaves = (event: FocusEvent) => {
		if (event.relatedTarget instanceof Node && editorElement?.contains(event.relatedTarget)) return;
		onfocusout?.();
	};

	const linkSummary = (link: ProfileLink) => [link.label || link.type, link.url].filter(Boolean).join(' · ');
</script>

<svelte:window onclick={closeWhenClickOutside} />

<div class="links-editor" bind:this={editorElement} onfocusout={closeWhenFocusLeaves}>
	{#each links as link, index}
		<div class="link-card" class:collapsed={activeIndex !== index}>
			<div class="link-card-header">
				<button
					type="button"
					class="card-toggle"
					aria-expanded={activeIndex === index}
					onclick={() => toggleCard(index)}
				>
					<span class="card-title">Profile link {index + 1}</span>
					<span class="card-summary">{linkSummary(link)}</span>
				</button>
				<button type="button" class="remove-btn" onclick={() => removeLink(index)}>Remove</button>
			</div>

			{#if activeIndex === index}
			<div class="link-grid">
				<label>
					Platform
					<select
						value={link.type}
						onchange={(event) => updateLink(index, { type: event.currentTarget.value })}
					>
						{#each typeOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>

				<label>
					Display label
					<input
						value={link.label}
						placeholder={link.type === 'Other' ? 'e.g. Semantic Scholar' : link.type}
						oninput={(event) => updateLink(index, { label: event.currentTarget.value })}
					/>
				</label>
			</div>

			<label>
				URL
				<input
					value={link.url}
					placeholder="https://..."
					oninput={(event) => updateLink(index, { url: event.currentTarget.value })}
				/>
			</label>
			{/if}
		</div>
	{/each}

	<button type="button" class="add-btn" onclick={addLink}>+ Add profile or publication link</button>

	{#if error}
		<p class="field-error" role="alert">{error}</p>
	{/if}
</div>

<style>
	.links-editor {
		display: grid;
		gap: 1rem;
		min-width: 0;
	}

	.link-card {
		display: grid;
		gap: 0.85rem;
		border: 1px solid #d8dee9;
		border-radius: 8px;
		padding: 1rem;
		background: #fff;
		min-width: 0;
	}

	.link-card.collapsed {
		gap: 0;
	}

	.link-card-header {
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
		margin: 0;
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

	.link-grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 0.85rem;
	}

	label {
		display: grid;
		gap: 0.35rem;
		font-weight: 700;
		min-width: 0;
	}

	input,
	select {
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
	select:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.card-toggle:focus-visible {
		outline: none;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.16);
		border-radius: 8px;
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

	.field-error {
		font-size: 0.84rem;
		font-weight: 600;
		color: #b42318;
		line-height: 1.35;
	}

	@media (max-width: 700px) {
		.link-grid {
			grid-template-columns: 1fr;
		}
	}
</style>