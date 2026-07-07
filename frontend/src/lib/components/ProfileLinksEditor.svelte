<script lang="ts">
	import type { ProfileLink } from '$lib/auth/types';

	interface Props {
		links: ProfileLink[];
		typeOptions: readonly string[];
		onchange: (links: ProfileLink[]) => void;
	}

	let { links, typeOptions, onchange }: Props = $props();

	const createLink = (): ProfileLink => ({ type: 'LinkedIn', label: '', url: '' });

	const updateLink = (index: number, patch: Partial<ProfileLink>) => {
		onchange(links.map((link, itemIndex) => (itemIndex === index ? { ...link, ...patch } : link)));
	};

	const removeLink = (index: number) => {
		onchange(links.filter((_, itemIndex) => itemIndex !== index));
	};

	const addLink = () => {
		onchange([...links, createLink()]);
	};
</script>

<div class="links-editor">
	{#each links as link, index}
		<div class="link-card">
			<div class="link-card-header">
				<h3>Profile link {index + 1}</h3>
				<button type="button" class="remove-btn" onclick={() => removeLink(index)}>Remove</button>
			</div>

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
		</div>
	{/each}

	<button type="button" class="add-btn" onclick={addLink}>+ Add profile or publication link</button>
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

	.link-card-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
	}

	h3 {
		margin: 0;
		font-size: 1rem;
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

	@media (max-width: 700px) {
		.link-grid {
			grid-template-columns: 1fr;
		}
	}
</style>