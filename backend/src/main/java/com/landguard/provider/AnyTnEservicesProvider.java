package com.landguard.provider;

import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.repository.RetrievedPropertyRecordRepository;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
public class AnyTnEservicesProvider implements LandRecordProvider {

    private final RetrievedPropertyRecordRepository recordRepository;

    public AnyTnEservicesProvider(RetrievedPropertyRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public String getProviderId() {
        return "anytn-eservices";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        return new ProviderCapabilities(
                "anytn-eservices",
                "AnyTN e-Services (Revenue Dept - Tamil Nilam)",
                Arrays.asList("Patta Extract", "Chitta Extract", "FMB Sketch", "TSLR Extract", "A-Register"),
                Arrays.asList("district", "taluk", "village", "surveyNumber", "subdivision", "pattaNumber"),
                false, // Live unrestricted API without CAPTCHA is not publicly available
                true   // Manual citizen verification required on official portal
        );
    }

    @Override
    public ProviderSearchResult search(PropertySearchRequest request) {
        // Query stored verified/retained records for this source if any exist
        List<RetrievedPropertyRecord> stored = recordRepository.searchProperties(
                request.getDistrict(),
                request.getTaluk(),
                request.getVillage(),
                request.getSurveyNumber(),
                request.getSubdivision(),
                request.getPattaNumber(),
                request.getOwnerName()
        ).stream().filter(r -> "anytn-eservices".equals(r.getSourceId()) && !r.isDemoData()).toList();

        if (!stored.isEmpty()) {
            return ProviderSearchResult.success(
                    getProviderId(),
                    stored,
                    "Retrieved from verified revenue database cache (AnyTN Revenue Extract)."
            );
        }

        // Truth in integration: Official portal requires interactive Tamil CAPTCHA verification
        return ProviderSearchResult.manualRequired(
                getProviderId(),
                "Official AnyTN portal requires interactive Tamil CAPTCHA verification. Automated scraping is prohibited by NIC policy. Please use the safe 'Open Official Portal' link with the Quick Copy values."
        );
    }

    @Override
    public ProviderHealthStatus checkHealth() {
        long start = System.currentTimeMillis();
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create("https://eservices.tn.gov.in/").toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "LandGuardPro/2.0");
            int code = conn.getResponseCode();
            long latency = System.currentTimeMillis() - start;
            return new ProviderHealthStatus(code >= 200 && code < 400, code, latency, "Portal reachable");
        } catch (Exception ex) {
            return new ProviderHealthStatus(false, 503, System.currentTimeMillis() - start, ex.getMessage());
        }
    }
}
