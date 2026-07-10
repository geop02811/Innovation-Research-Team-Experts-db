<script lang="ts">
	import type { ComplianceCredential } from '$lib/auth/types';

	interface Props {
		credentials: ComplianceCredential[];
		monthOptions: readonly string[];
		yearOptions: readonly string[];
		onchange: (credentials: ComplianceCredential[]) => void;
	}

	let { credentials, monthOptions, yearOptions, onchange }: Props = $props();
	let activeIndex = $state<number | null>(null);
	let editorElement: HTMLDivElement | undefined;

	const createCredential = (): ComplianceCredential => ({
		name: '',
		issuingOrganization: '',
		issueMonth: '',
		issueYear: '',
		credentialUrl: '',
		skillsAssociated: ''
	});

	const updateCredential = (index: number, patch: Partial<ComplianceCredential>) => {
		onchange(
			credentials.map((credential, itemIndex) =>
				itemIndex === index ? { ...credential, ...patch } : credential
			)
		);
	};

	const removeCredential = (index: number) => {
		onchange(credentials.filter((_, itemIndex) => itemIndex !== index));
		if (activeIndex === index) activeIndex = null;
		else if (activeIndex !== null && activeIndex > index) activeIndex -= 1;
	};

	const addCredential = () => {
		const nextCredentials = [...credentials, createCredential()];
		onchange(nextCredentials);
		activeIndex = nextCredentials.length - 1;
	};

	const toggleCard = (index: number) => {
		activeIndex = activeIndex === index ? null : index;
	};

	const closeWhenClickOutside = (event: MouseEvent) => {
		if (!editorElement || !(event.target instanceof Node)) return;
		if (!editorElement.contains(event.target)) activeIndex = null;
	};

	const issueDate = (credential: ComplianceCredential) =>
		[credential.issueMonth, credential.issueYear].filter(Boolean).join(' ');

	const credentialSummary = (credential: ComplianceCredential) =>
		[credential.name || 'Compliance credential', credential.issuingOrganization, issueDate(credential)]
			.filter(Boolean)
			.join(' · ');
</script>

<svelte:window onclick={closeWhenClickOutside} />

<div class="cards-editor" bind:this={editorElement}>
	{#each credentials as credential, index}
		<div class="entry-card" class:collapsed={activeIndex !== index}>
			<div class="entry-card-header">
				<button
					type="button"
					class="card-toggle"
					aria-expanded={activeIndex === index}
					onclick={() => toggleCard(index)}
				>
					<span class="card-title">Compliance credential {index + 1}</span>
					<span class="card-summary">{credentialSummary(credential)}</span>
				</button>
				<button type="button" class="remove-btn" onclick={() => removeCredential(index)}>Remove</button>
			</div>

			{#if activeIndex === index}
			<label>
				Name
				<input
					value={credential.name}
					placeholder="e.g. Artificial Intelligence Governance Professional (AIGP)"
					oninput={(event) => updateCredential(index, { name: event.currentTarget.value })}
				/>
			</label>

			<label>
				Issuing organization
				<input
					value={credential.issuingOrganization}
					placeholder="e.g. International Association of Privacy Professionals (IAPP)"
					oninput={(event) => updateCredential(index, { issuingOrganization: event.currentTarget.value })}
				/>
			</label>

			<div class="field-grid two">
				<label>
					Issue month
					<select
						value={credential.issueMonth}
						onchange={(event) => updateCredential(index, { issueMonth: event.currentTarget.value })}
					>
						<option value="">Month</option>
						{#each monthOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
				<label>
					Issue year
					<select
						value={credential.issueYear}
						onchange={(event) => updateCredential(index, { issueYear: event.currentTarget.value })}
					>
						<option value="">Year</option>
						{#each yearOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
			</div>

			<label>
				Credential ID / URL
				<input
					value={credential.credentialUrl}
					placeholder="https://..."
					oninput={(event) => updateCredential(index, { credentialUrl: event.currentTarget.value })}
				/>
			</label>

			<label>
				Skills associated
				<textarea
					value={credential.skillsAssociated}
					rows="3"
					placeholder="e.g. AI Governance, Risk Assessment, Regulatory Compliance"
					oninput={(event) => updateCredential(index, { skillsAssociated: event.currentTarget.value })}
				></textarea>
			</label>
			{/if}
		</div>
	{/each}

	<button type="button" class="add-btn" onclick={addCredential}>+ Add compliance credential</button>
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

	.field-grid {
		display: grid;
		gap: 0.85rem;
	}

	.field-grid.two {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}

	label {
		display: grid;
		gap: 0.35rem;
		font-weight: 700;
		min-width: 0;
	}

	input,
	select,
	textarea {
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

	textarea {
		resize: vertical;
		min-height: 6rem;
	}

	input:focus,
	select:focus,
	textarea:focus {
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

	@media (max-width: 700px) {
		.field-grid.two {
			grid-template-columns: 1fr;
		}
	}
</style>