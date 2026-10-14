package com.landguard.provider;

import java.util.List;

public class ProviderCapabilities {

    private final String providerId;
    private final String providerName;
    private final List<String> supportedRecordTypes;
    private final List<String> supportedSearchFields;
    private final boolean liveApiAvailable;
    private final boolean manualVerificationRequired;

    public ProviderCapabilities(
            String providerId,
            String providerName,
            List<String> supportedRecordTypes,
            List<String> supportedSearchFields,
            boolean liveApiAvailable,
            boolean manualVerificationRequired) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.supportedRecordTypes = supportedRecordTypes;
        this.supportedSearchFields = supportedSearchFields;
        this.liveApiAvailable = liveApiAvailable;
        this.manualVerificationRequired = manualVerificationRequired;
    }

    public String getProviderId() { return providerId; }
    public String getProviderName() { return providerName; }
    public List<String> getSupportedRecordTypes() { return supportedRecordTypes; }
    public List<String> getSupportedSearchFields() { return supportedSearchFields; }
    public boolean isLiveApiAvailable() { return liveApiAvailable; }
    public boolean isManualVerificationRequired() { return manualVerificationRequired; }
}
