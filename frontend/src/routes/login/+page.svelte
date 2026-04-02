<script lang="ts">
	import { browser } from '$app/environment';
	import { goto } from '$app/navigation';
	import { page } from '$app/state';
	import { authService } from '$lib/auth/auth.service';

	let email = $state('');
	let password = $state('');
	let error = $state('');
	let info = $state('');
	let loading = $state(false);

	if (browser) {
		const message = page.url.searchParams.get('message');
		if (message) {
			info = message;
		}
	}

	const submit = async (event: SubmitEvent) => {
		event.preventDefault();
		error = '';
		loading = true;

		const result = authService.login(email, password);
		loading = false;
		if (!result.ok || !result.session) {
			error = result.message;
			return;
		}

		const returnTo = page.url.searchParams.get('returnTo');
		if (result.session.role === 'ADMIN') {
			await goto('/admin');
			return;
		}

		await goto(returnTo || '/experts');
	};
</script>

<svelte:head>
	<title>Login | University of Zimbabwe Experts</title>
</svelte:head>

<main class="page-shell auth-shell">
	<section class="auth-card">
		<p class="kicker">Secure Access</p>
		<h1>Login</h1>
		<p class="helper">Only approved users can view experts profiles.</p>
		{#if info}
			<p class="info-msg">{info}</p>
		{/if}
		{#if error}
			<p class="error-msg">{error}</p>
		{/if}

		<form onsubmit={submit}>
			<label>
				University email
				<input type="email" bind:value={email} required placeholder="name@uz.ac.zw" />
			</label>

			<label>
				Password
				<input type="password" bind:value={password} required minlength="6" />
			</label>

			<button type="submit" disabled={loading}>{loading ? 'Signing in...' : 'Sign in'}</button>
		</form>

		<div class="hint-block">
			<p>Need an account? <a href="/signup">Create expert profile</a></p>
			<p>Demo admin: admin@uz.ac.zw / Admin@123</p>
		</div>
	</section>
</main>

<style>
	.auth-shell {
		display: grid;
		place-items: center;
		padding-top: 2rem;
	}

	.auth-card {
		width: min(520px, 100%);
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 1.5rem;
		box-shadow: var(--shadow);
	}

	h1 {
		margin: 0.3rem 0 0.6rem;
		font-family: 'Fraunces', serif;
	}

	.helper {
		margin-top: 0;
		color: var(--ink-soft);
	}

	form {
		display: grid;
		gap: 1rem;
	}

	label {
		display: grid;
		gap: 0.45rem;
		font-weight: 600;
	}

	input {
		border-radius: 12px;
		border: 1px solid #cfd4de;
		padding: 0.7rem 0.8rem;
		font-size: 1rem;
	}

	button {
		border: 0;
		background: #0a3a8d;
		color: #fff;
		padding: 0.8rem 1rem;
		border-radius: 999px;
		font-weight: 700;
		cursor: pointer;
	}

	button:disabled {
		opacity: 0.7;
		cursor: not-allowed;
	}

	.error-msg {
		background: #fee;
		color: #7c1020;
		padding: 0.6rem 0.8rem;
		border-radius: 10px;
	}

	.info-msg {
		background: #ecf6ff;
		color: #0a3a8d;
		padding: 0.6rem 0.8rem;
		border-radius: 10px;
	}

	.hint-block {
		margin-top: 1rem;
		font-size: 0.92rem;
		color: var(--ink-soft);
	}

	.hint-block p {
		margin: 0.3rem 0;
	}

	.hint-block a {
		text-decoration: underline;
	}
</style>
