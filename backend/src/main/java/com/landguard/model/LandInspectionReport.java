package com.landguard.model;

import java.util.ArrayList;
import java.util.List;

public class LandInspectionReport {
    private String inspectionId;
    private String timestamp;
    private String district;
    private String taluk;
    private String village;
    private String surveyNumber;
    private String subDivision;
    private String plotNumber;
    private String claimedOwner;
    private double landAreaSqFt;
    private double cents;
    private CostCalculationResult costBreakdown;
    private String riskLevel;
    private String dtcpAdvice;
    private String revenueAdvice;
    private String courtAdvice;
    private List<PortalInspectionDetail> portalChecks = new ArrayList<>();
    private List<String> checklistActions = new ArrayList<>();

    public LandInspectionReport() {}

    public String getInspectionId() { return inspectionId; }
    public void setInspectionId(String inspectionId) { this.inspectionId = inspectionId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

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

    public double getCents() { return cents; }
    public void setCents(double cents) { this.cents = cents; }

    public CostCalculationResult getCostBreakdown() { return costBreakdown; }
    public void setCostBreakdown(CostCalculationResult costBreakdown) { this.costBreakdown = costBreakdown; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getDtcpAdvice() { return dtcpAdvice; }
    public void setDtcpAdvice(String dtcpAdvice) { this.dtcpAdvice = dtcpAdvice; }

    public String getRevenueAdvice() { return revenueAdvice; }
    public void setRevenueAdvice(String revenueAdvice) { this.revenueAdvice = revenueAdvice; }

    public String getCourtAdvice() { return courtAdvice; }
    public void setCourtAdvice(String courtAdvice) { this.courtAdvice = courtAdvice; }

    public List<PortalInspectionDetail> getPortalChecks() { return portalChecks; }
    public void setPortalChecks(List<PortalInspectionDetail> portalChecks) { this.portalChecks = portalChecks; }

    public List<String> getChecklistActions() { return checklistActions; }
    public void setChecklistActions(List<String> checklistActions) { this.checklistActions = checklistActions; }
}