import { browser } from '$app/environment';
import { SESSION_COOKIE_NAME, parseSession, serializeSession } from '$lib/auth/session';
import type { ExpertProfile, SessionUser, UserAccount, UserRole } from '$lib/auth/types';

const USERS_STORAGE_KEY = 'uz_users';

const createSeedAdmin = (): UserAccount => ({
	id: crypto.randomUUID(),
	email: 'admin@uz.ac.zw',
	password: 'Admin@123',
	role: 'ADMIN',
	status: 'ACTIVE',
	profile: {
		titlePrefix: 'Prof',
		fullName: 'System Administrator',
		contactDetails: 'University of Zimbabwe Admin Office',
		academicRank: 'Professor',
		universityEmail: 'admin@uz.ac.zw',
		phoneNumber: '+263000000000',
		highestQualification: 'PhD',
		professionalMemberships: 'N/A',
		complianceAccreditation: 'N/A',
		faculty: 'ICT',
		department: 'ICT',
		yearsOfConsultancyExperience: '10+ years',
		consultancyExperience: 'Platform governance and approvals',
		consultancyAvailability: 'Full-time',
		preferredConsultancyTypes: ['Advisory'],
		geographicScope: 'Local',
		skillsAndCompetences: ['Leadership'],
		languagesSpoken: ['English'],
		areasOfExpertise: ['Public Policy'],
		industrialAreasOfExpertise: ['Education'],
		notes: 'Seed admin account',
		profilePhotoDataUrl: ''
	},
	createdAt: new Date().toISOString(),
	updatedAt: new Date().toISOString()
});

const readUsers = (): UserAccount[] => {
	if (!browser) {
		return [];
	}

	const raw = localStorage.getItem(USERS_STORAGE_KEY);
	if (!raw) {
		const seed = [createSeedAdmin()];
		localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(seed));
		return seed;
	}

	try {
		return JSON.parse(raw) as UserAccount[];
	} catch {
		const seed = [createSeedAdmin()];
		localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(seed));
		return seed;
	}
};

const writeUsers = (users: UserAccount[]) => {
	if (!browser) {
		return;
	}
	localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(users));
};

const toSession = (user: UserAccount): SessionUser => ({
	id: user.id,
	email: user.email,
	role: user.role,
	status: user.status,
	fullName: user.profile.fullName
});

const setSessionCookie = (session: SessionUser | null) => {
	if (!browser) {
		return;
	}

	if (!session) {
		document.cookie = `${SESSION_COOKIE_NAME}=; Path=/; Max-Age=0; SameSite=Lax`;
		return;
	}

	document.cookie = `${SESSION_COOKIE_NAME}=${serializeSession(session)}; Path=/; Max-Age=2592000; SameSite=Lax`;
};

export const authService = {
	getSessionFromCookie: (): SessionUser | null => {
		if (!browser) {
			return null;
		}
		const parts = document.cookie.split(';').map((p) => p.trim());
		const match = parts.find((part) => part.startsWith(`${SESSION_COOKIE_NAME}=`));
		return parseSession(match?.split('=').slice(1).join('='));
	},

	signup: (payload: { email: string; password: string; profile: ExpertProfile }): { ok: boolean; message: string } => {
		if (!browser) {
			return { ok: false, message: 'Signup is only available in browser mode.' };
		}

		const users = readUsers();
		if (users.some((user) => user.email.toLowerCase() === payload.email.toLowerCase())) {
			return { ok: false, message: 'An account with this email already exists.' };
		}

		users.push({
			id: crypto.randomUUID(),
			email: payload.email,
			password: payload.password,
			role: 'USER',
			status: 'PENDING',
			profile: payload.profile,
			createdAt: new Date().toISOString(),
			updatedAt: new Date().toISOString()
		});

		writeUsers(users);
		return { ok: true, message: 'Signup successful. Your profile is pending admin approval.' };
	},

	login: (email: string, password: string): { ok: boolean; message: string; session?: SessionUser } => {
		if (!browser) {
			return { ok: false, message: 'Login is only available in browser mode.' };
		}

		const users = readUsers();
		const user = users.find((item) => item.email.toLowerCase() === email.toLowerCase());
		if (!user || user.password !== password) {
			return { ok: false, message: 'Invalid email or password.' };
		}

		if (user.status === 'PENDING') {
			return { ok: false, message: 'Your account is pending approval.' };
		}

		if (user.status === 'DISABLED') {
			return { ok: false, message: 'Your account is disabled. Contact an administrator.' };
		}

		const session = toSession(user);
		setSessionCookie(session);
		return { ok: true, message: 'Login successful', session };
	},

	logout: () => {
		setSessionCookie(null);
	},

	listUsers: (): UserAccount[] => readUsers(),

	approveUser: (id: string, comment: string) => {
		const users = readUsers();
		const next = users.map((user) =>
			user.id === id
				? {
						...user,
						status: 'APPROVED' as const,
						approvalComment: comment,
						updatedAt: new Date().toISOString()
					}
				: user
		);
		writeUsers(next);
	},

	rejectUser: (id: string, comment: string) => {
		const users = readUsers();
		const next = users.map((user) =>
			user.id === id
				? {
						...user,
						status: 'DISABLED' as const,
						approvalComment: comment,
						updatedAt: new Date().toISOString()
					}
				: user
		);
		writeUsers(next);
	},

	updateRole: (id: string, role: UserRole) => {
		const users = readUsers();
		const next = users.map((user) =>
			user.id === id ? { ...user, role, updatedAt: new Date().toISOString() } : user
		);
		writeUsers(next);
	}
};
