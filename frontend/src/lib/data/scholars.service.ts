import { browser } from '$app/environment';
import { PUBLIC_API_BASE_URL } from '$env/static/public';
import type { Scholar } from '$lib/types/scholar';

const slugify = (name: string) =>
	name
		.toLowerCase()
		.replace(/[^a-z0-9]+/g, '-')
		.replace(/(^-|-$)/g, '');

const mapToScholar = (e: Record<string, string>): Scholar => ({
	id: e.id,
	slug: slugify(e.fullName ?? e.id),
	fullName: e.fullName ?? '',
	qualifications: e.highestQualification ?? '',
	role: (e.academicRank as Scholar['role']) ?? 'Lecturer',
	headline: [e.titlePrefix, e.academicRank, e.department].filter(Boolean).join(' · '),
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
	shortBio: e.consultancyExperience ?? e.notes ?? '',
	sections: [],
	links: [],
	highestQualification: e.highestQualification as Scholar['highestQualification'],
	faculty: e.faculty,
	department: e.department,
	yearsOfConsultancyExperience: e.yearsOfConsultancyExperience,
	consultancyAvailability: e.consultancyAvailability ? [e.consultancyAvailability] : [],
	preferredConsultancyTypes: e.preferredConsultancyTypes ? e.preferredConsultancyTypes.split(',').map((s: string) => s.trim()) : [],
	geographicScope: e.geographicScope ? [e.geographicScope] : [],
	languagesSpoken: e.languagesSpoken ? e.languagesSpoken.split(',').map((s: string) => s.trim()) : [],
	areasOfExpertise: e.areasOfExpertise ? e.areasOfExpertise.split(',').map((s: string) => s.trim()) : [],
	industrialAreasOfExpertise: e.industrialAreasOfExpertise ? e.industrialAreasOfExpertise.split(',').map((s: string) => s.trim()) : [],
	sections: [
		e.notes ? { title: 'Biography', body: e.notes } : null,
		e.consultancyExperience ? { title: 'Consultancy Experience', body: e.consultancyExperience } : null,
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
			const res = await fetchFn(`${PUBLIC_API_BASE_URL}/api/viewer/experts`, { headers });
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
