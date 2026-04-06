import { scholarsService } from '$lib/data/scholars.service';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
	const scholars = await scholarsService.list();

	return {
		scholars
	};
};
