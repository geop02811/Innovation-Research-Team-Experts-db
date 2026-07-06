<script lang="ts">
	import { authService } from '$lib/auth/auth.service';
	import NotificationBell from '$lib/components/NotificationBell.svelte';
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import type { LayoutData } from './$types';

	let { children, data }: { children: import('svelte').Snippet; data: LayoutData } = $props();

	const currentYear = new Date().getFullYear();

	let menuOpen = $state(false);
	const toggleMenu = () => (menuOpen = !menuOpen);
	const closeMenu = () => (menuOpen = false);

	const logout = () => {
		authService.logout();
		closeMenu();
		window.location.href = '/login';
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
			<a href="/" class="brand" onclick={closeMenu}>
				<span class="brand-shape brand-shape-faded" aria-hidden="true"></span>
				<span class="brand-shape brand-shape-orange" aria-hidden="true"></span>
				<span class="brand-shape brand-shape-white" aria-hidden="true"></span>
				<img src="/Uz_logo2.png" alt="University of Zimbabwe" class="nav-logo" />
				<span class="brand-text">University of Zimbabwe<br /><span class="brand-sub">Research and Funding Portal</span></span>
			</a>
			<!-- Desktop nav -->
			<nav class="desktop-nav" aria-label="Main navigation">
				<a href="/">Home</a>
				<a href="/experts">Researchers</a>
				{#if data.session?.role === 'ADMIN'}
					<a class="admin-link" href="/admin">Admin Dashboard</a>
				{/if}
				{#if data.session}
					<NotificationBell role={data.session.role} />
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
			<!-- Hamburger button (mobile only) -->
			<button class="hamburger" aria-label="Toggle menu" aria-expanded={menuOpen} onclick={toggleMenu}>
				<span class:open={menuOpen}></span>
				<span class:open={menuOpen}></span>
				<span class:open={menuOpen}></span>
			</button>
		</div>
		<!-- Mobile drawer -->
		{#if menuOpen}
			<!-- svelte-ignore a11y_click_events_have_key_events a11y_no_static_element_interactions -->
			<div class="mobile-overlay" onclick={closeMenu}></div>
			<nav class="mobile-nav" aria-label="Mobile navigation">
				<a href="/" onclick={closeMenu}>Home</a>
				<a href="/experts" onclick={closeMenu}>Researchers</a>
				{#if data.session?.role === 'ADMIN'}
					<a class="admin-link" href="/admin" onclick={closeMenu}>Admin Dashboard</a>
				{/if}
				{#if data.session}
					<div class="mobile-notif-row">
						<NotificationBell role={data.session.role} />
						<span class="mobile-notif-label">Notifications</span>
					</div>
					<a href="/profile" class="profile-link" onclick={closeMenu}>My Profile</a>
					<button class="mobile-logout" onclick={logout}>Log Out</button>
				{:else}
					<a href="/login" onclick={closeMenu}>Login</a>
					<a href="/signup" class="mobile-signup" onclick={closeMenu}>Sign Up</a>
				{/if}
			</nav>
		{/if}
	</header>

	{@render children()}

	<footer class="site-footer">
		<div class="site-footer-top">
			<div class="footer-col">
				<h4>Explore</h4>
				<ul>
					<li><a href="/experts">Researchers Directory</a></li>
					<li><a href="/experts">Area of Research</a></li>
					<li><a href="/experts">Academic Staff</a></li>
					<li><a href="/experts">Postgraduate Students</a></li>
				</ul>
			</div>
			<div class="footer-col">
				<h4>Research</h4>
				<ul>
					<li><a href="/experts">Research Highlights</a></li>
					<li><a href="https://www.uz.ac.zw/index.php/admissions/university-programmes" target="_blank" rel="noopener noreferrer" >Institutes &amp; Centres</a></li>
					<li><a href="/grants">Funding Opportunities</a></li>
					<li><a href="https://www.researchgate.net/institution/University-of-Zimbabwe"  target="_blank" rel="noopener noreferrer">Publications</a></li>
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
					<li><a href="/signup">Register as Researcher</a></li>
					<li><a href="/profile">My Profile</a></li>
				</ul>
			</div>
			<div class="footer-col footer-col-brand">
				<div class="footer-brand-name">University of Zimbabwe</div>
				<p class="footer-brand-tagline">Educating to Change Lives</p>
				<p class="footer-address">P.O. Box MP 167<br />Mount Pleasant, Harare, Zimbabwe</p>
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
