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
					profilePhotoDataUrl: p.profilePhotoDataUrl
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

	listUsers: async (): Promise<{ users: import('$lib/auth/types').AdminUser[]; error?: string }> => {
		const token = browser ? localStorage.getItem(JWT_STORAGE_KEY) : null;
		try {
			const res = await fetch(`${PUBLIC_API_BASE_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${token}` }
			});
			if (!res.ok) {
				const text = await res.text();
				return { users: [], error: `Server error ${res.status}: ${text || res.statusText}` };
			}
			return { users: await res.json() };
		} catch (e) {
			return { users: [], error: 'Could not reach the server. Is the backend running?' };
		}
	},

	approveUser: async (id: string): Promise<void> => {
		const token = browser ? localStorage.getItem(JWT_STORAGE_KEY) : null;
		await fetch(`${PUBLIC_API_BASE_URL}/api/admin/users/${id}/approve`, {
			method: 'PUT',
			headers: { Authorization: `Bearer ${token}` }
		});
	},

	rejectUser: async (id: string): Promise<void> => {
		const token = browser ? localStorage.getItem(JWT_STORAGE_KEY) : null;
		await fetch(`${PUBLIC_API_BASE_URL}/api/admin/users/${id}/reject`, {
			method: 'PUT',
			headers: { Authorization: `Bearer ${token}` }
		});
	},

	updateRole: async (id: string, role: import('$lib/auth/types').UserRole): Promise<void> => {
		const token = browser ? localStorage.getItem(JWT_STORAGE_KEY) : null;
		await fetch(`${PUBLIC_API_BASE_URL}/api/admin/users/${id}/role`, {
			method: 'PUT',
			headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
			body: JSON.stringify({ role })
		});
	}
};
