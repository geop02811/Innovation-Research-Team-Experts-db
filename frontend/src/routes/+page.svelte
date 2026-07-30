<script lang="ts">
	import { onMount } from 'svelte';
	import { authService } from '$lib/auth/auth.service';
	import type { EventItem, AlumniNewsItem, GrantItem } from '$lib/auth/auth.service';

	const heroImages = [
		'/sophia.jpeg',
		'/Sophia_at_the_AI_for_Good_Global_Summit_2018_(27254369347)_(cropped).jpg'
	];

	let currentSlide = $state(0);
	let activeTab = $state<'research' | 'alumni' | 'events' | 'internal-funding-opportunities' | 'funding-opportunities'>('research');

	let events = $state<EventItem[]>([]);
	let alumniNews = $state<AlumniNewsItem[]>([]);
	let grants = $state<GrantItem[]>([]);
	const grantsForwardUrl = 'https://www.grantsforward.com/';

	// Running projects = funding-opportunities with status RUNNING pulled from the grants management system
	const runningProjects = $derived(grants.filter((g) => g.status === 'RUNNING'));
	// Internal funding-opportunities = funding-opportunities with status INTERNAL
	const internalGrants = $derived(grants.filter((g) => g.status === 'INTERNAL'));

	onMount(() => {
		const id = setInterval(() => {
			currentSlide = (currentSlide + 1) % heroImages.length;
		}, 5000);
		authService.getEvents().then(d => events = d);
		authService.getAlumniNews().then(d => alumniNews = d);
		authService.getGrants().then(d => grants = d);
		return () => clearInterval(id);
	});

	const goToGrants = () => {
		window.location.href = '/grants';
	};

	const goToRunningProjects = () => {
		window.location.href = '/grants?status=RUNNING';
	};

	const goToInternalGrants = () => {
		window.location.href = '/grants?status=INTERNAL';
	};

	const monthName = (dateStr: string) => {
		if (!dateStr) return '';
		const d = new Date(dateStr);
		return d.toLocaleString('default', { month: 'short' });
	};
	const dayNum = (dateStr: string) => {
		if (!dateStr) return '';
		return new Date(dateStr).getDate().toString();
	};
</script>

<svelte:head>
	<title>Home | University of Zimbabwe Researchers</title>
	<meta
		name="description"
		content="University of Zimbabwe researchers platform for scholar discovery, research, and profile exploration."
	/>
</svelte:head>

<!-- ═══════════════════════════════════════════ HERO ═══ -->
<section class="hp-hero">
	<div class="hp-hero-slides" aria-hidden="true">
		{#each heroImages as img, i}
			<div class="hp-hero-slide" style="transform: translateX({(i - currentSlide) * 100}%)">
				<img src={img} alt="" />
			</div>
		{/each}
	</div>
	<div class="hp-hero-inner">
		<p class="hp-kicker">University of Zimbabwe — Research &amp; Funding</p>
		<h1 class="hp-title">Discover UZ's Leading Researchers &amp; Scholars</h1>
		<p class="hp-subtitle">
			Explore the University of Zimbabwe's community of academics, innovators, and industry consultants.
			Connect with experts driving research, policy, and development across Africa.
		</p>
		<div class="hp-ctas">
			<a class="hp-btn-primary" href="/experts">Browse Researchers</a>
			<a class="hp-btn-secondary" href="/signup">Join as a Researcher</a>
		</div>
	</div>
	<div class="hp-hero-badge" aria-hidden="true">
		<span class="badge-year">Est. 1952</span>
		<span class="badge-label">University of Zimbabwe</span>
	</div>
</section>

<!-- ══════════════════════════════════════ QUICK STATS ═══ -->
<section class="hp-stats">
	<div class="hp-stats-inner">
		<div class="stat-item">
			<span class="stat-number">2000+</span>
			<span class="stat-label">Academic Staff</span>
		</div>
		<div class="stat-item">
			<span class="stat-number">11</span>
			<span class="stat-label">Faculties</span>
		</div>
		<div class="stat-item">
			<span class="stat-number">80+</span>
			<span class="stat-label">Areas of Research </span>
		</div>
		<div class="stat-item">
			<span class="stat-number">40+</span>
			<span class="stat-label">Years of Research</span>
		</div>
	</div>
</section>

<!-- ═══════════════════════════════════ SECTION TABS NAV ═══ -->
<nav class="hp-tabs" aria-label="Page sections">
	<button class="hp-tab" class:hp-tab-active={activeTab === 'research'} onclick={() => activeTab = 'research'}>Research</button>
	<button class="hp-tab" class:hp-tab-active={activeTab === 'alumni'} onclick={() => activeTab = 'alumni'}>Alumni</button>
	<button class="hp-tab" class:hp-tab-active={activeTab === 'events'} onclick={() => activeTab = 'events'}>Running Projects</button>
	<button class="hp-tab" class:hp-tab-active={activeTab === 'internal-funding-opportunities'} onclick={() => activeTab = 'internal-funding-opportunities'}>Internal Funding Opportunities</button>
	<button class="hp-tab" class:hp-tab-active={activeTab === 'funding-opportunities'} onclick={() => activeTab = 'funding-opportunities'}>Funding Opportunities</button>
</nav>

<!-- ══════════════════════════════════════ RESEARCH HIGHLIGHTS ═══ -->
{#if activeTab === 'research'}
<section class="hp-section" id="research">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Research Highlights</h2>
			<a class="hp-section-link" href="/research">Explore All Research →</a>
		</div>
		<div class="hp-cards">
			<a href="/research#biomedical" class="hp-card-wrap">
				<article class="hp-card hp-card-featured" style="background-image: url('/bio_med.webp')">
					<div class="hp-card-inner">
						<div class="hp-card-tag">Biomedical Engineering</div>
						<div class="hp-card-body">
							<h3>Low-cost diagnostic devices for rural Zimbabwe</h3>
							<p>Prof. Chinyama's team is developing solar-powered point-of-care diagnostic tools deployable across Zimbabwe's rural clinics, reducing reliance on centralised laboratories.</p>
							<div class="hp-card-meta">Prof. T. Chinyama · Faculty of Engineering</div>
						</div>
					</div>
				</article>
			</a>
			<a href="/research#climate" class="hp-card-wrap">
				<article class="hp-card" style="background-image: url('/climate.jpg')">
					<div class="hp-card-inner">
						<div class="hp-card-tag">Climate Science</div>
						<div class="hp-card-body">
							<h3>Drought resilience &amp; agricultural adaptation</h3>
							<p>Modelling rainfall variability and soil conditions to guide smallholder farming policy in Southern Africa.</p>
							<div class="hp-card-meta">Dr. S. Moyo · Faculty of Agriculture</div>
						</div>
					</div>
				</article>
			</a>
			<a href="/research#ai-languages" class="hp-card-wrap">
				<article class="hp-card" style="background-image: url('/computer_science.webp')">
					<div class="hp-card-inner">
						<div class="hp-card-tag">Computer Science</div>
						<div class="hp-card-body">
							<h3>AI for local-language NLP in Shona &amp; Ndebele</h3>
							<p>Building open training datasets and transformer models for Zimbabwe's indigenous languages to power chatbots and assistive technologies.</p>
							<div class="hp-card-meta">Dr. R. Dube · Faculty of Science</div>
						</div>
					</div>
				</article>
			</a>
			<a href="/research#public-health" class="hp-card-wrap">
				<article class="hp-card" style="background-image: url('/public_health.webp')">
					<div class="hp-card-inner">
						<div class="hp-card-tag">Public Health</div>
						<div class="hp-card-body">
							<h3>Urban sanitation &amp; water quality monitoring</h3>
							<p>Community-based IoT sensor networks to provide real-time water-quality data in Harare's peri-urban settlements.</p>
							<div class="hp-card-meta">Prof. N. Zvobgo · Faculty of Medicine</div>
						</div>
					</div>
				</article>
			</a>
			<a href="/research#arts" class="hp-card-wrap">
				<article class="hp-card" style="background-image: url('/Arts.jpg')">
					<div class="hp-card-inner">
						<div class="hp-card-tag">Arts &amp; Humanities</div>
						<div class="hp-card-body">
							<h3>Preserving Zimbabwe's visual heritage through digital archiving</h3>
							<p>Digitising and cataloguing pre-colonial and post-independence artworks to create an open-access national cultural repository.</p>
							<div class="hp-card-meta">Dr. A. Mutasa · Faculty of Arts</div>
						</div>
					</div>
				</article>
			</a>
			<a href="/research#music" class="hp-card-wrap">
				<article class="hp-card" style="background-image: url('/Music.jpg')">
					<div class="hp-card-inner">
						<div class="hp-card-tag">Music</div>
						<div class="hp-card-body">
							<h3>Ethnomusicology &amp; the living traditions of Mbira</h3>
							<p>Documenting and analysing the Mbira dzavadzimu as a living cultural practice, exploring its role in contemporary Zimbabwean identity and spiritual life.</p>
							<div class="hp-card-meta">Prof. C. Nhongo · Faculty of Arts</div>
						</div>
					</div>
				</article>
			</a>
		</div>
	</div>
</section>
{/if}

<!-- ══════════════════════════════════════ ALUMNI NEWS ═══ -->
{#if activeTab === 'alumni'}
<section class="hp-section hp-section-alt" id="alumni">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Alumni Spotlight</h2>
			<span class="hp-section-link">What Our Graduates Are Doing</span>
		</div>
		{#if alumniNews.length === 0}
			<p class="hp-empty-tab">No alumni news yet. Check back soon.</p>
		{:else}
			<div class="hp-news-list">
				{#each alumniNews as item (item.id)}
					<article class="hp-news-item">
						<div class="hp-news-date">{item.newsDate}</div>
						<div class="hp-news-body">
							<h3>{item.headline}</h3>
							{#if item.body}<p>{item.body}</p>{/if}
						</div>
					</article>
				{/each}
			</div>
		{/if}
	</div>
</section>
{/if}

<!-- ═══════════════════════════════════════ RUNNING PROJECTS ═══ -->
{#if activeTab === 'events'}
<section class="hp-section" id="events">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Running Projects</h2>
		</div>
		{#if runningProjects.length === 0}
			<p class="hp-empty-tab">No running projects at the moment. Check back soon.</p>
		{:else}
			<div class="hp-grant-list">
				{#each runningProjects.slice(0, 3) as project (project.id)}
					<article class="hp-grant-item" class:hp-grant-featured={project.featured}>
						{#if project.featured}<div class="hp-grant-ribbon" aria-label="Featured">Featured</div>{/if}
						<div class="hp-grant-accent"></div>
						<div class="hp-grant-left">
							<p class="hp-grant-funder">{project.funder}</p>
							<h3 class="hp-grant-title">{project.title}</h3>
							{#if project.description}<p class="hp-grant-desc">{project.description}</p>{/if}
							<div class="hp-rp-pills">
								<span class="hp-rp-pill hp-rp-pill-uni">UZ</span>
								{#if project.category}<span class="hp-rp-pill hp-rp-pill-faculty">{project.category}</span>{/if}
							</div>
						</div>
						<div class="hp-grant-divider" aria-hidden="true"></div>
						<div class="hp-grant-right">
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">Budget</span>
								<span class="hp-grant-meta-value">{project.amount}</span>
							</div>
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">Status</span>
								<span class="hp-rp-status-badge">Running</span>
							</div>
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">End date</span>
								<span class="hp-grant-meta-value">{project.closingDate ?? '—'}</span>
							</div>
						</div>
					</article>
				{/each}
			</div>
		{/if}

		<div class="hp-grant-more">
			<button type="button" class="hp-grant-more-btn" onclick={goToRunningProjects}>
				See All Running Projects
				<svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
					<line x1="3" y1="8" x2="13" y2="8" />
					<polyline points="9 4 13 8 9 12" />
				</svg>
			</button>
		</div>
	</div>
</section>
{/if}

<!-- ══════════════════════════════════ INTERNAL funding-opportunities ═══ -->
{#if activeTab === 'internal-funding-opportunities'}
<section class="hp-section hp-section-alt" id="internal-grants">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Internal Funding Opportunities</h2>
		</div>
		{#if internalGrants.length === 0}
			<a class="hp-fallback-card" href={grantsForwardUrl} target="_blank" rel="noopener noreferrer">
				<span class="hp-fallback-eyebrow">External funding database</span>
				<strong>Explore GrantsForward</strong>
				<span>Browse current funding opportunities while internal funding updates are being prepared.</span>
				<span class="hp-fallback-action">Open GrantsForward →</span>
			</a>
		{:else}
			<div class="hp-grant-list">
				{#each internalGrants.slice(0, 3) as grant (grant.id)}
					<article class="hp-grant-item" class:hp-grant-featured={grant.featured}>
						{#if grant.featured}<div class="hp-grant-ribbon" aria-label="Featured">Featured</div>{/if}
						<div class="hp-grant-accent"></div>
						<div class="hp-grant-left">
							<p class="hp-grant-funder">{grant.funder}</p>
							<h3 class="hp-grant-title">{grant.title}</h3>
							{#if grant.description}<p class="hp-grant-desc">{grant.description}</p>{/if}
						</div>
						<div class="hp-grant-divider" aria-hidden="true"></div>
						<div class="hp-grant-right">
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">Amount</span>
								<span class="hp-grant-meta-value">{grant.amount}</span>
							</div>
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">Closing date</span>
								<span class="hp-grant-meta-value">{grant.closingDate ?? '—'}</span>
							</div>
						</div>
					</article>
				{/each}
			</div>
		{/if}

		<div class="hp-grant-more">
			<button type="button" class="hp-grant-more-btn" onclick={goToInternalGrants}>
				View More Internal Funding Opportunities
				<svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
					<line x1="3" y1="8" x2="13" y2="8" />
					<polyline points="9 4 13 8 9 12" />
				</svg>
			</button>
		</div>
	</div>
</section>
{/if}

<!-- ══════════════════════════════════════════ funding-opportunities ═══ -->
{#if activeTab === 'funding-opportunities'}
<section class="hp-section" id="grants">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Funding Opportunities</h2>
			<button type="button" class="hp-section-link hp-section-link-btn" onclick={goToGrants}>View All Funding Opportunities →</button>
		</div>

		{#if grants.length === 0}
			<a class="hp-fallback-card" href={grantsForwardUrl} target="_blank" rel="noopener noreferrer">
				<span class="hp-fallback-eyebrow">External funding database</span>
				<strong>Explore GrantsForward</strong>
				<span>Browse current funding opportunities while new UZ funding opportunities are being prepared.</span>
				<span class="hp-fallback-action">Open GrantsForward →</span>
			</a>
		{:else}
			<div class="hp-grant-list">
				{#each grants.slice(0, 3) as grant (grant.id)}
					<article class="hp-grant-item" class:hp-grant-featured={grant.featured}>
						{#if grant.featured}<div class="hp-grant-ribbon" aria-label="Featured">Featured</div>{/if}
						<div class="hp-grant-accent"></div>
						<div class="hp-grant-left">
							<p class="hp-grant-funder">{grant.funder}</p>
							<h3 class="hp-grant-title">{grant.title}</h3>
							{#if grant.description}<p class="hp-grant-desc">{grant.description}</p>{/if}
						</div>
						<div class="hp-grant-divider" aria-hidden="true"></div>
						<div class="hp-grant-right">
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">Amount</span>
								<span class="hp-grant-meta-value">{grant.amount}</span>
							</div>
							<div class="hp-grant-meta-item">
								<span class="hp-grant-meta-label">Closing date</span>
								<span class="hp-grant-meta-value">{grant.closingDate ?? '—'}</span>
							</div>
						</div>
					</article>
				{/each}
			</div>
		{/if}

		<div class="hp-grant-more">
			<button type="button" class="hp-grant-more-btn" onclick={goToGrants}>
				View All Available Funding Opportunities
				<svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
					<line x1="3" y1="8" x2="13" y2="8" />
					<polyline points="9 4 13 8 9 12" />
				</svg>
			</button>
		</div>
	</div>
</section>
{/if}

<!-- ═══════════════════════════ CALL TO ACTION BAND ═══ -->
<section class="hp-cta-band">
	<div class="hp-cta-band-inner">
		<h2>Are you a UZ expert or researcher?</h2>
		<p>Create your profile and be discovered by collaborators, industry partners, and the global academic community.</p>
		<div class="hp-ctas">
			<a class="hp-btn-primary" href="/signup">Register Your Profile</a>
			<a class="hp-btn-outline" href="/experts">Explore Researchers</a>
		</div>
	</div>
</section>

<style>
/* ─── Hero ─────────────────────────────────────────── */
.hp-hero {
	background: var(--hero);
	color: #fff;
	padding: 5rem 1.5rem 4rem;
	position: relative;
	overflow: hidden;
}
.hp-hero::before {
	content: '';
	position: absolute;
	inset: 0;
	background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
	pointer-events: none;	z-index: 0;
}
.hp-hero-slides {
	position: absolute;
	inset: 0;
	overflow: hidden;
	z-index: 1;
}
.hp-hero-slide {
	position: absolute;
	inset: 0;
	transition: transform 0.9s cubic-bezier(0.77,0,0.175,1);
}
.hp-hero-slide img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	object-position: top center;
	filter: brightness(0.28);
}
.hp-hero-inner {
	max-width: 820px;
	margin: 0 auto;
	position: relative;
	z-index: 2;
}
.hp-kicker {
	text-transform: uppercase;
	font-size: 0.78rem;
	font-weight: 700;
	letter-spacing: 0.16em;
	color: var(--uz-gold);
	margin: 0 0 0.8rem;
}
.hp-title {
	font-family: 'Fraunces', serif;
	font-size: clamp(2rem, 5vw, 3.4rem);
	line-height: 1.1;
	margin: 0 0 1rem;
	color: #fff;
}
.hp-subtitle {
	font-size: 1.1rem;
	line-height: 1.65;
	color: rgba(255,255,255,0.87);
	max-width: 68ch;
	margin: 0 0 1.8rem;
}
.hp-ctas {
	display: flex;
	flex-wrap: wrap;
	gap: 0.75rem;
}
.hp-btn-primary {
	background: var(--uz-orange);
	color: #fff;
	padding: 0.75rem 1.6rem;
	border-radius: 999px;
	font-weight: 800;
	font-size: 0.9rem;
	letter-spacing: 0.04em;
	text-transform: uppercase;
	transition: background 0.2s;
}
.hp-btn-primary:hover { background: var(--uz-orange-dark); }
.hp-btn-secondary {
	background: rgba(255,255,255,0.15);
	color: #fff;
	padding: 0.75rem 1.6rem;
	border-radius: 999px;
	font-weight: 700;
	font-size: 0.9rem;
	letter-spacing: 0.04em;
	text-transform: uppercase;
	border: 1px solid rgba(255,255,255,0.4);
	transition: background 0.2s;
}
.hp-btn-secondary:hover { background: rgba(255,255,255,0.22); }
.hp-btn-outline {
	background: transparent;
	color: #fff;
	padding: 0.75rem 1.6rem;
	border-radius: 999px;
	font-weight: 700;
	font-size: 0.9rem;
	letter-spacing: 0.04em;
	text-transform: uppercase;
	border: 2px solid rgba(255,255,255,0.5);
	transition: background 0.2s;
}
.hp-btn-outline:hover { background: rgba(255,255,255,0.12); }
.hp-hero-badge {
	position: absolute;
	z-index: 2;
	right: 2rem;
	top: 50%;
	transform: translateY(-50%);
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 0.3rem;
	opacity: 0.18;
	pointer-events: none;
}
.badge-year {
	font-family: 'Fraunces', serif;
	font-size: 3.5rem;
	font-weight: 700;
	color: #fff;
	line-height: 1;
}
.badge-label {
	font-size: 0.7rem;
	text-transform: uppercase;
	letter-spacing: 0.14em;
	color: #fff;
}
@media (max-width: 640px) { .hp-hero-badge { display: none; } }

/* ─── Stats bar ────────────────────────────────────── */
.hp-stats {
	background: var(--uz-navy-deep);
	color: #fff;
}
.hp-stats-inner {
	max-width: 1100px;
	margin: 0 auto;
	padding: 1.4rem 1.5rem;
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 1rem;
	text-align: center;
}
.stat-number {
	display: block;
	font-family: 'Fraunces', serif;
	font-size: 2rem;
	font-weight: 700;
	color: var(--uz-gold);
	line-height: 1.1;
}
.stat-label {
	font-size: 0.8rem;
	text-transform: uppercase;
	letter-spacing: 0.1em;
	color: rgba(255,255,255,0.7);
}
@media (max-width: 600px) {
	.hp-stats-inner { grid-template-columns: repeat(2, 1fr); }
}

/* ─── Section tabs ─────────────────────────────────── */
.hp-tabs {
	background: #fff;
	border-bottom: 2px solid var(--line);
	display: flex;
	justify-content: center;
	overflow-x: auto;
	-webkit-overflow-scrolling: touch;
	gap: 0;
	position: sticky;
	top: var(--site-header-height);
	z-index: 25;
	box-shadow: 0 10px 22px rgba(27, 43, 78, 0.08);
}
.hp-tab {
	padding: 1rem 1.5rem;
	font-weight: 700;
	font-size: 0.88rem;
	text-transform: uppercase;
	letter-spacing: 0.06em;
	color: var(--ink-soft);
	white-space: nowrap;
	border-bottom: 3px solid transparent;
	margin-bottom: -2px;
	transition: color 0.15s, border-color 0.15s;
	background: none;
	border-top: none;
	border-left: none;
	border-right: none;
	cursor: pointer;
	font-family: inherit;
}
.hp-tab:hover, .hp-tab-active {
	color: var(--uz-orange);
	border-bottom-color: var(--uz-orange);
}

/* ─── Generic section ──────────────────────────────── */
.hp-section { padding: 3.5rem 1.5rem; scroll-margin-top: calc(var(--site-header-height) + 64px); }
.hp-section-alt { background: var(--uz-orange-light); }
.hp-section-inner { max-width: 1100px; margin: 0 auto; }
.hp-section-header {
	display: flex;
	align-items: baseline;
	justify-content: space-between;
	flex-wrap: wrap;
	gap: 0.5rem;
	margin-bottom: 2rem;
	border-bottom: 3px solid var(--uz-gold);
	padding-bottom: 0.75rem;
}
.hp-section-title {
	font-family: 'Fraunces', serif;
	font-size: clamp(1.4rem, 3vw, 1.9rem);
	margin: 0;
	color: var(--uz-navy);
}
.hp-section-link {
	font-size: 0.88rem;
	font-weight: 700;
	color: var(--uz-orange);
	text-transform: uppercase;
	letter-spacing: 0.06em;
}
.hp-section-link:hover { text-decoration: underline; }
.hp-section-link-btn {
	background: none;
	border: none;
	padding: 0;
	cursor: pointer;
	font-family: inherit;
}

/* ─── Empty tab state ───────────────────────────────── */
.hp-empty-tab {
	color: var(--ink-soft);
	font-size: 0.95rem;
	padding: 2rem 0;
	margin: 0;
}

/* ─── Research cards ───────────────────────────────── */
.hp-card-wrap {
	display: block;
	text-decoration: none;
}
.hp-cards {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 1.2rem;
}
.hp-card {
	background: var(--uz-navy);
	border: none;
	border-radius: 0;
	padding: 2rem;
	box-shadow: 0 4px 16px rgba(27,43,78,0.15);
	transition: transform 0.18s, box-shadow 0.18s;
	position: relative;
	overflow: hidden;
	background-size: cover;
	background-position: center;
	min-height: 340px;
}
.hp-card::before {
	content: '';
	position: absolute;
	inset: 0;
	background: linear-gradient(to bottom, rgba(10,20,45,0.45) 0%, rgba(10,20,45,0.88) 100%);
	z-index: 0;
}
.hp-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 16px 36px rgba(27,43,78,0.28);
}
.hp-card-featured {
	border-left: 4px solid var(--uz-gold);
}
.hp-card-inner {
	position: relative;
	z-index: 1;
	height: 100%;
	display: flex;
	flex-direction: column;
}
.hp-card-body {
	margin-top: auto;
}
.hp-card-tag {
	display: inline-block;
	background: rgba(255,255,255,0.15);
	color: #fff;
	border: 1px solid rgba(255,255,255,0.3);
	backdrop-filter: blur(4px);
	font-size: 0.74rem;
	font-weight: 700;
	text-transform: uppercase;
	letter-spacing: 0.08em;
	padding: 0.2rem 0.55rem;
	border-radius: 100px;
	margin-bottom: 0.6rem;
	align-self: flex-start;
}
.hp-card h3 {
	font-family: 'Fraunces', serif;
	font-size: 1.05rem;
	margin: 0 0 0.5rem;
	line-height: 1.3;
	color: #fff;
}
.hp-card p {
	margin: 0 0 0.75rem;
	font-size: 0.9rem;
	color: rgba(255,255,255,0.82);
	line-height: 1.5;
}
.hp-card-meta {
	font-size: 0.78rem;
	font-weight: 600;
	color: rgba(255,255,255,0.7);
	text-transform: uppercase;
	letter-spacing: 0.06em;
}

/* ─── Alumni news ──────────────────────────────────── */
.hp-news-list {
	display: flex;
	flex-direction: column;
	gap: 0;
}
.hp-news-item {
	display: grid;
	grid-template-columns: 110px 1fr;
	gap: 1.2rem;
	padding: 1.4rem 0;
	border-bottom: 1px solid var(--line);
}
.hp-news-item:last-child { border-bottom: none; }
.hp-news-date {
	font-size: 0.78rem;
	font-weight: 700;
	text-transform: uppercase;
	letter-spacing: 0.08em;
	color: var(--uz-orange);
	padding-top: 0.2rem;
}
.hp-news-body h3 {
	font-family: 'Fraunces', serif;
	font-size: 1.05rem;
	margin: 0 0 0.4rem;
	color: var(--ink);
}
.hp-news-body p {
	margin: 0;
	font-size: 0.92rem;
	color: var(--ink-soft);
	line-height: 1.55;
}
@media (max-width: 500px) {
	.hp-news-item { grid-template-columns: 1fr; gap: 0.3rem; }
}

/* ─── Events ───────────────────────────────────────── */
.hp-events {
	display: flex;
	flex-direction: column;
	gap: 1rem;
}
.hp-event {
	display: grid;
	grid-template-columns: 72px 1fr;
	gap: 1.2rem;
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 14px;
	padding: 1.2rem 1.4rem;
	box-shadow: 0 2px 10px rgba(27,43,78,0.06);
}
.hp-event-date {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background: var(--uz-navy);
	color: #fff;
	border-radius: 10px;
	padding: 0.5rem;
	text-align: center;
	flex-shrink: 0;
}
.ev-day {
	font-family: 'Fraunces', serif;
	font-size: 1.6rem;
	font-weight: 700;
	line-height: 1;
}
.ev-month {
	font-size: 0.7rem;
	text-transform: uppercase;
	letter-spacing: 0.1em;
	font-weight: 700;
}
.hp-event-body h3 {
	font-family: 'Fraunces', serif;
	font-size: 1rem;
	margin: 0.3rem 0 0.35rem;
	color: var(--ink);
}
.hp-event-body p {
	margin: 0 0 0.4rem;
	font-size: 0.88rem;
	color: var(--ink-soft);
	line-height: 1.5;
}
.hp-event-loc {
	font-size: 0.8rem;
	color: var(--ink-soft);
	font-weight: 600;
}

/* ─── Competitions ─────────────────────────────────── */
.hp-comp-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 1.2rem;
}
.hp-comp-card {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 0;
	padding: 2rem;
	box-shadow: 0 4px 16px rgba(27,43,78,0.07);
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
}
.hp-comp-badge {
	display: inline-block;
	font-size: 0.7rem;
	font-weight: 800;
	text-transform: uppercase;
	letter-spacing: 0.1em;
	padding: 0.2rem 0.6rem;
	border-radius: 100px;
	align-self: flex-start;
}
.hp-badge-open { background: #e0f0ff; color: #1a3a6b; }
.hp-badge-upcoming { background: #fff3dc; color: #8a5a00; }
.hp-comp-card h3 {
	font-family: 'Fraunces', serif;
	font-size: 1.05rem;
	margin: 0;
	color: var(--ink);
}
.hp-comp-card p {
	margin: 0;
	font-size: 0.88rem;
	color: var(--ink-soft);
	line-height: 1.5;
	flex: 1;
}
.hp-comp-footer {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 0.5rem;
	margin-top: 0.5rem;
	padding-top: 0.75rem;
	border-top: 1px solid var(--line);
}
.hp-comp-deadline {
	font-size: 0.78rem;
	font-weight: 700;
	color: var(--ink-soft);
}
.hp-comp-cta {
	font-size: 0.8rem;
	font-weight: 700;
	color: var(--uz-orange);
	text-transform: uppercase;
	letter-spacing: 0.06em;
}
.hp-comp-cta:hover { text-decoration: underline; }

/* ─── funding-opportunities ───────────────────────────────────────── */
.hp-grant-list {
	display: flex;
	flex-direction: column;
	gap: 1.1rem;
}
.hp-fallback-card {
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
	background: #faf9f6;
	border: 1px solid #e8e4dc;
	border-left: 4px solid var(--uz-orange);
	padding: 1.5rem 1.6rem;
	color: var(--ink);
	text-decoration: none;
	box-shadow: 0 2px 6px rgba(27, 43, 78, 0.06);
	transition: border-color 0.15s, box-shadow 0.15s, transform 0.15s;
}
.hp-fallback-card:hover {
	border-color: #d8cfc1;
	box-shadow: 0 8px 22px rgba(27, 43, 78, 0.1);
	transform: translateY(-1px);
}
.hp-fallback-eyebrow {
	font-size: 0.72rem;
	font-weight: 800;
	text-transform: uppercase;
	letter-spacing: 0.08em;
	color: var(--uz-orange-dark);
}
.hp-fallback-card strong {
	font-family: 'Fraunces', serif;
	font-size: 1.2rem;
	color: var(--uz-navy);
}
.hp-fallback-card span:not(.hp-fallback-eyebrow):not(.hp-fallback-action) {
	font-size: 0.92rem;
	line-height: 1.55;
	color: var(--ink-soft);
}
.hp-fallback-action {
	margin-top: 0.25rem;
	font-size: 0.82rem;
	font-weight: 800;
	text-transform: uppercase;
	letter-spacing: 0.06em;
	color: var(--uz-orange);
}
.hp-grant-item {
	position: relative;
	display: grid;
	grid-template-columns: minmax(0, 1fr) auto 220px;
	gap: 0;
	align-items: stretch;
	background: #faf9f6;
	border: 1px solid #e8e4dc;
	overflow: hidden;
	box-shadow: 0 2px 6px rgba(27, 43, 78, 0.06);
}
.hp-grant-featured {
	border-color: #c9621a;
}
/* Red/orange accent line top-left */
.hp-grant-accent {
	position: absolute;
	top: 0;
	left: 0;
	width: 38px;
	height: 4px;
	background: var(--uz-orange);
}
/* Featured diagonal ribbon */
.hp-grant-ribbon {
	position: absolute;
	top: 14px;
	right: -28px;
	width: 110px;
	text-align: center;
	background: var(--uz-navy);
	color: #fff;
	font-size: 0.62rem;
	font-weight: 800;
	text-transform: uppercase;
	letter-spacing: 0.1em;
	padding: 0.28rem 0;
	transform: rotate(45deg);
	z-index: 1;
}
.hp-grant-left {
	padding: 1.6rem 1.6rem 1.5rem;
	display: flex;
	flex-direction: column;
	gap: 0.4rem;
	min-width: 0;
}
.hp-grant-funder {
	margin: 0;
	font-size: 0.82rem;
	color: var(--ink-soft);
	font-weight: 400;
	overflow-wrap: break-word;
}
.hp-grant-desc {
	margin: 0.35rem 0 0;
	font-size: 0.86rem;
	color: var(--ink-soft);
	line-height: 1.55;
	overflow-wrap: break-word;
}
.hp-grant-title {
	margin: 0;
	font-family: 'Fraunces', serif;
	font-size: 1.05rem;
	font-weight: 700;
	color: var(--uz-navy);
	line-height: 1.35;
	overflow-wrap: break-word;
}
/* Vertical divider */
.hp-grant-divider {
	width: 1px;
	background: #e0dbd2;
	margin: 1.2rem 0;
}
.hp-grant-right {
	padding: 1.6rem 1.5rem;
	display: flex;
	flex-direction: column;
	gap: 1rem;
	justify-content: center;
	min-width: 0;
	width: 220px;
	flex-shrink: 0;
}
.hp-grant-meta-item {
	display: flex;
	flex-direction: column;
	gap: 0.15rem;
}
.hp-grant-meta-label {
	font-size: 0.75rem;
	font-weight: 600;
	text-transform: uppercase;
	letter-spacing: 0.07em;
	color: var(--ink-soft);
}
.hp-grant-meta-value {
	font-size: 0.92rem;
	color: var(--ink);
	font-weight: 500;
}
.hp-grant-more {
	display: flex;
	justify-content: center;
	margin-top: 2rem;
}
.hp-grant-more-btn {
	display: inline-flex;
	align-items: center;
	gap: 0.5rem;
	padding: 0.75rem 1.8rem;
	background: var(--uz-orange);
	color: #fff;
	border: none;
	border-radius: 999px;
	font-size: 0.9rem;
	font-weight: 700;
	font-family: inherit;
	text-transform: uppercase;
	letter-spacing: 0.05em;
	cursor: pointer;
	transition: background 0.15s;
}
.hp-grant-more-btn:hover { background: var(--uz-orange-dark); }
.hp-rp-status-badge {
	display: inline-block;
	background: #d4f0e0;
	color: #1a6b3a;
	font-size: 0.72rem;
	font-weight: 800;
	text-transform: uppercase;
	letter-spacing: 0.1em;
	padding: 0.2rem 0.6rem;
	border-radius: 100px;
}
.hp-rp-pills {
	display: flex;
	flex-wrap: wrap;
	gap: 0.45rem;
	margin-top: 0.9rem;
}
.hp-rp-pill {
	display: inline-block;
	font-size: 0.72rem;
	font-weight: 700;
	padding: 0.22rem 0.7rem;
	border-radius: 100px;
	white-space: nowrap;
}
.hp-rp-pill-uni {
	background: var(--uz-navy);
	color: #fff;
	letter-spacing: 0.08em;
	text-transform: uppercase;
}
.hp-rp-pill-faculty {
	background: #eef2fb;
	color: var(--uz-navy);
	border: 1px solid #c8d3ef;
}
.hp-rp-pill-manager {
	background: #fff6e8;
	color: #7a4800;
	border: 1px solid #f0d8a8;
}
@media (max-width: 700px) {
	.hp-tabs {
		justify-content: flex-start;
		scrollbar-width: none;
	}
	.hp-tabs::-webkit-scrollbar {
		display: none;
	}
	.hp-tab {
		padding: 0.9rem 1.05rem;
		font-size: 0.78rem;
		letter-spacing: 0.05em;
	}
	.hp-grant-item {
		grid-template-columns: 1fr;
	}
	.hp-grant-divider {
		width: auto;
		height: 1px;
		margin: 0 1.5rem;
	}
	.hp-grant-right {
		padding-top: 0.75rem;
		padding-bottom: 1.4rem;
		flex-direction: row;
		gap: 1.5rem;
	}
	.hp-grant-ribbon {
		top: 10px;
		right: -26px;
		width: 100px;
	}
}

/* ─── CTA band ─────────────────────────────────────── */
.hp-cta-band {
	background: var(--uz-navy);
	color: #fff;
	padding: 3.5rem 1.5rem;
	text-align: center;
}
.hp-cta-band-inner {
	max-width: 660px;
	margin: 0 auto;
}
.hp-cta-band h2 {
	font-family: 'Fraunces', serif;
	font-size: clamp(1.5rem, 3.5vw, 2.2rem);
	margin: 0 0 0.75rem;
}
.hp-cta-band p {
	margin: 0 0 1.5rem;
	font-size: 1rem;
	color: rgba(255,255,255,0.85);
	line-height: 1.6;
}
.hp-cta-band .hp-ctas {
	justify-content: center;
}
</style>
