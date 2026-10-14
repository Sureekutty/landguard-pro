package com.landguard.dto.property;

import java.util.List;
import java.util.Map;

public class ComparisonReportDto {

    public enum FieldStatus {
        MATCH,
        MISMATCH,
        PARTIAL_MATCH,
        MISSING,
        NOT_SUPPORTED,
        SOURCE_UNAVAILABLE,
        NOT_VERIFIED
    }

    public static class FieldComparisonResult {
        private String fieldName;
        private FieldStatus status;
        private String revenueValue;
        private String registrationValue;
        private String reason;

        public FieldComparisonResult() {}

        public FieldComparisonResult(String fieldName, FieldStatus status, String revenueValue, String registrationValue, String reason) {
            this.fieldName = fieldName;
            this.status = status;
            this.revenueValue = revenueValue;
            this.registrationValue = registrationValue;
            this.reason = reason;
        }

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public FieldStatus getStatus() { return status; }
        public void setStatus(FieldStatus status) { this.status = status; }

        public String getRevenueValue() { return revenueValue; }
        public void setRevenueValue(String revenueValue) { this.revenueValue = revenueValue; }

        public String getRegistrationValue() { return registrationValue; }
        public void setRegistrationValue(String registrationValue) { this.registrationValue = registrationValue; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    private String propertyId;
    private String overallVerdict; // "NO_DISCREPANCY_DETECTED", "POTENTIAL_DISCREPANCY_REQUIRING_INVESTIGATION", "INSUFFICIENT_EVIDENCE"
    private List<FieldComparisonResult> fieldComparisons;
    private String legalAdvisoryNotice;
    private Map<String, Object> summary;

    public ComparisonReportDto() {}

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getOverallVerdict() { return overallVerdict; }
    public void setOverallVerdict(String overallVerdict) { this.overallVerdict = overallVerdict; }

    public List<FieldComparisonResult> getFieldComparisons() { return fieldComparisons; }
    public void setFieldComparisons(List<FieldComparisonResult> fieldComparisons) { this.fieldComparisons = fieldComparisons; }

    public String getLegalAdvisoryNotice() { return legalAdvisoryNotice; }
    public void setLegalAdvisoryNotice(String legalAdvisoryNotice) { this.legalAdvisoryNotice = legalAdvisoryNotice; }

    public Map<String, Object> getSummary() { return summary; }
    public void setSummary(Map<String, Object> summary) { this.summary = summary; }
}
