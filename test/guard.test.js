/**
 * Unit Test Suite for LandGuard Pro Link Allowlist Guard
 * Run with: node --test test/guard.test.js
 */

const { describe, it } = require('node:test');
const assert = require('node:assert/strict');
const { validateGovernmentUrl, APPROVED_HOSTNAMES } = require('../assets/js/guard.js');
const { OFFICIAL_LINKS } = require('../assets/js/links.js');

describe('Task 2: Link Allowlist Guard Validation Tests', () => {

  describe('Approved .gov.in Portals (Must Pass)', () => {
    it('should validate all configured official portal URLs', () => {
      OFFICIAL_LINKS.forEach(portal => {
        const result = validateGovernmentUrl(portal.url);
        assert.equal(result.valid, true, `Expected ${portal.id} (${portal.url}) to pass validation`);
        assert.equal(result.reason, null);
        assert.ok(result.sanitizedUrl.startsWith('https://'));
      });
    });

    it('should pass AnyTN e-Services root URL', () => {
      const result = validateGovernmentUrl('https://eservices.tn.gov.in/');
      assert.equal(result.valid, true);
      assert.equal(result.hostname, 'eservices.tn.gov.in');
    });

    it('should pass TNREGINET root URL', () => {
      const result = validateGovernmentUrl('https://tnreginet.gov.in/');
      assert.equal(result.valid, true);
      assert.equal(result.hostname, 'tnreginet.gov.in');
    });

    it('should pass DTCP OnlinePPA root URL', () => {
      const result = validateGovernmentUrl('https://onlineppa.tn.gov.in/');
      assert.equal(result.valid, true);
      assert.equal(result.hostname, 'onlineppa.tn.gov.in');
    });
  });

  describe('Insecure HTTP Protocols (Must Reject)', () => {
    it('should reject http:// protocol for eservices', () => {
      const result = validateGovernmentUrl('http://eservices.tn.gov.in/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /Insecure protocol/i);
    });

    it('should reject http:// protocol for tnreginet', () => {
      const result = validateGovernmentUrl('http://tnreginet.gov.in/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /Insecure protocol/i);
    });
  });

  describe('Lookalike & Malicious Domains (Must Reject)', () => {
    it('should reject subdomain suffix hijack (tnreginet.gov.in.evil.com)', () => {
      const result = validateGovernmentUrl('https://tnreginet.gov.in.evil.com/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /does not end with '\.gov\.in'|strict TLD validation/i);
    });

    it('should reject hyphen lookalike domain (tnreginet-gov.in)', () => {
      const result = validateGovernmentUrl('https://tnreginet-gov.in/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /does not end with '\.gov\.in'/i);
    });

    it('should reject domain with evil prefix (evil-eservices.tn.gov.in)', () => {
      const result = validateGovernmentUrl('https://evil-eservices.tn.gov.in/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /not in the approved government portal allowlist/i);
    });

    it('should reject random .com or .org domains pretending to be official', () => {
      const result = validateGovernmentUrl('https://tnreginet.com/');
      assert.equal(result.valid, false);
    });
  });

  describe('JavaScript & Pseudo Protocols (Must Reject)', () => {
    it('should reject javascript: pseudo-protocol', () => {
      const result = validateGovernmentUrl('javascript:alert(1)');
      assert.equal(result.valid, false);
    });

    it('should reject data: URIs', () => {
      const result = validateGovernmentUrl('data:text/html,<script>alert(1)</script>');
      assert.equal(result.valid, false);
    });

    it('should reject file: protocol', () => {
      const result = validateGovernmentUrl('file:///etc/passwd');
      assert.equal(result.valid, false);
    });
  });

  describe('Query Strings & Parameter Pollution (Must Reject)', () => {
    it('should reject URLs containing query strings (?survey=123/4B)', () => {
      const result = validateGovernmentUrl('https://eservices.tn.gov.in/?survey=123&owner=Kumar');
      assert.equal(result.valid, false);
      assert.match(result.reason, /query parameters/i);
    });

    it('should reject single query parameter', () => {
      const result = validateGovernmentUrl('https://tnreginet.gov.in/?test=1');
      assert.equal(result.valid, false);
      assert.match(result.reason, /query parameters/i);
    });
  });

  describe('Fragments & Credentials (Must Reject)', () => {
    it('should reject URLs containing URL fragments (#section)', () => {
      const result = validateGovernmentUrl('https://eservices.tn.gov.in/#chitta');
      assert.equal(result.valid, false);
      assert.match(result.reason, /fragment identifier/i);
    });

    it('should reject URLs containing embedded credentials (user:pass@)', () => {
      const result = validateGovernmentUrl('https://admin:secret@eservices.tn.gov.in/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /embedded credentials/i);
    });

    it('should reject non-standard port numbers', () => {
      const result = validateGovernmentUrl('https://eservices.tn.gov.in:8080/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /port/i);
    });
  });

  describe('Unapproved Government Domains (Must Reject)', () => {
    it('should reject valid .gov.in domains that are not explicitly approved', () => {
      const result = validateGovernmentUrl('https://unapproved-portal.gov.in/');
      assert.equal(result.valid, false);
      assert.match(result.reason, /not in the approved government portal allowlist/i);
    });
  });

  describe('Input Robustness', () => {
    it('should gracefully handle empty or null input', () => {
      assert.equal(validateGovernmentUrl('').valid, false);
      assert.equal(validateGovernmentUrl(null).valid, false);
      assert.equal(validateGovernmentUrl(undefined).valid, false);
      assert.equal(validateGovernmentUrl(12345).valid, false);
    });
  });
});
