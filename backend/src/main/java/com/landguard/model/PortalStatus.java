package com.landguard.model;

public class PortalStatus {
    private String name;
    private String url;
    private String description;
    private boolean isOfficialGov;
    private boolean reachable;
    private int statusCode;
    private long responseTimeMs;
    private String securityStatus;

    public PortalStatus() {}

    public PortalStatus(String name, String url, String description, boolean isOfficialGov, 
                        boolean reachable, int statusCode, long responseTimeMs, String securityStatus) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.isOfficialGov = isOfficialGov;
        this.reachable = reachable;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.securityStatus = securityStatus;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isOfficialGov() { return isOfficialGov; }
    public void setOfficialGov(boolean officialGov) { isOfficialGov = officialGov; }

    public boolean isReachable() { return reachable; }
    public void setReachable(boolean reachable) { this.reachable = reachable; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public String getSecurityStatus() { return securityStatus; }
    public void setSecurityStatus(String securityStatus) { this.securityStatus = securityStatus; }
}
