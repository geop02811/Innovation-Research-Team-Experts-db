<script lang="ts">
	import type { Scholar } from '$lib/types/scholar';

	interface Props {
		scholar: Scholar;
	}

	let { scholar }: Props = $props();
	type TabId = 'bio' | 'skills' | 'experience' | 'profiles';
	let activeTab = $state<TabId>('bio');

	const tabs: { id: TabId; label: string }[] = [
		{ id: 'bio', label: 'Biography' },
		{ id: 'skills', label: 'Skills & Competence' },
		{ id: 'experience', label: 'Experience' },
		{ id: 'profiles', label: 'Profiles & Publications' }
	];

	const experienceDateRange = (experience: NonNullable<Scholar['professionalExperiences']>[number]) => {
		const start = [experience.startMonth, experience.startYear].filter(Boolean).join(' ');
		const end = experience.isCurrent
			? 'Present'
			: [experience.endMonth, experience.endYear].filter(Boolean).join(' ');
		return [start, end].filter(Boolean).join(' - ');
	};
</script>

<div class="profile-tabs-container">
	<nav class="profile-tabs" aria-label="Profile sections">
		{#each tabs as tab}
			<button
				type="button"
				class="tab-button"
				class:active={activeTab === tab.id}
				onclick={() => (activeTab = tab.id)}
				aria-pressed={activeTab === tab.id}
			>
				{tab.label}
			</button>
		{/each}
	</nav>

	<div class="tabs-content">
		{#if activeTab === 'bio'}
			<section class="tab-content active" role="tabpanel">
				<h2>Biography</h2>
				<p class="bio-copy">{scholar.bio || scholar.shortBio || 'No bio has been added yet.'}</p>
			</section>
		{:else if activeTab === 'skills'}
			<section class="tab-content active" role="tabpanel">
				<h2>Skills &amp; Competence</h2>
				<div class="section-grid">
					<div class="subsection-card">
						<h3>Areas of Expertise</h3>
						<div class="tag-list">
							{#each scholar.areasOfExpertise ?? [] as item}
								<span>{item}</span>
							{:else}
								<p>No areas added yet.</p>
							{/each}
						</div>
					</div>
					<div class="subsection-card">
						<h3>Skills</h3>
						<div class="tag-list">
							{#each scholar.sections.find((section) => section.title === 'Skills & Competences')?.body.split(' · ').filter(Boolean) ?? [] as item}
								<span>{item}</span>
							{:else}
								<p>No skills added yet.</p>
							{/each}
						</div>
					</div>
					<div class="subsection-card">
						<h3>Industrial Areas</h3>
						<div class="tag-list">
							{#each scholar.industrialAreasOfExpertise ?? [] as item}
								<span>{item}</span>
							{:else}
								<p>No industrial areas added yet.</p>
							{/each}
						</div>
					</div>
					<div class="subsection-card">
						<h3>Languages</h3>
						<div class="tag-list">
							{#each scholar.languageProficiencies?.length ? scholar.languageProficiencies : (scholar.languagesSpoken ?? []).map((language) => ({ language, proficiency: '' })) as item}
								<span>{item.language}{item.proficiency ? ` · ${item.proficiency}` : ''}</span>
							{:else}
								<p>No languages added yet.</p>
							{/each}
						</div>
					</div>
				</div>
			</section>
		{:else if activeTab === 'experience'}
			<section class="tab-content active" role="tabpanel">
				<h2>Experience</h2>
				<div class="experience-grid">
					{#each scholar.professionalExperiences ?? [] as experience}
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
							<p>{experience.summary || 'No summary added.'}</p>
						</article>
					{:else}
						<p>No experience has been added yet.</p>
					{/each}
				</div>
			</section>
		{:else if activeTab === 'profiles'}
			<section class="tab-content active" role="tabpanel">
				<h2>Profiles &amp; Publications</h2>
				<div class="link-grid">
					{#each scholar.links as link}
						<a class="profile-link-card" href={link.url} target="_blank" rel="noreferrer">
							<span>{link.label}</span>
							<small>{link.url}</small>
						</a>
					{:else}
						<p>No public profile or publication links have been added yet.</p>
					{/each}
				</div>
			</section>
		{/if}
	</div>
</div>

<style>
	.profile-tabs-container {
		display: flex;
		flex-direction: column;
		gap: 2rem;
	}

	.profile-tabs {
		display: flex;
		gap: 2.2rem;
		border-bottom: 1px solid #dfe5ef;
		overflow-x: auto;
	}

	.tab-button {
		position: relative;
		background: transparent;
		border: 0;
		border-radius: 0;
		padding: 1rem 0 1.05rem;
		font-size: clamp(1rem, 2vw, 1.45rem);
		font-weight: 800;
		color: var(--uz-navy, #1b2b4e);
		cursor: pointer;
		white-space: nowrap;
		transition:
			color 0.2s ease;
	}

	.tab-button::before {
		content: '';
		position: absolute;
		left: 0;
		right: 0;
		top: 0;
		height: 3px;
		background: transparent;
	}

	.tab-button:hover {
		color: var(--uz-orange, #e87722);
	}

	.tab-button.active {
		color: var(--uz-orange, #e87722);
	}

	.tab-button.active::before {
		background: var(--uz-orange, #e87722);
	}

	.tab-content {
		display: grid;
		gap: 1rem;
	}

	.tab-content h2 {
		margin: 0 0 1rem 0;
		font-size: 1.25rem;
		color: var(--ink, #1f2a44);
	}

	.tab-content p,
	.bio-copy {
		line-height: 1.6;
		color: #555;
		margin: 0;
	}

	.section-grid,
	.experience-grid,
	.link-grid {
		display: grid;
		gap: 1rem;
	}

	.section-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}

	.subsection-card,
	.experience-card,
	.profile-link-card {
		display: grid;
		gap: 0.5rem;
		border: 1px solid #dfe5ef;
		border-radius: 8px;
		padding: 1rem;
		background: #fff;
	}

	.subsection-card h3,
	.experience-card h3 {
		margin: 0;
		font-size: 1rem;
		color: var(--ink, #1f2a44);
	}

	.tag-list {
		display: flex;
		flex-wrap: wrap;
		gap: 0.45rem;
	}

	.tag-list span {
		border-radius: 999px;
		background: #f3f6fb;
		border: 1px solid #d9e0ec;
		padding: 0.35rem 0.65rem;
		font-size: 0.82rem;
		font-weight: 700;
		color: #41506a;
	}

	.experience-meta {
		font-size: 0.86rem;
		font-weight: 700;
		color: #6a7282;
	}

	.profile-link-card {
		color: inherit;
		text-decoration: none;
	}

	.profile-link-card:hover {
		border-color: var(--uz-orange, #e87722);
	}

	.profile-link-card span {
		font-weight: 800;
		color: var(--ink, #1f2a44);
	}

	.profile-link-card small {
		color: #6a7282;
		word-break: break-word;
	}

	@media (max-width: 720px) {
		.section-grid {
			grid-template-columns: 1fr;
		}
	}
</style>
