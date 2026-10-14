package com.landguard.service;

import com.landguard.model.PortalStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Service
public class PortalMonitorService {

    private final HttpClient httpClient;

    private static final Pattern OFFICIAL_TN_GOV_PATTERN = Pattern.compile(
        "^https://([a-zA-Z0-9.-]+\\.)?(tn\\.gov\\.in|gov\\.in|nic\\.in)(/.*)?$",
        Pattern.CASE_INSENSITIVE
    );

    private final List<Map<String, String>> portals = List.of(
        Map.of(
            "name", "TNREGINET Portal",
            "url", "https://tnreginet.gov.in",
            "desc", "Official Inspector General of Registration: 30-Year Encumbrance Certificate (EC) & Survey Guideline Value."
        ),
        Map.of(
            "name", "AnyTN eServices (Revenue Dept)",
            "url", "https://eservices.tn.gov.in",
            "desc", "Tamil Nadu Revenue Portal: Computerised Patta, Chitta extract, FMB boundary sketch, and A-Register."
        ),
        Map.of(
            "name", "DTCP Online Layout Approval",
            "url", "https://onlineppa.tn.gov.in",
            "desc", "Directorate of Town & Country Planning: In-principle layout sanctions & Section 22-A Regularisation."
        ),
        Map.of(
            "name", "DTCP Main Directorate",
            "url", "https://tcp.tn.gov.in",
            "desc", "Directorate of Town & Country Planning official master repository and regional town planning notices."
        ),
        Map.of(
            "name", "e-Courts Namakkal / Rasipuram",
            "url", "https://services.ecourts.gov.in",
            "desc", "National Judicial Data Grid: Verify pending partition suits, stay orders, and litigation for Rasipuram Taluk."
        ),
        Map.of(
            "name", "TNRERA Project Registry",
            "url", "https://tnrera.in",
            "desc", "Tamil Nadu Real Estate Regulatory Authority: Verify registered residential layout promoter registrations."
        )
    );

    public PortalMonitorService() {
        this.httpClient = createResilientHttpClient();
    }

    private HttpClient createResilientHttpClient() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(6))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(6))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }
    }

    public boolean isOfficialDomain(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        return OFFICIAL_TN_GOV_PATTERN.matcher(url.trim()).matches();
    }

    public List<PortalStatus> checkAllPortals() {
        List<PortalStatus> results = new ArrayList<>();

        for (Map<String, String> portal : portals) {
            String name = portal.get("name");
            String url = portal.get("url");
            String desc = portal.get("desc");

            boolean isOfficial = isOfficialDomain(url);
            String securityNotice = isOfficial ? "Verified Official .gov.in Domain" : "WARNING: Unofficial Domain";

            long start = System.currentTimeMillis();
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(6))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) LandGuard/1.0")
                        .GET()
                        .build();

                HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                long latency = System.currentTimeMillis() - start;
                int code = response.statusCode();
                boolean reachable = (code >= 200 && code < 400);

                results.add(new PortalStatus(name, url, desc, isOfficial, reachable, code, latency, securityNotice));
            } catch (Exception ex) {
                long latency = System.currentTimeMillis() - start;
                results.add(new PortalStatus(name, url, desc, isOfficial, false, 0, latency, securityNotice + " (Connection Timed Out)"));
            }
        }
        return results;
    }
}
