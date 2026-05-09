<script lang="ts">
	import { onMount } from 'svelte';
	import { PUBLIC_API_BASE_URL } from '$env/static/public';
	import { authService } from '$lib/auth/auth.service';
	import type { GrantItem } from '$lib/auth/auth.service';

	let grants = $state<GrantItem[]>([]);
	let allGrants = $state<GrantItem[]>([]); // unfiltered — used for counts and categories
	let loadError = $state('');
	let searchLoading = $state(false);

	// Fetch grants from backend — supports ?q= full-text search and ?status= filter
	const fetchGrants = async (q?: string, status?: string) => {
		const params = new URLSearchParams();
		if (q && q.trim()) params.set('q', q.trim());
		if (status && status.trim()) params.set('status', status.trim());
		const url = `${PUBLIC_API_BASE_URL}/api/grants${params.size ? '?' + params.toString() : ''}`;
		try {
			const res = await fetch(url);
			if (!res.ok) return;
			grants = await res.json();
		} catch { /* silent */ }
	};

	// Fetch all grants without filters for accurate sidebar counts and category list
	const fetchAllGrants = async () => {
		try {
			const res = await fetch(`${PUBLIC_API_BASE_URL}/api/grants`);
			if (!res.ok) return;
			allGrants = await res.json();
		} catch { /* silent */ }
	};

	onMount(async () => {
		// Always fetch all grants first for counts/categories
		await fetchAllGrants();
		// Auto-apply status filter from URL param (e.g. ?status=RUNNING or ?status=OPEN,INTERNAL)
		const params = new URLSearchParams(window.location.search);
		const statusParam = params.get('status');
		const allowed = ['OPEN', 'UPCOMING', 'CLOSED', 'RUNNING', 'INTERNAL'];
		if (statusParam) {
			const parsed = statusParam.split(',').map(s => s.trim().toUpperCase()).filter(s => allowed.includes(s));
			if (parsed.length > 0) {
				selectedStatuses = parsed;
				await fetchGrants(undefined, parsed.join(','));
			} else {
				grants = allGrants;
			}
		} else {
			grants = allGrants; // no filter — reuse already-fetched data
		}
	});

	const categories = $derived([...new Set(allGrants.map((g) => g.category).filter(Boolean))]);

	let searchValue = $state('');
	let appliedSearch = $state('');
	let selectedStatuses = $state<string[]>([]); // multi-select status filter
	let selectedCategory = $state('');
	let sortBy = $state('closing');
	let itemsPerPage = $state(5);
	let currentPage = $state(1);

	let openSections = $state<Record<string, boolean>>({ status: true, category: true });
	const toggleSection = (key: string) => { openSections[key] = !openSections[key]; };

	const applySearch = async () => {
		appliedSearch = searchValue.trim();
		currentPage = 1;
		searchLoading = true;
		await fetchGrants(appliedSearch || undefined, selectedStatuses.length ? selectedStatuses.join(',') : undefined);
		searchLoading = false;
	};

	// Re-fetch when status filter checkbox changes
	const onStatusChange = async (s: string) => {
		selectedStatuses = selectedStatuses.includes(s)
			? selectedStatuses.filter(x => x !== s)
			: [...selectedStatuses, s];
		currentPage = 1;
		searchLoading = true;
		await fetchGrants(appliedSearch || undefined, selectedStatuses.length ? selectedStatuses.join(',') : undefined);
		searchLoading = false;
	};

	const clearFilters = async () => {
		searchValue = '';
		appliedSearch = '';
		selectedStatuses = [];
		selectedCategory = '';
		currentPage = 1;
		grants = allGrants; // reset display to full unfiltered list
	};

	const hasFilters = $derived(!!(appliedSearch || selectedStatuses.length || selectedCategory));

	// Client-side: only category filter and sort remain (backend already handles search + status)
	const allFilteredGrants = $derived(
		grants
			.filter((g) => {
				const matchesCategory = !selectedCategory || g.category === selectedCategory;
				return matchesCategory;
			})
			.sort((a, b) => {
				if (sortBy === 'closing') return (a.closingDate ?? '').localeCompare(b.closingDate ?? '');
				if (sortBy === 'funder') return (a.funder ?? '').localeCompare(b.funder ?? '');
				if (sortBy === 'title') return (a.title ?? '').localeCompare(b.title ?? '');
				return 0;
			})
	);

	const totalPages = $derived(Math.max(1, Math.ceil(allFilteredGrants.length / itemsPerPage)));

	// Reset to page 1 whenever filters/sort/perPage change
	$effect(() => {
		void allFilteredGrants;
		void itemsPerPage;
		currentPage = 1;
	});

	// The slice shown on the current page
	const filteredGrants = $derived(
		allFilteredGrants.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
	);

	// Page numbers to show: always first, last, current ±1, with ellipsis gaps
	const pageNumbers = $derived.by(() => {
		const total = totalPages;
		const pages: (number | string)[] = [];
		if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);
		pages.push(1);
		if (currentPage > 3) pages.push('...');
		for (let p = Math.max(2, currentPage - 1); p <= Math.min(total - 1, currentPage + 1); p++) {
			pages.push(p);
		}
		if (currentPage < total - 2) pages.push('...');
		pages.push(total);
		return pages;
	});

	const goTo = (p: number) => {
		currentPage = Math.max(1, Math.min(p, totalPages));
		window.scrollTo({ top: 0, behavior: 'smooth' });
	};

	const statusLabel: Record<string, string> = { OPEN: 'Open', UPCOMING: 'Upcoming', CLOSED: 'Closed', RUNNING: 'Running', INTERNAL: 'Internal' };
	// Count grants that include this status (handles comma-separated)
	const countByStatus = (s: string) => allGrants.filter((g) => g.status?.split(',').map(x => x.trim()).includes(s)).length;
	const countByCategory = (c: string) => allGrants.filter((g) => g.category === c).length;
</script>

<svelte:head>
	<title>Grants & Funding | University of Zimbabwe Experts</title>
	<meta name="description" content="Browse available research grants and funding opportunities for University of Zimbabwe academics and researchers." />
</svelte:head>

<!-- Page header -->
<div class="gp-header">
	<div class="gp-header-inner">
		<div class="gp-accent-bar"></div>
		<h1 class="gp-title">Research Funding Database</h1>
		<div class="gp-search-bar">
			<input
				type="text"
				bind:value={searchValue}
				placeholder="Search..."
				aria-label="Search grants"
				onkeydown={(e) => e.key === 'Enter' && applySearch()}
			/>
			<button type="button" class="gp-search-btn" onclick={applySearch}>
				<svg width="15" height="15" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2.5">
					<circle cx="8.5" cy="8.5" r="5.5"/><line x1="13" y1="13" x2="18" y2="18"/>
				</svg>
				search
			</button>
		</div>
	</div>
</div>

<!-- Body -->
<div class="gp-body">
	<!-- Sidebar -->
	<aside class="gp-sidebar">
		<!-- Status -->
		<div class="sb-section">
			<button type="button" class="sb-header" onclick={() => toggleSection('status')} aria-expanded={openSections.status}>
				<span>Status</span>
				<span class="sb-chevron" class:open={openSections.status}>&#8963;</span>
			</button>
			{#if openSections.status}
				<div class="sb-body">
					{#each ['OPEN', 'UPCOMING', 'CLOSED', 'RUNNING', 'INTERNAL'] as s}
						<label class="sb-row">
						<input type="checkbox" checked={selectedStatuses.includes(s)} onchange={() => onStatusChange(s)} />
							<span class="sb-label">{statusLabel[s]}</span>
							<span class="sb-count">({countByStatus(s)})</span>
						</label>
					{/each}
				</div>
			{/if}
		</div>

		<!-- Category -->
		<div class="sb-section sb-section-last">
			<button type="button" class="sb-header" onclick={() => toggleSection('category')} aria-expanded={openSections.category}>
				<span>Category</span>
				<span class="sb-chevron" class:open={openSections.category}>&#8963;</span>
			</button>
			{#if openSections.category}
				<div class="sb-body">
					{#each categories as cat}
						<label class="sb-row">
							<input type="checkbox" checked={selectedCategory === cat} onchange={() => (selectedCategory = selectedCategory === cat ? '' : cat)} />
							<span class="sb-label">{cat}</span>
							<span class="sb-count">({countByCategory(cat)})</span>
						</label>
					{/each}
				</div>
			{/if}
		</div>

		{#if hasFilters}
			<button type="button" class="sb-reset" onclick={clearFilters}>Reset filters</button>
		{/if}
	</aside>

	<!-- Results -->
	<div class="gp-main">
		<!-- Sort bar -->
		<div class="gp-sort-bar">
			<div class="gp-sort-controls">
				<select bind:value={sortBy} aria-label="Sort by">
					<option value="closing">Sort by closing date</option>
					<option value="funder">Sort by funder</option>
					<option value="title">Sort by title</option>
				</select>
				<select bind:value={itemsPerPage} aria-label="Items per page">
					<option value={5}>5 per page</option>
					<option value={10}>10 per page</option>
					<option value={25}>25 per page</option>
				</select>
			</div>
			<p class="gp-count">
			{#if searchLoading}
				Searching…
			{:else}
				<strong>{allFilteredGrants.length}</strong> result{allFilteredGrants.length === 1 ? '' : 's'} found
			{/if}
		</p>
		</div>

		<!-- Grant list -->
		<div class="gp-list">
			{#if filteredGrants.length === 0}
				<div class="gp-empty">
					No grants match your search.
					{#if hasFilters}<button type="button" onclick={clearFilters}>Clear filters</button>{/if}
				</div>
			{:else}
				{#each filteredGrants as grant (grant.id)}
					<article class="gp-card" class:gp-card-featured={grant.featured} class:gp-card-closed={grant.status === 'closed'}>
						{#if grant.featured}<div class="gp-ribbon">Featured</div>{/if}
						<div class="gp-card-accent"></div>

						<div class="gp-card-left">
							<p class="gp-card-funder">{grant.funder}</p>
							<h2 class="gp-card-title">{grant.title}</h2>
							<p class="gp-card-desc">{grant.description}</p>
							<span class="gp-card-cat">{grant.category}</span>
						</div>

						<div class="gp-card-divider" aria-hidden="true"></div>

						<div class="gp-card-right">
							<span class="gp-badge gp-badge-{grant.status.toLowerCase()}">{statusLabel[grant.status]}</span>
							<div class="gp-meta">
								<span class="gp-meta-label">Amount</span>
								<span class="gp-meta-value">{grant.amount}</span>
							</div>
							<div class="gp-meta">
								<span class="gp-meta-label">Closing date</span>
								<span class="gp-meta-value">{grant.closingDate}</span>
							</div>
						</div>

						<!-- Hover Apply button -->
						{#if grant.applyUrl}
							<a
								href={grant.applyUrl}
								target="_blank"
								rel="noopener noreferrer"
								class="gp-apply-btn"
								aria-label="Apply or get more information about {grant.title}"
							>
								Apply / More Info
								<svg width="13" height="13" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
									<line x1="3" y1="8" x2="13" y2="8"/><polyline points="9 4 13 8 9 12"/>
								</svg>
							</a>
						{/if}
					</article>
				{/each}
			{/if}
		</div>

		<!-- Pagination -->
		{#if totalPages > 1}
			<nav class="gp-pagination" aria-label="Pagination">
				{#each pageNumbers as p}
					{#if p === '...'}
						<span class="pg-ellipsis">&hellip;</span>
					{:else}
						<button
							type="button"
							class="pg-btn"
							class:pg-active={p === currentPage}
							onclick={() => goTo(p as number)}
							aria-current={p === currentPage ? 'page' : undefined}
						>
							{p}
						</button>
					{/if}
				{/each}
				<button
					type="button"
					class="pg-btn pg-next"
					disabled={currentPage === totalPages}
					onclick={() => goTo(currentPage + 1)}
					aria-label="Next page"
				>&gt;&gt;</button>
			</nav>
		{/if}
	</div>
</div>

<style>
	/* Header */
	.gp-header {
		background: #fff;
		border-bottom: 1px solid #e0e0e8;
		padding: 2.5rem 2rem 2rem;
	}
	.gp-header-inner { max-width: 1200px; margin: 0 auto; }
	.gp-accent-bar { width: 36px; height: 4px; background: var(--uz-orange); margin-bottom: 0.8rem; }
	.gp-title {
		font-family: 'Fraunces', serif;
		font-size: clamp(1.6rem, 3vw, 2.2rem);
		color: var(--uz-navy);
		margin: 0 0 1.25rem;
		font-weight: 700;
	}
	.gp-search-bar {
		display: flex;
		max-width: 780px;
		border: 1px solid #c8c8d8;
		border-radius: 4px;
		overflow: hidden;
		background: #fff;
	}
	.gp-search-bar input {
		flex: 1;
		padding: 0.75rem 1rem;
		border: none;
		font-size: 0.95rem;
		color: var(--ink);
		outline: none;
	}
	.gp-search-btn {
		display: flex;
		align-items: center;
		gap: 0.4rem;
		padding: 0.75rem 1.4rem;
		background: var(--uz-navy);
		color: #fff;
		border: none;
		font-size: 0.9rem;
		font-weight: 700;
		cursor: pointer;
		white-space: nowrap;
		transition: background 0.15s;
	}
	.gp-search-btn:hover { background: var(--uz-navy-deep); }

	/* Body */
	.gp-body {
		display: grid;
		grid-template-columns: 280px minmax(0, 1fr);
		max-width: 1200px;
		margin: 0 auto;
		padding: 2rem 2rem 4rem;
		gap: 2rem;
		align-items: start;
		min-height: 100vh;
	}

	/* Sidebar */
	.gp-sidebar {
		position: sticky;
		top: 80px;
		max-height: calc(100vh - 100px);
		overflow-y: auto;
		scrollbar-width: thin;
	}
	.sb-section {
		border: 1px solid #e0e0e8;
		border-bottom: none;
		background: #fff;
	}
	.sb-section-last { border-bottom: 1px solid #e0e0e8; }
	.sb-header {
		width: 100%;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0.85rem 1rem;
		background: none;
		border: none;
		font-size: 0.82rem;
		font-weight: 700;
		color: var(--ink);
		cursor: pointer;
		text-align: left;
		text-transform: uppercase;
		letter-spacing: 0.07em;
	}
	.sb-header:hover { background: #f7f7fb; }
	.sb-chevron {
		font-size: 0.8rem;
		color: #888;
		display: inline-block;
		transform: rotate(180deg);
		transition: transform 0.2s;
	}
	.sb-chevron.open { transform: rotate(0deg); }
	.sb-body {
		padding: 0.25rem 1rem 0.85rem;
		display: flex;
		flex-direction: column;
		gap: 0.05rem;
		border-top: 1px solid #e8e8f0;
	}
	.sb-row {
		display: flex;
		align-items: center;
		gap: 0.55rem;
		padding: 0.35rem 0;
		font-size: 0.875rem;
		cursor: pointer;
		color: var(--ink);
	}
	.sb-row input { width: 15px; height: 15px; flex-shrink: 0; accent-color: var(--uz-navy); cursor: pointer; }
	.sb-label { flex: 1; }
	.sb-count { font-size: 0.78rem; color: #999; }
	.sb-reset {
		display: block;
		width: 100%;
		margin-top: 0.75rem;
		padding: 0.6rem;
		border: 1px solid #d0d0da;
		border-radius: 4px;
		background: #fff;
		font-size: 0.85rem;
		font-weight: 600;
		color: var(--uz-orange);
		cursor: pointer;
		text-align: center;
	}
	.sb-reset:hover { background: #fff5ee; }

	/* Main */
	.gp-main { min-height: 70vh; min-width: 0; width: 100%; }
	.gp-sort-bar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		flex-wrap: wrap;
		gap: 0.75rem;
		margin-bottom: 1.25rem;
	}
	.gp-sort-controls { display: flex; gap: 0.6rem; flex-wrap: wrap; }
	.gp-sort-controls select {
		padding: 0.5rem 0.85rem;
		border: 1px solid #c8c8d8;
		border-radius: 4px;
		font-size: 0.875rem;
		color: var(--ink);
		background: #fff;
		cursor: pointer;
	}
	.gp-sort-controls select:focus { outline: none; border-color: var(--uz-navy); }
	.gp-count { font-size: 0.95rem; color: var(--ink-soft); margin: 0; }
	.gp-count strong { color: var(--ink); }

	/* Grant list */
	.gp-list { display: flex; flex-direction: column; gap: 1.1rem; }

	/* Grant card */
	.gp-card {
		position: relative;
		display: grid;
		grid-template-columns: minmax(0, 1fr) auto 200px;
		align-items: stretch;
		background: #fff;
		border: 1px solid #e0dcd4;
		overflow: hidden;
		box-shadow: 0 2px 6px rgba(27,43,78,0.05);
		transition: box-shadow 0.15s;
	}
	.gp-card:hover { box-shadow: 0 5px 18px rgba(27,43,78,0.1); }
	.gp-card-featured { border-color: #c9621a; }
	.gp-card-closed { opacity: 0.55; }
	.gp-card-accent { position: absolute; top: 0; left: 0; width: 36px; height: 4px; background: var(--uz-orange); }
	.gp-ribbon {
		position: absolute;
		top: 14px;
		right: -28px;
		width: 110px;
		text-align: center;
		background: #1a3a5c;
		color: #fff;
		font-size: 0.6rem;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.1em;
		padding: 0.28rem 0;
		transform: rotate(45deg);
		z-index: 1;
	}
	.gp-card-left {
		padding: 1.6rem 1.5rem 1.4rem;
		display: flex;
		flex-direction: column;
		gap: 0.35rem;
		min-width: 0;
	}
	.gp-card-funder { margin: 0; font-size: 0.82rem; color: var(--ink-soft); overflow-wrap: break-word; }
	.gp-card-title { margin: 0; font-size: 1rem; font-weight: 700; color: var(--uz-navy); line-height: 1.4; overflow-wrap: break-word; }
	.gp-card-desc { margin: 0; font-size: 0.86rem; color: var(--ink-soft); line-height: 1.55; overflow-wrap: break-word; }
	.gp-card-cat {
		align-self: flex-start;
		margin-top: 0.2rem;
		padding: 0.16rem 0.55rem;
		border-radius: 100px;
		background: var(--uz-orange-light);
		color: var(--uz-orange-dark);
		font-size: 0.72rem;
		font-weight: 700;
		text-transform: uppercase;
		letter-spacing: 0.05em;
	}
	.gp-card-divider { width: 1px; background: #e4e0d8; margin: 1.2rem 0; }
	.gp-card-right {
		padding: 1.4rem 1.3rem;
		display: flex;
		flex-direction: column;
		gap: 0.85rem;
		justify-content: center;
		min-width: 0;
		width: 200px;
		flex-shrink: 0;
	}
	.gp-badge {
		display: inline-block;
		font-size: 0.68rem;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.1em;
		padding: 0.2rem 0.6rem;
		border-radius: 100px;
		align-self: flex-start;
	}
	.gp-badge-open     { background: #e0f0ff; color: #1a3a6b; }
	.gp-badge-upcoming { background: #fff3dc; color: #8a5a00; }
	.gp-badge-closed   { background: #f0f0f0; color: #666; }
	.gp-badge-running  { background: #d4f0e0; color: #1a6b3a; }
	.gp-meta { display: flex; flex-direction: column; gap: 0.1rem; }
	.gp-meta-label { font-size: 0.73rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.07em; color: var(--ink-soft); }
	.gp-meta-value { font-size: 0.9rem; color: var(--ink); font-weight: 500; }

	/* Empty */
	.gp-empty {
		border: 1px dashed #d0d0da;
		padding: 2rem;
		border-radius: 4px;
		background: #fff;
		font-size: 0.95rem;
		color: var(--ink-soft);
		display: flex;
		gap: 0.5rem;
		align-items: center;
	}
	.gp-empty button {
		background: none;
		border: none;
		color: var(--uz-orange);
		font-weight: 600;
		cursor: pointer;
		text-decoration: underline;
		font-size: inherit;
		padding: 0;
	}

	/* Apply hover button */
	.gp-apply-btn {
		position: absolute;
		bottom: 0;
		right: 0;
		display: flex;
		align-items: center;
		gap: 0.4rem;
		padding: 0.55rem 1.1rem;
		background: var(--uz-orange);
		color: #fff;
		font-size: 0.8rem;
		font-weight: 700;
		text-transform: uppercase;
		letter-spacing: 0.06em;
		border-radius: 8px 0 0 0;
		opacity: 0;
		transform: translateY(6px);
		transition: opacity 0.18s ease, transform 0.18s ease;
		pointer-events: none;
		text-decoration: none;
	}
	.gp-card:hover .gp-apply-btn {
		opacity: 1;
		transform: translateY(0);
		pointer-events: auto;
	}
	.gp-apply-btn:hover {
		background: var(--uz-orange-dark);
	}

	/* Pagination */
	.gp-pagination {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.3rem;
		margin-top: 2.5rem;
		padding-top: 1.5rem;
		border-top: 1px solid #e8e8f0;
	}
	.pg-btn {
		min-width: 36px;
		height: 36px;
		padding: 0 0.5rem;
		border: none;
		background: none;
		border-radius: 4px;
		font-size: 0.95rem;
		font-weight: 500;
		color: var(--uz-navy);
		cursor: pointer;
		transition: background 0.12s, color 0.12s;
		font-family: inherit;
	}
	.pg-btn:hover:not(:disabled) {
		background: #f0f0f8;
	}
	.pg-btn:disabled {
		opacity: 0.35;
		cursor: default;
	}
	.pg-active {
		color: var(--uz-orange) !important;
		font-weight: 700;
	}
	.pg-next {
		font-size: 0.8rem;
		font-weight: 700;
		color: var(--uz-navy);
	}
	.pg-ellipsis {
		min-width: 36px;
		height: 36px;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 0.95rem;
		color: var(--ink-soft);
	}

	/* Responsive */
	@media (max-width: 900px) {
		.gp-body { grid-template-columns: 1fr; padding: 1.25rem 1rem 3rem; }
		.gp-sidebar { position: static; max-height: none; }
		.gp-card { grid-template-columns: 1fr; }
		.gp-card-divider { width: auto; height: 1px; margin: 0 1.2rem; }
		.gp-card-right { flex-direction: row; flex-wrap: wrap; gap: 1rem; padding-top: 0.75rem; padding-bottom: 1.2rem; }
		.gp-ribbon { top: 10px; right: -26px; width: 100px; }
	}
	@media (max-width: 600px) {
		.gp-header { padding: 1.5rem 1rem 1.25rem; }
		.gp-sort-bar { flex-direction: column; align-items: flex-start; }
	}
</style>
