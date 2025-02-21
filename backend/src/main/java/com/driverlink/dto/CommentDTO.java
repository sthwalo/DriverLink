package com.driverlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    
    @NotBlank(message = "Comment content is required")
    private String content;
    
    @NotNull(message = "Incident ID is required")
    private Long incidentId;
    
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
