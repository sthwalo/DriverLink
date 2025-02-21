package com.driverlink.repository;

import com.driverlink.model.Incident;
import com.driverlink.model.IncidentStatus;
import com.driverlink.model.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByLocationCityAndActive(String city, boolean active);
    List<Incident> findByStatusAndActive(IncidentStatus status, boolean active);
    
    @Query("SELECT i FROM Incident i WHERE i.active = true AND " +
           "ST_Distance(ST_MakePoint(i.location.longitude, i.location.latitude), " +
           "ST_MakePoint(?1, ?2)) <= ?3")
    List<Incident> findNearbyIncidents(double longitude, double latitude, double radiusInKm);

    @Query("SELECT i FROM Incident i WHERE " +
           "(:status is null OR i.status = :status) AND " +
           "(:type is null OR i.type = :type) AND " +
           "(:city is null OR i.location.city = :city) AND " +
           "(:startDate is null OR i.createdAt >= :startDate) AND " +
           "(:endDate is null OR i.createdAt <= :endDate) AND " +
           "i.active = true")
    List<Incident> findIncidentsWithFilters(
            @Param("status") IncidentStatus status,
            @Param("type") IncidentType type,
            @Param("city") String city,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(i) FROM Incident i WHERE " +
           "i.status = :status AND i.active = true AND " +
           "i.createdAt >= :startDate")
    long countActiveIncidentsByStatusSince(
            @Param("status") IncidentStatus status,
            @Param("startDate") LocalDateTime startDate);
}
