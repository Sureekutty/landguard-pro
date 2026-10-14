/**
 * Task 6: CSP Compliance & Page Hardening Test Suite
 * Run with: node --test test/csp-compliance.test.js
 */

const { describe, it } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

describe('Task 6: Page Hardening & CSP Compliance', () => {
  const htmlPath = path.join(__dirname, '..', 'index.html');
  const html = fs.readFileSync(htmlPath, 'utf8');

  it('should declare strict Content-Security-Policy meta tag', () => {
    assert.match(
      html,
      /<meta\s+http-equiv="Content-Security-Policy"\s+content="default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; base-uri 'none'; form-action 'none';">/i,
      'Strict CSP meta tag is required'
    );
  });

  it('should declare referrer policy meta tag of no-referrer', () => {
    assert.match(
      html,
      /<meta\s+name="referrer"\s+content="no-referrer">/i,
      'Referrer policy of no-referrer is required'
    );
  });

  it('should contain ZERO inline <script> tags', () => {
    // Only scripts with src="..." are allowed
    const inlineScripts = html.match(/<script(?![^>]*src=)[^>]*>[\s\S]*?<\/script>/gi) || [];
    assert.equal(inlineScripts.length, 0, `Found inline script tags: ${inlineScripts.length}`);
  });

  it('should contain ZERO inline <style> tags', () => {
    const inlineStyles = html.match(/<style[^>]*>[\s\S]*?<\/style>/gi) || [];
    assert.equal(inlineStyles.length, 0, `Found inline style tags: ${inlineStyles.length}`);
  });

  it('should contain ZERO inline event handler attributes (onclick, onchange, etc.)', () => {
    const inlineHandlers = html.match(/\son(click|change|submit|input|keydown|keyup|load|error)=/gi) || [];
    assert.equal(inlineHandlers.length, 0, `Found inline event handler attributes: ${inlineHandlers.join(', ')}`);
  });

  it('should load only local scripts and styles (no external CDN URLs)', () => {
    // Match any script src or link href starting with http:// or https://
    const cdnScripts = html.match(/<script[^>]+src=["']https?:\/\/[^"']+["']/gi) || [];
    const cdnStyles = html.match(/<link[^>]+href=["']https?:\/\/[^"']+["']/gi) || [];
    assert.equal(cdnScripts.length, 0, `Found external CDN scripts: ${cdnScripts.join(', ')}`);
    assert.equal(cdnStyles.length, 0, `Found external CDN stylesheets: ${cdnStyles.join(', ')}`);
  });

  it('should bundle local Bootstrap and Icons files in assets directory', () => {
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'css', 'bootstrap.min.css')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'css', 'bootstrap-icons.min.css')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'js', 'bootstrap.bundle.min.js')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'img', 'tamilnadu-logo.svg')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'js', 'links.js')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'js', 'guard.js')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'js', 'app.js')));
    assert.ok(fs.existsSync(path.join(__dirname, '..', 'assets', 'css', 'app.css')));
  });
});
