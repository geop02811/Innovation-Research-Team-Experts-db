import { browser } from '$app/environment';
import { PUBLIC_API_BASE_URL } from '$env/static/public';
import { SESSION_COOKIE_NAME, parseSession, serializeSession } from '$lib/auth/session';
import type { ExpertProfile, SessionUser } from '$lib/auth/types';

const JWT_STORAGE_KEY = 'uz_token';

const setSessionCookie = (session: SessionUser | null) => {
	if (!browser) return;

	if (!session) {
		document.cookie = `${SESSION_COOKIE_NAME}=; Path=/; Max-Age=0; SameSite=Lax`;
		return;
	}

	document.cookie = `${SESSION_COOKIE_NAME}=${serializeSession(session)}; Path=/; Max-Age=2592000; SameSite=Lax`;
};

export const authService = {
	getSessionFromCookie: (): SessionUser | null => {
		if (!browser) return null;
		const parts = document.cookie.split(';').map((p) => p.trim());
		const match = parts.find((part) => part.startsWith(`${SESSION_COOKIE_NAME}=`));
		return parseSession(match?.split('=').slice(1).join('='));
	},

	getToken: (): string | null => {
		if (!browser) return null;
		return localStorage.getItem(JWT_STORAGE_KEY);
	},

	signup: async (payload: {
		email: string;
		password: string;
		profile: ExpertProfile;
	}): Promise<{ ok: boolean; message: string }> => {
		const parts = payload.profile.fullName.trim().split(/\s+/);
		const name = parts[0] ?? payload.profile.fullName;
		const surname = parts.length > 1 ? parts.slice(1).join(' ') : name;

		try {
			const res = await fetch(`${PUBLIC_API_BASE_URL}/api/auth/signup`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ name, surname, email: payload.email, password: payload.password })
			});

			if (res.status === 201) {
				return { ok: true, message: 'Account created. Await admin approval before login.' };
			}

			const text = await res.text();
			return { ok: false, message: text || 'Signup failed. Please try again.' };
		} catch {
			return { ok: false, message: 'Could not reach the server. Please try again later.' };
		}
	},

	login: async (
		email: string,
		password: string
	): Promise<{ ok: boolean; message: string; session?: SessionUser }> => {
		try {
			const res = await fetch(`${PUBLIC_API_BASE_URL}/api/auth/login`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ email, password })
			});

			if (!res.ok) {
				const text = await res.text();
				return { ok: false, message: text || 'Invalid email or password.' };
			}

			const data = await res.json();

			if (browser) {
				localStorage.setItem(JWT_STORAGE_KEY, data.token);
			}

			const session: SessionUser = {
				id: data.id,
				email: data.email,
				fullName: data.fullName,
				role: data.role,
				status: data.status
			};

			setSessionCookie(session);
			return { ok: true, message: 'Login successful', session };
		} catch {
			return { ok: false, message: 'Could not reach the server. Please try again later.' };
		}
	},

	logout: () => {
		setSessionCookie(null);
		if (browser) {
			localStorage.removeItem(JWT_STORAGE_KEY);
		}
	},

	// TODO: connect to backend admin API
	listUsers: (): import('$lib/auth/types').UserAccount[] => [],
	approveUser: (_id: string, _comment: string) => {},
	rejectUser: (_id: string, _comment: string) => {},
	updateRole: (_id: string, _role: import('$lib/auth/types').UserRole) => {}
};
