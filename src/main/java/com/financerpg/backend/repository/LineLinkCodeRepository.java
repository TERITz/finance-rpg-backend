package com.financerpg.backend.repository;

import com.financerpg.backend.entity.LineLinkCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LineLinkCodeRepository extends JpaRepository<LineLinkCode, Long> {
    Optional<LineLinkCode> findByCode(String code);
    void deleteByUserId(Long userId);
}