package com.driverlink.service;

import com.driverlink.dto.RatingDTO;
import com.driverlink.dto.RatingStatistics;
import com.driverlink.exception.ResourceNotFoundException;
import com.driverlink.exception.ValidationException;
import com.driverlink.model.Incident;
import com.driverlink.model.Rating;
import com.driverlink.model.User;
import com.driverlink.repository.IncidentRepository;
import com.driverlink.repository.RatingRepository;
import com.driverlink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    private User testUser;
    private Incident testIncident;
    private Rating testRating;
    private RatingDTO testRatingDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testIncident = new Incident();
        testIncident.setId(1L);
        testIncident.setTitle("Test Incident");

        testRating = new Rating();
        testRating.setId(1L);
        testRating.setValue(4);
        testRating.setComment("Great service");
        testRating.setUser(testUser);
        testRating.setIncident(testIncident);
        testRating.setCreatedAt(LocalDateTime.now());
        testRating.setActive(true);

        testRatingDTO = new RatingDTO();
        testRatingDTO.setIncidentId(1L);
        testRatingDTO.setValue(4);
        testRatingDTO.setComment("Great service");
    }

    @Test
    void getRatingsForIncident_ShouldReturnFilteredRatings() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        when(incidentRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findRatingsWithFilters(eq(1L), eq(3), eq(5), eq(since), eq(true)))
                .thenReturn(Arrays.asList(testRating));

        // When
        List<RatingDTO> result = ratingService.getRatingsForIncident(1L, 3, 5, since);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValue()).isEqualTo(testRating.getValue());
        verify(ratingRepository).findRatingsWithFilters(1L, 3, 5, since, true);
    }

    @Test
    void getRatingStatistics_ShouldReturnStatistics() {
        // Given
        when(incidentRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.getAverageRatingForIncident(1L)).thenReturn(4.0);
        when(ratingRepository.countByIncidentIdAndActive(1L, true)).thenReturn(10L);
        
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(4, 6L);
        distribution.put(5, 4L);
        when(ratingRepository.getRatingDistribution(1L)).thenReturn(distribution);

        // When
        RatingStatistics stats = ratingService.getRatingStatistics(1L);

        // Then
        assertThat(stats.getAverageRating()).isEqualTo(4.0);
        assertThat(stats.getTotalRatings()).isEqualTo(10L);
        assertThat(stats.getRatingDistribution()).containsEntry(4, 6L);
        assertThat(stats.getRatingDistribution()).containsEntry(5, 4L);
    }

    @Test
    void createRating_ShouldCreateNewRating() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(ratingRepository.findByIncidentIdAndUserIdAndActive(1L, 1L, true))
                .thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // When
        RatingDTO result = ratingService.createRating(testRatingDTO, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(testRatingDTO.getValue());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void createRating_ShouldThrowException_WhenInvalidRating() {
        // Given
        testRatingDTO.setValue(6); // Invalid rating value

        // When/Then
        assertThrows(ValidationException.class,
                () -> ratingService.createRating(testRatingDTO, 1L));
    }

    @Test
    void createRating_ShouldThrowException_WhenDuplicateRating() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(ratingRepository.findByIncidentIdAndUserIdAndActive(1L, 1L, true))
                .thenReturn(Optional.of(testRating));

        // When/Then
        assertThrows(ValidationException.class,
                () -> ratingService.createRating(testRatingDTO, 1L));
    }

    @Test
    void updateRating_ShouldUpdateExistingRating() {
        // Given
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        testRatingDTO.setValue(5);
        testRatingDTO.setComment("Updated comment");

        // When
        RatingDTO result = ratingService.updateRating(1L, testRatingDTO, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Updated comment");
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void updateRating_ShouldThrowException_WhenNotOwner() {
        // Given
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));

        // When/Then
        assertThrows(ValidationException.class,
                () -> ratingService.updateRating(1L, testRatingDTO, 2L));
    }

    @Test
    void deleteRating_ShouldSoftDeleteRating() {
        // Given
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));

        // When
        ratingService.deleteRating(1L, 1L);

        // Then
        verify(ratingRepository).save(argThat(rating -> !rating.isActive()));
    }
}
