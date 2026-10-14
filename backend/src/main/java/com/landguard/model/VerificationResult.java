package com.landguard.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationResult {
    private boolean success;
    private String fileName;
    private boolean surveyMatched;
    private boolean ownerMatched;
    private boolean overallMatch;
    private int matchConfidenceScore;
    private String extractedSnippet;
    private List<String> discrepancies = new ArrayList<>();
    private List<String> positiveMatches = new ArrayList<>();
    private String message;

    public VerificationResult() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public boolean isSurveyMatched() { return surveyMatched; }
    public void setSurveyMatched(boolean surveyMatched) { this.surveyMatched = surveyMatched; }

    public boolean isOwnerMatched() { return ownerMatched; }
    public void setOwnerMatched(boolean ownerMatched) { this.ownerMatched = ownerMatched; }

    public boolean isOverallMatch() { return overallMatch; }
    public void setOverallMatch(boolean overallMatch) { this.overallMatch = overallMatch; }

    public int getMatchConfidenceScore() { return matchConfidenceScore; }
    public void setMatchConfidenceScore(int matchConfidenceScore) { this.matchConfidenceScore = matchConfidenceScore; }

    public String getExtractedSnippet() { return extractedSnippet; }
    public void setExtractedSnippet(String extractedSnippet) { this.extractedSnippet = extractedSnippet; }

    public List<String> getDiscrepancies() { return discrepancies; }
    public void setDiscrepancies(List<String> discrepancies) { this.discrepancies = discrepancies; }

    public List<String> getPositiveMatches() { return positiveMatches; }
    public void setPositiveMatches(List<String> positiveMatches) { this.positiveMatches = positiveMatches; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
