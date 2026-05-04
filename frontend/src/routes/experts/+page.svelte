<script lang="ts">
	import ScholarGrid from '$lib/components/scholars/ScholarGrid.svelte';
	import ScholarsHero from '$lib/components/scholars/ScholarsHero.svelte';
	import FilterPanel from '$lib/components/scholars/FilterPanel.svelte';
	import { getDepartmentOptionsByFaculty } from '$lib/auth/form-options';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
	let searchValue = $state('');
	let appliedSearch = $state('');
	let mobileFiltersOpen = $state(false);

	let highestQualification = $state('');
	let faculty = $state('');
	let department = $state('');
	let yearsOfExperience = $state('');
	let consultancyAvailability = $state<string[]>([]);
	let preferredConsultancyTypes = $state<string[]>([]);
	let geographicScope = $state<string[]>([]);
	let languagesSpoken = $state<string[]>([]);
	let areasOfExpertise = $state<string[]>([]);
	let industrialAreasOfExpertise = $state<string[]>([]);

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

	<div class="experts-layout">
		<!-- Sidebar filter (desktop) -->
		<aside class="filter-sidebar">
			<FilterPanel
				allScholars={data.scholars}
				{faculty}
				{department}
				{highestQualification}
				{yearsOfExperience}
				{consultancyAvailability}
				{preferredConsultancyTypes}
				{geographicScope}
				{languagesSpoken}
				{areasOfExpertise}
				{industrialAreasOfExpertise}
				onFacultyChange={onFacultyChange}
				onDepartmentChange={(v) => (department = v)}
				onQualificationChange={(v) => (highestQualification = v)}
				onYearsChange={(v) => (yearsOfExperience = v)}
				onConsultancyAvailabilityChange={(v) => (consultancyAvailability = v)}
				onPreferredTypesChange={(v) => (preferredConsultancyTypes = v)}
				onGeographicScopeChange={(v) => (geographicScope = v)}
				onLanguagesChange={(v) => (languagesSpoken = v)}
				onAreasChange={(v) => (areasOfExpertise = v)}
				onIndustrialAreasChange={(v) => (industrialAreasOfExpertise = v)}
				onReset={clearFilters}
			/>
		</aside>

		<!-- Main content -->
		<div class="experts-main">
			<!-- Search bar -->
			<div class="search-bar">
				<div class="search-input-wrap">
					<svg class="search-bar-icon" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
						<circle cx="8.5" cy="8.5" r="5.5" />
						<line x1="13" y1="13" x2="18" y2="18" />
					</svg>
					<input
						type="text"
						bind:value={searchValue}
						placeholder="Search by name or faculty..."
						aria-label="Search by name or faculty"
						onkeydown={(e) => e.key === 'Enter' && applyFilters()}
					/>
				</div>
				<button type="button" class="search-btn" onclick={applyFilters}>Search</button>
				<!-- Mobile filter toggle -->
				<button
					type="button"
					class="mobile-filter-btn"
					onclick={() => (mobileFiltersOpen = !mobileFiltersOpen)}
					aria-expanded={mobileFiltersOpen}
				>
					<svg viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
						<line x1="3" y1="5" x2="17" y2="5" />
						<line x1="5" y1="10" x2="15" y2="10" />
						<line x1="7" y1="15" x2="13" y2="15" />
					</svg>
					Filters
				</button>
			</div>

			<!-- Mobile filter drawer -->
			{#if mobileFiltersOpen}
				<div class="mobile-filter-panel">
					<FilterPanel
						allScholars={data.scholars}
						{faculty}
						{department}
						{highestQualification}
						{yearsOfExperience}
						{consultancyAvailability}
						{preferredConsultancyTypes}
						{geographicScope}
						{languagesSpoken}
						{areasOfExpertise}
						{industrialAreasOfExpertise}
						onFacultyChange={onFacultyChange}
						onDepartmentChange={(v) => (department = v)}
						onQualificationChange={(v) => (highestQualification = v)}
						onYearsChange={(v) => (yearsOfExperience = v)}
						onConsultancyAvailabilityChange={(v) => (consultancyAvailability = v)}
						onPreferredTypesChange={(v) => (preferredConsultancyTypes = v)}
						onGeographicScopeChange={(v) => (geographicScope = v)}
						onLanguagesChange={(v) => (languagesSpoken = v)}
						onAreasChange={(v) => (areasOfExpertise = v)}
						onIndustrialAreasChange={(v) => (industrialAreasOfExpertise = v)}
						onReset={clearFilters}
					/>
				</div>
			{/if}

			<!-- Result count -->
			<p class="results-count">
				<strong>{filteredScholars.length}</strong>
				{filteredScholars.length === 1 ? 'expert' : 'experts'} found
			</p>

			<ScholarGrid scholars={filteredScholars} onTagClick={onExpertiseTagClick} />
		</div>
	</div>
</main>

<style>
	.page-shell {
		margin: 0 auto;
		min-height: 100vh;
	}

	/* ── Two-column layout ── */
	.experts-layout {
		display: grid;
		grid-template-columns: 280px 1fr;
		gap: 2rem;
		align-items: start;
		padding: 2rem 2.5rem 3rem;
		max-width: 1400px;
		margin: 0 auto;
	}

	.experts-main {
		min-height: 80vh;
	}

	.filter-sidebar {
		position: sticky;
		top: 80px; /* below the fixed nav */
		max-height: calc(100vh - 100px);
		overflow-y: auto;
		scrollbar-width: thin;
		scrollbar-color: #d0d0da transparent;
	}

	/* ── Search bar ── */
	.search-bar {
		display: flex;
		gap: 0.75rem;
		margin-bottom: 1.25rem;
		align-items: center;
	}

	.search-input-wrap {
		flex: 1;
		position: relative;
	}

	.search-bar-icon {
		position: absolute;
		left: 0.75rem;
		top: 50%;
		transform: translateY(-50%);
		width: 16px;
		height: 16px;
		color: #aaa;
		pointer-events: none;
	}

	.search-input-wrap input {
		width: 100%;
		padding: 0.7rem 1rem 0.7rem 2.5rem;
		border: 1px solid #d0d0da;
		border-radius: 8px;
		font-size: 0.95rem;
		color: var(--ink);
		background: #fff;
		transition: border-color 0.15s, box-shadow 0.15s;
	}

	.search-input-wrap input:focus {
		outline: none;
		border-color: var(--uz-navy);
		box-shadow: 0 0 0 3px rgba(27, 43, 78, 0.08);
	}

	.search-btn {
		padding: 0.7rem 1.4rem;
		border: none;
		background: var(--uz-navy);
		color: #fff;
		border-radius: 8px;
		font-size: 0.9rem;
		font-weight: 600;
		cursor: pointer;
		white-space: nowrap;
		transition: background 0.15s;
	}

	.search-btn:hover {
		background: var(--uz-navy-deep);
	}

	/* Mobile filter toggle — hidden on desktop */
	.mobile-filter-btn {
		display: none;
		align-items: center;
		gap: 0.4rem;
		padding: 0.7rem 1rem;
		border: 1px solid #d0d0da;
		background: #fff;
		border-radius: 8px;
		font-size: 0.9rem;
		font-weight: 600;
		cursor: pointer;
		color: var(--ink);
		white-space: nowrap;
	}

	.mobile-filter-panel {
		margin-bottom: 1.25rem;
	}

	/* ── Results count ── */
	.results-count {
		font-size: 0.9rem;
		color: var(--ink-soft);
		margin: 0 0 1.25rem;
	}

	.results-count strong {
		color: var(--ink);
	}

	/* ── Responsive ── */
	@media (max-width: 1024px) {
		.experts-layout {
			grid-template-columns: 240px 1fr;
			gap: 1.5rem;
			padding: 1.5rem 1.5rem 2.5rem;
		}
	}

	@media (max-width: 768px) {
		.experts-layout {
			grid-template-columns: 1fr;
			padding: 1rem 1rem 2rem;
			gap: 0;
		}

		.filter-sidebar {
			display: none; /* hidden on mobile; shown via drawer */
		}

		.mobile-filter-btn {
			display: flex;
		}
	}
</style>
