<script lang="ts">
	import ProfileTabs from './ProfileTabs.svelte';
	import type { Scholar } from '$lib/types/scholar';

	let { scholar }: { scholar: Scholar } = $props();
</script>

<article class="profile-shell">
	<div class="profile-breadcrumb">
		{scholar.faculty || 'Faculty'} / People / {scholar.fullName}
	</div>

	<header class="profile-header">
		<div class="profile-media">
			<img src={scholar.avatarUrl} alt={scholar.fullName} loading="eager" />
		</div>
		<div class="profile-identity">
			<h1>{scholar.fullName}</h1>
			<p class="qualifications">{scholar.qualifications}</p>
			<p class="role-badge">{scholar.role}</p>
			<p class="role">{scholar.headline}</p>
			<p class="role-sub">Faculty of {scholar.faculty || scholar.college}</p>
		</div>
	</header>

	<section class="profile-contact-grid">
		<div>
			<h3>Email:</h3>
			<p>{scholar.email}</p>
		</div>
		<div>
			<h3>Institution:</h3>
			<p>{scholar.college}</p>
		</div>
		<div>
			<h3>Tel:</h3>
			<p>{scholar.phone}</p>
		</div>
		<div>
			<h3>Location:</h3>
			<p>{scholar.location}</p>
		</div>
	</section>

	<div class="profile-content">
		<aside>
			<h2>Research Areas</h2>
			<ul>
				{#each scholar.researchAreas as area}
					<li>{area}</li>
				{/each}
			</ul>

			{#if scholar.areasOfExpertise && scholar.areasOfExpertise.length > 0}
				<h3>Areas of Expertise</h3>
				<ul>
					{#each scholar.areasOfExpertise as area}
						<li>{area}</li>
					{/each}
				</ul>
			{/if}

			{#if scholar.industrialAreasOfExpertise && scholar.industrialAreasOfExpertise.length > 0}
				<h3>Industrial Areas</h3>
				<ul>
					{#each scholar.industrialAreasOfExpertise as area}
						<li>{area}</li>
					{/each}
				</ul>
			{/if}

			{#if scholar.languagesSpoken && scholar.languagesSpoken.length > 0}
				<h3>Languages</h3>
				<ul>
					{#each scholar.languagesSpoken as lang}
						<li>{lang}</li>
					{/each}
				</ul>
			{/if}

			<div class="profile-links">
				{#each scholar.links as link}
					<a href={link.url} target="_blank" rel="noreferrer">{link.label}</a>
				{/each}
			</div>
		</aside>

		<section class="profile-sections">
			<ProfileTabs sections={scholar.sections} />
		</section>
	</div>
</article>

<style>
	.profile-shell {
		max-width: 1200px;
		margin: 0 auto;
		padding: 1.5rem 2rem 2rem;
	}

	.profile-breadcrumb {
		font-size: 0.875rem;
		color: #666;
		margin-bottom: 2rem;
	}

	.profile-header {
		display: grid;
		grid-template-columns: 210px 1fr;
		gap: 2rem;
		margin-bottom: 2rem;
		align-items: start;
		padding: 1.5rem;
		border: 1px solid #d6d0d5;
		border-radius: 14px;
		background: #f5f3f5;
	}

	.profile-media img {
		width: 100%;
		aspect-ratio: 4 / 5;
		object-fit: cover;
		border-radius: 12px;
		box-shadow: 0 12px 24px rgba(22, 10, 20, 0.2);
	}

	.profile-identity {
		background: #65275f;
		color: #fff;
		padding: 1.6rem 1.8rem;
		border-radius: 10px;
	}

	.profile-identity h1 {
		margin: 0 0 0.5rem 0;
		font-family: 'Fraunces', serif;
		font-size: clamp(2rem, 5.2vw, 4.25rem);
		line-height: 0.95;
		letter-spacing: 0.01em;
		color: #fff;
	}

	.qualifications {
		font-size: clamp(0.95rem, 1.65vw, 1.45rem);
		font-style: italic;
		font-weight: 500;
		color: rgba(255, 255, 255, 0.95);
		margin: 0 0 1.35rem;
	}

	.role-badge {
		display: inline-block;
		margin: 0 0 1rem 0;
		background-color: rgba(255, 255, 255, 0.14);
		padding: 0.4rem 0.95rem;
		border-radius: 999px;
		font-size: 0.82rem;
		letter-spacing: 0.14em;
		font-weight: 700;
		text-transform: uppercase;
		color: #fff;
	}

	.role {
		font-size: clamp(1.15rem, 2.05vw, 1.7rem);
		line-height: 1.15;
		font-weight: 700;
		margin: 0;
		color: #fff;
	}

	.role-sub {
		font-size: clamp(0.95rem, 1.4vw, 1.25rem);
		font-weight: 500;
		color: rgba(255, 255, 255, 0.9);
		margin: 0.7rem 0 0 0;
	}

	.profile-contact-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 1.5rem;
		margin-bottom: 2rem;
		padding: 1.5rem;
		background-color: #fff;
		border: 1px solid #e6e0e5;
		border-radius: 8px;
	}

	.profile-contact-grid h3 {
		font-size: 0.76rem;
		font-weight: 600;
		text-transform: uppercase;
		color: #8a1b62;
		letter-spacing: 0.08em;
		margin: 0 0 0.5rem 0;
	}

	.profile-contact-grid p {
		margin: 0;
		font-size: 0.98rem;
		color: #1f2a44;
		line-height: 1.35;
	}

	.profile-content {
		display: grid;
		grid-template-columns: 250px 1fr;
		gap: 3rem;
	}

	aside h2 {
		font-size: 1rem;
		margin: 0 0 1rem 0;
	}

	aside h3 {
		font-size: 0.9rem;
		margin: 1.5rem 0 0.75rem 0;
	}

	aside ul {
		list-style: none;
		padding: 0;
		margin: 0;
	}

	aside li {
		padding: 0.5rem 0;
		font-size: 0.9rem;
		color: #555;
	}

	.profile-links {
		margin-top: 2rem;
		padding-top: 1rem;
		border-top: 1px solid #e0e0e0;
		display: flex;
		flex-direction: column;
		gap: 0.75rem;
	}

	.profile-links a {
		color: #0066cc;
		text-decoration: none;
		font-size: 0.9rem;
	}

	.profile-links a:hover {
		text-decoration: underline;
	}

	.profile-sections {
		padding-left: 2rem;
	}

	@media (max-width: 768px) {
		.profile-header {
			grid-template-columns: 1fr;
			padding: 1rem;
			gap: 1rem;
		}

		.profile-media {
			max-width: 260px;
		}

		.profile-identity {
			padding: 1rem 1.1rem;
		}

		.profile-identity h1 {
			line-height: 1.02;
		}

		.profile-contact-grid {
			grid-template-columns: repeat(2, 1fr);
		}

		.profile-content {
			grid-template-columns: 1fr;
		}

		.profile-sections {
			padding-left: 0;
		}
	}
</style>
