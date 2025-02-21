package com.driverlink.controller;

import com.driverlink.dto.VoteDTO;
import com.driverlink.security.CurrentUser;
import com.driverlink.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Tag(name = "Vote", description = "Vote management APIs")
public class VoteController {
    private final VoteService voteService;

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<VoteDTO>> getVotesForIncident(@PathVariable Long incidentId) {
        return ResponseEntity.ok(voteService.getVotesForIncident(incidentId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Create vote", description = "Create a new vote for an incident")
    public ResponseEntity<VoteDTO> createVote(
            @Valid @RequestBody VoteDTO voteDTO,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return ResponseEntity.ok(voteService.createVote(voteDTO, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete vote", description = "Soft delete a vote")
    public ResponseEntity<Void> deleteVote(
            @Parameter(description = "Vote ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser Long userId) {
        voteService.deleteVote(id, userId);
        return ResponseEntity.noContent().build();
    }
}
