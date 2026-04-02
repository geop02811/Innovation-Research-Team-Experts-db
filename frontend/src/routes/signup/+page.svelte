<script lang="ts">
	import { goto } from '$app/navigation';
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import TagFilter from '$lib/components/scholars/TagFilter.svelte';
	import { authService } from '$lib/auth/auth.service';
	import {
		academicRankOptions,
		areasOfExpertiseOptions,
		consultancyAvailabilityOptions,
		departmentOptions,
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

		const dataUrl = await new Promise<string>((resolve, reject) => {
			const reader = new FileReader();
			reader.onload = () => resolve(String(reader.result || ''));
			reader.onerror = () => reject(new Error('Cannot read image file'));
			reader.readAsDataURL(file);
		});

		error = '';
		profilePhotoDataUrl = dataUrl;
	};

	const validateMandatoryFields = (): string | null => {
		if (
			!titlePrefix ||
			!fullName ||
			!contactDetails ||
			!academicRank ||
			!universityEmail ||
			!phoneNumber ||
			!highestQualification ||
			!professionalMemberships ||
			!complianceAccreditation ||
			!faculty ||
			!department ||
			!yearsOfConsultancyExperience ||
			!consultancyExperience ||
			!consultancyAvailability ||
			!geographicScope ||
			!notes ||
			!email ||
			!password ||
			!confirmPassword ||
			!professionalPhotoConfirmed ||
			!profilePhotoDataUrl
		) {
			return 'All fields are mandatory. Please complete every field to continue.';
		}

		if (
			preferredConsultancyTypes.length === 0 ||
			skillsAndCompetences.length === 0 ||
			languagesSpoken.length === 0 ||
			areasOfExpertise.length === 0 ||
			industrialAreasOfExpertise.length === 0
		) {
			return 'Please select at least one option in all multi-select fields.';
		}

		if (password !== confirmPassword) {
			return 'Passwords do not match.';
		}

		if (password.length < 6) {
			return 'Password must be at least 6 characters long.';
		}

		if (!universityEmail.toLowerCase().endsWith('uz.ac.zw')) {
			return 'Please use your University of Zimbabwe email address.';
		}

		return null;
	};

	const submit = async (event: SubmitEvent) => {
		event.preventDefault();
		error = '';

		const validationError = validateMandatoryFields();
		if (validationError) {
			error = validationError;
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
			geographicScope: geographicScope as ExpertProfile['geographicScope'],
			skillsAndCompetences,
			languagesSpoken,
			areasOfExpertise,
			industrialAreasOfExpertise,
			notes,
			profilePhotoDataUrl
		};

		const result = authService.signup({ email, password, profile });
		submitting = false;
		if (!result.ok) {
			error = result.message;
			return;
		}

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

		{#if error}
			<p class="error-msg">{error}</p>
		{/if}

		<form onsubmit={submit}>
			<div class="grid two">
				<label>
					Full Name Prefix
					<select bind:value={titlePrefix} required>
						<option value="">Select title</option>
						{#each titlePrefixOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
				<label>
					Full Name
					<input bind:value={fullName} required />
				</label>
			</div>

			<label>
				Contact details
				<textarea bind:value={contactDetails} rows="2" required></textarea>
			</label>

			<div class="grid two">
				<label>
					Academic Title/Rank
					<select bind:value={academicRank} required>
						<option value="">Select rank</option>
						{#each academicRankOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
				<label>
					Highest Qualification
					<select bind:value={highestQualification} required>
						<option value="">Select qualification</option>
						{#each highestQualificationOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
			</div>

			<div class="grid two">
				<label>
					University email
					<input type="email" bind:value={universityEmail} required placeholder="name@uz.ac.zw" />
				</label>
				<label>
					Phone number
					<input bind:value={phoneNumber} required />
				</label>
			</div>

			<label>
				Professional Memberships
				<textarea bind:value={professionalMemberships} rows="2" required></textarea>
			</label>

			<label>
				Compliance/Accreditation
				<textarea
					bind:value={complianceAccreditation}
					rows="2"
					required
					placeholder="List any licenses, certifications, or compliance standards"
				></textarea>
			</label>

			<div class="grid two">
				<label>
					Faculty
					<input bind:value={faculty} required placeholder="Computer Engineering, Medicine..." />
				</label>
				<label>
					Department
					<select bind:value={department} required>
						<option value="">Select department</option>
						{#each departmentOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
			</div>

			<div class="grid two">
				<label>
					Years of Consultancy Experience
					<select bind:value={yearsOfConsultancyExperience} required>
						<option value="">Select years</option>
						{#each yearsOfConsultancyOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
				<label>
					Consultancy Availability
					<select bind:value={consultancyAvailability} required>
						<option value="">Select availability</option>
						{#each consultancyAvailabilityOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
			</div>

			<label>
				Consultancy Experience
				<textarea
					bind:value={consultancyExperience}
					rows="3"
					required
					placeholder="Summarize past consultancy projects, clients, and outcomes"
				></textarea>
			</label>

			<div class="grid two">
				<MultiSelectFilter
					label="Preferred Consultancy Types"
					selected={preferredConsultancyTypes}
					options={[...preferredConsultancyTypeOptions]}
					onchange={(value) => (preferredConsultancyTypes = value)}
				/>
				<label>
					Geographic Scope
					<select bind:value={geographicScope} required>
						<option value="">Select scope</option>
						{#each geographicScopeOptions as item}
							<option value={item}>{item}</option>
						{/each}
					</select>
				</label>
			</div>

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

			<div class="grid one">
				<TagFilter
					label="Areas of Expertise"
					selected={areasOfExpertise}
					options={[...areasOfExpertiseOptions]}
					onchange={(value) => (areasOfExpertise = value)}
				/>
			</div>

			<div class="grid one">
				<TagFilter
					label="Industrial Areas of Expertise"
					selected={industrialAreasOfExpertise}
					options={[...industrialAreasOptions]}
					onchange={(value) => (industrialAreasOfExpertise = value)}
				/>
			</div>

			<label>
				Professional Profile Picture
				<input type="file" accept="image/*" required onchange={onPhotoChange} />
			</label>

			<label class="check-row">
				<input type="checkbox" bind:checked={professionalPhotoConfirmed} required />
				I confirm that the uploaded picture is professional.
			</label>

			<label>
				Notes
				<textarea bind:value={notes} rows="3" required placeholder="Add any additional information or preferences"></textarea>
			</label>

			<div class="grid two">
				<label>
					Login Email
					<input type="email" bind:value={email} required />
				</label>
				<label>
					Password
					<input type="password" bind:value={password} minlength="6" required />
				</label>
			</div>

			<label>
				Confirm Password
				<input type="password" bind:value={confirmPassword} minlength="6" required />
			</label>

			<button type="submit" disabled={submitting}>
				{submitting ? 'Submitting profile...' : 'Submit for Approval'}
			</button>
		</form>
	</section>
</main>

<style>
	.signup-shell {
		padding-top: 1.5rem;
	}

	.signup-card {
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 18px;
		padding: 1.4rem;
		box-shadow: var(--shadow);
	}

	h1 {
		font-family: 'Fraunces', serif;
		margin: 0.35rem 0 0.5rem;
	}

	.helper {
		margin-top: 0;
		color: var(--ink-soft);
	}

	form {
		display: grid;
		gap: 1rem;
	}

	label {
		display: grid;
		gap: 0.45rem;
		font-weight: 600;
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
	}

	.grid {
		display: grid;
		gap: 1rem;
	}

	.grid.two {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}

	.error-msg {
		background: #fee;
		color: #7c1020;
		padding: 0.65rem 0.8rem;
		border-radius: 10px;
	}

	button {
		margin-top: 0.35rem;
		border: 0;
		background: #0a3a8d;
		color: #fff;
		padding: 0.8rem 1rem;
		border-radius: 999px;
		font-weight: 700;
		cursor: pointer;
	}

	button:disabled {
		opacity: 0.65;
		cursor: not-allowed;
	}

	@media (max-width: 900px) {
		.grid.two {
			grid-template-columns: 1fr;
		}
	}
</style>
