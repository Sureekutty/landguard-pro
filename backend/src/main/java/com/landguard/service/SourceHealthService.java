package com.landguard.service;

import com.landguard.entity.SourceRegistry;
import com.landguard.entity.SourceRetrievalHistory;
import com.landguard.repository.SourceRegistryRepository;
import com.landguard.repository.SourceRetrievalHistoryRepository;
import com.landguard.security.SsrfProtectionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SourceHealthService {

    private static final Logger log = LoggerFactory.getLogger(SourceHealthService.class);
    private static final int TIMEOUT_MS = 6000;

    private final SourceRegistryRepository sourceRepository;
    private final SourceRetrievalHistoryRepository historyRepository;
    private final SsrfProtectionValidator ssrfValidator;

    public SourceHealthService(
            SourceRegistryRepository sourceRepository,
            SourceRetrievalHistoryRepository historyRepository,
            SsrfProtectionValidator ssrfValidator) {
        this.sourceRepository = sourceRepository;
        this.historyRepository = historyRepository;
        this.ssrfValidator = ssrfValidator;
    }

    public SourceRegistry performHealthCheck(String sourceId) {
        SourceRegistry source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));

        String url = source.getOfficialUrl();
        SsrfProtectionValidator.ValidationResult validation = ssrfValidator.validateUrl(url);

        if (!validation.isValid()) {
            source.setIntegrationStatus("ERROR");
            source.setLastHealthCheck(LocalDateTime.now());
            recordHistory(sourceId, "HEALTH_CHECK", 400, 0L, false, "SSRF Check Failed: " + validation.getMessage());
            return sourceRepository.save(source);
        }

        long startTime = System.currentTimeMillis();
        int statusCode = 0;
        boolean success = false;
        String errorMessage = null;

        try {
            URI uri = URI.create(url);
            URL targetUrl = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 LandGuardPro-SecurityAuditor/2.0");
            connection.setInstanceFollowRedirects(false); // Do not follow arbitrary redirects

            statusCode = connection.getResponseCode();
            success = (statusCode >= 200 && statusCode < 400);

            if (success) {
                source.setLastSuccessfulRetrieval(LocalDateTime.now());
                if (!"ACTIVE".equals(source.getIntegrationStatus()) && !"DISABLED".equals(source.getIntegrationStatus())) {
                    source.setIntegrationStatus("ACTIVE");
                }
            } else {
                source.setIntegrationStatus("TEMPORARILY_UNAVAILABLE");
                errorMessage = "HTTP Status " + statusCode;
            }
        } catch (IOException ex) {
            statusCode = 503;
            errorMessage = "Connection failed: " + ex.getMessage();
            source.setIntegrationStatus("TEMPORARILY_UNAVAILABLE");
        }

        long duration = System.currentTimeMillis() - startTime;
        source.setLastHealthCheck(LocalDateTime.now());
        sourceRepository.save(source);

        recordHistory(sourceId, "HEALTH_CHECK", statusCode, duration, success, errorMessage);
        return source;
    }

    private void recordHistory(String sourceId, String reqType, int status, long duration, boolean success, String error) {
        try {
            SourceRetrievalHistory history = new SourceRetrievalHistory(
                    UUID.randomUUID().toString(),
                    sourceId,
                    reqType,
                    status,
                    duration,
                    success,
                    error
            );
            historyRepository.save(history);
        } catch (Exception ex) {
            log.warn("Failed to record source history: {}", ex.getMessage());
        }
    }
}
