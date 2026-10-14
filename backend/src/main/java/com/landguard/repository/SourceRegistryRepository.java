package com.landguard.repository;

import com.landguard.entity.SourceRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRegistryRepository extends JpaRepository<SourceRegistry, String> {
    List<SourceRegistry> findByIntegrationStatusNot(String status);
}
