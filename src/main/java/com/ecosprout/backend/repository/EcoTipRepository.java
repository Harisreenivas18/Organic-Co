package com.ecosprout.backend.repository;

import com.ecosprout.backend.model.EcoTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EcoTipRepository extends JpaRepository<EcoTip, Long> {

    @Query(value = "SELECT * FROM eco_tips ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<EcoTip> findRandomTip();
}