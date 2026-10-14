/**
 * LandGuard Pro - Link Allowlist Guard & Safe Renderer
 * 
 * Enforces strict government domain validation and safe DOM rendering.
 * Prevents phishing, open redirects, URL tampering, and data exfiltration.
 */

(function (root, factory) {
  if (typeof module === 'object' && module.exports) {
    const linksModule = require('./links.js');
    module.exports = factory(linksModule);
  } else {
    root.LandGuardGuard = factory(root.LandGuardLinks);
  }
})(typeof globalThis !== 'undefined' ? globalThis : this, function (linksModule) {
  'use strict';

  const APPROVED_HOSTNAMES = (linksModule && linksModule.APPROVED_HOSTNAMES) || Object.freeze([
    'eservices.tn.gov.in',
    'tnreginet.gov.in',
    'onlineppa.tn.gov.in'
  ]);

  /**
   * Validates an external government URL against strict security rules.
   * 
   * Strict validation rules:
   * 1. Protocol MUST be 'https:'.
   * 2. Hostname MUST end with '.gov.in' and be an approved government domain.
   * 3. Lookalikes (e.g., tnreginet.gov.in.evil.com, tnreginet-gov.in) MUST be rejected.
   * 4. Query strings ('?foo=bar') MUST be rejected.
   * 5. URL fragments ('#section') MUST be rejected.
   * 6. Embedded credentials ('user:pass@') MUST be rejected.
   * 7. Non-standard ports MUST be rejected.
   * 
   * @param {string} urlString
   * @param {readonly string[]} [customAllowlist]
   * @returns {{ valid: boolean, reason: string | null, sanitizedUrl?: string, hostname?: string }}
   */
  function validateGovernmentUrl(urlString, customAllowlist) {
    if (typeof urlString !== 'string' || !urlString.trim()) {
      return { valid: false, reason: 'URL must be a non-empty string' };
    }

    let parsed;
    try {
      parsed = new URL(urlString.trim());
    } catch (err) {
      return { valid: false, reason: 'Malformed URL: ' + err.message };
    }

    // 1. Protocol check - strictly https:
    if (parsed.protocol !== 'https:') {
      return { valid: false, reason: `Insecure protocol '${parsed.protocol}'. Only 'https:' is permitted.` };
    }

    // 2. Credentials check - must not contain username or password
    if (parsed.username || parsed.password) {
      return { valid: false, reason: 'URL contains embedded credentials which is forbidden.' };
    }

    // 3. Query string check - outbound government links must never carry query parameters
    if (parsed.search && parsed.search !== '') {
      return { valid: false, reason: 'URL contains query parameters. Outbound links must point only to the portal root.' };
    }

    // 4. Fragment check - no hashes
    if (parsed.hash && parsed.hash !== '') {
      return { valid: false, reason: 'URL contains a fragment identifier which is forbidden.' };
    }

    // 5. Port check - must be default 443 or empty
    if (parsed.port && parsed.port !== '443') {
      return { valid: false, reason: `Non-standard port '${parsed.port}' is forbidden.` };
    }

    // 6. Hostname verification
    const hostname = parsed.hostname.toLowerCase();
    const allowlist = customAllowlist || APPROVED_HOSTNAMES;

    // Check lookalike patterns: must have dot before gov.in (e.g. .gov.in, not -gov.in)
    const isGovIn = hostname === 'gov.in' || hostname.endsWith('.gov.in');
    if (!isGovIn) {
      return { valid: false, reason: `Domain '${hostname}' does not end with '.gov.in'.` };
    }

    // Prevent subdomain suffix hijack (e.g., tnreginet.gov.in.evil.com)
    // If it ends with .gov.in, split by '.' and ensure the last two labels are exactly ['gov', 'in']
    const parts = hostname.split('.');
    if (parts.length < 3 || parts[parts.length - 2] !== 'gov' || parts[parts.length - 1] !== 'in') {
      return { valid: false, reason: `Domain '${hostname}' fails strict TLD validation.` };
    }

    // Check against approved hostnames allowlist
    const isApproved = allowlist.includes(hostname);
    if (!isApproved) {
      return { valid: false, reason: `Domain '${hostname}' is not in the approved government portal allowlist.` };
    }

    return {
      valid: true,
      reason: null,
      sanitizedUrl: parsed.origin + (parsed.pathname === '/' ? '/' : parsed.pathname),
      hostname: hostname
    };
  }

  /**
   * Safely renders an outbound official government link using pure DOM APIs.
   * 
   * NEVER concatenates user inputs or URLs into innerHTML.
   * 
   * @param {object} linkConfig - configuration object from links.js
   * @param {object} [options]
   * @param {string} [options.lang='en'] - 'en' or 'ta'
   * @param {string} [options.className='btn btn-gov btn-sm'] - CSS classes for button
   * @param {boolean} [options.showNotice=true] - whether to render the departure notice
   * @param {boolean} [options.showStaleness=true] - whether to render last checked & stale warning
   * @returns {HTMLElement} DOM element ready to insert
   */
  function renderSafeLink(linkConfig, options) {
    const opts = Object.assign({
      lang: 'en',
      className: 'btn btn-gov btn-sm',
      showNotice: true,
      showStaleness: true
    }, options);

    const container = document.createElement('div');
    container.className = 'safe-gov-link-container mb-2';

    if (!linkConfig || !linkConfig.url) {
      return renderBlockedMessage(container, 'No link configuration provided.');
    }

    // Execute validation guard
    const validation = validateGovernmentUrl(linkConfig.url);

    if (!validation.valid) {
      // Log failure to console as required by Task 2
      console.error('Link blocked: failed safety check', {
        id: linkConfig.id,
        url: linkConfig.url,
        reason: validation.reason
      });
      return renderBlockedMessage(container, validation.reason);
    }

    // 1. Create the anchor tag safely with DOM APIs
    const anchor = document.createElement('a');
    anchor.setAttribute('href', validation.sanitizedUrl);
    anchor.setAttribute('target', '_blank');
    anchor.setAttribute('rel', 'noopener noreferrer');
    anchor.className = opts.className;

    // Icon
    const icon = document.createElement('i');
    icon.className = 'bi bi-box-arrow-up-right me-1';
    anchor.appendChild(icon);

    // Label
    const labelText = (linkConfig.label && (linkConfig.label[opts.lang] || linkConfig.label.en)) || linkConfig.id;
    const textNode = document.createTextNode(' ' + labelText);
    anchor.appendChild(textNode);

    container.appendChild(anchor);

    // 2. Staleness indicator badge
    if (opts.showStaleness && linkConfig.lastChecked && linksModule && linksModule.checkLinkStaleness) {
      const staleness = linksModule.checkLinkStaleness(linkConfig.lastChecked, 60);
      const metaSpan = document.createElement('span');
      metaSpan.className = 'ms-2 small text-muted';
      metaSpan.textContent = `(Verified: ${linkConfig.lastChecked})`;
      container.appendChild(metaSpan);

      if (staleness.isStale) {
        const warningBadge = document.createElement('span');
        warningBadge.className = 'badge bg-warning text-dark ms-2';
        const warnIcon = document.createElement('i');
        warnIcon.className = 'bi bi-exclamation-triangle-fill me-1';
        warningBadge.appendChild(warnIcon);
        warningBadge.appendChild(document.createTextNode(`Review >60d ago (${staleness.daysAgo} days)`));
        container.appendChild(warningBadge);
      }
    }

    // 3. Departure warning note (Task 3 requirement)
    if (opts.showNotice) {
      const notice = document.createElement('div');
      notice.className = 'departure-notice text-muted small mt-1';
      const shieldIcon = document.createElement('i');
      shieldIcon.className = 'bi bi-shield-check text-success me-1';
      notice.appendChild(shieldIcon);

      const noticeText = opts.lang === 'ta'
        ? 'நீங்கள் லேண்ட்கார்டில் இருந்து வெளியேறுகிறீர்கள். விவரங்களை அதிகாரப்பூர்வ தளத்தில் மட்டுமே உள்ளிடவும்.'
        : 'You are leaving LandGuard. Enter your details on the official site only.';
      notice.appendChild(document.createTextNode(noticeText));
      container.appendChild(notice);
    }

    return container;
  }

  /**
   * Helper to construct a visible "Link blocked: failed safety check" DOM alert.
   */
  function renderBlockedMessage(container, reason) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger d-flex align-items-center p-2 mb-1';
    alertDiv.setAttribute('role', 'alert');

    const icon = document.createElement('i');
    icon.className = 'bi bi-shield-x text-danger fs-5 me-2';
    alertDiv.appendChild(icon);

    const contentDiv = document.createElement('div');
    const strong = document.createElement('strong');
    strong.textContent = 'Link blocked: failed safety check';
    contentDiv.appendChild(strong);

    if (reason) {
      const reasonDiv = document.createElement('div');
      reasonDiv.className = 'small text-muted';
      reasonDiv.textContent = reason;
      contentDiv.appendChild(reasonDiv);
    }

    alertDiv.appendChild(contentDiv);
    container.appendChild(alertDiv);
    return container;
  }

  return {
    validateGovernmentUrl,
    renderSafeLink,
    renderBlockedMessage,
    APPROVED_HOSTNAMES
  };
});
