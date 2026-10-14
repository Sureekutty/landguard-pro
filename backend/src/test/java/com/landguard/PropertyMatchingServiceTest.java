package com.landguard;

import com.landguard.dto.property.ComparisonReportDto;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.service.PropertyMatchingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PropertyMatchingServiceTest {

    @Autowired
    private PropertyMatchingService matchingService;

    @Test
    @DisplayName("Cross-Verification: Verified plot with DTCP sanction and clean EC returns NO_DISCREPANCY_DETECTED")
    void testCleanPropertyComparison() {
        RetrievedPropertyRecord rec = new RetrievedPropertyRecord();
        rec.setId("REC-CLEAN-1");
        rec.setSurveyNumber("142");
        rec.setSubdivision("3B");
        rec.setFullSurvey("142/3B");
        rec.setOwnerNameRaw("Sureendhar J (சுரேந்தர்)");
        rec.setFatherName("Jagannathan");
        rec.setExtentSqFt(1650.0);
        rec.setDtcpApproved(true);
        rec.setDtcpStatus("DTCP Regularised Order No. 452/2018");
        rec.setEcClean(true);
        rec.setEcStatus("Clean 30-Year Ancestral Chain");

        ComparisonReportDto report = matchingService.compareRecords(rec);

        assertNotNull(report);
        assertEquals("NO_DISCREPANCY_DETECTED_IN_RECORDS_EXAMINED", report.getOverallVerdict());
        assertFalse(report.getFieldComparisons().isEmpty());
        assertTrue(report.getFieldComparisons().stream().allMatch(f ->
                f.getStatus() == ComparisonReportDto.FieldStatus.MATCH
        ));
    }

    @Test
    @DisplayName("Cross-Verification: Unapproved plot triggers POTENTIAL_DISCREPANCY_REQUIRING_INVESTIGATION for Section 22-A Bar")
    void testUnapprovedPlotTriggersDiscrepancy() {
        RetrievedPropertyRecord rec = new RetrievedPropertyRecord();
        rec.setId("REC-UNAPPROVED-1");
        rec.setSurveyNumber("142");
        rec.setSubdivision("2A");
        rec.setFullSurvey("142/2A");
        rec.setOwnerNameRaw("Periyasamy G (பெரியசாமி)");
        rec.setExtentSqFt(1650.0);
        rec.setDtcpApproved(false); // Section 22-A Barred!
        rec.setDtcpStatus("Unapproved Residential Layout");
        rec.setEcClean(true);

        ComparisonReportDto report = matchingService.compareRecords(rec);

        assertNotNull(report);
        assertEquals("POTENTIAL_DISCREPANCY_REQUIRING_INVESTIGATION", report.getOverallVerdict());
        assertTrue(report.getFieldComparisons().stream().anyMatch(f ->
                f.getFieldName().contains("DTCP") && f.getStatus() == ComparisonReportDto.FieldStatus.MISMATCH
        ), "Must flag DTCP unapproved status as MISMATCH");
    }

    @Test
    @DisplayName("Cross-Verification: Missing extent triggers INSUFFICIENT_EVIDENCE")
    void testMissingExtentTriggersMissingEvidence() {
        RetrievedPropertyRecord rec = new RetrievedPropertyRecord();
        rec.setId("REC-MISSING-1");
        rec.setSurveyNumber("142");
        rec.setSubdivision("1");
        rec.setFullSurvey("142/1");
        rec.setOwnerNameRaw("Kandasamy R (கந்தசாமி)");
        rec.setExtentSqFt(null); // Missing extent
        rec.setDtcpApproved(true);
        rec.setEcClean(true);

        ComparisonReportDto report = matchingService.compareRecords(rec);

        assertNotNull(report);
        assertTrue(report.getFieldComparisons().stream().anyMatch(f ->
                f.getFieldName().contains("Extent") && f.getStatus() == ComparisonReportDto.FieldStatus.MISSING
        ));
    }
}
