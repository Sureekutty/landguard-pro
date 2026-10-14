# LandGuard Pro — Production Deployment & Cloud Architecture Guide 🚀

LandGuard Pro is an enterprise real-estate due diligence platform engineered for Tamil Nadu, India.
This guide details end-to-end deployment for both the Java Spring Boot REST backend and the GitHub Pages static frontend.

---

## Architecture Overview

```
                      +---------------------------------------+
                      |       GitHub Pages Static Web         |
                      |  https://sureekutty.github.io/       |
                      |           landguard-pro/             |
                      +-------------------+-------------------+
                                          |
                        HTTPS REST Calls  | JWT Bearer Auth
                        (Configurable)    | Content-Security-Policy
                                          v
                      +---------------------------------------+
                      |      LandGuard Pro Spring Boot        |
                      |   Backend Service (Port 8080)        |
                      |   - Spring Security + BCrypt(12)      |
                      |   - Government SSRF Validator         |
                      |   - LandRecordProvider Registry       |
                      |   - Property Matching Engine          |
                      +-------------------+-------------------+
                                          |
                                          | JDBC (SSL / Pool)
                                          v
                      +---------------------------------------+
                      |         PostgreSQL 16 Database        |
                      |   (Flyway Automated Schema V1)        |
                      |   - Users, Roles, Audit Logs          |
                      |   - Source Registry, Dossiers         |
                      +---------------------------------------+
```

---

## 1. Local Development & Docker Compose (Zero Configuration)

You can launch the entire stack (PostgreSQL 16 + Spring Boot Backend) locally using Docker:

```bash
# Clone the repository
git clone https://github.com/Sureekutty/landguard-pro.git
cd landguard-pro

# Copy environment variables
cp .env.example .env

# Start database and backend
docker compose up --build -d

# Check service health
docker compose ps
curl http://localhost:8080/actuator/health
```

The database will be initialized automatically with Flyway migrations.

---

## 2. Standalone Backend Execution (Without Docker)

The backend includes an in-memory H2 database fallback mode for instant local testing without needing PostgreSQL installed:

```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/landguard-backend-2.0.0.jar
```

Server starts on `http://localhost:8080`.
The system will automatically initialize the pre-seeded Tamil Nadu official source registries and bootstrap the admin account configured in `application.properties`.

---

## 3. Cloud Deployment (Render / Railway / AWS ECS)

### Step 3.1: Provision Managed PostgreSQL (Supabase or Neon)
1. Sign up on [Supabase](https://supabase.com) or [Neon](https://neon.tech).
2. Create a new PostgreSQL database instance in the **Asia South 1 (Mumbai)** region for lowest latency to Tamil Nadu users.
3. Copy the Connection Pooling or Direct JDBC URL, for example:
   ```
   jdbc:postgresql://ep-silent-dew-123456.ap-southeast-1.aws.neon.tech/neondb?sslmode=require
   ```

### Step 3.2: Deploy Backend to Render / Railway
1. **New Web Service**: Connect your GitHub repository `Sureendhar/landguard-pro`.
2. **Root Directory**: `backend` (or use the root `Dockerfile`).
3. **Runtime**: Docker or Java 21.
4. **Environment Variables**:
   | Variable | Value | Description |
   | :--- | :--- | :--- |
   | `PORT` | `8080` (or `$PORT`) | Web service port |
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:5432/<dbname>?sslmode=require` | PostgreSQL JDBC connection string |
   | `SPRING_DATASOURCE_USERNAME` | `<db_user>` | Database user |
   | `SPRING_DATASOURCE_PASSWORD` | `<db_password>` | Database password |
   | `JWT_SECRET` | 64+ char random hex string | Cryptographic key for JWT tokens |
   | `ADMIN_BOOTSTRAP_USERNAME` | `admin` | Initial admin account |
   | `ADMIN_BOOTSTRAP_PASSWORD` | Strong password | Initial admin password |
   | `CORS_ALLOWED_ORIGINS` | `https://sureekutty.github.io` | Allowed frontend origins |
5. Click **Deploy**. Flyway will migrate the database schema automatically upon startup.

---

## 4. Frontend Configuration & GitHub Pages

The static frontend is hosted on GitHub Pages:
[https://sureekutty.github.io/landguard-pro/](https://sureekutty.github.io/landguard-pro/)

### Connecting Frontend to Your Live Backend API:
In `assets/js/app.js`, the default API base URL is resolved as follows:
```javascript
const API_BASE_URL = window.LANDGUARD_API_URL || 
    (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
        ? 'http://localhost:8080/api/v1' 
        : 'https://landguard-backend.onrender.com/api/v1'); // Replace with your production backend URL
```

When hosted on GitHub Pages, the frontend makes authenticated requests to the production backend using standard JWT Bearer tokens. If the backend is temporarily offline, the frontend provides clear status indicators ("Source unavailable; verification incomplete") without fabricating fake results.

---

## 5. Security Checklist Before Going Live

- [ ] Ensure all default passwords in `.env.example` are changed to cryptographically strong values in your production `.env`.
- [ ] Confirm `JWT_SECRET` is at least 256 bits (64 hex characters) and kept private.
- [ ] Verify that PostgreSQL connections require SSL (`sslmode=require`).
- [ ] Confirm CORS only permits your authorized domain (`https://sureekutty.github.io`).
- [ ] Ensure server rate limiting is active (10 failed login attempts per IP per minute).
- [ ] Ensure SSRF protection is active: all external provider fetches are strictly checked against `.gov.in` allowlists and private/loopback/cloud metadata IP addresses are rejected.
