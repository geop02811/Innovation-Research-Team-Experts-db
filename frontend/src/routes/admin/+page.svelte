<script lang="ts">
	import { onMount } from 'svelte';
	import { authService } from '$lib/auth/auth.service';
	import type { UserAccount, UserRole } from '$lib/auth/types';

	let users = $state<UserAccount[]>([]);
	let activeTab = $state<'pending' | 'approved' | 'all'>('pending');
	let moderationComment = $state<Record<string, string>>({});

	const loadUsers = () => {
		users = authService.listUsers();
	};

	onMount(loadUsers);

	const pendingUsers = $derived(users.filter((user) => user.status === 'PENDING'));
	const approvedUsers = $derived(users.filter((user) => user.status === 'APPROVED' || user.status === 'ACTIVE'));

	const approve = (id: string) => {
		authService.approveUser(id, moderationComment[id] || 'Approved by admin');
		loadUsers();
	};

	const reject = (id: string) => {
		authService.rejectUser(id, moderationComment[id] || 'Rejected by admin');
		loadUsers();
	};

	const changeRole = (id: string, role: UserRole) => {
		authService.updateRole(id, role);
		loadUsers();
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

		{#if activeTab === 'pending'}
			<section class="panel">
				{#if pendingUsers.length === 0}
					<p>No pending users.</p>
				{:else}
					{#each pendingUsers as user}
						<article class="user-card">
							<h3>{user.profile.titlePrefix} {user.profile.fullName}</h3>
							<p>{user.email} • {user.profile.department} • {user.profile.faculty}</p>
							<p>{user.profile.consultancyExperience}</p>
							<textarea
								bind:value={moderationComment[user.id]}
								rows="2"
								placeholder="Comment for approval/rejection"
							></textarea>
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
				{#if approvedUsers.length === 0}
					<p>No approved users.</p>
				{:else}
					{#each approvedUsers as user}
						<article class="user-card">
							<h3>{user.profile.titlePrefix} {user.profile.fullName}</h3>
							<p>{user.email} • Status: {user.status}</p>
							<p>Approval comment: {user.approvalComment || 'None'}</p>
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
							<h3>{user.profile.fullName}</h3>
							<p>{user.email}</p>
						</div>
						<div class="row-actions">
							<select value={user.role} onchange={(event) => changeRole(user.id, (event.currentTarget as HTMLSelectElement).value as UserRole)}>
								<option value="USER">USER</option>
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
</style>
