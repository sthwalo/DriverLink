package com.driverlink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Statistics about votes on an incident")
public class VoteStatistics {
    @Schema(description = "ID of the incident")
    private Long incidentId;

    @Schema(description = "Number of upvotes")
    private Long upvotes;

    @Schema(description = "Number of downvotes")
    private Long downvotes;

    @Schema(description = "Number of reports")
    private Long reports;

    @Schema(description = "Number of unique users who voted")
    private Long uniqueVoters;
}
