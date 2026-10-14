package com.landguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_dossiers")
public class UserDossier {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36)
    private String userId;

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

    @Column(name = "claimed_owner", length = 255)
    private String claimedOwner;

    @Column(name = "extent_sqft")
    private Double extentSqFt;

    @Column(name = "asking_rate")
    private Double askingRate;

    @Column(name = "guideline_rate")
    private Double guidelineRate;

    @Column(name = "dtcp_approved")
    private Boolean dtcpApproved;

    @Column(name = "ec_clean")
    private Boolean ecClean;

    @Column(name = "status_notes", columnDefinition = "TEXT")
    private String statusNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserDossier() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

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

    public String getClaimedOwner() { return claimedOwner; }
    public void setClaimedOwner(String claimedOwner) { this.claimedOwner = claimedOwner; }

    public Double getExtentSqFt() { return extentSqFt; }
    public void setExtentSqFt(Double extentSqFt) { this.extentSqFt = extentSqFt; }

    public Double getAskingRate() { return askingRate; }
    public void setAskingRate(Double askingRate) { this.askingRate = askingRate; }

    public Double getGuidelineRate() { return guidelineRate; }
    public void setGuidelineRate(Double guidelineRate) { this.guidelineRate = guidelineRate; }

    public Boolean getDtcpApproved() { return dtcpApproved; }
    public void setDtcpApproved(Boolean dtcpApproved) { this.dtcpApproved = dtcpApproved; }

    public Boolean getEcClean() { return ecClean; }
    public void setEcClean(Boolean ecClean) { this.ecClean = ecClean; }

    public String getStatusNotes() { return statusNotes; }
    public void setStatusNotes(String statusNotes) { this.statusNotes = statusNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
