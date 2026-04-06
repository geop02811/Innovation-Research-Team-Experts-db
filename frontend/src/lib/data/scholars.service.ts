import { SCHOLARS_MOCK } from '$lib/data/scholars.mock';
import type { Scholar } from '$lib/types/scholar';

export interface ScholarsDataSource {
	listScholars(): Promise<Scholar[]>;
	getScholarBySlug(slug: string): Promise<Scholar | null>;
}

class MockScholarsDataSource implements ScholarsDataSource {
	async listScholars(): Promise<Scholar[]> {
		return SCHOLARS_MOCK;
	}

	async getScholarBySlug(slug: string): Promise<Scholar | null> {
		return SCHOLARS_MOCK.find((scholar) => scholar.slug === slug) ?? null;
	}
}

// Swap this implementation with an API-backed data source once backend endpoints are ready.
const dataSource: ScholarsDataSource = new MockScholarsDataSource();

export const scholarsService = {
	list: () => dataSource.listScholars(),
	getBySlug: (slug: string) => dataSource.getScholarBySlug(slug)
};
