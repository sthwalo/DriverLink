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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    /**
     * Get ratings for an incident with optional filtering
     */
    @Transactional(readOnly = true)
    public List<RatingDTO> getRatingsForIncident(Long incidentId, Integer minRating, Integer maxRating, LocalDateTime since) {
        validateIncidentExists(incidentId);
        return ratingRepository.findRatingsWithFilters(incidentId, minRating, maxRating, since, true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get average rating for an incident
     */
    @Transactional(readOnly = true)
    public Double getAverageRatingForIncident(Long incidentId) {
        validateIncidentExists(incidentId);
        return ratingRepository.getAverageRatingForIncident(incidentId);
    }

    /**
     * Get rating statistics for an incident
     */
    @Transactional(readOnly = true)
    public RatingStatistics getRatingStatistics(Long incidentId) {
        validateIncidentExists(incidentId);
        return RatingStatistics.builder()
                .averageRating(ratingRepository.getAverageRatingForIncident(incidentId))
                .totalRatings(ratingRepository.countByIncidentIdAndActive(incidentId, true))
                .ratingDistribution(ratingRepository.getRatingDistribution(incidentId))
                .build();
    }

    /**
     * Create a new rating
     */
    @Transactional
    public RatingDTO createRating(RatingDTO dto, Long userId) {
        validateRatingInput(dto);
        User user = getUserById(userId);
        Incident incident = getIncidentById(dto.getIncidentId());

        validateNoExistingRating(dto.getIncidentId(), userId);

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setIncident(incident);
        rating.setValue(dto.getValue());
        rating.setComment(dto.getComment());

        Rating saved = ratingRepository.save(rating);
        return convertToDTO(saved);
    }

    /**
     * Update an existing rating
     */
    @Transactional
    public RatingDTO updateRating(Long id, RatingDTO dto, Long userId) {
        validateRatingInput(dto);
        Rating rating = getRatingById(id);
        validateRatingOwnership(rating, userId);

        rating.setValue(dto.getValue());
        rating.setComment(dto.getComment());

        Rating updated = ratingRepository.save(rating);
        return convertToDTO(updated);
    }

    /**
     * Delete a rating
     */
    @Transactional
    public void deleteRating(Long id, Long userId) {
        Rating rating = getRatingById(id);
        validateRatingOwnership(rating, userId);

        rating.setActive(false);
        ratingRepository.save(rating);
    }

    private Rating getRatingById(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Incident getIncidentById(Long incidentId) {
        return incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
    }

    private void validateIncidentExists(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new ResourceNotFoundException("Incident not found");
        }
    }

    private void validateRatingInput(RatingDTO dto) {
        if (dto.getValue() < 1 || dto.getValue() > 5) {
            throw new ValidationException("Rating value must be between 1 and 5");
        }
        if (dto.getComment() != null && dto.getComment().length() > 500) {
            throw new ValidationException("Comment cannot exceed 500 characters");
        }
    }

    private void validateNoExistingRating(Long incidentId, Long userId) {
        if (ratingRepository.findByIncidentIdAndUserIdAndActive(incidentId, userId, true).isPresent()) {
            throw new ValidationException("User has already rated this incident");
        }
    }

    private void validateRatingOwnership(Rating rating, Long userId) {
        if (!rating.getUser().getId().equals(userId)) {
            throw new ValidationException("User can only modify their own ratings");
        }
    }

    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setIncidentId(rating.getIncident().getId());
        dto.setUserId(rating.getUser().getId());
        dto.setValue(rating.getValue());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}
