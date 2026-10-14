package com.landguard.service;

import com.landguard.model.VerificationResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfVerificationService {

    private static final String[] PROHIBITED_KEYWORDS = {
        "poramboke", "puramboke", "புறம்போக்கு",
        "odai", "eri", "vakkal", "நீர்நிலை", "ஏரி", "ஓடை",
        "hr&ce", "temple", "கோவில்",
        "panchami", "பஞ்சமி",
        "bhoodan", "பூமிதானம்",
        "court attachment", "attachment", "முடக்கம்",
        "mortgage", "அடமானம்"
    };

    public VerificationResult verifyDocument(MultipartFile file, String surveyNo, String ownerName) {
        VerificationResult result = new VerificationResult();

        if (file == null || file.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Uploaded PDF file is empty or missing.");
            return result;
        }

        result.setFileName(file.getOriginalFilename());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);
            String lowerText = fullText.toLowerCase();

            boolean surveyFound = false;
            if (surveyNo != null && !surveyNo.trim().isEmpty()) {
                String cleanSurvey = surveyNo.trim().replaceAll("\\s+", "");
                surveyFound = fullText.contains(surveyNo.trim()) || 
                              lowerText.contains(cleanSurvey.toLowerCase());
            }

            boolean ownerFound = false;
            if (ownerName != null && !ownerName.trim().isEmpty()) {
                String cleanOwner = ownerName.trim().toLowerCase();
                ownerFound = lowerText.contains(cleanOwner);
                if (!ownerFound) {
                    // Try splitting name parts (first name / initial)
                    String[] parts = cleanOwner.split("\\s+");
                    int matchedParts = 0;
                    for (String part : parts) {
                        if (part.length() > 2 && lowerText.contains(part)) {
                            matchedParts++;
                        }
                    }
                    if (matchedParts > 0 && matchedParts == parts.length) {
                        ownerFound = true;
                    }
                }
            }

            List<String> discrepancies = new ArrayList<>();
            List<String> positiveMatches = new ArrayList<>();

            if (surveyFound) {
                positiveMatches.add("Survey / Subdivision Number [" + surveyNo + "] verified in document.");
            } else {
                discrepancies.add("Survey Number [" + surveyNo + "] was NOT found in the extracted document text.");
            }

            if (ownerFound) {
                positiveMatches.add("Claimed Owner Name [" + ownerName + "] verified in document.");
            } else {
                discrepancies.add("Claimed Owner Name [" + ownerName + "] was NOT found in the extracted text. Check parent deed or spelling.");
            }

            // Red flag checks
            for (String redFlag : PROHIBITED_KEYWORDS) {
                if (lowerText.contains(redFlag)) {
                    discrepancies.add("CRITICAL WARNING: Prohibited classification or encumbrance term found: '" + redFlag + "'");
                }
            }

            int score = 0;
            if (surveyFound) score += 50;
            if (ownerFound) score += 50;
            if (!discrepancies.isEmpty()) {
                score = Math.max(0, score - (discrepancies.size() * 15));
            }

            result.setSuccess(true);
            result.setSurveyMatched(surveyFound);
            result.setOwnerMatched(ownerFound);
            result.setOverallMatch(surveyFound && ownerFound && discrepancies.isEmpty());
            result.setMatchConfidenceScore(score);
            result.setDiscrepancies(discrepancies);
            result.setPositiveMatches(positiveMatches);

            // Preview snippet
            int maxLen = Math.min(fullText.length(), 400);
            result.setExtractedSnippet(fullText.substring(0, maxLen).replaceAll("\\s+", " "));
            result.setMessage(result.isOverallMatch() ? "Document verified successfully with high confidence." : "Verification completed with remarks.");

            return result;
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage("Failed to parse PDF document: " + e.getMessage());
            return result;
        }
    }
}
