package com.driverlink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Data Transfer Object for Vote")
public class VoteDTO {
    @Schema(description = "Vote ID")
    private Long id;
    
    @Schema(description = "ID of the incident being voted on")
    @NotNull(message = "Incident ID is required")
    private Long incidentId;
    
    @Schema(description = "ID of the user who voted")
    private Long userId;
    
    @Schema(description = "Username of the voter")
    private String username;
    
    @Schema(description = "Type of vote (UPVOTE, DOWNVOTE, REPORT)")
    @NotNull(message = "Vote type is required")
    private String voteType;
    
    @Schema(description = "Whether the vote is active")
    private boolean active;
    
    @Schema(description = "When the vote was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "When the vote was last updated")
    private LocalDateTime updatedAt;
}
