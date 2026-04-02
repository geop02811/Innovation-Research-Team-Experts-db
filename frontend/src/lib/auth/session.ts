import type { SessionUser } from '$lib/auth/types';

export const SESSION_COOKIE_NAME = 'uz_session';

export const serializeSession = (session: SessionUser): string =>
	encodeURIComponent(JSON.stringify(session));

export const parseSession = (rawValue?: string | null): SessionUser | null => {
	if (!rawValue) {
		return null;
	}

	try {
		return JSON.parse(decodeURIComponent(rawValue)) as SessionUser;
	} catch {
		return null;
	}
};
