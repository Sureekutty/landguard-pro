package com.landguard.model;

public class LandInspectionRequest {
    private String district;
    private String taluk;
    private String village;
    private String surveyNumber;
    private String subDivision;
    private String plotNumber;
    private String claimedOwner;
    private double landAreaSqFt = 1650.0;
    private double askingRate = 1200.0;
    private boolean isLayoutOrSubdivided = true;

    public LandInspectionRequest() {}

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluk() { return taluk; }
    public void setTaluk(String taluk) { this.taluk = taluk; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getSurveyNumber() { return surveyNumber; }
    public void setSurveyNumber(String surveyNumber) { this.surveyNumber = surveyNumber; }

    public String getSubDivision() { return subDivision; }
    public void setSubDivision(String subDivision) { this.subDivision = subDivision; }

    public String getPlotNumber() { return plotNumber; }
    public void setPlotNumber(String plotNumber) { this.plotNumber = plotNumber; }

    public String getClaimedOwner() { return claimedOwner; }
    public void setClaimedOwner(String claimedOwner) { this.claimedOwner = claimedOwner; }

    public double getLandAreaSqFt() { return landAreaSqFt; }
    public void setLandAreaSqFt(double landAreaSqFt) { this.landAreaSqFt = landAreaSqFt; }

    public double getAskingRate() { return askingRate; }
    public void setAskingRate(double askingRate) { this.askingRate = askingRate; }

    public boolean isLayoutOrSubdivided() { return isLayoutOrSubdivided; }
    public void setLayoutOrSubdivided(boolean layoutOrSubdivided) { isLayoutOrSubdivided = layoutOrSubdivided; }
}