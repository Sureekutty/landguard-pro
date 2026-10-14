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
public class TnreginetProvider implements LandRecordProvider {

    private final RetrievedPropertyRecordRepository recordRepository;

    public TnreginetProvider(RetrievedPropertyRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public String getProviderId() {
        return "tnreginet-ec";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        return new ProviderCapabilities(
                "tnreginet-ec",
                "TNREGINET (Registration Department)",
                Arrays.asList("Encumbrance Certificate (Form 15)", "Guideline Value", "Certified Document Copy"),
                Arrays.asList("zone", "district", "sro", "village", "surveyNumber", "subdivision", "documentNumber", "registrationYear"),
                false,
                true
        );
    }

    @Override
    public ProviderSearchResult search(PropertySearchRequest request) {
        List<RetrievedPropertyRecord> stored = recordRepository.searchProperties(
                request.getDistrict(),
                request.getTaluk(),
                request.getVillage(),
                request.getSurveyNumber(),
                request.getSubdivision(),
                null,
                request.getOwnerName()
        ).stream().filter(r -> "tnreginet-ec".equals(r.getSourceId()) && !r.isDemoData()).toList();

        if (!stored.isEmpty()) {
            return ProviderSearchResult.success(
                    getProviderId(),
                    stored,
                    "Retrieved from verified registration database cache (Form 15 EC Extract)."
            );
        }

        return ProviderSearchResult.manualRequired(
                getProviderId(),
                "Official TNREGINET EC search requires user authentication and CAPTCHA. Verification incomplete until searched on tnreginet.gov.in."
        );
    }

    @Override
    public ProviderHealthStatus checkHealth() {
        long start = System.currentTimeMillis();
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create("https://tnreginet.gov.in/").toURL().openConnection();
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
