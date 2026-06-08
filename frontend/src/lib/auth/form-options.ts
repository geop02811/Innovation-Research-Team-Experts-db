export const titlePrefixOptions = ['Eng', 'Dr', 'Prof', 'Mr', 'Mrs', 'Ms'] as const;
export const academicRankOptions = [
	'Lecturer',
	'Senior Lecturer',
	'Associate Professor',
	'Professor',
	'Innovation Lead'
] as const;
export const highestQualificationOptions = ['BSc', 'Masters', 'PhD'] as const;
export const facultyOptions = [
	'Agriculture Environment And Food Systems',
	'Arts and Humanities',
	'Business Management Sciences And Economics',
	'Computer Engineering Informatics And Communications',
	'Education',
	'Engineering And The Built Environment',
	'Law',
	'Medicine And Health Sciences',
	'Science',
	'Social And Behavioural Sciences',
	'Veterinary Science',
	'Institutes & Units '
] as const;

const defaultDepartmentOptions = [
	'Business',
	'Engineering',
	'ICT',
	'Education',
	'Health Sciences',
	'Agriculture',
	'Social Sciences'
] as const;

type FacultyName = (typeof facultyOptions)[number];

export const facultyDepartmentMap: Record<FacultyName, readonly string[]> = {
	'Agriculture Environment And Food Systems': [
		'Agricultural Economics and Development',
		'Animal Science',
		'Crop Science',
		'Soil Science and Agricultural Engineering'
	],
	'Arts and Humanities': ['Arts', 'Humanities', 'Languages and Culture'],
	'Business Management Sciences And Economics': [
		'Accounting and Finance',
		'Business Management',
		'Economics and Econometrics'
	],
	'Computer Engineering Informatics And Communications': [
		'Analytics and Informatics',
		'Computer Engineering',
		'Computer Science',
		'Electronics and Telecommunications',
		'Centre for ICT Product Development Services and Training',
		'Centre for Computer Technology Training and Development'
	],
	'Education' : ['Teacher Education', 'Curriculum and Instruction', 'Educational Foundations'],
	'Engineering And The Built Environment': [
		'Civil Engineering',
		'Mechanical Engineering',
		'Electrical Engineering',
		'Architecture and Built Environment'
	],
	'Law': ['Public Law', 'Private Law', 'Commercial Law'],
	'Medicine And Health Sciences': [
		'Department of Surgery',
		'Department of Community Medicine',
		'Department of Paediatrics and Child Health',
		'Department of Obstetrics and Gynaecology',
		'Department of Pharmacy',
		'Department of Nursing Science',
		'Department of Psychiatry',
		'Institute of Continuing Health Education',
		'Department of Rehabilitation',
		'Department of Anaesthesia and Critical Care Medicine',
		'Department of Haematology',
		'Department of Histopathology',
		'Department of Medical Laboratory Sciences',
		'Clinical Pharmacology and Toxicology',
		'Department of Medical Microbiology',
		'Department of Radiology',
		'Animal House Unit',
		'Department of Physiology',
		'Department of Medicine',
		'Department of Dentistry'
	],
	'Science': ['Biological Sciences', 'Chemical Sciences', 'Mathematical Sciences', 'Physical Sciences'],
	'Social And Behavioural Sciences': [
		'Psychology',
		'Sociology',
		'Social Work',
		'Political and Administrative Studies'
	],
	'Veterinary Science': ['Veterinary Clinical Studies', 'Veterinary Pathobiology', 'Veterinary Public Health'],
	'Institutes & Units': ['Research Institutes', 'Centers of Excellence', 'University Units']
};

export const allDepartmentOptions = Array.from(
	new Set([...defaultDepartmentOptions, ...Object.values(facultyDepartmentMap).flat()])
);

export const departmentOptions = allDepartmentOptions;

export const getDepartmentOptionsByFaculty = (faculty: string) => {
	if (!faculty) {
		return allDepartmentOptions;
	}

	return facultyDepartmentMap[faculty as FacultyName] ?? defaultDepartmentOptions;
};
export const yearsOfConsultancyOptions = ['0-2 years', '3-5 years', '6-10 years', '10+ years'] as const;
export const consultancyAvailabilityOptions = [
	'Full-time',
	'Part-time',
	'Short-term (<3 months)',
	'Medium-term (3-12 months)',
	'Long-term (>1 year)'
] as const;
export const preferredConsultancyTypeOptions = [
	'Advisory',
	'Training & Capacity Building',
	'Project Implementation',
	'Monitoring & Evaluation',
	'Research & Policy Analysis',
	'Technical Support',
	'Workshop Facilitation'
] as const;
export const geographicScopeOptions = ['Local', 'Regional', 'Continental', 'International'] as const;
export const skillsOptions = [
	'Leadership',
	'Training',
	'Project Management',
	'Facilitation',
	'Communication',
	'CAD',
	'Simulation',
	'End of Project Evaluation',
	'Feasibility studies',
	'Other'
] as const;
export const languageOptions = [
	'English',
	'Ndebele',
	'Shona',
	'French',
	'Portuguese',
	'Swahili',
	'Other'
] as const;
export const areasOfExpertiseOptions = [
	'Human Capital Development',
	'Organizational Development',
	'Strategic Management',
	'Engineering Design',
	'Renewable Energy',
	'ICT Systems',
	'Finance & Accounting',
	'Marketing',
	'Public Policy',
	'Education',
	'Health',
	'Agriculture',
	'Legal'
] as const;
export const industrialAreasOptions = ['Health', 'Solar Energy', 'Mining', 'Education', 'Agriculture'] as const;
