import { parseSession, SESSION_COOKIE_NAME } from '$lib/auth/session';
import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async ({ cookies, url }) => {
	const session = parseSession(cookies.get(SESSION_COOKIE_NAME));
	if (!session || (session.status !== 'APPROVED' && session.status !== 'ACTIVE')) {
		const returnTo = encodeURIComponent(url.pathname);
		throw redirect(302, `/login?returnTo=${returnTo}`);
	}

	return { session };
};
