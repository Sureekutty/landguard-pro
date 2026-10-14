package com.landguard.provider;

import com.landguard.entity.RetrievedPropertyRecord;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ProviderSearchResult {

    public enum Status {
        SUCCESS,
        NO_RECORDS_FOUND,
        SOURCE_UNAVAILABLE,
        REQUIRES_AUTHORIZATION,
        MANUAL_VERIFICATION_REQUIRED,
        ERROR
    }

    private final String providerId;
    private final Status status;
    private final String message;
    private final List<RetrievedPropertyRecord> records;
    private final String evidenceCitation;
    private final LocalDateTime retrievalTimestamp;

    public ProviderSearchResult(String providerId, Status status, String message, List<RetrievedPropertyRecord> records, String evidenceCitation) {
        this.providerId = providerId;
        this.status = status;
        this.message = message;
        this.records = records != null ? records : Collections.emptyList();
        this.evidenceCitation = evidenceCitation;
        this.retrievalTimestamp = LocalDateTime.now();
    }

    public static ProviderSearchResult success(String providerId, List<RetrievedPropertyRecord> records, String citation) {
        return new ProviderSearchResult(providerId, Status.SUCCESS, "Records retrieved successfully.", records, citation);
    }

    public static ProviderSearchResult noRecords(String providerId, String reason) {
        return new ProviderSearchResult(providerId, Status.NO_RECORDS_FOUND, reason != null ? reason : "No matching record returned by the selected source.", Collections.emptyList(), null);
    }

    public static ProviderSearchResult unavailable(String providerId, String message) {
        return new ProviderSearchResult(providerId, Status.SOURCE_UNAVAILABLE, message != null ? message : "Source unavailable; verification incomplete.", Collections.emptyList(), null);
    }

    public static ProviderSearchResult manualRequired(String providerId, String guidance) {
        return new ProviderSearchResult(providerId, Status.MANUAL_VERIFICATION_REQUIRED, guidance, Collections.emptyList(), null);
    }

    public static ProviderSearchResult requiresAuth(String providerId, String guidance) {
        return new ProviderSearchResult(providerId, Status.REQUIRES_AUTHORIZATION, guidance, Collections.emptyList(), null);
    }

    public String getProviderId() { return providerId; }
    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public List<RetrievedPropertyRecord> getRecords() { return records; }
    public String getEvidenceCitation() { return evidenceCitation; }
    public LocalDateTime getRetrievalTimestamp() { return retrievalTimestamp; }
}
