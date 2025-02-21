package com.driverlink.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class RatingStatistics {
    private Double averageRating;
    private Long totalRatings;
    private Map<Integer, Long> ratingDistribution;
}
