package com.landguard;

import com.landguard.dto.property.DocumentIngestionResponse;
import com.landguard.entity.RetrievedPropertyRecord;
import com.landguard.repository.RetrievedPropertyRecordRepository;
import com.landguard.service.PdfVerificationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DocumentIngestionAndVerificationTest {

    @Autowired
    private PdfVerificationService verificationService;

    @Autowired
    private RetrievedPropertyRecordRepository recordRepository;

    @Test
    @DisplayName("Document Ingestion: Reject empty or missing PDF file")
    void testRejectEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);
        DocumentIngestionResponse resp = verificationService.ingestOfficialDocument(emptyFile, null, null, null, null, null, null, null);

        assertNotNull(resp);
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("empty or missing"));
    }

    @Test
    @DisplayName("Document Ingestion: Reject non-government PDF without official authority headers")
    void testRejectNonGovernmentPdf() throws IOException {
        byte[] pdfBytes = createPdfWithText(
                "XYZ Real Estate Brochure",
                "Private property available for immediate purchase. Call 9876543210."
        );
        MockMultipartFile fakeFile = new MockMultipartFile("file", "brochure.pdf", "application/pdf", pdfBytes);

        DocumentIngestionResponse resp = verificationService.ingestOfficialDocument(fakeFile, null, null, null, null, null, null, null);

        assertNotNull(resp);
        assertFalse(resp.isSuccess(), "Non-government document must be rejected");
        assertTrue(resp.getMessage().contains("No valid Tamil Nadu government authority header found"));
    }

    @Test
    @DisplayName("Document Ingestion: Ingest authentic AnyTN Patta PDF with digital signature reference")
    void testIngestAuthenticAnyTnPattaPdf() throws IOException {
        byte[] pdfBytes = createPdfWithText(
                "Government of Tamil Nadu - AnyTN e-Services Revenue Department",
                "Computerised Patta Chitta Extract Ref: TN-REV-RAS-9999/2026",
                "District: Namakkal, Taluk: Rasipuram, Village: Gurusamipalayam",
                "Survey: 149/1, Patta: 9999, Owner: Selvamurugan K",
                "AnyTN Digital Signature Verified by Special Tahsildar"
        );
        MockMultipartFile validFile = new MockMultipartFile("file", "patta_extract.pdf", "application/pdf", pdfBytes);

        DocumentIngestionResponse resp = verificationService.ingestOfficialDocument(
                validFile, "Namakkal", "Rasipuram", "Gurusamipalayam", "149/1", "1", "9999", "Selvamurugan K");

        assertNotNull(resp);
        assertTrue(resp.isSuccess(), "Official AnyTN document should be accepted and ingested");
        assertTrue(resp.isDigitalSignatureVerified());
        assertEquals("AnyTN e-Services (Revenue Department)", resp.getSourceRegistry());
        assertNotNull(resp.getDocumentSha256());
        assertEquals(64, resp.getDocumentSha256().length(), "SHA-256 hash must be 64 hexadecimal characters");

        RetrievedPropertyRecord record = resp.getIngestedRecord();
        assertNotNull(record);
        assertFalse(record.isDemoData(), "Ingested government records must NOT be demo data");
        assertEquals("149/1", record.getFullSurvey());
        assertEquals("9999", record.getPattaNumber());
        assertEquals("OFFICIAL_GOVERNMENT_DOCUMENT_VERIFIED", record.getEvidenceStatus());

        // Verify persisted into PostgreSQL/H2 repository
        assertTrue(recordRepository.findById(record.getId()).isPresent(), "Record must be saved in database");
    }

    @Test
    @DisplayName("Document Ingestion: Ingest authentic TNREGINET Form 15 Encumbrance Certificate PDF")
    void testIngestAuthenticTnreginetEcPdf() throws IOException {
        byte[] pdfBytes = createPdfWithText(
                "Government of Tamil Nadu - Registration Department TNREGINET",
                "Encumbrance Certificate (Form 15) Ref: EC/2026/RAS/88888",
                "Sub-Registrar Office: Rasipuram, District: Namakkal",
                "Survey: 150/2B, Owner: Balasubramaniam S",
                "Certified Electronic Copy issued by Inspector General of Registration"
        );
        MockMultipartFile validFile = new MockMultipartFile("file", "form15_ec.pdf", "application/pdf", pdfBytes);

        DocumentIngestionResponse resp = verificationService.ingestOfficialDocument(
                validFile, "Namakkal", "Rasipuram", "Gurusamipalayam", "150/2B", "2B", "1555", "Balasubramaniam S");

        assertNotNull(resp);
        assertTrue(resp.isSuccess());
        assertEquals("TNREGINET (Registration Department)", resp.getSourceRegistry());
        assertEquals("EC/2026/RAS/88888", resp.getDigitalSignatureReference());
        assertFalse(resp.getIngestedRecord().isDemoData());
    }

    private byte[] createPdfWithText(String... lines) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                for (String line : lines) {
                    cs.showText(line);
                    cs.newLineAtOffset(0, -20);
                }
                cs.endText();
            }
            doc.save(baos);
        }
        return baos.toByteArray();
    }
}
