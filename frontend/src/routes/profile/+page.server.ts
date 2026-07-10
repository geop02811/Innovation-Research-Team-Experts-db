import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ parent, cookies, fetch }) => {
	const { session } = await parent();

	if (!session) {
		redirect(302, '/login');
	}

	const token = cookies.get('uz_token');
	if (!token) {
		redirect(302, '/login');
	}

	try {
		const res = await fetch('/api/viewer/profile', {
			headers: { Authorization: `Bearer ${token}` }
		});

		if (res.status === 401 || res.status === 403) {
			redirect(302, '/login?message=Session expired. Please log in again.');
		}

		if (!res.ok) {
			return { profile: null };
		}

		const profile = await res.json();
		return { profile };
	} catch {
		return { profile: null };
	}
};
