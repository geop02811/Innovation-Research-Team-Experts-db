import { env } from '$env/dynamic/private';
import type { RequestHandler } from './$types';

const getBackendBaseUrl = () => env.PRIVATE_API_BASE_URL || 'http://localhost:8080';

const forwardRequest: RequestHandler = async ({ fetch, params, request, url }) => {
	const target = new URL(`/api/${params.path ?? ''}${url.search}`, getBackendBaseUrl());
	const headers = new Headers(request.headers);
	headers.delete('connection');
	headers.delete('content-length');
	headers.delete('host');
	headers.delete('origin');

	const hasBody = request.method !== 'GET' && request.method !== 'HEAD';
	const body = hasBody ? await request.arrayBuffer() : undefined;
	const response = await fetch(target, {
		method: request.method,
		headers,
		body
	});

	return new Response(response.body, {
		status: response.status,
		statusText: response.statusText,
		headers: response.headers
	});
};

export const GET = forwardRequest;
export const POST = forwardRequest;
export const PUT = forwardRequest;
export const PATCH = forwardRequest;
export const DELETE = forwardRequest;
export const OPTIONS = forwardRequest;