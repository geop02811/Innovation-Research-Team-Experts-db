<script lang="ts">
	import { onMount } from 'svelte';

	const heroImages = [
		'/sophia.jpeg',
		'/Sophia_at_the_AI_for_Good_Global_Summit_2018_(27254369347)_(cropped).jpg'
	];

	let currentSlide = $state(0);
	let activeTab = $state<'research' | 'alumni' | 'events' | 'competitions'>('research');

	onMount(() => {
		const id = setInterval(() => {
			currentSlide = (currentSlide + 1) % heroImages.length;
		}, 5000);
		return () => clearInterval(id);
	});
</script>

<svelte:head>
	<title>Home | University of Zimbabwe Experts</title>
	<meta
		name="description"
		content="University of Zimbabwe experts platform for scholar discovery, research, and profile exploration."
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
		<p class="hp-kicker">University of Zimbabwe — Experts &amp; Research</p>
		<h1 class="hp-title">Discover UZ's Leading Researchers &amp; Scholars</h1>
		<p class="hp-subtitle">
			Explore the University of Zimbabwe's community of academics, innovators, and industry consultants.
			Connect with experts driving research, policy, and development across Africa.
		</p>
		<div class="hp-ctas">
			<a class="hp-btn-primary" href="/experts">Browse Experts</a>
			<a class="hp-btn-secondary" href="/signup">Join as an Expert</a>
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
			<span class="stat-number">500+</span>
			<span class="stat-label">Academic Staff</span>
		</div>
		<div class="stat-item">
			<span class="stat-number">12</span>
			<span class="stat-label">Faculties</span>
		</div>
		<div class="stat-item">
			<span class="stat-number">80+</span>
			<span class="stat-label">Research Areas</span>
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
	<button class="hp-tab" class:hp-tab-active={activeTab === 'events'} onclick={() => activeTab = 'events'}>Events</button>
	<button class="hp-tab" class:hp-tab-active={activeTab === 'competitions'} onclick={() => activeTab = 'competitions'}>Competitions</button>
</nav>

<!-- ══════════════════════════════════════ RESEARCH HIGHLIGHTS ═══ -->
{#if activeTab === 'research'}
<section class="hp-section" id="research">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Research Highlights</h2>
			<a class="hp-section-link" href="/experts">View All Experts →</a>
		</div>
		<div class="hp-cards">
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
		<div class="hp-news-list">
			<article class="hp-news-item">
				<div class="hp-news-date">March 2026</div>
				<div class="hp-news-body">
					<h3>Dr. Tatenda Mawere appointed WHO Regional Health Adviser</h3>
					<p>UZ Medical graduate Dr. Mawere (Class of 2008) has been appointed as WHO's Regional Health Adviser for Eastern &amp; Southern Africa, overseeing public health policy for 14 nations.</p>
				</div>
			</article>
			<article class="hp-news-item">
				<div class="hp-news-date">February 2026</div>
				<div class="hp-news-body">
					<h3>Eng. Rudo Chikwanda leads cross-border renewable energy project</h3>
					<p>UZ Engineering alumna Eng. Chikwanda (Class of 2012) is spearheading a $45M solar grid interconnect project linking Zimbabwe, Zambia, and Mozambique.</p>
				</div>
			</article>
			<article class="hp-news-item">
				<div class="hp-news-date">January 2026</div>
				<div class="hp-news-body">
					<h3>Prof. Blessing Murove elected Fellow of the Royal Society</h3>
					<p>Professor Murove, a UZ Physics graduate and now Professor at UCT, has been elected Fellow of the Royal Society for contributions to quantum materials research.</p>
				</div>
			</article>
		</div>
	</div>
</section>
{/if}

<!-- ═══════════════════════════════════════════ EVENTS ═══ -->
{#if activeTab === 'events'}
<section class="hp-section" id="events">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Upcoming Events</h2>
		</div>
		<div class="hp-events">
			<article class="hp-event">
				<div class="hp-event-date">
					<span class="ev-day">15</span>
					<span class="ev-month">Apr</span>
				</div>
				<div class="hp-event-body">
					<span class="hp-card-tag">Seminar</span>
					<h3>Annual UZ Research Symposium 2026</h3>
					<p>A two-day symposium showcasing cutting-edge research from all twelve faculties. Open to staff, students, and industry partners.</p>
					<span class="hp-event-loc">📍 Senate Chamber, UZ Main Campus</span>
				</div>
			</article>
			<article class="hp-event">
				<div class="hp-event-date">
					<span class="ev-day">22</span>
					<span class="ev-month">Apr</span>
				</div>
				<div class="hp-event-body">
					<span class="hp-card-tag">Workshop</span>
					<h3>Postgraduate Research Methods Workshop</h3>
					<p>Intensive training in quantitative and qualitative research methodologies for PhD and Masters students.</p>
					<span class="hp-event-loc">📍 Faculty of Social Sciences, Room 204</span>
				</div>
			</article>
			<article class="hp-event">
				<div class="hp-event-date">
					<span class="ev-day">30</span>
					<span class="ev-month">Apr</span>
				</div>
				<div class="hp-event-body">
					<span class="hp-card-tag">Public Lecture</span>
					<h3>Innovation &amp; Entrepreneurship in Africa</h3>
					<p>Distinguished lecture by Prof. Tsitsi Dangarembga on bridging the gap between academic innovation and commercial enterprise.</p>
					<span class="hp-event-loc">📍 Great Hall, UZ Main Campus — Free Entry</span>
				</div>
			</article>
		</div>
	</div>
</section>
{/if}

<!-- ══════════════════════════════════ COMPETITIONS ═══ -->
{#if activeTab === 'competitions'}
<section class="hp-section hp-section-alt" id="competitions">
	<div class="hp-section-inner">
		<div class="hp-section-header">
			<h2 class="hp-section-title">Competitions &amp; Calls for Proposals</h2>
		</div>
		<div class="hp-comp-grid">
			<article class="hp-comp-card">
				<div class="hp-comp-badge hp-badge-open">Open</div>
				<h3>UZ Innovation Grant 2026</h3>
				<p>Funding up to USD 20,000 for applied research projects with demonstrable societal impact in Zimbabwe. Open to all academic staff.</p>
				<div class="hp-comp-footer">
					<span class="hp-comp-deadline">⏳ Deadline: 30 April 2026</span>
					<a class="hp-comp-cta" href="/experts">Find Collaborators</a>
				</div>
			</article>
			<article class="hp-comp-card">
				<div class="hp-comp-badge hp-badge-open">Open</div>
				<h3>SADC Research Excellence Awards</h3>
				<p>Annual awards recognising outstanding research contributions by academics from SADC member states. Nominations open to all UZ faculties.</p>
				<div class="hp-comp-footer">
					<span class="hp-comp-deadline">⏳ Deadline: 15 May 2026</span>
					<a class="hp-comp-cta" href="/experts">Find Collaborators</a>
				</div>
			</article>
			<article class="hp-comp-card">
				<div class="hp-comp-badge hp-badge-upcoming">Upcoming</div>
				<h3>UZ Student Entrepreneurship Challenge</h3>
				<p>Teams of final-year students pitch solutions to real Zimbabwean development challenges. Prizes include mentorship and seed funding.</p>
				<div class="hp-comp-footer">
					<span class="hp-comp-deadline">⏳ Opens: 1 June 2026</span>
					<a class="hp-comp-cta" href="/experts">Find a Supervisor</a>
				</div>
			</article>
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
			<a class="hp-btn-outline" href="/experts">Explore Experts</a>
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
	gap: 0;
	position: sticky;
	top: 81px;
	z-index: 20;
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
.hp-section { padding: 3.5rem 1.5rem; scroll-margin-top: 140px; }
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

/* ─── Research cards ───────────────────────────────── */
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
