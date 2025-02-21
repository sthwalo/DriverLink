package com.driverlink.service;

import com.driverlink.dto.CommentDTO;
import com.driverlink.dto.CommentStatistics;
import com.driverlink.exception.ResourceNotFoundException;
import com.driverlink.exception.ValidationException;
import com.driverlink.model.Comment;
import com.driverlink.model.Incident;
import com.driverlink.model.User;
import com.driverlink.repository.CommentRepository;
import com.driverlink.repository.IncidentRepository;
import com.driverlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    
    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final int MIN_COMMENT_LENGTH = 5;

    /**
     * Get paginated comments for an incident with optional filtering
     */
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsForIncident(
            Long incidentId, 
            LocalDateTime since,
            String searchTerm,
            Pageable pageable) {
        validateIncidentExists(incidentId);
        return commentRepository.findCommentsWithFilters(incidentId, since, searchTerm, true, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Get comment statistics for an incident
     */
    @Transactional(readOnly = true)
    public CommentStatistics getCommentStatistics(Long incidentId) {
        validateIncidentExists(incidentId);
        return CommentStatistics.builder()
                .totalComments(commentRepository.countByIncidentIdAndActive(incidentId, true))
                .uniqueCommenters(commentRepository.countUniqueCommenters(incidentId))
                .lastCommentDate(commentRepository.findLastCommentDate(incidentId))
                .build();
    }

    /**
     * Create a new comment
     */
    @Transactional
    public CommentDTO createComment(CommentDTO dto, Long userId) {
        validateCommentInput(dto);
        User user = getUserById(userId);
        Incident incident = getIncidentById(dto.getIncidentId());

        // Check for spam (multiple comments in short time)
        validateNotSpamming(userId, dto.getIncidentId());

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setIncident(incident);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);
        return convertToDTO(saved);
    }

    /**
     * Update an existing comment
     */
    @Transactional
    public CommentDTO updateComment(Long id, CommentDTO dto, Long userId) {
        validateCommentInput(dto);
        Comment comment = getCommentById(id);
        validateCommentOwnership(comment, userId);
        validateUpdateTimeframe(comment);

        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);
        return convertToDTO(updated);
    }

    /**
     * Delete a comment
     */
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = getCommentById(id);
        validateCommentOwnership(comment, userId);

        comment.setActive(false);
        commentRepository.save(comment);
    }

    private Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Incident getIncidentById(Long incidentId) {
        return incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
    }

    private void validateIncidentExists(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new ResourceNotFoundException("Incident not found");
        }
    }

    private void validateCommentInput(CommentDTO dto) {
        if (dto.getContent() == null || dto.getContent().trim().length() < MIN_COMMENT_LENGTH) {
            throw new ValidationException("Comment must be at least " + MIN_COMMENT_LENGTH + " characters long");
        }
        if (dto.getContent().length() > MAX_COMMENT_LENGTH) {
            throw new ValidationException("Comment cannot exceed " + MAX_COMMENT_LENGTH + " characters");
        }
        // Check for inappropriate content
        if (containsInappropriateContent(dto.getContent())) {
            throw new ValidationException("Comment contains inappropriate content");
        }
    }

    private void validateCommentOwnership(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User can only modify their own comments");
        }
    }

    private void validateUpdateTimeframe(Comment comment) {
        // Only allow updates within 30 minutes of creation
        if (comment.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new ValidationException("Comments can only be edited within 30 minutes of creation");
        }
    }

    private void validateNotSpamming(Long userId, Long incidentId) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);
        long recentComments = commentRepository.countRecentCommentsByUser(userId, incidentId, threshold);
        if (recentComments >= 3) {
            throw new ValidationException("Please wait before posting more comments");
        }
    }

    private boolean containsInappropriateContent(String content) {
        // Implement content moderation logic here
        // This could integrate with a content moderation service or use a list of banned words
        return false; // Placeholder implementation
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setIncidentId(comment.getIncident().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
