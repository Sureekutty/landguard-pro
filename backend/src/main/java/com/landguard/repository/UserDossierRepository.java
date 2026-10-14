package com.landguard.repository;

import com.landguard.entity.UserDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDossierRepository extends JpaRepository<UserDossier, String> {
    List<UserDossier> findByUserIdOrderByCreatedAtDesc(String userId);
}
