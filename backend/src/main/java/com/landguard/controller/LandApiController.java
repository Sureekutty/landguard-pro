package com.landguard.controller;

import com.landguard.model.CostCalculationResult;
import com.landguard.model.LandInspectionReport;
import com.landguard.model.LandInspectionRequest;
import com.landguard.model.PortalStatus;
import com.landguard.model.PropertyDossier;
import com.landguard.model.SurveyRecord;
import com.landguard.service.SurveyDataService;
import com.landguard.model.VerificationResult;
import com.landguard.service.CalculationService;
import com.landguard.service.DossierService;
import com.landguard.service.LandInspectionService;
import com.landguard.service.PortalMonitorService;
import com.landguard.service.PdfVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LandApiController {

    private final CalculationService calculationService;
    private final PortalMonitorService portalMonitorService;
    private final PdfVerificationService pdfVerificationService;
    private final DossierService dossierService;
    private final LandInspectionService landInspectionService;
    private final SurveyDataService surveyDataService;

    public LandApiController(CalculationService calculationService,
                             PortalMonitorService portalMonitorService,
                             PdfVerificationService pdfVerificationService,
                             DossierService dossierService,
                             LandInspectionService landInspectionService,
                             SurveyDataService surveyDataService) {
        this.calculationService = calculationService;
        this.portalMonitorService = portalMonitorService;
        this.pdfVerificationService = pdfVerificationService;
        this.dossierService = dossierService;
        this.landInspectionService = landInspectionService;
        this.surveyDataService = surveyDataService;
    }

    @PostMapping("/inspect")
    public ResponseEntity<LandInspectionReport> inspectLand(@RequestBody LandInspectionRequest req) {
        return ResponseEntity.ok(landInspectionService.inspectLand(req));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.getOrDefault("username", "");
        String password = credentials.getOrDefault("password", "");
        
        Map<String, Object> resp = new HashMap<>();
        if (!username.trim().isEmpty() && !password.trim().isEmpty()) {
            resp.put("success", true);
            resp.put("username", username.trim());
            resp.put("role", "LAND_INVESTOR");
            resp.put("token", "TOKEN-" + System.currentTimeMillis());
            return ResponseEntity.ok(resp);
        }
        resp.put("success", false);
        resp.put("message", "Please enter both username and password.");
        return ResponseEntity.badRequest().body(resp);
    }

    @GetMapping("/calculate")
    public ResponseEntity<CostCalculationResult> calculate(
            @RequestParam(defaultValue = "1650") double sqFt,
            @RequestParam(defaultValue = "1200") double ratePerSqFt) {
        return ResponseEntity.ok(calculationService.calculateTotalCost(sqFt, ratePerSqFt));
    }

    @RequestMapping(value = "/dtcp-calculator", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> calculateDtcpRegularisation(
            @RequestParam double sqFt,
            @RequestParam(defaultValue = "Village Panchayat") String localBodyType) {
        
        // Exact official Tamil Nadu statutory formula under G.O. Ms. No. 78 H&UD Dept & G.O. Ms. No. 172
        // 1 Sq. Meter = 10.7639 Sq. Feet
        double sqMeters = Math.round((sqFt / 10.7639) * 100.0) / 100.0;
        
        double regRatePerSqM;
        double devRatePerSqM;
        
        if (localBodyType.contains("Corporation")) {
            regRatePerSqM = 100.0;
            devRatePerSqM = 500.0;
        } else if (localBodyType.contains("Municipality")) {
            regRatePerSqM = 60.0;
            devRatePerSqM = 150.0;
        } else if (localBodyType.contains("Town Panchayat")) {
            regRatePerSqM = 30.0;
            devRatePerSqM = 75.0;
        } else {
            // Village Panchayat (e.g. Gurusamipalayam)
            regRatePerSqM = 30.0;
            devRatePerSqM = 25.0;
        }

        double regCharges = Math.round(sqMeters * regRatePerSqM);
        double devCharges = Math.round(sqMeters * devRatePerSqM);
        double scrutinyFee = 500.0; // Flat Rule 5(2) onlineppa.tn.gov.in
        double totalGovtFee = regCharges + devCharges + scrutinyFee;

        Map<String, Object> resp = new HashMap<>();
        resp.put("sqFt", sqFt);
        resp.put("sqMeters", sqMeters);
        resp.put("localBodyType", localBodyType);
        resp.put("regularisationRatePerSqM", regRatePerSqM);
        resp.put("regularisationCharges", regCharges);
        resp.put("developmentRatePerSqM", devRatePerSqM);
        resp.put("developmentCharges", devCharges);
        resp.put("scrutinyFee", scrutinyFee);
        resp.put("totalGovtFee", totalGovtFee);
        resp.put("officialGovOrder", "G.O. Ms. No. 78 H&UD Dept dt 04.05.2017 & G.O. Ms. No. 172 dt 13.10.2017");
        resp.put("portalSource", "https://onlineppa.tn.gov.in (Rule 5 & Rule 9)");
        resp.put("legalAdvice", "Under Section 22-A of Registration Act, the Sub-Registrar cannot register resale in 2-3 years without in-principle regularisation order. Require seller to deduct ₹ " + totalGovtFee + " or provide regularisation order before registration.");
        
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/portals")
    public ResponseEntity<List<PortalStatus>> getPortals() {
        return ResponseEntity.ok(portalMonitorService.checkAllPortals());
    }

    @GetMapping("/validate-link")
    public ResponseEntity<Map<String, Object>> validateLink(@RequestParam String url) {
        boolean official = portalMonitorService.isOfficialDomain(url);
        Map<String, Object> resp = new HashMap<>();
        resp.put("url", url);
        resp.put("isOfficialGovDomain", official);
        resp.put("status", official ? "TRUSTED_GOVERNMENT_PORTAL" : "WARNING_UNVERIFIED_THIRD_PARTY_OR_PHISHING");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/verify-doc")
    public ResponseEntity<VerificationResult> verifyDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("surveyNo") String surveyNo,
            @RequestParam("ownerName") String ownerName) {
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

    @DeleteMapping("/dossiers")
    public ResponseEntity<Map<String, Object>> clearAllDossiers() {
        dossierService.clearAll();
        return ResponseEntity.ok(Map.of("success", true, "message", "All stored dossiers cleared successfully."));
    }

    
    @GetMapping("/search-parcels")
    public ResponseEntity<List<SurveyRecord>> searchParcels(
            @RequestParam(defaultValue = "Namakkal") String district,
            @RequestParam(defaultValue = "Rasipuram") String taluk,
            @RequestParam(defaultValue = "Gurusamipalayam") String village,
            @RequestParam(defaultValue = "ALL") String searchMode,
            @RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(surveyDataService.searchParcels(district, taluk, village, searchMode, query));
    }

    @GetMapping("/survey-records")
    public ResponseEntity<List<SurveyRecord>> getSurveyRecords(
            @RequestParam(defaultValue = "Namakkal") String district,
            @RequestParam(defaultValue = "Rasipuram") String taluk,
            @RequestParam(defaultValue = "Gurusamipalayam") String village,
            @RequestParam(defaultValue = "142") String surveyNumber) {
        return ResponseEntity.ok(surveyDataService.getRecordsForSurvey(district, taluk, village, surveyNumber));
    }

    @GetMapping("/survey-detail")
    public ResponseEntity<SurveyRecord> getSurveyDetail(
            @RequestParam(defaultValue = "Namakkal") String district,
            @RequestParam(defaultValue = "Rasipuram") String taluk,
            @RequestParam(defaultValue = "Gurusamipalayam") String village,
            @RequestParam(defaultValue = "142") String surveyNumber,
            @RequestParam(defaultValue = "2A") String subDivision) {
        return ResponseEntity.ok(surveyDataService.getRecordBySubDivision(district, taluk, village, surveyNumber, subDivision));
    }

    @PostMapping("/dossiers/save-from-survey")
    public ResponseEntity<PropertyDossier> saveFromSurvey(@RequestBody SurveyRecord rec) {
        PropertyDossier d = new PropertyDossier();
        d.setId("REC-" + System.currentTimeMillis() % 1000000);
        d.setDistrict(rec.getDistrict());
        d.setTaluk(rec.getTaluk());
        d.setVillage(rec.getVillage());
        d.setSurveyNumber(rec.getSurveyNumber());
        d.setSubDivision(rec.getSubDivision());
        d.setClaimedOwner(rec.getOwnerNameEn() + " (" + rec.getOwnerNameTa() + ")");
        d.setExtentSqFt(rec.getExtentSqFt());
        d.setAskingRate(rec.getAskingRate());
        d.setDtcpApproved(rec.isDtcpApproved());
        d.setEcClean30Years(rec.isEcClean());
        d.setPattaOwnerMatch(true);
        d.setStatusNotes("Patta No: " + rec.getPattaNumber() + " | Classification: " + rec.getClassification() + " | DTCP: " + rec.getDtcpStatus());
        return ResponseEntity.ok(dossierService.save(d));
    }
}
