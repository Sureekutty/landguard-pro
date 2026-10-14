package com.landguard.provider;

import com.landguard.dto.property.PropertySearchRequest;

public interface LandRecordProvider {

    String getProviderId();

    ProviderCapabilities getCapabilities();

    ProviderSearchResult search(PropertySearchRequest request);

    ProviderHealthStatus checkHealth();
}
