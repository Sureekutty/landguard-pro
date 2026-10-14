package com.landguard.model;

public class EcRecordEntry {
    private String docNumber;
    private String regDate;
    private String natureOfDocument;
    private String executant;
    private String claimant;
    private String extent;
    private String considerationValue;
    private String sroOffice;
    private String statusNotes;

    public EcRecordEntry() {}

    public EcRecordEntry(String docNumber, String regDate, String natureOfDocument,
                         String executant, String claimant, String extent,
                         String considerationValue, String sroOffice, String statusNotes) {
        this.docNumber = docNumber;
        this.regDate = regDate;
        this.natureOfDocument = natureOfDocument;
        this.executant = executant;
        this.claimant = claimant;
        this.extent = extent;
        this.considerationValue = considerationValue;
        this.sroOffice = sroOffice;
        this.statusNotes = statusNotes;
    }

    public String getDocNumber() { return docNumber; }
    public void setDocNumber(String docNumber) { this.docNumber = docNumber; }

    public String getRegDate() { return regDate; }
    public void setRegDate(String regDate) { this.regDate = regDate; }

    public String getNatureOfDocument() { return natureOfDocument; }
    public void setNatureOfDocument(String natureOfDocument) { this.natureOfDocument = natureOfDocument; }

    public String getExecutant() { return executant; }
    public void setExecutant(String executant) { this.executant = executant; }

    public String getClaimant() { return claimant; }
    public void setClaimant(String claimant) { this.claimant = claimant; }

    public String getExtent() { return extent; }
    public void setExtent(String extent) { this.extent = extent; }

    public String getConsiderationValue() { return considerationValue; }
    public void setConsiderationValue(String considerationValue) { this.considerationValue = considerationValue; }

    public String getSroOffice() { return sroOffice; }
    public void setSroOffice(String sroOffice) { this.sroOffice = sroOffice; }

    public String getStatusNotes() { return statusNotes; }
    public void setStatusNotes(String statusNotes) { this.statusNotes = statusNotes; }
}
