package com.landguard.dto.property;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class PropertySearchRequest {

    @NotBlank(message = "District is required")
    private String district = "Namakkal";

    @NotBlank(message = "Taluk is required")
    private String taluk = "Rasipuram";

    @NotBlank(message = "Village is required")
    private String village = "Gurusamipalayam";

    private String surveyNumber;
    private String subdivision;
    private String pattaNumber;
    private String ownerName;
    private String documentNumber;
    private Integer registrationYear;
    private String sro;

    // Optional query string from universal search bar
    private String query;
    private String searchMode = "ALL"; // ALL, NAME, SURVEY, PATTA

    // Flag for testing/demo fixtures (strictly labeled when enabled)
    private boolean includeDemoData = false;

    // Filter by specific source providers if desired
    private List<String> targetSources;

    public PropertySearchRequest() {}

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluk() { return taluk; }
    public void setTaluk(String taluk) { this.taluk = taluk; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getSurveyNumber() { return surveyNumber; }
    public void setSurveyNumber(String surveyNumber) { this.surveyNumber = surveyNumber; }

    public String getSubdivision() { return subdivision; }
    public void setSubdivision(String subdivision) { this.subdivision = subdivision; }

    public String getPattaNumber() { return pattaNumber; }
    public void setPattaNumber(String pattaNumber) { this.pattaNumber = pattaNumber; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public Integer getRegistrationYear() { return registrationYear; }
    public void setRegistrationYear(Integer registrationYear) { this.registrationYear = registrationYear; }

    public String getSro() { return sro; }
    public void setSro(String sro) { this.sro = sro; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getSearchMode() { return searchMode; }
    public void setSearchMode(String searchMode) { this.searchMode = searchMode; }

    public boolean isIncludeDemoData() { return includeDemoData; }
    public void setIncludeDemoData(boolean includeDemoData) { this.includeDemoData = includeDemoData; }

    public List<String> getTargetSources() { return targetSources; }
    public void setTargetSources(List<String> targetSources) { this.targetSources = targetSources; }
}
