/**
 * LandGuard Pro - Central Government Link Configuration
 * 
 * HARD SECURITY RULES:
 * 1. Only approved official government portals are defined here.
 * 2. Every URL points ONLY to the main portal root (no query strings, fragments, or deep paths).
 * 3. Never append survey numbers, names, or any user-entered data to these URLs.
 * 4. All configurations are deep-frozen to prevent runtime tampering.
 */

(function (root, factory) {
  if (typeof module === 'object' && module.exports) {
    // Node.js environment
    module.exports = factory();
  } else {
    // Browser environment
    root.LandGuardLinks = factory();
  }
})(typeof globalThis !== 'undefined' ? globalThis : this, function () {
  'use strict';

  /**
   * Strictly approved official Tamil Nadu Government portals.
   * Format: YYYY-MM-DD for lastChecked.
   */
  const OFFICIAL_LINKS = [
    {
      id: 'anytn-eservices',
      label: {
        en: 'AnyTN e-Services (Patta / Chitta)',
        ta: 'எனி டி.என் இ-சேவைகள் (பட்டா / சிட்டா)'
      },
      url: 'https://eservices.tn.gov.in/',
      purpose: 'Official Tamil Nadu Government portal to verify Land Records, Patta, Chitta, FMB sketch, and TSLR extract.',
      lastChecked: '2026-10-14'
    },
    {
      id: 'tnreginet-ec',
      label: {
        en: 'TNREGINET (Encumbrance Certificate Search)',
        ta: 'டி.என்.ரெஜிநெட் (வில்லங்கச் சான்றிதழ்)'
      },
      url: 'https://tnreginet.gov.in/',
      purpose: 'Official Inspector General of Registration (IGR) portal to search Encumbrance Certificate (EC), guideline values, and registered documents.',
      lastChecked: '2026-10-14'
    },
    {
      id: 'dtcp-onlineppa',
      label: {
        en: 'DTCP / OnlinePPA (Layout Regularisation & Planning)',
        ta: 'டி.டி.சி.பி / ஆன்லைன் பி.பி.ஏ (மனை அங்கீகாரம்)'
      },
      url: 'https://onlineppa.tn.gov.in/',
      purpose: 'Official Directorate of Town and Country Planning portal to verify approved layouts, plot regularisation, and planning permissions.',
      lastChecked: '2026-10-14'
    }
  ];

  // Deep freeze the configuration so no malicious runtime script can modify links
  OFFICIAL_LINKS.forEach(item => {
    Object.freeze(item.label);
    Object.freeze(item);
  });
  Object.freeze(OFFICIAL_LINKS);

  /**
   * Approved hostnames allowlist.
   */
  const APPROVED_HOSTNAMES = Object.freeze([
    'eservices.tn.gov.in',
    'tnreginet.gov.in',
    'onlineppa.tn.gov.in'
  ]);

  /**
   * Retrieve all configured official links.
   * @returns {readonly object[]}
   */
  function getAllLinks() {
    return OFFICIAL_LINKS;
  }

  /**
   * Find a link configuration by its unique ID.
   * @param {string} id
   * @returns {object|undefined}
   */
  function getLinkById(id) {
    return OFFICIAL_LINKS.find(link => link.id === id);
  }

  /**
   * Check if a link verification is older than the allowed maximum days.
   * @param {object|string} linkOrDate - link object or YYYY-MM-DD date string
   * @param {number} maxDays - threshold days (default 60)
   * @returns {{ isStale: boolean, daysAgo: number }}
   */
  function checkLinkStaleness(linkOrDate, maxDays = 60) {
    const dateStr = typeof linkOrDate === 'string' ? linkOrDate : (linkOrDate && linkOrDate.lastChecked);
    if (!dateStr) {
      return { isStale: true, daysAgo: Infinity };
    }
    const checkedDate = new Date(dateStr + 'T00:00:00Z');
    const now = new Date();
    const diffMs = now.getTime() - checkedDate.getTime();
    const daysAgo = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    return {
      isStale: daysAgo > maxDays,
      daysAgo: daysAgo >= 0 ? daysAgo : 0
    };
  }

  return {
    OFFICIAL_LINKS,
    APPROVED_HOSTNAMES,
    getAllLinks,
    getLinkById,
    checkLinkStaleness
  };
});
