package com.driverlink.controller;

import com.driverlink.dto.IncidentDTO;
import com.driverlink.model.IncidentStatus;
import com.driverlink.model.IncidentType;
import com.driverlink.security.CurrentUser;
import com.driverlink.service.IncidentService;
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
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incident", description = "Incident management APIs")
public class IncidentController {
    private final IncidentService incidentService;

    @GetMapping
    @Operation(summary = "Get all incidents", description = "Retrieve all incidents with optional filtering")
    public ResponseEntity<List<IncidentDTO>> getAllIncidents(
            @Parameter(description = "Filter by incident status")
            @RequestParam(required = false) IncidentStatus status,
            @Parameter(description = "Filter by incident type")
            @RequestParam(required = false) IncidentType type,
            @Parameter(description = "Filter by city")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by date range - start")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by date range - end")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(incidentService.getAllIncidents(status, type, city, startDate, endDate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID", description = "Retrieve a specific incident by its ID")
    public ResponseEntity<IncidentDTO> getIncidentById(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby incidents", description = "Find incidents within a specified radius from given coordinates")
    public ResponseEntity<List<IncidentDTO>> getNearbyIncidents(
            @Parameter(description = "Latitude", required = true)
            @RequestParam Double latitude,
            @Parameter(description = "Longitude", required = true)
            @RequestParam Double longitude,
            @Parameter(description = "Radius in kilometers", required = true)
            @RequestParam Double radiusKm) {
        return ResponseEntity.ok(incidentService.getNearbyIncidents(latitude, longitude, radiusKm));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Create incident", description = "Create a new incident report")
    public ResponseEntity<IncidentDTO> createIncident(
            @Valid @RequestBody IncidentDTO incidentDTO,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return ResponseEntity.ok(incidentService.createIncident(incidentDTO, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update incident", description = "Update an existing incident")
    public ResponseEntity<IncidentDTO> updateIncident(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody IncidentDTO incidentDTO) {
        return ResponseEntity.ok(incidentService.updateIncident(id, incidentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete incident", description = "Soft delete an incident")
    public ResponseEntity<Void> deleteIncident(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}
