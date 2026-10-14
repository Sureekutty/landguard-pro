package com.landguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "source_retrieval_history")
public class SourceRetrievalHistory {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "source_id", nullable = false, length = 50)
    private String sourceId;

    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(nullable = false)
    private boolean success;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "retrieval_timestamp", nullable = false)
    private LocalDateTime retrievalTimestamp;

    public SourceRetrievalHistory() {}

    public SourceRetrievalHistory(String id, String sourceId, String requestType, Integer statusCode, Long responseTimeMs, boolean success, String errorMessage) {
        this.id = id;
        this.sourceId = sourceId;
        this.requestType = requestType;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.success = success;
        this.errorMessage = errorMessage;
        this.retrievalTimestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getRetrievalTimestamp() { return retrievalTimestamp; }
    public void setRetrievalTimestamp(LocalDateTime retrievalTimestamp) { this.retrievalTimestamp = retrievalTimestamp; }
}
