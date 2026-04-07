<script lang="ts">
	import { goto } from '$app/navigation';
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import TagFilter from '$lib/components/scholars/TagFilter.svelte';
	import { authService } from '$lib/auth/auth.service';
	import {
		titlePrefixOptions,
		academicRankOptions,
		highestQualificationOptions,
		facultyOptions,
		getDepartmentOptionsByFaculty,
		yearsOfConsultancyOptions,
		consultancyAvailabilityOptions,
		preferredConsultancyTypeOptions,
		geographicScopeOptions,
		skillsOptions,
		languageOptions,
		areasOfExpertiseOptions,
		industrialAreasOptions
	} from '$lib/auth/form-options';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	type Section = 'personal' | 'academic' | 'consultancy' | 'expertise' | 'biography';

	let activeSection = $state<Section>('personal');
	let isEditing = $state(false);
	let isSaving = $state(false);
	let saveError = $state('');

	const sections: { id: Section; label: string; icon: string }[] = [
		{
			id: 'personal', label: 'Personal Details',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="5"/><path d="M3 21a9 9 0 0 1 18 0"/></svg>`
		},
		{
			id: 'academic', label: 'Academic Profile',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>`
		},
		{
			id: 'consultancy', label: 'Consultancy',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>`
		},
		{
			id: 'expertise', label: 'Expertise & Skills',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`
		},
		{
			id: 'biography', label: 'Biography & Notes',
			icon: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`
		}
	];

	let formData = $state({
		titlePrefix:                  data.profile?.titlePrefix                  || '',
		fullName:                     data.profile?.fullName                     || '',
		email:                        data.profile?.email                        || '',
		phone:                        data.profile?.phoneNumber                  || '',
		contactDetails:               data.profile?.contactDetails               || '',
		academicRank:                 data.profile?.academicRank                 || '',
		highestQualification:         data.profile?.highestQualification         || '',
		faculty:                      data.profile?.faculty                      || '',
		department:                   data.profile?.department                   || '',
		professionalMemberships:      data.profile?.professionalMemberships      || '',
		complianceAccreditation:      data.profile?.complianceAccreditation      || '',
		yearsOfConsultancyExperience: data.profile?.yearsOfConsultancyExperience || '',
		consultancyAvailability:      data.profile?.consultancyAvailability      || '',
		consultancyExperience:        data.profile?.consultancyExperience        || '',
		preferredConsultancyTypes:    (data.profile?.preferredConsultancyTypes   || []) as string[],
		geographicScope:              data.profile?.geographicScope              || '',
		areasOfExpertise:             (data.profile?.areasOfExpertise            || []) as string[],
		industrialAreasOfExpertise:   (data.profile?.industrialAreasOfExpertise  || []) as string[],
		skillsAndCompetences:         (data.profile?.skillsAndCompetences        || []) as string[],
		languagesSpoken:              (data.profile?.languagesSpoken             || []) as string[],
		notes:                        data.profile?.notes                        || '',
		profilePhotoDataUrl:          data.profile?.profilePhotoDataUrl          || ''
	});

	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(formData.faculty));

	const onFacultyChange = (value: string) => {
		formData.faculty = value;
		if (formData.department && !getDepartmentOptionsByFaculty(value).includes(formData.department)) {
			formData.department = '';
		}
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
					const MAX = 400;
					const scale = Math.min(1, MAX / Math.max(img.width, img.height));
					const canvas = document.createElement('canvas');
					canvas.width = Math.round(img.width * scale);
					canvas.height = Math.round(img.height * scale);
					canvas.getContext('2d')!.drawImage(img, 0, 0, canvas.width, canvas.height);
					resolve(canvas.toDataURL('image/jpeg', 0.8));
				};
				img.src = String(reader.result);
			};
			reader.readAsDataURL(file);
		});
		formData.profilePhotoDataUrl = dataUrl;
	};

	const startEdit = () => { isEditing = true; saveError = ''; };
	const cancelEdit = () => { isEditing = false; saveError = ''; };

	const handleSave = async () => {
		isSaving = true;
		saveError = '';
		const result = await authService.updateProfile({
			fullName:                     formData.fullName,
			phoneNumber:                  formData.phone,
			contactDetails:               formData.contactDetails,
			academicRank:                 formData.academicRank,
			highestQualification:         formData.highestQualification,
			faculty:                      formData.faculty,
			department:                   formData.department,
			professionalMemberships:      formData.professionalMemberships,
			complianceAccreditation:      formData.complianceAccreditation,
			yearsOfConsultancyExperience: formData.yearsOfConsultancyExperience,
			consultancyAvailability:      formData.consultancyAvailability,
			consultancyExperience:        formData.consultancyExperience,
			preferredConsultancyTypes:    formData.preferredConsultancyTypes.join(','),
			geographicScope:              formData.geographicScope,
			areasOfExpertise:             formData.areasOfExpertise.join(','),
			industrialAreasOfExpertise:   formData.industrialAreasOfExpertise.join(','),
			skillsAndCompetences:         formData.skillsAndCompetences.join(','),
			languagesSpoken:              formData.languagesSpoken.join(','),
			notes:                        formData.notes,
			profilePhotoDataUrl:          formData.profilePhotoDataUrl
		});
		isSaving = false;
		if (result.ok) {
			isEditing = false;
		} else {
			saveError = result.message;
		}
	};

	const handleLogout = async () => {
		authService.logout();
		await goto('/login');
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
	<title>My Profile | University of Zimbabwe Experts</title>
</svelte:head>

<main class="page-shell profile-shell">

	<!-- Profile hero header -->
	<div class="profile-hero">
		<div class="profile-hero-inner">
			<div class="avatar-wrap">
				<img
					src={formData.profilePhotoDataUrl || '/default-avatar.svg'}
					alt="Profile photo"
					class="avatar-img"
				/>
				{#if isEditing}
					<label class="avatar-edit-btn" title="Change photo">
						✏
						<input type="file" accept="image/*" onchange={onPhotoChange} hidden />
					</label>
				{/if}
			</div>
			<div class="hero-info">
				<p class="hero-prefix">{formData.titlePrefix}</p>
				<h1 class="hero-name">{formData.fullName || '—'}</h1>
				<p class="hero-rank">{formData.academicRank}</p>
				<p class="hero-faculty">{formData.faculty}{formData.department ? ' · ' + formData.department : ''}</p>
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
	</div>

	<!-- Sidebar + content -->
	<div class="profile-body">

		<nav class="profile-sidebar" aria-label="Profile sections">
			{#each sections as s}
				<button
					class="sidebar-item"
					class:active={activeSection === s.id}
					onclick={() => switchSection(s.id)}
				>				<span class="sidebar-icon">{@html s.icon}</span>					<span class="sidebar-label">{s.label}</span>
					{#if activeSection === s.id}
						<span class="sidebar-indicator"></span>
					{/if}
				</button>
			{/each}
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
						<textarea bind:value={formData.contactDetails} rows="3" placeholder="Office address, preferred contact method…"></textarea>
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
							<select bind:value={formData.academicRank}>
								<option value="">Select rank</option>
								{#each academicRankOptions as opt}<option value={opt}>{opt}</option>{/each}
							</select>
						{:else}
							<span class="field-value">{formData.academicRank || '—'}</span>
						{/if}
					</div>
					<div class="field">
						<span class="field-label">Highest Qualification</span>
						{#if isEditing}
							<select bind:value={formData.highestQualification}>
								<option value="">Select qualification</option>
								{#each highestQualificationOptions as opt}<option value={opt}>{opt}</option>{/each}
							</select>
						{:else}
							<span class="field-value">{formData.highestQualification || '—'}</span>
						{/if}
					</div>
				</div>

				<div class="field-grid two">
					<div class="field">
						<span class="field-label">Faculty</span>
						{#if isEditing}
							<select value={formData.faculty} onchange={(e) => onFacultyChange(e.currentTarget.value)}>
								<option value="">Select faculty</option>
								{#each facultyOptions as opt}<option value={opt}>{opt}</option>{/each}
							</select>
						{:else}
							<span class="field-value">{formData.faculty || '—'}</span>
						{/if}
					</div>
					<div class="field">
						<span class="field-label">Department</span>
						{#if isEditing}
							<select bind:value={formData.department}>
								<option value="">Select department</option>
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
						<textarea bind:value={formData.professionalMemberships} rows="2" placeholder="e.g. IEEE, ZIE, ACCA"></textarea>
					{:else}
						<span class="field-value">{formData.professionalMemberships || '—'}</span>
					{/if}
				</div>

				<div class="field">
					<span class="field-label">Compliance / Accreditation</span>
					{#if isEditing}
						<textarea bind:value={formData.complianceAccreditation} rows="2" placeholder="Licences, certifications, compliance standards"></textarea>
					{:else}
						<span class="field-value">{formData.complianceAccreditation || '—'}</span>
					{/if}
				</div>
			</section>
			{/if}

			<!-- CONSULTANCY -->
			{#if activeSection === 'consultancy'}
			<section class="content-section">
				<div class="section-heading">
					<h2>Consultancy</h2>
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
								{#each consultancyAvailabilityOptions as opt}<option value={opt}>{opt}</option>{/each}
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
								label="Preferred Consultancy Types"
								selected={formData.preferredConsultancyTypes}
								options={[...preferredConsultancyTypeOptions]}
								onchange={(v) => (formData.preferredConsultancyTypes = v)}
							/>
						{:else}
							<span class="field-label">Preferred Consultancy Types</span>
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
							<select bind:value={formData.geographicScope}>
								<option value="">Select scope</option>
								{#each geographicScopeOptions as opt}<option value={opt}>{opt}</option>{/each}
							</select>
						{:else}
							<span class="field-value">{formData.geographicScope || '—'}</span>
						{/if}
					</div>
				</div>

				<div class="field">
					<span class="field-label">Consultancy Experience Summary</span>
					{#if isEditing}
						<textarea bind:value={formData.consultancyExperience} rows="5" placeholder="Summarise past consultancy projects, clients, and outcomes"></textarea>
					{:else}
						<p class="field-value long-text">{formData.consultancyExperience || '—'}</p>
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
							onchange={(v) => (formData.skillsAndCompetences = v)}
						/>
						<MultiSelectFilter
							label="Languages Spoken"
							selected={formData.languagesSpoken}
							options={[...languageOptions]}
							onchange={(v) => (formData.languagesSpoken = v)}
						/>
					</div>
				{:else}
					<div class="expertise-view-grid">
						<div class="expertise-group">
							<span class="field-label">Areas of Expertise</span>
							<div class="tags-row">
								{#each formData.areasOfExpertise as t}<span class="tag">{t}</span>{:else}<span class="field-value">—</span>{/each}
							</div>
						</div>
						<div class="expertise-group">
							<span class="field-label">Industrial Areas of Expertise</span>
							<div class="tags-row">
								{#each formData.industrialAreasOfExpertise as t}<span class="tag">{t}</span>{:else}<span class="field-value">—</span>{/each}
							</div>
						</div>
						<div class="expertise-group">
							<span class="field-label">Skills and Competences</span>
							<div class="tags-row">
								{#each formData.skillsAndCompetences as t}<span class="tag">{t}</span>{:else}<span class="field-value">—</span>{/each}
							</div>
						</div>
						<div class="expertise-group">
							<span class="field-label">Languages Spoken</span>
							<div class="tags-row">
								{#each formData.languagesSpoken as t}<span class="tag">{t}</span>{:else}<span class="field-value">—</span>{/each}
							</div>
						</div>
					</div>
				{/if}
			</section>
			{/if}

			<!-- BIOGRAPHY & NOTES -->
			{#if activeSection === 'biography'}
			<section class="content-section">
				<div class="section-heading">
					<h2>Biography &amp; Notes</h2>
					{#if !isEditing}<button class="btn-edit-section" onclick={startEdit}>Edit</button>{/if}
				</div>

				<div class="field">
					<span class="field-label">Profile Notes / Biography</span>
					{#if isEditing}
						<textarea bind:value={formData.notes} rows="8" placeholder="Write a short professional bio or notes for collaborators…"></textarea>
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
						<img src={formData.profilePhotoDataUrl} alt="Profile photo" class="photo-preview" />
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
</main>

<style>
.profile-shell { padding: 0 0 3rem; margin-top: 2.5rem; margin-bottom: 2.5rem; }

.profile-hero {
	background: var(--uz-navy);
	padding: 2rem 1.5rem;
	border-bottom: 3px solid var(--uz-orange);
}
.profile-hero-inner {
	max-width: 1100px;
	margin: 0 auto;
	display: flex;
	align-items: center;
	gap: 1.5rem;
	flex-wrap: wrap;
}
.avatar-wrap { position: relative; flex-shrink: 0; }
.avatar-img {
	width: 96px;
	height: 96px;
	border-radius: 12px;
	object-fit: cover;
	border: 3px solid rgba(255,255,255,0.25);
	display: block;
}
.avatar-edit-btn {
	position: absolute;
	bottom: 4px;
	right: 4px;
	background: var(--uz-orange);
	color: #fff;
	border-radius: 50%;
	width: 26px;
	height: 26px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 0.75rem;
	cursor: pointer;
	border: 2px solid #fff;
}
.hero-info { flex: 1; min-width: 0; }
.hero-prefix { margin: 0; font-size: 0.78rem; color: rgba(255,255,255,0.55); text-transform: uppercase; letter-spacing: 0.1em; }
.hero-name { margin: 0.1rem 0 0.25rem; font-family: 'Fraunces', serif; font-size: clamp(1.2rem, 3vw, 1.7rem); color: #fff; }
.hero-rank { margin: 0; font-weight: 600; color: var(--uz-gold); font-size: 0.92rem; }
.hero-faculty { margin: 0.1rem 0 0; color: rgba(255,255,255,0.65); font-size: 0.85rem; }
.hero-email { margin: 0.15rem 0 0; color: rgba(255,255,255,0.45); font-size: 0.8rem; }
.hero-actions { display: flex; gap: 0.75rem; flex-wrap: wrap; align-items: center; margin-left: auto; }
.save-error { color: #fca5a5; font-size: 0.85rem; margin: 0; }

.profile-body {
	max-width: 1100px;
	margin: 2rem auto 0;
	padding: 0 1.5rem;
	display: grid;
	grid-template-columns: 220px 1fr;
	gap: 2rem;
	align-items: start;
}

.profile-sidebar {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 16px;
	overflow: hidden;
	box-shadow: 0 2px 12px rgba(27,43,78,0.07);
	position: sticky;
	top: 100px;
	display: flex;
	flex-direction: column;
}
.sidebar-item {
	display: flex;
	align-items: center;
	gap: 0.65rem;
	padding: 0.9rem 1.1rem;
	background: none;
	border: none;
	border-top: 1px solid var(--line);
	cursor: pointer;
	font-family: inherit;
	font-size: 0.88rem;
	font-weight: 600;
	color: var(--ink-soft);
	text-align: left;
	position: relative;
	transition: background 0.15s, color 0.15s;
}
.sidebar-item:first-child { border-top: none; padding-top: 1.3rem; }
.sidebar-item:last-child { padding-bottom: 1.3rem; }
.sidebar-item:hover { background: var(--uz-orange-light); color: var(--uz-orange-dark); }
.sidebar-item.active { background: var(--uz-orange-light); color: var(--uz-orange-dark); }
.sidebar-indicator {
	position: absolute;
	left: 0; top: 0; bottom: 0;
	width: 4px;
	background: var(--uz-orange);
	border-radius: 0 4px 4px 0;
}
.sidebar-icon { display: flex; align-items: center; justify-content: center; flex-shrink: 0; width: 20px; color: inherit; }
.sidebar-label { flex: 1; }

.profile-content { display: flex; flex-direction: column; gap: 1.5rem; }
.content-section {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 16px;
	padding: 1.8rem;
	box-shadow: 0 2px 12px rgba(27,43,78,0.07);
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
	border-bottom: 2px solid var(--uz-gold);
}
.section-heading h2 { font-family: 'Fraunces', serif; font-size: 1.2rem; margin: 0; color: var(--uz-navy); }
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
	transition: background 0.15s, color 0.15s;
}
.btn-edit-section:hover { background: var(--uz-orange); color: #fff; }

.field-grid { display: grid; gap: 1rem; }
.field-grid.two { grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); }
.field { display: flex; flex-direction: column; gap: 0.4rem; }
.field-label { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.07em; color: var(--ink-soft); }
.field-value { font-size: 0.95rem; color: var(--ink); }
.long-text { white-space: pre-wrap; line-height: 1.6; margin: 0; }

input, select, textarea {
	border: 1px solid var(--line);
	border-radius: 10px;
	padding: 0.55rem 0.8rem;
	font-size: 0.93rem;
	font-family: inherit;
	color: var(--ink);
	background: #fafafa;
	transition: border-color 0.15s;
}
input:focus, select:focus, textarea:focus { outline: none; border-color: var(--uz-navy); background: #fff; }
input:disabled { background: #f0f0f2; cursor: not-allowed; color: var(--ink-soft); }
textarea { resize: vertical; min-height: 80px; }

.tags-row { display: flex; flex-wrap: wrap; gap: 0.4rem; }
.tag {
	background: var(--uz-orange-light);
	color: var(--uz-orange-dark);
	font-size: 0.78rem;
	font-weight: 600;
	padding: 0.25rem 0.65rem;
	border-radius: 100px;
}
.expertise-view-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
	gap: 1.5rem;
}
.expertise-group { display: flex; flex-direction: column; gap: 0.5rem; }

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
.photo-upload-label:hover { background: var(--uz-orange-dark); }
.photo-preview { width: 120px; height: 120px; border-radius: 10px; object-fit: cover; border: 1px solid var(--line); margin-top: 0.5rem; }

.save-bar {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 16px;
	padding: 1rem 1.5rem;
	display: flex;
	align-items: center;
	justify-content: flex-end;
	gap: 0.75rem;
	box-shadow: 0 2px 12px rgba(27,43,78,0.07);
}
.save-error-bar { color: #d32f2f; font-size: 0.85rem; margin: 0; flex: 1; }

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
.btn-primary { background: var(--uz-navy); color: #fff; border: 1.5px solid #fff; }
.btn-primary:hover:not(:disabled) { background: #0a2a6b; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-outline { background: transparent; color: #fff; border: 1.5px solid #fff; }
.btn-outline:hover { background: rgba(255,255,255,0.12); }
.btn-danger { background: transparent; color: #d32f2f; border: 1.5px solid #d32f2f; }
.btn-danger:hover { background: #d32f2f; color: #fff; }

@media (max-width: 720px) {
	.profile-body { grid-template-columns: 1fr; gap: 1rem; }
	.profile-sidebar { position: static; flex-direction: row; overflow-x: auto; }
	.sidebar-item {
		flex-direction: column;
		gap: 0.25rem;
		padding: 0.65rem 0.8rem;
		font-size: 0.72rem;
		border-bottom: none;
		border-right: 1px solid var(--line);
		min-width: 75px;
		text-align: center;
		justify-content: center;
	}
	.sidebar-item:last-child { border-right: none; }
	.sidebar-indicator { top: auto; bottom: 0; left: 0; right: 0; height: 3px; width: auto; border-radius: 0; }
}
</style>
