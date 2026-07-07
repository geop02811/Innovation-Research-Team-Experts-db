<script lang="ts">
	import type { Scholar } from '$lib/types/scholar';
	import {
		facultyOptions,
		getDepartmentOptionsByFaculty,
		highestQualificationOptions,
		yearsOfConsultancyOptions,
		consultancyAvailabilityOptions,
		preferredConsultancyTypeOptions,
		geographicScopeOptions,
		languageOptions,
		areasOfExpertiseOptions,
		industrialAreasOptions
	} from '$lib/auth/form-options';

	interface Props {
		allScholars: Scholar[];
		faculty: string;
		department: string;
		highestQualification: string;
		yearsOfExperience: string;
		consultancyAvailability: string[];
		preferredConsultancyTypes: string[];
		geographicScope: string[];
		languagesSpoken: string[];
		areasOfExpertise: string[];
		industrialAreasOfExpertise: string[];
		onFacultyChange: (v: string) => void;
		onDepartmentChange: (v: string) => void;
		onQualificationChange: (v: string) => void;
		onYearsChange: (v: string) => void;
		onConsultancyAvailabilityChange: (v: string[]) => void;
		onPreferredTypesChange: (v: string[]) => void;
		onGeographicScopeChange: (v: string[]) => void;
		onLanguagesChange: (v: string[]) => void;
		onAreasChange: (v: string[]) => void;
		onIndustrialAreasChange: (v: string[]) => void;
		onReset: () => void;
	}

	let {
		allScholars,
		faculty,
		department,
		highestQualification,
		yearsOfExperience,
		consultancyAvailability,
		preferredConsultancyTypes,
		geographicScope,
		languagesSpoken,
		areasOfExpertise,
		industrialAreasOfExpertise,
		onFacultyChange,
		onDepartmentChange,
		onQualificationChange,
		onYearsChange,
		onConsultancyAvailabilityChange,
		onPreferredTypesChange,
		onGeographicScopeChange,
		onLanguagesChange,
		onAreasChange,
		onIndustrialAreasChange,
		onReset
	}: Props = $props();

	let openSections = $state<Record<string, boolean>>({
		availability: true,
		faculty: true,
		qualification: false,
		experience: false,
		consultancyType: false,
		geographic: false,
		languages: false,
		expertise: true,
		industrial: false
	});

	const toggle = (section: string) => {
		openSections[section] = !openSections[section];
	};

	type StringArrayScholarField =
		| 'consultancyAvailability'
		| 'preferredConsultancyTypes'
		| 'geographicScope'
		| 'languagesSpoken'
		| 'areasOfExpertise'
		| 'industrialAreasOfExpertise';

	// Count helpers — how many scholars have this value
	const countArray = (field: StringArrayScholarField, value: string): number =>
		allScholars.filter((s) => {
			const v = s[field];
			return Array.isArray(v) ? v.includes(value) : v === value;
		}).length;

	const countSingle = (field: keyof Scholar, value: string): number =>
		allScholars.filter((s) => s[field] === value).length;

	// Search inside long lists
	let expertiseSearch = $state('');
	let industrialSearch = $state('');

	const filteredExpertiseOptions = $derived(
		expertiseSearch
			? (areasOfExpertiseOptions as readonly string[]).filter((o) =>
					o.toLowerCase().includes(expertiseSearch.toLowerCase())
				)
			: (areasOfExpertiseOptions as readonly string[])
	);

	const filteredIndustrialOptions = $derived(
		industrialSearch
			? (industrialAreasOptions as readonly string[]).filter((o) =>
					o.toLowerCase().includes(industrialSearch.toLowerCase())
				)
			: (industrialAreasOptions as readonly string[])
	);

	const availableDepts = $derived(getDepartmentOptionsByFaculty(faculty));

	const toggleMulti = (list: string[], item: string, onChange: (v: string[]) => void) => {
		const next = list.includes(item) ? list.filter((x) => x !== item) : [...list, item];
		onChange(next);
	};

	const hasActiveFilters = $derived(
		!!(
			faculty ||
			department ||
			highestQualification ||
			yearsOfExperience ||
			consultancyAvailability.length > 0 ||
			preferredConsultancyTypes.length > 0 ||
			geographicScope.length > 0 ||
			languagesSpoken.length > 0 ||
			areasOfExpertise.length > 0 ||
			industrialAreasOfExpertise.length > 0
		)
	);
</script>

<div class="filter-panel">
	<!-- Header -->
	<div class="filter-header">
		<h2 class="filter-title">Filter</h2>
		{#if hasActiveFilters}
			<button type="button" class="reset-btn" onclick={onReset}>Reset</button>
		{/if}
	</div>

	<!-- Availability -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('availability')}
			aria-expanded={openSections.availability}
		>
			<span>Availability</span>
			<span class="chevron" class:open={openSections.availability}>&#8963;</span>
		</button>
		{#if openSections.availability}
			<div class="accordion-body">
				{#each consultancyAvailabilityOptions as option}
					{@const count = countArray('consultancyAvailability', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="checkbox"
							checked={consultancyAvailability.includes(option)}
							onchange={() =>
								toggleMulti(consultancyAvailability, option, onConsultancyAvailabilityChange)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Faculty -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('faculty')}
			aria-expanded={openSections.faculty}
		>
			<span>Faculty / Institute and Units</span>
			<span class="chevron" class:open={openSections.faculty}>&#8963;</span>
		</button>
		{#if openSections.faculty}
			<div class="accordion-body">
				<select
					class="section-select"
					value={faculty}
					onchange={(e) => onFacultyChange(e.currentTarget.value)}
				>
					<option value="">All Faculties</option>
					{#each facultyOptions as opt}
						{@const count = countSingle('faculty', opt)}
						<option value={opt}>{opt}{count > 0 ? ` (${count})` : ''}</option>
					{/each}
				</select>
				{#if faculty}
					<select
						class="section-select"
						style="margin-top: 0.5rem;"
						value={department}
						onchange={(e) => onDepartmentChange(e.currentTarget.value)}
					>
						<option value="">All Departments / Institutes / Units</option>
						{#each availableDepts as dept}
							<option value={dept}>{dept}</option>
						{/each}
					</select>
				{/if}
			</div>
		{/if}
	</div>

	<!-- Qualification -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('qualification')}
			aria-expanded={openSections.qualification}
		>
			<span>Highest Qualification</span>
			<span class="chevron" class:open={openSections.qualification}>&#8963;</span>
		</button>
		{#if openSections.qualification}
			<div class="accordion-body">
				{#each highestQualificationOptions as option}
					{@const count = countSingle('highestQualification', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="radio"
							name="qualification"
							value={option}
							checked={highestQualification === option}
							onchange={() => onQualificationChange(highestQualification === option ? '' : option)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Years of Experience -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('experience')}
			aria-expanded={openSections.experience}
		>
			<span>Years of Experience</span>
			<span class="chevron" class:open={openSections.experience}>&#8963;</span>
		</button>
		{#if openSections.experience}
			<div class="accordion-body">
				{#each yearsOfConsultancyOptions as option}
					{@const count = countSingle('yearsOfConsultancyExperience', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="radio"
							name="years"
							value={option}
							checked={yearsOfExperience === option}
							onchange={() => onYearsChange(yearsOfExperience === option ? '' : option)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Consultancy Type -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('consultancyType')}
			aria-expanded={openSections.consultancyType}
		>
			<span>Consultancy Type</span>
			<span class="chevron" class:open={openSections.consultancyType}>&#8963;</span>
		</button>
		{#if openSections.consultancyType}
			<div class="accordion-body">
				{#each preferredConsultancyTypeOptions as option}
					{@const count = countArray('preferredConsultancyTypes', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="checkbox"
							checked={preferredConsultancyTypes.includes(option)}
							onchange={() =>
								toggleMulti(preferredConsultancyTypes, option, onPreferredTypesChange)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Geographic Scope -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('geographic')}
			aria-expanded={openSections.geographic}
		>
			<span>Geographic Scope</span>
			<span class="chevron" class:open={openSections.geographic}>&#8963;</span>
		</button>
		{#if openSections.geographic}
			<div class="accordion-body">
				{#each geographicScopeOptions as option}
					{@const count = countArray('geographicScope', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="checkbox"
							checked={geographicScope.includes(option)}
							onchange={() => toggleMulti(geographicScope, option, onGeographicScopeChange)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Languages -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('languages')}
			aria-expanded={openSections.languages}
		>
			<span>Languages</span>
			<span class="chevron" class:open={openSections.languages}>&#8963;</span>
		</button>
		{#if openSections.languages}
			<div class="accordion-body">
				{#each languageOptions as option}
					{@const count = countArray('languagesSpoken', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="checkbox"
							checked={languagesSpoken.includes(option)}
							onchange={() => toggleMulti(languagesSpoken, option, onLanguagesChange)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Research Expertise -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('expertise')}
			aria-expanded={openSections.expertise}
		>
			<span>Research Expertise</span>
			<span class="chevron" class:open={openSections.expertise}>&#8963;</span>
		</button>
		{#if openSections.expertise}
			<div class="accordion-body">
				<div class="section-search-wrap">
					<input
						class="section-search"
						type="text"
						placeholder="Search..."
						bind:value={expertiseSearch}
						aria-label="Search research expertise"
					/>
					<svg class="search-icon" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
						<circle cx="8.5" cy="8.5" r="5.5" />
						<line x1="13" y1="13" x2="18" y2="18" />
					</svg>
				</div>
				{#each filteredExpertiseOptions as option}
					{@const count = countArray('areasOfExpertise', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="checkbox"
							checked={areasOfExpertise.includes(option)}
							onchange={() => toggleMulti(areasOfExpertise, option, onAreasChange)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>

	<!-- Industrial Areas -->
	<div class="accordion-section">
		<button
			type="button"
			class="accordion-header"
			onclick={() => toggle('industrial')}
			aria-expanded={openSections.industrial}
		>
			<span>Industrial Areas</span>
			<span class="chevron" class:open={openSections.industrial}>&#8963;</span>
		</button>
		{#if openSections.industrial}
			<div class="accordion-body">
				<div class="section-search-wrap">
					<input
						class="section-search"
						type="text"
						placeholder="Search..."
						bind:value={industrialSearch}
						aria-label="Search industrial areas"
					/>
					<svg class="search-icon" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
						<circle cx="8.5" cy="8.5" r="5.5" />
						<line x1="13" y1="13" x2="18" y2="18" />
					</svg>
				</div>
				{#each filteredIndustrialOptions as option}
					{@const count = countArray('industrialAreasOfExpertise', option)}
					<label class="checkbox-row" class:dimmed={count === 0}>
						<input
							type="checkbox"
							checked={industrialAreasOfExpertise.includes(option)}
							onchange={() =>
								toggleMulti(industrialAreasOfExpertise, option, onIndustrialAreasChange)}
						/>
						<span class="option-label">{option}</span>
						{#if count > 0}<span class="count">({count})</span>{/if}
					</label>
				{/each}
			</div>
		{/if}
	</div>
</div>

<style>
	.filter-panel {
		background: #fff;
		border: 1px solid #e0e0e8;
		border-radius: 8px;
		overflow: hidden;
	}

	/* ── Header ── */
	.filter-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 1.1rem 1.25rem 1rem;
		border-bottom: 1px solid #e8e8f0;
	}

	.filter-title {
		margin: 0;
		font-size: 1.1rem;
		font-weight: 700;
		color: var(--uz-navy);
		letter-spacing: 0.01em;
	}

	.reset-btn {
		background: none;
		border: none;
		font-size: 0.875rem;
		font-weight: 600;
		color: var(--uz-orange);
		cursor: pointer;
		padding: 0;
		text-decoration: underline;
		text-underline-offset: 2px;
	}

	.reset-btn:hover {
		color: var(--uz-orange-dark);
	}

	/* ── Accordion ── */
	.accordion-section {
		border-bottom: 1px solid #e8e8f0;
	}

	.accordion-section:last-child {
		border-bottom: none;
	}

	.accordion-header {
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0.85rem 1.25rem;
		background: none;
		border: none;
		cursor: pointer;
		font-size: 0.9rem;
		font-weight: 600;
		color: var(--ink);
		text-align: left;
		transition: background 0.15s;
	}

	.accordion-header:hover {
		background: #f7f7fb;
	}

	.chevron {
		font-size: 0.85rem;
		color: #888;
		transition: transform 0.2s;
		display: inline-block;
		transform: rotate(180deg); /* points down when closed */
		line-height: 1;
	}

	.chevron.open {
		transform: rotate(0deg); /* points up when open */
	}

	.accordion-body {
		padding: 0.25rem 1.25rem 1rem;
		display: flex;
		flex-direction: column;
		gap: 0.1rem;
	}

	/* ── Checkboxes / Radios ── */
	.checkbox-row {
		display: flex;
		align-items: center;
		gap: 0.6rem;
		padding: 0.35rem 0;
		cursor: pointer;
		font-size: 0.875rem;
		color: var(--ink);
	}

	.checkbox-row.dimmed {
		opacity: 0.45;
	}

	.checkbox-row input[type='checkbox'],
	.checkbox-row input[type='radio'] {
		width: 16px;
		height: 16px;
		flex-shrink: 0;
		accent-color: var(--uz-navy);
		cursor: pointer;
	}

	.option-label {
		flex: 1;
	}

	.count {
		font-size: 0.8rem;
		color: #888;
		margin-left: auto;
		flex-shrink: 0;
	}

	/* ── Select dropdown ── */
	.section-select {
		width: 100%;
		padding: 0.5rem 0.75rem;
		border: 1px solid #d0d0da;
		border-radius: 6px;
		font-size: 0.875rem;
		color: var(--ink);
		background: #fff;
		cursor: pointer;
		appearance: auto;
	}

	.section-select:focus {
		outline: none;
		border-color: var(--uz-navy);
		box-shadow: 0 0 0 2px rgba(27, 43, 78, 0.1);
	}

	/* ── Section search ── */
	.section-search-wrap {
		position: relative;
		margin-bottom: 0.5rem;
	}

	.section-search {
		width: 100%;
		padding: 0.45rem 2rem 0.45rem 0.75rem;
		border: 1px solid #d0d0da;
		border-radius: 6px;
		font-size: 0.85rem;
		color: var(--ink);
		background: #f9f9fb;
	}

	.section-search:focus {
		outline: none;
		border-color: var(--uz-navy);
		background: #fff;
	}

	.search-icon {
		position: absolute;
		right: 0.55rem;
		top: 50%;
		transform: translateY(-50%);
		width: 14px;
		height: 14px;
		color: #aaa;
		pointer-events: none;
	}
</style>
