package com.landguard.repository;

import com.landguard.entity.RetrievedPropertyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetrievedPropertyRecordRepository extends JpaRepository<RetrievedPropertyRecord, String> {

    List<RetrievedPropertyRecord> findByDistrictIgnoreCaseAndTalukIgnoreCaseAndVillageIgnoreCase(
        String district, String taluk, String village
    );

    List<RetrievedPropertyRecord> findByDistrictIgnoreCaseAndTalukIgnoreCaseAndVillageIgnoreCaseAndSurveyNumber(
        String district, String taluk, String village, String surveyNumber
    );

    @Query("SELECT r FROM RetrievedPropertyRecord r WHERE " +
           "LOWER(r.district) = LOWER(:district) AND " +
           "LOWER(r.taluk) = LOWER(:taluk) AND " +
           "LOWER(r.village) = LOWER(:village) AND " +
           "(:surveyNumber IS NULL OR r.surveyNumber = :surveyNumber) AND " +
           "(:subdivision IS NULL OR LOWER(r.subdivision) = LOWER(:subdivision)) AND " +
           "(:pattaNumber IS NULL OR r.pattaNumber = :pattaNumber) AND " +
           "(:ownerQuery IS NULL OR LOWER(r.ownerNameRaw) LIKE LOWER(CONCAT('%', :ownerQuery, '%')) OR LOWER(r.ownerNameNormalized) LIKE LOWER(CONCAT('%', :ownerQuery, '%')))")
    List<RetrievedPropertyRecord> searchProperties(
        @Param("district") String district,
        @Param("taluk") String taluk,
        @Param("village") String village,
        @Param("surveyNumber") String surveyNumber,
        @Param("subdivision") String subdivision,
        @Param("pattaNumber") String pattaNumber,
        @Param("ownerQuery") String ownerQuery
    );
}
