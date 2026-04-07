import { scholarsService } from '$lib/data/scholars.service';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
	const scholars = await scholarsService.list(fetch);

	return {
		scholars
	};
};
