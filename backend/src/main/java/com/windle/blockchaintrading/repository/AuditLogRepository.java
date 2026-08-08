package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);

    List<AuditLog> findByAction(String action);

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:search IS NULL OR :search = ''
               OR LOWER(a.action) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(a.entityName) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY a.createdAt DESC
        """)
    Page<AuditLog> search(@Param("search") String search, Pageable pageable);
}