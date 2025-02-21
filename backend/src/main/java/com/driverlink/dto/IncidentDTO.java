package com.driverlink.dto;

import com.driverlink.model.IncidentStatus;
import com.driverlink.model.IncidentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IncidentDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Location is required")
    private LocationDTO location;
    
    private Long reporterId;
    
    @NotNull(message = "Incident type is required")
    private IncidentType type;
    
    private IncidentStatus status;
    private int verificationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
}
