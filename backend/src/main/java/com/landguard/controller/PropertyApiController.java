package com.landguard.controller;

import com.landguard.dto.property.ComparisonReportDto;
import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.dto.property.PropertySearchResponse;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.model.CostCalculationResult;
import com.landguard.model.PropertyDossier;
import com.landguard.model.VerificationResult;
import com.landguard.provider.DemoDataProvider;
import com.landguard.provider.LandRecordProvider;
import com.landguard.provider.ProviderCapabilities;
import com.landguard.service.CalculationService;
import com.landguard.service.DossierService;
import com.landguard.service.PdfVerificationService;
import com.landguard.service.PropertyMatchingService;
import com.landguard.service.PropertySearchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyApiController {

    private final PropertySearchService searchService;
    private final PropertyMatchingService matchingService;
    private final CalculationService calculationService;
    private final PdfVerificationService pdfVerificationService;
    private final DossierService dossierService;
    private final List<LandRecordProvider> providers;
    private final DemoDataProvider demoDataProvider;

    public PropertyApiController(
            PropertySearchService searchService,
            PropertyMatchingService matchingService,
            CalculationService calculationService,
            PdfVerificationService pdfVerificationService,
            DossierService dossierService,
            List<LandRecordProvider> providers,
            DemoDataProvider demoDataProvider) {
        this.searchService = searchService;
        this.matchingService = matchingService;
        this.calculationService = calculationService;
        this.pdfVerificationService = pdfVerificationService;
        this.dossierService = dossierService;
        this.providers = providers;
        this.demoDataProvider = demoDataProvider;
    }

    @GetMapping("/search-capabilities")
    public ResponseEntity<Map<String, Object>> getCapabilities() {
        List<ProviderCapabilities> caps = providers.stream()
                .map(LandRecordProvider::getCapabilities)
                .toList();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("supportedDistricts", Arrays.asList("Namakkal", "Salem", "Erode", "Coimbatore", "Tiruppur", "Tiruchirappalli"));
        resp.put("primaryTaluk", "Rasipuram");
        resp.put("primaryVillage", "Gurusamipalayam");
        resp.put("supportedSearchModes", Arrays.asList("ALL", "NAME", "SURVEY", "PATTA"));
        resp.put("providerCapabilities", caps);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/search")
    public ResponseEntity<PropertySearchResponse> searchProperties(
            @Valid @RequestBody PropertySearchRequest request,
            Authentication auth) {
        String userId = auth != null ? auth.getName() : null;
        PropertySearchResponse resp = searchService.search(request, userId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetrievedPropertyRecord> getPropertyById(@PathVariable String id) {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setIncludeDemoData(true);
        req.setSearchMode("SURVEY");
        req.setSurveyNumber(id);

        PropertySearchResponse resp = searchService.search(req, null);
        if (!resp.getRecords().isEmpty()) {
            return ResponseEntity.ok(resp.getRecords().get(0));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<Map<String, Object>> getEvidence(@PathVariable String id) {
        RetrievedPropertyRecord rec = findRecordById(id);
        if (rec == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("found", false));
        }

        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("propertyId", rec.getId());
        evidence.put("fullSurvey", rec.getFullSurvey());
        evidence.put("evidenceStatus", rec.getEvidenceStatus());
        evidence.put("evidenceCitation", rec.getEvidenceCitation());
        evidence.put("isDemoData", rec.isDemoData());
        evidence.put("retrievalTimestamp", rec.getRetrievalTimestamp());
        evidence.put("officialPortals", Arrays.asList(
                Map.of("name", "AnyTN e-Services", "url", "https://eservices.tn.gov.in/", "purpose", "Patta / Chitta / FMB Extract"),
                Map.of("name", "TNREGINET", "url", "https://tnreginet.gov.in/", "purpose", "Encumbrance Certificate & Guideline Values"),
                Map.of("name", "DTCP / OnlinePPA", "url", "https://onlineppa.tn.gov.in/", "purpose", "Section 22-A Layout Sanctions")
        ));
        return ResponseEntity.ok(evidence);
    }

    @PostMapping("/{id}/compare")
    public ResponseEntity<ComparisonReportDto> compareProperty(@PathVariable String id) {
        RetrievedPropertyRecord rec = findRecordById(id);
        if (rec == null) {
            rec = new RetrievedPropertyRecord();
            rec.setId(id);
            rec.setFullSurvey(id);
            rec.setSurveyNumber(id);
        }
        ComparisonReportDto report = matchingService.compareRecords(rec);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/calculate-outflow")
    public ResponseEntity<CostCalculationResult> calculateOutflow(
            @RequestParam(defaultValue = "1650") double sqFt,
            @RequestParam(defaultValue = "1100") double ratePerSqFt) {
        return ResponseEntity.ok(calculationService.calculateTotalCost(sqFt, ratePerSqFt));
    }

    @PostMapping("/verify-document")
    public ResponseEntity<VerificationResult> verifyDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "surveyNo", required = false) String surveyNo,
            @RequestParam(value = "ownerName", required = false) String ownerName) {
        VerificationResult result = pdfVerificationService.verifyDocument(file, surveyNo, ownerName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dossiers")
    public ResponseEntity<List<PropertyDossier>> getDossiers() {
        return ResponseEntity.ok(dossierService.getAllDossiers());
    }

    @PostMapping("/dossiers")
    public ResponseEntity<PropertyDossier> saveDossier(@RequestBody PropertyDossier dossier) {
        return ResponseEntity.ok(dossierService.save(dossier));
    }

    @DeleteMapping("/dossiers/{id}")
    public ResponseEntity<Map<String, Object>> deleteDossier(@PathVariable String id) {
        boolean deleted = dossierService.deleteById(id);
        return ResponseEntity.ok(Map.of("success", deleted, "id", id));
    }

    private RetrievedPropertyRecord findRecordById(String id) {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setIncludeDemoData(true);
        req.setQuery(id);
        PropertySearchResponse resp = searchService.search(req, null);
        for (RetrievedPropertyRecord r : resp.getRecords()) {
            if (id.equalsIgnoreCase(r.getId()) || id.equalsIgnoreCase(r.getSubdivision()) || id.equalsIgnoreCase(r.getFullSurvey())) {
                return r;
            }
        }
        return resp.getRecords().isEmpty() ? null : resp.getRecords().get(0);
    }
}
