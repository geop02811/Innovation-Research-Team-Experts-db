<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import { authService } from '$lib/auth/auth.service';
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import type { LayoutData } from './$types';

	let { children, data }: { children: import('svelte').Snippet; data: LayoutData } = $props();

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
			<a href="/" class="brand">University of Zimbabwe Experts</a>
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
</div>
