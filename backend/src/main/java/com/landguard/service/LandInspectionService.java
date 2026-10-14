package com.landguard.service;

import com.landguard.model.CostCalculationResult;
import com.landguard.model.LandInspectionReport;
import com.landguard.model.LandInspectionRequest;
import com.landguard.model.PortalInspectionDetail;
import com.landguard.model.PropertyDossier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LandInspectionService {

    private final CalculationService calculationService;
    private final DossierService dossierService;
    private final PortalMonitorService portalMonitorService;

    public LandInspectionService(CalculationService calculationService,
                                 DossierService dossierService,
                                 PortalMonitorService portalMonitorService) {
        this.calculationService = calculationService;
        this.dossierService = dossierService;
        this.portalMonitorService = portalMonitorService;
    }

    public LandInspectionReport inspectLand(LandInspectionRequest req) {
        LandInspectionReport report = new LandInspectionReport();
        String id = "INSP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        report.setInspectionId(id);
        report.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String district = (req.getDistrict() != null && !req.getDistrict().trim().isEmpty()) ? req.getDistrict().trim() : "Namakkal";
        String taluk = (req.getTaluk() != null && !req.getTaluk().trim().isEmpty()) ? req.getTaluk().trim() : "Rasipuram";
        String village = (req.getVillage() != null && !req.getVillage().trim().isEmpty()) ? req.getVillage().trim() : "Gurusamipalayam";
        String surveyNo = (req.getSurveyNumber() != null && !req.getSurveyNumber().trim().isEmpty()) ? req.getSurveyNumber().trim() : "142";
        String subDiv = (req.getSubDivision() != null) ? req.getSubDivision().trim() : "";
        String owner = (req.getClaimedOwner() != null) ? req.getClaimedOwner().trim() : "";
        String plotNo = (req.getPlotNumber() != null) ? req.getPlotNumber().trim() : "";
        double area = req.getLandAreaSqFt() > 0 ? req.getLandAreaSqFt() : 1650.0;
        double rate = req.getAskingRate() > 0 ? req.getAskingRate() : 1200.0;

        report.setDistrict(district);
        report.setTaluk(taluk);
        report.setVillage(village);
        report.setSurveyNumber(surveyNo);
        report.setSubDivision(subDiv);
        report.setPlotNumber(plotNo);
        report.setClaimedOwner(owner);
        report.setLandAreaSqFt(area);
        report.setCents(Math.round((area / 435.6) * 100.0) / 100.0);

        // Outflow Calculation
        CostCalculationResult cost = calculationService.calculateTotalCost(area, rate);
        report.setCostBreakdown(cost);

        // Build Portal Inspection Details
        List<PortalInspectionDetail> portals = new ArrayList<>();

        // 1. TNREGINET EC & Guideline
        portals.add(new PortalInspectionDetail(
            "TNREGINET (Registration Dept)",
            "https://tnreginet.gov.in",
            "30-Year Encumbrance Certificate (EC) Search",
            "Go to tnreginet.gov.in -> E-Services -> Encumbrance Certificate -> Search EC. Enter Zone/District: [" + district + "], SRO: [" + taluk + "], Village: [" + village + "], Survey No: [" + surveyNo + "]. Select minimum 30 years continuous range.",
            "https://tnreginet.gov.in/portal/webHP?requestType=ApplicationRH&actionVal=viewEC&queryType=Select",
            true,
            "REQUIRED: Zero mortgages, attachments, or parallel sales must appear."
        ));

        // 2. AnyTN Revenue Services (Patta / Chitta / FMB)
        portals.add(new PortalInspectionDetail(
            "AnyTN eServices (Revenue Dept)",
            "https://eservices.tn.gov.in",
            "View Patta / Chitta Extract & FMB Sketch",
            "Go to eservices.tn.gov.in -> View Patta & FMB / Chitta Extract. Select District: [" + district + "], Taluk: [" + taluk + "], Village: [" + village + "], Survey No: [" + surveyNo + "]" + (subDiv.isEmpty() ? "" : ", Sub-division: [" + subDiv + "]") + ". Check if owner matches [" + owner + "].",
            "https://eservices.tn.gov.in/eservicesnew/land/chitta.html?lan=ta",
            true,
            "REQUIRED: Computerised Patta must be in seller's name. Check FMB for public road access."
        ));

        // 3. DTCP Layout Regularisation (Section 22-A)
        portals.add(new PortalInspectionDetail(
            "DTCP Regularisation Portal",
            "https://onlineppa.tn.gov.in",
            "Verify Layout Approval & Section 22-A Status",
            "Go to onlineppa.tn.gov.in -> Layout Regularisation Scheme. Search layout in Village: [" + village + "] under Taluk: [" + taluk + "]. Confirm in-principle layout sanction number.",
            "https://onlineppa.tn.gov.in",
            true,
            req.isLayoutOrSubdivided() ? "CRITICAL: Unapproved plot CANNOT be resold in 2-3 years without DTCP approval." : "Agricultural extent exemption applies if not subdivided."
        ));

        // 4. e-Courts Judicial Case Status
        portals.add(new PortalInspectionDetail(
            "e-Courts Namakkal / Rasipuram",
            "https://services.ecourts.gov.in",
            "Civil Litigation & Injunction Search",
            "Search case status in District Court / Munsif Court for party name: [" + owner + "] or property survey no: [" + surveyNo + "] to detect pending partition suits or stay orders.",
            "https://services.ecourts.gov.in/ecourtindia_v6/",
            true,
            "ADVISORY: Clean litigation certificate gives peace of mind against family disputes."
        ));

        report.setPortalChecks(portals);

        // Risk & Advice Synthesis
        if (req.isLayoutOrSubdivided()) {
            report.setRiskLevel("ACTION_REQUIRED_DTCP");
            report.setDtcpAdvice("Since this is a residential plot / subdivided site in " + village + ", Section 22-A of the Registration Act strictly prohibits future sale deed registration without DTCP layout approval or regularisation. If you plan to sell after 2-3 years, demand the seller provide the DTCP sanction order before registration.");
        } else {
            report.setRiskLevel("STANDARD_DUE_DILIGENCE");
            report.setDtcpAdvice("Full agricultural parcel without subdivision does not require layout regularisation, but converting to plots later requires formal DTCP master layout approval.");
        }

        report.setRevenueAdvice("Verify that Survey No " + surveyNo + (subDiv.isEmpty() ? "" : "/" + subDiv) + " in " + village + " village A-Register is Punjai/Nanjai patta land and NOT classified as Poramboke (Government), Eri/Odai (Water body catchment), Temple (HR&CE), or Panchami.");
        report.setCourtAdvice("Obtain a 30-year Non-Encumbrance Certificate from Rasipuram SRO and have a local advocate conduct a litigation check in the local civil court.");

        // Action Checklist
        List<String> actions = new ArrayList<>();
        actions.add("Verify 30-year parent document chain for Survey No " + surveyNo + " with no missing links.");
        actions.add("Verify Computerised Patta on eservices.tn.gov.in matches seller: " + (owner.isEmpty() ? "Current Landholder" : owner));
        actions.add("Inspect FMB sketch at Taluk office to confirm actual physical boundary and road width.");
        actions.add("Check Village Panchayat Kist receipts to ensure no local body tax arrears.");
        actions.add("Condition the advance payment on seller furnishing the DTCP regularisation order / Panchayat sanction.");

        report.setChecklistActions(actions);

        // Auto-persist dossier to MongoDB
        PropertyDossier dossier = new PropertyDossier();
        dossier.setId(id);
        dossier.setDistrict(district);
        dossier.setTaluk(taluk);
        dossier.setVillage(village);
        dossier.setSurveyNumber(surveyNo);
        dossier.setSubDivision(subDiv);
        dossier.setClaimedOwner(owner);
        dossier.setExtentSqFt(area);
        dossier.setAskingRate(rate);
        dossier.setStatusNotes("Automated multi-portal inspection for " + village + " Survey No " + surveyNo + ". DTCP Check: " + (req.isLayoutOrSubdivided() ? "Required" : "Exempt"));
        // Dossier is saved explicitly by user action to keep database clean
        // dossierService.save(dossier);

        return report;
    }
}