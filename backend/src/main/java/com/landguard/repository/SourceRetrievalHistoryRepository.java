package com.landguard.repository;

import com.landguard.entity.SourceRetrievalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRetrievalHistoryRepository extends JpaRepository<SourceRetrievalHistory, String> {
    List<SourceRetrievalHistory> findTop20BySourceIdOrderByRetrievalTimestampDesc(String sourceId);
}
