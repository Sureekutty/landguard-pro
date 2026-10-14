/**
 * Comprehensive Real-Browser End-to-End Automated Test Suite
 * Tests LandGuard Pro using real headless Chrome & Chrome DevTools Protocol (CDP)
 * Zero external npm dependencies (uses native Node http, child_process, and WebSocket)
 */

const http = require('node:http');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { spawn } = require('node:child_process');
const assert = require('node:assert/strict');

const PORT = 4173;
const ROOT_DIR = path.resolve(__dirname, '..');
const CHROME_PATH = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.ico': 'image/x-icon'
};

// 1. Lightweight Static HTTP Server
function startStaticServer() {
  return new Promise((resolve, reject) => {
    const server = http.createServer((req, res) => {
      let reqPath = decodeURIComponent(req.url.split('?')[0]);
      if (reqPath === '/' || reqPath === '') reqPath = '/index.html';
      const filePath = path.join(ROOT_DIR, reqPath);

      // Security check: must stay within ROOT_DIR
      if (!filePath.startsWith(ROOT_DIR)) {
        res.writeHead(403);
        res.end('Forbidden');
        return;
      }

      fs.stat(filePath, (err, stats) => {
        if (err || !stats.isFile()) {
          res.writeHead(404, { 'Content-Type': 'text/plain' });
          res.end('File Not Found');
          return;
        }

        const ext = path.extname(filePath).toLowerCase();
        const mime = MIME_TYPES[ext] || 'application/octet-stream';
        res.writeHead(200, {
          'Content-Type': mime,
          'Content-Security-Policy': "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; base-uri 'none'; form-action 'none';"
        });
        fs.createReadStream(filePath).pipe(res);
      });
    });

    server.listen(PORT, '127.0.0.1', () => {
      resolve(server);
    });
    server.on('error', reject);
  });
}

// 2. Launch Chrome Headless with Remote Debugging
function launchChrome(debuggingPort) {
  const profileDir = path.join(os.tmpdir(), '.tmp-chrome-profile-' + Date.now() + '-' + Math.random().toString(36).slice(2));
  return new Promise((resolve, reject) => {
    const chrome = spawn(CHROME_PATH, [
      '--headless=new',
      `--remote-debugging-port=${debuggingPort}`,
      '--no-first-run',
      '--no-default-browser-check',
      '--disable-gpu',
      '--disable-extensions',
      '--disable-background-networking',
      '--user-data-dir=' + profileDir
    ]);

    chrome.on('error', reject);

    // Poll until Chrome remote debugging endpoint is ready
    let attempts = 0;
    const interval = setInterval(async () => {
      attempts++;
      try {
        const res = await fetch(`http://127.0.0.1:${debuggingPort}/json/version`);
        if (res.ok) {
          const data = await res.json();
          clearInterval(interval);
          resolve({ process: chrome, profileDir, webSocketDebuggerUrl: data.webSocketDebuggerUrl });
        }
      } catch (err) {
        if (attempts > 30) {
          clearInterval(interval);
          chrome.kill();
          reject(new Error('Timed out waiting for Chrome remote debugging endpoint'));
        }
      }
    }, 200);
  });
}

// 3. Simple CDP WebSocket Client
class CdpClient {
  constructor(wsUrl) {
    this.wsUrl = wsUrl;
    this.ws = null;
    this.id = 0;
    this.callbacks = new Map();
    this.events = new Map();
  }

  connect() {
    return new Promise((resolve, reject) => {
      this.ws = new WebSocket(this.wsUrl);
      this.ws.onopen = () => resolve();
      this.ws.onerror = reject;
      this.ws.onmessage = (msg) => {
        const data = JSON.parse(msg.data);
        if (data.id && this.callbacks.has(data.id)) {
          const { res, rej } = this.callbacks.get(data.id);
          this.callbacks.delete(data.id);
          if (data.error) rej(data.error);
          else res(data.result);
        } else if (data.method && this.events.has(data.method)) {
          const listeners = this.events.get(data.method);
          listeners.forEach(fn => fn(data.params));
        }
      };
    });
  }

  send(method, params = {}) {
    return new Promise((res, rej) => {
      const id = ++this.id;
      this.callbacks.set(id, { res, rej });
      this.ws.send(JSON.stringify({ id, method, params }));
    });
  }

  on(event, handler) {
    if (!this.events.has(event)) this.events.set(event, []);
    this.events.get(event).push(handler);
  }

  close() {
    if (this.ws) this.ws.close();
  }

  async eval(expr) {
    const res = await this.send('Runtime.evaluate', {
      expression: expr,
      returnByValue: true,
      awaitPromise: true
    });
    if (res.exceptionDetails) {
      throw new Error(`Eval exception: ${res.exceptionDetails.text} (${JSON.stringify(res.exceptionDetails)})`);
    }
    return res.result ? res.result.value : undefined;
  }
}

// 4. Test Runner
async function runE2eTests() {
  console.log('======================================================================');
  console.log('LandGuard Pro - End-to-End Real Browser Automation Test Suite');
  console.log('======================================================================');

  let server;
  let chrome;
  let browserClient;
  let pageClient;
  const consoleErrors = [];
  const uncaughtExceptions = [];

  try {
    // A. Start Local Server
    console.log('[1/10] Starting local HTTP static server on port ' + PORT + '...');
    server = await startStaticServer();
    console.log('       Server active at http://127.0.0.1:' + PORT + '/');

    // B. Launch Headless Chrome
    console.log('[2/10] Launching Google Chrome in headless mode with CDP...');
    chrome = await launchChrome(9222);
    console.log('       Chrome CDP connected at ' + chrome.webSocketDebuggerUrl);

    browserClient = new CdpClient(chrome.webSocketDebuggerUrl);
    await browserClient.connect();

    // Create a new target/page
    const target = await browserClient.send('Target.createTarget', { url: 'about:blank' });
    const pageWsUrl = `ws://127.0.0.1:9222/devtools/page/${target.targetId}`;
    pageClient = new CdpClient(pageWsUrl);
    await pageClient.connect();

    // Enable Domains
    await pageClient.send('Page.enable');
    await pageClient.send('Runtime.enable');
    await pageClient.send('DOM.enable');
    await pageClient.send('Log.enable');

    // Track console errors & exceptions
    pageClient.on('Runtime.exceptionThrown', (params) => {
      uncaughtExceptions.push(params.exceptionDetails);
    });
    pageClient.on('Log.entryAdded', (params) => {
      if (params.entry && params.entry.level === 'error') {
        consoleErrors.push(params.entry.text);
      }
    });
    pageClient.on('Page.javascriptDialogOpening', async () => {
      try {
        await pageClient.send('Page.handleJavaScriptDialog', { accept: true });
      } catch (err) {
        // ignore
      }
    });

    // C. Navigate to LandGuard Pro
    console.log('[3/10] Navigating to http://127.0.0.1:' + PORT + '/index.html...');
    await pageClient.send('Page.navigate', { url: `http://127.0.0.1:${PORT}/index.html` });

    // Wait for DOMContentLoaded / load
    await new Promise((res) => {
      pageClient.on('Page.loadEventFired', res);
      setTimeout(res, 2000);
    });
    await new Promise(r => setTimeout(r, 600));

    // D. Verify Zero Console Errors & Non-Blocking Overlay
    console.log('[4/10] Verifying page load health, zero errors, and non-blocking session...');
    assert.equal(uncaughtExceptions.length, 0, `Uncaught exceptions detected: ${JSON.stringify(uncaughtExceptions)}`);
    console.log('       ✓ Zero uncaught JavaScript exceptions on load.');

    const title = await pageClient.eval('document.title');
    assert.ok(title.includes('LandGuard Pro'), `Title unexpected: ${title}`);
    console.log(`       ✓ Page title confirmed: "${title}"`);

    const overlayIsHidden = await pageClient.eval('document.getElementById("loginOverlay").classList.contains("d-none")');
    assert.equal(overlayIsHidden, true, 'Login overlay must be hidden with d-none on initial load');
    console.log('       ✓ Login overlay is hidden (Zero lockout / Dashboard fully accessible).');

    const authUser = await pageClient.eval('localStorage.getItem("landguard_auth_user")');
    assert.ok(authUser && authUser.includes('Authorized Investor'), `Authorized session auto-provisioned: ${authUser}`);
    console.log(`       ✓ Auto-provisioned session verified: "${authUser}"`);

    // E. Verify Cadastre Grid & Exactly 1 Sample Holding
    console.log('[5/10] Auditing master parcels grid and sample holding policy...');
    const parcelCardsCount = await pageClient.eval('document.querySelectorAll("#ownersGrid .owner-card").length');
    assert.ok(parcelCardsCount >= 10, `Expected at least 10 parcels, got ${parcelCardsCount}`);
    console.log(`       ✓ Rendered ${parcelCardsCount} cadastral holdings in explorer grid.`);

    const sampleCards = await pageClient.eval(`
      Array.from(document.querySelectorAll("#ownersGrid .owner-card"))
        .filter(c => c.textContent.includes("Reference Benchmark") || c.textContent.includes("SAMPLE"))
        .length
    `);
    assert.equal(sampleCards, 1, `Expected strictly 1 sample benchmark card, found ${sampleCards}`);
    console.log('       ✓ Strictly 1 educational sample benchmark card verified in active cadastre.');

    // F. Verify Universal Search Functionality
    console.log('[6/10] Testing Universal Search across Name, Survey, and Patta...');
    // Search for Sureendhar
    await pageClient.eval(`(() => {
      const inp = document.getElementById("inpSearchQuery");
      inp.value = "Sureendhar";
      runSearch();
    })()`);
    await new Promise(r => setTimeout(r, 150));

    const sureeMatches = await pageClient.eval('document.querySelectorAll("#ownersGrid .owner-card").length');
    assert.equal(sureeMatches, 1, `Search "Sureendhar" should yield exactly 1 match (Survey 148/1), got ${sureeMatches}`);
    const sureeSurvey = await pageClient.eval('document.querySelector("#ownersGrid .owner-card .badge.bg-primary").innerText');
    assert.equal(sureeSurvey, '148/1');
    console.log('       ✓ Search "Sureendhar" matched Survey 148/1 instantly.');

    // Search for Survey 142/2A
    await pageClient.eval(`(() => {
      const inp = document.getElementById("inpSearchQuery");
      inp.value = "142/2A";
      runSearch();
    })()`);
    await new Promise(r => setTimeout(r, 150));
    const surveyMatches = await pageClient.eval('document.querySelectorAll("#ownersGrid .owner-card").length');
    assert.equal(surveyMatches, 1, `Search "142/2A" should yield 1 match, got ${surveyMatches}`);
    console.log('       ✓ Search "142/2A" matched Gurusamy Jagannathan holding.');

    // Reset search
    await pageClient.eval(`(() => {
      const inp = document.getElementById("inpSearchQuery");
      inp.value = "";
      runSearch();
    })()`);
    await new Promise(r => setTimeout(r, 150));

    // G. Verify Active Parcel Inspection & Dynamic Syncing
    console.log('[7/10] Testing active parcel selection and dynamic inspector sync...');
    await pageClient.eval('selectRecordData("148/1")'); // Select Survey 148/1
    await new Promise(r => setTimeout(r, 150));

    const sbSurvey = await pageClient.eval('document.getElementById("sbActiveSurvey").innerText');
    const sbPatta = await pageClient.eval('document.getElementById("sbActivePatta").innerText');
    const sbOwner = await pageClient.eval('document.getElementById("sbActiveOwner").innerText');
    assert.ok(sbSurvey.includes('148/1'), `Sidebar survey expected 148/1, got ${sbSurvey}`);
    assert.ok(sbPatta.includes('1550'), `Sidebar patta expected 1550, got ${sbPatta}`);
    assert.ok(sbOwner.includes('Sureendhar J'), `Sidebar owner expected Sureendhar J, got ${sbOwner}`);
    console.log(`       ✓ Active Inspection Widget synced: ${sbSurvey} | ${sbPatta} | ${sbOwner}`);

    // H. Verify All 9 Sidebar Navigation Desks & Partitions
    console.log('[8/10] Testing Sidebar Navigation across all 9 Partitioned Desks...');
    const pages = [
      { id: 'page-patta', label: '2. Patta Pass Book' },
      { id: 'page-ec', label: '3. 30-Yr EC Desk' },
      { id: 'page-fmb', label: '4. Cadastral FMB' },
      { id: 'page-deed', label: '5. Registered Deed' },
      { id: 'page-calculator', label: '6. Cost & Negotiation' },
      { id: 'page-dossiers', label: '7. Saved Dossiers' },
      { id: 'page-status', label: '8. Live Portal Monitor' },
      { id: 'page-fetch', label: '9. Official Fetch Desk' },
      { id: 'page-search', label: '1. Cadastre Explorer' }
    ];

    for (const p of pages) {
      await pageClient.eval(`switchPageView("${p.id}")`);
      await new Promise(r => setTimeout(r, 100));

      const isActive = await pageClient.eval(`document.getElementById("${p.id}").classList.contains("active")`);
      assert.equal(isActive, true, `Desk ${p.id} failed to activate`);

      const navActive = await pageClient.eval(`
        document.querySelector('#mainPageNavTabs button[data-page="${p.id}"]').classList.contains("active")
      `);
      assert.equal(navActive, true, `Sidebar button for ${p.id} failed to get active class`);
      console.log(`       ✓ Desk "${p.label}" switched and rendered active seamlessly.`);
    }

    // I. Verify Dedicated Document Viewers Content
    console.log('[9/10] Auditing dedicated document viewers data integrity...');
    const pattaNo = await pageClient.eval('document.getElementById("fpPattaNo").innerText');
    assert.equal(pattaNo, '1550');
    console.log(`       ✓ Patta Pass Book extracted number: ${pattaNo}`);

    const ecDocRows = await pageClient.eval('document.querySelectorAll("#fpEcTableBody tr").length');
    assert.ok(ecDocRows >= 2, `Expected at least 2 EC entries, got ${ecDocRows}`);
    console.log(`       ✓ 30-Yr EC Ledger rendered ${ecDocRows} registered encumbrance records.`);

    const fmbHasSvg = await pageClient.eval('Boolean(document.querySelector("#svgFmbCadastre, .fmb-cadastre-canvas polygon, .fmb-cadastre-canvas line"))');
    assert.equal(fmbHasSvg, true, 'FMB triangulation sketch SVG must contain geometry elements');
    console.log('       ✓ Cadastral FMB triangulation SVG geometry rendered.');

    // J. Verify Dossier Vault Saving
    console.log('[10/10] Testing Dossier Vault saving and storage...');
    await pageClient.eval('window.alert = () => {}; saveSelectedToDossier();');
    await new Promise(r => setTimeout(r, 150));

    const dossierCount = await pageClient.eval('document.getElementById("navDossierCount").innerText');
    assert.ok(parseInt(dossierCount, 10) >= 1, `Dossier count expected >= 1, got ${dossierCount}`);
    console.log(`       ✓ Dossier successfully saved to client vault (Count: ${dossierCount}).`);

    console.log('======================================================================');
    console.log('[ALL REAL-BROWSER TESTS PASSED] 10/10 Comprehensive E2E Scenarios Validated!');
    console.log('======================================================================');

  } finally {
    if (pageClient) pageClient.close();
    if (browserClient) browserClient.close();
    if (chrome && chrome.process) {
      chrome.process.kill();
    }
    if (chrome && chrome.profileDir) {
      try {
        fs.rmSync(chrome.profileDir, { recursive: true, force: true });
      } catch (e) {
        // profile cleanup best-effort
      }
    }
    if (server) {
      server.close();
    }
  }
}

runE2eTests().catch(err => {
  console.error('\n[TEST RUNNER FAILED]:', err);
  process.exit(1);
});
