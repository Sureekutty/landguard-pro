package com.landguard.provider;

import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.entity.RetrievedPropertyRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DemoDataProvider implements LandRecordProvider {

    private final List<RetrievedPropertyRecord> demoParcels = new ArrayList<>();

    public DemoDataProvider() {
        initDemoParcels();
    }

    private void initDemoParcels() {
        // Strictly 1 sample demo parcel for educational explanation only
        demoParcels.add(createDemo("DEMO-1", "999", "DEMO1", "999/DEMO1", "DEMO-01",
                "Sample Benchmark Holding (Educational Reference)", "sample benchmark holding", "Training Registry", "ரயத்து புஞ்சை (Training Sample)",
                "0.02.10", 2260.0, 5.19, 1100.0, 750.0, true, "DTCP Sanctioned Layout Sample", true, "Clear 30-Year Ancestral Chain"));
    }

    private RetrievedPropertyRecord createDemo(
            String id, String survey, String subdiv, String fullSurvey, String patta,
            String ownerRaw, String ownerNorm, String father, String classification,
            String extentRaw, double sqft, double cents, double askingRate, double guidelineRate,
            boolean dtcpApproved, String dtcpStatus, boolean ecClean, String ecStatus) {
        RetrievedPropertyRecord r = new RetrievedPropertyRecord();
        r.setId(id);
        r.setSourceId("demo-data");
        r.setDistrict("Namakkal");
        r.setTaluk("Rasipuram");
        r.setVillage("Gurusamipalayam");
        r.setSurveyNumber(survey);
        r.setSubdivision(subdiv);
        r.setFullSurvey(fullSurvey);
        r.setPattaNumber(patta);
        r.setOwnerNameRaw(ownerRaw);
        r.setOwnerNameNormalized(ownerNorm);
        r.setFatherName(father);
        r.setLandClassification(classification);
        r.setExtentRaw(extentRaw);
        r.setExtentSqFt(sqft);
        r.setExtentCents(cents);
        r.setAskingRate(askingRate);
        r.setGuidelineRate(guidelineRate);
        r.setDtcpApproved(dtcpApproved);
        r.setDtcpStatus(dtcpStatus);
        r.setEcClean(ecClean);
        r.setEcStatus(ecStatus);
        r.setDemoData(true);
        r.setEvidenceStatus("DEMO DATA — NOT AN OFFICIAL RECORD");
        r.setEvidenceCitation("[TEST FIXTURE] Mock procedural record for development & demonstration only.");
        r.setRetrievalTimestamp(LocalDateTime.now());
        return r;
    }

    @Override
    public String getProviderId() {
        return "demo-data";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        return new ProviderCapabilities(
                "demo-data",
                "LandGuard Demonstration Test Fixtures",
                Arrays.asList("Demo Revenue Records", "Demo EC Entries", "Demo Cadastre"),
                Arrays.asList("district", "taluk", "village", "surveyNumber", "subdivision", "pattaNumber", "ownerName"),
                true,
                false
        );
    }

    @Override
    public ProviderSearchResult search(PropertySearchRequest request) {
        if (!request.isIncludeDemoData()) {
            return ProviderSearchResult.noRecords(getProviderId(), "Demo provider inactive. To inspect demo records, enable includeDemoData flag.");
        }

        String q = request.getQuery() != null ? request.getQuery().trim().toLowerCase() : "";
        String ownerQ = request.getOwnerName() != null ? request.getOwnerName().trim().toLowerCase() : q;
        String surveyQ = request.getSurveyNumber() != null ? request.getSurveyNumber().trim() : q;
        String pattaQ = request.getPattaNumber() != null ? request.getPattaNumber().trim() : q;
        String mode = request.getSearchMode() != null ? request.getSearchMode().toUpperCase() : "ALL";

        List<RetrievedPropertyRecord> matched = new ArrayList<>();

        for (RetrievedPropertyRecord r : demoParcels) {
            boolean match = false;
            if (mode.equals("NAME") && !ownerQ.isEmpty()) {
                match = r.getOwnerNameRaw().toLowerCase().contains(ownerQ) || r.getOwnerNameNormalized().contains(ownerQ);
            } else if (mode.equals("SURVEY") && !surveyQ.isEmpty()) {
                match = r.getSurveyNumber().equalsIgnoreCase(surveyQ) || r.getFullSurvey().equalsIgnoreCase(surveyQ);
            } else if (mode.equals("PATTA") && !pattaQ.isEmpty()) {
                match = r.getPattaNumber().equalsIgnoreCase(pattaQ);
            } else if (mode.equals("ALL") || q.isEmpty()) {
                match = true;
            } else {
                match = r.getOwnerNameRaw().toLowerCase().contains(q) ||
                        r.getSurveyNumber().equalsIgnoreCase(q) ||
                        r.getFullSurvey().equalsIgnoreCase(q) ||
                        r.getPattaNumber().equalsIgnoreCase(q);
            }

            if (match) {
                matched.add(r);
            }
        }

        if (matched.isEmpty()) {
            // Core Rule: NEVER fabricate records to make searches appear successful!
            return ProviderSearchResult.noRecords(getProviderId(), "No matching record returned by the selected source.");
        }

        return ProviderSearchResult.success(
                getProviderId(),
                matched,
                "DEMO DATA — NOT AN OFFICIAL RECORD. Retrieved from local development test fixtures."
        );
    }

    @Override
    public ProviderHealthStatus checkHealth() {
        return new ProviderHealthStatus(true, 200, 1L, "Demo fixture provider active");
    }
}
