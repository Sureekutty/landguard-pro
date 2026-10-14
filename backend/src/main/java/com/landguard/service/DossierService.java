package com.landguard.service;

import com.landguard.entity.UserDossier;
import com.landguard.model.PropertyDossier;
import com.landguard.repository.UserDossierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DossierService {

    private static final Logger log = LoggerFactory.getLogger(DossierService.class);

    private final UserDossierRepository jpaRepository;
    private final Map<String, PropertyDossier> inMemoryFallbackStore = new ConcurrentHashMap<>();

    public DossierService(UserDossierRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public PropertyDossier save(PropertyDossier dossier) {
        if (dossier.getId() == null || dossier.getId().trim().isEmpty()) {
            dossier.setId("PROP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        inMemoryFallbackStore.put(dossier.getId(), dossier);

        if (jpaRepository != null) {
            try {
                UserDossier entity = new UserDossier();
                entity.setId(dossier.getId());
                entity.setDistrict(dossier.getDistrict());
                entity.setTaluk(dossier.getTaluk());
                entity.setVillage(dossier.getVillage());
                entity.setSurveyNumber(dossier.getSurveyNumber());
                entity.setSubdivision(dossier.getSubDivision());
                entity.setClaimedOwner(dossier.getClaimedOwner());
                entity.setExtentSqFt(dossier.getExtentSqFt());
                entity.setAskingRate(dossier.getAskingRate());
                entity.setDtcpApproved(dossier.isDtcpApproved());
                entity.setEcClean(dossier.isEcClean30Years());
                entity.setStatusNotes(dossier.getStatusNotes());
                jpaRepository.save(entity);
            } catch (Exception ex) {
                log.warn("JPA database save failed. Retained in memory store: {}", ex.getMessage());
            }
        }
        return dossier;
    }

    public List<PropertyDossier> getAllDossiers() {
        if (jpaRepository != null) {
            try {
                List<UserDossier> entities = jpaRepository.findAll();
                if (!entities.isEmpty()) {
                    List<PropertyDossier> list = new ArrayList<>();
                    for (UserDossier e : entities) {
                        PropertyDossier d = new PropertyDossier();
                        d.setId(e.getId());
                        d.setDistrict(e.getDistrict());
                        d.setTaluk(e.getTaluk());
                        d.setVillage(e.getVillage());
                        d.setSurveyNumber(e.getSurveyNumber());
                        d.setSubDivision(e.getSubdivision());
                        d.setClaimedOwner(e.getClaimedOwner());
                        d.setExtentSqFt(e.getExtentSqFt() != null ? e.getExtentSqFt() : 1650.0);
                        d.setAskingRate(e.getAskingRate() != null ? e.getAskingRate() : 1100.0);
                        d.setDtcpApproved(Boolean.TRUE.equals(e.getDtcpApproved()));
                        d.setEcClean30Years(Boolean.TRUE.equals(e.getEcClean()));
                        d.setStatusNotes(e.getStatusNotes());
                        list.add(d);
                    }
                    return list;
                }
            } catch (Exception ex) {
                log.warn("JPA read failed, falling back to in-memory store: {}", ex.getMessage());
            }
        }
        return new ArrayList<>(inMemoryFallbackStore.values());
    }

    public PropertyDossier getById(String id) {
        if (jpaRepository != null) {
            try {
                return jpaRepository.findById(id).map(e -> {
                    PropertyDossier d = new PropertyDossier();
                    d.setId(e.getId());
                    d.setDistrict(e.getDistrict());
                    d.setTaluk(e.getTaluk());
                    d.setVillage(e.getVillage());
                    d.setSurveyNumber(e.getSurveyNumber());
                    d.setSubDivision(e.getSubdivision());
                    d.setClaimedOwner(e.getClaimedOwner());
                    d.setStatusNotes(e.getStatusNotes());
                    return d;
                }).orElse(inMemoryFallbackStore.get(id));
            } catch (Exception ex) {
                return inMemoryFallbackStore.get(id);
            }
        }
        return inMemoryFallbackStore.get(id);
    }

    public boolean deleteById(String id) {
        inMemoryFallbackStore.remove(id);
        if (jpaRepository != null) {
            try {
                jpaRepository.deleteById(id);
                return true;
            } catch (Exception ex) {
                log.warn("JPA delete failed: {}", ex.getMessage());
            }
        }
        return true;
    }

    public void clearAll() {
        inMemoryFallbackStore.clear();
        if (jpaRepository != null) {
            try {
                jpaRepository.deleteAll();
            } catch (Exception ex) {
                log.warn("JPA clear failed: {}", ex.getMessage());
            }
        }
    }
}