package com.landguard.controller;

import com.landguard.entity.AuditEvent;
import com.landguard.entity.SourceRegistry;
import com.landguard.repository.AuditEventRepository;
import com.landguard.repository.SourceRegistryRepository;
import com.landguard.security.SsrfProtectionValidator;
import com.landguard.service.SourceHealthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SourceRegistryController {

    private final SourceRegistryRepository sourceRepository;
    private final SourceHealthService healthService;
    private final SsrfProtectionValidator ssrfValidator;
    private final AuditEventRepository auditEventRepository;

    public SourceRegistryController(
            SourceRegistryRepository sourceRepository,
            SourceHealthService healthService,
            SsrfProtectionValidator ssrfValidator,
            AuditEventRepository auditEventRepository) {
        this.sourceRepository = sourceRepository;
        this.healthService = healthService;
        this.ssrfValidator = ssrfValidator;
        this.auditEventRepository = auditEventRepository;
    }

    // --- PUBLIC ENDPOINTS ---

    @GetMapping("/sources")
    public ResponseEntity<List<SourceRegistry>> getActiveSources() {
        return ResponseEntity.ok(sourceRepository.findByIntegrationStatusNot("DISABLED"));
    }

    @GetMapping("/sources/{id}/status")
    public ResponseEntity<Map<String, Object>> getSourceStatus(@PathVariable String id) {
        SourceRegistry source = sourceRepository.findById(id).orElse(null);
        if (source == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("found", false, "message", "Source not registered"));
        }
        return ResponseEntity.ok(Map.of(
                "id", source.getId(),
                "name", source.getName(),
                "officialUrl", source.getOfficialUrl(),
                "integrationStatus", source.getIntegrationStatus(),
                "authorizationStatus", source.getAuthorizationStatus(),
                "lastHealthCheck", source.getLastHealthCheck() != null ? source.getLastHealthCheck().toString() : "Never",
                "disclaimer", source.getSourceDisclaimer()
        ));
    }

    // --- ADMIN MANAGEMENT ENDPOINTS (ROLE_ADMIN) ---

    @GetMapping("/admin/sources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SourceRegistry>> getAllSourcesForAdmin() {
        return ResponseEntity.ok(sourceRepository.findAll());
    }

    @PostMapping("/admin/sources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addSource(
            @RequestBody SourceRegistry newSource,
            Authentication auth,
            HttpServletRequest req) {
        // Strict SSRF check before accepting any portal URL
        SsrfProtectionValidator.ValidationResult check = ssrfValidator.validateUrl(newSource.getOfficialUrl());
        if (!check.isValid()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", check.getMessage()));
        }

        if (sourceRepository.existsById(newSource.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("success", false, "error", "Source ID already exists"));
        }

        newSource.setCreatedAt(LocalDateTime.now());
        newSource.setUpdatedAt(LocalDateTime.now());
        SourceRegistry saved = sourceRepository.save(newSource);

        recordAudit(auth != null ? auth.getName() : "admin", "ADD_SOURCE", "SOURCE", saved.getId(), req.getRemoteAddr(), "Added new government source: " + saved.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/admin/sources/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateSource(
            @PathVariable String id,
            @RequestBody SourceRegistry updated,
            Authentication auth,
            HttpServletRequest req) {
        SourceRegistry existing = sourceRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "Source not found"));
        }

        if (updated.getOfficialUrl() != null && !updated.getOfficialUrl().equals(existing.getOfficialUrl())) {
            SsrfProtectionValidator.ValidationResult check = ssrfValidator.validateUrl(updated.getOfficialUrl());
            if (!check.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "error", check.getMessage()));
            }
            existing.setOfficialUrl(updated.getOfficialUrl());
        }

        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getIntegrationStatus() != null) existing.setIntegrationStatus(updated.getIntegrationStatus());
        if (updated.getAuthorizationStatus() != null) existing.setAuthorizationStatus(updated.getAuthorizationStatus());
        if (updated.getKnownLimitations() != null) existing.setKnownLimitations(updated.getKnownLimitations());
        if (updated.getApplicableUsageRestrictions() != null) existing.setApplicableUsageRestrictions(updated.getApplicableUsageRestrictions());
        if (updated.getSourceDisclaimer() != null) existing.setSourceDisclaimer(updated.getSourceDisclaimer());
        existing.setUpdatedAt(LocalDateTime.now());

        SourceRegistry saved = sourceRepository.save(existing);
        recordAudit(auth != null ? auth.getName() : "admin", "UPDATE_SOURCE", "SOURCE", saved.getId(), req.getRemoteAddr(), "Updated source: " + saved.getName());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/admin/sources/{id}/health-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SourceRegistry> runHealthCheck(
            @PathVariable String id,
            Authentication auth,
            HttpServletRequest req) {
        SourceRegistry updated = healthService.performHealthCheck(id);
        recordAudit(auth != null ? auth.getName() : "admin", "TRIGGER_HEALTH_CHECK", "SOURCE", id, req.getRemoteAddr(), "Triggered health check");
        return ResponseEntity.ok(updated);
    }

    private void recordAudit(String actor, String action, String resourceType, String resourceId, String ip, String details) {
        try {
            AuditEvent event = new AuditEvent(
                    UUID.randomUUID().toString(),
                    actor, action, resourceType, resourceId, ip, details
            );
            auditEventRepository.save(event);
        } catch (Exception ignored) {}
    }
}
