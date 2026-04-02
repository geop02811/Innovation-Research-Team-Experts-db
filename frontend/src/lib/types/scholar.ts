export type ScholarRole = 'Professor' | 'Research Fellow' | 'Doctoral Scholar' | 'Postdoctoral Scholar';

export interface ScholarLink {
	label: string;
	url: string;
}

export interface ScholarProfileSection {
	title: string;
	body: string;
}

export interface Scholar {
	id: string;
	slug: string;
	fullName: string;
	qualifications: string;
	role: ScholarRole;
	headline: string;
	college: string;
	program: string;
	cohort: string;
	electionYear: number;
	avatarUrl: string;
	country: string;
	email: string;
	phone: string;
	location: string;
	researchAreas: string[];
	shortBio: string;
	sections: ScholarProfileSection[];
	links: ScholarLink[];
	highestQualification?: 'BSc' | 'Masters' | 'PhD';
	faculty?: string;
	department?: string;
	yearsOfConsultancyExperience?: string;
	consultancyAvailability?: string[];
	preferredConsultancyTypes?: string[];
	geographicScope?: string[];
	languagesSpoken?: string[];
	areasOfExpertise?: string[];
	industrialAreasOfExpertise?: string[];
}
