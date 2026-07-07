<script lang="ts">
	import ProfileTabs from './ProfileTabs.svelte';
	import type { Scholar } from '$lib/types/scholar';

	let { scholar }: { scholar: Scholar } = $props();

	const experienceDateRange = (experience: NonNullable<Scholar['professionalExperiences']>[number]) => {
		const start = [experience.startMonth, experience.startYear].filter(Boolean).join(' ');
		const end = experience.isCurrent
			? 'Present'
			: [experience.endMonth, experience.endYear].filter(Boolean).join(' ');
		return [start, end].filter(Boolean).join(' - ');
	};
</script>

<article class="profile-shell">

	<nav class="profile-breadcrumb">
		<a href="/experts">← {scholar.faculty || 'Faculty'}</a>
		<span>/ People /</span>
		<span>{scholar.fullName?.toUpperCase()}</span>
	</nav>

	<!-- Full-width navy banner -->
	<header class="profile-banner">
		<div class="banner-photo">
			<img src={scholar.avatarUrl} alt={scholar.fullName} loading="eager" />
		</div>
		<div class="banner-info">
			<h1>
				{scholar.fullName}
				{#if scholar.qualifications}<span class="quals">{scholar.qualifications}</span>{/if}
			</h1>
			<div class="accent-bar"></div>
			{#if scholar.role}<p class="role-badge">{scholar.role}</p>{/if}
			{#if scholar.headline}<p class="banner-headline">{scholar.headline}</p>{/if}
			{#if scholar.faculty}<p class="banner-faculty">Faculty of {scholar.faculty}</p>{/if}
		</div>
	</header>

	<!-- Contact section — indented to clear the hanging photo -->
	<section class="profile-meta">
		<div class="meta-links">
			{#each scholar.links as link}
				<a class="meta-link-item" href={link.url} target="_blank" rel="noreferrer">{link.label}</a>
			{/each}
		</div>
		<div class="contact-grid">
			{#if scholar.email}
				<div class="contact-cell">
					<span class="contact-label">Email:</span>
					<p>{scholar.email}</p>
				</div>
			{/if}
			{#if scholar.college}
				<div class="contact-cell">
					<span class="contact-label">College:</span>
					<p>{scholar.college}</p>
				</div>
			{/if}
			{#if scholar.phone}
				<div class="contact-cell">
					<span class="contact-label">Tel:</span>
					<p>{scholar.phone}</p>
				</div>
			{/if}
			{#if scholar.location}
				<div class="contact-cell">
					<span class="contact-label">Location:</span>
					<p>{scholar.location}</p>
				</div>
			{/if}
		</div>
	</section>

	<!-- Body: sidebar + tabs -->
	<div class="profile-body">
		<aside class="profile-aside">
			{#if scholar.researchAreas?.length}
				<h3 class="aside-heading">Areas of Research</h3>
				<ul>
					{#each scholar.researchAreas as area}<li>{area}</li>{/each}
				</ul>
			{/if}
			{#if scholar.areasOfExpertise?.length}
				<h3 class="aside-heading">Areas of Expertise</h3>
				<ul>
					{#each scholar.areasOfExpertise as area}<li>{area}</li>{/each}
				</ul>
			{/if}
			{#if scholar.industrialAreasOfExpertise?.length}
				<h3 class="aside-heading">Industrial Areas</h3>
				<ul>
					{#each scholar.industrialAreasOfExpertise as area}<li>{area}</li>{/each}
				</ul>
			{/if}
			{#if scholar.languagesSpoken?.length}
				<h3 class="aside-heading">Languages</h3>
				<ul>
					{#each scholar.languageProficiencies?.length ? scholar.languageProficiencies : scholar.languagesSpoken.map((language) => ({ language, proficiency: '' })) as item}
						<li>{item.language}{item.proficiency ? ` · ${item.proficiency}` : ''}</li>
					{/each}
				</ul>
			{/if}
		</aside>

		<section class="profile-sections">
			{#if scholar.professionalExperiences?.length}
				<section class="experience-section">
					<h2>Experience</h2>
					<div class="experience-grid">
						{#each scholar.professionalExperiences as experience}
							<article class="experience-card">
								<h3>{experience.title || 'Experience'}</h3>
								<p class="experience-meta">
									{#if experience.organization}{experience.organization}{/if}
									{#if experience.employmentType} · {experience.employmentType}{/if}
								</p>
								{#if experienceDateRange(experience)}
									<p class="experience-meta">{experienceDateRange(experience)}</p>
								{/if}
								{#if experience.location || experience.locationType}
									<p class="experience-meta">
										{[experience.location, experience.locationType].filter(Boolean).join(' · ')}
									</p>
								{/if}
								<p>{experience.summary}</p>
							</article>
						{/each}
					</div>
				</section>
			{/if}
			<ProfileTabs sections={scholar.sections} />
		</section>
	</div>

</article>

<style>
	/* ── Shell ── */
	.profile-shell {
		max-width: 1200px;
		margin: 0 auto;
		background: #fff;
		overflow-x: hidden;
	}

	/* ── Breadcrumb ── */
	.profile-breadcrumb {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		gap: 0.4rem;
		padding: 0.75rem 1rem;
		font-size: 0.78rem;
		font-weight: 700;
		letter-spacing: 0.08em;
		text-transform: uppercase;
		color: #59606f;
		border-bottom: 1px solid var(--line);
	}
	.profile-breadcrumb a { color: inherit; text-decoration: none; }
	.profile-breadcrumb a:hover { text-decoration: underline; }

	/* ── Banner ── */
	.profile-banner {
		background: var(--uz-navy, #1b2b4e);
		border-bottom: 4px solid var(--uz-orange, #e87722);
		display: flex;
		align-items: flex-end;
		gap: 2.5rem;
		padding: 2rem 2rem 0;
	}

	/* Photo hangs below the banner */
	.banner-photo {
		flex-shrink: 0;
		position: relative;
		z-index: 2;
	}
	.banner-photo img {
		display: block;
		width: 152px;
		height: 152px;
		aspect-ratio: 1 / 1;
		border-radius: 50%;
		object-fit: cover;
		margin-bottom: -3rem;
		border: 4px solid #fff;
		box-shadow: 0 8px 28px rgba(0,0,0,0.35);
	}

	/* Text info in banner */
	.banner-info {
		flex: 1;
		min-width: 0;
		color: #fff;
		padding-bottom: 1.8rem;
	}
	.banner-info h1 {
		font-family: 'Fraunces', serif;
		font-size: clamp(1.6rem, 4.5vw, 3.8rem);
		line-height: 1.1;
		margin: 0 0 0.4rem;
		color: #fff;
		word-break: break-word;
	}
	.quals {
		font-size: clamp(1rem, 1.8vw, 1.5rem);
		font-style: italic;
		font-weight: 500;
		color: rgba(255,255,255,0.85);
		margin-left: 0.5rem;
		vertical-align: baseline;
	}
	.accent-bar {
		width: 2.5rem;
		height: 3px;
		background: var(--uz-orange, #e87722);
		margin: 0.6rem 0 1rem;
	}
	.role-badge {
		display: inline-block;
		background: rgba(255,255,255,0.14);
		border: 1px solid rgba(255,255,255,0.25);
		color: #fff;
		font-size: 0.78rem;
		font-weight: 700;
		letter-spacing: 0.14em;
		text-transform: uppercase;
		padding: 0.3rem 0.8rem;
		margin-bottom: 0.8rem;
	}
	.banner-headline {
		font-size: clamp(1rem, 1.6vw, 1.3rem);
		font-weight: 600;
		color: #fff;
		margin: 0;
	}
	.banner-faculty {
		font-size: 0.95rem;
		color: rgba(255,255,255,0.7);
		margin: 0.3rem 0 0;
	}

	/* ── Meta / contact section ── */
	.profile-meta {
		display: flex;
		gap: 2rem;
		align-items: flex-start;
		padding: calc(3.5rem + 1.5rem) 2rem 1.5rem;
		border-bottom: 1px solid var(--line);
	}
	.meta-links {
		flex-shrink: 0;
		width: 220px;
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}
	.meta-link-item {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		min-height: 38px;
		background: var(--uz-orange, #e87722);
		color: #fff;
		font-size: 0.75rem;
		font-weight: 700;
		text-decoration: none;
		border-radius: 8px;
		padding: 0.45rem 0.65rem;
		text-align: center;
	}
	.contact-grid {
		flex: 1;
		min-width: 0;
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 1.2rem 2rem;
	}
	.contact-label {
		display: block;
		font-size: 0.72rem;
		font-weight: 700;
		letter-spacing: 0.1em;
		text-transform: uppercase;
		color: var(--uz-orange, #e87722);
		margin-bottom: 0.25rem;
	}
	.contact-cell p {
		margin: 0;
		font-size: 0.95rem;
		color: var(--ink, #1f2a44);
		word-break: break-word;
	}

	/* ── Body ── */
	.profile-body {
		display: grid;
		grid-template-columns: 230px 1fr;
		gap: 3rem;
		padding: 2rem;
	}

	.profile-aside { border-right: 1px solid var(--line); padding-right: 2rem; }
	.aside-heading {
		font-size: 0.78rem;
		font-weight: 700;
		letter-spacing: 0.08em;
		text-transform: uppercase;
		color: var(--uz-navy, #1b2b4e);
		margin: 1.5rem 0 0.5rem;
		padding-bottom: 0.3rem;
		border-bottom: 2px solid var(--uz-orange, #e87722);
	}
	.profile-aside ul {
		list-style: none;
		padding: 0;
		margin: 0;
	}
	.profile-aside li {
		padding: 0.35rem 0;
		font-size: 0.88rem;
		color: #555;
		border-bottom: 1px solid var(--line);
	}
	.profile-sections {
		display: grid;
		gap: 2rem;
		min-width: 0;
	}
	.experience-section {
		display: grid;
		gap: 1rem;
	}
	.experience-section h2 {
		margin: 0;
		font-size: 1.25rem;
		color: var(--ink, #1f2a44);
	}
	.experience-grid {
		display: grid;
		gap: 1rem;
	}
	.experience-card {
		display: grid;
		gap: 0.35rem;
		border: 1px solid var(--line);
		border-radius: 8px;
		padding: 1rem;
		background: #fff;
	}
	.experience-card h3 {
		margin: 0;
		font-size: 1rem;
		color: var(--ink, #1f2a44);
	}
	.experience-card p {
		margin: 0;
		line-height: 1.6;
		color: #555;
	}
	.experience-card .experience-meta {
		font-size: 0.86rem;
		font-weight: 700;
		color: #6a7282;
	}

	/* ── Responsive ── */
	@media (max-width: 768px) {
		.profile-banner {
			flex-direction: column;
			align-items: flex-start;
			padding: 1.5rem 1rem 1.5rem;
			gap: 1rem;
		}
		.banner-photo img {
			margin-bottom: 0;
			width: 120px;
		}
		.profile-meta {
			flex-direction: column;
			padding: 1.5rem 1rem;
		}
		.meta-links { width: auto; flex-direction: row; }
		.contact-grid { grid-template-columns: 1fr 1fr; }
		.profile-body {
			grid-template-columns: 1fr;
			padding: 1rem;
		}
		.profile-aside {
			border-right: none;
			padding-right: 0;
			border-bottom: 1px solid var(--line);
			padding-bottom: 1.5rem;
		}
	}

	@media (max-width: 480px) {
		.profile-breadcrumb { padding: 0.6rem 0.75rem; font-size: 0.72rem; }
		.banner-info h1 { font-size: 1.5rem; }
		.contact-grid { grid-template-columns: 1fr; }
		.profile-meta { padding: 1rem 0.75rem; }
		.profile-body { padding: 0.75rem; }
	}
</style>
