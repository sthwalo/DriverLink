package com.driverlink.service;

import com.driverlink.dto.IncidentDTO;
import com.driverlink.dto.LocationDTO;
import com.driverlink.model.*;
import com.driverlink.repository.IncidentRepository;
import com.driverlink.repository.LocationRepository;
import com.driverlink.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private IncidentService incidentService;

    private User testUser;
    private Location testLocation;
    private Incident testIncident;
    private IncidentDTO testIncidentDTO;
    private LocationDTO testLocationDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setLatitude(-26.2041);
        testLocation.setLongitude(28.0473);
        testLocation.setCity("Johannesburg");

        testIncident = new Incident();
        testIncident.setId(1L);
        testIncident.setTitle("Test Incident");
        testIncident.setDescription("Test Description");
        testIncident.setType(IncidentType.ACCIDENT);
        testIncident.setStatus(IncidentStatus.PENDING);
        testIncident.setLocation(testLocation);
        testIncident.setReporter(testUser);
        testIncident.setCreatedAt(LocalDateTime.now());

        testLocationDTO = new LocationDTO();
        testLocationDTO.setLatitude(-26.2041);
        testLocationDTO.setLongitude(28.0473);
        testLocationDTO.setCity("Johannesburg");

        testIncidentDTO = new IncidentDTO();
        testIncidentDTO.setTitle("Test Incident");
        testIncidentDTO.setDescription("Test Description");
        testIncidentDTO.setType(IncidentType.ACCIDENT);
        testIncidentDTO.setLocation(testLocationDTO);
    }

    @Test
    void getAllIncidents_ShouldReturnFilteredIncidents() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        when(incidentRepository.findIncidentsWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(testIncident));
        when(ratingService.getAverageRatingForIncident(any())).thenReturn(4.5);

        // When
        List<IncidentDTO> result = incidentService.getAllIncidents(
                IncidentStatus.PENDING, IncidentType.ACCIDENT, "Johannesburg", startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testIncident.getTitle());
        verify(incidentRepository).findIncidentsWithFilters(
                IncidentStatus.PENDING, IncidentType.ACCIDENT, "Johannesburg", startDate, endDate);
    }

    @Test
    void getIncidentById_ShouldReturnIncident() {
        // Given
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(ratingService.getAverageRatingForIncident(any())).thenReturn(4.5);

        // When
        IncidentDTO result = incidentService.getIncidentById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(testIncident.getTitle());
        assertThat(result.getType()).isEqualTo(testIncident.getType());
    }

    @Test
    void getIncidentById_ShouldThrowException_WhenNotFound() {
        // Given
        when(incidentRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> incidentService.getIncidentById(1L));
    }

    @Test
    void createIncident_ShouldCreateNewIncident() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(incidentRepository.save(any(Incident.class))).thenReturn(testIncident);
        when(ratingService.getAverageRatingForIncident(any())).thenReturn(0.0);

        // When
        IncidentDTO result = incidentService.createIncident(testIncidentDTO, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(testIncidentDTO.getTitle());
        assertThat(result.getType()).isEqualTo(testIncidentDTO.getType());
        verify(locationRepository).save(any(Location.class));
        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void getNearbyIncidents_ShouldReturnNearbyIncidents() {
        // Given
        when(incidentRepository.findNearbyIncidents(any(), any(), any()))
                .thenReturn(Arrays.asList(testIncident));
        when(ratingService.getAverageRatingForIncident(any())).thenReturn(4.5);

        // When
        List<IncidentDTO> result = incidentService.getNearbyIncidents(-26.2041, 28.0473, 5.0);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testIncident.getTitle());
        verify(incidentRepository).findNearbyIncidents(28.0473, -26.2041, 5.0);
    }
}
