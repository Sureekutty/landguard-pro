package com.landguard.model;

import java.time.LocalDateTime;

public class PropertyDossier {

    private String id;
    private String district = "Namakkal";
    private String taluk = "Rasipuram";
    private String village = "Gurusamipalayam";
    private String pincode = "637403";
    private String surveyNumber;
    private String subDivision;
    private String claimedOwner;
    private double extentSqFt = 1650.0;
    private double askingRate = 1375.0;
    private boolean dtcpApproved = false;
    private String approvalOrRegularisationNumber;
    private boolean ecClean30Years = false;
    private boolean pattaOwnerMatch = false;
    private boolean panchayatTaxCurrent = false;
    private boolean verifiedByAdvocate = false;
    private String statusNotes;
    private String createdAt;

    public PropertyDossier() {
        this.createdAt = LocalDateTime.now().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluk() { return taluk; }
    public void setTaluk(String taluk) { this.taluk = taluk; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getSurveyNumber() { return surveyNumber; }
    public void setSurveyNumber(String surveyNumber) { this.surveyNumber = surveyNumber; }

    public String getSubDivision() { return subDivision; }
    public void setSubDivision(String subDivision) { this.subDivision = subDivision; }

    public String getClaimedOwner() { return claimedOwner; }
    public void setClaimedOwner(String claimedOwner) { this.claimedOwner = claimedOwner; }

    public double getExtentSqFt() { return extentSqFt; }
    public void setExtentSqFt(double extentSqFt) { this.extentSqFt = extentSqFt; }

    public double getAskingRate() { return askingRate; }
    public void setAskingRate(double askingRate) { this.askingRate = askingRate; }

    public boolean isDtcpApproved() { return dtcpApproved; }
    public void setDtcpApproved(boolean dtcpApproved) { this.dtcpApproved = dtcpApproved; }

    public String getApprovalOrRegularisationNumber() { return approvalOrRegularisationNumber; }
    public void setApprovalOrRegularisationNumber(String approvalOrRegularisationNumber) { 
        this.approvalOrRegularisationNumber = approvalOrRegularisationNumber; 
    }

    public boolean isEcClean30Years() { return ecClean30Years; }
    public void setEcClean30Years(boolean ecClean30Years) { this.ecClean30Years = ecClean30Years; }

    public boolean isPattaOwnerMatch() { return pattaOwnerMatch; }
    public void setPattaOwnerMatch(boolean pattaOwnerMatch) { this.pattaOwnerMatch = pattaOwnerMatch; }

    public boolean isPanchayatTaxCurrent() { return panchayatTaxCurrent; }
    public void setPanchayatTaxCurrent(boolean panchayatTaxCurrent) { this.panchayatTaxCurrent = panchayatTaxCurrent; }

    public boolean isVerifiedByAdvocate() { return verifiedByAdvocate; }
    public void setVerifiedByAdvocate(boolean verifiedByAdvocate) { this.verifiedByAdvocate = verifiedByAdvocate; }

    public String getStatusNotes() { return statusNotes; }
    public void setStatusNotes(String statusNotes) { this.statusNotes = statusNotes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
