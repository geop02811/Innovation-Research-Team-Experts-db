<script lang="ts">
	import { goto } from '$app/navigation';
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import TagFilter from '$lib/components/scholars/TagFilter.svelte';
	import { authService } from '$lib/auth/auth.service';
	import {
		academicRankOptions,
		areasOfExpertiseOptions,
		consultancyAvailabilityOptions,
		getDepartmentOptionsByFaculty,
		facultyOptions,
		geographicScopeOptions,
		highestQualificationOptions,
		industrialAreasOptions,
		languageOptions,
		preferredConsultancyTypeOptions,
		skillsOptions,
		titlePrefixOptions,
		yearsOfConsultancyOptions
	} from '$lib/auth/form-options';
	import type { ExpertProfile } from '$lib/auth/types';

	// ── Step state ──────────────────────────────────────────────────────────────
	let currentStep = $state(1);
	const totalSteps = 5;
	const stepLabels = [
		'Personal Details',
		'Academic Profile',
		'Consultancy',
		'Expertise & Skills',
		'Account Setup'
	];

	// ── Form fields ─────────────────────────────────────────────────────────────
	let titlePrefix = $state('');
	let fullName = $state('');
	let contactDetails = $state('');
	let academicRank = $state('');
	let universityEmail = $state('');
	let phoneNumber = $state('');
	let highestQualification = $state('');
	let professionalMemberships = $state('');
	let complianceAccreditation = $state('');
	let faculty = $state('');
	let department = $state('');
	let yearsOfConsultancyExperience = $state('');
	let consultancyExperience = $state('');
	let consultancyAvailability = $state('');
	let preferredConsultancyTypes = $state<string[]>([]);
	let geographicScope = $state('');
	let skillsAndCompetences = $state<string[]>([]);
	let languagesSpoken = $state<string[]>([]);
	let areasOfExpertise = $state<string[]>([]);
	let industrialAreasOfExpertise = $state<string[]>([]);
	let notes = $state('');
	let email = $state('');
	let password = $state('');
	let confirmPassword = $state('');
	let profilePhotoDataUrl = $state('');
	let professionalPhotoConfirmed = $state(false);
	let error = $state('');
	let submitting = $state(false);

	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(faculty));

	const onFacultyChange = (value: string) => {
		faculty = value;
		if (department && !getDepartmentOptionsByFaculty(value).includes(department)) {
			department = '';
		}
	};

	const onPhotoChange = async (event: Event) => {
		const input = event.currentTarget as HTMLInputElement;
		const file = input.files?.[0];
		if (!file) {
			profilePhotoDataUrl = '';
			return;
		}

		if (!file.type.startsWith('image/')) {
			error = 'Profile picture must be an image file.';
			input.value = '';
			return;
		}

		// Resize + compress to JPEG ≤ 400×400, quality 0.8 — keeps data URL under ~100KB
		const dataUrl = await new Promise<string>((resolve, reject) => {
			const reader = new FileReader();
			reader.onerror = () => reject(new Error('Cannot read image file'));
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

		error = '';
		profilePhotoDataUrl = dataUrl;
	};

	// ── Per-step validation ─────────────────────────────────────────────────────
	const validateStep = (step: number): string | null => {
		switch (step) {
			case 1:
				if (!titlePrefix || !fullName || !universityEmail || !phoneNumber || !contactDetails)
					return 'Please complete all fields to continue.';
				if (!universityEmail.toLowerCase().endsWith('uz.ac.zw'))
					return 'Please use your University of Zimbabwe email address.';
				break;
			case 2:
				if (
					!academicRank ||
					!highestQualification ||
					!faculty ||
					!department ||
					!professionalMemberships ||
					!complianceAccreditation
				)
					return 'Please complete all fields to continue.';
				break;
			case 3:
				if (
					!yearsOfConsultancyExperience ||
					!consultancyAvailability ||
					!consultancyExperience ||
					!geographicScope
				)
					return 'Please complete all fields to continue.';
				if (preferredConsultancyTypes.length === 0)
					return 'Please select at least one preferred consultancy type.';
				break;
			case 4:
				if (
					areasOfExpertise.length === 0 ||
					industrialAreasOfExpertise.length === 0 ||
					skillsAndCompetences.length === 0 ||
					languagesSpoken.length === 0
				)
					return 'Please select at least one option in each expertise field.';
				break;
			case 5:
				if (
					!profilePhotoDataUrl ||
					!professionalPhotoConfirmed ||
					!notes ||
					!email ||
					!password ||
					!confirmPassword
				)
					return 'Please complete all fields to continue.';
				if (password !== confirmPassword) return 'Passwords do not match.';
				if (password.length < 6) return 'Password must be at least 6 characters long.';
				break;
		}
		return null;
	};

	const nextStep = () => {
		const stepError = validateStep(currentStep);
		if (stepError) { error = stepError; return; }
		error = '';
		currentStep = Math.min(currentStep + 1, totalSteps);
	};

	const prevStep = () => {
		error = '';
		currentStep = Math.max(currentStep - 1, 1);
	};

	const submit = async () => {
		const stepError = validateStep(5);
		if (stepError) { error = stepError; return; }

		submitting = true;
		const profile: ExpertProfile = {
			titlePrefix: titlePrefix as ExpertProfile['titlePrefix'],
			fullName,
			contactDetails,
			academicRank: academicRank as ExpertProfile['academicRank'],
			universityEmail,
			phoneNumber,
			highestQualification,
			professionalMemberships,
			complianceAccreditation,
			faculty,
			department: department as ExpertProfile['department'],
			yearsOfConsultancyExperience:
				yearsOfConsultancyExperience as ExpertProfile['yearsOfConsultancyExperience'],
			consultancyExperience,
			consultancyAvailability: consultancyAvailability as ExpertProfile['consultancyAvailability'],
			preferredConsultancyTypes,
			geographicScope: geographicScope as ExpertProfile['geographicScope'],
			skillsAndCompetences,
			languagesSpoken,
			areasOfExpertise,
			industrialAreasOfExpertise,
			notes,
			profilePhotoDataUrl
		};

		const result = await authService.signup({ email, password, profile });
		submitting = false;
		if (!result.ok) { error = result.message; return; }

		await goto('/login?message=Account created. Await admin approval before login.');
	};
</script>

<svelte:head>
	<title>Sign Up | University of Zimbabwe Experts</title>
</svelte:head>

<main class="page-shell signup-shell">
	<section class="signup-card">
		<p class="kicker">Expert Registration</p>
		<h1>Create your expert profile</h1>
		<p class="helper">All fields are mandatory. New accounts are placed in PENDING status for admin review.</p>

		<!-- Step indicator -->
		<nav class="stepper" aria-label="Registration steps">
			{#each stepLabels as label, i}
				<div class="step" class:active={currentStep === i + 1} class:done={currentStep > i + 1}>
					<span class="step-circle">{currentStep > i + 1 ? '✓' : i + 1}</span>
					<span class="step-label">{label}</span>
				</div>
				{#if i < stepLabels.length - 1}
					<div class="step-line" class:filled={currentStep > i + 1}></div>
				{/if}
			{/each}
		</nav>

		{#if error}
			<p class="error-msg" role="alert">{error}</p>
		{/if}

		<!-- ── Step 1: Personal Details ─────────────────────────────────── -->
		{#if currentStep === 1}
			<div class="step-body">
				<div class="grid two">
					<label>
						Full Name Prefix
						<select bind:value={titlePrefix}>
							<option value="">Select title</option>
							{#each titlePrefixOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
					<label>
						Full Name
						<input bind:value={fullName} placeholder="e.g. John Moyo" />
					</label>
				</div>

				<div class="grid two">
					<label>
						University Email
						<input type="email" bind:value={universityEmail} placeholder="name@uz.ac.zw" />
					</label>
					<label>
						Phone Number
						<input bind:value={phoneNumber} placeholder="+263 77 000 0000" />
					</label>
				</div>

				<label>
					Contact Details
					<textarea bind:value={contactDetails} rows="3" placeholder="Office address, preferred contact method, etc."></textarea>
				</label>
			</div>
		{/if}

		<!-- ── Step 2: Academic Profile ─────────────────────────────────── -->
		{#if currentStep === 2}
			<div class="step-body">
				<div class="grid two">
					<label>
						Academic Title / Rank
						<select bind:value={academicRank}>
							<option value="">Select rank</option>
							{#each academicRankOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
					<label>
						Highest Qualification
						<select bind:value={highestQualification}>
							<option value="">Select qualification</option>
							{#each highestQualificationOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
				</div>

				<div class="grid two">
					<label>
						Faculty
						<select value={faculty} onchange={(e) => onFacultyChange(e.currentTarget.value)}>
							<option value="">Select faculty</option>
							{#each facultyOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
					<label>
						Department
						<select bind:value={department}>
							<option value="">Select department</option>
							{#each availableDepartmentOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
				</div>

				<label>
					Professional Memberships
					<textarea bind:value={professionalMemberships} rows="2" placeholder="e.g. IEEE, ZIE, ACCA"></textarea>
				</label>

				<label>
					Compliance / Accreditation
					<textarea bind:value={complianceAccreditation} rows="2" placeholder="List any licenses, certifications, or compliance standards"></textarea>
				</label>
			</div>
		{/if}

		<!-- ── Step 3: Consultancy ───────────────────────────────────────── -->
		{#if currentStep === 3}
			<div class="step-body">
				<div class="grid two">
					<label>
						Years of Consultancy Experience
						<select bind:value={yearsOfConsultancyExperience}>
							<option value="">Select years</option>
							{#each yearsOfConsultancyOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
					<label>
						Consultancy Availability
						<select bind:value={consultancyAvailability}>
							<option value="">Select availability</option>
							{#each consultancyAvailabilityOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
				</div>

				<div class="grid two">
					<MultiSelectFilter
						label="Preferred Consultancy Types"
						selected={preferredConsultancyTypes}
						options={[...preferredConsultancyTypeOptions]}
						onchange={(value) => (preferredConsultancyTypes = value)}
					/>
					<label>
						Geographic Scope
						<select bind:value={geographicScope}>
							<option value="">Select scope</option>
							{#each geographicScopeOptions as item}
								<option value={item}>{item}</option>
							{/each}
						</select>
					</label>
				</div>

				<label>
					Consultancy Experience
					<textarea bind:value={consultancyExperience} rows="4" placeholder="Summarize past consultancy projects, clients, and outcomes"></textarea>
				</label>
			</div>
		{/if}

		<!-- ── Step 4: Expertise & Skills ───────────────────────────────── -->
		{#if currentStep === 4}
			<div class="step-body">
				<TagFilter
					label="Areas of Expertise"
					selected={areasOfExpertise}
					options={[...areasOfExpertiseOptions]}
					onchange={(value) => (areasOfExpertise = value)}
				/>

				<TagFilter
					label="Industrial Areas of Expertise"
					selected={industrialAreasOfExpertise}
					options={[...industrialAreasOptions]}
					onchange={(value) => (industrialAreasOfExpertise = value)}
				/>

				<div class="grid two">
					<MultiSelectFilter
						label="Skills and Competences"
						selected={skillsAndCompetences}
						options={[...skillsOptions]}
						onchange={(value) => (skillsAndCompetences = value)}
					/>
					<MultiSelectFilter
						label="Languages Spoken"
						selected={languagesSpoken}
						options={[...languageOptions]}
						onchange={(value) => (languagesSpoken = value)}
					/>
				</div>
			</div>
		{/if}

		<!-- ── Step 5: Account Setup ─────────────────────────────────────── -->
		{#if currentStep === 5}
			<div class="step-body">
				<label>
					Professional Profile Picture
					<input type="file" accept="image/*" onchange={onPhotoChange} />
				</label>

				{#if profilePhotoDataUrl}
					<img src={profilePhotoDataUrl} alt="Profile preview" class="photo-preview" />
				{/if}

				<label class="check-row">
					<input type="checkbox" bind:checked={professionalPhotoConfirmed} />
					I confirm that the uploaded picture is professional.
				</label>

				<label>
					Notes
					<textarea bind:value={notes} rows="3" placeholder="Add any additional information or preferences"></textarea>
				</label>

				<div class="grid two">
					<label>
						Login Email
						<input type="email" bind:value={email} />
					</label>
					<label>
						Password
						<input type="password" bind:value={password} minlength="6" />
					</label>
				</div>

				<label>
					Confirm Password
					<input type="password" bind:value={confirmPassword} minlength="6" />
				</label>
			</div>
		{/if}

		<!-- Navigation buttons -->
		<div class="form-actions">
			{#if currentStep > 1}
				<button type="button" class="btn-back" onclick={prevStep}>← Back</button>
			{/if}
			{#if currentStep < totalSteps}
				<button type="button" class="btn-next" onclick={nextStep}>Next →</button>
			{:else}
				<button type="button" class="btn-next" onclick={submit} disabled={submitting}>
					{submitting ? 'Submitting...' : 'Submit for Approval'}
				</button>
			{/if}
		</div>
	</section>
</main>

<style>
	.signup-shell {
		padding-top: 1.5rem;
		padding-bottom: 3rem;
	}

	.signup-card {
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 1.8rem;
		box-shadow: var(--shadow);
		display: grid;
		gap: 1.4rem;
	}

	h1 {
		font-family: 'Fraunces', serif;
		margin: 0.25rem 0 0;
	}

	.helper {
		margin: 0;
		color: var(--ink-soft);
	}

	/* ── Stepper ── */
	.stepper {
		display: flex;
		align-items: center;
		gap: 0;
		overflow-x: auto;
		padding-bottom: 0.25rem;
	}

	.step {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 0.3rem;
		flex-shrink: 0;
		min-width: 72px;
	}

	.step-circle {
		width: 2rem;
		height: 2rem;
		border-radius: 50%;
		border: 2px solid #cfd4de;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 0.82rem;
		font-weight: 700;
		color: #8a93a8;
		background: #fff;
		transition: border-color 0.2s, background 0.2s, color 0.2s;
	}

	.step.active .step-circle {
		border-color: #0a3a8d;
		background: #0a3a8d;
		color: #fff;
	}

	.step.done .step-circle {
		border-color: #0a3a8d;
		background: #e8eef8;
		color: #0a3a8d;
	}

	.step-label {
		font-size: 0.7rem;
		font-weight: 600;
		color: #8a93a8;
		text-align: center;
		white-space: nowrap;
	}

	.step.active .step-label,
	.step.done .step-label {
		color: #0a3a8d;
	}

	.step-line {
		flex: 1;
		height: 2px;
		background: #cfd4de;
		margin-bottom: 1.3rem;
		min-width: 16px;
		transition: background 0.2s;
	}

	.step-line.filled {
		background: #0a3a8d;
	}

	/* ── Step content ── */
	.step-body {
		display: grid;
		gap: 1rem;
	}

	label {
		display: grid;
		gap: 0.45rem;
		font-weight: 600;
		font-size: 0.95rem;
	}

	.check-row {
		display: flex;
		align-items: center;
		gap: 0.55rem;
		font-weight: 600;
	}

	input,
	select,
	textarea {
		border-radius: 12px;
		border: 1px solid #cfd4de;
		padding: 0.66rem 0.8rem;
		font-size: 1rem;
		background: #fff;
		font-family: inherit;
	}

	input:focus,
	select:focus,
	textarea:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.grid {
		display: grid;
		gap: 1rem;
	}

	.grid.two {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}

	.photo-preview {
		width: 96px;
		height: 96px;
		border-radius: 50%;
		object-fit: cover;
		border: 2px solid #cfd4de;
	}

	/* ── Error ── */
	.error-msg {
		background: #fee;
		color: #7c1020;
		padding: 0.65rem 0.8rem;
		border-radius: 10px;
		margin: 0;
	}

	/* ── Navigation ── */
	.form-actions {
		display: flex;
		justify-content: flex-end;
		gap: 0.75rem;
		padding-top: 0.5rem;
		border-top: 1px solid var(--line);
	}

	.btn-back {
		border: 1px solid #cfd4de;
		background: #fff;
		color: #333;
		padding: 0.7rem 1.4rem;
		border-radius: 999px;
		font-weight: 600;
		cursor: pointer;
		font-size: 0.95rem;
	}

	.btn-back:hover {
		background: #f5f6fa;
	}

	.btn-next {
		border: 0;
		background: #0a3a8d;
		color: #fff;
		padding: 0.7rem 1.6rem;
		border-radius: 999px;
		font-weight: 700;
		cursor: pointer;
		font-size: 0.95rem;
	}

	.btn-next:disabled {
		opacity: 0.65;
		cursor: not-allowed;
	}

	.btn-next:not(:disabled):hover {
		background: #082f75;
	}

	@media (max-width: 700px) {
		.grid.two {
			grid-template-columns: 1fr;
		}

		.step-label {
			display: none;
		}

		.stepper {
			justify-content: center;
		}
	}
</style>
