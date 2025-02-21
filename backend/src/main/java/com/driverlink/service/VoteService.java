package com.driverlink.service;

import com.driverlink.dto.VoteDTO;
import com.driverlink.dto.VoteStatistics;
import com.driverlink.exception.ResourceNotFoundException;
import com.driverlink.exception.ValidationException;
import com.driverlink.exception.ResourceAccessDeniedException;
import com.driverlink.model.Incident;
import com.driverlink.model.User;
import com.driverlink.model.Vote;
import com.driverlink.model.VoteType;
import com.driverlink.repository.IncidentRepository;
import com.driverlink.repository.UserRepository;
import com.driverlink.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;

    /**
     * Get votes for an incident
     */
    @Transactional(readOnly = true)
    public List<VoteDTO> getVotesForIncident(Long incidentId) {
        validateIncidentExists(incidentId);
        return voteRepository.findByIncidentIdAndActiveTrue(incidentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get vote statistics for an incident
     */
    @Transactional(readOnly = true)
    public VoteStatistics getVoteStatistics(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new ResourceNotFoundException("Incident", "id", incidentId);
        }

        List<Object[]> voteCounts = voteRepository.countActiveVotesByType(incidentId);
        Long uniqueVoters = voteRepository.countUniqueVoters(incidentId);

        Map<VoteType, Long> votesByType = new HashMap<>();
        for (Object[] result : voteCounts) {
            VoteType type = (VoteType) result[0];
            Long count = (Long) result[1];
            votesByType.put(type, count);
        }

        return VoteStatistics.builder()
                .incidentId(incidentId)
                .upvotes(votesByType.getOrDefault(VoteType.UPVOTE, 0L))
                .downvotes(votesByType.getOrDefault(VoteType.DOWNVOTE, 0L))
                .reports(votesByType.getOrDefault(VoteType.REPORT, 0L))
                .uniqueVoters(uniqueVoters)
                .build();
    }

    /**
     * Create or update a vote
     */
    @Transactional
    public VoteDTO vote(VoteDTO voteDTO, Long userId) {
        validateVoteInput(voteDTO);
        User user = getUserById(userId);
        Incident incident = getIncidentById(voteDTO.getIncidentId());

        // Check if user has already voted
        Vote existingVote = voteRepository.findByIncidentIdAndUserIdAndActiveTrue(
                voteDTO.getIncidentId(), userId).orElse(null);

        if (existingVote != null) {
            // Update existing vote
            VoteType voteType;
            try {
                voteType = VoteType.valueOf(voteDTO.getVoteType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid vote type: " + voteDTO.getVoteType() + 
                    ". Valid types are: " + String.join(", ", 
                    java.util.Arrays.stream(VoteType.values())
                        .map(VoteType::name)
                        .toArray(String[]::new)));
            }
            existingVote.setVoteType(voteType);
            existingVote.setUpdatedAt(LocalDateTime.now());
            return convertToDTO(voteRepository.save(existingVote));
        }

        // Create new vote
        Vote vote = new Vote();
        vote.setVoteType(VoteType.valueOf(voteDTO.getVoteType().toUpperCase()));
        vote.setIncident(incident);
        vote.setUser(user);
        
        return convertToDTO(voteRepository.save(vote));
    }

    /**
     * Create a new vote or update existing one
     */
    @Transactional
    public VoteDTO createVote(VoteDTO voteDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Incident incident = incidentRepository.findById(voteDTO.getIncidentId())
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", voteDTO.getIncidentId()));

        // Validate and convert vote type
        VoteType voteType;
        try {
            voteType = VoteType.valueOf(voteDTO.getVoteType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid vote type: " + voteDTO.getVoteType() + 
                ". Valid types are: " + String.join(", ", 
                java.util.Arrays.stream(VoteType.values())
                    .map(VoteType::name)
                    .toArray(String[]::new)));
        }

        // Check if user already has an active vote for this incident
        Optional<Vote> existingVote = voteRepository.findByIncidentIdAndUserIdAndActiveTrue(
                incident.getId(), userId);

        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                throw new ValidationException("You have already voted " + voteType + " for this incident");
            }
            // Update existing vote
            vote.setVoteType(voteType);  
            vote.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new vote
            vote = new Vote();
            vote.setUser(user);
            vote.setIncident(incident);
            vote.setVoteType(voteType);  
            vote.setActive(true);
        }

        vote = voteRepository.save(vote);
        return convertToDTO(vote);
    }

    /**
     * Remove a vote
     */
    @Transactional
    public void removeVote(Long id, Long userId) {
        Vote vote = getVoteById(id);
        validateVoteOwnership(vote, userId);

        vote.setActive(false);
        voteRepository.save(vote);
    }

    /**
     * Delete a vote
     */
    @Transactional
    public void deleteVote(Long voteId, Long userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "id", voteId));

        if (!vote.getUser().getId().equals(userId)) {
            throw new ResourceAccessDeniedException("You are not authorized to delete this vote");
        }

        vote.setActive(false);
        voteRepository.save(vote);
    }

    /**
     * Check if user has voted on an incident
     */
    @Transactional(readOnly = true)
    public boolean hasUserVoted(Long incidentId, Long userId) {
        return voteRepository.existsByIncidentIdAndUserIdAndActiveTrue(incidentId, userId);
    }

    private Vote getVoteById(Long id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "id", id));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Incident getIncidentById(Long incidentId) {
        return incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", incidentId));
    }

    private void validateIncidentExists(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new ResourceNotFoundException("Incident", "id", incidentId);
        }
    }

    private void validateVoteInput(VoteDTO dto) {
        if (dto.getVoteType() == null) {
            throw new ValidationException("Vote type must be specified");
        }
    }

    private void validateVoteOwnership(Vote vote, Long userId) {
        if (!vote.getUser().getId().equals(userId)) {
            throw new ResourceAccessDeniedException("You are not authorized to modify this vote");
        }
    }

    private VoteDTO convertToDTO(Vote vote) {
        VoteDTO dto = new VoteDTO();
        dto.setId(vote.getId());
        dto.setIncidentId(vote.getIncident().getId());
        dto.setUserId(vote.getUser().getId());
        dto.setUsername(vote.getUser().getUsername());
        dto.setVoteType(vote.getVoteType().name());
        dto.setActive(vote.isActive());
        dto.setCreatedAt(vote.getCreatedAt());
        dto.setUpdatedAt(vote.getUpdatedAt());
        return dto;
    }
}
