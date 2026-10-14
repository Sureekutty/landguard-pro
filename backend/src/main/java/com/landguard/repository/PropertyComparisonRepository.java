package com.landguard.repository;

import com.landguard.entity.PropertyComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyComparisonRepository extends JpaRepository<PropertyComparison, String> {
    Optional<PropertyComparison> findFirstByPropertyIdOrderByCreatedAtDesc(String propertyId);
}
