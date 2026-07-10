<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { browser } from '$app/environment';
	import { authService } from '$lib/auth/auth.service';
	import type {
		AdminNotificationsResponse,
		ViewerNotificationsResponse,
		UserRole
	} from '$lib/auth/types';

	let { role }: { role: UserRole } = $props();

	// ── State ──────────────────────────────────────────────
	let open = $state(false);
	let adminData = $state<AdminNotificationsResponse | null>(null);
	let viewerData = $state<ViewerNotificationsResponse | null>(null);
	let profileReminder = $state<{ incomplete: boolean; completion: number } | null>(null);
	let loading = $state(false);

	// Total badge count
	const badgeCount = $derived.by(() => {
		const reminderCount = profileReminder?.incomplete ? 1 : 0;
		if (role === 'ADMIN') return (adminData?.pendingCount ?? 0) + reminderCount;
		return (viewerData?.newExpertsCount ?? 0) + reminderCount;
	});

	const hasItems = $derived(badgeCount > 0);

	const getArrayLength = (value: unknown) => (Array.isArray(value) ? value.length : 0);
	const getJsonArrayLength = (value: unknown) => {
		if (Array.isArray(value)) return value.length;
		if (typeof value !== 'string' || !value.trim()) return 0;
		try {
			const parsed = JSON.parse(value);
			return Array.isArray(parsed) ? parsed.length : 0;
		} catch {
			return 0;
		}
	};

	const calculateProfileCompletion = (profile: Record<string, unknown>) => {
		const checks = [
			profile.profilePhotoDataUrl,
			profile.fullName,
			profile.email,
			profile.phoneNumber,
			profile.academicRank,
			profile.highestQualification,
			profile.faculty,
			profile.department,
			profile.researchInterests,
			getArrayLength(profile.skillsAndCompetences) > 0,
			getJsonArrayLength(profile.languageProficiencies) > 0,
			getJsonArrayLength(profile.professionalExperiences) > 0,
			profile.bio
		];

		return Math.round((checks.filter(Boolean).length / checks.length) * 100);
	};

	// ── Fetch ──────────────────────────────────────────────
	const fetchNotifications = async () => {
		loading = true;
		const profile = await authService.getProfile();
		if (profile) {
			const completion = calculateProfileCompletion(profile);
			profileReminder = { incomplete: completion < 100, completion };
		}
		if (role === 'ADMIN') {
			adminData = await authService.getAdminNotifications();
		} else {
			viewerData = await authService.getViewerNotifications();
		}
		loading = false;
	};

	// ── Close on outside click ─────────────────────────────
	const handleOutsideClick = (e: MouseEvent) => {
		const target = e.target as HTMLElement;
		if (!target.closest('.notif-bell-wrapper')) {
			open = false;
		}
	};

	// ── Polling every 60 s ────────────────────────────────
	let pollInterval: ReturnType<typeof setInterval>;

	onMount(() => {
		fetchNotifications();
		pollInterval = setInterval(fetchNotifications, 60_000);
		document.addEventListener('click', handleOutsideClick);
		return () => {
			document.removeEventListener('click', handleOutsideClick);
		};
	});

	onDestroy(() => {
		if (browser) clearInterval(pollInterval);
	});

	const toggle = () => {
		open = !open;
		if (open && !loading) fetchNotifications();
	};
</script>

<div class="notif-bell-wrapper">
	<button class="bell-btn" onclick={toggle} aria-label="Notifications" title="Notifications">
		<svg
			width="20"
			height="20"
			viewBox="0 0 24 24"
			fill="none"
			stroke="currentColor"
			stroke-width="2"
			stroke-linecap="round"
			stroke-linejoin="round"
			aria-hidden="true"
		>
			<path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
			<path d="M13.73 21a2 2 0 0 1-3.46 0" />
		</svg>
		{#if badgeCount > 0}
			<span class="badge" aria-label="{badgeCount} notifications">{badgeCount > 99 ? '99+' : badgeCount}</span>
		{/if}
	</button>

	{#if open}
		<div class="notif-panel" role="dialog" aria-label="Notifications panel">
			<div class="notif-header">
				<span class="notif-title">Notifications</span>
				{#if hasItems}
					<span class="notif-count-label">{badgeCount} new</span>
				{/if}
			</div>

			{#if loading}
				<div class="notif-empty">Loading…</div>
			{:else}
				{#if profileReminder?.incomplete}
					<a class="notif-item profile-reminder" href="/profile" onclick={() => (open = false)}>
						<div class="notif-avatar">%</div>
						<div class="notif-body">
							<span class="notif-name">Complete your profile</span>
							<span class="notif-meta">Your profile is {profileReminder.completion}% complete.</span>
							<span class="notif-tags">Add missing details so collaborators can assess your work.</span>
						</div>
					</a>
				{/if}

			<!-- ── ADMIN VIEW ─────────────────────────────── -->
			{#if role === 'ADMIN'}
				{#if (adminData?.pendingCount ?? 0) === 0 && !profileReminder?.incomplete}
					<div class="notif-empty">No pending approvals right now.</div>
				{:else if (adminData?.pendingCount ?? 0) > 0}
					<div class="notif-section-label">
						<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
						Pending approvals
					</div>
					{#each adminData!.pendingUsers as user (user.id)}
						<a class="notif-item" href="/admin" onclick={() => (open = false)}>
							<div class="notif-avatar">{user.displayName.charAt(0).toUpperCase()}</div>
							<div class="notif-body">
								<span class="notif-name">{user.displayName}</span>
								<span class="notif-meta">
									{[user.academicRank, user.faculty].filter(Boolean).join(' · ')}
								</span>
								<span class="notif-time">{user.joinedAgo}</span>
							</div>
						</a>
					{/each}
					<a class="notif-footer-link" href="/admin" onclick={() => (open = false)}>
						View all in Admin Dashboard →
					</a>
				{/if}

			<!-- ── VIEWER / EXPERT VIEW ───────────────────── -->
			{:else}
				{#if (viewerData?.newExpertsCount ?? 0) === 0 && !profileReminder?.incomplete}
					<div class="notif-empty">No new researchers in the last 30 days.</div>
				{:else if (viewerData?.newExpertsCount ?? 0) > 0}
					<div class="notif-section-label">
						<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
						New researchers joined
					</div>
					{#each viewerData!.newExperts as expert (expert.id)}
					<a class="notif-item" href="/experts/{expert.fullName.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '')}" onclick={() => (open = false)}>
							<div class="notif-avatar">{expert.displayName.charAt(0).toUpperCase()}</div>
							<div class="notif-body">
								<span class="notif-name">{expert.displayName}</span>
								<span class="notif-meta">
									{[expert.academicRank, expert.faculty].filter(Boolean).join(' · ')}
								</span>
								{#if expert.areasOfExpertise}
									<span class="notif-tags">
										{expert.areasOfExpertise.split(',').slice(0, 2).map(s => s.trim()).join(', ')}
									</span>
								{/if}
								<span class="notif-time">{expert.joinedAgo}</span>
							</div>
						</a>
					{/each}
					<a class="notif-footer-link" href="/experts" onclick={() => (open = false)}>
						Browse all Researchers →
					</a>
				{/if}
			{/if}
			{/if}
		</div>
	{/if}
</div>

<style>
	.notif-bell-wrapper {
		position: relative;
		display: inline-flex;
		align-items: center;
	}

	.bell-btn {
		position: relative;
		background: none;
		border: none;
		cursor: pointer;
		padding: 6px;
		color: #fff;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: background 0.15s;
	}

	.bell-btn:hover {
		background: rgba(255, 255, 255, 0.15);
	}

	.badge {
		position: absolute;
		top: 0;
		right: 0;
		background: #e53e3e;
		color: #fff;
		font-size: 10px;
		font-weight: 700;
		min-width: 17px;
		height: 17px;
		border-radius: 9px;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 3px;
		line-height: 1;
		border: 2px solid var(--uz-navy, #1b2b4e);
		pointer-events: none;
	}

	/* ── Panel ───────────────────────────────────────────── */
	.notif-panel {
		position: absolute;
		top: calc(100% + 10px);
		right: 0;
		width: 320px;
		max-height: 480px;
		overflow-y: auto;
		background: #fff;
		border-radius: 12px;
		box-shadow: 0 8px 32px rgba(27, 43, 78, 0.18);
		z-index: 1000;
		display: flex;
		flex-direction: column;
	}

	.notif-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 14px 16px 10px;
		border-bottom: 1px solid #eee;
		position: sticky;
		top: 0;
		background: #fff;
		border-radius: 12px 12px 0 0;
	}

	.notif-title {
		font-weight: 700;
		font-size: 14px;
		color: #1a1a2e;
	}

	.notif-count-label {
		font-size: 11px;
		font-weight: 600;
		background: #fff0e4;
		color: #f47920;
		padding: 2px 8px;
		border-radius: 20px;
	}

	.notif-section-label {
		display: flex;
		align-items: center;
		gap: 6px;
		font-size: 11px;
		font-weight: 700;
		text-transform: uppercase;
		letter-spacing: 0.06em;
		color: #8a8fa8;
		padding: 10px 16px 4px;
	}

	.notif-item {
		display: flex;
		align-items: flex-start;
		gap: 10px;
		padding: 10px 16px;
		text-decoration: none;
		color: inherit;
		transition: background 0.12s;
		cursor: pointer;
		border-bottom: 1px solid #f5f5f7;
	}

	.notif-item:hover {
		background: #fafafa;
	}

	.notif-avatar {
		width: 34px;
		height: 34px;
		min-width: 34px;
		border-radius: 50%;
		background: linear-gradient(135deg, #1b2b4e, #f47920);
		color: #fff;
		font-weight: 700;
		font-size: 14px;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 2px;
	}

	.notif-body {
		display: flex;
		flex-direction: column;
		gap: 2px;
		min-width: 0;
	}

	.notif-name {
		font-size: 13px;
		font-weight: 600;
		color: #1a1a2e;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.notif-meta {
		font-size: 12px;
		color: #6b7280;
	}

	.notif-tags {
		font-size: 11px;
		color: #f47920;
		font-weight: 500;
	}

	.notif-time {
		font-size: 11px;
		color: #aaa;
	}

	.notif-empty {
		padding: 24px 16px;
		text-align: center;
		color: #8a8fa8;
		font-size: 13px;
	}

	.notif-footer-link {
		display: block;
		padding: 12px 16px;
		text-align: center;
		font-size: 12px;
		font-weight: 600;
		color: #f47920;
		text-decoration: none;
		border-top: 1px solid #eee;
		position: sticky;
		bottom: 0;
		background: #fff;
		border-radius: 0 0 12px 12px;
		transition: background 0.12s;
	}

	.notif-footer-link:hover {
		background: #fff7f0;
	}

	/* ── Mobile adjustment ───────────────────────────────── */
	@media (max-width: 700px) {
		.notif-panel {
			right: -60px;
			width: 290px;
		}
	}
</style>
