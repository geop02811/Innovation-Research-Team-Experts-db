# syntax=docker/dockerfile:1
FROM node:24-bookworm-slim AS deps

WORKDIR /app

COPY package.json package-lock.json ./
# Cache NPM packages across builds
RUN --mount=type=cache,target=/root/.npm npm ci

FROM deps AS build

COPY . .
RUN npm run build
RUN npm prune --omit=dev

FROM node:24-bookworm-slim AS runtime

WORKDIR /app

ENV NODE_ENV=production \
    HOST=0.0.0.0 \
    PORT=3000

COPY --from=build /app/build ./build
COPY --from=build /app/package.json ./package.json
COPY --from=build /app/node_modules ./node_modules

EXPOSE 3000
CMD ["node", "build"]