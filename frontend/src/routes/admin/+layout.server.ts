import { parseSession, SESSION_COOKIE_NAME } from '$lib/auth/session';
import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async ({ cookies }) => {
	const session = parseSession(cookies.get(SESSION_COOKIE_NAME));
	if (!session || session.role !== 'ADMIN') {
		throw redirect(302, '/login');
	}

	return { session };
};
