package com.driverlink.repository;

import com.driverlink.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByIncidentIdAndActive(Long incidentId, boolean active);
    Optional<Rating> findByIncidentIdAndUserIdAndActive(Long incidentId, Long userId, boolean active);
    Long countByIncidentIdAndActive(Long incidentId, boolean active);
    
    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.incident.id = :incidentId AND r.active = true")
    Double getAverageRatingForIncident(@Param("incidentId") Long incidentId);

    @Query("SELECT r FROM Rating r WHERE r.incident.id = :incidentId " +
           "AND (:minRating IS NULL OR r.value >= :minRating) " +
           "AND (:maxRating IS NULL OR r.value <= :maxRating) " +
           "AND (:since IS NULL OR r.createdAt >= :since) " +
           "AND r.active = :active " +
           "ORDER BY r.createdAt DESC")
    List<Rating> findRatingsWithFilters(
            @Param("incidentId") Long incidentId,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            @Param("since") LocalDateTime since,
            @Param("active") boolean active);

    @Query("SELECT r.value as rating, COUNT(r) as count " +
           "FROM Rating r " +
           "WHERE r.incident.id = :incidentId AND r.active = true " +
           "GROUP BY r.value")
    Map<Integer, Long> getRatingDistribution(@Param("incidentId") Long incidentId);

    @Query("SELECT COUNT(r) FROM Rating r " +
           "WHERE r.incident.id = :incidentId " +
           "AND r.value = :value AND r.active = true")
    Long countByIncidentIdAndValueAndActive(
            @Param("incidentId") Long incidentId,
            @Param("value") Integer value);
}
