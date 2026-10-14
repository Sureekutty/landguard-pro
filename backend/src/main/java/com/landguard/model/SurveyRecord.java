package com.landguard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyRecord {
    private String district;
    private String taluk;
    private String village;
    private String sroOffice;
    private String surveyNumber;
    private String subDivision;
    private String fullSurvey;
    private String pattaNumber;
    private String ownerNameEn;
    private String ownerNameTa;
    private String fatherNameEn;
    private String fatherNameTa;
    private String classification;
    private String extentHectares;
    private double extentSqFt;
    private double cents;
    private String taxAssessment;
    private double guidelineValuePerSqFt;
    private double askingRate;
    private String dtcpStatus;
    private boolean dtcpApproved;
    private double dtcpRegularisationCost;
    private String ecStatus;
    private boolean ecClean;
    private boolean activeLitigation;
    private String litigationNotes;

    // Rich Document fields
    private String fmbSvg;
    private String fmbRoadWidth;
    private String northBoundary;
    private String southBoundary;
    private String eastBoundary;
    private String westBoundary;
    private String surveyorName;
    private String tahsildarName;
    private String deedNumber;
    private String ecNumber;
    // Corner plot & specialized intelligence
    private boolean isCornerPlot;
    private String cornerPlotRoads;
    private String cornerSplayRequired;
    private String cornerSplayAreaLoss;
    private String cornerPlotPremium;
    private String pattaProofCitation;
    private String ecProofCitation;
    private String fmbProofCitation;
    private String cornerProofCitation;
    private String dtcpProofCitation;



    private List<EcRecordEntry> ecEntries = new ArrayList<>();
    private List<TitleDeedLink> titleDeedChain = new ArrayList<>();
    private Map<String, String> officialPortalLinks = new HashMap<>();

    public SurveyRecord() {}

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluk() { return taluk; }
    public void setTaluk(String taluk) { this.taluk = taluk; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getSroOffice() { return sroOffice; }
    public void setSroOffice(String sroOffice) { this.sroOffice = sroOffice; }

    public String getSurveyNumber() { return surveyNumber; }
    public void setSurveyNumber(String surveyNumber) { this.surveyNumber = surveyNumber; }

    public String getSubDivision() { return subDivision; }
    public void setSubDivision(String subDivision) { this.subDivision = subDivision; }

    public String getFullSurvey() { return fullSurvey; }
    public void setFullSurvey(String fullSurvey) { this.fullSurvey = fullSurvey; }

    public String getPattaNumber() { return pattaNumber; }
    public void setPattaNumber(String pattaNumber) { this.pattaNumber = pattaNumber; }

    public String getOwnerNameEn() { return ownerNameEn; }
    public void setOwnerNameEn(String ownerNameEn) { this.ownerNameEn = ownerNameEn; }

    public String getOwnerNameTa() { return ownerNameTa; }
    public void setOwnerNameTa(String ownerNameTa) { this.ownerNameTa = ownerNameTa; }

    public String getFatherNameEn() { return fatherNameEn; }
    public void setFatherNameEn(String fatherNameEn) { this.fatherNameEn = fatherNameEn; }

    public String getFatherNameTa() { return fatherNameTa; }
    public void setFatherNameTa(String fatherNameTa) { this.fatherNameTa = fatherNameTa; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public String getExtentHectares() { return extentHectares; }
    public void setExtentHectares(String extentHectares) { this.extentHectares = extentHectares; }

    public double getExtentSqFt() { return extentSqFt; }
    public void setExtentSqFt(double extentSqFt) { this.extentSqFt = extentSqFt; }

    public double getCents() { return cents; }
    public void setCents(double cents) { this.cents = cents; }

    public String getTaxAssessment() { return taxAssessment; }
    public void setTaxAssessment(String taxAssessment) { this.taxAssessment = taxAssessment; }

    public double getGuidelineValuePerSqFt() { return guidelineValuePerSqFt; }
    public void setGuidelineValuePerSqFt(double guidelineValuePerSqFt) { this.guidelineValuePerSqFt = guidelineValuePerSqFt; }

    public double getAskingRate() { return askingRate; }
    public void setAskingRate(double askingRate) { this.askingRate = askingRate; }

    public String getDtcpStatus() { return dtcpStatus; }
    public void setDtcpStatus(String dtcpStatus) { this.dtcpStatus = dtcpStatus; }

    public boolean isDtcpApproved() { return dtcpApproved; }
    public void setDtcpApproved(boolean dtcpApproved) { this.dtcpApproved = dtcpApproved; }

    public double getDtcpRegularisationCost() { return dtcpRegularisationCost; }
    public void setDtcpRegularisationCost(double dtcpRegularisationCost) { this.dtcpRegularisationCost = dtcpRegularisationCost; }

    public String getEcStatus() { return ecStatus; }
    public void setEcStatus(String ecStatus) { this.ecStatus = ecStatus; }

    public boolean isEcClean() { return ecClean; }
    public void setEcClean(boolean ecClean) { this.ecClean = ecClean; }

    public boolean isActiveLitigation() { return activeLitigation; }
    public void setActiveLitigation(boolean activeLitigation) { this.activeLitigation = activeLitigation; }

    public String getLitigationNotes() { return litigationNotes; }
    public void setLitigationNotes(String litigationNotes) { this.litigationNotes = litigationNotes; }

    public String getFmbSvg() { return fmbSvg; }
    public void setFmbSvg(String fmbSvg) { this.fmbSvg = fmbSvg; }

    public String getFmbRoadWidth() { return fmbRoadWidth; }
    public void setFmbRoadWidth(String fmbRoadWidth) { this.fmbRoadWidth = fmbRoadWidth; }

    public String getNorthBoundary() { return northBoundary; }
    public void setNorthBoundary(String northBoundary) { this.northBoundary = northBoundary; }

    public String getSouthBoundary() { return southBoundary; }
    public void setSouthBoundary(String southBoundary) { this.southBoundary = southBoundary; }

    public String getEastBoundary() { return eastBoundary; }
    public void setEastBoundary(String eastBoundary) { this.eastBoundary = eastBoundary; }

    public String getWestBoundary() { return westBoundary; }
    public void setWestBoundary(String westBoundary) { this.westBoundary = westBoundary; }

    public String getSurveyorName() { return surveyorName; }
    public void setSurveyorName(String surveyorName) { this.surveyorName = surveyorName; }

    public String getTahsildarName() { return tahsildarName; }
    public void setTahsildarName(String tahsildarName) { this.tahsildarName = tahsildarName; }

    public String getDeedNumber() { return deedNumber; }
    public void setDeedNumber(String deedNumber) { this.deedNumber = deedNumber; }

    
    public boolean isCornerPlot() { return isCornerPlot; }
    public void setCornerPlot(boolean cornerPlot) { isCornerPlot = cornerPlot; }

    public String getCornerPlotRoads() { return cornerPlotRoads; }
    public void setCornerPlotRoads(String cornerPlotRoads) { this.cornerPlotRoads = cornerPlotRoads; }

    public String getCornerSplayRequired() { return cornerSplayRequired; }
    public void setCornerSplayRequired(String cornerSplayRequired) { this.cornerSplayRequired = cornerSplayRequired; }

    public String getCornerSplayAreaLoss() { return cornerSplayAreaLoss; }
    public void setCornerSplayAreaLoss(String cornerSplayAreaLoss) { this.cornerSplayAreaLoss = cornerSplayAreaLoss; }

    public String getCornerPlotPremium() { return cornerPlotPremium; }
    public void setCornerPlotPremium(String cornerPlotPremium) { this.cornerPlotPremium = cornerPlotPremium; }

    public String getPattaProofCitation() { return pattaProofCitation; }
    public void setPattaProofCitation(String pattaProofCitation) { this.pattaProofCitation = pattaProofCitation; }

    public String getEcProofCitation() { return ecProofCitation; }
    public void setEcProofCitation(String ecProofCitation) { this.ecProofCitation = ecProofCitation; }

    public String getFmbProofCitation() { return fmbProofCitation; }
    public void setFmbProofCitation(String fmbProofCitation) { this.fmbProofCitation = fmbProofCitation; }

    public String getCornerProofCitation() { return cornerProofCitation; }
    public void setCornerProofCitation(String cornerProofCitation) { this.cornerProofCitation = cornerProofCitation; }

    public String getDtcpProofCitation() { return dtcpProofCitation; }
    public void setDtcpProofCitation(String dtcpProofCitation) { this.dtcpProofCitation = dtcpProofCitation; }

    public String getEcNumber() { return ecNumber; }
    public void setEcNumber(String ecNumber) { this.ecNumber = ecNumber; }

    public List<EcRecordEntry> getEcEntries() { return ecEntries; }
    public void setEcEntries(List<EcRecordEntry> ecEntries) { this.ecEntries = ecEntries; }

    public List<TitleDeedLink> getTitleDeedChain() { return titleDeedChain; }
    public void setTitleDeedChain(List<TitleDeedLink> titleDeedChain) { this.titleDeedChain = titleDeedChain; }

    public Map<String, String> getOfficialPortalLinks() { return officialPortalLinks; }
    public void setOfficialPortalLinks(Map<String, String> officialPortalLinks) { this.officialPortalLinks = officialPortalLinks; }
}
