import { error } from '@sveltejs/kit';
import { scholarsService } from '$lib/data/scholars.service';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ params }) => {
	const scholar = await scholarsService.getBySlug(params.slug);

	if (!scholar) {
		throw error(404, 'Scholar profile not found');
	}

	return {
		scholar
	};
};
