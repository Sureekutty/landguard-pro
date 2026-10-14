package com.landguard.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Component
public class SsrfProtectionValidator {

    private static final Logger log = LoggerFactory.getLogger(SsrfProtectionValidator.class);

    private static final List<String> APPROVED_GOVERNMENT_DOMAINS = Arrays.asList(
            "eservices.tn.gov.in",
            "tnreginet.gov.in",
            "onlineppa.tn.gov.in",
            "tnglms.in",
            "services.ecourts.gov.in"
    );

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }

    public ValidationResult validateUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return new ValidationResult(false, "URL cannot be empty.");
        }

        URI uri;
        try {
            uri = URI.create(urlString.trim());
        } catch (Exception ex) {
            return new ValidationResult(false, "Malformed URI: " + ex.getMessage());
        }

        // 1. Protocol check - strictly https:
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            return new ValidationResult(false, "Forbidden protocol '" + uri.getScheme() + "'. Only 'https' is permitted.");
        }

        // 2. Port check - default 443 only
        if (uri.getPort() != -1 && uri.getPort() != 443) {
            return new ValidationResult(false, "Non-standard port " + uri.getPort() + " is forbidden.");
        }

        // 3. UserInfo check - no credentials
        if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty()) {
            return new ValidationResult(false, "Embedded credentials in URL are strictly prohibited.");
        }

        String rawHost = uri.getHost();
        if (rawHost == null || rawHost.trim().isEmpty()) {
            return new ValidationResult(false, "Missing hostname in URL.");
        }
        final String host = rawHost.toLowerCase();

        // 4. Domain allowlist check
        boolean isApproved = APPROVED_GOVERNMENT_DOMAINS.stream().anyMatch(approved ->
                host.equals(approved) || host.endsWith("." + approved)
        );

        if (!isApproved) {
            return new ValidationResult(false, "Domain '" + host + "' is not in the approved government domain allowlist.");
        }

        // 5. DNS Resolution & Private IP check (Anti-SSRF / DNS Rebinding protection)
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses) {
                if (addr.isLoopbackAddress() || addr.isSiteLocalAddress() || addr.isLinkLocalAddress() || addr.isAnyLocalAddress()) {
                    log.warn("SSRF attempt detected! Host '{}' resolved to private/loopback IP: {}", host, addr.getHostAddress());
                    return new ValidationResult(false, "SSRF Blocked: Host resolves to internal or private IP address.");
                }

                // Check specifically for AWS/GCP cloud metadata IP: 169.254.169.254
                if ("169.254.169.254".equals(addr.getHostAddress())) {
                    log.warn("SSRF attempt targeting cloud metadata service: {}", host);
                    return new ValidationResult(false, "SSRF Blocked: Host resolves to cloud metadata address.");
                }
            }
        } catch (UnknownHostException ex) {
            return new ValidationResult(false, "DNS Resolution failed for host: " + host);
        }

        return new ValidationResult(true, "URL passed all safety and SSRF validation checks.");
    }
}
