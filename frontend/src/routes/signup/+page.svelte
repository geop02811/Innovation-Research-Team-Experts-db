<script lang="ts">
	import { browser } from '$app/environment';
	import { goto } from '$app/navigation';
	import { onMount } from 'svelte';
	import SelectField from '$lib/components/SelectField.svelte';
	import LanguageProficiencyField from '$lib/components/LanguageProficiencyField.svelte';
	import ExperienceEntriesEditor from '$lib/components/ExperienceEntriesEditor.svelte';
	import ProfessionalMembershipsEditor from '$lib/components/ProfessionalMembershipsEditor.svelte';
	import ComplianceCredentialsEditor from '$lib/components/ComplianceCredentialsEditor.svelte';
	import ResearchGroupsEditor from '$lib/components/ResearchGroupsEditor.svelte';
	import ProfileLinksEditor from '$lib/components/ProfileLinksEditor.svelte';
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import TagFilter from '$lib/components/scholars/TagFilter.svelte';
	import { authService } from '$lib/auth/auth.service';
	import {
		hasCompleteComplianceCredentials,
		hasCompleteProfessionalMemberships,
		parseComplianceCredentials,
		parseProfessionalMemberships,
		serializeComplianceCredentials,
		serializeProfessionalMemberships
	} from '$lib/auth/structured-profile-fields';
	import {
		hasCompleteResearchGroups,
		parseResearchGroups,
		parseResearchInterests,
		serializeResearchGroups,
		serializeResearchInterests
	} from '$lib/auth/research-profile-fields';
	import {
		academicRankOptions,
		areasOfExpertiseOptions,
		consultancyAvailabilityOptions,
		employmentTypeOptions,
		getDepartmentOptionsByFaculty,
		facultyOptions,
		geographicScopeOptions,
		highestQualificationOptions,
		industrialAreasOptions,
		languageOptions,
		languageProficiencyOptions,
		locationTypeOptions,
		membershipPositionOptions,
		monthOptions,
		professionalMembershipOrganizationOptions,
		preferredConsultancyTypeOptions,
		profileLinkTypeOptions,
		researchInterestOptions,
		skillsOptions,
		titlePrefixOptions,
		yearOptions,
		yearsOfConsultancyOptions
	} from '$lib/auth/form-options';
	import type {
		ComplianceCredential,
		ExpertProfile,
		LanguageProficiency,
		ProfessionalExperience,
		ProfessionalMembership,
		ProfileLink,
		ResearchGroup
	} from '$lib/auth/types';

	// ── Step state ──────────────────────────────────────────────────────────────
	let currentStep = $state(1);
	const totalSteps = 5;
	const stepLabels = [
		'Personal & Account',
		'Academic Profile',
		'Skills & Competence',
		'Experience',
		'Profiles & Publications'
	];

	// ── Form fields ─────────────────────────────────────────────────────────────
	let titlePrefix = $state('');
	let fullName = $state('');
	let contactDetails = $state('');
	let bio = $state('');
	let academicRank = $state('');
	let customAcademicRank = $state('');
	let universityEmail = $state('');
	let phoneNumber = $state('');
	let highestQualification = $state('');
	let professionalMemberships = $state<ProfessionalMembership[]>([]);
	let complianceCredentials = $state<ComplianceCredential[]>([]);
	let faculty = $state('');
	let department = $state('');
	let researchInterests = $state<string[]>([]);
	let researchGroups = $state<ResearchGroup[]>([]);
	let yearsOfConsultancyExperience = $state('');
	let professionalExperiences = $state<ProfessionalExperience[]>([]);
	let consultancyAvailability = $state('');
	let preferredConsultancyTypes = $state<string[]>([]);
	let geographicScope = $state<string[]>([]);
	let profileLinks = $state<ProfileLink[]>([]);
	let skillsAndCompetences = $state<string[]>([]);
	let languagesSpoken = $state<string[]>([]);
	let languageProficiencies = $state<LanguageProficiency[]>([]);
	let areasOfExpertise = $state<string[]>([]);
	let industrialAreasOfExpertise = $state<string[]>([]);
	let password = $state('');
	let confirmPassword = $state('');
	let profilePhotoDataUrl = $state('');
	let stepError = $state('');
	let profilePhotoError = $state('');
	let submitting = $state(false);
	let draftReady = $state(false);
	let validationRequested = $state(false);
	let touchedFields = $state<Record<string, boolean>>({});

	const SIGNUP_DRAFT_STORAGE_KEY = 'uz_signup_draft_v1';

	type SignupDraft = {
		currentStep: number;
		titlePrefix: string;
		fullName: string;
		contactDetails: string;
		bio: string;
		academicRank: string;
		customAcademicRank: string;
		universityEmail: string;
		phoneNumber: string;
		highestQualification: string;
		profilePhotoDataUrl: string;
		professionalMemberships: ProfessionalMembership[];
		complianceCredentials: ComplianceCredential[];
		faculty: string;
		department: string;
		researchInterests: string[];
		researchGroups: ResearchGroup[];
		yearsOfConsultancyExperience: string;
		professionalExperiences: ProfessionalExperience[];
		consultancyAvailability: string;
		preferredConsultancyTypes: string[];
		geographicScope: string[];
		profileLinks: ProfileLink[];
		skillsAndCompetences: string[];
		languagesSpoken: string[];
		languageProficiencies: LanguageProficiency[];
		areasOfExpertise: string[];
		industrialAreasOfExpertise: string[];
	};

	const availableDepartmentOptions = $derived(getDepartmentOptionsByFaculty(faculty));
	const markTouched = (field: string) => {
		touchedFields[field] = true;
	};
	const shouldShowFieldError = (field: string) => touchedFields[field] || validationRequested;
	const showRequiredError = (field: string, value: string, message: string) =>
		shouldShowFieldError(field) && !value.trim() ? message : '';
	const showArrayError = (field: string, values: readonly unknown[], message: string) =>
		shouldShowFieldError(field) && values.length === 0 ? message : '';
	const getTitlePrefixError = () => showRequiredError('titlePrefix', titlePrefix, 'Please select a title prefix.');
	const getFullNameError = () => showRequiredError('fullName', fullName, 'Please enter your full name.');
	const getUniversityEmailError = () => {
		if (!shouldShowFieldError('universityEmail')) return '';
		if (!universityEmail.trim()) return 'Please enter your university email.';
		if (!isValidUzEmail(universityEmail))
			return 'Please enter a valid UZ student, admin, or departmental email address.';
		return '';
	};
	const getPhoneNumberError = () => showRequiredError('phoneNumber', phoneNumber, 'Please enter your phone number.');
	const getContactDetailsError = () => showRequiredError('contactDetails', contactDetails, 'Please enter your contact details.');
	const getBioError = () => showRequiredError('bio', bio, 'Please enter your bio.');
	const getProfilePhotoRequiredError = () =>
		shouldShowFieldError('profilePhoto') && !profilePhotoDataUrl ? PROFILE_PHOTO_REQUIRED_ERROR : '';
	const getPasswordError = () => {
		if (!shouldShowFieldError('password')) return '';
		if (!password.trim()) return 'Please enter a password.';
		if (password.length < 6) return 'Password must be at least 6 characters long.';
		return '';
	};
	const getConfirmPasswordError = () => {
		if (!shouldShowFieldError('confirmPassword')) return '';
		if (!confirmPassword.trim()) return 'Please confirm your password.';
		if (password && confirmPassword && password !== confirmPassword) return 'Passwords do not match.';
		return '';
	};
	const getAcademicRankError = () =>
		showRequiredError('academicRank', academicRank, 'Please select an academic title / rank.');
	const getCustomAcademicRankError = () =>
		shouldShowFieldError('customAcademicRank') && isCustomAcademicRank && !customAcademicRank.trim()
			? 'Please enter your academic title or rank.'
			: '';
	const getHighestQualificationError = () =>
		showRequiredError('highestQualification', highestQualification, 'Please select your highest qualification.');
	const getFacultyError = () => showRequiredError('faculty', faculty, 'Please select your faculty / institute / unit.');
	const getDepartmentError = () =>
		showRequiredError('department', department, 'Please select your department / institute / unit.');
	const getResearchInterestsError = () =>
		showArrayError('researchInterests', researchInterests, 'Please add at least one research interest.');
	const getAreasOfExpertiseError = () =>
		showArrayError('areasOfExpertise', areasOfExpertise, 'Please select at least one area of expertise.');
	const getIndustrialAreasError = () =>
		showArrayError(
			'industrialAreasOfExpertise',
			industrialAreasOfExpertise,
			'Please select at least one industrial area of expertise.'
		);
	const getSkillsError = () =>
		showArrayError('skillsAndCompetences', skillsAndCompetences, 'Please select at least one skill or competence.');
	const getLanguagesError = () =>
		showArrayError('languagesSpoken', languagesSpoken, 'Please add at least one language.');
	const getYearsOfExperienceError = () =>
		showRequiredError('yearsOfConsultancyExperience', yearsOfConsultancyExperience, 'Please select your years of experience.');
	const getConsultancyAvailabilityError = () =>
		showRequiredError('consultancyAvailability', consultancyAvailability, 'Please select your consultancy availability.');
	const getPreferredConsultancyTypesError = () =>
		showArrayError(
			'preferredConsultancyTypes',
			preferredConsultancyTypes,
			'Please select at least one preferred research or consultancy type.'
		);
	const getGeographicScopeError = () =>
		showArrayError('geographicScope', geographicScope, 'Please select at least one geographic scope.');
	const getProfileLinksError = () =>
		shouldShowFieldError('profileLinks') && profileLinks.length > 0 && !hasCompleteProfileLinks()
			? 'Please complete the platform and URL for each profile link.'
			: '';
	const isCustomAcademicRank = $derived(
		academicRank === 'Other' ||
		(Boolean(academicRank) && !(academicRankOptions as readonly string[]).includes(academicRank))
	);
	const resolvedAcademicRank = () =>
		isCustomAcademicRank ? customAcademicRank.trim() : academicRank;
	const onAcademicRankChange = (value: string) => {
		academicRank = value;
		if (value !== 'Other') customAcademicRank = '';
	};

	const onFacultyChange = (value: string) => {
		faculty = value;
		if (department && !getDepartmentOptionsByFaculty(value).includes(department)) {
			department = '';
		}
	};

	const MAX_FILE_BYTES = 5 * 1024 * 1024; // 5 MB
	const PROFILE_PHOTO_SIZE = 400;
	const MIN_PROFILE_PHOTO_DIMENSION = PROFILE_PHOTO_SIZE;
	const PROFILE_PHOTO_REQUIRED_ERROR = 'Please upload a professional profile picture.';
	const PASSWORD_REQUIRED_ERROR = 'Please enter and confirm your password.';
	const PASSWORD_VALIDATION_ERRORS = [
		'Passwords do not match.',
		'Password must be at least 6 characters long.',
		PASSWORD_REQUIRED_ERROR
	];

	const asString = (value: unknown) => (typeof value === 'string' ? value : '');
	const asStringArray = (value: unknown) =>
		Array.isArray(value) ? value.filter((item): item is string => typeof item === 'string') : [];
	const asResearchInterestArray = (value: unknown) => parseResearchInterests(value);
	const asResearchGroupArray = (value: unknown) => parseResearchGroups(value);
	const asProfessionalMembershipArray = (value: unknown) => parseProfessionalMemberships(value);
	const asComplianceCredentialArray = (value: unknown) => parseComplianceCredentials(value);
	const asProfessionalExperienceArray = (value: unknown) =>
		Array.isArray(value)
			? value.filter(
					(item): item is ProfessionalExperience =>
						typeof item === 'object' &&
						item !== null &&
						typeof (item as ProfessionalExperience).title === 'string' &&
						typeof (item as ProfessionalExperience).organization === 'string' &&
						typeof (item as ProfessionalExperience).summary === 'string'
				)
			: [];
	const asProfileLinkArray = (value: unknown) =>
		Array.isArray(value)
			? value.filter(
					(item): item is ProfileLink =>
						typeof item === 'object' &&
						item !== null &&
						typeof (item as ProfileLink).type === 'string' &&
						typeof (item as ProfileLink).url === 'string'
				)
			: [];
	const asLanguageProficiencyArray = (value: unknown) =>
		Array.isArray(value)
			? value.filter(
					(item): item is LanguageProficiency =>
						typeof item === 'object' &&
						item !== null &&
						typeof (item as LanguageProficiency).language === 'string' &&
						typeof (item as LanguageProficiency).proficiency === 'string'
				)
			: [];
	const normalizedKey = (value: string) => value.trim().toLowerCase();
	const syncLanguageProficiencies = (selectedLanguages: string[]) => {
		languagesSpoken = selectedLanguages;
		languageProficiencies = selectedLanguages.map((language) => {
			const existing = languageProficiencies.find(
				(entry) => normalizedKey(entry.language) === normalizedKey(language)
			);
			return { language, proficiency: existing?.proficiency ?? '' };
		});
	};
	const hasCompleteLanguageProficiencies = () =>
		languagesSpoken.every((language) => {
			const entry = languageProficiencies.find(
				(item) => normalizedKey(item.language) === normalizedKey(language)
			);
			return Boolean(entry?.proficiency);
		});
	const hasCompleteProfessionalExperiences = () =>
		professionalExperiences.every(
			(experience) =>
				Boolean(experience.title.trim()) &&
				Boolean(experience.organization.trim()) &&
				Boolean(experience.startYear) &&
				Boolean(experience.summary.trim())
		);
	const hasCompleteProfileLinks = () =>
		profileLinks.every(
			(link) =>
				Boolean(link.type.trim()) &&
				Boolean(link.url.trim()) &&
				(link.type !== 'Other' || Boolean(link.label.trim()))
		);
	const normalizeUrl = (value: string) => {
		const trimmed = value.trim();
		if (!trimmed) return '';
		return /^https?:\/\//i.test(trimmed) ? trimmed : `https://${trimmed}`;
	};
	const normalizedProfileLinks = () =>
		profileLinks.map((link) => ({
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
		bio,
		academicRank,
		customAcademicRank,
		universityEmail,
		phoneNumber,
		highestQualification,
		profilePhotoDataUrl,
		professionalMemberships,
		complianceCredentials,
		faculty,
		department,
		researchInterests,
		researchGroups,
		yearsOfConsultancyExperience,
		professionalExperiences,
		consultancyAvailability,
		preferredConsultancyTypes,
		geographicScope,
		profileLinks,
		skillsAndCompetences,
		languagesSpoken,
		languageProficiencies,
		areasOfExpertise,
		industrialAreasOfExpertise
	});

	const restoreSignupDraft = (draft: Partial<SignupDraft>) => {
		const restoredStep = clampStep(draft.currentStep);
		currentStep = restoredStep > 1 ? 1 : restoredStep;
		titlePrefix = asString(draft.titlePrefix);
		fullName = asString(draft.fullName);
		contactDetails = asString(draft.contactDetails);
		bio = asString(draft.bio);
		academicRank = asString(draft.academicRank);
		customAcademicRank = asString(draft.customAcademicRank);
		if (academicRank && !(academicRankOptions as readonly string[]).includes(academicRank)) {
			customAcademicRank = customAcademicRank || academicRank;
			academicRank = 'Other';
		}
		universityEmail = asString(draft.universityEmail);
		phoneNumber = asString(draft.phoneNumber);
		highestQualification = asString(draft.highestQualification);
		profilePhotoDataUrl = asString(draft.profilePhotoDataUrl);
		professionalMemberships = asProfessionalMembershipArray(draft.professionalMemberships);
		complianceCredentials = asComplianceCredentialArray(draft.complianceCredentials);
		faculty = asString(draft.faculty);
		department = asString(draft.department);
		researchInterests = asResearchInterestArray(draft.researchInterests);
		researchGroups = asResearchGroupArray(draft.researchGroups);
		yearsOfConsultancyExperience = asString(draft.yearsOfConsultancyExperience);
		professionalExperiences = asProfessionalExperienceArray(draft.professionalExperiences);
		consultancyAvailability = asString(draft.consultancyAvailability);
		preferredConsultancyTypes = asStringArray(draft.preferredConsultancyTypes);
		geographicScope = asStringArray(draft.geographicScope);
		profileLinks = asProfileLinkArray(draft.profileLinks);
		skillsAndCompetences = asStringArray(draft.skillsAndCompetences);
		languagesSpoken = asStringArray(draft.languagesSpoken);
		languageProficiencies = asLanguageProficiencyArray(draft.languageProficiencies);
		syncLanguageProficiencies(languagesSpoken);
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
		markTouched('profilePhoto');
		profilePhotoError = '';
		if (!file) {
			profilePhotoDataUrl = '';
			return;
		}

		if (file.size > MAX_FILE_BYTES) {
			profilePhotoError = 'Profile picture must be 5 MB or smaller.';
			profilePhotoDataUrl = '';
			input.value = '';
			return;
		}

		if (!file.type.startsWith('image/')) {
			profilePhotoError = 'Profile picture must be an image file.';
			profilePhotoDataUrl = '';
			input.value = '';
			return;
		}

		const dataUrl = await new Promise<string>((resolve, reject) => {
			const reader = new FileReader();
			reader.onerror = () => reject(new Error('Cannot read image file'));
			reader.onload = () => {
				const img = new Image();
				img.onerror = () => reject(new Error('Cannot decode image'));
				img.onload = () => {
					if (
						img.width < MIN_PROFILE_PHOTO_DIMENSION ||
						img.height < MIN_PROFILE_PHOTO_DIMENSION
					) {
						reject(
							new Error(
								`Profile picture must be at least ${PROFILE_PHOTO_SIZE} x ${PROFILE_PHOTO_SIZE} pixels.`
							)
						);
						return;
					}

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
		}).catch((photoError) => {
			const message = photoError instanceof Error ? photoError.message : 'Cannot process image file.';
			profilePhotoError = message;
			profilePhotoDataUrl = '';
			input.value = '';
			return '';
		});

		if (!dataUrl) return;

		profilePhotoError = '';
		profilePhotoDataUrl = dataUrl;
	};

	const validatePasswordFields = (): string | null => {
		if (!password || !confirmPassword) return PASSWORD_REQUIRED_ERROR;
		if (password.length < 6) return 'Password must be at least 6 characters long.';
		if (password !== confirmPassword) return 'Passwords do not match.';
		return null;
	};

	// ── Per-step validation ─────────────────────────────────────────────────────
	const validateStep = (step: number): string | null => {
		switch (step) {
			case 1:
				if (!titlePrefix || !fullName || !universityEmail || !phoneNumber || !contactDetails || !bio)
					return 'Please complete all fields to continue.';
				if (!isValidUzEmail(universityEmail))
					return 'Please enter a valid UZ student, admin, or departmental email address.';
				if (!profilePhotoDataUrl) return PROFILE_PHOTO_REQUIRED_ERROR;
				if (!password || !confirmPassword) return PASSWORD_REQUIRED_ERROR;
				return validatePasswordFields();
				break;
			case 2:
				if (
					!resolvedAcademicRank() ||
					!highestQualification ||
					!faculty ||
					!department
				)
					return 'Please complete all fields to continue.';
				if (!hasCompleteProfessionalMemberships(professionalMemberships))
					return 'Please add at least one complete professional membership.';
				if (!hasCompleteComplianceCredentials(complianceCredentials))
					return 'Please add at least one complete compliance or accreditation credential.';
				break;
			case 3:
				if (researchInterests.length === 0) return 'Please add at least one research interest.';
				if (!hasCompleteResearchGroups(researchGroups))
					return 'Please complete the group name and associated organization for each research group.';
				if (
					areasOfExpertise.length === 0 ||
					industrialAreasOfExpertise.length === 0 ||
					skillsAndCompetences.length === 0 ||
					languagesSpoken.length === 0
				)
					return 'Please select at least one option in each expertise field.';
				if (!hasCompleteLanguageProficiencies())
					return 'Please select a proficiency level for each language.';
				break;
			case 4:
				if (
					!yearsOfConsultancyExperience ||
					!consultancyAvailability ||
					professionalExperiences.length === 0 ||
					geographicScope.length === 0
				)
					return 'Please complete all fields to continue.';
				if (preferredConsultancyTypes.length === 0)
					return 'Please select at least one preferred experience type.';
				if (!hasCompleteProfessionalExperiences())
					return 'Please complete the title, organization, start year, and summary for each experience.';
				break;
			case 5:
				if (profileLinks.length > 0 && !hasCompleteProfileLinks())
					return 'Please complete the platform and URL for each profile link.';
				break;
		}
		return null;
	};

	const validateSignup = (): string | null => {
		for (let step = 1; step <= totalSteps; step += 1) {
			const validationMessage = validateStep(step);
			if (validationMessage) {
				currentStep = step;
				return validationMessage;
			}
		}
		return null;
	};

	const setValidationMessage = (validationMessage: string) => {
		stepError = validationMessage;
	};

	const goToStep = (step: number) => {
		stepError = '';
		currentStep = Math.min(Math.max(step, 1), totalSteps);
	};

	const requestStep = (step: number) => {
		goToStep(step);
	};

	const nextStep = () => {
		goToStep(currentStep + 1);
	};

	const prevStep = () => {
		goToStep(currentStep - 1);
	};

	const submit = async () => {
		validationRequested = true;
		const validationMessage = validateSignup();
		if (validationMessage) {
			setValidationMessage(validationMessage);
			return;
		}

		submitting = true;
		const links = normalizedProfileLinks();
		const profile: ExpertProfile = {
			titlePrefix: titlePrefix as ExpertProfile['titlePrefix'],
			fullName,
			contactDetails,
			bio,
			researchInterests: serializeResearchInterests(researchInterests),
			researchGroups: serializeResearchGroups(researchGroups),
			academicRank: resolvedAcademicRank(),
			universityEmail,
			phoneNumber,
			highestQualification,
			professionalMemberships: serializeProfessionalMemberships(professionalMemberships),
			complianceAccreditation: serializeComplianceCredentials(complianceCredentials),
			faculty,
			department: department as ExpertProfile['department'],
			yearsOfConsultancyExperience:
				yearsOfConsultancyExperience as ExpertProfile['yearsOfConsultancyExperience'],
			consultancyExperience: summarizeExperiences(professionalExperiences),
			professionalExperiences,
			consultancyAvailability: consultancyAvailability as ExpertProfile['consultancyAvailability'],
			preferredConsultancyTypes,
			geographicScope: geographicScope.join(',') as unknown as ExpertProfile['geographicScope'],
			skillsAndCompetences,
			languagesSpoken,
			languageProficiencies,
			areasOfExpertise,
			industrialAreasOfExpertise,
			notes: '',
			profileLinks: links,
			profilePhotoDataUrl
		};

		const result = await authService.signup({
			email: universityEmail.trim().toLowerCase(),
			password,
			profile
		});
		submitting = false;
		if (!result.ok) {
			stepError = result.message;
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
		<p class="kicker">Researcher Registration</p>
		<h1>Create your researcher profile</h1>
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
					onclick={() => requestStep(i + 1)}
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

		<!-- ── Step 1: Personal & Account ─────────────────────────────────── -->
		{#if currentStep === 1}
			<div class="step-body">
				{#if stepError}
					<p class="error-msg section-error" role="alert">{stepError}</p>
				{/if}

				<div class="two grid">
					<SelectField
						label="Full Name Prefix"
						value={titlePrefix}
						placeholder="Select title"
						options={titlePrefixOptions}
						error={getTitlePrefixError()}
						onfocusout={() => markTouched('titlePrefix')}
						onchange={(value) => (titlePrefix = value)}
					/>
					<label>
						Full Name
						<input
							bind:value={fullName}
							placeholder="e.g. George Penyaitu"
							onblur={() => markTouched('fullName')}
							aria-invalid={getFullNameError() ? 'true' : undefined}
						/>
						{#if getFullNameError()}
							<span class="field-error" role="alert">{getFullNameError()}</span>
						{/if}
					</label>
				</div>

				<div class="two grid">
					<label>
						University Email
						<input
							type="email"
							bind:value={universityEmail}
							placeholder="e.g. firstname.surname@innovhub.uz.ac.zw"
							onblur={() => markTouched('universityEmail')}
							aria-invalid={getUniversityEmailError() ? 'true' : undefined}
						/>
						{#if getUniversityEmailError()}
							<span class="field-error" role="alert">{getUniversityEmailError()}</span>
						{/if}
					</label>
					<label>
						Phone Number
						<input
							bind:value={phoneNumber}
							placeholder="+263 77 000 0000"
							onblur={() => markTouched('phoneNumber')}
							aria-invalid={getPhoneNumberError() ? 'true' : undefined}
						/>
						{#if getPhoneNumberError()}
							<span class="field-error" role="alert">{getPhoneNumberError()}</span>
						{/if}
					</label>
				</div>

				<label>
					Contact Details
					<textarea
						bind:value={contactDetails}
						rows="3"
						placeholder="Office address, preferred contact method, etc."
						onblur={() => markTouched('contactDetails')}
						aria-invalid={getContactDetailsError() ? 'true' : undefined}
					></textarea>
					{#if getContactDetailsError()}
						<span class="field-error" role="alert">{getContactDetailsError()}</span>
					{/if}
				</label>

				<label>
					Profile Bio
					<textarea
						bind:value={bio}
						rows="4"
						maxlength="600"
						placeholder="Write the short professional bio shown on your researcher card and profile."
						onblur={() => markTouched('bio')}
						aria-invalid={getBioError() ? 'true' : undefined}
					></textarea>
					{#if getBioError()}
						<span class="field-error" role="alert">{getBioError()}</span>
					{/if}
				</label>

				<label>
					Professional Profile Picture
					<input type="file" accept="image/*" onchange={onPhotoChange} />
				</label>

				{#if profilePhotoError || getProfilePhotoRequiredError()}
					<p class="error-msg field-error" role="alert">{profilePhotoError || getProfilePhotoRequiredError()}</p>
				{/if}

				{#if profilePhotoDataUrl}
					<img src={profilePhotoDataUrl} alt="Profile preview" class="photo-preview" />
				{/if}

				<div class="two grid">
					<label>
						Password
						<input
							type="password"
							bind:value={password}
							minlength="6"
							onblur={() => markTouched('password')}
							aria-invalid={getPasswordError() ? 'true' : undefined}
						/>
						{#if getPasswordError()}
							<span class="field-error" role="alert">{getPasswordError()}</span>
						{/if}
					</label>
					<label>
						Confirm Password
						<input
							type="password"
							bind:value={confirmPassword}
							minlength="6"
							onblur={() => markTouched('confirmPassword')}
							aria-invalid={getConfirmPasswordError() ? 'true' : undefined}
						/>
						{#if getConfirmPasswordError()}
							<span class="field-error" role="alert">{getConfirmPasswordError()}</span>
						{/if}
					</label>
				</div>
			</div>
		{/if}

		<!-- ── Step 2: Academic Profile ─────────────────────────────────── -->
		{#if currentStep === 2}
			<div class="step-body">
				{#if stepError}
					<p class="error-msg section-error" role="alert">{stepError}</p>
				{/if}

				<div class="two grid">
					<SelectField
						label="Academic Title / Rank"
						value={academicRank}
						placeholder="Select rank"
						options={academicRankOptions}
						error={getAcademicRankError()}
						onfocusout={() => markTouched('academicRank')}
						onchange={onAcademicRankChange}
					/>
					<SelectField
						label="Highest Qualification"
						value={highestQualification}
						placeholder="Select qualification"
						options={highestQualificationOptions}
						error={getHighestQualificationError()}
						onfocusout={() => markTouched('highestQualification')}
						onchange={(value) => (highestQualification = value)}
					/>
				</div>

				{#if isCustomAcademicRank}
					<label>
						Custom Academic Title / Rank
						<input
							bind:value={customAcademicRank}
							placeholder="Enter your academic title or rank"
							onblur={() => markTouched('customAcademicRank')}
							aria-invalid={getCustomAcademicRankError() ? 'true' : undefined}
						/>
						{#if getCustomAcademicRankError()}
							<span class="field-error" role="alert">{getCustomAcademicRankError()}</span>
						{/if}
					</label>
				{/if}

				<div class="two grid">
					<SelectField
						label="Faculty / Institute and Units"
						value={faculty}
						placeholder="Select faculty, institute or unit"
						options={facultyOptions}
						error={getFacultyError()}
						onfocusout={() => markTouched('faculty')}
						onchange={onFacultyChange}
					/>
					<SelectField
						label="Department / Institute / Unit"
						value={department}
						placeholder="Select department, institute or unit"
						options={availableDepartmentOptions}
						error={getDepartmentError()}
						onfocusout={() => markTouched('department')}
						onchange={(value) => (department = value)}
					/>
				</div>

				<div class="field-block">
					<span class="field-label">Professional Memberships</span>
					<ProfessionalMembershipsEditor
						memberships={professionalMemberships}
						organizationOptions={professionalMembershipOrganizationOptions}
						positionOptions={membershipPositionOptions}
						monthOptions={monthOptions}
						yearOptions={yearOptions}
						onchange={(value) => (professionalMemberships = value)}
					/>
				</div>

				<div class="field-block">
					<span class="field-label">Compliance / Accreditation</span>
					<ComplianceCredentialsEditor
						credentials={complianceCredentials}
						monthOptions={monthOptions}
						yearOptions={yearOptions}
						onchange={(value) => (complianceCredentials = value)}
					/>
				</div>
			</div>
		{/if}

		<!-- ── Step 3: Expertise & Skills ───────────────────────────────── -->
		{#if currentStep === 3}
			<div class="step-body">
				{#if stepError}
					<p class="error-msg section-error" role="alert">{stepError}</p>
				{/if}

				<TagFilter
					label="Research Interests"
					selected={researchInterests}
					options={[...researchInterestOptions]}
					error={getResearchInterestsError()}
					onfocusout={() => markTouched('researchInterests')}
					onchange={(value) => (researchInterests = value)}
				/>

				<div class="field-block">
					<span class="field-label">Research Groups</span>
					<ResearchGroupsEditor
						groups={researchGroups}
						onchange={(value) => (researchGroups = value)}
					/>
				</div>

				<TagFilter
					label="Areas of Expertise"
					selected={areasOfExpertise}
					options={[...areasOfExpertiseOptions]}
					error={getAreasOfExpertiseError()}
					onfocusout={() => markTouched('areasOfExpertise')}
					onchange={(value) => (areasOfExpertise = value)}
				/>

				<TagFilter
					label="Industrial Areas of Expertise"
					selected={industrialAreasOfExpertise}
					options={[...industrialAreasOptions]}
					error={getIndustrialAreasError()}
					onfocusout={() => markTouched('industrialAreasOfExpertise')}
					onchange={(value) => (industrialAreasOfExpertise = value)}
				/>

				<div class="two grid">
					<MultiSelectFilter
						label="Skills and Competences"
						selected={skillsAndCompetences}
						options={[...skillsOptions]}
						allowCustom
						customPlaceholder="Add custom skill or competence"
						error={getSkillsError()}
						onfocusout={() => markTouched('skillsAndCompetences')}
						onchange={(value) => (skillsAndCompetences = value)}
					/>
					<LanguageProficiencyField
						label="Language"
						selected={languagesSpoken}
						entries={languageProficiencies}
						options={[...languageOptions]}
						proficiencyOptions={languageProficiencyOptions}
						allowCustom
						customPlaceholder="Add custom language"
						validationRequested={validationRequested}
						onLanguagesChange={syncLanguageProficiencies}
						onEntriesChange={(value) => (languageProficiencies = value)}
					/>
				</div>
			</div>
		{/if}

		<!-- ── Step 4: Experience ───────────────────────────────────────── -->
		{#if currentStep === 4}
			<div class="step-body">
				{#if stepError}
					<p class="error-msg section-error" role="alert">{stepError}</p>
				{/if}

				<div class="two grid">
					<SelectField
						label="Years of Experience"
						value={yearsOfConsultancyExperience}
						placeholder="Select years"
						options={yearsOfConsultancyOptions}
						error={getYearsOfExperienceError()}
						onfocusout={() => markTouched('yearsOfConsultancyExperience')}
						onchange={(value) => (yearsOfConsultancyExperience = value)}
					/>
					<SelectField
						label="Consultancy Availability"
						value={consultancyAvailability}
						placeholder="Select availability"
						options={consultancyAvailabilityOptions}
						error={getConsultancyAvailabilityError()}
						onfocusout={() => markTouched('consultancyAvailability')}
						onchange={(value) => (consultancyAvailability = value)}
					/>
				</div>

				<div class="two grid">
					<MultiSelectFilter
						label="Preferred Research / Consultancy"
						selected={preferredConsultancyTypes}
						options={[...preferredConsultancyTypeOptions]}
						error={getPreferredConsultancyTypesError()}
						onfocusout={() => markTouched('preferredConsultancyTypes')}
						onchange={(value) => (preferredConsultancyTypes = value)}
					/>
					<MultiSelectFilter
						label="Geographic Scope"
						selected={geographicScope}
						options={[...geographicScopeOptions]}
						error={getGeographicScopeError()}
						onfocusout={() => markTouched('geographicScope')}
						onchange={(value) => (geographicScope = value)}
					/>
				</div>


				<ExperienceEntriesEditor
					experiences={professionalExperiences}
					employmentTypeOptions={employmentTypeOptions}
					monthOptions={monthOptions}
					yearOptions={yearOptions}
					locationTypeOptions={locationTypeOptions}
					onchange={(value) => (professionalExperiences = value)}
				/>
			</div>
		{/if}

		<!-- ── Step 5: Profiles & Publications ─────────────────────────── -->
		{#if currentStep === 5}
			<div class="step-body">
				{#if stepError}
					<p class="error-msg section-error" role="alert">{stepError}</p>
				{/if}

				<p class="step-intro">
					Optionally add public profile and publication links collaborators can use to review your work.
				</p>

				<ProfileLinksEditor
					links={profileLinks}
					typeOptions={profileLinkTypeOptions}
					error={getProfileLinksError()}
					onfocusout={() => markTouched('profileLinks')}
					onchange={(value) => (profileLinks = value)}
				/>
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

	.field-block {
		display: grid;
		gap: 0.65rem;
		min-width: 0;
	}

	.field-label {
		font-weight: 700;
		font-size: 0.95rem;
		color: var(--ink);
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
		width: 120px;
		height: 120px;
		aspect-ratio: 1 / 1;
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
	/* ── Error ── */
	.error-msg {
		background: #fee;
		color: #7c1020;
		padding: 0.65rem 0.8rem;
		border-radius: 10px;
		margin: 0;
	}

	.field-error {
		color: #b42318;
		font-size: 0.84rem;
		font-weight: 600;
		line-height: 1.35;
		margin: -0.12rem 0 0;
	}

	input[aria-invalid='true'] {
		border-color: #e5484d;
		box-shadow: 0 0 0 3px rgba(229, 72, 77, 0.12);
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
