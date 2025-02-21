package com.driverlink.controller;

import com.driverlink.dto.CommentDTO;
import com.driverlink.dto.CommentStatistics;
import com.driverlink.security.CurrentUser;
import com.driverlink.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment management APIs")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/incident/{incidentId}")
    @Operation(summary = "Get comments for incident",
            description = "Retrieve paginated comments for a specific incident with optional filtering")
    public ResponseEntity<Page<CommentDTO>> getCommentsForIncident(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long incidentId,
            @Parameter(description = "Filter comments since date")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            @Parameter(description = "Search term in comment content")
            @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsForIncident(incidentId, since, searchTerm, pageable));
    }

    @GetMapping("/incident/{incidentId}/statistics")
    @Operation(summary = "Get comment statistics",
            description = "Get statistics about comments for an incident")
    public ResponseEntity<CommentStatistics> getCommentStatistics(
            @Parameter(description = "Incident ID", required = true)
            @PathVariable Long incidentId) {
        return ResponseEntity.ok(commentService.getCommentStatistics(incidentId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Create comment",
            description = "Create a new comment for an incident")
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return ResponseEntity.ok(commentService.createComment(commentDTO, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update comment",
            description = "Update an existing comment")
    public ResponseEntity<CommentDTO> updateComment(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CommentDTO commentDTO,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDTO, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete comment",
            description = "Soft delete a comment")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
