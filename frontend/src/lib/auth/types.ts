export type AccountStatus = 'PENDING' | 'APPROVED' | 'ACTIVE' | 'DISABLED';
export type UserRole = 'USER' | 'VIEWER' | 'ADMIN';

export interface ExpertProfile {
	titlePrefix: 'Eng' | 'Dr' | 'Prof' | 'Mr' | 'Mrs' | 'Ms';
	fullName: string;
	contactDetails: string;
	academicRank: 'Lecturer' | 'Senior Lecturer' | 'Associate Professor' | 'Professor';
	universityEmail: string;
	phoneNumber: string;
	highestQualification: string;
	professionalMemberships: string;
	complianceAccreditation: string;
	faculty: string;
	department:
		| 'Business'
		| 'Engineering'
		| 'ICT'
		| 'Education'
		| 'Health Sciences'
		| 'Agriculture'
		| 'Social Sciences';
	yearsOfConsultancyExperience: '0-2 years' | '3-5 years' | '6-10 years' | '10+ years';
	consultancyExperience: string;
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
	areasOfExpertise: string[];
	industrialAreasOfExpertise: string[];
	notes: string;
	profilePhotoDataUrl: string;
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
	consultancyAvailability: string | null;
	preferredConsultancyTypes: string | null;
	geographicScope: string | null;
	skillsAndCompetences: string | null;
	languagesSpoken: string | null;
	areasOfExpertise: string | null;
	industrialAreasOfExpertise: string | null;
	notes: string | null;
	profilePhotoDataUrl: string | null;
}
