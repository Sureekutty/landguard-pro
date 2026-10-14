package com.landguard.dto.property;

import com.landguard.entity.RetrievedPropertyRecord;

public class DocumentIngestionResponse {

    private boolean success;
    private String message;
    private RetrievedPropertyRecord ingestedRecord;
    private boolean digitalSignatureVerified;
    private String digitalSignatureReference;
    private String documentSha256;
    private String sourceRegistry;

    public DocumentIngestionResponse() {}

    public DocumentIngestionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RetrievedPropertyRecord getIngestedRecord() {
        return ingestedRecord;
    }

    public void setIngestedRecord(RetrievedPropertyRecord ingestedRecord) {
        this.ingestedRecord = ingestedRecord;
    }

    public boolean isDigitalSignatureVerified() {
        return digitalSignatureVerified;
    }

    public void setDigitalSignatureVerified(boolean digitalSignatureVerified) {
        this.digitalSignatureVerified = digitalSignatureVerified;
    }

    public String getDigitalSignatureReference() {
        return digitalSignatureReference;
    }

    public void setDigitalSignatureReference(String digitalSignatureReference) {
        this.digitalSignatureReference = digitalSignatureReference;
    }

    public String getDocumentSha256() {
        return documentSha256;
    }

    public void setDocumentSha256(String documentSha256) {
        this.documentSha256 = documentSha256;
    }

    public String getSourceRegistry() {
        return sourceRegistry;
    }

    public void setSourceRegistry(String sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }
}
