package com.landguard.service;

import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.dto.property.PropertySearchResponse;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.provider.LandRecordProvider;
import com.landguard.provider.ProviderSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PropertySearchService {

    private static final Logger log = LoggerFactory.getLogger(PropertySearchService.class);

    private final List<LandRecordProvider> providers;

    public PropertySearchService(List<LandRecordProvider> providers) {
        this.providers = providers;
    }

    public PropertySearchResponse search(PropertySearchRequest request, String userId) {
        // 1. Normalize Search Input
        normalizeRequest(request);

        String searchId = "SRCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, String> sourceStatuses = new LinkedHashMap<>();
        List<RetrievedPropertyRecord> aggregatedRecords = new ArrayList<>();

        // 2. Query each relevant provider
        for (LandRecordProvider provider : providers) {
            String pId = provider.getProviderId();

            // Skip demo provider if not explicitly enabled
            if ("demo-data".equals(pId) && !request.isIncludeDemoData()) {
                continue;
            }

            // If specific target sources were requested, filter
            if (request.getTargetSources() != null && !request.getTargetSources().isEmpty() && !request.getTargetSources().contains(pId)) {
                continue;
            }

            try {
                ProviderSearchResult result = provider.search(request);
                sourceStatuses.put(pId, result.getStatus().name() + ": " + result.getMessage());

                if (result.getRecords() != null && !result.getRecords().isEmpty()) {
                    aggregatedRecords.addAll(result.getRecords());
                }
            } catch (Exception ex) {
                log.error("Provider '{}' query error: {}", pId, ex.getMessage());
                sourceStatuses.put(pId, "ERROR: " + ex.getMessage());
            }
        }

        // 3. Assemble Response
        PropertySearchResponse resp = new PropertySearchResponse();
        resp.setSearchId(searchId);
        resp.setRetrievalTimestamp(LocalDateTime.now());
        resp.setSourceStatuses(sourceStatuses);
        resp.setRecords(aggregatedRecords);
        resp.setTotalRecordsFound(aggregatedRecords.size());

        Map<String, Object> querySummary = new LinkedHashMap<>();
        querySummary.put("district", request.getDistrict());
        querySummary.put("taluk", request.getTaluk());
        querySummary.put("village", request.getVillage());
        querySummary.put("surveyNumber", request.getSurveyNumber());
        querySummary.put("subdivision", request.getSubdivision());
        querySummary.put("pattaNumber", request.getPattaNumber());
        querySummary.put("ownerName", request.getOwnerName());
        querySummary.put("searchMode", request.getSearchMode());
        querySummary.put("includeDemoData", request.isIncludeDemoData());
        resp.setQuerySummary(querySummary);

        // Core Requirement: Truthful confidence & advisory notice
        if (aggregatedRecords.isEmpty()) {
            resp.setMatchConfidence("NO_RECORDS");
        } else {
            boolean hasDemo = aggregatedRecords.stream().anyMatch(RetrievedPropertyRecord::isDemoData);
            resp.setMatchConfidence(hasDemo ? "DEMO_FIXTURE_MATCH" : "CACHED_OFFICIAL_RECORD_MATCH");
        }

        resp.setFurtherVerificationRequired(true);
        resp.setLegalAdvisoryNotice(
                "ADVISORY: Information retrieved through LandGuard Pro is compiled for due diligence preparation only. " +
                "Under Tamil Nadu Land Registration rules, automated matches do not constitute legal certification. " +
                "Always verify certified copies directly with the Taluk Tahsildar (AnyTN) and Sub-Registrar Office (TNREGINET)."
        );

        return resp;
    }

    private void normalizeRequest(PropertySearchRequest req) {
        if (req.getDistrict() != null) req.setDistrict(capitalize(req.getDistrict().trim()));
        if (req.getTaluk() != null) req.setTaluk(capitalize(req.getTaluk().trim()));
        if (req.getVillage() != null) req.setVillage(capitalize(req.getVillage().trim()));

        // Parse combined survey strings like "142/2A" or "142 - 2A" into survey and subdivision
        if (req.getSurveyNumber() != null) {
            String s = req.getSurveyNumber().trim();
            if (s.contains("/")) {
                String[] parts = s.split("/");
                req.setSurveyNumber(parts[0].trim());
                if (parts.length > 1 && (req.getSubdivision() == null || req.getSubdivision().isEmpty())) {
                    req.setSubdivision(parts[1].trim());
                }
            } else if (s.contains("-")) {
                String[] parts = s.split("-");
                req.setSurveyNumber(parts[0].trim());
                if (parts.length > 1 && (req.getSubdivision() == null || req.getSubdivision().isEmpty())) {
                    req.setSubdivision(parts[1].trim());
                }
            } else {
                req.setSurveyNumber(s);
            }
        }

        if (req.getSubdivision() != null) req.setSubdivision(req.getSubdivision().trim().toUpperCase());
        if (req.getPattaNumber() != null) req.setPattaNumber(req.getPattaNumber().trim());
        if (req.getOwnerName() != null) req.setOwnerName(req.getOwnerName().trim());
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + (str.length() > 1 ? str.substring(1).toLowerCase() : "");
    }
}
