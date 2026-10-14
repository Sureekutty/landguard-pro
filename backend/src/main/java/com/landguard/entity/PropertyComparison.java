package com.landguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_comparisons")
public class PropertyComparison {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "property_id", length = 36)
    private String propertyId;

    @Column(name = "overall_verdict", nullable = false, length = 50)
    private String overallVerdict;

    @Column(name = "survey_match", nullable = false, length = 30)
    private String surveyMatch;

    @Column(name = "owner_match", nullable = false, length = 30)
    private String ownerMatch;

    @Column(name = "extent_match", nullable = false, length = 30)
    private String extentMatch;

    @Column(name = "encumbrance_status", nullable = false, length = 50)
    private String encumbranceStatus;

    @Column(name = "comparison_summary", columnDefinition = "TEXT")
    private String comparisonSummary;

    @Column(name = "comparison_details_json", columnDefinition = "TEXT")
    private String comparisonDetailsJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PropertyComparison() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getOverallVerdict() { return overallVerdict; }
    public void setOverallVerdict(String overallVerdict) { this.overallVerdict = overallVerdict; }

    public String getSurveyMatch() { return surveyMatch; }
    public void setSurveyMatch(String surveyMatch) { this.surveyMatch = surveyMatch; }

    public String getOwnerMatch() { return ownerMatch; }
    public void setOwnerMatch(String ownerMatch) { this.ownerMatch = ownerMatch; }

    public String getExtentMatch() { return extentMatch; }
    public void setExtentMatch(String extentMatch) { this.extentMatch = extentMatch; }

    public String getEncumbranceStatus() { return encumbranceStatus; }
    public void setEncumbranceStatus(String encumbranceStatus) { this.encumbranceStatus = encumbranceStatus; }

    public String getComparisonSummary() { return comparisonSummary; }
    public void setComparisonSummary(String comparisonSummary) { this.comparisonSummary = comparisonSummary; }

    public String getComparisonDetailsJson() { return comparisonDetailsJson; }
    public void setComparisonDetailsJson(String comparisonDetailsJson) { this.comparisonDetailsJson = comparisonDetailsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
