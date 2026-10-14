package com.landguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "source_registry")
public class SourceRegistry {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "official_url", nullable = false, length = 255)
    private String officialUrl;

    @Column(nullable = false, length = 100)
    private String jurisdiction;

    @Column(name = "supported_record_types", nullable = false, length = 255)
    private String supportedRecordTypes;

    @Column(name = "supported_search_fields", nullable = false, length = 255)
    private String supportedSearchFields;

    @Column(name = "integration_method", nullable = false, length = 50)
    private String integrationMethod;

    @Column(name = "authorization_status", nullable = false, length = 50)
    private String authorizationStatus;

    @Column(name = "integration_status", nullable = false, length = 50)
    private String integrationStatus; // NOT_CONFIGURED, REQUIRES_AUTHORIZATION, MANUAL_VERIFICATION, CONFIGURED, ACTIVE, TEMPORARILY_UNAVAILABLE, DISABLED, ERROR

    @Column(name = "last_successful_retrieval")
    private LocalDateTime lastSuccessfulRetrieval;

    @Column(name = "last_health_check")
    private LocalDateTime lastHealthCheck;

    @Column(name = "known_limitations", columnDefinition = "TEXT")
    private String knownLimitations;

    @Column(name = "applicable_usage_restrictions", columnDefinition = "TEXT")
    private String applicableUsageRestrictions;

    @Column(name = "source_disclaimer", columnDefinition = "TEXT")
    private String sourceDisclaimer;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SourceRegistry() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOfficialUrl() { return officialUrl; }
    public void setOfficialUrl(String officialUrl) { this.officialUrl = officialUrl; }

    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }

    public String getSupportedRecordTypes() { return supportedRecordTypes; }
    public void setSupportedRecordTypes(String supportedRecordTypes) { this.supportedRecordTypes = supportedRecordTypes; }

    public String getSupportedSearchFields() { return supportedSearchFields; }
    public void setSupportedSearchFields(String supportedSearchFields) { this.supportedSearchFields = supportedSearchFields; }

    public String getIntegrationMethod() { return integrationMethod; }
    public void setIntegrationMethod(String integrationMethod) { this.integrationMethod = integrationMethod; }

    public String getAuthorizationStatus() { return authorizationStatus; }
    public void setAuthorizationStatus(String authorizationStatus) { this.authorizationStatus = authorizationStatus; }

    public String getIntegrationStatus() { return integrationStatus; }
    public void setIntegrationStatus(String integrationStatus) { this.integrationStatus = integrationStatus; }

    public LocalDateTime getLastSuccessfulRetrieval() { return lastSuccessfulRetrieval; }
    public void setLastSuccessfulRetrieval(LocalDateTime lastSuccessfulRetrieval) { this.lastSuccessfulRetrieval = lastSuccessfulRetrieval; }

    public LocalDateTime getLastHealthCheck() { return lastHealthCheck; }
    public void setLastHealthCheck(LocalDateTime lastHealthCheck) { this.lastHealthCheck = lastHealthCheck; }

    public String getKnownLimitations() { return knownLimitations; }
    public void setKnownLimitations(String knownLimitations) { this.knownLimitations = knownLimitations; }

    public String getApplicableUsageRestrictions() { return applicableUsageRestrictions; }
    public void setApplicableUsageRestrictions(String applicableUsageRestrictions) { this.applicableUsageRestrictions = applicableUsageRestrictions; }

    public String getSourceDisclaimer() { return sourceDisclaimer; }
    public void setSourceDisclaimer(String sourceDisclaimer) { this.sourceDisclaimer = sourceDisclaimer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
