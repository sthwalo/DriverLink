package com.driverlink.service;

import com.driverlink.dto.IncidentDTO;
import com.driverlink.dto.LocationDTO;
import com.driverlink.model.Incident;
import com.driverlink.model.IncidentStatus;
import com.driverlink.model.IncidentType;
import com.driverlink.model.Location;
import com.driverlink.model.User;
import com.driverlink.repository.IncidentRepository;
import com.driverlink.repository.LocationRepository;
import com.driverlink.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RatingService ratingService;

    /**
     * Get all incidents with optional filtering
     */
    @Transactional(readOnly = true)
    public List<IncidentDTO> getAllIncidents(IncidentStatus status, IncidentType type, 
            String city, LocalDateTime startDate, LocalDateTime endDate) {
        return incidentRepository.findIncidentsWithFilters(status, type, city, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get incident by ID
     */
    @Transactional(readOnly = true)
    public IncidentDTO getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found"));
    }

    /**
     * Find incidents within specified radius from coordinates
     */
    @Transactional(readOnly = true)
    public List<IncidentDTO> getNearbyIncidents(Double latitude, Double longitude, Double radiusKm) {
        return incidentRepository.findNearbyIncidents(longitude, latitude, radiusKm)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new incident
     */
    @Transactional
    public IncidentDTO createIncident(IncidentDTO dto, Long userId) {
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Location location = new Location();
        location.setLatitude(dto.getLocation().getLatitude());
        location.setLongitude(dto.getLocation().getLongitude());
        location.setAddress(dto.getLocation().getAddress());
        location.setArea(dto.getLocation().getArea());
        location.setCity(dto.getLocation().getCity());
        location.setProvince(dto.getLocation().getProvince());
        locationRepository.save(location);

        Incident incident = new Incident();
        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setLocation(location);
        incident.setReporter(reporter);
        incident.setType(dto.getType());
        incident.setStatus(IncidentStatus.PENDING);

        Incident saved = incidentRepository.save(incident);
        return convertToDTO(saved);
    }

    /**
     * Update an existing incident
     */
    @Transactional
    public IncidentDTO updateIncident(Long id, IncidentDTO dto) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found"));

        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setType(dto.getType());

        if (dto.getLocation() != null) {
            Location location = incident.getLocation();
            location.setLatitude(dto.getLocation().getLatitude());
            location.setLongitude(dto.getLocation().getLongitude());
            location.setAddress(dto.getLocation().getAddress());
            location.setArea(dto.getLocation().getArea());
            location.setCity(dto.getLocation().getCity());
            location.setProvince(dto.getLocation().getProvince());
            locationRepository.save(location);
        }

        Incident updated = incidentRepository.save(incident);
        return convertToDTO(updated);
    }

    /**
     * Soft delete an incident
     */
    @Transactional
    public void deleteIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found"));
        incident.setActive(false);
        incidentRepository.save(incident);
    }

    private IncidentDTO convertToDTO(Incident incident) {
        IncidentDTO dto = new IncidentDTO();
        dto.setId(incident.getId());
        dto.setTitle(incident.getTitle());
        dto.setDescription(incident.getDescription());
        dto.setReporterId(incident.getReporter().getId());
        dto.setType(incident.getType());
        dto.setStatus(incident.getStatus());
        dto.setVerificationCount(incident.getVerificationCount());
        dto.setCreatedAt(incident.getCreatedAt());
        dto.setUpdatedAt(incident.getUpdatedAt());
        
        LocationDTO locationDTO = new LocationDTO();
        Location location = incident.getLocation();
        locationDTO.setId(location.getId());
        locationDTO.setLatitude(location.getLatitude());
        locationDTO.setLongitude(location.getLongitude());
        locationDTO.setAddress(location.getAddress());
        locationDTO.setArea(location.getArea());
        locationDTO.setCity(location.getCity());
        locationDTO.setProvince(location.getProvince());
        dto.setLocation(locationDTO);

        // Add average rating
        dto.setAverageRating(ratingService.getAverageRatingForIncident(incident.getId()));
        
        return dto;
    }
}
