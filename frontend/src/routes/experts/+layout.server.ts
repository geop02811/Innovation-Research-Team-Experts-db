import { parseSession, SESSION_COOKIE_NAME } from '$lib/auth/session';
import { redirect } from '@sveltejs/kit';
import { PUBLIC_API_BASE_URL } from '$env/static/public';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async ({ cookies, url, fetch }) => {
	const session = parseSession(cookies.get(SESSION_COOKIE_NAME));
	if (!session) {
		throw redirect(302, `/login?returnTo=${encodeURIComponent(url.pathname)}`);
	}

	// Verify current role live from the backend so a role change takes effect immediately
	// without requiring the user to log out and back in.
	try {
		const res = await fetch(`${PUBLIC_API_BASE_URL}/api/viewer/me`, {
			headers: { Authorization: `Bearer ${cookies.get('uz_token') ?? ''}` }
		});

		if (res.status === 401 || res.status === 403) {
			throw redirect(302, '/login?message=Access denied. Please log in again.');
		}

		if (res.ok) {
			const me = await res.json();
			if (me.role === 'USER') {
				throw redirect(302, '/login?message=Your account does not have permission to view expert profiles.');
			}
		}
	} catch (e: unknown) {
		// If the error is a redirect, re-throw it
		if (e instanceof Response || (e as { status?: number }).status) throw e;
		// Network error — fall back to cookie role check
		if (session.role === 'USER') {
			throw redirect(302, '/login?message=Your account does not have permission to view expert profiles.');
		}
	}

	return { session };
};
