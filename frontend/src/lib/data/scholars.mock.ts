import type { Scholar } from '$lib/types/scholar';

export const SCHOLARS_MOCK: Scholar[] = [
	{
		id: '1',
		slug: 'michael-osborne',
		fullName: 'Michael Osborne',
		qualifications: 'BSc (Hons) BEng (Hons) DPhil',
		role: 'Professor',
		headline: 'Professor of Machine Learning',
		college: 'University of Zimbabwe',
		program: 'Engineering Science',
		cohort: '1998 UZ Expert',
		electionYear: 1998,
		avatarUrl:
			'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=900&q=80',
		country: 'Zimbabwe',
		email: 'mosb@robots.ox.ac.uk',
		phone: '+44 1865 616622',
		location: 'Engineering Faculty, University of Zimbabwe',
		researchAreas: ['Artificial Intelligence', 'Future of Work', 'Responsible Innovation'],
		bio:
			'Michael leads research at the intersection of artificial intelligence and economic transformation, with a focus on practical systems that expand equitable opportunity.',
		shortBio:
			'Michael leads research at the intersection of artificial intelligence and economic transformation, with a focus on practical systems that expand equitable opportunity.',
		sections: [
			{
				title: 'Biography',
				body: 'Michael Osborne is a Professor of Machine Learning at the University of Zimbabwe, where he works on computational methods that help governments and institutions anticipate technological change.'
			},
			{
				title: 'Research Interest',
				body: 'His current work explores probabilistic forecasting, labor market transitions, and how AI systems can support public-interest decision making in emerging economies.'
			},
			{
				title: 'Teaching and Supervision',
				body: 'He supervises multidisciplinary student teams building applied AI tools and regularly teaches courses in machine learning, statistics, and policy-aware systems design.'
			}
		],
		links: [
			{ label: 'Department Profile', url: 'https://eng.ox.ac.uk' },
			{ label: 'Publications', url: 'https://scholar.google.com' }
		],
		highestQualification: 'PhD',
		faculty: 'Computer Engineering',
		department: 'Engineering',
		yearsOfConsultancyExperience: '10+ years',
		consultancyAvailability: ['Part-time', 'Long-term (>1 year)'],
		preferredConsultancyTypes: ['Advisory', 'Technology Transfer', 'Research & Policy Analysis'],
		geographicScope: ['Regional', 'International'],
		languagesSpoken: ['English', 'French'],
		areasOfExpertise: ['Artificial Intelligence', 'Engineering Design', 'Strategic Management'],
		industrialAreasOfExpertise: ['Education', 'Health']
	},
	{
		id: '2',
		slug: 'rutendo-moyo',
		fullName: 'Rutendo Moyo',
		qualifications: 'BSc (Hons) MSc DPhil Candidate',
		role: 'Doctoral Scholar',
		headline: 'DPhil Candidate in Digital Governance',
		college: 'University of Zimbabwe',
		program: 'Public Policy',
		cohort: '2019 UZ Expert',
		electionYear: 2019,
		avatarUrl:
			'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=900&q=80',
		country: 'Zimbabwe',
		email: 'rutendo.moyo@policy.ox.ac.uk',
		phone: '+44 1865 510100',
		location: 'University of Zimbabwe, Harare',
		researchAreas: ['Digital Public Infrastructure', 'Identity Systems', 'Civic Technology'],
		bio:
			'Rutendo researches trustworthy digital public systems and collaborates with civic institutions on policy and implementation frameworks for inclusive service delivery.',
		shortBio:
			'Rutendo researches trustworthy digital public systems and collaborates with civic institutions on policy and implementation frameworks for inclusive service delivery.',
		sections: [
			{
				title: 'Biography',
				body: 'Rutendo Moyo is a DPhil candidate studying governance architectures for digital identity and payments ecosystems in lower-resource environments.'
			},
			{
				title: 'Research Interest',
				body: 'Her work centers on privacy-preserving identity, interoperability standards, and institutional capacity building for resilient digital state platforms.'
			},
			{
				title: 'Teaching and Supervision',
				body: 'She partners with public sector organizations and research networks to translate policy research into practical implementation guidance.'
			}
		],
		links: [
			{ label: 'ORCID', url: 'https://orcid.org' },
			{ label: 'LinkedIn', url: 'https://linkedin.com' }
		],
		highestQualification: 'Masters',
		faculty: 'Computer Engineering',
		department: 'ICT',
		yearsOfConsultancyExperience: '3–5 years',
		consultancyAvailability: ['Part-time', 'Medium-term (3–12 months)'],
		preferredConsultancyTypes: ['Training & Capacity Building', 'Project Implementation'],
		geographicScope: ['Local', 'Regional'],
		languagesSpoken: ['English', 'Shona'],
		areasOfExpertise: ['ICT Systems', 'Organizational Development', 'Public Policy'],
		industrialAreasOfExpertise: ['Education', 'Health']
	},
	{
		id: '3',
		slug: 'tapiwa-ncube',
		fullName: 'Tapiwa Ncube',
		qualifications: 'BEng MSc PhD',
		role: 'Postdoctoral Scholar',
		headline: 'Postdoctoral Researcher in Climate Informatics',
		college: 'University of Zimbabwe',
		program: 'Environmental Engineering',
		cohort: '2016 UZ Expert',
		electionYear: 2016,
		avatarUrl:
			'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=900&q=80',
		country: 'Zimbabwe',
		email: 'tapiwa.ncube@eng.ox.ac.uk',
		phone: '+44 1865 288888',
		location: 'Engineering Faculty, University of Zimbabwe',
		researchAreas: ['Climate Risk Modelling', 'Hydrology', 'Decision Support Systems'],
		bio:
			'Tapiwa develops climate risk models for water systems and leads translational projects connecting engineering research with municipal planning decisions.',
		shortBio:
			'Tapiwa develops climate risk models for water systems and leads translational projects connecting engineering research with municipal planning decisions.',
		sections: [
			{
				title: 'Biography',
				body: 'Tapiwa Ncube is a postdoctoral researcher focused on hydroclimatic forecasting and adaptation planning in regions facing compounding climate risk.'
			},
			{
				title: 'Research Interest',
				body: 'His research combines remote sensing, probabilistic modeling, and optimization to improve planning for drought and flood resilience.'
			},
			{
				title: 'Teaching and Supervision',
				body: 'He collaborates with municipalities, infrastructure operators, and NGOs to pilot decision tools for long-term infrastructure resilience.'
			}
		],
		links: [
			{ label: 'Research Group', url: 'https://ox.ac.uk' },
			{ label: 'GitHub', url: 'https://github.com' }
		],
		highestQualification: 'PhD',
		faculty: 'Computer Engineering',
		department: 'Engineering',
		yearsOfConsultancyExperience: '6–10 years',
		consultancyAvailability: ['Full-time', 'Long-term (>1 year)'],
		preferredConsultancyTypes: ['Research & Policy Analysis', 'Project Implementation'],
		geographicScope: ['Regional', 'Continental', 'International'],
		languagesSpoken: ['English', 'Shona', 'Ndebele'],
		areasOfExpertise: ['Renewable Energy', 'Engineering Design', 'Strategic Management'],
		industrialAreasOfExpertise: ['Agriculture', 'Health']
	}
];
