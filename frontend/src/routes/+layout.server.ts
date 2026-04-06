import { parseSession, SESSION_COOKIE_NAME } from '$lib/auth/session';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async ({ cookies }) => {
	const session = parseSession(cookies.get(SESSION_COOKIE_NAME));
	return { session };
};
