package com.landguard;

import com.landguard.dto.property.PropertySearchRequest;
import com.landguard.provider.ApiSetuProvider;
import com.landguard.provider.ProviderCapabilities;
import com.landguard.provider.ProviderSearchResult;
import com.landguard.security.SsrfProtectionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ApiSetuProviderTest {

    @Autowired
    private ApiSetuProvider apiSetuProvider;

    @Autowired
    private SsrfProtectionValidator ssrfValidator;

    @Test
    @DisplayName("API Setu Provider: ID and metadata are properly configured")
    void testProviderMetadata() {
        assertEquals("apisetu-tn", apiSetuProvider.getProviderId());

        ProviderCapabilities caps = apiSetuProvider.getCapabilities();
        assertNotNull(caps);
        assertEquals("apisetu-tn", caps.getProviderId());
        assertTrue(caps.isLiveApiAvailable(), "API Setu supports machine-to-machine automated REST API");
        assertFalse(caps.isManualVerificationRequired(), "API Setu does not require interactive CAPTCHA");
        assertTrue(caps.getSupportedRecordTypes().contains("Record of Rights (RoR)"));
    }

    @Test
    @DisplayName("API Setu SSRF Protection: Gateway domain is permitted while evil lookalikes are blocked")
    void testSsrfProtectionOnApiSetu() {
        // Valid official endpoint
        SsrfProtectionValidator.ValidationResult validRes = ssrfValidator.validateUrl("https://apisetu.gov.in/v1/ror");
        assertTrue(validRes.isValid(), "Official https://apisetu.gov.in must pass SSRF validation");

        // Malicious lookalike domains
        SsrfProtectionValidator.ValidationResult evilRes1 = ssrfValidator.validateUrl("https://apisetu.gov.in.evil.com/v1");
        assertFalse(evilRes1.isValid(), "Suffix hijack must be rejected by SSRF guard");

        SsrfProtectionValidator.ValidationResult evilRes2 = ssrfValidator.validateUrl("https://evil-apisetu.gov.in/v1");
        assertFalse(evilRes2.isValid(), "Prefix spoof must be rejected by SSRF guard");

        SsrfProtectionValidator.ValidationResult httpRes = ssrfValidator.validateUrl("http://apisetu.gov.in/v1");
        assertFalse(httpRes.isValid(), "Insecure HTTP must be rejected by SSRF guard");
    }

    @Test
    @DisplayName("API Setu Search: Returns authentic stored government records")
    void testSearchAuthenticRecords() {
        PropertySearchRequest req = new PropertySearchRequest();
        req.setDistrict("Namakkal");
        req.setTaluk("Rasipuram");
        req.setVillage("Gurusamipalayam");
        req.setSurveyNumber("148");
        req.setSubdivision("1");

        ProviderSearchResult res = apiSetuProvider.search(req);
        assertNotNull(res);
        assertNotNull(res.getRecords());
        assertFalse(res.getRecords().isEmpty(), "API Setu connector should find verified 148/1 parcel");
        assertTrue(res.getRecords().stream().noneMatch(r -> r.isDemoData()), "Records must be official, not demo data");
    }
}
