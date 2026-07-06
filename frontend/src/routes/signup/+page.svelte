<script lang="ts">
	import { browser } from '$app/environment';
	import { goto } from '$app/navigation';
	import { onMount } from 'svelte';
	import SelectField from '$lib/components/SelectField.svelte';
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
	const totalSteps = 6;
	const stepLabels = [
		'Personal Details',
		'Academic Profile',
		'Experience',
		'Skills & Competence',
		'Supporting Docs',
		'Account Security'
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
	let geographicScope = $state<string[]>([]);
	let cvDataUrl = $state('');
	let cvFileName = $state('');
	let universityIdDataUrl = $state('');
	let universityIdFileName = $state('');
	let skillsAndCompetences = $state<string[]>([]);
	let languagesSpoken = $state<string[]>([]);
	let areasOfExpertise = $state<string[]>([]);
	let industrialAreasOfExpertise = $state<string[]>([]);
	let password = $state('');
	let confirmPassword = $state('');
	let profilePhotoDataUrl = $state('');
	let error = $state('');
	let submitting = $state(false);
	let draftReady = $state(false);

	const SIGNUP_DRAFT_STORAGE_KEY = 'uz_signup_draft_v1';

	type SignupDraft = {
		currentStep: number;
		titlePrefix: string;
		fullName: string;
		contactDetails: string;
		academicRank: string;
		universityEmail: string;
		phoneNumber: string;
		highestQualification: string;
		professionalMemberships: string;
		complianceAccreditation: string;
		faculty: string;
		department: string;
		yearsOfConsultancyExperience: string;
		consultancyExperience: string;
		consultancyAvailability: string;
		preferredConsultancyTypes: string[];
		geographicScope: string[];
		skillsAndCompetences: string[];
		languagesSpoken: string[];
		areasOfExpertise: string[];
		industrialAreasOfExpertise: string[];
	};

	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(faculty));

	const onFacultyChange = (value: string) => {
		faculty = value;
		if (department && !getDepartmentOptionsByFaculty(value).includes(department)) {
			department = '';
		}
	};

	const MAX_FILE_BYTES = 5 * 1024 * 1024; // 5 MB

	const asString = (value: unknown) => (typeof value === 'string' ? value : '');
	const asStringArray = (value: unknown) =>
		Array.isArray(value) ? value.filter((item): item is string => typeof item === 'string') : [];
	const isValidUzEmail = (value: string) => {
		const email = value.trim().toLowerCase();
		const studentEmailPattern = /^[a-z]+(?:-[a-z]+)*\.[a-z]+(?:-[a-z]+)*@students\.uz\.ac\.zw$/;
		const adminEmailPattern = /^[a-z][a-z]+@admin\.uz\.ac\.zw$/;
		const academicEmailPattern =
			/^[a-z][a-z0-9._-]*@(?!admin\.|students\.)[a-z][a-z0-9-]*\.uz\.ac\.zw$/;

		return (
			studentEmailPattern.test(email) ||
			adminEmailPattern.test(email) ||
			academicEmailPattern.test(email)
		);
	};
	const clampStep = (value: unknown) => {
		const step = Number(value);
		return Number.isInteger(step) ? Math.min(Math.max(step, 1), totalSteps) : 1;
	};

	const getSignupDraft = (): SignupDraft => ({
		currentStep,
		titlePrefix,
		fullName,
		contactDetails,
		academicRank,
		universityEmail,
		phoneNumber,
		highestQualification,
		professionalMemberships,
		complianceAccreditation,
		faculty,
		department,
		yearsOfConsultancyExperience,
		consultancyExperience,
		consultancyAvailability,
		preferredConsultancyTypes,
		geographicScope,
		skillsAndCompetences,
		languagesSpoken,
		areasOfExpertise,
		industrialAreasOfExpertise
	});

	const restoreSignupDraft = (draft: Partial<SignupDraft>) => {
		currentStep = Math.min(clampStep(draft.currentStep), 5);
		titlePrefix = asString(draft.titlePrefix);
		fullName = asString(draft.fullName);
		contactDetails = asString(draft.contactDetails);
		academicRank = asString(draft.academicRank);
		universityEmail = asString(draft.universityEmail);
		phoneNumber = asString(draft.phoneNumber);
		highestQualification = asString(draft.highestQualification);
		professionalMemberships = asString(draft.professionalMemberships);
		complianceAccreditation = asString(draft.complianceAccreditation);
		faculty = asString(draft.faculty);
		department = asString(draft.department);
		yearsOfConsultancyExperience = asString(draft.yearsOfConsultancyExperience);
		consultancyExperience = asString(draft.consultancyExperience);
		consultancyAvailability = asString(draft.consultancyAvailability);
		preferredConsultancyTypes = asStringArray(draft.preferredConsultancyTypes);
		geographicScope = asStringArray(draft.geographicScope);
		skillsAndCompetences = asStringArray(draft.skillsAndCompetences);
		languagesSpoken = asStringArray(draft.languagesSpoken);
		areasOfExpertise = asStringArray(draft.areasOfExpertise);
		industrialAreasOfExpertise = asStringArray(draft.industrialAreasOfExpertise);
	};

	onMount(() => {
		const savedDraft = localStorage.getItem(SIGNUP_DRAFT_STORAGE_KEY);
		if (savedDraft) {
			try {
				restoreSignupDraft(JSON.parse(savedDraft));
			} catch {
				localStorage.removeItem(SIGNUP_DRAFT_STORAGE_KEY);
			}
		}
		draftReady = true;
	});

	$effect(() => {
		if (!browser || !draftReady) return;
		localStorage.setItem(SIGNUP_DRAFT_STORAGE_KEY, JSON.stringify(getSignupDraft()));
	});

	const onPhotoChange = async (event: Event) => {
		const input = event.currentTarget as HTMLInputElement;
		const file = input.files?.[0];
		if (!file) {
			profilePhotoDataUrl = '';
			return;
		}

		if (file.size > MAX_FILE_BYTES) {
			error = 'Profile picture must be 5 MB or smaller.';
			input.value = '';
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

	const onCvChange = (event: Event) => {
		const input = event.currentTarget as HTMLInputElement;
		const file = input.files?.[0];
		if (!file) {
			cvDataUrl = '';
			cvFileName = '';
			return;
		}
		if (file.size > MAX_FILE_BYTES) {
			error = `${file.name} exceeds the 5 MB limit. Please choose a smaller file.`;
			input.value = '';
			return;
		}
		const reader = new FileReader();
		reader.onload = () => {
			cvDataUrl = String(reader.result);
			cvFileName = file.name;
			error = '';
		};
		reader.readAsDataURL(file);
	};

	const onUniversityIdChange = (event: Event) => {
		const input = event.currentTarget as HTMLInputElement;
		const file = input.files?.[0];
		if (!file) {
			universityIdDataUrl = '';
			universityIdFileName = '';
			return;
		}
		if (file.size > MAX_FILE_BYTES) {
			error = `${file.name} exceeds the 5 MB limit. Please choose a smaller file.`;
			input.value = '';
			return;
		}
		const reader = new FileReader();
		reader.onload = () => {
			universityIdDataUrl = String(reader.result);
			universityIdFileName = file.name;
			error = '';
		};
		reader.readAsDataURL(file);
	};

	// ── Per-step validation ─────────────────────────────────────────────────────
	const validateStep = (step: number): string | null => {
		switch (step) {
			case 1:
				if (!titlePrefix || !fullName || !universityEmail || !phoneNumber || !contactDetails)
					return 'Please complete all fields to continue.';
				if (!isValidUzEmail(universityEmail))
					return 'Please enter a valid UZ student, admin, or departmental email address.';
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
					geographicScope.length === 0
				)
					return 'Please complete all fields to continue.';
				if (preferredConsultancyTypes.length === 0)
					return 'Please select at least one preferred experience type.';
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
				if (!cvDataUrl || !universityIdDataUrl)
					return 'Please upload both your CV and University ID to continue.';
				break;
			case 6:
				if (!profilePhotoDataUrl || !password || !confirmPassword)
					return 'Please complete all fields to continue.';
				if (password !== confirmPassword) return 'Passwords do not match.';
				if (password.length < 6) return 'Password must be at least 6 characters long.';
				break;
		}
		return null;
	};

	const validateSignup = (): string | null => {
		for (let step = 1; step <= totalSteps; step += 1) {
			const stepError = validateStep(step);
			if (stepError) {
				currentStep = step;
				return stepError;
			}
		}
		return null;
	};

	const goToStep = (step: number) => {
		error = '';
		currentStep = Math.min(Math.max(step, 1), totalSteps);
	};

	const nextStep = () => {
		goToStep(currentStep + 1);
	};

	const prevStep = () => {
		goToStep(currentStep - 1);
	};

	const submit = async () => {
		const stepError = validateSignup();
		if (stepError) {
			error = stepError;
			return;
		}

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
			geographicScope: geographicScope.join(',') as unknown as ExpertProfile['geographicScope'],
			cvDataUrl,
			universityIdDataUrl,
			skillsAndCompetences,
			languagesSpoken,
			areasOfExpertise,
			industrialAreasOfExpertise,
			notes: '',
			profilePhotoDataUrl
		};

		const result = await authService.signup({
			email: universityEmail.trim().toLowerCase(),
			password,
			profile
		});
		submitting = false;
		if (!result.ok) {
			error = result.message;
			return;
		}
		draftReady = false;
		if (browser) localStorage.removeItem(SIGNUP_DRAFT_STORAGE_KEY);

		await goto('/login?message=Account created. Await admin approval before login.');
	};
</script>

<svelte:head>
	<title>Sign Up | University of Zimbabwe Researchers</title>
</svelte:head>

<main class="page-shell signup-shell">
	<section class="signup-card">
		<p class="kicker">Expert Registration</p>
		<h1>Create your expert profile</h1>
		<p class="helper">
			All fields are mandatory. Once your profile is approved, you will be able to log in.
		</p>

		<!-- Step indicator -->
		<nav class="stepper" aria-label="Registration steps">
			{#each stepLabels as label, i}
				<button
					type="button"
					class="step"
					class:active={currentStep === i + 1}
					class:done={currentStep > i + 1}
					onclick={() => goToStep(i + 1)}
					aria-current={currentStep === i + 1 ? 'step' : undefined}
				>
					<span class="step-circle">{i + 1}</span>
					<span class="step-label">{label}</span>
				</button>
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
				<div class="two grid">
					<SelectField
						label="Full Name Prefix"
						value={titlePrefix}
						placeholder="Select title"
						options={titlePrefixOptions}
						onchange={(value) => (titlePrefix = value)}
					/>
					<label>
						Full Name
						<input bind:value={fullName} placeholder="e.g. George Penyaitu" />
					</label>
				</div>

				<div class="two grid">
					<label>
						University Email
						<input
							type="email"
							bind:value={universityEmail}
							placeholder="e.g. firstname.surname@innovhub.uz.ac.zw"
						/>
					</label>
					<label>
						Phone Number
						<input bind:value={phoneNumber} placeholder="+263 77 000 0000" />
					</label>
				</div>

				<label>
					Contact Details
					<textarea
						bind:value={contactDetails}
						rows="3"
						placeholder="Office address, preferred contact method, etc."
					></textarea>
				</label>
			</div>
		{/if}

		<!-- ── Step 2: Academic Profile ─────────────────────────────────── -->
		{#if currentStep === 2}
			<div class="step-body">
				<div class="two grid">
					<SelectField
						label="Academic Title / Rank"
						value={academicRank}
						placeholder="Select rank"
						options={academicRankOptions}
						onchange={(value) => (academicRank = value)}
					/>
					<SelectField
						label="Highest Qualification"
						value={highestQualification}
						placeholder="Select qualification"
						options={highestQualificationOptions}
						onchange={(value) => (highestQualification = value)}
					/>
				</div>

				<div class="two grid">
					<SelectField
						label="Faculty / Institute and Units"
						value={faculty}
						placeholder="Select faculty, institute or unit"
						options={facultyOptions}
						onchange={onFacultyChange}
					/>
					<SelectField
						label="Department"
						value={department}
						placeholder="Select department"
						options={availableDepartmentOptions}
						onchange={(value) => (department = value)}
					/>
				</div>

				<label>
					Professional Memberships
					<textarea bind:value={professionalMemberships} rows="2" placeholder="e.g. IEEE, ZIE, ACCA"
					></textarea>
				</label>

				<label>
					Compliance / Accreditation
					<textarea
						bind:value={complianceAccreditation}
						rows="2"
						placeholder="List any licenses, certifications, or compliance standards"
					></textarea>
				</label>
			</div>
		{/if}

		<!-- ── Step 3: Experience ───────────────────────────────────────── -->
		{#if currentStep === 3}
			<div class="step-body">
				<div class="two grid">
					<SelectField
						label="Years of Experience"
						value={yearsOfConsultancyExperience}
						placeholder="Select years"
						options={yearsOfConsultancyOptions}
						onchange={(value) => (yearsOfConsultancyExperience = value)}
					/>
					<SelectField
						label="Consultancy Availability"
						value={consultancyAvailability}
						placeholder="Select availability"
						options={consultancyAvailabilityOptions}
						onchange={(value) => (consultancyAvailability = value)}
					/>
				</div>

				<div class="two grid">
					<MultiSelectFilter
						label="Preferred Research / Consultancy"
						selected={preferredConsultancyTypes}
						options={[...preferredConsultancyTypeOptions]}
						onchange={(value) => (preferredConsultancyTypes = value)}
					/>
					<MultiSelectFilter
						label="Geographic Scope"
						selected={geographicScope}
						options={[...geographicScopeOptions]}
						onchange={(value) => (geographicScope = value)}
					/>
				</div>

				<label>
					Experience
					<textarea
						bind:value={consultancyExperience}
						rows="4"
						placeholder="Summarize past Research, Consultancy and Outcomes"
					></textarea>
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

				<div class="two grid">
					<MultiSelectFilter
						label="Skills and Competences"
						selected={skillsAndCompetences}
						options={[...skillsOptions]}
						allowCustom
						customPlaceholder="Add custom skill or competence"
						onchange={(value) => (skillsAndCompetences = value)}
					/>
					<MultiSelectFilter
						label="Language"
						selected={languagesSpoken}
						options={[...languageOptions]}
						allowCustom
						customPlaceholder="Add custom language"
						onchange={(value) => (languagesSpoken = value)}
					/>
				</div>
			</div>
		{/if}

		<!-- ── Step 5: Supporting Documents ────────────────────────────── -->
		{#if currentStep === 5}
			<div class="step-body">
				<p class="step-intro">
					Please upload your CV and University ID for verification. Files are stored securely and
					only visible to administrators.
				</p>

				<label>
					CV / Résumé <span class="required">*</span>
					<input
						type="file"
						accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*"
						onchange={onCvChange}
					/>
					{#if cvFileName}<span class="file-chosen">{cvFileName}</span>{/if}
				</label>

				<label>
					University ID <span class="required">*</span>
					<input type="file" accept="image/*,.pdf" onchange={onUniversityIdChange} />
					{#if universityIdFileName}<span class="file-chosen">{universityIdFileName}</span>{/if}
					{#if universityIdDataUrl && universityIdDataUrl.startsWith('data:image')}
						<img src={universityIdDataUrl} alt="University ID preview" class="photo-preview" />
					{/if}
				</label>
			</div>
		{/if}

		<!-- ── Step 6: Account Security ───────────────────────────────────── -->
		{#if currentStep === 6}
			<div class="step-body">
				<label>
					Professional Profile Picture
					<input type="file" accept="image/*" onchange={onPhotoChange} />
				</label>

				{#if profilePhotoDataUrl}
					<img src={profilePhotoDataUrl} alt="Profile preview" class="photo-preview" />
				{/if}

				<div class="two grid">
					<label>
						Password
						<input type="password" bind:value={password} minlength="6" />
					</label>
					<label>
						Confirm Password
						<input type="password" bind:value={confirmPassword} minlength="6" />
					</label>
				</div>
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
		width: 100%;
		padding-top: 1.5rem;
		padding-bottom: 3rem;
	}

	.signup-card {
		width: 100%;
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 1.8rem;
		box-shadow: var(--shadow);
		display: grid;
		gap: 1.4rem;
		align-content: start;
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
		border: 0;
		background: transparent;
		padding: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 0.3rem;
		flex-shrink: 0;
		min-width: 72px;
		font: inherit;
		cursor: pointer;
	}

	.step:focus-visible {
		outline: 3px solid rgba(10, 58, 141, 0.18);
		outline-offset: 0.35rem;
		border-radius: 12px;
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
		transition:
			border-color 0.2s,
			background 0.2s,
			color 0.2s;
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
		min-height: 28rem;
		align-content: start;
	}

	label {
		display: grid;
		gap: 0.45rem;
		font-weight: 600;
		font-size: 0.95rem;
		min-width: 0;
	}

	input:not([type='checkbox']),
	textarea {
		box-sizing: border-box;
		width: 100%;
		max-width: 100%;
		min-width: 0;
		border-radius: 12px;
		border: 1px solid #cfd4de;
		padding: 0.66rem 0.8rem;
		font-size: 1rem;
		background: #fff;
		font-family: inherit;
	}

	input:focus,
	textarea:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.grid {
		display: grid;
		gap: 1rem;
		min-width: 0;
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
	.step-intro {
		font-size: 0.92rem;
		color: var(--ink-soft);
		margin: 0 0 1rem;
		line-height: 1.55;
	}
	.required {
		color: var(--uz-orange);
		font-weight: 700;
	}
	.file-chosen {
		display: block;
		margin-top: 0.3rem;
		font-size: 0.82rem;
		color: var(--ink-soft);
		font-style: italic;
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
		border: 1px solid transparent;
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
		.signup-card {
			border-radius: 16px;
			gap: 1.25rem;
			padding: clamp(1rem, 5vw, 1.35rem);
		}

		h1 {
			font-size: 1.35rem;
			line-height: 1.2;
		}

		.helper {
			font-size: 1rem;
			line-height: 1.45;
		}

		.stepper {
			display: grid;
			grid-template-columns: repeat(3, minmax(0, 1fr));
			gap: 0.75rem 0.5rem;
			justify-content: stretch;
			overflow: visible;
			padding-bottom: 0;
		}

		.step {
			min-width: 0;
			width: 100%;
			gap: 0.25rem;
		}

		.step-line {
			display: none;
		}

		.step-circle {
			margin: 0 auto;
		}

		.step-label {
			display: block;
			font-size: 0.64rem;
			line-height: 1.15;
			white-space: normal;
			overflow-wrap: anywhere;
		}

		.step-body {
			min-height: 0;
		}

		.grid.two {
			grid-template-columns: 1fr;
		}

		.form-actions {
			justify-content: stretch;
			flex-wrap: wrap;
		}

		.btn-back,
		.btn-next {
			flex: 1 1 8rem;
			min-width: 0;
			padding-inline: 1rem;
		}
	}
</style>
