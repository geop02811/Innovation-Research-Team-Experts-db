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
		padding: 2rem;
	}

	.profile-breadcrumb {
		font-size: 0.875rem;
		color: #666;
		margin-bottom: 2rem;
	}

	.profile-header {
		display: grid;
		grid-template-columns: 150px 1fr;
		gap: 2rem;
		margin-bottom: 2rem;
		align-items: start;
	}

	.profile-media img {
		width: 100%;
		aspect-ratio: 1;
		object-fit: cover;
		border-radius: 8px;
	}

	.profile-identity h1 {
		margin: 0 0 0.5rem 0;
		font-size: 2rem;
	}

	.qualifications {
		font-size: 0.9rem;
		color: #666;
		margin: 0.25rem 0;
	}

	.role-badge {
		display: inline-block;
		margin: 0.5rem 0 0 0;
		background-color: #f0f0f0;
		padding: 0.25rem 0.75rem;
		border-radius: 16px;
		font-size: 0.75rem;
		font-weight: 600;
		color: #333;
	}

	.role {
		font-size: 1.1rem;
		font-weight: 500;
		margin: 1rem 0 0 0;
	}

	.role-sub {
		font-size: 0.9rem;
		color: #666;
		margin: 0.5rem 0 0 0;
	}

	.profile-contact-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 1.5rem;
		margin-bottom: 2rem;
		padding: 1.5rem;
		background-color: #f9f9f9;
		border-radius: 8px;
	}

	.profile-contact-grid h3 {
		font-size: 0.75rem;
		font-weight: 600;
		text-transform: uppercase;
		color: #666;
		margin: 0 0 0.5rem 0;
	}

	.profile-contact-grid p {
		margin: 0;
		font-size: 0.9rem;
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
