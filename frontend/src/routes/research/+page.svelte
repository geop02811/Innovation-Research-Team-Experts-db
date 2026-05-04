<svelte:head>
	<title>Research | University of Zimbabwe Experts</title>
	<meta
		name="description"
		content="Explore cutting-edge research at the University of Zimbabwe — from biomedical engineering to climate science, arts, and AI."
	/>
</svelte:head>

<!-- ── Page shell ── -->
<div class="rp-shell">

	<!-- Rotated side label (MIT-style) -->
	<div class="rp-side-label" aria-hidden="true">Research</div>

	<!-- ── Intro section ── -->
	<section class="rp-intro">
		<div class="rp-intro-inner">
			<div class="rp-intro-text">
				<p class="rp-breadcrumb"><a href="/">Home</a> / Research</p>
				<h1 class="rp-intro-headline">
					At UZ, pushing the boundaries of knowledge and translating discovery into lasting impact for
					Zimbabwe and the continent is our defining purpose.
				</h1>
				<p class="rp-intro-body">
					Research flourishes across our <strong>11 faculties</strong> and dozens of centres, labs, and
					institutes that convene experts across disciplines to explore new intellectual frontiers and solve
					important societal problems. From solar-powered diagnostics in rural clinics to AI tools for
					indigenous languages, UZ researchers drive change where it matters most.
				</p>
				<a href="/experts" class="rp-cta">Browse Our Experts →</a>
			</div>
			<div class="rp-intro-media">
				<img src="/bio_med.webp" alt="UZ researcher at work" />
			</div>
		</div>
	</section>

	<!-- ── Research areas ── -->
	<section class="rp-areas">
		<div class="rp-section-inner">
			<div class="rp-section-header">
				<div class="rp-accent"></div>
				<h2>Research Areas</h2>
			</div>
			<div class="rp-areas-grid">
				{#each areas as area}
					<a href="/experts" class="rp-area-chip">{area}</a>
				{/each}
			</div>
		</div>
	</section>

	<!-- ── Research highlights ── -->
	<section class="rp-highlights">
		<div class="rp-section-inner">
			<div class="rp-section-header">
				<div class="rp-accent"></div>
				<h2>Research Highlights</h2>
			</div>

			<!-- Biomedical Engineering -->
			<article class="rp-article" id="biomedical">
				<div class="rp-article-media">
					<img src="/bio_med.webp" alt="Biomedical Engineering research" />
				</div>
				<div class="rp-article-body">
					<span class="rp-article-tag">Biomedical Engineering</span>
					<h3>Low-cost diagnostic devices for rural Zimbabwe</h3>
					<p>
						Prof. Chinyama's team in the Faculty of Engineering is developing solar-powered,
						point-of-care diagnostic tools designed for deployment across Zimbabwe's rural clinics.
						These devices require no external power grid and can diagnose malaria, typhoid, and
						anaemia within minutes, dramatically reducing reliance on centralised laboratories and
						improving patient outcomes in under-resourced settings.
					</p>
					<p>
						The project has been piloted in Mashonaland Central and is now being scaled under a
						partnership with the Ministry of Health and Child Care.
					</p>
					<div class="rp-article-meta">
						<strong>Prof. T. Chinyama</strong> · Faculty of Engineering &amp; the Built Environment
					</div>
					<a href="/experts" class="rp-article-link">Find this Expert →</a>
				</div>
			</article>

			<!-- Climate Science -->
			<article class="rp-article rp-article-alt" id="climate">
				<div class="rp-article-body">
					<span class="rp-article-tag">Climate Science</span>
					<h3>Drought resilience &amp; agricultural adaptation in Southern Africa</h3>
					<p>
						Dr. Moyo's interdisciplinary team models rainfall variability, soil moisture retention, and
						crop stress indices across Zimbabwe's agro-ecological zones. The research produces
						actionable recommendations for smallholder farmers and informs national drought-response
						policy at the Ministry of Lands, Agriculture, Water and Rural Resettlement.
					</p>
					<p>
						The team collaborates with CGIAR and the African Climate Policy Centre, and contributes
						datasets to SADC's regional early-warning systems.
					</p>
					<div class="rp-article-meta">
						<strong>Dr. S. Moyo</strong> · Faculty of Agriculture, Environment &amp; Food Systems
					</div>
					<a href="/experts" class="rp-article-link">Find this Expert →</a>
				</div>
				<div class="rp-article-media">
					<img src="/climate.jpg" alt="Climate research in the field" />
				</div>
			</article>

			<!-- AI & Languages -->
			<article class="rp-article" id="ai-languages">
				<div class="rp-article-media">
					<img src="/computer_science.webp" alt="AI and NLP research" />
				</div>
				<div class="rp-article-body">
					<span class="rp-article-tag">Computer Science &amp; AI</span>
					<h3>AI for local-language NLP in Shona &amp; Ndebele</h3>
					<p>
						Dr. Dube's group at the Faculty of Science is building open training corpora and
						transformer-based language models for Shona and Ndebele — two of Zimbabwe's most widely
						spoken languages. The project addresses a critical gap in African AI infrastructure, where
						indigenous languages remain severely under-resourced.
					</p>
					<p>
						Downstream applications include voice assistants for health workers, educational chatbots,
						and real-time translation tools for public service delivery.
					</p>
					<div class="rp-article-meta">
						<strong>Dr. R. Dube</strong> · Faculty of Science
					</div>
					<a href="/experts" class="rp-article-link">Find this Expert →</a>
				</div>
			</article>

			<!-- Public Health -->
			<article class="rp-article rp-article-alt" id="public-health">
				<div class="rp-article-body">
					<span class="rp-article-tag">Public Health</span>
					<h3>Urban sanitation &amp; real-time water quality monitoring</h3>
					<p>
						Prof. Zvobgo's team has deployed a network of low-cost IoT sensors across Harare's
						peri-urban settlements to continuously monitor water quality parameters including turbidity,
						pH, and coliform levels. Data is transmitted in real time to a public dashboard accessible
						by residents and city health officials.
					</p>
					<p>
						The system has already identified three contamination events that would otherwise have gone
						undetected for weeks, enabling rapid public health response.
					</p>
					<div class="rp-article-meta">
						<strong>Prof. N. Zvobgo</strong> · Faculty of Medicine &amp; Health Sciences
					</div>
					<a href="/experts" class="rp-article-link">Find this Expert →</a>
				</div>
				<div class="rp-article-media">
					<img src="/public_health.webp" alt="Water quality monitoring research" />
				</div>
			</article>

			<!-- Arts & Heritage -->
			<article class="rp-article" id="arts">
				<div class="rp-article-media">
					<img src="/Arts.jpg" alt="Heritage digitisation project" />
				</div>
				<div class="rp-article-body">
					<span class="rp-article-tag">Arts &amp; Humanities</span>
					<h3>Preserving Zimbabwe's visual heritage through digital archiving</h3>
					<p>
						Dr. Mutasa leads a multi-year project digitising and cataloguing pre-colonial rock art,
						early-independence era paintings, and contemporary Zimbabwean artworks. The resulting
						open-access repository is freely available to researchers, educators, and the public
						worldwide.
					</p>
					<p>
						The project involves community partnerships with rural custodians of cultural sites and has
						trained over 40 local archivists in digital preservation methods.
					</p>
					<div class="rp-article-meta">
						<strong>Dr. A. Mutasa</strong> · Faculty of Arts &amp; Humanities
					</div>
					<a href="/experts" class="rp-article-link">Find this Expert →</a>
				</div>
			</article>

			<!-- Ethnomusicology -->
			<article class="rp-article rp-article-alt" id="music">
				<div class="rp-article-body">
					<span class="rp-article-tag">Music &amp; Ethnomusicology</span>
					<h3>The living traditions of Mbira: identity, spirituality, and contemporary practice</h3>
					<p>
						Prof. Nhongo's longitudinal ethnomusicological study documents how Mbira dzavadzimu
						practice evolves across urban and rural Zimbabwe, tracing its role in healing ceremonies,
						political expression, and cross-cultural diaspora identity. The research draws on over 300
						interviews and audio recordings gathered across five provinces.
					</p>
					<p>
						Findings are being used to inform UNESCO's intangible cultural heritage nomination process
						and a forthcoming curriculum for music schools.
					</p>
					<div class="rp-article-meta">
						<strong>Prof. C. Nhongo</strong> · Faculty of Arts &amp; Humanities
					</div>
					<a href="/experts" class="rp-article-link">Find this Expert →</a>
				</div>
				<div class="rp-article-media">
					<img src="/Music.jpg" alt="Mbira ethnomusicology research" />
				</div>
			</article>
		</div>
	</section>

	<!-- ── CTA band ── -->
	<section class="rp-cta-band">
		<div class="rp-cta-inner">
			<h2>Are you a UZ researcher?</h2>
			<p>Join the expert directory and be discoverable by collaborators, funders, and the global academic community.</p>
			<div class="rp-cta-btns">
				<a href="/signup" class="rp-btn-primary">Register Your Profile</a>
				<a href="/experts" class="rp-btn-outline">Explore All Experts</a>
			</div>
		</div>
	</section>
</div>

<script lang="ts">
	const areas = [
		'Biomedical Engineering', 'Climate Science', 'Artificial Intelligence', 'Public Health',
		'Agriculture & Food Systems', 'Water & Sanitation', 'Renewable Energy', 'Education',
		'Arts & Humanities', 'Ethnomusicology', 'Law & Governance', 'Economics',
		'Computer Science', 'Social Sciences', 'Veterinary Science', 'Pharmaceuticals'
	];
</script>

<style>
	/* ── Shell ── */
	.rp-shell {
		position: relative;
	}

	/* ── Rotated side label ── */
	.rp-side-label {
		position: fixed;
		left: 0;
		top: 50%;
		transform: translateY(-50%) rotate(-90deg);
		transform-origin: center center;
		font-size: 0.7rem;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.18em;
		color: #aaa;
		pointer-events: none;
		z-index: 10;
		white-space: nowrap;
	}

	@media (max-width: 900px) {
		.rp-side-label { display: none; }
	}

	/* ── Shared section styles ── */
	.rp-section-inner {
		max-width: 1100px;
		margin: 0 auto;
		padding: 0 2rem;
	}

	.rp-section-header {
		display: flex;
		align-items: center;
		gap: 1rem;
		margin-bottom: 2rem;
		padding-bottom: 0.75rem;
		border-bottom: 1px solid #e0e0e8;
	}

	.rp-accent {
		width: 40px;
		height: 4px;
		background: var(--uz-orange);
		flex-shrink: 0;
	}

	.rp-section-header h2 {
		font-family: 'Fraunces', serif;
		font-size: clamp(1.4rem, 2.5vw, 1.9rem);
		margin: 0;
		color: var(--uz-navy);
	}

	/* ── Intro ── */
	.rp-intro {
		background: #fff;
		border-bottom: 1px solid #e0e0e8;
		padding: 4rem 2rem 3.5rem;
	}

	.rp-intro-inner {
		max-width: 1100px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: 1fr 420px;
		gap: 3rem;
		align-items: center;
	}

	.rp-breadcrumb {
		font-size: 0.8rem;
		color: var(--ink-soft);
		margin: 0 0 1.25rem;
	}

	.rp-breadcrumb a {
		color: var(--ink-soft);
		text-decoration: none;
	}

	.rp-breadcrumb a:hover { text-decoration: underline; }

	.rp-intro-headline {
		font-family: 'Fraunces', serif;
		font-size: clamp(1.6rem, 3vw, 2.4rem);
		line-height: 1.2;
		color: var(--ink);
		margin: 0 0 1.25rem;
		font-weight: 700;
	}

	.rp-intro-body {
		font-size: 1rem;
		line-height: 1.7;
		color: var(--ink-soft);
		margin: 0 0 1.5rem;
	}

	.rp-cta {
		font-size: 0.9rem;
		font-weight: 700;
		color: var(--uz-orange);
		text-decoration: none;
		border-bottom: 2px solid var(--uz-orange);
		padding-bottom: 1px;
		transition: color 0.15s;
	}

	.rp-cta:hover { color: var(--uz-orange-dark); border-color: var(--uz-orange-dark); }

	.rp-intro-media img {
		width: 100%;
		height: 340px;
		object-fit: cover;
		border-radius: 4px;
	}

	/* ── Research areas ── */
	.rp-areas {
		padding: 3rem 0;
		background: var(--bg);
	}

	.rp-areas-grid {
		display: flex;
		flex-wrap: wrap;
		gap: 0.6rem;
	}

	.rp-area-chip {
		padding: 0.4rem 1rem;
		border: 1px solid #d0d0da;
		border-radius: 100px;
		font-size: 0.85rem;
		font-weight: 500;
		color: var(--ink);
		text-decoration: none;
		background: #fff;
		transition: border-color 0.15s, background 0.15s, color 0.15s;
	}

	.rp-area-chip:hover {
		border-color: var(--uz-orange);
		background: var(--uz-orange-light);
		color: var(--uz-orange-dark);
	}

	/* ── Highlights ── */
	.rp-highlights {
		padding: 4rem 0;
		background: #fff;
	}

	/* ── Individual article ── */
	.rp-article {
		display: grid;
		grid-template-columns: 420px 1fr;
		gap: 3rem;
		align-items: center;
		padding: 3rem 0;
		border-top: 1px solid #e8e8f0;
		scroll-margin-top: 90px; /* offset for sticky nav */
	}

	.rp-article:first-of-type {
		border-top: none;
	}

	.rp-article-alt {
		grid-template-columns: 1fr 420px;
	}

	.rp-article-alt .rp-article-body {
		order: 1;
	}

	.rp-article-alt .rp-article-media {
		order: 2;
	}

	.rp-article-media img {
		width: 100%;
		height: 300px;
		object-fit: cover;
		border-radius: 4px;
	}

	.rp-article-body {
		display: flex;
		flex-direction: column;
		gap: 0.75rem;
	}

	.rp-article-tag {
		display: inline-block;
		font-size: 0.72rem;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.1em;
		color: var(--uz-orange);
		align-self: flex-start;
	}

	.rp-article-body h3 {
		font-family: 'Fraunces', serif;
		font-size: clamp(1.2rem, 2vw, 1.6rem);
		margin: 0;
		color: var(--uz-navy);
		line-height: 1.25;
	}

	.rp-article-body p {
		margin: 0;
		font-size: 0.95rem;
		color: var(--ink-soft);
		line-height: 1.7;
	}

	.rp-article-meta {
		font-size: 0.82rem;
		color: var(--ink-soft);
		padding-top: 0.25rem;
	}

	.rp-article-link {
		align-self: flex-start;
		font-size: 0.88rem;
		font-weight: 700;
		color: var(--uz-orange);
		text-decoration: none;
		border-bottom: 1px solid var(--uz-orange);
		padding-bottom: 1px;
		margin-top: 0.25rem;
	}

	.rp-article-link:hover { color: var(--uz-orange-dark); border-color: var(--uz-orange-dark); }

	/* ── CTA band ── */
	.rp-cta-band {
		background: var(--uz-navy);
		color: #fff;
		padding: 3.5rem 2rem;
		text-align: center;
	}

	.rp-cta-inner {
		max-width: 660px;
		margin: 0 auto;
	}

	.rp-cta-band h2 {
		font-family: 'Fraunces', serif;
		font-size: clamp(1.5rem, 3vw, 2.2rem);
		margin: 0 0 0.75rem;
	}

	.rp-cta-band p {
		margin: 0 0 1.5rem;
		font-size: 1rem;
		color: rgba(255,255,255,0.85);
		line-height: 1.6;
	}

	.rp-cta-btns {
		display: flex;
		gap: 0.75rem;
		justify-content: center;
		flex-wrap: wrap;
	}

	.rp-btn-primary {
		background: var(--uz-orange);
		color: #fff;
		padding: 0.75rem 1.6rem;
		border-radius: 999px;
		font-weight: 800;
		font-size: 0.9rem;
		text-transform: uppercase;
		letter-spacing: 0.04em;
		text-decoration: none;
		transition: background 0.15s;
	}

	.rp-btn-primary:hover { background: var(--uz-orange-dark); }

	.rp-btn-outline {
		background: transparent;
		color: #fff;
		padding: 0.75rem 1.6rem;
		border-radius: 999px;
		font-weight: 700;
		font-size: 0.9rem;
		text-transform: uppercase;
		letter-spacing: 0.04em;
		border: 2px solid rgba(255,255,255,0.5);
		text-decoration: none;
		transition: background 0.15s;
	}

	.rp-btn-outline:hover { background: rgba(255,255,255,0.12); }

	/* ── Responsive ── */
	@media (max-width: 900px) {
		.rp-intro-inner,
		.rp-article,
		.rp-article-alt {
			grid-template-columns: 1fr;
		}

		.rp-intro-media { order: -1; }
		.rp-intro-media img { height: 220px; }

		.rp-article-alt .rp-article-body { order: 0; }
		.rp-article-alt .rp-article-media { order: 0; }

		.rp-article-media img { height: 220px; }

		.rp-intro { padding: 2rem 1rem; }
		.rp-section-inner { padding: 0 1rem; }
		.rp-article { gap: 1.5rem; padding: 2rem 0; }
	}
</style>
