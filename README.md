# Innovation-Research-Team-Experts-db
University Expert Database Management System (UEDMS)

## Docker deployment

The app is wired to run with one Docker Compose command. Compose starts three containers:

- `postgres` for the database and persistent volume
- `backend` for the Spring Boot API
- `frontend` for the SvelteKit web app and `/api/...` proxy

Only the frontend is exposed publicly by default. The browser calls the frontend on port `3000`; SvelteKit forwards `/api/...` requests to the backend over Docker's internal network.

### First run

1. Start Docker Desktop or your Docker daemon.
2. Create a local environment file:

```sh
cp .env.example .env
```

3. Edit `.env` and replace the database password and JWT secret. A good JWT secret can be generated with:

```sh
openssl rand -base64 32
```

4. Build and start the stack:

```sh
docker compose up --build
```

5. Open the app at `http://localhost:3000`.

### Useful commands

```sh
docker compose up --build -d
docker compose logs -f backend
docker compose logs -f frontend
docker compose down
```

Data is stored in the `expertdb_data` Docker volume, so `docker compose down` does not delete the database. To remove the database volume intentionally, run `docker compose down -v`.

### Password reset emails

The forgot-password flow creates a 6-digit OTP that expires after 10 minutes by default. In local development, if SMTP is not configured, the backend logs the OTP so you can test the flow:

```sh
docker compose logs -f backend
```

To send real emails, set SMTP values in `.env`, for example:

```env
APP_PASSWORD_RESET_MAIL_FROM=no-reply@example.ac.zw
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=username@example.com
SPRING_MAIL_PASSWORD=change-this-mail-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

### Production notes

Set `PUBLIC_APP_ORIGIN` in `.env` to the public URL, for example `https://experts.example.ac.zw`.

The current defaults preserve the existing development schema behavior:

```env
SPRING_FLYWAY_ENABLED=false
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Before final production launch, verify the migrations against a copy of the database, then switch to:

```env
SPRING_FLYWAY_ENABLED=true
SPRING_JPA_HIBERNATE_DDL_AUTO=none
```
