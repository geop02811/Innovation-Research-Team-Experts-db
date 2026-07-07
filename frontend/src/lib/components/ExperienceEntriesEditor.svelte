<script lang="ts">
	import type { ProfessionalExperience } from '$lib/auth/types';

	interface Props {
		experiences: ProfessionalExperience[];
		employmentTypeOptions: readonly string[];
		monthOptions: readonly string[];
		yearOptions: readonly string[];
		locationTypeOptions: readonly string[];
		onchange: (experiences: ProfessionalExperience[]) => void;
	}

	let {
		experiences,
		employmentTypeOptions,
		monthOptions,
		yearOptions,
		locationTypeOptions,
		onchange
	}: Props = $props();

	const createExperience = (): ProfessionalExperience => ({
		title: '',
		employmentType: '',
		organization: '',
		isCurrent: false,
		startMonth: '',
		startYear: '',
		endMonth: '',
		endYear: '',
		location: '',
		locationType: '',
		summary: ''
	});

	const updateExperience = (index: number, patch: Partial<ProfessionalExperience>) => {
		onchange(
			experiences.map((experience, itemIndex) =>
				itemIndex === index ? { ...experience, ...patch } : experience
			)
		);
	};

	const removeExperience = (index: number) => {
		onchange(experiences.filter((_, itemIndex) => itemIndex !== index));
	};

	const addExperience = () => {
		onchange([...experiences, createExperience()]);
	};
</script>

<div class="experiences-editor">
	{#each experiences as experience, index}
		<div class="experience-card">
			<div class="experience-card-header">
				<h3>Experience {index + 1}</h3>
				<button type="button" class="remove-btn" onclick={() => removeExperience(index)}>Remove</button>
			</div>

			<div class="field-grid two">
				<label>
					Title
					<input
						value={experience.title}
						placeholder="e.g. Retail Sales Manager"
						oninput={(event) => updateExperience(index, { title: event.currentTarget.value })}
					/>
				</label>
				<label>
					Employment type
					<select
						value={experience.employmentType}
						onchange={(event) => updateExperience(index, { employmentType: event.currentTarget.value })}
					>
						<option value="">Please select</option>
						{#each employmentTypeOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
			</div>

			<label>
				Company or organization
				<input
					value={experience.organization}
					placeholder="e.g. University of Zimbabwe"
					oninput={(event) => updateExperience(index, { organization: event.currentTarget.value })}
				/>
			</label>

			<label class="checkbox-row">
				<input
					type="checkbox"
					checked={experience.isCurrent}
					onchange={(event) =>
						updateExperience(index, {
							isCurrent: event.currentTarget.checked,
							endMonth: event.currentTarget.checked ? '' : experience.endMonth,
							endYear: event.currentTarget.checked ? '' : experience.endYear
						})}
				/>
				<span>I am currently working in this role</span>
			</label>

			<div class="field-grid two">
				<label>
					Start month
					<select
						value={experience.startMonth}
						onchange={(event) => updateExperience(index, { startMonth: event.currentTarget.value })}
					>
						<option value="">Month</option>
						{#each monthOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
				<label>
					Start year
					<select
						value={experience.startYear}
						onchange={(event) => updateExperience(index, { startYear: event.currentTarget.value })}
					>
						<option value="">Year</option>
						{#each yearOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
			</div>

			{#if !experience.isCurrent}
				<div class="field-grid two">
					<label>
						End month
						<select
							value={experience.endMonth}
							onchange={(event) => updateExperience(index, { endMonth: event.currentTarget.value })}
						>
							<option value="">Month</option>
							{#each monthOptions as option}
								<option value={option}>{option}</option>
							{/each}
						</select>
					</label>
					<label>
						End year
						<select
							value={experience.endYear}
							onchange={(event) => updateExperience(index, { endYear: event.currentTarget.value })}
						>
							<option value="">Year</option>
							{#each yearOptions as option}
								<option value={option}>{option}</option>
							{/each}
						</select>
					</label>
				</div>
			{/if}

			<div class="field-grid two">
				<label>
					Location
					<input
						value={experience.location}
						placeholder="e.g. Harare, Zimbabwe"
						oninput={(event) => updateExperience(index, { location: event.currentTarget.value })}
					/>
				</label>
				<label>
					Location type
					<select
						value={experience.locationType}
						onchange={(event) => updateExperience(index, { locationType: event.currentTarget.value })}
					>
						<option value="">Please select</option>
						{#each locationTypeOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
				</label>
			</div>

			<label>
				Experience summary
				<textarea
					value={experience.summary}
					rows="4"
					placeholder="Summarize what you did, delivered, researched, or consulted on"
					oninput={(event) => updateExperience(index, { summary: event.currentTarget.value })}
				></textarea>
			</label>
		</div>
	{/each}

	<button type="button" class="add-btn" onclick={addExperience}>+ Add experience</button>
</div>

<style>
	.experiences-editor {
		display: grid;
		gap: 1rem;
		min-width: 0;
	}

	.experience-card {
		display: grid;
		gap: 0.9rem;
		border: 1px solid #d8dee9;
		border-radius: 8px;
		padding: 1rem;
		background: #fff;
		min-width: 0;
	}

	.experience-card-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
	}

	h3 {
		margin: 0;
		font-size: 1rem;
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

	.checkbox-row {
		display: flex;
		align-items: center;
		gap: 0.55rem;
		font-weight: 700;
	}

	.checkbox-row input {
		width: 1.1rem;
		height: 1.1rem;
		accent-color: #0a3a8d;
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