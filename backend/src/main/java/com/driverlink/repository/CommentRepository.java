package com.driverlink.repository;

import com.driverlink.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIncidentIdAndActive(Long incidentId, boolean active);
    List<Comment> findByUserIdAndActive(Long userId, boolean active);
    Long countByIncidentIdAndActive(Long incidentId, boolean active);

    @Query("SELECT c FROM Comment c WHERE c.incident.id = :incidentId " +
           "AND (:since IS NULL OR c.createdAt >= :since) " +
           "AND (:searchTerm IS NULL OR LOWER(c.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND c.active = :active")
    Page<Comment> findCommentsWithFilters(
            @Param("incidentId") Long incidentId,
            @Param("since") LocalDateTime since,
            @Param("searchTerm") String searchTerm,
            @Param("active") boolean active,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT c.user.id) FROM Comment c " +
           "WHERE c.incident.id = :incidentId AND c.active = true")
    Long countUniqueCommenters(@Param("incidentId") Long incidentId);

    @Query("SELECT MAX(c.createdAt) FROM Comment c " +
           "WHERE c.incident.id = :incidentId AND c.active = true")
    LocalDateTime findLastCommentDate(@Param("incidentId") Long incidentId);

    @Query("SELECT COUNT(c) FROM Comment c " +
           "WHERE c.user.id = :userId " +
           "AND c.incident.id = :incidentId " +
           "AND c.createdAt >= :since " +
           "AND c.active = true")
    Long countRecentCommentsByUser(
            @Param("userId") Long userId,
            @Param("incidentId") Long incidentId,
            @Param("since") LocalDateTime since);

    @Query("SELECT c FROM Comment c " +
           "WHERE c.incident.id = :incidentId " +
           "AND c.createdAt >= :since " +
           "AND c.active = true " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(
            @Param("incidentId") Long incidentId,
            @Param("since") LocalDateTime since);
}
