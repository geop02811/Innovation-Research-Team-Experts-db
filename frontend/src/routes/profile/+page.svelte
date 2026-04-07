<script lang="ts">
	import { goto } from '$app/navigation';
	import { facultyOptions, getDepartmentOptionsByFaculty } from '$lib/auth/form-options';
	import { authService } from '$lib/auth/auth.service';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	let isEditing = $state(false);
	let isSaving = $state(false);
	let saveError = $state('');

	// Form state
	let formData = $state({
		fullName: data.profile?.fullName || '',
		email: data.profile?.email || '',
		phone: data.profile?.phoneNumber || '',
		faculty: data.profile?.faculty || '',
		department: data.profile?.department || '',
		academicRank: data.profile?.academicRank || '',
		highestQualification: data.profile?.highestQualification || '',
		areasOfExpertise: data.profile?.areasOfExpertise || [],
		skillsAndCompetences: data.profile?.skillsAndCompetences || [],
		languagesSpoken: data.profile?.languagesSpoken || [],
		consultancyExperience: data.profile?.consultancyExperience || '',
		notes: data.profile?.notes || '',
		profilePhotoDataUrl: data.profile?.profilePhotoDataUrl || ''
	});

	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(formData.faculty));

	const onFacultyChange = (value: string) => {
		formData.faculty = value;
		if (formData.department && !getDepartmentOptionsByFaculty(value).includes(formData.department)) {
			formData.department = '';
		}
	};

	const onPhotoChange = (e: Event) => {
		const file = (e.target as HTMLInputElement).files?.[0];
		if (!file) return;
		const reader = new FileReader();
		reader.onload = () => {
			formData.profilePhotoDataUrl = reader.result as string;
		};
		reader.readAsDataURL(file);
	};

	const handleSave = async () => {
		isSaving = true;
		saveError = '';
		const result = await authService.updateProfile({
			fullName: formData.fullName,
			phoneNumber: formData.phone,
			faculty: formData.faculty,
			department: formData.department,
			academicRank: formData.academicRank,
			highestQualification: formData.highestQualification,
			areasOfExpertise: formData.areasOfExpertise.join(','),
			skillsAndCompetences: formData.skillsAndCompetences.join(','),
			languagesSpoken: formData.languagesSpoken.join(','),
			consultancyExperience: formData.consultancyExperience,
			notes: formData.notes,
			profilePhotoDataUrl: formData.profilePhotoDataUrl
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
</script>

<svelte:head>
	<title>My Profile | University of Zimbabwe Experts</title>
</svelte:head>

<main class="page-shell profile-shell">
	<div class="profile-header-bar">
		<div>
			<p class="back-link"><a href="/experts">Back to experts</a></p>
			<h1>My Expert Profile</h1>
		</div>
		<div class="header-actions">
			{#if isEditing}
				{#if saveError}<p class="save-error">{saveError}</p>{/if}
				<button class="btn btn-secondary" onclick={() => { isEditing = false; saveError = ''; }}>Cancel</button>
				<button class="btn btn-primary" onclick={handleSave} disabled={isSaving}>
					{isSaving ? 'Saving...' : 'Save Changes'}
				</button>
			{:else}
				<button class="btn btn-primary" onclick={() => (isEditing = true)}>Edit Profile</button>
				<button class="btn btn-logout" onclick={handleLogout}>Logout</button>
			{/if}
		</div>
	</div>

	<article class="profile-card">
		<div class="profile-top">
			<div class="profile-avatar">
				<img src={formData.profilePhotoDataUrl || '/default-avatar.svg'} alt="Profile" />
				{#if isEditing}
					<label class="avatar-edit-btn" title="Change photo">
						&#9998;
						<input type="file" accept="image/*" onchange={onPhotoChange} hidden />
					</label>
				{/if}
			</div>
			<div class="profile-header-info">
				<h2>{formData.fullName}</h2>
				<p class="rank">{formData.academicRank}</p>
				<p class="faculty">{formData.faculty}</p>
				<p class="email">{formData.email}</p>
			</div>
		</div>

		<div class="profile-content">
			<section class="section">
				<h3>Contact Information</h3>
				<div class="info-grid">
					<div class="info-item">
						<label>Email:</label>
						{#if isEditing}
							<input type="email" bind:value={formData.email} disabled />
						{:else}
							<p>{formData.email}</p>
						{/if}
					</div>
					<div class="info-item">
						<label>Phone:</label>
						{#if isEditing}
							<input type="tel" bind:value={formData.phone} />
						{:else}
							<p>{formData.phone}</p>
						{/if}
					</div>
				</div>
			</section>

			<section class="section">
				<h3>Academic Information</h3>
				<div class="info-grid">
					<div class="info-item">
						<label>Faculty:</label>
						{#if isEditing}
							<select value={formData.faculty} onchange={(e) => onFacultyChange(e.currentTarget.value)}>
								<option value="">Select faculty</option>
								{#each facultyOptions as item}
									<option value={item}>{item}</option>
								{/each}
							</select>
						{:else}
							<p>{formData.faculty}</p>
						{/if}
					</div>
					<div class="info-item">
						<label>Department:</label>
						{#if isEditing}
							<select bind:value={formData.department}>
								<option value="">Select department</option>
								{#each availableDepartmentOptions as item}
									<option value={item}>{item}</option>
								{/each}
							</select>
						{:else}
							<p>{formData.department}</p>
						{/if}
					</div>
					<div class="info-item">
						<label>Academic Rank:</label>
						{#if isEditing}
							<input type="text" bind:value={formData.academicRank} />
						{:else}
							<p>{formData.academicRank}</p>
						{/if}
					</div>
					<div class="info-item">
						<label>Highest Qualification:</label>
						{#if isEditing}
							<input type="text" bind:value={formData.highestQualification} />
						{:else}
							<p>{formData.highestQualification}</p>
						{/if}
					</div>
				</div>
			</section>

			<section class="section">
				<h3>Expertise</h3>
				{#if isEditing}
					<div class="expertise-list">
						<div class="expertise-group">
							<label>Areas of Expertise:</label>
							<div class="tags-display">
								{#each formData.areasOfExpertise as area}
									<span class="tag">{area}</span>
								{/each}
							</div>
						</div>
						<div class="expertise-group">
							<label>Skills and Competences:</label>
							<div class="tags-display">
								{#each formData.skillsAndCompetences as skill}
									<span class="tag">{skill}</span>
								{/each}
							</div>
						</div>
						<div class="expertise-group">
							<label>Languages Spoken:</label>
							<div class="tags-display">
								{#each formData.languagesSpoken as lang}
									<span class="tag">{lang}</span>
								{/each}
							</div>
						</div>
					</div>
				{:else}
					<div class="expertise-list">
						<div class="expertise-group">
							<label>Areas of Expertise:</label>
							<div class="tags-display">
								{#each formData.areasOfExpertise as area}
									<span class="tag">{area}</span>
								{/each}
							</div>
						</div>
						<div class="expertise-group">
							<label>Skills and Competences:</label>
							<div class="tags-display">
								{#each formData.skillsAndCompetences as skill}
									<span class="tag">{skill}</span>
								{/each}
							</div>
						</div>
						<div class="expertise-group">
							<label>Languages Spoken:</label>
							<div class="tags-display">
								{#each formData.languagesSpoken as lang}
									<span class="tag">{lang}</span>
								{/each}
							</div>
						</div>
					</div>
				{/if}
			</section>

			<section class="section">
				<h3>Consultancy Experience</h3>
				{#if isEditing}
					<textarea rows="4" bind:value={formData.consultancyExperience}></textarea>
				{:else}
					<p>{formData.consultancyExperience || 'No consultancy experience provided'}</p>
				{/if}
			</section>

			<section class="section">
				<h3>Additional Notes</h3>
				{#if isEditing}
					<textarea rows="4" bind:value={formData.notes}></textarea>
				{:else}
					<p>{formData.notes || 'No additional notes'}</p>
				{/if}
			</section>
		</div>
	</article>
</main>

<style>
	.profile-shell {
		padding-top: 1.5rem;
	}

	.profile-header-bar {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		gap: 2rem;
		margin-bottom: 2rem;
	}

	.header-actions {
		display: flex;
		gap: 1rem;
		flex-wrap: wrap;
		justify-content: flex-end;
	}

	.btn {
		padding: 0.6rem 1.2rem;
		border-radius: 8px;
		border: none;
		font-weight: 600;
		cursor: pointer;
		transition: all 0.2s ease;
		font-size: 0.9rem;
	}

	.btn-primary {
		background: #0a3a8d;
		color: #fff;
	}

	.btn-primary:hover:not(:disabled) {
		background: #062552;
	}

	.btn-secondary {
		background: #f0f0f0;
		color: #333;
		border: 1px solid #ccc;
	}

	.btn-secondary:hover {
		background: #e0e0e0;
	}

	.btn-logout {
		background: transparent;
		color: #d32f2f;
		border: 1px solid #d32f2f;
	}

	.btn-logout:hover {
		background: #d32f2f;
		color: #fff;
	}

	.btn:disabled {
		opacity: 0.6;
		cursor: not-allowed;
	}

	.profile-card {
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 2rem;
		box-shadow: var(--shadow);
	}

	.profile-top {
		display: flex;
		gap: 2rem;
		margin-bottom: 2.5rem;
		padding-bottom: 2rem;
		border-bottom: 1px solid #eee;
	}

	.profile-avatar {
		flex-shrink: 0;
		position: relative;
		display: inline-block;
	}

	.profile-avatar img {
		width: 140px;
		height: 140px;
		border-radius: 12px;
		object-fit: cover;
		border: 1px solid var(--line);
		display: block;
	}

	.avatar-edit-btn {
		position: absolute;
		bottom: 6px;
		right: 6px;
		background: #0a3a8d;
		color: #fff;
		border-radius: 50%;
		width: 30px;
		height: 30px;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 0.85rem;
		cursor: pointer;
		border: 2px solid #fff;
		box-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
		transition: background 0.2s;
	}

	.avatar-edit-btn:hover {
		background: #062552;
	}

	.save-error {
		color: #d32f2f;
		font-size: 0.85rem;
		margin: 0;
		align-self: center;
	}

	.profile-header-info {
		flex: 1;
		min-width: 0;
	}

	.profile-header-info h2 {
		margin: 0 0 0.5rem;
		font-family: 'Fraunces', serif;
		font-size: 1.8rem;
	}

	.rank {
		margin: 0.2rem 0;
		font-weight: 600;
		color: #0a3a8d;
		font-size: 1rem;
	}

	.faculty {
		margin: 0.2rem 0;
		color: var(--ink-soft);
		font-size: 0.95rem;
	}

	.email {
		margin: 0.3rem 0 0;
		color: var(--ink-soft);
		font-size: 0.9rem;
	}

	.profile-content {
		display: flex;
		flex-direction: column;
		gap: 2rem;
	}

	.section {
		padding-bottom: 1.5rem;
		border-bottom: 1px solid #f0f0f0;
	}

	.section:last-child {
		border-bottom: none;
		padding-bottom: 0;
	}

	.section h3 {
		margin: 0 0 1rem;
		font-size: 1.1rem;
		font-weight: 600;
		color: #333;
	}

	.info-grid {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
		gap: 1.5rem;
	}

	.info-item {
		display: flex;
		flex-direction: column;
		gap: 0.4rem;
	}

	.info-item label {
		font-weight: 600;
		font-size: 0.85rem;
		color: #666;
		text-transform: uppercase;
		letter-spacing: 0.05em;
	}

	.info-item p {
		margin: 0;
		color: #333;
		font-size: 0.95rem;
	}

	.info-item input {
		padding: 0.6rem 0.8rem;
		border: 1px solid #cfd4de;
		border-radius: 8px;
		font-size: 0.95rem;
		font-family: inherit;
	}

	.info-item input:disabled {
		background: #f5f5f5;
		cursor: not-allowed;
	}

	.expertise-list {
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}

	.expertise-group {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.expertise-group label {
		font-weight: 600;
		font-size: 0.85rem;
		color: #666;
		text-transform: uppercase;
		letter-spacing: 0.05em;
	}

	.tags-display {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
	}

	.tag {
		display: inline-block;
		background: #f0f0f0;
		color: #333;
		padding: 0.4rem 0.8rem;
		border-radius: 16px;
		font-size: 0.85rem;
		border: 1px solid #ddd;
	}

	textarea {
		width: 100%;
		padding: 0.8rem;
		border: 1px solid #cfd4de;
		border-radius: 8px;
		font-family: inherit;
		font-size: 0.95rem;
		resize: vertical;
	}

	.back-link {
		margin: 0 0 0.5rem;
		font-size: 0.9rem;
	}

	.back-link a {
		color: #0a3a8d;
		text-decoration: none;
		transition: color 0.2s;
	}

	.back-link a:hover {
		text-decoration: underline;
	}

	h1 {
		margin: 0;
		font-family: 'Fraunces', serif;
		font-size: 2rem;
	}

	@media (max-width: 768px) {
		.profile-header-bar {
			flex-direction: column;
		}

		.header-actions {
			width: 100%;
			justify-content: flex-start;
		}

		.profile-top {
			flex-direction: column;
			align-items: center;
			text-align: center;
		}

		.profile-avatar img {
			width: 100px;
			height: 100px;
		}

		.info-grid {
			grid-template-columns: 1fr;
		}
	}
</style>
