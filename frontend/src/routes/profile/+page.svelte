<script lang="ts">
	import ComplianceCredentialsEditor from '$lib/components/ComplianceCredentialsEditor.svelte';
	import ExperienceEntriesEditor from '$lib/components/ExperienceEntriesEditor.svelte';
	import LanguageProficiencyField from '$lib/components/LanguageProficiencyField.svelte';
	import ProfessionalMembershipsEditor from '$lib/components/ProfessionalMembershipsEditor.svelte';
	import ProfileLinksEditor from '$lib/components/ProfileLinksEditor.svelte';
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import TagFilter from '$lib/components/scholars/TagFilter.svelte';
	import { authService } from '$lib/auth/auth.service';
	import {
		credentialIssueDate,
		membershipOrganizationName,
		membershipPositionHeld,
		membershipPeriod,
		parseComplianceCredentials,
		parseProfessionalMemberships,
		serializeComplianceCredentials,
		serializeProfessionalMemberships
	} from '$lib/auth/structured-profile-fields';
	import {
		titlePrefixOptions,
		academicRankOptions,
		highestQualificationOptions,
		facultyOptions,
		getDepartmentOptionsByFaculty,
		yearsOfConsultancyOptions,
		consultancyAvailabilityOptions,
		employmentTypeOptions,
		preferredConsultancyTypeOptions,
		geographicScopeOptions,
		skillsOptions,
		languageOptions,
		languageProficiencyOptions,
		locationTypeOptions,
		membershipPositionOptions,
		monthOptions,
		professionalMembershipOrganizationOptions,
		profileLinkTypeOptions,
		yearOptions,
		areasOfExpertiseOptions,
		industrialAreasOptions
	} from '$lib/auth/form-options';
	import type {
		ComplianceCredential,
		LanguageProficiency,
		ProfessionalExperience,
		ProfessionalMembership,
		ProfileLink
	} from '$lib/auth/types';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	type Section = 'personal' | 'academic' | 'consultancy' | 'expertise' | 'links' | 'biography';

	let activeSection = $state<Section>('personal');
	let isEditing = $state(false);
	let isSaving = $state(false);
	let saveError = $state('');
	const PROFILE_PHOTO_SIZE = 400;

	const sections: { id: Section; label: string; icon: string }[] = [
		{
			id: 'personal',
			label: 'Personal Details',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="5"/><path d="M3 21a9 9 0 0 1 18 0"/></svg>`
		},
		{
			id: 'academic',
			label: 'Academic Profile',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>`
		},
		{
			id: 'consultancy',
			label: 'Experience',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>`
		},
		{
			id: 'expertise',
			label: 'Expertise & Skills',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`
		},
		{
			id: 'links',
			label: 'Links & Publications',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>`
		},
		{
			id: 'biography',
			label: 'Bio & Notes',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`
		}
	];

	const normalizedKey = (value: string) => value.trim().toLowerCase();
	const parseLanguageProficiencies = (
		value: unknown,
		languages: string[]
	): LanguageProficiency[] => {
		let parsed: unknown = value;
		if (typeof value === 'string' && value.trim()) {
			try {
				parsed = JSON.parse(value);
			} catch {
				parsed = [];
			}
		}

		const entries = Array.isArray(parsed)
			? parsed.filter(
					(item): item is LanguageProficiency =>
						typeof item === 'object' &&
						item !== null &&
						typeof (item as LanguageProficiency).language === 'string' &&
						typeof (item as LanguageProficiency).proficiency === 'string'
				)
			: [];

		return languages.map((language) => {
			const entry = entries.find((item) => normalizedKey(item.language) === normalizedKey(language));
			return { language, proficiency: entry?.proficiency ?? '' };
		});
	};

	const parseJsonArray = (value: unknown): unknown[] => {
		if (Array.isArray(value)) return value;
		if (typeof value !== 'string' || !value.trim()) return [];
		try {
			const parsed = JSON.parse(value);
			return Array.isArray(parsed) ? parsed : [];
		} catch {
			return [];
		}
	};

	const parseLegacyExperienceSummary = (summary: string): ProfessionalExperience[] =>
		summary
			.split(/\n{2,}|\s+\.\s+(?=[^.:]{2,90}\sat\s[^:]{2,120}:)/)
			.map((entry) => entry.trim().replace(/^\.+\s*/, ''))
			.filter(Boolean)
			.map((entry): ProfessionalExperience => {
				const match = entry.match(/^(.+?)\s+at\s+(.+?):\s*(.+)$/i);
				return {
					title: match?.[1]?.trim() || 'Experience',
					employmentType: '',
					organization: match?.[2]?.trim() || '',
					isCurrent: false,
					startMonth: '',
					startYear: '',
					endMonth: '',
					endYear: '',
					location: '',
					locationType: '',
					summary: (match?.[3] ?? entry).trim()
				};
			});

	const parseProfessionalExperiences = (
		value: unknown,
		fallbackSummary: string | null | undefined
	): ProfessionalExperience[] => {
		const entries = parseJsonArray(value).filter(
			(item): item is ProfessionalExperience =>
				typeof item === 'object' &&
				item !== null &&
				typeof (item as ProfessionalExperience).title === 'string' &&
				typeof (item as ProfessionalExperience).organization === 'string' &&
				typeof (item as ProfessionalExperience).summary === 'string'
		);

		if (entries.length > 0 || !fallbackSummary) return entries;
		return parseLegacyExperienceSummary(fallbackSummary);
	};

	const parseProfileLinks = (value: unknown): ProfileLink[] =>
		parseJsonArray(value).filter(
			(item): item is ProfileLink =>
				typeof item === 'object' &&
				item !== null &&
				typeof (item as ProfileLink).type === 'string' &&
				typeof (item as ProfileLink).url === 'string'
		);
	const asProfessionalMemberships = (value: unknown): ProfessionalMembership[] =>
		parseProfessionalMemberships(value);
	const asComplianceCredentials = (value: unknown): ComplianceCredential[] =>
		parseComplianceCredentials(value);

	const normalizeUrl = (value: string) => {
		const trimmed = value.trim();
		if (!trimmed) return '';
		return /^https?:\/\//i.test(trimmed) ? trimmed : `https://${trimmed}`;
	};

	const normalizeProfileLinks = (links: ProfileLink[]) =>
		links.map((link) => ({
			type: link.type,
			label: link.label.trim() || link.type,
			url: normalizeUrl(link.url)
		}));

	const summarizeExperiences = (experiences: ProfessionalExperience[]) =>
		experiences
			.map((experience) =>
				[
					[experience.title, experience.organization].filter(Boolean).join(' at '),
					experience.summary
				]
					.filter(Boolean)
					.join(': ')
			)
			.filter(Boolean)
			.join('\n\n');

	const experienceDateRange = (experience: ProfessionalExperience) => {
		const start = [experience.startMonth, experience.startYear].filter(Boolean).join(' ');
		const end = experience.isCurrent
			? 'Present'
			: [experience.endMonth, experience.endYear].filter(Boolean).join(' ');
		return [start, end].filter(Boolean).join(' - ');
	};
	const isKnownAcademicRank = (value: string) =>
		(academicRankOptions as readonly string[]).includes(value);

	const profileCompletion = $derived.by(() => {
		const checks = [
			formData.profilePhotoDataUrl,
			formData.fullName,
			formData.email,
			formData.phone,
			formData.academicRank,
			formData.highestQualification,
			formData.faculty,
			formData.department,
			formData.skillsAndCompetences.length > 0,
			formData.languageProficiencies.length > 0,
			formData.professionalExperiences.length > 0,
			formData.bio
		];

		return Math.round((checks.filter(Boolean).length / checks.length) * 100);
	});

	const getInitialFormData = () => {
		const profile = data.profile;
		const selectedLanguages = (profile?.languagesSpoken || []) as string[];

		return {
			titlePrefix: profile?.titlePrefix || '',
			fullName: profile?.fullName || '',
			email: profile?.email || '',
			phone: profile?.phoneNumber || '',
			contactDetails: profile?.contactDetails || '',
			bio: profile?.bio || profile?.notes || '',
			academicRank: profile?.academicRank || '',
			customAcademicRank:
				profile?.academicRank && !isKnownAcademicRank(profile.academicRank) ? profile.academicRank : '',
			highestQualification: profile?.highestQualification || '',
			faculty: profile?.faculty || '',
			department: profile?.department || '',
			professionalMemberships: asProfessionalMemberships(profile?.professionalMemberships),
			complianceCredentials: asComplianceCredentials(profile?.complianceAccreditation),
			yearsOfConsultancyExperience: profile?.yearsOfConsultancyExperience || '',
			consultancyAvailability: profile?.consultancyAvailability || '',
			consultancyExperience: profile?.consultancyExperience || '',
			professionalExperiences: parseProfessionalExperiences(
				profile?.professionalExperiences,
				profile?.consultancyExperience
			),
			preferredConsultancyTypes: (profile?.preferredConsultancyTypes || []) as string[],
			geographicScope: (profile?.geographicScope
				? profile.geographicScope
					.split(',')
					.map((s: string) => s.trim())
					.filter(Boolean)
				: []) as string[],

			areasOfExpertise: (profile?.areasOfExpertise || []) as string[],
			industrialAreasOfExpertise: (profile?.industrialAreasOfExpertise || []) as string[],
			skillsAndCompetences: (profile?.skillsAndCompetences || []) as string[],
			languagesSpoken: selectedLanguages,
			languageProficiencies: parseLanguageProficiencies(
				profile?.languageProficiencies,
				selectedLanguages
			),
			notes: profile?.notes || '',
			profileLinks: parseProfileLinks(profile?.profileLinks),
			profilePhotoDataUrl: profile?.profilePhotoDataUrl || ''
		};
	};

	let formData = $state(getInitialFormData());

	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(formData.faculty));

	const onFacultyChange = (value: string) => {
		formData.faculty = value;
		if (
			formData.department &&
			!getDepartmentOptionsByFaculty(value).includes(formData.department)
		) {
			formData.department = '';
		}
	};

	const academicRankSelectValue = () =>
		formData.academicRank && !isKnownAcademicRank(formData.academicRank) ? 'Other' : formData.academicRank;
	const isCustomAcademicRank = $derived(academicRankSelectValue() === 'Other');
	const resolvedAcademicRank = () =>
		isCustomAcademicRank ? formData.customAcademicRank.trim() : formData.academicRank;
	const onAcademicRankChange = (value: string) => {
		formData.academicRank = value;
		if (value !== 'Other') formData.customAcademicRank = '';
	};

	const syncLanguageProficiencies = (languages: string[]) => {
		formData.languagesSpoken = languages;
		formData.languageProficiencies = languages.map((language) => {
			const existing = formData.languageProficiencies.find(
				(entry) => normalizedKey(entry.language) === normalizedKey(language)
			);
			return { language, proficiency: existing?.proficiency ?? '' };
		});
	};

	const onPhotoChange = async (e: Event) => {
		const file = (e.target as HTMLInputElement).files?.[0];
		if (!file) return;
		const dataUrl = await new Promise<string>((resolve, reject) => {
			const reader = new FileReader();
			reader.onerror = () => reject(new Error('Cannot read file'));
			reader.onload = () => {
				const img = new Image();
				img.onerror = () => reject(new Error('Cannot decode image'));
				img.onload = () => {
					const sourceSize = Math.min(img.width, img.height);
					const sourceX = Math.round((img.width - sourceSize) / 2);
					const sourceY = Math.round((img.height - sourceSize) / 2);
					const canvas = document.createElement('canvas');
					canvas.width = PROFILE_PHOTO_SIZE;
					canvas.height = PROFILE_PHOTO_SIZE;
					canvas
						.getContext('2d')!
						.drawImage(
							img,
							sourceX,
							sourceY,
							sourceSize,
							sourceSize,
							0,
							0,
							PROFILE_PHOTO_SIZE,
							PROFILE_PHOTO_SIZE
						);
					resolve(canvas.toDataURL('image/jpeg', 0.8));
				};
				img.src = String(reader.result);
			};
			reader.readAsDataURL(file);
		});
		formData.profilePhotoDataUrl = dataUrl;
	};

	const startEdit = () => {
		isEditing = true;
		saveError = '';
	};
	const cancelEdit = () => {
		isEditing = false;
		saveError = '';
	};

	const handleSave = async () => {
		isSaving = true;
		saveError = '';
		const profileLinks = normalizeProfileLinks(formData.profileLinks);
		const result = await authService.updateProfile({
			fullName: formData.fullName,
			phoneNumber: formData.phone,
			contactDetails: formData.contactDetails,
			bio: formData.bio,
			academicRank: resolvedAcademicRank(),
			highestQualification: formData.highestQualification,
			faculty: formData.faculty,
			department: formData.department,
			professionalMemberships: serializeProfessionalMemberships(formData.professionalMemberships),
			complianceAccreditation: serializeComplianceCredentials(formData.complianceCredentials),
			yearsOfConsultancyExperience: formData.yearsOfConsultancyExperience,
			consultancyAvailability: formData.consultancyAvailability,
			consultancyExperience: summarizeExperiences(formData.professionalExperiences),
			professionalExperiences: JSON.stringify(formData.professionalExperiences),
			preferredConsultancyTypes: formData.preferredConsultancyTypes.join(','),
			geographicScope: formData.geographicScope.join(','),
			areasOfExpertise: formData.areasOfExpertise.join(','),
			industrialAreasOfExpertise: formData.industrialAreasOfExpertise.join(','),
			skillsAndCompetences: formData.skillsAndCompetences.join(','),
			languagesSpoken: formData.languagesSpoken.join(','),
			languageProficiencies: JSON.stringify(formData.languageProficiencies),
			notes: formData.notes,
			profileLinks: JSON.stringify(profileLinks),
			profilePhotoDataUrl: formData.profilePhotoDataUrl
		});
		isSaving = false;
		if (result.ok) {
			formData.academicRank = resolvedAcademicRank();
			formData.customAcademicRank = '';
			formData.profileLinks = profileLinks;
			formData.consultancyExperience = summarizeExperiences(formData.professionalExperiences);
			isEditing = false;
		} else {
			saveError = result.message;
		}
	};

	const handleLogout = () => {
		authService.logout();
		window.location.href = '/login';
	};

	const switchSection = (id: Section) => {
		if (isEditing) {
			if (!confirm('You have unsaved changes. Discard and switch sections?')) return;
			isEditing = false;
			saveError = '';
		}
		activeSection = id;
	};
</script>

<svelte:head>
	<title>My Profile | University of Zimbabwe Researchers</title>
</svelte:head>

<main class="page-shell profile-shell">
	<div class="profile-dashboard">
		<section class="profile-overview" aria-label="Profile overview">
			<div class="profile-editor-card">
				<div class="avatar-wrap">
					<img
						src={formData.profilePhotoDataUrl || '/default-avatar.svg'}
						alt={formData.fullName || 'User avatar'}
						class="avatar-img"
					/>
					{#if isEditing}
						<label class="avatar-edit-btn" title="Upload new photo">
							Upload photo
							<input type="file" accept="image/*" onchange={onPhotoChange} hidden />
						</label>
					{/if}
				</div>

				<div class="hero-info">
					<p class="hero-prefix">{formData.titlePrefix || 'Expert profile'}</p>
					<h1 class="hero-name">{formData.fullName || 'Complete your profile'}</h1>
					<p class="hero-rank">{formData.academicRank || 'Academic rank not set'}</p>
					<p class="hero-faculty">
						{formData.faculty || 'Faculty not set'}{formData.department ? ' · ' + formData.department : ''}
					</p>
					<p class="hero-email">{formData.email}</p>
				</div>

				<div class="hero-actions">
					{#if isEditing}
						{#if saveError}<p class="save-error">{saveError}</p>{/if}
						<button class="btn btn-outline" onclick={cancelEdit}>Cancel</button>
						<button class="btn btn-primary" onclick={handleSave} disabled={isSaving}>
							{isSaving ? 'Saving…' : 'Save Changes'}
						</button>
					{:else}
						<button class="btn btn-primary" onclick={startEdit}>Edit Profile</button>
						<button class="btn btn-danger" onclick={handleLogout}>Logout</button>
					{/if}
				</div>
			</div>

			<aside class="completion-card" aria-label="Profile completion">
				<div
					class="completion-ring"
					style={`background: conic-gradient(#20bf55 ${profileCompletion}%, #d9dee9 0);`}
				>
					<span>{profileCompletion}%</span>
				</div>
				<div>
					<h2>Complete your profile</h2>
					<ul class="completion-list">
						<li class:done={Boolean(formData.profilePhotoDataUrl)}>Profile photo</li>
						<li class:done={Boolean(formData.fullName && formData.phone)}>Personal details</li>
						<li class:done={Boolean(formData.academicRank && formData.faculty)}>Academic profile</li>
						<li class:done={formData.professionalExperiences.length > 0}>Experience</li>
					</ul>
				</div>
			</aside>
		</section>

		<div class="profile-body">
			<nav class="profile-sidebar" aria-label="Profile sections">
				<p class="sidebar-group-label">Profile</p>
				<div class="sidebar-menu-list">
					{#each sections as s}
						<button
							class="sidebar-item"
							class:active={activeSection === s.id}
							onclick={() => switchSection(s.id)}
						>
							<span class="sidebar-icon">{@html s.icon}</span>
							<span class="sidebar-label">{s.label}</span>
						</button>
					{/each}
				</div>
			</nav>

			<div class="profile-content">
			<!-- PERSONAL DETAILS -->
			{#if activeSection === 'personal'}
				<section class="content-section">
					<div class="section-heading">
						<h2>Personal Details</h2>
						{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
					</div>

					<div class="field-grid two">
						<div class="field">
							<span class="field-label">Title / Prefix</span>
							{#if isEditing}
								<select bind:value={formData.titlePrefix}>
									<option value="">Select title</option>
									{#each titlePrefixOptions as opt}<option value={opt}>{opt}</option>{/each}
								</select>
							{:else}
								<span class="field-value">{formData.titlePrefix || '—'}</span>
							{/if}
						</div>
						<div class="field">
							<span class="field-label">Full Name</span>
							{#if isEditing}
								<input type="text" bind:value={formData.fullName} placeholder="e.g. John Moyo" />
							{:else}
								<span class="field-value">{formData.fullName || '—'}</span>
							{/if}
						</div>
					</div>

					<div class="field-grid two">
						<div class="field">
							<span class="field-label">Email</span>
							{#if isEditing}
								<input type="email" bind:value={formData.email} disabled />
							{:else}
								<span class="field-value">{formData.email || '—'}</span>
							{/if}
						</div>
						<div class="field">
							<span class="field-label">Phone Number</span>
							{#if isEditing}
								<input type="tel" bind:value={formData.phone} placeholder="+263 77 000 0000" />
							{:else}
								<span class="field-value">{formData.phone || '—'}</span>
							{/if}
						</div>
					</div>

					<div class="field">
						<span class="field-label">Contact Details</span>
						{#if isEditing}
							<textarea
								bind:value={formData.contactDetails}
								rows="3"
								placeholder="Office address, preferred contact method…"
							></textarea>
						{:else}
							<span class="field-value">{formData.contactDetails || '—'}</span>
						{/if}
					</div>
				</section>
			{/if}

			<!-- ACADEMIC PROFILE -->
			{#if activeSection === 'academic'}
				<section class="content-section">
					<div class="section-heading">
						<h2>Academic Profile</h2>
						{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
					</div>

					<div class="field-grid two">
						<div class="field">
							<span class="field-label">Academic Rank</span>
							{#if isEditing}
								<select value={academicRankSelectValue()} onchange={(e) => onAcademicRankChange(e.currentTarget.value)}>
									<option value="">Select rank</option>
									{#each academicRankOptions as opt}<option value={opt}>{opt}</option>{/each}
								</select>
								{#if isCustomAcademicRank}
									<input
										type="text"
										bind:value={formData.customAcademicRank}
										placeholder="Enter your academic title or rank"
									/>
								{/if}
							{:else}
								<span class="field-value">{formData.academicRank || '—'}</span>
							{/if}
						</div>
						<div class="field">
							<span class="field-label">Highest Qualification</span>
							{#if isEditing}
								<select bind:value={formData.highestQualification}>
									<option value="">Select qualification</option>
									{#each highestQualificationOptions as opt}<option value={opt}>{opt}</option
										>{/each}
								</select>
							{:else}
								<span class="field-value">{formData.highestQualification || '—'}</span>
							{/if}
						</div>
					</div>

					<div class="field-grid two">
						<div class="field">
							<span class="field-label">Faculty / Institute and Units</span>
							{#if isEditing}
								<select
									value={formData.faculty}
									onchange={(e) => onFacultyChange(e.currentTarget.value)}
								>
									<option value="">Select faculty, institute or unit</option>
									{#each facultyOptions as opt}<option value={opt}>{opt}</option>{/each}
								</select>
							{:else}
								<span class="field-value">{formData.faculty || '—'}</span>
							{/if}
						</div>
						<div class="field">
							<span class="field-label">Department / Institute / Unit</span>
							{#if isEditing}
								<select bind:value={formData.department}>
									<option value="">Select department, institute or unit</option>
									{#each availableDepartmentOptions as opt}<option value={opt}>{opt}</option>{/each}
								</select>
							{:else}
								<span class="field-value">{formData.department || '—'}</span>
							{/if}
						</div>
					</div>

					<div class="field">
						<span class="field-label">Professional Memberships</span>
						{#if isEditing}
							<ProfessionalMembershipsEditor
								memberships={formData.professionalMemberships}
								organizationOptions={professionalMembershipOrganizationOptions}
								positionOptions={membershipPositionOptions}
								monthOptions={monthOptions}
								yearOptions={yearOptions}
								onchange={(value) => (formData.professionalMemberships = value)}
							/>
						{:else}
							<div class="experience-list">
								{#each formData.professionalMemberships as membership}
									<article class="experience-card-view">
										<h3>{membershipOrganizationName(membership) || 'Professional membership'}</h3>
										<p class="experience-meta">Position held: {membershipPositionHeld(membership) || '—'}</p>
										{#if membership.associatedWith}
											<p class="experience-meta">Associated with: {membership.associatedWith}</p>
										{/if}
										{#if membershipPeriod(membership)}
											<p class="experience-meta">{membershipPeriod(membership)}</p>
										{/if}
										<p class="field-value long-text">{membership.description || '—'}</p>
									</article>
								{:else}
									<span class="field-value">—</span>
								{/each}
							</div>
						{/if}
					</div>

					<div class="field">
						<span class="field-label">Compliance / Accreditation</span>
						{#if isEditing}
							<ComplianceCredentialsEditor
								credentials={formData.complianceCredentials}
								monthOptions={monthOptions}
								yearOptions={yearOptions}
								onchange={(value) => (formData.complianceCredentials = value)}
							/>
						{:else}
							<div class="experience-list">
								{#each formData.complianceCredentials as credential}
									<article class="experience-card-view">
										<h3>{credential.name || 'Compliance credential'}</h3>
										<p class="experience-meta">Issued by: {credential.issuingOrganization || '—'}</p>
										{#if credentialIssueDate(credential)}
											<p class="experience-meta">Issue date: {credentialIssueDate(credential)}</p>
										{/if}
										{#if credential.credentialUrl}
											<p class="experience-meta">Credential ID / URL: {credential.credentialUrl}</p>
										{/if}
										<p class="field-value long-text">{credential.skillsAssociated || '—'}</p>
									</article>
								{:else}
									<span class="field-value">—</span>
								{/each}
							</div>
						{/if}
					</div>
				</section>
			{/if}

			<!-- CONSULTANCY -->
			{#if activeSection === 'consultancy'}
				<section class="content-section">
					<div class="section-heading">
						<h2>Experience</h2>
						{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
					</div>

					<div class="field-grid two">
						<div class="field">
							<span class="field-label">Years of Experience</span>
							{#if isEditing}
								<select bind:value={formData.yearsOfConsultancyExperience}>
									<option value="">Select</option>
									{#each yearsOfConsultancyOptions as opt}<option value={opt}>{opt}</option>{/each}
								</select>
							{:else}
								<span class="field-value">{formData.yearsOfConsultancyExperience || '—'}</span>
							{/if}
						</div>
						<div class="field">
							<span class="field-label">Consultancy Availability</span>
							{#if isEditing}
								<select bind:value={formData.consultancyAvailability}>
									<option value="">Select</option>
									{#each consultancyAvailabilityOptions as opt}<option value={opt}>{opt}</option
										>{/each}
								</select>
							{:else}
								<span class="field-value">{formData.consultancyAvailability || '—'}</span>
							{/if}
						</div>
					</div>

					<div class="field-grid two">
						<div class="field">
							{#if isEditing}
								<MultiSelectFilter
									label="Preferred Research / Consultancy"
									selected={formData.preferredConsultancyTypes}
									options={[...preferredConsultancyTypeOptions]}
									onchange={(v) => (formData.preferredConsultancyTypes = v)}
								/>
							{:else}
								<span class="field-label">Preferred Research / Consultancy</span>
								<div class="tags-row">
									{#each formData.preferredConsultancyTypes as t}
										<span class="tag">{t}</span>
									{:else}
										<span class="field-value">—</span>
									{/each}
								</div>
							{/if}
						</div>
						<div class="field">
							<span class="field-label">Geographic Scope</span>
							{#if isEditing}
								<div class="checkbox-group">
									{#each geographicScopeOptions as opt}
										<label class="checkbox-option">
											<input
												type="checkbox"
												checked={formData.geographicScope.includes(opt)}
												onchange={() => {
													formData.geographicScope = formData.geographicScope.includes(opt)
														? formData.geographicScope.filter((s) => s !== opt)
														: [...formData.geographicScope, opt];
												}}
											/>
											<span>{opt}</span>
										</label>
									{/each}
								</div>
							{:else}
								<div class="tags-display">
									{#each formData.geographicScope as scope}
										<span class="tag">{scope}</span>
									{:else}
										<span class="field-value">—</span>
									{/each}
								</div>
							{/if}
						</div>
					</div>

					<div class="field">
						<span class="field-label">Experience</span>
						{#if isEditing}
							<ExperienceEntriesEditor
								experiences={formData.professionalExperiences}
								employmentTypeOptions={employmentTypeOptions}
								monthOptions={monthOptions}
								yearOptions={yearOptions}
								locationTypeOptions={locationTypeOptions}
								onchange={(v) => (formData.professionalExperiences = v)}
							/>
						{:else}
							<div class="experience-list">
								{#each formData.professionalExperiences as experience}
									<article class="experience-card-view">
										<h3>{experience.title || 'Experience'}</h3>
										<p class="experience-meta">
											{#if experience.organization}{experience.organization}{/if}
											{#if experience.employmentType} · {experience.employmentType}{/if}
										</p>
										{#if experienceDateRange(experience)}
											<p class="experience-meta">{experienceDateRange(experience)}</p>
										{/if}
										{#if experience.location || experience.locationType}
											<p class="experience-meta">
												{[experience.location, experience.locationType].filter(Boolean).join(' · ')}
											</p>
										{/if}
										<p class="field-value long-text">{experience.summary || '—'}</p>
									</article>
								{:else}
									<span class="field-value">—</span>
								{/each}
							</div>
						{/if}
					</div>
				</section>
			{/if}

			<!-- EXPERTISE & SKILLS -->
			{#if activeSection === 'expertise'}
				<section class="content-section">
					<div class="section-heading">
						<h2>Expertise &amp; Skills</h2>
						{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
					</div>

					{#if isEditing}
						<TagFilter
							label="Areas of Expertise"
							selected={formData.areasOfExpertise}
							options={[...areasOfExpertiseOptions]}
							onchange={(v) => (formData.areasOfExpertise = v)}
						/>
						<TagFilter
							label="Industrial Areas of Expertise"
							selected={formData.industrialAreasOfExpertise}
							options={[...industrialAreasOptions]}
							onchange={(v) => (formData.industrialAreasOfExpertise = v)}
						/>
						<div class="field-grid two">
							<MultiSelectFilter
								label="Skills and Competences"
								selected={formData.skillsAndCompetences}
								options={[...skillsOptions]}
								allowCustom
								customPlaceholder="Add custom skill or competence"
								onchange={(v) => (formData.skillsAndCompetences = v)}
							/>
							<LanguageProficiencyField
								label="Language"
								selected={formData.languagesSpoken}
								entries={formData.languageProficiencies}
								options={[...languageOptions]}
								proficiencyOptions={languageProficiencyOptions}
								allowCustom
								customPlaceholder="Add custom language"
								onLanguagesChange={syncLanguageProficiencies}
								onEntriesChange={(v) => (formData.languageProficiencies = v)}
							/>
						</div>
					{:else}
						<div class="expertise-view-grid">
							<div class="expertise-group">
								<span class="field-label">Areas of Expertise</span>
								<div class="tags-row">
									{#each formData.areasOfExpertise as t}<span class="tag">{t}</span>{:else}<span
											class="field-value">—</span
										>{/each}
								</div>
							</div>
							<div class="expertise-group">
								<span class="field-label">Industrial Areas of Expertise</span>
								<div class="tags-row">
									{#each formData.industrialAreasOfExpertise as t}<span class="tag">{t}</span
										>{:else}<span class="field-value">—</span>{/each}
								</div>
							</div>
							<div class="expertise-group">
								<span class="field-label">Skills and Competences</span>
								<div class="tags-row">
									{#each formData.skillsAndCompetences as t}<span class="tag">{t}</span>{:else}<span
											class="field-value">—</span
										>{/each}
								</div>
							</div>
							<div class="expertise-group">
								<span class="field-label">Languages Spoken</span>
								<div class="tags-row">
									{#each formData.languageProficiencies as item}
										<span class="tag">
											{item.language}{item.proficiency ? ` · ${item.proficiency}` : ''}
										</span>
									{:else}
										<span class="field-value">—</span>
									{/each}
								</div>
							</div>
						</div>
					{/if}
				</section>
			{/if}

			<!-- LINKS & PUBLICATIONS -->
			{#if activeSection === 'links'}
				<section class="content-section">
					<div class="section-heading">
						<h2>Links &amp; Publications</h2>
						{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
					</div>

					{#if isEditing}
						<ProfileLinksEditor
							links={formData.profileLinks}
							typeOptions={profileLinkTypeOptions}
							onchange={(v) => (formData.profileLinks = v)}
						/>
					{:else}
						<div class="profile-link-grid">
							{#each formData.profileLinks as link}
								<a href={link.url} target="_blank" rel="noreferrer" class="profile-link-card">
									<span class="profile-link-type">{link.type}</span>
									<span class="profile-link-label">{link.label || link.type}</span>
								</a>
							{:else}
								<span class="field-value">—</span>
							{/each}
						</div>
					{/if}
				</section>
			{/if}

			<!-- BIO & NOTES -->
			{#if activeSection === 'biography'}
				<section class="content-section">
					<div class="section-heading">
						<h2>Bio &amp; Notes</h2>
						{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
					</div>

					<div class="field">
						<span class="field-label">Profile Bio</span>
						{#if isEditing}
							<textarea
								bind:value={formData.bio}
								rows="8"
								maxlength="600"
								placeholder="Write the short professional bio shown on your researcher card and profile."
							></textarea>
						{:else}
							<p class="field-value long-text">{formData.bio || '—'}</p>
						{/if}
					</div>

					<div class="field">
						<span class="field-label">Additional Notes</span>
						{#if isEditing}
							<textarea
								bind:value={formData.notes}
								rows="4"
								placeholder="Optional internal notes or profile context."
							></textarea>
						{:else}
							<p class="field-value long-text">{formData.notes || '—'}</p>
						{/if}
					</div>

					<div class="field">
						<span class="field-label">Profile Photo</span>
						{#if isEditing}
							<label class="photo-upload-label">
								📷 Choose a new photo
								<input type="file" accept="image/*" onchange={onPhotoChange} hidden />
							</label>
							{#if formData.profilePhotoDataUrl}
								<img src={formData.profilePhotoDataUrl} alt="Preview" class="photo-preview" />
							{/if}
						{:else if formData.profilePhotoDataUrl}
							<img src={formData.profilePhotoDataUrl} alt={formData.fullName || 'Profile'} class="photo-preview" />
						{:else}
							<span class="field-value">No photo uploaded</span>
						{/if}
					</div>
				</section>
			{/if}

			<!-- Save bar -->
			{#if isEditing}
				<div class="save-bar">
					{#if saveError}<p class="save-error-bar">{saveError}</p>{/if}
					<button class="btn btn-outline" onclick={cancelEdit}>Cancel</button>
					<button class="btn btn-primary" onclick={handleSave} disabled={isSaving}>
						{isSaving ? 'Saving…' : 'Save Changes'}
					</button>
				</div>
			{/if}
		</div>
		</div>
	</div>
</main>

<style>
	.profile-shell {
		width: 100%;
		padding: 2rem 1.25rem 4rem;
		margin-top: 0;
		margin-bottom: 0;
		background:
			radial-gradient(circle at 5% 8%, rgba(10, 58, 141, 0.1), transparent 26rem),
			linear-gradient(180deg, #f4f7fb 0%, #eef2f7 100%);
	}

	.profile-dashboard {
		max-width: 1180px;
		margin: 0 auto;
		display: grid;
		gap: 1.4rem;
	}

	.profile-overview {
		display: grid;
		grid-template-columns: minmax(0, 1fr) 270px;
		gap: 1rem;
		align-items: stretch;
	}

	.profile-editor-card,
	.completion-card,
	.profile-sidebar,
	.content-section,
	.save-bar {
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid #dfe5ef;
		box-shadow: 0 18px 48px rgba(27, 43, 78, 0.09);
	}

	.profile-editor-card {
		display: flex;
		align-items: center;
		gap: 1.25rem;
		flex-wrap: wrap;
		border-radius: 22px;
		padding: 1.35rem;
	}

	.avatar-wrap {
		position: relative;
		flex-shrink: 0;
	}
	.avatar-img {
		width: 152px;
		height: 152px;
		aspect-ratio: 1 / 1;
		border-radius: 50%;
		object-fit: cover;
		border: 4px solid #fff;
		box-shadow: 0 18px 36px rgba(27, 43, 78, 0.2);
		display: block;
	}
	.avatar-edit-btn {
		position: absolute;
		left: 50%;
		bottom: -0.55rem;
		transform: translateX(-50%);
		background: #fff;
		color: #0a3a8d;
		border-radius: 999px;
		min-width: 8rem;
		min-height: 2.2rem;
		padding: 0 0.85rem;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 0.8rem;
		font-weight: 800;
		cursor: pointer;
		border: 1px solid #d7deeb;
		box-shadow: 0 10px 24px rgba(27, 43, 78, 0.12);
	}
	.hero-info {
		flex: 1;
		min-width: 0;
	}
	.hero-prefix {
		margin: 0;
		font-size: 0.76rem;
		font-weight: 800;
		color: #6f7788;
		text-transform: uppercase;
		letter-spacing: 0.1em;
	}
	.hero-name {
		margin: 0.15rem 0 0.25rem;
		font-family: 'Fraunces', serif;
		font-size: clamp(1.6rem, 3vw, 2.3rem);
		color: #17213b;
		line-height: 1.08;
	}
	.hero-rank {
		margin: 0;
		font-weight: 800;
		color: #0a3a8d;
		font-size: 1rem;
	}
	.hero-faculty {
		margin: 0.25rem 0 0;
		color: #545f73;
		font-size: 0.95rem;
		font-weight: 600;
	}
	.hero-email {
		margin: 0.25rem 0 0;
		color: #6f7788;
		font-size: 0.92rem;
	}
	.hero-actions {
		display: flex;
		gap: 0.75rem;
		flex-wrap: wrap;
		align-items: center;
		margin-left: auto;
		justify-content: flex-end;
	}
	.save-error {
		color: #d32f2f;
		font-size: 0.85rem;
		margin: 0;
		width: 100%;
		text-align: right;
	}

	.completion-card {
		border-radius: 22px;
		padding: 1.25rem;
		display: grid;
		gap: 1rem;
		align-content: start;
	}
	.completion-ring {
		width: 104px;
		height: 104px;
		border-radius: 50%;
		padding: 12px;
		display: grid;
		place-items: center;
		justify-self: center;
	}
	.completion-ring span {
		display: grid;
		place-items: center;
		width: 100%;
		height: 100%;
		border-radius: 50%;
		background: #fff;
		font-size: 1.2rem;
		font-weight: 900;
		color: #17213b;
	}
	.completion-card h2 {
		margin: 0 0 0.6rem;
		font-size: 0.95rem;
		font-weight: 900;
		color: #17213b;
		text-align: center;
	}
	.completion-list {
		display: grid;
		gap: 0.45rem;
		padding: 0;
		margin: 0;
		list-style: none;
	}
	.completion-list li {
		position: relative;
		padding-left: 1.35rem;
		font-size: 0.86rem;
		font-weight: 700;
		color: #7a8496;
	}
	.completion-list li::before {
		content: '×';
		position: absolute;
		left: 0;
		color: #98a2b3;
	}
	.completion-list li.done {
		color: #27334f;
	}
	.completion-list li.done::before {
		content: '✓';
		color: #16803c;
	}

	.profile-body {
		margin: 0;
		padding: 0;
		display: grid;
		grid-template-columns: 260px minmax(0, 1fr);
		gap: 1rem;
		align-items: start;
	}

	.profile-sidebar {
		border-radius: 22px;
		padding: 1rem;
		position: sticky;
		top: 100px;
		align-self: start;
		display: grid;
		gap: 0.6rem;
		min-width: 0;
	}
	.sidebar-group-label {
		margin: 0;
		padding: 0 0.4rem;
		font-size: 0.72rem;
		font-weight: 700;
		color: #98a2b3;
	}
	.sidebar-menu-list {
		display: grid;
		gap: 0.25rem;
	}
	.sidebar-item {
		display: flex;
		align-items: center;
		gap: 0.6rem;
		width: 100%;
		padding: 0.58rem 0.65rem;
		background: none;
		border: 1.5px solid transparent;
		border-radius: 10px;
		cursor: pointer;
		font-family: inherit;
		font-size: 0.84rem;
		font-weight: 700;
		color: #505a70;
		text-align: left;
		transition:
			background 0.15s ease,
			border-color 0.15s ease,
			color 0.15s ease,
			box-shadow 0.15s ease;
	}
	.sidebar-item:hover {
		background: #f6f9ff;
		color: #0a3a8d;
	}
	.sidebar-item.active {
		background: #fff;
		border-color: #2f6df6;
		color: #0a3a8d;
		box-shadow: 0 8px 18px rgba(47, 109, 246, 0.1);
	}
	.sidebar-icon {
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
		width: 1.1rem;
		height: 1.1rem;
		color: inherit;
	}
	.sidebar-label {
		flex: 1;
		min-width: 0;
		overflow-wrap: anywhere;
	}

	.profile-content {
		display: flex;
		flex-direction: column;
		gap: 1rem;
		min-width: 0;
	}
	.content-section {
		border-radius: 22px;
		padding: 1.75rem;
		display: flex;
		flex-direction: column;
		gap: 1.2rem;
	}
	.section-heading {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		padding-bottom: 0.75rem;
		border-bottom: 1px solid #dfe5ef;
	}
	.section-heading h2 {
		font-family: 'Fraunces', serif;
		font-size: 1.35rem;
		margin: 0;
		color: #17213b;
	}
	.btn-edit-section {
		background: none;
		border: 1.5px solid var(--uz-orange);
		color: var(--uz-orange);
		font-size: 0.8rem;
		font-weight: 700;
		padding: 0.3rem 0.85rem;
		border-radius: 999px;
		cursor: pointer;
		font-family: inherit;
		text-transform: uppercase;
		letter-spacing: 0.05em;
		transition:
			background 0.15s,
			color 0.15s;
	}
	.btn-edit-section:hover {
		background: var(--uz-orange);
		color: #fff;
	}

	.field-grid {
		display: grid;
		gap: 1rem;
	}
	.field-grid.two {
		grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
	}
	.field {
		display: flex;
		flex-direction: column;
		gap: 0.4rem;
	}
	.field-label {
		font-size: 0.75rem;
		font-weight: 700;
		text-transform: uppercase;
		letter-spacing: 0.07em;
		color: var(--ink-soft);
	}
	.field-value {
		font-size: 0.95rem;
		color: var(--ink);
	}
	.long-text {
		white-space: pre-wrap;
		line-height: 1.6;
		margin: 0;
	}
	.experience-list {
		display: grid;
		gap: 0.85rem;
	}
	.experience-card-view {
		display: grid;
		gap: 0.35rem;
		border: 1px solid var(--line);
		border-radius: 8px;
		padding: 1rem;
		background: #fff;
	}
	.experience-card-view h3 {
		margin: 0;
		font-size: 1rem;
		color: var(--ink);
	}
	.experience-meta {
		margin: 0;
		font-size: 0.86rem;
		font-weight: 600;
		color: var(--ink-soft);
	}
	.profile-link-grid {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
		gap: 0.85rem;
	}
	.profile-link-card {
		display: grid;
		gap: 0.25rem;
		border: 1px solid var(--line);
		border-radius: 8px;
		padding: 0.9rem 1rem;
		background: #fff;
		color: inherit;
		text-decoration: none;
	}
	.profile-link-card:hover {
		border-color: var(--uz-navy);
		box-shadow: 0 10px 24px rgba(27, 43, 78, 0.1);
	}
	.profile-link-type {
		font-size: 0.72rem;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.07em;
		color: var(--ink-soft);
	}
	.profile-link-label {
		font-size: 0.95rem;
		font-weight: 800;
		color: var(--uz-navy);
		overflow-wrap: anywhere;
	}

	input,
	select,
	textarea {
		border: 1px solid var(--line);
		border-radius: 10px;
		padding: 0.55rem 0.8rem;
		font-size: 0.93rem;
		font-family: inherit;
		color: var(--ink);
		background: #fafafa;
		transition: border-color 0.15s;
	}
	input:focus,
	select:focus,
	textarea:focus {
		outline: none;
		border-color: var(--uz-navy);
		background: #fff;
	}
	input:disabled {
		background: #f0f0f2;
		cursor: not-allowed;
		color: var(--ink-soft);
	}
	textarea {
		resize: vertical;
		min-height: 80px;
	}

	.tags-row {
		display: flex;
		flex-wrap: wrap;
		gap: 0.4rem;
	}
	.tag {
		background: var(--uz-orange-light);
		color: var(--uz-orange-dark);
		font-size: 0.78rem;
		font-weight: 600;
		padding: 0.25rem 0.65rem;
		border-radius: 100px;
	}
	.tags-display {
		display: flex;
		flex-wrap: wrap;
		gap: 0.4rem;
	}
	.checkbox-group {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem 1.2rem;
		padding: 0.25rem 0;
	}
	.checkbox-option {
		display: flex;
		align-items: center;
		gap: 0.4rem;
		font-size: 0.9rem;
		color: var(--ink);
		cursor: pointer;
	}
	.checkbox-option input[type='checkbox'] {
		width: 1rem;
		height: 1rem;
		accent-color: var(--uz-orange);
		cursor: pointer;
	}
	.expertise-view-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
		gap: 1.5rem;
	}
	.expertise-group {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.photo-upload-label {
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		background: var(--uz-orange);
		color: #fff;
		padding: 0.5rem 1.1rem;
		border-radius: 999px;
		font-weight: 700;
		font-size: 0.85rem;
		cursor: pointer;
		width: fit-content;
		transition: background 0.15s;
	}
	.photo-upload-label:hover {
		background: var(--uz-orange-dark);
	}
	.photo-preview {
		width: 152px;
		height: 152px;
		aspect-ratio: 1 / 1;
		border-radius: 50%;
		object-fit: cover;
		border: 1px solid var(--line);
		margin-top: 0.5rem;
	}

	.save-bar {
		border-radius: 22px;
		padding: 1rem 1.5rem;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 0.75rem;
	}
	.save-error-bar {
		color: #d32f2f;
		font-size: 0.85rem;
		margin: 0;
		flex: 1;
	}

	.btn {
		padding: 0.6rem 1.3rem;
		border-radius: 10px;
		border: none;
		font-weight: 700;
		font-size: 0.9rem;
		cursor: pointer;
		font-family: inherit;
		transition: all 0.2s;
	}
	.btn-primary {
		background: var(--uz-navy);
		color: #fff;
		border: 1.5px solid var(--uz-navy);
	}
	.btn-primary:hover:not(:disabled) {
		background: #0a2a6b;
	}
	.btn-primary:disabled {
		opacity: 0.6;
		cursor: not-allowed;
	}
	.btn-outline {
		background: transparent;
		color: var(--uz-navy);
		border: 1.5px solid var(--uz-navy);
	}
	.btn-outline:hover {
		background: #eef4ff;
	}
	.btn-danger {
		background: transparent;
		color: #d32f2f;
		border: 1.5px solid #d32f2f;
	}
	.btn-danger:hover {
		background: #d32f2f;
		color: #fff;
	}

	@media (max-width: 960px) {
		.profile-overview {
			grid-template-columns: 1fr;
		}
		.completion-card {
			grid-template-columns: auto minmax(0, 1fr);
			align-items: center;
		}
		.completion-card h2 {
			text-align: left;
		}
	}

	@media (max-width: 720px) {
		.profile-shell {
			padding: 1rem 0.75rem 3rem;
		}
		.profile-editor-card {
			justify-content: center;
			text-align: center;
		}
		.hero-actions {
			justify-content: center;
			margin-left: 0;
			width: 100%;
		}
		.completion-card {
			grid-template-columns: 1fr;
		}
		.completion-card h2 {
			text-align: center;
		}
		.profile-body {
			grid-template-columns: 1fr;
			gap: 1rem;
		}
		.profile-sidebar {
			position: static;
			overflow-x: auto;
		}
		.sidebar-menu-list {
			display: flex;
			gap: 0.35rem;
			min-width: max-content;
		}
		.sidebar-group-label {
			display: none;
		}
		.sidebar-item {
			flex-direction: column;
			gap: 0.25rem;
			padding: 0.58rem 0.7rem;
			font-size: 0.72rem;
			min-width: 6.25rem;
			text-align: center;
			justify-content: center;
		}
	}
</style>
