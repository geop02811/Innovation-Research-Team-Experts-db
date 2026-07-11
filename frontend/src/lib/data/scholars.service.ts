import { browser } from '$app/environment';
import type { Scholar, ScholarExperience, ScholarLink } from '$lib/types/scholar';
import { parseResearchGroups, parseResearchInterests } from '$lib/auth/research-profile-fields';

const API_BASE_URL = '';

const slugify = (name: string) =>
	name
		.toLowerCase()
		.replace(/[^a-z0-9]+/g, '-')
		.replace(/(^-|-$)/g, '');

const parseLanguageProficiencies = (value: unknown): { language: string; proficiency: string }[] => {
	if (typeof value !== 'string' || !value.trim()) return [];
	try {
		const parsed = JSON.parse(value);
		return Array.isArray(parsed)
			? parsed.filter(
					(item): item is { language: string; proficiency: string } =>
						typeof item === 'object' &&
						item !== null &&
						typeof item.language === 'string' &&
						typeof item.proficiency === 'string'
				)
			: [];
	} catch {
		return [];
	}
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

const parseProfileLinks = (value: unknown): ScholarLink[] =>
	parseJsonArray(value).filter(
		(item): item is ScholarLink =>
			typeof item === 'object' &&
			item !== null &&
			typeof (item as ScholarLink).label === 'string' &&
			typeof (item as ScholarLink).url === 'string'
	);

const parseLegacyExperienceSummary = (summary: string): ScholarExperience[] =>
	summary
		.split(/\n{2,}|\s+\.\s+(?=[^.:]{2,90}\sat\s[^:]{2,120}:)/)
		.map((entry) => entry.trim().replace(/^\.+\s*/, ''))
		.filter(Boolean)
		.map((entry): ScholarExperience => {
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
	fallbackSummary: string | undefined
): ScholarExperience[] => {
	const entries = parseJsonArray(value).filter(
		(item): item is ScholarExperience =>
			typeof item === 'object' &&
			item !== null &&
			typeof (item as ScholarExperience).title === 'string' &&
			typeof (item as ScholarExperience).organization === 'string' &&
			typeof (item as ScholarExperience).summary === 'string'
	);

	if (entries.length > 0 || !fallbackSummary) return entries;
	return parseLegacyExperienceSummary(fallbackSummary);
};

const displayFullName = (fullName: string | undefined, titlePrefix: string | undefined) => {
	const name = fullName?.trim() ?? '';
	const prefix = titlePrefix?.trim() ?? '';
	if (!prefix || name.toLowerCase().startsWith(`${prefix.toLowerCase()} `)) return name;
	return `${prefix} ${name}`.trim();
};

const mapToScholar = (e: Record<string, string>): Scholar => ({
	id: e.id,
	slug: slugify(e.fullName ?? e.id),
	fullName: displayFullName(e.fullName, e.titlePrefix),
	titlePrefix: e.titlePrefix,
	qualifications: e.highestQualification ?? '',
	role: (e.academicRank as Scholar['role']) ?? 'Lecturer',
	headline: [e.academicRank, e.department].filter(Boolean).join(' · '),
	college: e.faculty ?? '',
	program: e.department ?? '',
	cohort: e.faculty ?? '',
	electionYear: new Date().getFullYear(),
	avatarUrl: e.profilePhotoDataUrl ?? '',
	country: 'Zimbabwe',
	email: e.universityEmail ?? '',
	phone: e.phoneNumber ?? '',
	location: e.faculty ?? '',
	researchAreas: e.areasOfExpertise ? e.areasOfExpertise.split(',').map((s: string) => s.trim()) : [],
	bio: e.bio ?? e.notes ?? '',
	researchInterests: parseResearchInterests(e.researchInterests),
	researchGroups: parseResearchGroups(e.researchGroups),
	shortBio: e.bio ?? e.notes ?? '',
	links: parseProfileLinks(e.profileLinks),
	professionalExperiences: parseProfessionalExperiences(e.professionalExperiences, e.consultancyExperience),
	highestQualification: e.highestQualification as Scholar['highestQualification'],
	faculty: e.faculty,
	department: e.department,
	yearsOfConsultancyExperience: e.yearsOfConsultancyExperience,
	consultancyAvailability: e.consultancyAvailability ? [e.consultancyAvailability] : [],
	preferredConsultancyTypes: e.preferredConsultancyTypes ? e.preferredConsultancyTypes.split(',').map((s: string) => s.trim()) : [],
	geographicScope: e.geographicScope ? [e.geographicScope] : [],
	languagesSpoken: e.languagesSpoken ? e.languagesSpoken.split(',').map((s: string) => s.trim()) : [],
	languageProficiencies: parseLanguageProficiencies(e.languageProficiencies),
	areasOfExpertise: e.areasOfExpertise ? e.areasOfExpertise.split(',').map((s: string) => s.trim()) : [],
	industrialAreasOfExpertise: e.industrialAreasOfExpertise ? e.industrialAreasOfExpertise.split(',').map((s: string) => s.trim()) : [],
	sections: [
		e.bio ? { title: 'Biography', body: e.bio } : e.notes ? { title: 'Biography', body: e.notes } : null,
		e.skillsAndCompetences
			? {
					title: 'Skills & Competences',
					body: e.skillsAndCompetences.split(',').map((s: string) => s.trim()).filter(Boolean).join(' · ')
				}
			: null
	].filter(Boolean) as import('$lib/types/scholar').ScholarProfileSection[]
});

const getToken = () => (browser ? localStorage.getItem('uz_token') : null);

export const scholarsService = {
	list: async (fetchFn: typeof fetch = fetch): Promise<Scholar[]> => {
		try {
			const token = getToken();
			const headers: Record<string, string> = {};
			if (token) headers['Authorization'] = `Bearer ${token}`;
			const res = await fetchFn(`${API_BASE_URL}/api/viewer/experts`, { headers });
			if (!res.ok) return [];
			const data: Record<string, string>[] = await res.json();
			return data.map(mapToScholar);
		} catch {
			return [];
		}
	},

	search: async (q: string, fetchFn: typeof fetch = fetch): Promise<Scholar[]> => {
		try {
			const token = getToken();
			const headers: Record<string, string> = {};
			if (token) headers['Authorization'] = `Bearer ${token}`;
			const url = `${API_BASE_URL}/api/viewer/experts?q=${encodeURIComponent(q)}`;
			const res = await fetchFn(url, { headers });
			if (!res.ok) return [];
			const data: Record<string, string>[] = await res.json();
			return data.map(mapToScholar);
		} catch {
			return [];
		}
	},

	getBySlug: async (slug: string, fetchFn: typeof fetch = fetch): Promise<Scholar | null> => {
		const all = await scholarsService.list(fetchFn);
		return all.find((s) => s.slug === slug) ?? null;
	}
};
