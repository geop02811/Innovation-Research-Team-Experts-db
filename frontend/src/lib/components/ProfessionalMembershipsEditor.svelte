<script lang="ts">
	import {
		membershipOrganizationName,
		membershipPeriod,
		membershipPositionHeld
	} from '$lib/auth/structured-profile-fields';
	import type { ProfessionalMembership } from '$lib/auth/types';

	interface Props {
		memberships: ProfessionalMembership[];
		organizationOptions: readonly string[];
		positionOptions: readonly string[];
		monthOptions: readonly string[];
		yearOptions: readonly string[];
		onchange: (memberships: ProfessionalMembership[]) => void;
	}

	let { memberships, organizationOptions, positionOptions, monthOptions, yearOptions, onchange }: Props = $props();
	let activeIndex = $state<number | null>(null);
	let editorElement: HTMLDivElement | undefined;

	const createMembership = (): ProfessionalMembership => ({
		organization: 'IEEE',
		customOrganization: '',
		positionHeld: 'Member',
		customPositionHeld: '',
		isCurrent: true,
		associatedWith: '',
		startMonth: '',
		startYear: '',
		endMonth: '',
		endYear: '',
		description: ''
	});

	const updateMembership = (index: number, patch: Partial<ProfessionalMembership>) => {
		onchange(
			memberships.map((membership, itemIndex) =>
				itemIndex === index ? { ...membership, ...patch } : membership
			)
		);
	};

	const removeMembership = (index: number) => {
		onchange(memberships.filter((_, itemIndex) => itemIndex !== index));
		if (activeIndex === index) activeIndex = null;
		else if (activeIndex !== null && activeIndex > index) activeIndex -= 1;
	};

	const addMembership = () => {
		const nextMemberships = [...memberships, createMembership()];
		onchange(nextMemberships);
		activeIndex = nextMemberships.length - 1;
	};

	const toggleCard = (index: number) => {
		activeIndex = activeIndex === index ? null : index;
	};

	const closeWhenClickOutside = (event: MouseEvent) => {
		if (!editorElement || !(event.target instanceof Node)) return;
		if (!editorElement.contains(event.target)) activeIndex = null;
	};

	const toggleCurrentMembership = (index: number) => {
		const isCurrent = !(memberships[index]?.isCurrent ?? true);
		updateMembership(index, {
			isCurrent,
			endMonth: isCurrent ? '' : memberships[index]?.endMonth,
			endYear: isCurrent ? '' : memberships[index]?.endYear
		});
	};

	const membershipSummary = (membership: ProfessionalMembership) =>
		[
			membershipOrganizationName(membership) || 'Organization not set',
			membershipPositionHeld(membership),
			membershipPeriod(membership)
		]
			.filter(Boolean)
			.join(' · ');
</script>

<svelte:window onclick={closeWhenClickOutside} />

<div class="cards-editor" bind:this={editorElement}>
	{#each memberships as membership, index}
		<div class="entry-card" class:collapsed={activeIndex !== index}>
			<div class="entry-card-header">
				<button
					type="button"
					class="card-toggle"
					aria-expanded={activeIndex === index}
					onclick={() => toggleCard(index)}
				>
					<span class="card-title">Professional membership {index + 1}</span>
					<span class="card-summary">{membershipSummary(membership)}</span>
				</button>
				<button type="button" class="remove-btn" onclick={() => removeMembership(index)}>Remove</button>
			</div>

			{#if activeIndex === index}
			<div class="field-grid two">
				<label>
					Membership / organization name
					<select
						value={membership.organization}
						onchange={(event) => updateMembership(index, { organization: event.currentTarget.value })}
					>
						{#each organizationOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
				<label>
					Position held
					<select
						value={membership.positionHeld}
						onchange={(event) => updateMembership(index, { positionHeld: event.currentTarget.value })}
					>
						{#each positionOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
			</div>

			{#if membership.organization === 'Other'}
				<label>
					Custom membership / organization name
					<input
						value={membership.customOrganization}
						placeholder="e.g. Zimbabwe Institution of Engineers"
						oninput={(event) => updateMembership(index, { customOrganization: event.currentTarget.value })}
					/>
				</label>
			{/if}

			{#if membership.positionHeld === 'Other'}
				<label>
					Custom position held
					<input
						value={membership.customPositionHeld}
						placeholder="e.g. Chartered Member"
						oninput={(event) => updateMembership(index, { customPositionHeld: event.currentTarget.value })}
					/>
				</label>
			{/if}

			<label>
				Associated with
				<input
					value={membership.associatedWith}
					placeholder="Optional, e.g. current employer or department"
					oninput={(event) => updateMembership(index, { associatedWith: event.currentTarget.value })}
				/>
			</label>

			<div class="field-grid two">
				<label>
					Start month <span class="optional-text">optional</span>
					<select
						value={membership.startMonth}
						onchange={(event) => updateMembership(index, { startMonth: event.currentTarget.value })}
					>
						<option value="">Month</option>
						{#each monthOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
				<label>
					Start year <span class="optional-text">optional</span>
					<select
						value={membership.startYear}
						onchange={(event) => updateMembership(index, { startYear: event.currentTarget.value })}
					>
						<option value="">Year</option>
						{#each yearOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
			</div>

			<button
				type="button"
				class="current-membership-toggle"
				class:active={membership.isCurrent}
				role="switch"
				aria-checked={membership.isCurrent}
				onclick={() => toggleCurrentMembership(index)}
			>
				<span class="toggle-track" aria-hidden="true">
					<span class="toggle-thumb"></span>
				</span>
				<span>This membership is current</span>
			</button>

			{#if membership.isCurrent}
				<p class="period-note">Dates are optional. If a start date is added, the end date is recorded as Present.</p>
			{:else}
				<div class="field-grid two">
					<label>
						End month <span class="optional-text">optional</span>
						<select
							value={membership.endMonth}
							onchange={(event) => updateMembership(index, { endMonth: event.currentTarget.value })}
						>
							<option value="">Month</option>
							{#each monthOptions as option}
								<option value={option}>{option}</option>
							{/each}
						</select>
					</label>
					<label>
						End year <span class="optional-text">optional</span>
						<select
							value={membership.endYear}
							onchange={(event) => updateMembership(index, { endYear: event.currentTarget.value })}
						>
							<option value="">Year</option>
							{#each yearOptions as option}
								<option value={option}>{option}</option>
							{/each}
						</select>
					</label>
				</div>
			{/if}

			<label>
				Description
				<textarea
					value={membership.description}
					rows="4"
					placeholder="Briefly describe your active involvement, responsibilities, or member benefits"
					oninput={(event) => updateMembership(index, { description: event.currentTarget.value })}
				></textarea>
			</label>
			{/if}
		</div>
	{/each}

	<button type="button" class="add-btn" onclick={addMembership}>+ Add professional membership</button>
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

	.entry-card-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
	}

	.entry-card.collapsed {
		gap: 0;
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

	.optional-text {
		font-size: 0.78rem;
		font-weight: 600;
		color: var(--ink-soft);
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
		min-height: 7rem;
	}

	input:focus,
	select:focus,
	textarea:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.current-membership-toggle {
		border: 0;
		background: transparent;
		padding: 0;
		display: flex;
		align-items: center;
		gap: 0.55rem;
		justify-self: start;
		font: inherit;
		font-weight: 700;
		color: var(--ink);
		cursor: pointer;
		text-align: left;
	}

	.toggle-track {
		width: 2.35rem;
		height: 1.35rem;
		border: 2px solid #0a3a8d;
		border-radius: 999px;
		background: #fff;
		padding: 0.12rem;
		display: inline-flex;
		align-items: center;
		transition:
			background 0.18s ease,
			border-color 0.18s ease;
	}

	.toggle-thumb {
		width: 0.85rem;
		height: 0.85rem;
		border-radius: 999px;
		background: #0a3a8d;
		transform: translateX(0);
		transition: transform 0.18s ease;
	}

	.current-membership-toggle.active .toggle-track {
		background: #0a3a8d;
	}

	.current-membership-toggle.active .toggle-thumb {
		background: #fff;
		transform: translateX(1rem);
	}

	.current-membership-toggle:focus-visible,
	.card-toggle:focus-visible {
		outline: none;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.16);
		border-radius: 8px;
	}

	.period-note {
		margin: -0.2rem 0 0;
		color: var(--ink-soft);
		font-size: 0.86rem;
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