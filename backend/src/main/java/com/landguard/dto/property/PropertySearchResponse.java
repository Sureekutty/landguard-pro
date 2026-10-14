package com.landguard.dto.property;

import com.landguard.entity.RetrievedPropertyRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PropertySearchResponse {

    private String searchId;
    private Map<String, Object> querySummary;
    private Map<String, String> sourceStatuses;
    private List<RetrievedPropertyRecord> records;
    private int totalRecordsFound;
    private String matchConfidence;
    private boolean furtherVerificationRequired;
    private String legalAdvisoryNotice;
    private LocalDateTime retrievalTimestamp;

    public PropertySearchResponse() {}

    public String getSearchId() { return searchId; }
    public void setSearchId(String searchId) { this.searchId = searchId; }

    public Map<String, Object> getQuerySummary() { return querySummary; }
    public void setQuerySummary(Map<String, Object> querySummary) { this.querySummary = querySummary; }

    public Map<String, String> getSourceStatuses() { return sourceStatuses; }
    public void setSourceStatuses(Map<String, String> sourceStatuses) { this.sourceStatuses = sourceStatuses; }

    public List<RetrievedPropertyRecord> getRecords() { return records; }
    public void setRecords(List<RetrievedPropertyRecord> records) { this.records = records; }

    public int getTotalRecordsFound() { return totalRecordsFound; }
    public void setTotalRecordsFound(int totalRecordsFound) { this.totalRecordsFound = totalRecordsFound; }

    public String getMatchConfidence() { return matchConfidence; }
    public void setMatchConfidence(String matchConfidence) { this.matchConfidence = matchConfidence; }

    public boolean isFurtherVerificationRequired() { return furtherVerificationRequired; }
    public void setFurtherVerificationRequired(boolean furtherVerificationRequired) { this.furtherVerificationRequired = furtherVerificationRequired; }

    public String getLegalAdvisoryNotice() { return legalAdvisoryNotice; }
    public void setLegalAdvisoryNotice(String legalAdvisoryNotice) { this.legalAdvisoryNotice = legalAdvisoryNotice; }

    public LocalDateTime getRetrievalTimestamp() { return retrievalTimestamp; }
    public void setRetrievalTimestamp(LocalDateTime retrievalTimestamp) { this.retrievalTimestamp = retrievalTimestamp; }
}
