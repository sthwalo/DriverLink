package com.driverlink.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingDTO {
    private Long id;
    
    @NotNull(message = "Incident ID is required")
    private Long incidentId;
    
    private Long userId;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int value;
    
    @Size(max = 500)
    private String comment;
    
    private LocalDateTime createdAt;
}
