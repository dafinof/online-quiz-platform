package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.client.LeaderboardClient;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.UserScoreResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
public class LeaderboardControllerApiTest {

    @MockitoBean
    private LeaderboardClient leaderboardClient;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    // Test data
    private User createTestUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encoded_password")
                .email("test@example.com")
                .avatarUrl("https://example.com/avatar.jpg")
                .role(UserRole.PLAYER)
                .score(100)
                .level(1)
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    private UserData createTestUserData(UUID userId, String username, UserRole role) {
        return new UserData(
                userId,
                username,
                "encoded_password",
                role,
                true
        );
    }

    private UserScoreResponse createUserScoreResponse(UUID userId, String username, int score) {
        return UserScoreResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username(username)
                .avatarUrl("https://example.com/avatar.jpg")
                .score(score)
                .updatedOn(LocalDateTime.now())
                .build();
    }

    // ==================== GET /leaderboard Tests ====================

    @Test
    void showLeaderboard_withAuthenticatedUser_shouldReturn200OkAndLeaderboardView() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        User testUser = createTestUser();
        UserData userData = createTestUserData(userId, "testuser", UserRole.PLAYER);

        UserScoreResponse topScore1 = createUserScoreResponse(UUID.randomUUID(), "player1", 5000);
        UserScoreResponse topScore2 = createUserScoreResponse(UUID.randomUUID(), "player2", 4500);
        UserScoreResponse topScore3 = createUserScoreResponse(UUID.randomUUID(), "player3", 4000);
        UserScoreResponse otherScore = createUserScoreResponse(UUID.randomUUID(), "player4", 3500);

        List<UserScoreResponse> topScores = List.of(topScore1, topScore2, topScore3, otherScore);
        List<UserScoreResponse> scoresAfterTopThree = List.of(otherScore);

        when(leaderboardClient.getTopScores()).thenReturn(topScores);
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getScoresAfterTopThree(topScores)).thenReturn(scoresAfterTopThree);

        // Act & Assert
        mockMvc.perform(get("/leaderboard").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("leaderboard"))
                .andExpect(model().attributeExists("topScores", "user", "scoresAfterTopThree"))
                .andExpect(model().attribute("topScores", topScores))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("scoresAfterTopThree", scoresAfterTopThree));

        verify(leaderboardClient).getTopScores();
        verify(userService).getById(userId);
        verify(userService).getScoresAfterTopThree(topScores);
    }

    @Test
    void showLeaderboard_withEmptyLeaderboard_shouldReturn200OkWithEmptyLists() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        User testUser = createTestUser();
        UserData userData = createTestUserData(userId, "testuser", UserRole.PLAYER);

        List<UserScoreResponse> emptyTopScores = List.of();
        List<UserScoreResponse> emptyScoresAfterTopThree = List.of();

        when(leaderboardClient.getTopScores()).thenReturn(emptyTopScores);
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getScoresAfterTopThree(emptyTopScores)).thenReturn(emptyScoresAfterTopThree);

        // Act & Assert
        mockMvc.perform(get("/leaderboard").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("leaderboard"))
                .andExpect(model().attributeExists("topScores", "user", "scoresAfterTopThree"))
                .andExpect(model().attribute("topScores", emptyTopScores))
                .andExpect(model().attribute("scoresAfterTopThree", emptyScoresAfterTopThree));

        verify(leaderboardClient).getTopScores();
        verify(userService).getById(userId);
    }

    @Test
    void showLeaderboard_withOnlyTopThreeScores_shouldReturn200OkWithEmptyScoresAfterTopThree() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        User testUser = createTestUser();
        UserData userData = createTestUserData(userId, "testuser", UserRole.PLAYER);

        UserScoreResponse topScore1 = createUserScoreResponse(UUID.randomUUID(), "player1", 5000);
        UserScoreResponse topScore2 = createUserScoreResponse(UUID.randomUUID(), "player2", 4500);
        UserScoreResponse topScore3 = createUserScoreResponse(UUID.randomUUID(), "player3", 4000);

        List<UserScoreResponse> topScores = List.of(topScore1, topScore2, topScore3);
        List<UserScoreResponse> scoresAfterTopThree = List.of();

        when(leaderboardClient.getTopScores()).thenReturn(topScores);
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getScoresAfterTopThree(topScores)).thenReturn(scoresAfterTopThree);

        // Act & Assert
        mockMvc.perform(get("/leaderboard").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("leaderboard"))
                .andExpect(model().attribute("scoresAfterTopThree", scoresAfterTopThree));

        verify(leaderboardClient).getTopScores();
    }

    @Test
    void showLeaderboard_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(leaderboardClient);
        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAll_withAdminRole_shouldReturn3xxRedirectToLeaderboardAndInvokeDeleteAllScores() throws Exception {
        MockHttpServletRequestBuilder httpRequest = delete("/api/scores")
                .with(csrf());

        // Act & Assert
        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leaderboard"));

        verify(leaderboardClient).deleteAllScores();
    }
    @Test
    void deleteAll_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        MockHttpServletRequestBuilder httpRequest = delete("/api/scores")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(leaderboardClient);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteScore_withAdminRoleAndValidId_shouldReturn3xxRedirectToLeaderboardAndInvokeDeleteScore() throws Exception {
        UUID scoreId = UUID.randomUUID();

        MockHttpServletRequestBuilder httpRequest = delete("/api/scores/{id}", scoreId)
                .with(csrf());

        // Act & Assert
        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leaderboard"));

        verify(leaderboardClient).deleteScore(scoreId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteScore_withAdminRoleAndDifferentIds_shouldDeleteCorrectScores() throws Exception {
        UUID scoreId1 = UUID.randomUUID();
        UUID scoreId2 = UUID.randomUUID();

        // Delete first score
        mockMvc.perform(delete("/api/scores/{id}", scoreId1).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leaderboard"));

        mockMvc.perform(delete("/api/scores/{id}", scoreId2).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leaderboard"));

        verify(leaderboardClient).deleteScore(scoreId1);
        verify(leaderboardClient).deleteScore(scoreId2);
    }
}