package com.landguard;

import com.landguard.security.SsrfProtectionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GovernmentSourceRegistryTest {

    private final SsrfProtectionValidator ssrfValidator = new SsrfProtectionValidator();

    @Test
    @DisplayName("SSRF Protection: Approved government portals on HTTPS pass validation")
    void testApprovedGovernmentPortalsPass() {
        assertTrue(ssrfValidator.validateUrl("https://eservices.tn.gov.in/").isValid());
        assertTrue(ssrfValidator.validateUrl("https://tnreginet.gov.in/").isValid());
        assertTrue(ssrfValidator.validateUrl("https://onlineppa.tn.gov.in/").isValid());
        assertTrue(ssrfValidator.validateUrl("https://tnglms.in/").isValid());
        assertTrue(ssrfValidator.validateUrl("https://services.ecourts.gov.in/").isValid());
    }

    @Test
    @DisplayName("SSRF Protection: Insecure HTTP protocol is rejected")
    void testHttpRejected() {
        SsrfProtectionValidator.ValidationResult res = ssrfValidator.validateUrl("http://eservices.tn.gov.in/");
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("Only 'https' is permitted"));
    }

    @Test
    @DisplayName("SSRF Protection: Localhost and loopback IPs are blocked (Anti-SSRF)")
    void testLocalhostBlocked() {
        assertFalse(ssrfValidator.validateUrl("https://localhost/").isValid());
        assertFalse(ssrfValidator.validateUrl("https://127.0.0.1/").isValid());
        assertFalse(ssrfValidator.validateUrl("https://127.0.0.1:8080/").isValid());
    }

    @Test
    @DisplayName("SSRF Protection: Non-approved external domains are blocked")
    void testUnapprovedDomainBlocked() {
        SsrfProtectionValidator.ValidationResult res = ssrfValidator.validateUrl("https://attacker.com/");
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("not in the approved government domain allowlist"));
    }

    @Test
    @DisplayName("SSRF Protection: Embedded credentials in URL are rejected")
    void testCredentialsBlocked() {
        SsrfProtectionValidator.ValidationResult res = ssrfValidator.validateUrl("https://admin:secret@eservices.tn.gov.in/");
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("Embedded credentials in URL are strictly prohibited"));
    }

    @Test
    @DisplayName("SSRF Protection: Non-standard ports are rejected")
    void testNonStandardPortBlocked() {
        SsrfProtectionValidator.ValidationResult res = ssrfValidator.validateUrl("https://eservices.tn.gov.in:8443/");
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("Non-standard port"));
    }
}
