package com.driverlink.repository;

import com.driverlink.model.Vote;
import com.driverlink.model.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    /**
     * Find active vote by incident and user
     */
    Optional<Vote> findByIncidentIdAndUserIdAndActiveTrue(Long incidentId, Long userId);

    /**
     * Find all active votes for an incident
     */
    List<Vote> findByIncidentIdAndActiveTrue(Long incidentId);

    /**
     * Check if user has an active vote for an incident
     */
    boolean existsByIncidentIdAndUserIdAndActiveTrue(Long incidentId, Long userId);

    /**
     * Count active votes by type for an incident
     */
    @Query("SELECT v.voteType, COUNT(v) FROM Vote v WHERE v.incident.id = :incidentId AND v.active = true GROUP BY v.voteType")
    List<Object[]> countActiveVotesByType(@Param("incidentId") Long incidentId);

    /**
     * Count unique voters for an incident
     */
    @Query("SELECT COUNT(DISTINCT v.user.id) FROM Vote v WHERE v.incident.id = :incidentId AND v.active = true")
    Long countUniqueVoters(@Param("incidentId") Long incidentId);

    /**
     * Find recent votes for an incident
     */
    @Query("SELECT v FROM Vote v WHERE v.incident.id = :incidentId AND v.active = true ORDER BY v.createdAt DESC")
    List<Vote> findRecentVotes(@Param("incidentId") Long incidentId);
}
