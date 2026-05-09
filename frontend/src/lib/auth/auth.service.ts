import { browser } from '$app/environment';
import { PUBLIC_API_BASE_URL } from '$env/static/public';
import { SESSION_COOKIE_NAME, parseSession, serializeSession } from '$lib/auth/session';
import type {
	AdminNotificationsResponse,
	ExpertProfile,
	SessionUser,
	ViewerNotificationsResponse
} from '$lib/auth/types';

const JWT_STORAGE_KEY = 'uz_token';

const clearSession = () => {
	if (!browser) return;
	localStorage.removeItem(JWT_STORAGE_KEY);
	document.cookie = `${SESSION_COOKIE_NAME}=; Path=/; Max-Age=0; SameSite=Lax`;
	document.cookie = `uz_token=; Path=/; Max-Age=0; SameSite=Lax`;
};

/** Authenticated fetch — auto-logs out and redirects to /login on 401/403. */
const authFetch = async (input: string, init: RequestInit = {}): Promise<Response> => {
	const token = browser ? localStorage.getItem(JWT_STORAGE_KEY) : null;
	const res = await fetch(input, {
		...init,
		headers: {
			...(init.headers ?? {}),
			Authorization: `Bearer ${token}`
		}
	});
	if (res.status === 401) {
		clearSession();
		if (browser) window.location.href = '/login?message=Session expired. Please log in again.';
	} else if (res.status === 403) {
		clearSession();
		if (browser) window.location.href = '/login?message=Access denied. Your account permissions may have changed.';
	}
	return res;
};

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
		const p = payload.profile;

		try {
			const res = await fetch(`${PUBLIC_API_BASE_URL}/api/auth/signup`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({
					name,
					surname,
					email: payload.email,
					password: payload.password,
					titlePrefix: p.titlePrefix,
					fullName: p.fullName,
					contactDetails: p.contactDetails,
					academicRank: p.academicRank,
					universityEmail: p.universityEmail,
					phoneNumber: p.phoneNumber,
					highestQualification: p.highestQualification,
					professionalMemberships: p.professionalMemberships,
					complianceAccreditation: p.complianceAccreditation,
					faculty: p.faculty,
					department: p.department,
					yearsOfConsultancyExperience: p.yearsOfConsultancyExperience,
					consultancyExperience: p.consultancyExperience,
					consultancyAvailability: p.consultancyAvailability,
					preferredConsultancyTypes: p.preferredConsultancyTypes.join(','),
					geographicScope: p.geographicScope,
					skillsAndCompetences: p.skillsAndCompetences.join(','),
					languagesSpoken: p.languagesSpoken.join(','),
					areasOfExpertise: p.areasOfExpertise.join(','),
					industrialAreasOfExpertise: p.industrialAreasOfExpertise.join(','),
					notes: p.notes,
					profilePhotoDataUrl: p.profilePhotoDataUrl,
					cvDataUrl: p.cvDataUrl ?? '',
					universityIdDataUrl: p.universityIdDataUrl ?? ''
				})
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
				// Also store in a cookie so server-side layout loads can send it to the backend
				document.cookie = `uz_token=${data.token}; Path=/; Max-Age=86400; SameSite=Lax`;
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
		clearSession();
	},

	listUsers: async (): Promise<{ users: import('$lib/auth/types').AdminUser[]; error?: string }> => {
		try {
			const res = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/users`);
			if (!res.ok) {
				const text = await res.text();
				return { users: [], error: `Server error ${res.status}: ${text || res.statusText}` };
			}
			return { users: await res.json() };
		} catch {
			return { users: [], error: 'Could not reach the server. Is the backend running?' };
		}
	},

	approveUser: async (id: string): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/users/${id}/approve`, { method: 'PUT' });
	},

	rejectUser: async (id: string): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/users/${id}/reject`, { method: 'PUT' });
	},

	updateRole: async (id: string, role: import('$lib/auth/types').UserRole): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/users/${id}/role`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ role })
		});
	},

	updateProfile: async (payload: Record<string, unknown>): Promise<{ ok: boolean; message: string; profile?: unknown }> => {
		try {
			const res = await authFetch(`${PUBLIC_API_BASE_URL}/api/viewer/profile`, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload)
			});
			if (!res.ok) {
				const text = await res.text();
				return { ok: false, message: text || 'Failed to save profile.' };
			}
			const profile = await res.json();
			return { ok: true, message: 'Profile saved.', profile };
		} catch {
			return { ok: false, message: 'Could not reach the server. Please try again later.' };
		}
	},

	getAdminNotifications: async (): Promise<AdminNotificationsResponse> => {
		try {
			const res = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/notifications`);
			if (!res.ok) return { pendingCount: 0, pendingUsers: [] };
			return res.json();
		} catch {
			return { pendingCount: 0, pendingUsers: [] };
		}
	},

	getViewerNotifications: async (): Promise<ViewerNotificationsResponse> => {
		try {
			const res = await authFetch(`${PUBLIC_API_BASE_URL}/api/viewer/notifications`);
			if (!res.ok) return { newExpertsCount: 0, newExperts: [] };
			return res.json();
		} catch {
			return { newExpertsCount: 0, newExperts: [] };
		}
	},

	// ── Grants (public read) ──────────────────────────────────────
	getGrants: async (): Promise<GrantItem[]> => {
		try {
			const res = await fetch(`${PUBLIC_API_BASE_URL}/api/grants`);
			if (!res.ok) return [];
			return res.json();
		} catch {
			return [];
		}
	},

	// ── Grants (admin write) ──────────────────────────────────────
	createGrant: async (payload: GrantPayload): Promise<{ ok: boolean; data?: GrantItem; message?: string }> => {
		try {
			const res = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/grants`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload)
			});
			if (!res.ok) return { ok: false, message: await res.text() };
			return { ok: true, data: await res.json() };
		} catch {
			return { ok: false, message: 'Could not reach the server.' };
		}
	},

	updateGrant: async (id: string, payload: GrantPayload): Promise<{ ok: boolean; data?: GrantItem; message?: string }> => {
		try {
			const res = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/grants/${id}`, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload)
			});
			if (!res.ok) return { ok: false, message: await res.text() };
			return { ok: true, data: await res.json() };
		} catch {
			return { ok: false, message: 'Could not reach the server.' };
		}
	},

	deleteGrant: async (id: string): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/grants/${id}`, { method: 'DELETE' });
	},

	// ── Events ────────────────────────────────────────────────────
	getEvents: async (): Promise<EventItem[]> => {
		try { const r = await fetch(`${PUBLIC_API_BASE_URL}/api/events`); return r.ok ? r.json() : []; } catch { return []; }
	},
	createEvent: async (p: EventPayload) => {
		try { const r = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/events`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(p) }); return r.ok ? { ok: true, data: await r.json() } : { ok: false, message: await r.text() }; } catch { return { ok: false, message: 'Network error' }; }
	},
	updateEvent: async (id: string, p: EventPayload) => {
		try { const r = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/events/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(p) }); return r.ok ? { ok: true, data: await r.json() } : { ok: false, message: await r.text() }; } catch { return { ok: false, message: 'Network error' }; }
	},
	deleteEvent: async (id: string): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/events/${id}`, { method: 'DELETE' });
	},

	// ── Competitions ──────────────────────────────────────────────
	getCompetitions: async (): Promise<CompetitionItem[]> => {
		try { const r = await fetch(`${PUBLIC_API_BASE_URL}/api/competitions`); return r.ok ? r.json() : []; } catch { return []; }
	},
	createCompetition: async (p: CompetitionPayload) => {
		try { const r = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/competitions`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(p) }); return r.ok ? { ok: true, data: await r.json() } : { ok: false, message: await r.text() }; } catch { return { ok: false, message: 'Network error' }; }
	},
	updateCompetition: async (id: string, p: CompetitionPayload) => {
		try { const r = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/competitions/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(p) }); return r.ok ? { ok: true, data: await r.json() } : { ok: false, message: await r.text() }; } catch { return { ok: false, message: 'Network error' }; }
	},
	deleteCompetition: async (id: string): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/competitions/${id}`, { method: 'DELETE' });
	},

	// ── Alumni News ───────────────────────────────────────────────
	getAlumniNews: async (): Promise<AlumniNewsItem[]> => {
		try { const r = await fetch(`${PUBLIC_API_BASE_URL}/api/alumni-news`); return r.ok ? r.json() : []; } catch { return []; }
	},
	createAlumniNews: async (p: AlumniNewsPayload) => {
		try { const r = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/alumni-news`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(p) }); return r.ok ? { ok: true, data: await r.json() } : { ok: false, message: await r.text() }; } catch { return { ok: false, message: 'Network error' }; }
	},
	updateAlumniNews: async (id: string, p: AlumniNewsPayload) => {
		try { const r = await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/alumni-news/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(p) }); return r.ok ? { ok: true, data: await r.json() } : { ok: false, message: await r.text() }; } catch { return { ok: false, message: 'Network error' }; }
	},
	deleteAlumniNews: async (id: string): Promise<void> => {
		await authFetch(`${PUBLIC_API_BASE_URL}/api/admin/alumni-news/${id}`, { method: 'DELETE' });
	}
};

// ── Shared types ──────────────────────────────────────────────────────────────
export interface EventItem { id: string; title: string; description: string; eventDate: string; category: string; location: string; }
export interface EventPayload { title: string; description: string; eventDate: string; category: string; location: string; }

export interface CompetitionItem { id: string; title: string; description: string; deadline: string; status: string; prize: string; ctaLabel: string; ctaUrl: string; }
export interface CompetitionPayload { title: string; description: string; deadline: string; status: string; prize: string; ctaLabel: string; ctaUrl: string; }

export interface AlumniNewsItem { id: string; personName: string; headline: string; body: string; newsDate: string; }
export interface AlumniNewsPayload { personName: string; headline: string; body: string; newsDate: string; }

// ── Grant types (shared between admin UI and grants page) ──────────────
export interface GrantItem {
	id: string;
	funder: string;
	title: string;
	description: string;
	amount: string;
	closingDate: string; // ISO date string "YYYY-MM-DD"
	status: 'OPEN' | 'UPCOMING' | 'CLOSED' | 'RUNNING';
	category: string;
	applyUrl: string;
	featured: boolean;
}

export interface GrantPayload {
	funder: string;
	title: string;
	description: string;
	amount: string;
	closingDate: string;
	status: string;
	category: string;
	applyUrl: string;
	featured: boolean;
}
