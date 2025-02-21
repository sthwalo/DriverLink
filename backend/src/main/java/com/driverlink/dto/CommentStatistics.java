package com.driverlink.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentStatistics {
    private Long totalComments;
    private Long uniqueCommenters;
    private LocalDateTime lastCommentDate;
}
