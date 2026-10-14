package com.landguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "retrieved_property_records")
public class RetrievedPropertyRecord {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "search_id", length = 36)
    private String searchId;

    @Column(name = "source_id", length = 50)
    private String sourceId;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String taluk;

    @Column(nullable = false, length = 100)
    private String village;

    @Column(name = "survey_number", nullable = false, length = 50)
    private String surveyNumber;

    @Column(length = 50)
    private String subdivision;

    @Column(name = "full_survey", nullable = false, length = 100)
    private String fullSurvey;

    @Column(name = "patta_number", length = 50)
    private String pattaNumber;

    @Column(name = "owner_name_raw", nullable = false, length = 255)
    private String ownerNameRaw;

    @Column(name = "owner_name_normalized", length = 255)
    private String ownerNameNormalized;

    @Column(name = "father_name", length = 255)
    private String fatherName;

    @Column(name = "land_classification", length = 100)
    private String landClassification;

    @Column(name = "extent_raw", length = 100)
    private String extentRaw;

    @Column(name = "extent_sqft")
    private Double extentSqFt;

    @Column(name = "extent_cents")
    private Double extentCents;

    @Column(name = "asking_rate")
    private Double askingRate;

    @Column(name = "guideline_rate")
    private Double guidelineRate;

    @Column(name = "dtcp_approved")
    private boolean dtcpApproved;

    @Column(name = "dtcp_status", length = 255)
    private String dtcpStatus;

    @Column(name = "ec_clean")
    private boolean ecClean;

    @Column(name = "ec_status", length = 255)
    private String ecStatus;

    @Column(name = "is_demo_data", nullable = false)
    private boolean demoData = false;

    @Column(name = "evidence_status", length = 100)
    private String evidenceStatus;

    @Column(name = "evidence_citation", columnDefinition = "TEXT")
    private String evidenceCitation;

    @Column(name = "retrieval_timestamp", nullable = false)
    private LocalDateTime retrievalTimestamp;

    public RetrievedPropertyRecord() {}

    @PrePersist
    public void prePersist() {
        if (retrievalTimestamp == null) retrievalTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSearchId() { return searchId; }
    public void setSearchId(String searchId) { this.searchId = searchId; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluk() { return taluk; }
    public void setTaluk(String taluk) { this.taluk = taluk; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getSurveyNumber() { return surveyNumber; }
    public void setSurveyNumber(String surveyNumber) { this.surveyNumber = surveyNumber; }

    public String getSubdivision() { return subdivision; }
    public void setSubdivision(String subdivision) { this.subdivision = subdivision; }

    public String getFullSurvey() { return fullSurvey; }
    public void setFullSurvey(String fullSurvey) { this.fullSurvey = fullSurvey; }

    public String getPattaNumber() { return pattaNumber; }
    public void setPattaNumber(String pattaNumber) { this.pattaNumber = pattaNumber; }

    public String getOwnerNameRaw() { return ownerNameRaw; }
    public void setOwnerNameRaw(String ownerNameRaw) { this.ownerNameRaw = ownerNameRaw; }

    public String getOwnerNameNormalized() { return ownerNameNormalized; }
    public void setOwnerNameNormalized(String ownerNameNormalized) { this.ownerNameNormalized = ownerNameNormalized; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getLandClassification() { return landClassification; }
    public void setLandClassification(String landClassification) { this.landClassification = landClassification; }

    public String getExtentRaw() { return extentRaw; }
    public void setExtentRaw(String extentRaw) { this.extentRaw = extentRaw; }

    public Double getExtentSqFt() { return extentSqFt; }
    public void setExtentSqFt(Double extentSqFt) { this.extentSqFt = extentSqFt; }

    public Double getExtentCents() { return extentCents; }
    public void setExtentCents(Double extentCents) { this.extentCents = extentCents; }

    public Double getAskingRate() { return askingRate; }
    public void setAskingRate(Double askingRate) { this.askingRate = askingRate; }

    public Double getGuidelineRate() { return guidelineRate; }
    public void setGuidelineRate(Double guidelineRate) { this.guidelineRate = guidelineRate; }

    public boolean isDtcpApproved() { return dtcpApproved; }
    public void setDtcpApproved(boolean dtcpApproved) { this.dtcpApproved = dtcpApproved; }

    public String getDtcpStatus() { return dtcpStatus; }
    public void setDtcpStatus(String dtcpStatus) { this.dtcpStatus = dtcpStatus; }

    public boolean isEcClean() { return ecClean; }
    public void setEcClean(boolean ecClean) { this.ecClean = ecClean; }

    public String getEcStatus() { return ecStatus; }
    public void setEcStatus(String ecStatus) { this.ecStatus = ecStatus; }

    public boolean isDemoData() { return demoData; }
    public void setDemoData(boolean demoData) { this.demoData = demoData; }

    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }

    public String getEvidenceCitation() { return evidenceCitation; }
    public void setEvidenceCitation(String evidenceCitation) { this.evidenceCitation = evidenceCitation; }

    public LocalDateTime getRetrievalTimestamp() { return retrievalTimestamp; }
    public void setRetrievalTimestamp(LocalDateTime retrievalTimestamp) { this.retrievalTimestamp = retrievalTimestamp; }
}
