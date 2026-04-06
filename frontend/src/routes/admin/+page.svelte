<script lang="ts">
	import { onMount } from 'svelte';
	import { authService } from '$lib/auth/auth.service';
	import type { AdminUser, UserRole } from '$lib/auth/types';

	let users = $state<AdminUser[]>([]);
	let activeTab = $state<'pending' | 'approved' | 'all'>('pending');
	let loading = $state(false);
	let fetchError = $state('');

	const loadUsers = async () => {
		loading = true;
		fetchError = '';
		const result = await authService.listUsers();
		users = result.users;
		fetchError = result.error ?? '';
		loading = false;
	};

	onMount(loadUsers);

	const pendingUsers = $derived(users.filter((u) => u.status === 'PENDING'));
	const approvedUsers = $derived(users.filter((u) => u.status === 'APPROVED' || u.status === 'ACTIVE'));

	const approve = async (id: string) => {
		await authService.approveUser(id);
		await loadUsers();
	};

	const reject = async (id: string) => {
		await authService.rejectUser(id);
		await loadUsers();
	};

	const changeRole = async (id: string, role: UserRole) => {
		await authService.updateRole(id, role);
		await loadUsers();
	};
</script>

<svelte:head>
	<title>Admin Dashboard | University of Zimbabwe Experts</title>
</svelte:head>

<main class="page-shell">
	<section class="admin-shell">
		<div class="admin-header">
			<p class="kicker">Admin</p>
			<h1>Dashboard</h1>
			<p>Manage pending registrations, approved users, and permissions.</p>
		</div>

		<div class="tabs">
			<button class:active={activeTab === 'pending'} onclick={() => (activeTab = 'pending')}>Pending Users</button>
			<button class:active={activeTab === 'approved'} onclick={() => (activeTab = 'approved')}>Approved Users</button>
			<button class:active={activeTab === 'all'} onclick={() => (activeTab = 'all')}>All Users & Permissions</button>
		</div>

		{#if loading}
			<p class="status-msg">Loading...</p>
		{/if}

		{#if fetchError}
			<p class="error-msg">{fetchError}</p>
		{/if}

		{#if activeTab === 'pending'}
			<section class="panel">
				{#if !loading && pendingUsers.length === 0}
					<p>No pending users.</p>
				{:else}
					{#each pendingUsers as user}
						<article class="user-card">
							<h3>{user.titlePrefix ?? ''} {user.fullName ?? user.name + ' ' + user.surname}</h3>
							<p>{user.email} • {user.department ?? '—'} • {user.faculty ?? '—'}</p>
							{#if user.consultancyExperience}
								<p>{user.consultancyExperience}</p>
							{/if}
							<div class="actions">
								<button type="button" onclick={() => approve(user.id)}>Approve</button>
								<button type="button" class="danger" onclick={() => reject(user.id)}>Reject</button>
							</div>
						</article>
					{/each}
				{/if}
			</section>
		{/if}

		{#if activeTab === 'approved'}
			<section class="panel">
				{#if !loading && approvedUsers.length === 0}
					<p>No approved users.</p>
				{:else}
					{#each approvedUsers as user}
						<article class="user-card">
							<h3>{user.titlePrefix ?? ''} {user.fullName ?? user.name + ' ' + user.surname}</h3>
							<p>{user.email} • Status: {user.status}</p>
						</article>
					{/each}
				{/if}
			</section>
		{/if}

		{#if activeTab === 'all'}
			<section class="panel">
				{#each users as user}
					<article class="user-card compact">
						<div>
							<h3>{user.fullName ?? user.name + ' ' + user.surname}</h3>
							<p>{user.email}</p>
						</div>
						<div class="row-actions">
							<select value={user.role} onchange={(e) => changeRole(user.id, (e.currentTarget as HTMLSelectElement).value as UserRole)}>
								<option value="USER">USER</option>
								<option value="VIEWER">VIEWER</option>
								<option value="ADMIN">ADMIN</option>
							</select>
							<span class="status">{user.status}</span>
						</div>
					</article>
				{/each}
			</section>
		{/if}
	</section>
</main>

<style>
	.admin-shell {
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 1.3rem;
		box-shadow: var(--shadow);
	}

	h1 {
		font-family: 'Fraunces', serif;
		margin: 0.2rem 0 0.5rem;
	}

	.admin-header p {
		margin-top: 0;
		color: var(--ink-soft);
	}

	.tabs {
		display: flex;
		flex-wrap: wrap;
		gap: 0.7rem;
		margin: 1rem 0;
	}

	.tabs button {
		border: 1px solid #c8d2e6;
		background: #fff;
		padding: 0.5rem 0.8rem;
		border-radius: 999px;
		font-weight: 700;
		cursor: pointer;
	}

	.tabs button.active {
		background: #123f89;
		border-color: #123f89;
		color: #fff;
	}

	.panel {
		display: grid;
		gap: 0.9rem;
	}

	.user-card {
		padding: 0.9rem;
		border: 1px solid var(--line);
		border-radius: 14px;
		background: #fdfbf5;
	}

	.user-card h3 {
		margin: 0 0 0.35rem;
	}

	.user-card p {
		margin: 0.25rem 0;
	}

	textarea {
		width: 100%;
		margin-top: 0.45rem;
		border: 1px solid #cfd4de;
		border-radius: 10px;
		padding: 0.55rem;
	}

	.actions {
		display: flex;
		gap: 0.6rem;
		margin-top: 0.6rem;
	}

	.actions button {
		border: 0;
		padding: 0.55rem 0.8rem;
		border-radius: 8px;
		cursor: pointer;
		font-weight: 700;
		background: #0a3a8d;
		color: #fff;
	}

	.actions .danger {
		background: #9a1b2f;
	}

	.user-card.compact {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 0.6rem;
	}

	.row-actions {
		display: flex;
		align-items: center;
		gap: 0.6rem;
	}

	.row-actions select {
		border: 1px solid #cfd4de;
		border-radius: 8px;
		padding: 0.4rem;
	}

	.status {
		font-size: 0.85rem;
		font-weight: 700;
		color: #124f40;
	}

	.status-msg {
		color: var(--ink-soft);
	}

	.error-msg {
		background: #fee;
		color: #7c1020;
		padding: 0.65rem 0.8rem;
		border-radius: 10px;
	}
</style>
