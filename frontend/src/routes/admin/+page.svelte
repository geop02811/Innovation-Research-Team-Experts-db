<script lang="ts">
	import { onMount } from 'svelte';
	import { authService } from '$lib/auth/auth.service';
	import type { GrantItem, GrantPayload, EventItem, EventPayload, CompetitionItem, CompetitionPayload, AlumniNewsItem, AlumniNewsPayload } from '$lib/auth/auth.service';
	import type { AdminUser, UserRole } from '$lib/auth/types';

	let users = $state<AdminUser[]>([]);
	let activeTab = $state<'pending' | 'approved' | 'all' | 'grants' | 'events' | 'competitions' | 'alumni'>('pending');

	// ── Grants state ──────────────────────────────────────
	let grants = $state<GrantItem[]>([]);
	let grantsLoading = $state(false);
	let grantsError = $state('');
	let showGrantForm = $state(false);
	let editingGrant = $state<GrantItem | null>(null);

	const emptyForm = (): GrantPayload => ({
		funder: '', title: '', description: '', amount: '',
		closingDate: '', status: 'OPEN', category: '', applyUrl: '', featured: false
	});
	let grantForm = $state<GrantPayload>(emptyForm());
	let grantSaving = $state(false);
	let grantSaveError = $state('');

	const loadGrants = async () => {
		grantsLoading = true;
		grantsError = '';
		grants = await authService.getGrants();
		grantsLoading = false;
	};

	const openNewGrant = () => {
		editingGrant = null;
		grantForm = emptyForm();
		grantSaveError = '';
		showGrantForm = true;
	};

	const openEditGrant = (g: GrantItem) => {
		editingGrant = g;
		grantForm = {
			funder: g.funder, title: g.title, description: g.description ?? '',
			amount: g.amount ?? '', closingDate: g.closingDate ?? '',
			status: g.status, category: g.category ?? '',
			applyUrl: g.applyUrl ?? '', featured: g.featured
		};
		grantSaveError = '';
		showGrantForm = true;
	};

	const cancelGrantForm = () => { showGrantForm = false; editingGrant = null; };

	const saveGrant = async () => {
		grantSaving = true;
		grantSaveError = '';
		const result = editingGrant
			? await authService.updateGrant(editingGrant.id, grantForm)
			: await authService.createGrant(grantForm);
		grantSaving = false;
		if (!result.ok) { grantSaveError = result.message ?? 'Save failed.'; return; }
		showGrantForm = false;
		editingGrant = null;
		await loadGrants();
	};

	const deleteGrant = async (id: string) => {
		if (!confirm('Delete this grant?')) return;
		await authService.deleteGrant(id);
		await loadGrants();
	};
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

	onMount(() => { loadUsers(); loadGrants(); loadEvents(); loadCompetitions(); loadAlumniNews(); });

	// ── Events state ──────────────────────────────────────
	let events = $state<EventItem[]>([]);
	let showEventForm = $state(false); let editingEvent = $state<EventItem | null>(null);
	let eventSaving = $state(false); let eventSaveError = $state('');
	const emptyEventForm = (): EventPayload => ({ title:'', description:'', eventDate:'', category:'', location:'' });
	let eventForm = $state<EventPayload>(emptyEventForm());
	const loadEvents = async () => { events = await authService.getEvents(); };
	const openNewEvent = () => { editingEvent=null; eventForm=emptyEventForm(); eventSaveError=''; showEventForm=true; };
	const openEditEvent = (e: EventItem) => { editingEvent=e; eventForm={title:e.title, description:e.description??'', eventDate:e.eventDate??'', category:e.category??'', location:e.location??''}; eventSaveError=''; showEventForm=true; };
	const cancelEventForm = () => { showEventForm=false; editingEvent=null; };
	const saveEvent = async () => { eventSaving=true; eventSaveError=''; const r = editingEvent ? await authService.updateEvent(editingEvent.id, eventForm) : await authService.createEvent(eventForm); eventSaving=false; if(!r.ok){eventSaveError=r.message??'Save failed.'; return;} showEventForm=false; editingEvent=null; await loadEvents(); };
	const deleteEvent = async (id: string) => { if(!confirm('Delete this running project?')) return; await authService.deleteEvent(id); await loadEvents(); };

	// ── Competitions state ───────────────────────────────
	let competitions = $state<CompetitionItem[]>([]);
	let showCompForm = $state(false); let editingComp = $state<CompetitionItem | null>(null);
	let compSaving = $state(false); let compSaveError = $state('');
	const emptyCompForm = (): CompetitionPayload => ({ title:'', description:'', deadline:'', status:'OPEN', prize:'', ctaLabel:'', ctaUrl:'' });
	let compForm = $state<CompetitionPayload>(emptyCompForm());
	const loadCompetitions = async () => { competitions = await authService.getCompetitions(); };
	const openNewComp = () => { editingComp=null; compForm=emptyCompForm(); compSaveError=''; showCompForm=true; };
	const openEditComp = (c: CompetitionItem) => { editingComp=c; compForm={title:c.title, description:c.description??'', deadline:c.deadline??'', status:c.status, prize:c.prize??'', ctaLabel:c.ctaLabel??'', ctaUrl:c.ctaUrl??''}; compSaveError=''; showCompForm=true; };
	const cancelCompForm = () => { showCompForm=false; editingComp=null; };
	const saveComp = async () => { compSaving=true; compSaveError=''; const r = editingComp ? await authService.updateCompetition(editingComp.id, compForm) : await authService.createCompetition(compForm); compSaving=false; if(!r.ok){compSaveError=r.message??'Save failed.'; return;} showCompForm=false; editingComp=null; await loadCompetitions(); };
	const deleteComp = async (id: string) => { if(!confirm('Delete this internal grant?')) return; await authService.deleteCompetition(id); await loadCompetitions(); };

	// ── Alumni News state ────────────────────────────────
	let alumniNewsList = $state<AlumniNewsItem[]>([]);
	let showAlumniForm = $state(false); let editingAlumni = $state<AlumniNewsItem | null>(null);
	let alumniSaving = $state(false); let alumniSaveError = $state('');
	const emptyAlumniForm = (): AlumniNewsPayload => ({ personName:'', headline:'', body:'', newsDate:'' });
	let alumniForm = $state<AlumniNewsPayload>(emptyAlumniForm());
	const loadAlumniNews = async () => { alumniNewsList = await authService.getAlumniNews(); };
	const openNewAlumni = () => { editingAlumni=null; alumniForm=emptyAlumniForm(); alumniSaveError=''; showAlumniForm=true; };
	const openEditAlumni = (a: AlumniNewsItem) => { editingAlumni=a; alumniForm={personName:a.personName, headline:a.headline, body:a.body??'', newsDate:a.newsDate??''}; alumniSaveError=''; showAlumniForm=true; };
	const cancelAlumniForm = () => { showAlumniForm=false; editingAlumni=null; };
	const saveAlumni = async () => { alumniSaving=true; alumniSaveError=''; const r = editingAlumni ? await authService.updateAlumniNews(editingAlumni.id, alumniForm) : await authService.createAlumniNews(alumniForm); alumniSaving=false; if(!r.ok){alumniSaveError=r.message??'Save failed.'; return;} showAlumniForm=false; editingAlumni=null; await loadAlumniNews(); };
	const deleteAlumni = async (id: string) => { if(!confirm('Delete this news item?')) return; await authService.deleteAlumniNews(id); await loadAlumniNews(); };

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
			<button class:active={activeTab === 'grants'} onclick={() => (activeTab = 'grants')}>Grants</button>
			<button class:active={activeTab === 'events'} onclick={() => (activeTab = 'events')}>Running Projects</button>
			<button class:active={activeTab === 'competitions'} onclick={() => (activeTab = 'competitions')}>Internal Grants</button>
			<button class:active={activeTab === 'alumni'} onclick={() => (activeTab = 'alumni')}>Alumni News</button>
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

		<!-- ── Running Projects tab ── -->
		{#if activeTab === 'events'}
			<section class="panel">
				<div class="grants-toolbar">
					<h2 class="grants-heading">Running Projects</h2>
					<button type="button" class="btn-add-grant" onclick={openNewEvent}>+ Add Running Project</button>
				</div>
				{#if showEventForm}
					<div class="grant-form-card">
						<h3 class="grant-form-title">{editingEvent ? 'Edit Running Project' : 'New Running Project'}</h3>
						<div class="grant-form-grid">
							<label class="gf-label gf-full">Title *<input class="gf-input" type="text" bind:value={eventForm.title} placeholder="Event title" /></label>
							<label class="gf-label">Date<input class="gf-input" type="date" bind:value={eventForm.eventDate} /></label>
							<label class="gf-label">Category<input class="gf-input" type="text" bind:value={eventForm.category} placeholder="e.g. Seminar, Workshop" /></label>
							<label class="gf-label gf-full">Location<input class="gf-input" type="text" bind:value={eventForm.location} placeholder="e.g. Senate Chamber, UZ Main Campus" /></label>
							<label class="gf-label gf-full">Description<textarea class="gf-input gf-textarea" bind:value={eventForm.description}></textarea></label>
						</div>
						{#if eventSaveError}<p class="error-msg" style="margin-top:.75rem">{eventSaveError}</p>{/if}
						<div class="gf-actions">
							<button type="button" class="btn-save" onclick={saveEvent} disabled={eventSaving}>{eventSaving ? 'Saving...' : editingEvent ? 'Save Changes' : 'Create Running Project'}</button>
							<button type="button" class="btn-cancel" onclick={cancelEventForm}>Cancel</button>
						</div>
					</div>
				{/if}
				{#if events.length === 0 && !showEventForm}<p class="empty-msg">No running projects yet.</p>{/if}
				{#each events as ev (ev.id)}
					<div class="grant-row">
						<div class="grant-row-left">
							{#if ev.eventDate}<span class="grant-row-date">{ev.eventDate}</span>{/if}
							{#if ev.category}<span class="grant-row-status grant-row-status-open">{ev.category}</span>{/if}
							<span class="grant-row-title">{ev.title}</span>
							{#if ev.location}<span class="grant-row-funder">📍 {ev.location}</span>{/if}
						</div>
						<div class="grant-row-actions">
							<button type="button" class="btn-edit" onclick={() => openEditEvent(ev)}>Edit</button>
							<button type="button" class="btn-delete" onclick={() => deleteEvent(ev.id)}>Delete</button>
						</div>
					</div>
				{/each}
			</section>
		{/if}

		<!-- ── Internal Grants tab ── -->
		{#if activeTab === 'competitions'}
			<section class="panel">
				<div class="grants-toolbar">
					<h2 class="grants-heading">Internal Grants</h2>
					<button type="button" class="btn-add-grant" onclick={openNewComp}>+ Add Internal Grant</button>
				</div>
				{#if showCompForm}
					<div class="grant-form-card">
						<h3 class="grant-form-title">{editingComp ? 'Edit Internal Grant' : 'New Internal Grant'}</h3>
						<div class="grant-form-grid">
							<label class="gf-label gf-full">Title *<input class="gf-input" type="text" bind:value={compForm.title} /></label>
							<label class="gf-label">Deadline<input class="gf-input" type="date" bind:value={compForm.deadline} /></label>
							<label class="gf-label">Status
								<select class="gf-input" bind:value={compForm.status}>
									<option value="OPEN">Open</option>
									<option value="UPCOMING">Upcoming</option>
									<option value="CLOSED">Closed</option>
								</select>
							</label>
							<label class="gf-label">Prize / Award<input class="gf-input" type="text" bind:value={compForm.prize} placeholder="e.g. USD 5,000 + mentorship" /></label>
							<label class="gf-label">CTA Button Label<input class="gf-input" type="text" bind:value={compForm.ctaLabel} placeholder="e.g. Find Collaborators" /></label>
							<label class="gf-label">CTA URL<input class="gf-input" type="url" bind:value={compForm.ctaUrl} placeholder="https://..." /></label>
							<label class="gf-label gf-full">Description<textarea class="gf-input gf-textarea" bind:value={compForm.description}></textarea></label>
						</div>
						{#if compSaveError}<p class="error-msg" style="margin-top:.75rem">{compSaveError}</p>{/if}
						<div class="gf-actions">
							<button type="button" class="btn-save" onclick={saveComp} disabled={compSaving}>{compSaving ? 'Saving...' : editingComp ? 'Save Changes' : 'Create Internal Grant'}</button>
							<button type="button" class="btn-cancel" onclick={cancelCompForm}>Cancel</button>
						</div>
					</div>
				{/if}
				{#if competitions.length === 0 && !showCompForm}<p class="empty-msg">No internal grants yet.</p>{/if}
				{#each competitions as comp (comp.id)}
					<div class="grant-row">
						<div class="grant-row-left">
							<span class="grant-row-status grant-row-status-{comp.status.toLowerCase()}">{comp.status}</span>
							<span class="grant-row-title">{comp.title}</span>
							{#if comp.deadline}<span class="grant-row-date">Deadline: {comp.deadline}</span>{/if}
						</div>
						<div class="grant-row-actions">
							<button type="button" class="btn-edit" onclick={() => openEditComp(comp)}>Edit</button>
							<button type="button" class="btn-delete" onclick={() => deleteComp(comp.id)}>Delete</button>
						</div>
					</div>
				{/each}
			</section>
		{/if}

		<!-- ── Alumni News tab ── -->
		{#if activeTab === 'alumni'}
			<section class="panel">
				<div class="grants-toolbar">
					<h2 class="grants-heading">Alumni News</h2>
					<button type="button" class="btn-add-grant" onclick={openNewAlumni}>+ Add News Item</button>
				</div>
				{#if showAlumniForm}
					<div class="grant-form-card">
						<h3 class="grant-form-title">{editingAlumni ? 'Edit News Item' : 'New Alumni News'}</h3>
						<div class="grant-form-grid">
							<label class="gf-label">Person Name *<input class="gf-input" type="text" bind:value={alumniForm.personName} placeholder="e.g. Dr. Tatenda Mawere" /></label>
							<label class="gf-label">Date<input class="gf-input" type="text" bind:value={alumniForm.newsDate} placeholder="e.g. March 2026" /></label>
							<label class="gf-label gf-full">Headline *<input class="gf-input" type="text" bind:value={alumniForm.headline} placeholder="Short headline" /></label>
							<label class="gf-label gf-full">Body<textarea class="gf-input gf-textarea" bind:value={alumniForm.body} placeholder="Full story..."></textarea></label>
						</div>
						{#if alumniSaveError}<p class="error-msg" style="margin-top:.75rem">{alumniSaveError}</p>{/if}
						<div class="gf-actions">
							<button type="button" class="btn-save" onclick={saveAlumni} disabled={alumniSaving}>{alumniSaving ? 'Saving...' : editingAlumni ? 'Save Changes' : 'Create News Item'}</button>
							<button type="button" class="btn-cancel" onclick={cancelAlumniForm}>Cancel</button>
						</div>
					</div>
				{/if}
				{#if alumniNewsList.length === 0 && !showAlumniForm}<p class="empty-msg">No alumni news yet.</p>{/if}
				{#each alumniNewsList as item (item.id)}
					<div class="grant-row">
						<div class="grant-row-left">
							{#if item.newsDate}<span class="grant-row-date">{item.newsDate}</span>{/if}
							<span class="grant-row-title">{item.headline}</span>
							<span class="grant-row-funder">{item.personName}</span>
						</div>
						<div class="grant-row-actions">
							<button type="button" class="btn-edit" onclick={() => openEditAlumni(item)}>Edit</button>
							<button type="button" class="btn-delete" onclick={() => deleteAlumni(item.id)}>Delete</button>
						</div>
					</div>
				{/each}
			</section>
		{/if}

		<!-- ── Grants tab ── -->
		{#if activeTab === 'grants'}
			<section class="panel">
				<div class="grants-toolbar">
					<h2 class="grants-heading">Grants &amp; Funding</h2>
					<button type="button" class="btn-add-grant" onclick={openNewGrant}>+ Add Grant</button>
				</div>

				{#if grantsLoading}
					<p class="status-msg">Loading grants...</p>
				{/if}

				{#if grantsError}
					<p class="error-msg">{grantsError}</p>
				{/if}

				<!-- Add / Edit form -->
				{#if showGrantForm}
					<div class="grant-form-card">
						<h3 class="grant-form-title">{editingGrant ? 'Edit Grant' : 'New Grant'}</h3>
						<div class="grant-form-grid">
							<label class="gf-label">
								Funder *
								<input class="gf-input" type="text" bind:value={grantForm.funder} placeholder="e.g. Wellcome Trust" />
							</label>
							<label class="gf-label">
								Title *
								<input class="gf-input" type="text" bind:value={grantForm.title} placeholder="Grant title" />
							</label>
							<label class="gf-label gf-full">
								Description
								<textarea class="gf-input gf-textarea" bind:value={grantForm.description} placeholder="Brief description of the grant..."></textarea>
							</label>
							<label class="gf-label">
								Amount
								<input class="gf-input" type="text" bind:value={grantForm.amount} placeholder="e.g. USD 50,000" />
							</label>
							<label class="gf-label">
								Closing Date
								<input class="gf-input" type="date" bind:value={grantForm.closingDate} />
							</label>
							<label class="gf-label">
								Status
								<select class="gf-input" bind:value={grantForm.status}>
									<option value="OPEN">Open</option>
									<option value="UPCOMING">Upcoming</option>
									<option value="CLOSED">Closed</option>
									<option value="RUNNING">Running</option>
									<option value="INTERNAL">Internal</option>
								</select>
							</label>
							<label class="gf-label">
								Category
								<input class="gf-input" type="text" bind:value={grantForm.category} placeholder="e.g. Health & Medicine" />
							</label>
							<label class="gf-label gf-full">
								Apply / More Info URL
								<input class="gf-input" type="url" bind:value={grantForm.applyUrl} placeholder="https://..." />
							</label>
							<label class="gf-check">
								<input type="checkbox" bind:checked={grantForm.featured} />
								Featured (shows ribbon on card)
							</label>
						</div>

						{#if grantSaveError}
							<p class="error-msg" style="margin-top:0.75rem">{grantSaveError}</p>
						{/if}

						<div class="gf-actions">
							<button type="button" class="btn-save" onclick={saveGrant} disabled={grantSaving}>
								{grantSaving ? 'Saving...' : editingGrant ? 'Save Changes' : 'Create Grant'}
							</button>
							<button type="button" class="btn-cancel" onclick={cancelGrantForm}>Cancel</button>
						</div>
					</div>
				{/if}

				<!-- Grants list -->
				{#if !grantsLoading && grants.length === 0 && !showGrantForm}
					<p class="empty-msg">No grants yet. Click "+ Add Grant" to create one.</p>
				{/if}

				{#each grants as grant (grant.id)}
					<div class="grant-row">
						<div class="grant-row-left">
							<span class="grant-row-status grant-row-status-{grant.status.toLowerCase()}">{grant.status}</span>
							<span class="grant-row-funder">{grant.funder}</span>
							<span class="grant-row-title">{grant.title}</span>
							{#if grant.closingDate}<span class="grant-row-date">Closes: {grant.closingDate}</span>{/if}
						</div>
						<div class="grant-row-actions">
							<button type="button" class="btn-edit" onclick={() => openEditGrant(grant)}>Edit</button>
							<button type="button" class="btn-delete" onclick={() => deleteGrant(grant.id)}>Delete</button>
						</div>
					</div>
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

	/* ── Grants admin ── */
	.grants-toolbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		flex-wrap: wrap;
	}
	.grants-heading { margin: 0; font-size: 1.05rem; font-weight: 700; }
	.btn-add-grant {
		padding: 0.5rem 1.1rem;
		background: var(--uz-navy);
		color: #fff;
		border: none;
		border-radius: 8px;
		font-size: 0.88rem;
		font-weight: 700;
		cursor: pointer;
	}
	.btn-add-grant:hover { background: var(--uz-navy-deep); }

	/* Form card */
	.grant-form-card {
		border: 1px solid #d0d8f0;
		border-radius: 10px;
		padding: 1.25rem;
		background: #f7f9ff;
	}
	.grant-form-title { margin: 0 0 1rem; font-size: 1rem; font-weight: 700; color: var(--uz-navy); }
	.grant-form-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 0.75rem;
	}
	.gf-label {
		display: flex;
		flex-direction: column;
		gap: 0.25rem;
		font-size: 0.82rem;
		font-weight: 600;
		color: var(--ink);
	}
	.gf-full { grid-column: 1 / -1; }
	.gf-input {
		padding: 0.5rem 0.75rem;
		border: 1px solid #c8d2e6;
		border-radius: 6px;
		font-size: 0.88rem;
		color: var(--ink);
		background: #fff;
		font-family: inherit;
	}
	.gf-input:focus { outline: none; border-color: var(--uz-navy); }
	.gf-textarea { min-height: 80px; resize: vertical; }
	.gf-check {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font-size: 0.85rem;
		font-weight: 500;
		grid-column: 1 / -1;
		cursor: pointer;
	}
	.gf-actions { display: flex; gap: 0.6rem; margin-top: 1rem; }
	.btn-save {
		padding: 0.5rem 1.2rem;
		background: var(--uz-navy);
		color: #fff;
		border: none;
		border-radius: 6px;
		font-weight: 700;
		font-size: 0.88rem;
		cursor: pointer;
	}
	.btn-save:disabled { opacity: 0.5; cursor: default; }
	.btn-cancel {
		padding: 0.5rem 1rem;
		background: #fff;
		border: 1px solid #c8d2e6;
		border-radius: 6px;
		font-size: 0.88rem;
		cursor: pointer;
		color: var(--ink-soft);
	}

	/* Grant row */
	.grant-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		padding: 0.85rem 1rem;
		border: 1px solid #e0e4f0;
		border-radius: 8px;
		background: #fff;
		flex-wrap: wrap;
	}
	.grant-row-left {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		flex-wrap: wrap;
		min-width: 0;
	}
	.grant-row-status {
		font-size: 0.68rem;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.08em;
		padding: 0.18rem 0.55rem;
		border-radius: 100px;
		flex-shrink: 0;
	}
	.grant-row-status-open     { background: #e0f0ff; color: #1a3a6b; }
	.grant-row-status-upcoming { background: #fff3dc; color: #8a5a00; }
	.grant-row-status-closed   { background: #f0f0f0; color: #666; }
	.grant-row-funder { font-size: 0.8rem; color: var(--ink-soft); }
	.grant-row-title { font-size: 0.9rem; font-weight: 600; color: var(--ink); }
	.grant-row-date { font-size: 0.78rem; color: var(--ink-soft); }
	.grant-row-actions { display: flex; gap: 0.5rem; flex-shrink: 0; }
	.btn-edit {
		padding: 0.35rem 0.8rem;
		border: 1px solid #c8d2e6;
		background: #fff;
		border-radius: 6px;
		font-size: 0.82rem;
		font-weight: 600;
		cursor: pointer;
		color: var(--uz-navy);
	}
	.btn-edit:hover { background: #eef2ff; }
	.btn-delete {
		padding: 0.35rem 0.8rem;
		border: 1px solid #f5c6c6;
		background: #fff;
		border-radius: 6px;
		font-size: 0.82rem;
		font-weight: 600;
		cursor: pointer;
		color: #c0392b;
	}
	.btn-delete:hover { background: #fef0f0; }

	@media (max-width: 600px) {
		.grant-form-grid { grid-template-columns: 1fr; }
		.gf-full { grid-column: 1; }
	}
</style>
