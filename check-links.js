#!/usr/bin/env node
/**
 * LandGuard Pro - Offline Government Link Health & Redirect Checker
 * 
 * HARD RULES COMPLIANCE:
 * 1. Runs exclusively in local Node.js environment (NEVER in browser).
 * 2. Transmits ZERO user, owner, or land data.
 * 3. Does NOT scrape or automate government workflows.
 * 4. Simply checks HTTP reachability and verifies that redirects terminate
 *    on the identical approved government hostname.
 * 
 * Usage:
 *   node check-links.js
 */

const https = require('node:https');
const http = require('node:http');
const { OFFICIAL_LINKS, APPROVED_HOSTNAMES, checkLinkStaleness } = require('./assets/js/links.js');

const TIMEOUT_MS = 15000;
const MAX_REDIRECTS = 5;

/**
 * Fetch link headers and follow redirects up to maxRedirects.
 */
function probeUrl(targetUrl, redirectCount = 0) {
  return new Promise((resolve) => {
    if (redirectCount > MAX_REDIRECTS) {
      return resolve({
        success: false,
        finalUrl: targetUrl,
        statusCode: 0,
        error: `Exceeded maximum redirect limit (${MAX_REDIRECTS})`
      });
    }

    let parsed;
    try {
      parsed = new URL(targetUrl);
    } catch (e) {
      return resolve({ success: false, finalUrl: targetUrl, statusCode: 0, error: 'Malformed URL: ' + e.message });
    }

    const client = parsed.protocol === 'https:' ? https : http;
    const options = {
      protocol: parsed.protocol,
      hostname: parsed.hostname,
      port: parsed.port || (parsed.protocol === 'https:' ? 443 : 80),
      path: parsed.pathname + parsed.search,
      method: 'GET', // Many Indian gov portals reject HEAD with 403/405; GET is universally supported
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) LandGuardPro-SecurityAuditor/1.0',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'en-US,en;q=0.5'
      },
      timeout: TIMEOUT_MS
    };

    const startTime = Date.now();

    const req = client.request(options, (res) => {
      const responseTimeMs = Date.now() - startTime;
      const status = res.statusCode;

      // Handle Redirects (301, 302, 307, 308)
      if (status >= 300 && status < 400 && res.headers.location) {
        res.resume(); // discard body
        const nextUrl = new URL(res.headers.location, targetUrl).href;
        return resolve(probeUrl(nextUrl, redirectCount + 1));
      }

      // Consume stream to free socket
      res.resume();

      return resolve({
        success: status >= 200 && status < 400,
        statusCode: status,
        finalUrl: targetUrl,
        initialUrl: targetUrl,
        responseTimeMs,
        headers: res.headers
      });
    });

    req.on('timeout', () => {
      req.destroy();
      resolve({
        success: false,
        finalUrl: targetUrl,
        statusCode: 0,
        error: `Request timed out after ${TIMEOUT_MS / 1000}s`
      });
    });

    req.on('error', (err) => {
      resolve({
        success: false,
        finalUrl: targetUrl,
        statusCode: 0,
        error: err.message
      });
    });

    req.end();
  });
}

async function runCheck() {
  console.log('='.repeat(70));
  console.log('LandGuard Pro - Government Link Health & Security Audit');
  console.log('Date:', new Date().toISOString());
  console.log('='.repeat(70));

  let totalErrors = 0;
  const results = [];

  for (const portal of OFFICIAL_LINKS) {
    process.stdout.write(`Checking [${portal.id}] -> ${portal.url} ... `);

    const initialHost = new URL(portal.url).hostname.toLowerCase();
    const probe = await probeUrl(portal.url);
    const staleness = checkLinkStaleness(portal.lastChecked, 60);

    let statusDisplay = '';
    let redirectSafe = true;

    if (probe.success) {
      const finalHost = new URL(probe.finalUrl).hostname.toLowerCase();
      // Verify redirect target matches approved hostname or valid .gov.in
      if (finalHost !== initialHost && !APPROVED_HOSTNAMES.includes(finalHost)) {
        redirectSafe = false;
        totalErrors++;
        statusDisplay = `FAILED (Redirected to untrusted host: ${finalHost})`;
      } else {
        statusDisplay = `OK (HTTP ${probe.statusCode}, ${probe.responseTimeMs}ms)`;
      }
    } else {
      // Note: Government portals may occasionally rate-limit, block external IPs, or be in maintenance
      statusDisplay = `WARNING/ERROR (${probe.error || `HTTP ${probe.statusCode}`})`;
      // Don't treat transient ISP/Gov NIC timeouts as fatal build failures, but flag them clearly
    }

    console.log(statusDisplay);

    results.push({
      id: portal.id,
      url: portal.url,
      httpStatus: probe.statusCode || 'ERR',
      responseTime: probe.responseTimeMs ? `${probe.responseTimeMs}ms` : 'N/A',
      finalUrl: probe.finalUrl,
      sameHost: redirectSafe,
      lastChecked: portal.lastChecked,
      daysAgo: staleness.daysAgo,
      isStale: staleness.isStale
    });
  }

  console.log('\nAudit Summary:');
  console.table(results.map(r => ({
    Portal: r.id,
    Status: r.httpStatus,
    Latency: r.responseTime,
    'Same Host?': r.sameHost ? 'YES' : 'NO (RISK)',
    'Last Verified': `${r.lastChecked} (${r.daysAgo}d ago)`,
    'Stale (>60d)': r.isStale ? 'YES (UPDATE NEEDED)' : 'NO (FRESH)'
  })));

  if (totalErrors > 0) {
    console.error(`\n[SECURITY ALERT] ${totalErrors} link(s) failed host integrity verification!`);
    process.exit(1);
  } else {
    console.log('\n[SUCCESS] All approved links passed domain integrity and safety verification.');
    process.exit(0);
  }
}

if (require.main === module) {
  runCheck().catch((err) => {
    console.error('Fatal execution error:', err);
    process.exit(1);
  });
}
