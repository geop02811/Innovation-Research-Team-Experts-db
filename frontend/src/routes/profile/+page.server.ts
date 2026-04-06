import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ parent }) => {
	// Get session from parent layout
	const { session } = await parent();

	// Check if user is authenticated
	if (!session) {
		redirect(302, '/login');
	}

	// Load user profile data from session or database
	// For now, return the session data which should contain the profile
	return {
		profile: session.profile || {}
	};
};
