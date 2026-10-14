package com.landguard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.dto.property.PropertySearchResponse;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.service.PropertySearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PropertySearchEngineTest {

    @Autowired
    private PropertySearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Honest Search: Unknown record query returns zero records with explicit notice - NEVER fabricates records")
    void testUnknownSearchReturnsNoRecords() {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setDistrict("Namakkal");
        req.setTaluk("Rasipuram");
        req.setVillage("Gurusamipalayam");
        req.setSurveyNumber("9999");
        req.setOwnerName("NonExistentOwnerXyz");
        req.setIncludeDemoData(false); // Live/Official search mode

        PropertySearchResponse resp = searchService.search(req, "testuser");

        assertNotNull(resp);
        assertEquals(0, resp.getTotalRecordsFound(), "Must return 0 records for non-existent land query");
        assertTrue(resp.getRecords().isEmpty());
        assertEquals("NO_RECORDS", resp.getMatchConfidence());
        assertTrue(resp.isFurtherVerificationRequired());
        assertNotNull(resp.getLegalAdvisoryNotice());
    }

    @Test
    @DisplayName("Search Normalization: Correctly parses compound survey strings like '142/2A'")
    void testSearchNormalization() {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setDistrict("namakkal"); // lowercase to test capitalization
        req.setTaluk("rasipuram");
        req.setVillage("gurusamipalayam");
        req.setSurveyNumber("142/2A"); // compound survey string
        req.setIncludeDemoData(true);

        PropertySearchResponse resp = searchService.search(req, "testuser");

        assertNotNull(resp);
        assertEquals("Namakkal", resp.getQuerySummary().get("district"));
        assertEquals("Rasipuram", resp.getQuerySummary().get("taluk"));
        assertEquals("Gurusamipalayam", resp.getQuerySummary().get("village"));
        assertEquals("142", resp.getQuerySummary().get("surveyNumber"));
        assertEquals("2A", resp.getQuerySummary().get("subdivision"));
    }

    @Test
    @DisplayName("Demo Fixture Transparency: Records retrieved from demo provider are explicitly tagged as DEMO DATA")
    void testDemoDataTaggedExplicitly() {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setDistrict("Namakkal");
        req.setTaluk("Rasipuram");
        req.setVillage("Gurusamipalayam");
        req.setSurveyNumber("142");
        req.setIncludeDemoData(true);
        req.setTargetSources(List.of("demo-data"));

        PropertySearchResponse resp = searchService.search(req, "testuser");

        assertNotNull(resp);
        assertFalse(resp.getRecords().isEmpty(), "Demo search should find demo parcels for survey 142");
        assertTrue(resp.getRecords().stream().allMatch(r ->
                r.isDemoData() && "DEMO DATA — NOT AN OFFICIAL RECORD".equals(r.getEvidenceStatus())
        ), "All demo records must be explicitly labeled as DEMO DATA");
    }

    @Test
    @DisplayName("Demo Mode Isolation: With includeDemoData=false, demo records are NEVER returned")
    void testDemoDataNotReturnedInOfficialMode() {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setDistrict("Namakkal");
        req.setTaluk("Rasipuram");
        req.setVillage("Gurusamipalayam");
        req.setSurveyNumber("142");
        req.setIncludeDemoData(false); // Official mode only

        PropertySearchResponse resp = searchService.search(req, "testuser");

        assertNotNull(resp);
        assertFalse(resp.getRecords().isEmpty(), "Official search returns verified government master records");
        assertTrue(resp.getRecords().stream().noneMatch(RetrievedPropertyRecord::isDemoData), "Official search must not return demo data when demo mode is disabled");
    }
}
