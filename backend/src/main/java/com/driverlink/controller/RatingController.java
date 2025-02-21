package com.driverlink.controller;

import com.driverlink.dto.RatingDTO;
import com.driverlink.dto.RatingStatistics;
import com.driverlink.security.CurrentUser;
import com.driverlink.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating", description = "Rating management APIs")
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/incident/{incidentId}")
    @Operation(summary = "Get ratings for incident", 
               description = "Retrieve all ratings for a specific incident with optional filtering")
    public ResponseEntity<List<RatingDTO>> getRatingsForIncident(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long incidentId,
            @Parameter(description = "Minimum rating value (1-5)")
            @RequestParam(required = false) Integer minRating,
            @Parameter(description = "Maximum rating value (1-5)")
            @RequestParam(required = false) Integer maxRating,
            @Parameter(description = "Filter ratings since date")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return ResponseEntity.ok(ratingService.getRatingsForIncident(incidentId, minRating, maxRating, since));
    }

    @GetMapping("/incident/{incidentId}/statistics")
    @Operation(summary = "Get rating statistics", 
               description = "Get detailed rating statistics for an incident including average and distribution")
    public ResponseEntity<RatingStatistics> getRatingStatistics(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long incidentId) {
        return ResponseEntity.ok(ratingService.getRatingStatistics(incidentId));
    }

    @GetMapping("/incident/{incidentId}/average")
    @Operation(summary = "Get average rating", 
               description = "Get the average rating for an incident")
    public ResponseEntity<Double> getAverageRatingForIncident(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long incidentId) {
        return ResponseEntity.ok(ratingService.getAverageRatingForIncident(incidentId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Create rating", 
               description = "Create a new rating for an incident")
    public ResponseEntity<RatingDTO> createRating(
            @Valid @RequestBody RatingDTO ratingDTO,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return ResponseEntity.ok(ratingService.createRating(ratingDTO, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update rating", 
               description = "Update an existing rating")
    public ResponseEntity<RatingDTO> updateRating(
            @Parameter(description = "Rating ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody RatingDTO ratingDTO,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return ResponseEntity.ok(ratingService.updateRating(id, ratingDTO, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete rating", 
               description = "Soft delete a rating")
    public ResponseEntity<Void> deleteRating(
            @Parameter(description = "Rating ID", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        ratingService.deleteRating(id, userId);
        return ResponseEntity.noContent().build();
    }
}
