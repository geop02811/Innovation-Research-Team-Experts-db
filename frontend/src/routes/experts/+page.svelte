<script lang="ts">
	import ScholarGrid from '$lib/components/scholars/ScholarGrid.svelte';
	import ScholarsHero from '$lib/components/scholars/ScholarsHero.svelte';
	import SelectFilter from '$lib/components/scholars/SelectFilter.svelte';
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import TagFilter from '$lib/components/scholars/TagFilter.svelte';
	import {
		areasOfExpertiseOptions,
		consultancyAvailabilityOptions,
		facultyOptions,
		getDepartmentOptionsByFaculty,
		geographicScopeOptions,
		highestQualificationOptions,
		industrialAreasOptions,
		languageOptions,
		preferredConsultancyTypeOptions,
		yearsOfConsultancyOptions
	} from '$lib/auth/form-options';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
	let searchValue = $state('');
	let appliedSearch = $state('');
	let showAdvancedFilters = $state(false);

	// Default visible filters
	let highestQualification = $state('');
	let faculty = $state('');
	let department = $state('');

	// Advanced filters
	let yearsOfExperience = $state('');
	let consultancyAvailability = $state<string[]>([]);
	let preferredConsultancyTypes = $state<string[]>([]);
	let geographicScope = $state<string[]>([]);
	let languagesSpoken = $state<string[]>([]);
	let areasOfExpertise = $state<string[]>([]);
	let industrialAreasOfExpertise = $state<string[]>([]);

	// Filter options (from shared source)
	const yearsOfExperienceOptions = yearsOfConsultancyOptions;
	const preferredConsultancyTypesOptions = preferredConsultancyTypeOptions;
	const languagesSpokenOptions = languageOptions;
	const industrialAreasOfExpertiseOptions = industrialAreasOptions;
	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(faculty));

	const onFacultyChange = (value: string) => {
		faculty = value;
		if (department && !getDepartmentOptionsByFaculty(value).includes(department)) {
			department = '';
		}
	};

	const filteredScholars = $derived(
		data.scholars.filter((scholar) => {
			const searchTarget = `${scholar.fullName} ${scholar.faculty || ''}`.toLowerCase();
			const searchMatches = appliedSearch === '' || searchTarget.includes(appliedSearch);

			const qualificationMatches =
				highestQualification === '' ||
				scholar.highestQualification === highestQualification;
			const facultyMatches = faculty === '' || scholar.faculty === faculty;
			const departmentMatches = department === '' || scholar.department === department;

			// Advanced filter matches
			const yearsMatches =
				yearsOfExperience === '' ||
				scholar.yearsOfConsultancyExperience === yearsOfExperience;

			const consultancyAvailabilityMatches =
				consultancyAvailability.length === 0 ||
				(scholar.consultancyAvailability || []).some((item) =>
					consultancyAvailability.includes(item)
				);

			const preferredTypesMatches =
				preferredConsultancyTypes.length === 0 ||
				(scholar.preferredConsultancyTypes || []).some((item) =>
					preferredConsultancyTypes.includes(item)
				);

			const geographicMatches =
				geographicScope.length === 0 ||
				(scholar.geographicScope || []).some((item) =>
					geographicScope.includes(item)
				);

			const languagesMatches =
				languagesSpoken.length === 0 ||
				(scholar.languagesSpoken || []).some((item) =>
					languagesSpoken.includes(item)
				);

			const areasMatches =
				areasOfExpertise.length === 0 ||
				(scholar.areasOfExpertise || []).some((item) =>
					areasOfExpertise.includes(item)
				);

			const industrialAreasMatches =
				industrialAreasOfExpertise.length === 0 ||
				(scholar.industrialAreasOfExpertise || []).some((item) =>
					industrialAreasOfExpertise.includes(item)
				);

			return (
				searchMatches &&
				qualificationMatches &&
				facultyMatches &&
				departmentMatches &&
				yearsMatches &&
				consultancyAvailabilityMatches &&
				preferredTypesMatches &&
				geographicMatches &&
				languagesMatches &&
				areasMatches &&
				industrialAreasMatches
			);
		})
	);

	const applyFilters = () => {
		appliedSearch = searchValue.trim().toLowerCase();
	};

	const clearFilters = () => {
		searchValue = '';
		appliedSearch = '';
		highestQualification = '';
		faculty = '';
		department = '';
		yearsOfExperience = '';
		consultancyAvailability = [];
		preferredConsultancyTypes = [];
		geographicScope = [];
		languagesSpoken = [];
		areasOfExpertise = [];
		industrialAreasOfExpertise = [];
	};

	const onExpertiseTagClick = (expertise: string) => {
		if (!areasOfExpertise.includes(expertise)) {
			areasOfExpertise = [...areasOfExpertise, expertise];
		}
		showAdvancedFilters = true;
	};
</script>

<svelte:head>
	<title>Experts | University of Zimbabwe experts</title>
	<meta
		name="description"
		content="Directory of University of Zimbabwe experts, research focus areas, and academic profiles."
	/>
</svelte:head>

<main class="page-shell">
	<ScholarsHero />

	<section class="filters-container" aria-label="Filter experts">
		<div class="search-bar">
			<input
				type="text"
				bind:value={searchValue}
				placeholder="Search by name or faculty"
				aria-label="Search by name or faculty"
			/>
			<button type="button" class="search-btn" onclick={applyFilters}>Search</button>
			<button type="button" class="clear-btn" onclick={clearFilters} aria-label="Clear filters">
				×
			</button>
		</div>

		<div class="default-filters">
			<SelectFilter
				label="Highest Qualification"
				value={highestQualification}
				options={highestQualificationOptions}
				onchange={(value) => (highestQualification = value)}
			/>
			<SelectFilter
				label="Faculty"
				value={faculty}
				options={facultyOptions}
				onchange={onFacultyChange}
			/>
			<SelectFilter
				label="Department"
				value={department}
				options={availableDepartmentOptions}
				onchange={(value) => (department = value)}
			/>
		</div>

		<button
			type="button"
			class="advanced-filters-btn"
			onclick={() => (showAdvancedFilters = !showAdvancedFilters)}
			aria-expanded={showAdvancedFilters}
		>
			{showAdvancedFilters ? '▼ Hide Advanced Filters' : '▶ Show Advanced Filters'}
		</button>

		{#if showAdvancedFilters}
			<div class="advanced-filters">
				<div class="filters-grid">
					<SelectFilter
						label="Years of Consultancy Experience"
						value={yearsOfExperience}
						options={yearsOfExperienceOptions}
						onchange={(value) => (yearsOfExperience = value)}
					/>
					<MultiSelectFilter
						label="Consultancy Availability"
						selected={consultancyAvailability}
						options={consultancyAvailabilityOptions}
						onchange={(value) => (consultancyAvailability = value)}
					/>
					<MultiSelectFilter
						label="Preferred Consultancy Types"
						selected={preferredConsultancyTypes}
						options={preferredConsultancyTypesOptions}
						onchange={(value) => (preferredConsultancyTypes = value)}
					/>
					<MultiSelectFilter
						label="Geographic Scope"
						selected={geographicScope}
						options={geographicScopeOptions}
						onchange={(value) => (geographicScope = value)}
					/>
					<MultiSelectFilter
						label="Languages Spoken"
						selected={languagesSpoken}
						options={languagesSpokenOptions}
						onchange={(value) => (languagesSpoken = value)}
					/>
				</div>

				<div class="tag-filters">
					<TagFilter
						label="Areas of Expertise"
						selected={areasOfExpertise}
						options={areasOfExpertiseOptions}
						onchange={(value) => (areasOfExpertise = value)}
					/>
					<TagFilter
						label="Industrial Areas of Expertise"
						selected={industrialAreasOfExpertise}
						options={industrialAreasOfExpertiseOptions}
						onchange={(value) => (industrialAreasOfExpertise = value)}
					/>
				</div>
			</div>
		{/if}
	</section>

	<ScholarGrid scholars={filteredScholars} onTagClick={onExpertiseTagClick} />
</main>

<style>
	.page-shell {
		margin: 0 auto;
	}

	.filters-container {
		padding: 2rem;
		padding-bottom: 3rem;
		margin-bottom: 3rem;
		background-color: #f9f9f9;
		border-bottom: 1px solid #e0e0e0;
	}

	.search-bar {
		display: flex;
		gap: 1rem;
		margin-bottom: 1.5rem;
		align-items: stretch;
		flex-wrap: wrap;
	}

	.search-bar input {
		flex: 1;
		min-width: 260px;
		padding: 0.75rem 1rem;
		border: 1px solid #ccc;
		border-radius: 4px;
		font-size: 1rem;
	}

	.search-bar input:focus {
		outline: none;
		border-color: #333;
		box-shadow: 0 0 0 2px rgba(51, 51, 51, 0.1);
	}

	.search-btn,
	.clear-btn {
		padding: 0.75rem 1.5rem;
		min-height: 48px;
		border: 1px solid #333;
		background-color: #333;
		color: white;
		border-radius: 4px;
		cursor: pointer;
		font-size: 0.875rem;
		font-weight: 500;
		transition: background-color 0.2s ease;
	}

	.search-btn:hover {
		background-color: #555;
	}

	.clear-btn {
		padding: 0.75rem 1rem;
		background-color: transparent;
		color: #666;
		border-color: #ccc;
		font-weight: bold;
		font-size: 1.25rem;
	}

	.clear-btn:hover {
		background-color: #f5f5f5;
	}

	.default-filters {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
		gap: 1.5rem;
		margin-bottom: 2rem;
	}

	.advanced-filters-btn {
		background: none;
		border: none;
		padding: 0.5rem 0;
		margin-bottom: 1rem;
		font-size: 0.95rem;
		font-weight: 500;
		color: #333;
		cursor: pointer;
		display: flex;
		align-items: center;
		gap: 0.5rem;
		transition: color 0.2s ease;
	}

	.advanced-filters-btn:hover {
		color: #555;
	}

	.advanced-filters {
		margin-top: 2rem;
		padding-top: 2rem;
		border-top: 1px solid #e0e0e0;
	}

	.filters-grid {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
		gap: 1.5rem;
		margin-bottom: 1.5rem;
	}

	.tag-filters {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
		gap: 2rem;
		margin-top: 1.5rem;
	}

	@media (max-width: 1024px) {
		.filters-container {
			padding: 1.4rem;
			padding-bottom: 2rem;
		}

		.default-filters,
		.filters-grid {
			grid-template-columns: repeat(2, minmax(0, 1fr));
		}
	}

	@media (max-width: 760px) {
		.filters-container {
			padding: 1rem;
			padding-bottom: 1.4rem;
			margin-bottom: 1.8rem;
		}

		.search-bar {
			gap: 0.65rem;
		}

		.search-bar input {
			min-width: 0;
			width: 100%;
		}

		.search-btn,
		.clear-btn {
			width: 100%;
		}

		.default-filters,
		.filters-grid,
		.tag-filters {
			grid-template-columns: 1fr;
			gap: 1rem;
		}
	}
</style>
