import type { ResearchGroup } from '$lib/auth/types';

export const parseResearchInterests = (value: unknown): string[] => {
	if (Array.isArray(value)) {
		return value.filter((item): item is string => typeof item === 'string').map((item) => item.trim()).filter(Boolean);
	}

	if (typeof value !== 'string' || !value.trim()) return [];

	return value
		.split(',')
		.map((item) => item.trim())
		.filter(Boolean);
};

export const serializeResearchInterests = (interests: string[]) =>
	interests.map((interest) => interest.trim()).filter(Boolean).join(',');

export const parseResearchGroups = (value: unknown): ResearchGroup[] => {
	if (Array.isArray(value)) {
		return value.filter(
			(item): item is ResearchGroup =>
				typeof item === 'object' &&
				item !== null &&
				typeof (item as ResearchGroup).name === 'string' &&
				typeof (item as ResearchGroup).organization === 'string'
		);
	}

	if (typeof value !== 'string' || !value.trim()) return [];

	try {
		const parsed = JSON.parse(value);
		if (Array.isArray(parsed)) return parseResearchGroups(parsed);
	} catch {
		return value
			.split(/\n{2,}|\s*---\s*/)
			.map((entry) => entry.trim())
			.filter(Boolean)
			.map((entry) => {
				const [name = entry, organization = ''] = entry.split(/\s+[—-]\s+/);
				return { name: name.trim(), organization: organization.trim() };
			});
	}

	return [];
};

export const serializeResearchGroups = (groups: ResearchGroup[]) =>
	JSON.stringify(groups.map((group) => ({ name: group.name.trim(), organization: group.organization.trim() })).filter((group) => group.name || group.organization));

export const hasCompleteResearchGroups = (groups: ResearchGroup[]) =>
	groups.every((group) => Boolean(group.name.trim()) && Boolean(group.organization.trim()));