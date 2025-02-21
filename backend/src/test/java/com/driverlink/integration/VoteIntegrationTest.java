package com.driverlink.integration;

import com.driverlink.dto.VoteDTO;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VoteIntegrationTest {

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
    private VoteRepository voteRepository;

    private User testUser;
    private Incident testIncident;
    private Vote testVote;

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

        // Create test vote
        testVote = new Vote();
        testVote.setVoteType(VoteType.UPVOTE);
        testVote.setUser(testUser);
        testVote.setIncident(testIncident);
        testVote = voteRepository.save(testVote);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getVotesForIncident_ShouldReturnVotes() throws Exception {
        mockMvc.perform(get("/api/votes/incident/{id}", testIncident.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].voteType", equalTo("UPVOTE")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getVoteStatistics_ShouldReturnStatistics() throws Exception {
        mockMvc.perform(get("/api/votes/incident/{id}/statistics", testIncident.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotes", equalTo(1)))
                .andExpect(jsonPath("$.uniqueVoters", equalTo(1)))
                .andExpect(jsonPath("$.distribution.UPVOTE", equalTo(1)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createVote_ShouldCreateNewVote() throws Exception {
        // Delete existing vote first
        voteRepository.delete(testVote);

        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setIncidentId(testIncident.getId());
        voteDTO.setVoteType(VoteType.UPVOTE);

        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteType", equalTo("UPVOTE")))
                .andExpect(jsonPath("$.userId", equalTo(testUser.getId().intValue())));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateVote_ShouldUpdateExistingVote() throws Exception {
        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setVoteType(VoteType.DOWNVOTE);

        mockMvc.perform(put("/api/votes/{id}", testVote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteType", equalTo("DOWNVOTE")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void removeVote_ShouldSoftDeleteVote() throws Exception {
        mockMvc.perform(delete("/api/votes/{id}", testVote.getId()))
                .andExpect(status().isNoContent());

        // Verify vote is soft deleted
        mockMvc.perform(get("/api/votes/incident/{id}", testIncident.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(hasItem(
                        hasProperty("id", equalTo(testVote.getId().intValue()))))));
    }

    @Test
    @WithMockUser(username = "wronguser")
    void updateVote_ShouldFailForWrongUser() throws Exception {
        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setVoteType(VoteType.DOWNVOTE);

        mockMvc.perform(put("/api/votes/{id}", testVote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getVotes_ShouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/votes/incident/{id}", testIncident.getId()))
                .andExpect(status().isUnauthorized());
    }
}
