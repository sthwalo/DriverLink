package com.driverlink.integration;

import com.driverlink.dto.CommentDTO;
import com.driverlink.model.*;
import com.driverlink.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User testUser;
    private Incident testIncident;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        // Create test location
        Location location = new Location();
        location.setLatitude(-26.2041);
        location.setLongitude(28.0473);
        location.setCity("Johannesburg");
        location = locationRepository.save(location);

        // Create test incident
        testIncident = new Incident();
        testIncident.setTitle("Test Incident");
        testIncident.setDescription("Test Description");
        testIncident.setType(IncidentType.ACCIDENT);
        testIncident.setStatus(IncidentStatus.PENDING);
        testIncident.setLocation(location);
        testIncident.setReporter(testUser);
        testIncident = incidentRepository.save(testIncident);

        // Create test comment
        testComment = new Comment();
        testComment.setContent("Test Comment");
        testComment.setUser(testUser);
        testComment.setIncident(testIncident);
        testComment = commentRepository.save(testComment);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCommentsForIncident_ShouldReturnComments() throws Exception {
        mockMvc.perform(get("/api/comments/incident/{id}", testIncident.getId())
                .param("since", LocalDateTime.now().minusDays(1).toString())
                .param("searchTerm", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].content", containsString("Test Comment")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createComment_ShouldCreateNewComment() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setIncidentId(testIncident.getId());
        commentDTO.setContent("New Test Comment");

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", equalTo("New Test Comment")))
                .andExpect(jsonPath("$.userId", equalTo(testUser.getId().intValue())));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateComment_ShouldUpdateExistingComment() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("Updated Comment");

        mockMvc.perform(put("/api/comments/{id}", testComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", equalTo("Updated Comment")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteComment_ShouldSoftDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", testComment.getId()))
                .andExpect(status().isNoContent());

        // Verify comment is soft deleted
        mockMvc.perform(get("/api/comments/incident/{id}", testIncident.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(hasItem(
                        hasProperty("id", equalTo(testComment.getId().intValue()))))));
    }

    @Test
    @WithMockUser(username = "wronguser")
    void updateComment_ShouldFailForWrongUser() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("Updated Comment");

        mockMvc.perform(put("/api/comments/{id}", testComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createComment_ShouldFailForSpamming() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setIncidentId(testIncident.getId());
        commentDTO.setContent("Spam Comment");

        // Create multiple comments quickly
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post("/api/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentDTO)));
        }

        // The last one should fail
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("wait before posting")));
    }

    @Test
    void getComments_ShouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/comments/incident/{id}", testIncident.getId()))
                .andExpect(status().isUnauthorized());
    }
}
