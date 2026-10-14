package com.landguard.model;

public class TitleDeedLink {
    private String period;
    private String documentNo;
    private String sro;
    private String titleHolder;
    private String natureOfTransmission;
    private String extent;
    private String chainStatus;

    public TitleDeedLink() {}

    public TitleDeedLink(String period, String documentNo, String sro,
                         String titleHolder, String natureOfTransmission,
                         String extent, String chainStatus) {
        this.period = period;
        this.documentNo = documentNo;
        this.sro = sro;
        this.titleHolder = titleHolder;
        this.natureOfTransmission = natureOfTransmission;
        this.extent = extent;
        this.chainStatus = chainStatus;
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getDocumentNo() { return documentNo; }
    public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }

    public String getSro() { return sro; }
    public void setSro(String sro) { this.sro = sro; }

    public String getTitleHolder() { return titleHolder; }
    public void setTitleHolder(String titleHolder) { this.titleHolder = titleHolder; }

    public String getNatureOfTransmission() { return natureOfTransmission; }
    public void setNatureOfTransmission(String natureOfTransmission) { this.natureOfTransmission = natureOfTransmission; }

    public String getExtent() { return extent; }
    public void setExtent(String extent) { this.extent = extent; }

    public String getChainStatus() { return chainStatus; }
    public void setChainStatus(String chainStatus) { this.chainStatus = chainStatus; }
}
