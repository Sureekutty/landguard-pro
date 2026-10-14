package com.landguard.provider;

import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.repository.RetrievedPropertyRecordRepository;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class DtcpProvider implements LandRecordProvider {

    private final RetrievedPropertyRecordRepository recordRepository;

    public DtcpProvider(RetrievedPropertyRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public String getProviderId() {
        return "dtcp-onlineppa";
    }

    @Override
    public ProviderCapabilities getCapabilities() {
        return new ProviderCapabilities(
                "dtcp-onlineppa",
                "DTCP / OnlinePPA (Directorate of Town and Country Planning)",
                Arrays.asList("Layout Sanctions", "Section 22-A Bar Status", "Plot Regularisation Framework"),
                Arrays.asList("localBodyType", "district", "taluk", "village", "surveyNumber"),
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
                null
        ).stream().filter(r -> "dtcp-onlineppa".equals(r.getSourceId()) && !r.isDemoData()).toList();

        if (!stored.isEmpty()) {
            return ProviderSearchResult.success(
                    getProviderId(),
                    stored,
                    "Retrieved from verified planning records database cache."
            );
        }

        return ProviderSearchResult.manualRequired(
                getProviderId(),
                "Official DTCP Regularisation Portal requires manual layout enquiry or local body application number. Verification incomplete until searched on onlineppa.tn.gov.in."
        );
    }

    @Override
    public ProviderHealthStatus checkHealth() {
        long start = System.currentTimeMillis();
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create("https://onlineppa.tn.gov.in/").toURL().openConnection();
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
