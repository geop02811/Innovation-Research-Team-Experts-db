import type { ComplianceCredential, ProfessionalMembership } from '$lib/auth/types';

const standardMembershipOrganizations = new Set(['IEEE', 'ZIE', 'ACCA']);
const standardMembershipPositions = new Set(['Member', 'Professional Member', 'Fellow']);

export const membershipOrganizationName = (membership: ProfessionalMembership) =>
	membership.organization === 'Other'
		? membership.customOrganization.trim()
		: membership.organization.trim();

export const membershipPositionHeld = (membership: ProfessionalMembership) =>
	membership.positionHeld === 'Other'
		? (membership.customPositionHeld ?? '').trim()
		: membership.positionHeld.trim();

export const membershipPeriod = (membership: ProfessionalMembership) => {
	const start = [membership.startMonth, membership.startYear].filter(Boolean).join(' ');
	if (!start) return '';
	const end = membership.isCurrent
		? 'Present'
		: [membership.endMonth, membership.endYear].filter(Boolean).join(' ');
	return [start, end].filter(Boolean).join(' - ');
};

export const credentialIssueDate = (credential: ComplianceCredential) =>
	[credential.issueMonth, credential.issueYear].filter(Boolean).join(' ');

export const serializeProfessionalMemberships = (memberships: ProfessionalMembership[]) =>
	memberships
		.map((membership) => {
			const lines = [
				`Membership / Organization: ${membershipOrganizationName(membership)}`,
				`Position Held: ${membershipPositionHeld(membership)}`,
				`Associated With: ${membership.associatedWith.trim()}`,
				`Period: ${membershipPeriod(membership)}`,
				'Description:',
				membership.description
					.split('\n')
					.map((line) => line.trim())
					.filter(Boolean)
					.map((line) => `- ${line}`)
					.join('\n')
			].filter((line) => line !== '');

			return lines.join('\n');
		})
		.filter(Boolean)
		.join('\n\n---\n\n');

export const serializeComplianceCredentials = (credentials: ComplianceCredential[]) =>
	credentials
		.map((credential) =>
			[
				`Name: ${credential.name.trim()}`,
				`Issuing Organization: ${credential.issuingOrganization.trim()}`,
				`Issue Date: ${credentialIssueDate(credential)}`,
				`Credential ID / URL: ${credential.credentialUrl.trim()}`,
				`Skills Associated: ${credential.skillsAssociated.trim()}`
			]
				.filter(Boolean)
				.join('\n')
		)
		.filter(Boolean)
		.join('\n\n---\n\n');

const parseLineValue = (block: string, label: string) => {
	const escapedLabel = label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
	const match = block.match(new RegExp(`^${escapedLabel}:\\s*(.*)$`, 'im'));
	return match?.[1]?.trim() ?? '';
};

const parseMonthYear = (value: string) => {
	const [month = '', year = ''] = value.trim().split(/\s+/);
	return { month, year };
};

const parseMembershipPeriod = (value: string) => {
	const [startValue = '', endValue = ''] = value.split(/\s+-\s+/);
	const { month: startMonth, year: startYear } = parseMonthYear(startValue);
	const isCurrent = !endValue || /^present$/i.test(endValue.trim());
	const { month: endMonth, year: endYear } = isCurrent
		? { month: '', year: '' }
		: parseMonthYear(endValue);

	return { startMonth, startYear, isCurrent, endMonth, endYear };
};

const normalizeMembershipOrganization = (organization: string) => ({
	organization: standardMembershipOrganizations.has(organization) ? organization : 'Other',
	customOrganization: standardMembershipOrganizations.has(organization) ? '' : organization
});

const normalizeMembershipPosition = (positionHeld: string) => ({
	positionHeld: standardMembershipPositions.has(positionHeld) ? positionHeld : 'Other',
	customPositionHeld: standardMembershipPositions.has(positionHeld) ? '' : positionHeld
});

export const parseProfessionalMemberships = (value: unknown): ProfessionalMembership[] => {
	if (Array.isArray(value)) {
		return value
			.filter(
				(item): item is ProfessionalMembership =>
					typeof item === 'object' &&
					item !== null &&
					typeof (item as ProfessionalMembership).organization === 'string' &&
					typeof (item as ProfessionalMembership).positionHeld === 'string'
			)
			.map((membership) => ({
				...membership,
				customPositionHeld: membership.customPositionHeld ?? '',
				isCurrent: membership.isCurrent ?? true,
				endMonth: membership.endMonth ?? '',
				endYear: membership.endYear ?? ''
			}));
	}

	if (typeof value !== 'string' || !value.trim()) return [];

	const blocks = value.split(/\n\s*---\s*\n|\n{2,}/).map((block) => block.trim()).filter(Boolean);

	return blocks.map((block) => {
		const organization = parseLineValue(block, 'Membership / Organization');
		const positionHeld = parseLineValue(block, 'Position Held');
		const { startMonth, startYear, isCurrent, endMonth, endYear } = parseMembershipPeriod(
			parseLineValue(block, 'Period')
		);
		const description = block
			.split('\n')
			.filter((line) => line.trim().startsWith('-'))
			.map((line) => line.trim().replace(/^[-*]\s*/, ''))
			.join('\n');

		return {
			...normalizeMembershipOrganization(organization || block),
			...normalizeMembershipPosition(positionHeld),
			isCurrent,
			associatedWith: parseLineValue(block, 'Associated With'),
			startMonth,
			startYear,
			endMonth,
			endYear,
			description: description || block
		};
	});
};

export const parseComplianceCredentials = (value: unknown): ComplianceCredential[] => {
	if (Array.isArray(value)) {
		return value.filter(
			(item): item is ComplianceCredential =>
				typeof item === 'object' &&
				item !== null &&
				typeof (item as ComplianceCredential).name === 'string' &&
				typeof (item as ComplianceCredential).issuingOrganization === 'string'
		);
	}

	if (typeof value !== 'string' || !value.trim()) return [];

	const blocks = value.split(/\n\s*---\s*\n|\n{2,}/).map((block) => block.trim()).filter(Boolean);

	return blocks.map((block) => {
		const { month: issueMonth, year: issueYear } = parseMonthYear(parseLineValue(block, 'Issue Date'));

		return {
			name: parseLineValue(block, 'Name') || block,
			issuingOrganization: parseLineValue(block, 'Issuing Organization'),
			issueMonth,
			issueYear,
			credentialUrl: parseLineValue(block, 'Credential ID / URL'),
			skillsAssociated: parseLineValue(block, 'Skills Associated')
		};
	});
};

export const hasCompleteProfessionalMemberships = (memberships: ProfessionalMembership[]) =>
	memberships.length > 0 &&
	memberships.every(
		(membership) =>
			Boolean(membershipOrganizationName(membership)) &&
			Boolean(membershipPositionHeld(membership)) &&
			Boolean(membership.description.trim())
	);

export const hasCompleteComplianceCredentials = (credentials: ComplianceCredential[]) =>
	credentials.length > 0 &&
	credentials.every(
		(credential) =>
			Boolean(credential.name.trim()) &&
			Boolean(credential.issuingOrganization.trim()) &&
			Boolean(credential.skillsAssociated.trim())
	);