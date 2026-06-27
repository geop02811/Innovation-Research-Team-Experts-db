export const titlePrefixOptions = ['Eng', 'Dr', 'Prof', 'Mr', 'Mrs', 'Ms'] as const;
export const academicRankOptions = [
	'Lecturer',
	'Senior Lecturer',
	'Junior Lecturer',
	'Teaching/Research Assistant',
	'Associate Professor',
	'Professor',
	'Innovation Lead',
	'Grants Manager'
] as const;
export const highestQualificationOptions = ['BSc Hons','BAcc Hons','BLaws Hons','MBChB','BDS','BSc Nursing','BPharm','BVSc','BTech', 'MSc','MA','MPhil', 'DPhil'] as const;
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
	'Institutes & Units'
] as const;

const defaultDepartmentOptions = [
		'Computer Science',
		'Department of Accountancy',
		'Department of Biomedical Informatics and Biomedical Engineering',
		'Biochemistry',
		'Marketing and Communications'
] as const;

type FacultyName = (typeof facultyOptions)[number];

export const facultyDepartmentMap: Record<FacultyName, readonly string[]> = {
	'Agriculture Environment And Food Systems': [' ',],  // didnt find any
	'Arts and Humanities': ['Department of Philosophy Religion and Ethics', 'Department of Creative Media and Communication', 'Department of Languages Literature and Culture', 'Department of History Heritage and Knowledge Systems','Department of Peace Security and Society'],
	'Business Management Sciences And Economics': [
		'Department of Accountancy',
		'Department of Business Studies',
		'Department of Tourism, Leisure and Hospitality Studies',
		'Department of Graduate School of Management'
	],
	'Computer Engineering Informatics And Communications': [
		'Analytics and Informatics',
		'Computer Engineering',
		'Computer Science',
		'Electronics and Telecommunications',
		'Centre for ICT Product Development Services and Training',
		'Centre for Computer Technology Training and Development'
	],
	'Education' : [''], //didnt find any
	'Engineering And The Built Environment': [' '],//didnt find any
	'Law': [' '], //didnt find any here
	'Medicine And Health Sciences': [
		'Department of Biomedical Informatics and Biomedical Engineering',
		'Department of Biomedical Sciences',
		'Department of Child, Adolescent and Women’s Health',
		'Department of Clinical Pharmacology and Anaesthesia and Critical Care Medicine',
		'Department of Global, Public Health and Family Medicine',
		'Department of Health Professions and Student Support',
		'Department of Internal Medicine',
		'Institute of Laboratory Diagnostic and Investigative Sciences',
		'Department of Mental Health',
		'Department of Nursing Sciences',
		'Department of Oncology, Medical Physics and Imaging Sciences',
		'Department of Oral Health',
		'Department of Pharmacy and Pharmaceutical Sciences',
		'Department of Rehabilitation Sciences',
		'Department of Surgical Sciences',
		'Department of Multi-Media Resources Centre',
		'Department of Multi Teaching Laboratories',
		'Department of Central African Journal of Medicine ',
		'Department of Services Unit'
	],
	'Science': ['Biochemistry',], //didnt find any more
	'Social And Behavioural Sciences': [
		'Applied Psychology',
		'Community and Social Development',
		'Demography Settlement and Development',
		'Governance and Public Management',
		'Social Work'
	],
	'Veterinary Science': ['Animal Disease Intelligence', 'Zoonotic Diseases and Food Safety', 'Veterinary Pharmaceuticals Ethno-medicine and Vaccinology','Aquatic Health and Apiculture','Animal Reproductive Technologies and Nutrition'],
	'Institutes & Units': ['Environment Climate and Sustainable Development Institute', 'Centre for Postgraduate Studies']
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
