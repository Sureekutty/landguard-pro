# LandGuard Pro Sureendhar 🏛️
## Tamil Nadu Universal Land Parcel Due Diligence & Multi-Portal Verification Platform

**Live Web Portal:** [https://sureekutty.github.io/landguard-pro/](https://sureekutty.github.io/landguard-pro/)  
**Created By:** Sureendhar J

---

### Security & Authentication
- **Authentication**: Spring Security 6 with stateless JWT Bearer tokens and BCrypt(12) password hashing.
- **Role-Based Access Control**: `ROLE_USER` for due-diligence searches and dossiers; `ROLE_ADMIN` for source registry management.
- **Zero Plaintext Credentials**: All credentials configured via `.env` / environment variables. No secrets committed to Git or stored in client-side bundles.

---

## Security Architecture & Link Safety Hardening

LandGuard Pro incorporates strict client-side security controls:

1. **Central Link Configuration (`assets/js/links.js`)**: All outbound government portal links are defined in a single, deep-frozen module. No external URLs are hardcoded in HTML or UI scripts.
2. **Runtime Link Allowlist Guard (`assets/js/guard.js`)**: Enforces `https://`, `.gov.in` domain allowlisting, and rejects lookalikes (`tnreginet.gov.in.evil.com`, `tnreginet-gov.in`), query parameters, URL fragments, and embedded credentials.
3. **Safe DOM Rendering**: Constructed via DOM APIs with `rel="noopener noreferrer" target="_blank"` and departure notice.
4. **Zero Personal / Land Data Transmission**: Survey numbers and owner names are never appended to outbound URLs. Links only open official portal homepages. Users use the integrated **Quick Copy Official Details Helper** with single-click clipboard buttons to paste their data directly into official government forms.
5. **Staleness Tracking**: 60-day visual warning badges and an offline verification tool [`check-links.js`](check-links.js).
6. **Strict Content-Security-Policy (CSP)**: `default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; base-uri 'none'; form-action 'none';` with `no-referrer` and zero external CDNs.

---

## Local Verification & Testing

### 1. Run Automated Unit Test Suites
```bash
# Run all tests (Guard validation & CSP compliance)
node --test

# Run Link Allowlist Guard tests specifically
node --test test/guard.test.js

# Run CSP & Page Hardening tests specifically
node --test test/csp-compliance.test.js
```

### 2. Run Offline Link Health & Redirect Audit
```bash
# Audits official government portals for HTTP status & redirect integrity (no user data sent)
node check-links.js
```

---

## Repository Security Checklist (Manual GitHub Settings)

To ensure full repository security on GitHub, complete the following steps in repository settings:

- [ ] **Enable 2-Factor Authentication (2FA)** on the GitHub account:
  - Go to **GitHub Settings > Password and authentication > Two-factor authentication** and enable authenticator app / SMS.
- [ ] **Enable Branch Protection on `main`**:
  - Go to **Repo Settings > Branches > Add branch protection rule**.
  - Branch name pattern: `main`.
  - Check *Require a pull request before merging*.
  - Check *Require status checks to pass before merging*.
  - Check *Do not allow bypassing the above settings*.
- [ ] **Turn on Secret Scanning**:
  - Go to **Repo Settings > Code security and analysis**.
  - Enable *Secret scanning* and *Push protection*.
- [ ] **Turn on Dependabot**:
  - Go to **Repo Settings > Code security and analysis**.
  - Enable *Dependabot alerts* and *Dependabot security updates*.
