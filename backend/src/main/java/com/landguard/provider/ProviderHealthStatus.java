package com.landguard.provider;

public class ProviderHealthStatus {
    private final boolean healthy;
    private final int httpStatus;
    private final long latencyMs;
    private final String message;

    public ProviderHealthStatus(boolean healthy, int httpStatus, long latencyMs, String message) {
        this.healthy = healthy;
        this.httpStatus = httpStatus;
        this.latencyMs = latencyMs;
        this.message = message;
    }

    public boolean isHealthy() { return healthy; }
    public int getHttpStatus() { return httpStatus; }
    public long getLatencyMs() { return latencyMs; }
    public String getMessage() { return message; }
}
