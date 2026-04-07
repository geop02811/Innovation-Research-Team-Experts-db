<script lang="ts">
	import { onMount } from 'svelte';
	import { authService } from '$lib/auth/auth.service';
	import type { AdminUser, UserRole } from '$lib/auth/types';

	let users = $state<AdminUser[]>([]);
	let activeTab = $state<'pending' | 'approved' | 'all'>('pending');
	let loading = $state(false);
	let fetchError = $state('');
	let expandedId = $state<string | null>(null);

	const toggleExpand = (id: string) => {
		expandedId = expandedId === id ? null : id;
	};

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
		expandedId = null;
		await loadUsers();
	};

	const reject = async (id: string) => {
		await authService.rejectUser(id);
		expandedId = null;
		await loadUsers();
	};

	const changeRole = async (id: string, role: UserRole) => {
		await authService.updateRole(id, role);
		await loadUsers();
	};

	const displayName = (u: AdminUser) =>
		`${u.titlePrefix ?? ''} ${u.fullName ?? u.name + ' ' + u.surname}`.trim();
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
					<p class="empty-msg">No pending users.</p>
				{:else}
					{#each pendingUsers as user}
						<article class="user-card" class:expanded={expandedId === user.id}>
							<!-- Clickable summary row -->
							<button class="card-summary" onclick={() => toggleExpand(user.id)}>
								<div class="summary-left">
									{#if user.profilePhotoDataUrl}
										<img src={user.profilePhotoDataUrl} alt="Profile" class="avatar" />
									{:else}
										<div class="avatar placeholder">{displayName(user)[0]}</div>
									{/if}
									<div>
										<strong>{displayName(user)}</strong>
										<span class="meta">{user.email} • {user.faculty ?? '—'}</span>
									</div>
								</div>
								<span class="chevron">{expandedId === user.id ? '▲' : '▼'}</span>
							</button>

							<!-- Expandable detail drawer -->
							{#if expandedId === user.id}
								<div class="detail-drawer">
									<div class="detail-grid">
										<div class="detail-section">
											<p class="section-label">Contact</p>
											<p>{user.universityEmail ?? '—'}</p>
											<p>{user.phoneNumber ?? '—'}</p>
											{#if user.contactDetails}<p>{user.contactDetails}</p>{/if}
										</div>
										<div class="detail-section">
											<p class="section-label">Academic</p>
											<p>{user.academicRank ?? '—'} • {user.highestQualification ?? '—'}</p>
											<p>{user.faculty ?? '—'} — {user.department ?? '—'}</p>
											{#if user.professionalMemberships}<p>{user.professionalMemberships}</p>{/if}
										</div>
										<div class="detail-section">
											<p class="section-label">Consultancy</p>
											<p>{user.yearsOfConsultancyExperience ?? '—'} • {user.consultancyAvailability ?? '—'}</p>
											<p>{user.geographicScope ?? '—'}</p>
											{#if user.consultancyExperience}<p class="experience-text">{user.consultancyExperience}</p>{/if}
										</div>
										<div class="detail-section">
											<p class="section-label">Expertise</p>
											{#if user.areasOfExpertise}<p>{user.areasOfExpertise.split(',').join(' • ')}</p>{/if}
											{#if user.skillsAndCompetences}<p>{user.skillsAndCompetences.split(',').join(' • ')}</p>{/if}
										</div>
										{#if user.complianceAccreditation}
											<div class="detail-section">
												<p class="section-label">Compliance / Accreditation</p>
												<p>{user.complianceAccreditation}</p>
											</div>
										{/if}
										{#if user.notes}
											<div class="detail-section">
												<p class="section-label">Notes</p>
												<p>{user.notes}</p>
											</div>
										{/if}
									</div>

									<div class="actions">
										<button type="button" onclick={() => approve(user.id)}>Approve</button>
										<button type="button" class="danger" onclick={() => reject(user.id)}>Reject</button>
									</div>
								</div>
							{/if}
						</article>
					{/each}
				{/if}
			</section>
		{/if}

		{#if activeTab === 'approved'}
			<section class="panel">
				{#if !loading && approvedUsers.length === 0}
					<p class="empty-msg">No approved users.</p>
				{:else}
					{#each approvedUsers as user}
						<article class="user-card">
							<button class="card-summary no-chevron" onclick={() => toggleExpand(user.id)}>
								<div class="summary-left">
									{#if user.profilePhotoDataUrl}
										<img src={user.profilePhotoDataUrl} alt="Profile" class="avatar" />
									{:else}
										<div class="avatar placeholder">{displayName(user)[0]}</div>
									{/if}
									<div>
										<strong>{displayName(user)}</strong>
										<span class="meta">{user.email} • {user.faculty ?? '—'}</span>
									</div>
								</div>
								<span class="badge badge-active">{user.status}</span>
							</button>
							{#if expandedId === user.id}
								<div class="detail-drawer">
									<div class="detail-grid">
										<div class="detail-section">
											<p class="section-label">Academic</p>
											<p>{user.academicRank ?? '—'} • {user.highestQualification ?? '—'}</p>
											<p>{user.faculty ?? '—'} — {user.department ?? '—'}</p>
										</div>
										<div class="detail-section">
											<p class="section-label">Consultancy</p>
											<p>{user.consultancyAvailability ?? '—'} • {user.geographicScope ?? '—'}</p>
										</div>
									</div>
								</div>
							{/if}
						</article>
					{/each}
				{/if}
			</section>
		{/if}

		{#if activeTab === 'all'}
			<section class="panel">
				{#if !loading && users.length === 0}
					<p class="empty-msg">No users found.</p>
				{:else}
					<div class="permissions-table">
						<div class="pt-header">
							<span>User</span>
							<span>Status</span>
							<span>Role</span>
						</div>
						{#each users as user}
							<div class="pt-row">
								<div class="pt-user">
									{#if user.profilePhotoDataUrl}
										<img src={user.profilePhotoDataUrl} alt="Profile" class="avatar sm" />
									{:else}
										<div class="avatar sm placeholder">{displayName(user)[0]}</div>
									{/if}
									<div>
										<span class="pt-name">{displayName(user)}</span>
										<span class="meta">{user.email}</span>
									</div>
								</div>
								<span class="badge" class:badge-active={user.status === 'ACTIVE'} class:badge-pending={user.status === 'PENDING'} class:badge-disabled={user.status === 'DISABLED'}>
									{user.status}
								</span>
								<select
									value={user.role}
									onchange={(e) => changeRole(user.id, (e.currentTarget as HTMLSelectElement).value as UserRole)}
								>
									<option value="USER">User</option>
									<option value="VIEWER">Viewer</option>
									<option value="ADMIN">Admin</option>
								</select>
							</div>
						{/each}
					</div>
				{/if}
			</section>
		{/if}
	</section>
</main>

<style>
	.admin-shell {
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 1.5rem;
		box-shadow: var(--shadow);
		display: grid;
		gap: 1.2rem;
	}

	h1 {
		font-family: 'Fraunces', serif;
		margin: 0.2rem 0 0.3rem;
	}

	.admin-header p {
		margin: 0;
		color: var(--ink-soft);
	}

	/* ── Tabs ── */
	.tabs {
		display: flex;
		flex-wrap: wrap;
		gap: 0.6rem;
	}

	.tabs button {
		border: 1px solid #c8d2e6;
		background: #fff;
		padding: 0.45rem 1rem;
		border-radius: 999px;
		font-weight: 700;
		cursor: pointer;
		font-size: 0.9rem;
	}

	.tabs button.active {
		background: #123f89;
		border-color: #123f89;
		color: #fff;
	}

	/* ── Panel ── */
	.panel {
		display: grid;
		gap: 0.8rem;
	}

	.empty-msg {
		color: var(--ink-soft);
		margin: 0;
	}

	/* ── User card ── */
	.user-card {
		border: 1px solid var(--line);
		border-radius: 14px;
		overflow: hidden;
		background: #fafaf8;
	}

	.user-card.expanded {
		border-color: #0a3a8d;
	}

	/* ── Card summary row (clickable) ── */
	.card-summary {
		display: flex;
		align-items: center;
		justify-content: space-between;
		width: 100%;
		padding: 0.85rem 1rem;
		background: none;
		border: none;
		cursor: pointer;
		text-align: left;
		gap: 0.75rem;
	}

	.card-summary:hover {
		background: #f0f4ff;
	}

	.summary-left {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		min-width: 0;
	}

	.summary-left strong {
		display: block;
		font-size: 0.98rem;
	}

	.meta {
		display: block;
		font-size: 0.82rem;
		color: var(--ink-soft);
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.chevron {
		font-size: 0.7rem;
		color: #8a93a8;
		flex-shrink: 0;
	}

	/* ── Avatar ── */
	.avatar {
		width: 2.4rem;
		height: 2.4rem;
		border-radius: 50%;
		object-fit: cover;
		flex-shrink: 0;
	}

	.avatar.sm {
		width: 1.9rem;
		height: 1.9rem;
	}

	.avatar.placeholder {
		background: #d0daf5;
		color: #0a3a8d;
		display: flex;
		align-items: center;
		justify-content: center;
		font-weight: 700;
		font-size: 0.9rem;
	}

	/* ── Detail drawer ── */
	.detail-drawer {
		border-top: 1px solid var(--line);
		padding: 1rem;
		background: #fff;
	}

	.detail-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
		gap: 1rem;
		margin-bottom: 1rem;
	}

	.detail-section p {
		margin: 0.15rem 0;
		font-size: 0.88rem;
		color: #333;
	}

	.section-label {
		font-size: 0.72rem !important;
		font-weight: 700;
		text-transform: uppercase;
		letter-spacing: 0.05em;
		color: var(--ink-soft) !important;
		margin-bottom: 0.3rem !important;
	}

	.experience-text {
		color: #555 !important;
		line-height: 1.5;
	}

	/* ── Actions ── */
	.actions {
		display: flex;
		gap: 0.6rem;
	}

	.actions button {
		border: 0;
		padding: 0.5rem 1.1rem;
		border-radius: 8px;
		cursor: pointer;
		font-weight: 700;
		font-size: 0.9rem;
		background: #0a3a8d;
		color: #fff;
	}

	.actions button:hover {
		background: #082f75;
	}

	.actions .danger {
		background: #9a1b2f;
	}

	.actions .danger:hover {
		background: #7c1020;
	}

	/* ── Badges ── */
	.badge {
		display: inline-block;
		padding: 0.25rem 0.65rem;
		border-radius: 999px;
		font-size: 0.75rem;
		font-weight: 700;
		flex-shrink: 0;
		background: #e8eef8;
		color: #0a3a8d;
	}

	.badge-active {
		background: #d4f0e4;
		color: #0d6e42;
	}

	.badge-pending {
		background: #fff3cd;
		color: #856404;
	}

	.badge-disabled {
		background: #f0e8e8;
		color: #7c1020;
	}

	/* ── Permissions table ── */
	.permissions-table {
		border: 1px solid var(--line);
		border-radius: 14px;
		overflow: hidden;
	}

	.pt-header,
	.pt-row {
		display: grid;
		grid-template-columns: 1fr 120px 140px;
		align-items: center;
		gap: 1rem;
		padding: 0.75rem 1rem;
	}

	.pt-header {
		background: #f5f6fa;
		font-size: 0.78rem;
		font-weight: 700;
		text-transform: uppercase;
		letter-spacing: 0.05em;
		color: var(--ink-soft);
		border-bottom: 1px solid var(--line);
	}

	.pt-row {
		border-bottom: 1px solid var(--line);
		background: #fff;
	}

	.pt-row:last-child {
		border-bottom: none;
	}

	.pt-row:hover {
		background: #fafaf8;
	}

	.pt-user {
		display: flex;
		align-items: center;
		gap: 0.65rem;
		min-width: 0;
	}

	.pt-name {
		display: block;
		font-weight: 600;
		font-size: 0.9rem;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.pt-row select {
		border: 1px solid #cfd4de;
		border-radius: 8px;
		padding: 0.35rem 0.55rem;
		font-size: 0.88rem;
		font-family: inherit;
		background: #fff;
		cursor: pointer;
		width: 100%;
	}

	.pt-row select:focus {
		outline: none;
		border-color: #0a3a8d;
	}

	/* ── Feedback messages ── */
	.status-msg {
		color: var(--ink-soft);
		margin: 0;
	}

	.error-msg {
		background: #fee;
		color: #7c1020;
		padding: 0.65rem 0.8rem;
		border-radius: 10px;
		margin: 0;
	}

	@media (max-width: 600px) {
		.pt-header,
		.pt-row {
			grid-template-columns: 1fr 90px 110px;
			gap: 0.5rem;
			padding: 0.65rem 0.75rem;
		}
	}
</style>
