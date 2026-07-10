export type AccountStatus = 'PENDING' | 'APPROVED' | 'ACTIVE' | 'DISABLED';
export type UserRole = 'USER' | 'VIEWER' | 'ADMIN';

export interface LanguageProficiency {
	language: string;
	proficiency: string;
}

export interface ProfileLink {
	type: string;
	label: string;
	url: string;
}

export interface ProfessionalExperience {
	title: string;
	employmentType: string;
	organization: string;
	isCurrent: boolean;
	startMonth: string;
	startYear: string;
	endMonth: string;
	endYear: string;
	location: string;
	locationType: string;
	summary: string;
}

export interface ExpertProfile {
	titlePrefix: 'Eng' | 'Dr' | 'Prof' | 'Mr' | 'Mrs' | 'Ms';
	fullName: string;
	contactDetails: string;
	bio: string;
	academicRank: string;
	universityEmail: string;
	phoneNumber: string;
	highestQualification: string;
	professionalMemberships: string;
	complianceAccreditation: string;
	faculty: string;
	department: string;
	yearsOfConsultancyExperience: '0-2 years' | '3-5 years' | '6-10 years' | '10+ years';
	consultancyExperience: string;
	professionalExperiences: ProfessionalExperience[];
	consultancyAvailability:
		| 'Full-time'
		| 'Part-time'
		| 'Short-term (<3 months)'
		| 'Medium-term (3-12 months)'
		| 'Long-term (>1 year)';
	preferredConsultancyTypes: string[];
	geographicScope: 'Local' | 'Regional' | 'Continental' | 'International';
	skillsAndCompetences: string[];
	languagesSpoken: string[];
	languageProficiencies: LanguageProficiency[];
	areasOfExpertise: string[];
	industrialAreasOfExpertise: string[];
	notes: string;
	profileLinks: ProfileLink[];
	profilePhotoDataUrl: string;
	cvDataUrl?: string;
	universityIdDataUrl?: string;
}

export interface UserAccount {
	id: string;
	email: string;
	password: string;
	role: UserRole;
	status: AccountStatus;
	approvalComment?: string;
	profile: ExpertProfile;
	createdAt: string;
	updatedAt: string;
}

export interface SessionUser {
	id: string;
	email: string;
	role: UserRole;
	status: AccountStatus;
	fullName: string;
}

export interface LoginResponse {
	token: string;
	email: string;
	role: UserRole;
}

/** Flat user record returned by GET /api/admin/users */
export interface AdminUser {
	id: string;
	name: string;
	surname: string;
	email: string;
	role: UserRole;
	status: AccountStatus;
	// Profile fields (null for admin/seeded accounts)
	titlePrefix: string | null;
	fullName: string | null;
	contactDetails: string | null;
	bio: string | null;
	academicRank: string | null;
	universityEmail: string | null;
	phoneNumber: string | null;
	highestQualification: string | null;
	professionalMemberships: string | null;
	complianceAccreditation: string | null;
	faculty: string | null;
	department: string | null;
	yearsOfConsultancyExperience: string | null;
	consultancyExperience: string | null;
	professionalExperiences: string | null;
	consultancyAvailability: string | null;
	preferredConsultancyTypes: string | null;
	geographicScope: string | null;
	skillsAndCompetences: string | null;
	languagesSpoken: string | null;
	languageProficiencies: string | null;
	areasOfExpertise: string | null;
	industrialAreasOfExpertise: string | null;
	notes: string | null;
	profileLinks: string | null;
	profilePhotoDataUrl: string | null;
	cvDataUrl: string | null;
	universityIdDataUrl: string | null;
}
export interface PendingUserNotification {
	id: string;
	displayName: string;
	email: string;
	faculty: string | null;
	academicRank: string | null;
	joinedAgo: string;
}

export interface AdminNotificationsResponse {
	pendingCount: number;
	pendingUsers: PendingUserNotification[];
}

export interface NewExpertNotification {
	id: string;
	displayName: string;
	fullName: string;
	faculty: string | null;
	academicRank: string | null;
	areasOfExpertise: string | null;
	joinedAgo: string;
}

export interface ViewerNotificationsResponse {
	newExpertsCount: number;
	newExperts: NewExpertNotification[];
}