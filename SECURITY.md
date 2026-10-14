# Security Policy - LandGuard Pro

LandGuard Pro is a client-side static web application hosted on GitHub Pages (`https://sureekutty.github.io/landguard-pro/`). We take data privacy, outbound link integrity, and user security seriously.

---

## 1. Security Architecture & Principles

### A. Centralized & Tamper-Resistant Outbound Links
- All outbound government portal links are defined exclusively in [`assets/js/links.js`](assets/js/links.js) within a frozen, immutable structure (`Object.freeze`).
- No external links are hardcoded in HTML markup or dynamic templates.
- Only pre-approved official Tamil Nadu Government domains (`.gov.in`) are permitted:
  1. AnyTN e-Services (Patta / Chitta): `https://eservices.tn.gov.in/`
  2. TNREGINET (Encumbrance Search): `https://tnreginet.gov.in/`
  3. DTCP / OnlinePPA (Planning Permission & Regularisation): `https://onlineppa.tn.gov.in/`

### B. Link Allowlist Guard (`guard.js`)
Every link is validated at runtime before rendering. A link is rendered only if:
- Protocol is strictly `https:`.
- Hostname strictly ends with `.gov.in` and matches the approved allowlist. Subdomain suffix hijacks (e.g., `tnreginet.gov.in.evil.com`) and lookalike hyphenated domains (e.g., `tnreginet-gov.in`) are strictly rejected.
- URL contains **zero** query parameters (`search === ''`), **zero** fragments (`hash === ''`), and **zero** embedded credentials.
- Any link that fails this check is blocked from rendering, a visible `"Link blocked: failed safety check"` message is displayed, and an error is logged to the console.

### C. Safe DOM Rendering & Privacy Guarantee
- All links are constructed using pure DOM APIs (`document.createElement`, `setAttribute`, `appendChild`). No string concatenation into `innerHTML` is used for external URLs.
- Outbound anchors enforce `target="_blank"` and `rel="noopener noreferrer"`.
- Every link is accompanied by a departure notice:
  > *"You are leaving LandGuard. Enter your details on the official site only."*
- **Zero Outbound Data Transmission**: LandGuard Pro **never** attaches survey numbers, owner names, taluk, village, or user input to outbound URLs. Links open portal homepages only. Users copy their details field-by-field via the integrated **Quick Copy Official Details Helper** and paste them into official government forms.

### D. Link Staleness Monitoring
- Every configured link tracks a `lastChecked` date (YYYY-MM-DD).
- Links older than 60 days automatically display a warning badge in the UI: `Review >60d ago`.
- An offline CLI health audit script is provided at [`check-links.js`](check-links.js). It probes link reachability and verifies redirect targets remain on approved `.gov.in` hostnames without sending user data.

### E. Page Hardening & Strict Content Security Policy (CSP)
- A strict CSP meta tag is enforced on every page:
  ```http
  Content-Security-Policy: default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; base-uri 'none'; form-action 'none';
  ```
- Referrer policy is set to `no-referrer`.
- Zero external CDNs: Bootstrap 5.3.3 and Bootstrap Icons are vendored locally in `assets/`.
- Zero inline `<script>`, zero inline `<style>`, and zero inline event handlers (`onclick`, `onchange`, etc.). All event listeners are programmatically attached in `assets/js/app.js`.

---

## 2. Hard Security Rules

1. **No Scraping / Automation**: The application does not scrape, call, or automate government portals.
2. **Zero Third-Party Trackers**: No analytics, telemetry, tracking pixels, or third-party advertising scripts.
3. **No False Claims**: Procedural reference samples are never labeled as "official" or "verified" unless verified by an actual authoritative check. An explicit due diligence advisory is displayed on the platform.

---

## 3. Reporting a Vulnerability

If you discover any security issue, potential open redirect, lookalike domain vulnerability, or CSP bypass in LandGuard Pro:
1. Please open a private security advisory on the GitHub repository: **Security > Report a vulnerability**.
2. Alternatively, contact the repository maintainer directly via GitHub profile.
3. Please include reproduction steps, sample payload, and expected vs actual behavior.
4. We aim to acknowledge reports within 24 hours and patch issues promptly.
