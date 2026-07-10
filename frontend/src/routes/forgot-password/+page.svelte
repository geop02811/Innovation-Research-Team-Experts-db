<script lang="ts">
	import { goto } from '$app/navigation';
	import { authService } from '$lib/auth/auth.service';

	let email = $state('');
	let otp = $state('');
	let newPassword = $state('');
	let confirmPassword = $state('');
	let step = $state<'request' | 'confirm'>('request');
	let info = $state('');
	let error = $state('');
	let loading = $state(false);

	const requestReset = async (event: SubmitEvent) => {
		event.preventDefault();
		error = '';
		info = '';
		loading = true;

		const result = await authService.requestPasswordReset(email.trim().toLowerCase());
		loading = false;
		if (!result.ok) {
			error = result.message;
			return;
		}

		info = result.message;
		step = 'confirm';
	};

	const confirmReset = async (event: SubmitEvent) => {
		event.preventDefault();
		error = '';
		info = '';

		if (newPassword.length < 6) {
			error = 'Password must be at least 6 characters long.';
			return;
		}

		if (newPassword !== confirmPassword) {
			error = 'Passwords do not match.';
			return;
		}

		loading = true;
		const result = await authService.confirmPasswordReset({
			email: email.trim().toLowerCase(),
			otp: otp.trim(),
			newPassword
		});
		loading = false;

		if (!result.ok) {
			error = result.message;
			return;
		}

		await goto('/login?message=Password reset successful. You can now log in.');
	};
</script>

<svelte:head>
	<title>Forgot Password | University of Zimbabwe Researchers</title>
</svelte:head>

<main class="page-shell auth-shell">
	<section class="auth-card">
		<p class="kicker">Account Recovery</p>
		<h1>Reset password</h1>
		<p class="helper">
			Enter your account email to receive a one-time reset code.
		</p>

		{#if info}
			<p class="info-msg">{info}</p>
		{/if}
		{#if error}
			<p class="error-msg">{error}</p>
		{/if}

		{#if step === 'request'}
			<form onsubmit={requestReset}>
				<label>
					University email
					<input type="email" bind:value={email} required placeholder="name@uz.ac.zw" />
				</label>

				<button type="submit" disabled={loading}>{loading ? 'Sending code...' : 'Send reset code'}</button>
			</form>
		{:else}
			<form onsubmit={confirmReset}>
				<label>
					University email
					<input type="email" bind:value={email} required placeholder="name@uz.ac.zw" />
				</label>

				<label>
					Reset code
					<input inputmode="numeric" bind:value={otp} required maxlength="6" placeholder="6-digit code" />
				</label>

				<label>
					New password
					<input type="password" bind:value={newPassword} required minlength="6" />
				</label>

				<label>
					Confirm new password
					<input type="password" bind:value={confirmPassword} required minlength="6" />
				</label>

				<button type="submit" disabled={loading}>{loading ? 'Resetting...' : 'Reset password'}</button>
			</form>
		{/if}

		<div class="hint-block">
			<p><a href="/login">Back to login</a></p>
			<p class="dev-note">During local development, check backend logs for the reset code if SMTP is not configured.</p>
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

	.dev-note {
		font-size: 0.84rem;
		line-height: 1.45;
	}
</style>