<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import { authService } from '$lib/auth/auth.service';
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import type { LayoutData } from './$types';

	let { children, data }: { children: import('svelte').Snippet; data: LayoutData } = $props();

	const currentYear = new Date().getFullYear();

	const logout = async () => {
		authService.logout();
		await invalidateAll();
		await goto('/login');
	};
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
	<link
		href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,700&family=Source+Sans+3:wght@400;500;600;700&display=swap"
		rel="stylesheet"
	/>
</svelte:head>
<div class="app-shell">
	<header class="site-header">
		<div class="site-header-inner">
			<a href="/" class="brand">
				<img src="/University_of_Zimbabwe_LOGO.png" alt="University of Zimbabwe" class="nav-logo" />
				<span class="brand-text">University of Zimbabwe Experts</span>
			</a>
			<nav aria-label="Main navigation">
				<a href="/">Home</a>
				<a href="/experts">Experts</a>
				{#if data.session?.role === 'ADMIN'}
					<a class="admin-link" href="/admin">Admin Dashboard</a>
				{/if}
				{#if data.session}
					<a href="/profile" class="profile-link" title="My Profile">
						<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
							<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
							<circle cx="12" cy="7" r="4"></circle>
						</svg>
					</a>
				{:else}
					<a href="/login">Login</a>
					<a href="/signup">Sign Up</a>
				{/if}
			</nav>
		</div>
	</header>

	{@render children()}

	<footer class="site-footer">
		<div class="site-footer-top">
			<div class="footer-col">
				<h4>Explore</h4>
				<ul>
					<li><a href="/experts">Expert Directory</a></li>
					<li><a href="/experts">Research Areas</a></li>
					<li><a href="/experts">Academic Staff</a></li>
					<li><a href="/experts">Postgraduate Students</a></li>
				</ul>
			</div>
			<div class="footer-col">
				<h4>Research</h4>
				<ul>
					<li><a href="/experts">Research Highlights</a></li>
					<li><a href="/experts">Institutes &amp; Centres</a></li>
					<li><a href="/experts">Grants &amp; Funding</a></li>
					<li><a href="/experts">Publications</a></li>
				</ul>
			</div>
			<div class="footer-col">
				<h4>Community</h4>
				<ul>
					<li><a href="/experts">Alumni Network</a></li>
					<li><a href="/experts">Events</a></li>
					<li><a href="/experts">Competitions</a></li>
					<li><a href="/experts">News</a></li>
				</ul>
			</div>
			<div class="footer-col">
				<h4>Account</h4>
				<ul>
					<li><a href="/login">Login</a></li>
					<li><a href="/signup">Register as Expert</a></li>
					<li><a href="/profile">My Profile</a></li>
				</ul>
			</div>
			<div class="footer-col footer-col-brand">
				<div class="footer-brand-name">University of Zimbabwe</div>
				<p class="footer-brand-tagline">Nurturing Minds, Inspiring Innovation</p>
				<p class="footer-address">Mount Pleasant, Harare, Zimbabwe<br />P.O. Box MP 167</p>
			</div>
		</div>
		<div class="site-footer-bottom">
			<span>&copy; {currentYear} University of Zimbabwe. All rights reserved.</span>
			<div class="footer-legal">
				<a href="/">Accessibility</a>
				<a href="/">Privacy Policy</a>
				<a href="/">Cookie Statement</a>
				<a href="/">Legal Notice</a>
			</div>
		</div>
	</footer>
</div>
