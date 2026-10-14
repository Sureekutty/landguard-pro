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
   * Render all approved official links into a target DOM container.
   * @param {HTMLElement|string} targetContainer - element or element id
   * @param {object} [options]
   */
  function renderPortalLinksContainer(targetContainer, options) {
    const el = typeof targetContainer === 'string' ? document.getElementById(targetContainer) : targetContainer;
    if (!el) return;
    el.innerHTML = ''; // safe clear before DOM API appends

    const links = (linksModule && linksModule.getAllLinks) ? linksModule.getAllLinks() : [];
    links.forEach(link => {
      const linkEl = renderSafeLink(link, options);
      el.appendChild(linkEl);
    });
  }

  /**
   * Renders the "Quick Copy Official Details" helper panel (Task 4).
   * 
   * NEVER appends personal or land parameters to outbound URLs.
   * Instead, displays fields with individual "Copy" buttons so users can
   * paste their verified parcel parameters into the official portals safely.
   * 
   * @param {HTMLElement|string} targetContainer
   * @param {object} parcelData - { district, taluk, village, surveyNumber, pattaNumber, ownerName }
   */
  function renderCopyHelper(targetContainer, parcelData) {
    const el = typeof targetContainer === 'string' ? document.getElementById(targetContainer) : targetContainer;
    if (!el) return;
    el.innerHTML = '';

    const data = parcelData || {};
    const fields = [
      { id: 'district', labelEn: 'District', labelTa: 'மாவட்டம்', value: data.district || 'Namakkal' },
      { id: 'taluk', labelEn: 'Taluk', labelTa: 'வட்டம்', value: data.taluk || 'Rasipuram' },
      { id: 'village', labelEn: 'Village', labelTa: 'கிராமம்', value: data.village || 'Gurusamipalayam' },
      { id: 'survey', labelEn: 'Survey / Sub-Division No.', labelTa: 'புல எண் / உட்பிரிவு', value: data.surveyNumber || data.subDivision || data.fullSurvey || '142/3B' },
      { id: 'patta', labelEn: 'Patta Number', labelTa: 'பட்டா எண்', value: data.pattaNumber ? String(data.pattaNumber) : '1042' },
      { id: 'owner', labelEn: 'Owner Name (as reported)', labelTa: 'உரிமையாளர் பெயர்', value: data.ownerName || 'Sureendhar' }
    ];

    const card = document.createElement('div');
    card.className = 'card border-primary-subtle shadow-sm mb-3';

    // Header
    const cardHeader = document.createElement('div');
    cardHeader.className = 'card-header bg-primary-subtle text-primary-emphasis d-flex align-items-center justify-content-between py-2';

    const headerTitle = document.createElement('div');
    headerTitle.className = 'fw-bold d-flex align-items-center';
    const icon = document.createElement('i');
    icon.className = 'bi bi-clipboard2-check me-2 fs-5 text-primary';
    headerTitle.appendChild(icon);
    headerTitle.appendChild(document.createTextNode('Official Portal Fill Helper (No Outbound Data Sent)'));
    cardHeader.appendChild(headerTitle);

    const badge = document.createElement('span');
    badge.className = 'badge bg-light text-dark border';
    badge.textContent = 'Privacy Guard Active';
    cardHeader.appendChild(badge);
    card.appendChild(cardHeader);

    // Body
    const cardBody = document.createElement('div');
    cardBody.className = 'card-body p-3';

    const infoP = document.createElement('p');
    infoP.className = 'small text-muted mb-3';
    infoP.textContent = 'To protect your privacy, outbound official links never carry your survey number, owner name, or location. Use the Copy buttons below to paste values directly into the official government forms.';
    cardBody.appendChild(infoP);

    const row = document.createElement('div');
    row.className = 'row g-2';

    fields.forEach(field => {
      const col = document.createElement('div');
      col.className = 'col-md-6 col-lg-4';

      const fieldBox = document.createElement('div');
      fieldBox.className = 'p-2 border rounded bg-light d-flex flex-column justify-content-between h-100';

      const labelDiv = document.createElement('div');
      labelDiv.className = 'small text-muted mb-1';
      labelDiv.textContent = `${field.labelEn} (${field.labelTa})`;
      fieldBox.appendChild(labelDiv);

      const valDiv = document.createElement('div');
      valDiv.className = 'fw-bold text-dark text-truncate mb-2';
      valDiv.textContent = field.value;
      fieldBox.appendChild(valDiv);

      const copyBtn = document.createElement('button');
      copyBtn.type = 'button';
      copyBtn.className = 'btn btn-outline-secondary btn-sm d-flex align-items-center justify-content-center gap-1 w-100';
      
      const copyIcon = document.createElement('i');
      copyIcon.className = 'bi bi-clipboard';
      copyBtn.appendChild(copyIcon);
      const btnText = document.createTextNode(' Copy');
      copyBtn.appendChild(btnText);

      // Clipboard copy handler with fallback
      copyBtn.addEventListener('click', () => {
        copyToClipboard(field.value, copyBtn, btnText, copyIcon);
      });

      fieldBox.appendChild(copyBtn);
      col.appendChild(fieldBox);
      row.appendChild(col);
    });

    cardBody.appendChild(row);
    card.appendChild(cardBody);
    el.appendChild(card);
  }

  function copyToClipboard(text, btnElement, btnTextNode, iconElement) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(showSuccess).catch(fallback);
    } else {
      fallback();
    }

    function showSuccess() {
      btnElement.className = 'btn btn-success btn-sm d-flex align-items-center justify-content-center gap-1 w-100';
      iconElement.className = 'bi bi-check-lg';
      btnTextNode.nodeValue = ' Copied!';
      setTimeout(() => {
        btnElement.className = 'btn btn-outline-secondary btn-sm d-flex align-items-center justify-content-center gap-1 w-100';
        iconElement.className = 'bi bi-clipboard';
        btnTextNode.nodeValue = ' Copy';
      }, 2000);
    }

    function fallback() {
      const tempInp = document.createElement('input');
      tempInp.value = text;
      document.body.appendChild(tempInp);
      tempInp.select();
      try {
        document.execCommand('copy');
        showSuccess();
      } catch (e) {
        btnTextNode.nodeValue = ' Error';
      }
      document.body.removeChild(tempInp);
    }
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
    renderPortalLinksContainer,
    renderCopyHelper,
    renderBlockedMessage,
    APPROVED_HOSTNAMES
  };
});
