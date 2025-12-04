package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.model.Category;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
public class QuizControllerApiTest {

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DtoMapperQuiz dtoMapperQuiz;

    @Autowired
    private MockMvc mockMvc;

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

    private Quiz createTestQuiz(Category category) {
        return Quiz.builder()
                .id(UUID.randomUUID())
                .name("Test Quiz")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .category(category)
                .score(100)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    private QuestionOptionRequest createQuestionOptionRequest(boolean isCorrect) {
        return QuestionOptionRequest.builder()
                .id(UUID.randomUUID())
                .text("Option Text")
                .isCorrect(isCorrect)
                .isSelected(false)
                .build();
    }

    private QuestionRequest createQuestionRequest() {
        return QuestionRequest.builder()
                .id(UUID.randomUUID())
                .name("Test Question")
                .options(List.of(
                        createQuestionOptionRequest(true),
                        createQuestionOptionRequest(false)
                ))
                .build();
    }

    private NewQuizRequest createNewQuizRequest(Category category) {
        return NewQuizRequest.builder()
                .id(UUID.randomUUID())
                .name("Test Quiz")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .category(category)
                .score(100)
                .questions(List.of(createQuestionRequest()))
                .build();
    }

    @Test
    void getQuizzesPage_withAuthenticatedUser_shouldReturn200OkAndQuizzesView() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData(testUser.getId(), "testuser", UserRole.PLAYER);

        Quiz geographyQuiz = createTestQuiz(Category.GEOGRAPHY);
        Quiz historyQuiz = createTestQuiz(Category.HISTORY);
        Quiz musicQuiz = createTestQuiz(Category.MUSIC);

        List<Quiz> geographyQuizzes = List.of(geographyQuiz);
        List<Quiz> historyQuizzes = List.of(historyQuiz);
        List<Quiz> musicQuizzes = List.of(musicQuiz);

        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(quizService.getAllQuizzesByCategory(Category.GEOGRAPHY)).thenReturn(geographyQuizzes);
        when(quizService.getAllQuizzesByCategory(Category.HISTORY)).thenReturn(historyQuizzes);
        when(quizService.getAllQuizzesByCategory(Category.MUSIC)).thenReturn(musicQuizzes);

        mockMvc.perform(get("/quizzes").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("quizzes"))
                .andExpect(model().attributeExists("quizzesGeography", "quizzesHistory", "quizzesMusic", "user"))
                .andExpect(model().attribute("quizzesGeography", geographyQuizzes))
                .andExpect(model().attribute("quizzesHistory", historyQuizzes))
                .andExpect(model().attribute("quizzesMusic", musicQuizzes))
                .andExpect(model().attribute("user", testUser));

        verify(userService).getByUsername("testuser");
        verify(quizService).getAllQuizzesByCategory(Category.GEOGRAPHY);
        verify(quizService).getAllQuizzesByCategory(Category.HISTORY);
        verify(quizService).getAllQuizzesByCategory(Category.MUSIC);
    }

    @Test
    void getQuizzesPage_withEmptyQuizzes_shouldReturn200OkWithEmptyLists() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData(testUser.getId(), "testuser", UserRole.PLAYER);

        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(quizService.getAllQuizzesByCategory(Category.GEOGRAPHY)).thenReturn(List.of());
        when(quizService.getAllQuizzesByCategory(Category.HISTORY)).thenReturn(List.of());
        when(quizService.getAllQuizzesByCategory(Category.MUSIC)).thenReturn(List.of());

        mockMvc.perform(get("/quizzes").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("quizzes"))
                .andExpect(model().attribute("quizzesGeography", List.of()))
                .andExpect(model().attribute("quizzesHistory", List.of()))
                .andExpect(model().attribute("quizzesMusic", List.of()));

        verify(quizService, times(3)).getAllQuizzesByCategory(any(Category.class));
    }

    @Test
    void getQuizzesPage_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(get("/quizzes"))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(quizService);
        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getNewQuizPage_withAdminRole_shouldReturn200OkAndNewQuizView() throws Exception {
        mockMvc.perform(get("/new-quiz"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-quiz"))
                .andExpect(model().attributeExists("newQuizRequest"));

        verifyNoInteractions(quizService);
    }

    @Test
    @WithMockUser(roles = "QUIZMASTER")
    void getNewQuizPage_withQuizmasterRole_shouldReturn200OkAndNewQuizView() throws Exception {
        mockMvc.perform(get("/new-quiz"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-quiz"))
                .andExpect(model().attributeExists("newQuizRequest"));

        verifyNoInteractions(quizService);
    }

    @Test
    void getNewQuizPage_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(get("/new-quiz"))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(quizService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postNewQuiz_withAdminRoleAndValidData_shouldReturn3xxRedirectToQuizzesAndInvokeCreateService() throws Exception {
        MockHttpServletRequestBuilder httpRequest = post("/new-quiz")
                .with(csrf())
                .param("name", "Geography Quiz")
                .param("description", "Test geography questions")
                .param("imageUrl", "https://example.com/geo.jpg")
                .param("category", "GEOGRAPHY")
                .param("score", "100");

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));

        verify(quizService).createNewQuiz(any(NewQuizRequest.class));
    }

    @Test
    @WithMockUser(roles = "QUIZMASTER")
    void postNewQuiz_withQuizmasterRoleAndValidData_shouldReturn3xxRedirectToQuizzesAndInvokeCreateService() throws Exception {
        MockHttpServletRequestBuilder httpRequest = post("/new-quiz")
                .with(csrf())
                .param("name", "Music Quiz")
                .param("description", "Test music questions")
                .param("imageUrl", "https://example.com/music.jpg")
                .param("category", "MUSIC")
                .param("score", "150");

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));

        verify(quizService).createNewQuiz(any(NewQuizRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postNewQuiz_withAdminRoleAndInvalidData_shouldReturn200OkAndNewQuizView() throws Exception {
        MockHttpServletRequestBuilder httpRequest = post("/new-quiz")
                .with(csrf())
                .param("name", "")  // Invalid: blank
                .param("description", "Test")
                .param("imageUrl", "invalid-url")  // Invalid URL
                .param("category", "GEOGRAPHY")
                .param("score", "-10");  // Invalid: negative

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("new-quiz"));

        verify(quizService, never()).createNewQuiz(any());
    }

    @Test
    void getQuizPage_withAuthenticatedUserAndValidId_shouldReturn200OkAndQuizView() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData(testUser.getId(), "testuser", UserRole.PLAYER);
        UUID quizId = UUID.randomUUID();
        Quiz testQuiz = createTestQuiz(Category.HISTORY);
        testQuiz.setId(quizId);
        NewQuizRequest newQuizRequest = createNewQuizRequest(Category.HISTORY);

        when(quizService.getById(quizId)).thenReturn(Optional.of(testQuiz));
        when(dtoMapperQuiz.fromQuizToNewQuizRequest(testQuiz)).thenReturn(newQuizRequest);

        mockMvc.perform(get("/quiz/{id}", quizId).with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attribute("quiz", newQuizRequest));

        verify(quizService).getById(quizId);
        verify(dtoMapperQuiz).fromQuizToNewQuizRequest(testQuiz);
    }

    @Test
    void getQuizPage_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        UUID quizId = UUID.randomUUID();

        mockMvc.perform(get("/quiz/{id}", quizId))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(quizService);
        verifyNoInteractions(dtoMapperQuiz);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_withAdminRoleAndValidId_shouldReturn3xxRedirectToQuizzesAndInvokeDeleteService() throws Exception {
        UUID quizId = UUID.randomUUID();

        mockMvc.perform(delete("/quizzes/delete/{id}", quizId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));

        verify(quizService).deleteQuizById(quizId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_withAdminRoleAndMultipleIds_shouldDeleteCorrectQuizzes() throws Exception {
        UUID quizId1 = UUID.randomUUID();
        UUID quizId2 = UUID.randomUUID();

        mockMvc.perform(delete("/quizzes/delete/{id}", quizId1).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));

        mockMvc.perform(delete("/quizzes/delete/{id}", quizId2).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));

        verify(quizService).deleteQuizById(quizId1);
        verify(quizService).deleteQuizById(quizId2);
    }


    @Test
    void deleteQuiz_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        UUID quizId = UUID.randomUUID();

        mockMvc.perform(delete("/quizzes/delete/{id}", quizId).with(csrf()))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(quizService);
    }


    @Test
    void submitQuiz_withAuthenticatedUserAndZeroScore_shouldReturn3xxRedirectAndUpdateWithZeroScore() throws Exception {
        UUID userId = UUID.randomUUID();
        User testUser = createTestUser();
        testUser.setId(userId);
        UserData userData = createTestUserData(userId, "testuser", UserRole.PLAYER);

        NewQuizRequest quizRequest = createNewQuizRequest(Category.HISTORY);

        when(userService.getById(userId)).thenReturn(testUser);
        when(quizService.getQuizEarnedScore(quizRequest)).thenReturn(0);

        MockHttpServletRequestBuilder httpRequest = post("/quiz/submit")
                .with(user(userData))
                .with(csrf())
                .param("id", quizRequest.getId().toString())
                .param("name", quizRequest.getName())
                .param("category", quizRequest.getCategory().toString())
                .param("score", String.valueOf(quizRequest.getScore()));

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));

        verify(userService).setNewScore(testUser, 0);
        verify(quizService).submitQuiz(any(), eq(testUser));
    }

    @Test
    void submitQuiz_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        NewQuizRequest quizRequest = createNewQuizRequest(Category.MUSIC);

        mockMvc.perform(post("/quiz/submit")
                        .with(csrf())
                        .param("id", quizRequest.getId().toString())
                        .param("name", quizRequest.getName())
                        .param("category", quizRequest.getCategory().toString())
                        .param("score", String.valueOf(quizRequest.getScore())))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(userService);
        verifyNoInteractions(quizService);
    }
}