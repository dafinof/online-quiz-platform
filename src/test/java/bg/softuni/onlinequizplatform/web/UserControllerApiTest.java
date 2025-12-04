package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private QuizService quizService;

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

    private UserData createTestUserData() {
        return new UserData(
                UUID.randomUUID(),
                "testuser",
                "encoded_password",
                UserRole.PLAYER,
                true
        );
    }

    private Quiz createTestQuiz() {
        return Quiz.builder()
                .id(UUID.randomUUID())
                .name("Test Quiz")
                .description("Test Description")
                .score(100)
                .build();
    }

    @Test
    void getHomePage_withAuthenticatedUser_shouldReturn200OkAndHomeView() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData();
        List<Quiz> quizzes = List.of(createTestQuiz());

        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(userService.getAverageSuccessPercent(testUser)).thenReturn(50);
        when(quizService.getAllQuizzesByUser(testUser.getId())).thenReturn(quizzes);

        mockMvc.perform(get("/home").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user", "averageScore", "quizzes"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("averageScore", 50))
                .andExpect(model().attribute("quizzes", quizzes));

        verify(userService).getByUsername("testuser");
        verify(userService).getAverageSuccessPercent(testUser);
        verify(quizService).getAllQuizzesByUser(testUser.getId());
    }

    @Test
    void getHomePage_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(userService);
        verifyNoInteractions(quizService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersPage_withAdminRole_shouldReturn200OkAndUsersView() throws Exception {
        List<User> allUsers = List.of(createTestUser());
        List<User> players = List.of(createTestUser());
        List<User> admins = List.of();
        List<User> quizmasters = List.of();

        when(userService.getAllUsers()).thenReturn(allUsers);
        when(userService.getUsersByRole(UserRole.PLAYER)).thenReturn(players);
        when(userService.getUsersByRole(UserRole.ADMIN)).thenReturn(admins);
        when(userService.getUsersByRole(UserRole.QUIZMASTER)).thenReturn(quizmasters);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users", "usersPlayer", "usersAdmin", "usersQuizMaster"))
                .andExpect(model().attribute("users", allUsers))
                .andExpect(model().attribute("usersPlayer", players));

        verify(userService).getAllUsers();
        verify(userService).getUsersByRole(UserRole.PLAYER);
        verify(userService).getUsersByRole(UserRole.ADMIN);
        verify(userService).getUsersByRole(UserRole.QUIZMASTER);
    }

    @Test
    void getUsersPage_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editUserPage_withAdminRole_shouldReturn200OkAndEditUserView() throws Exception {
        User testUser = createTestUser();

        when(userService.getByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/users/edit/testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-user"))
                .andExpect(model().attributeExists("user", "editUserRequest"));

        verify(userService).getByUsername("testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editUser_withAdminRoleAndValidData_shouldReturn3xxRedirectToUsersAndInvokeUpdateService() throws Exception {
        User testUser = createTestUser();

        when(userService.getByUsername("testuser")).thenReturn(testUser);

        MockHttpServletRequestBuilder httpRequest = put("/users/edit/testuser")
                .with(csrf())
                .formField("username", "updateduser")
                .formField("email", "updated@example.com")
                .formField("avatarUrl", "https://example.com/new-avatar.jpg")
                .formField("score", "200")
                .formField("level", "2")
                .formField("role", "PLAYER")
                .formField("active", "true")
                .formField("newPassword", "");

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).getByUsername("testuser");
        verify(userService).updateUserProfile(eq("testuser"), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editUser_withAdminRoleAndInvalidData_shouldReturn200OkAndEditUserView() throws Exception {
        User testUser = createTestUser();

        when(userService.getByUsername("testuser")).thenReturn(testUser);

        MockHttpServletRequestBuilder httpRequest = put("/users/edit/testuser")
                .with(csrf())
                .formField("username", "short")  // Invalid: too short
                .formField("email", "invalid-email")  // Invalid email format
                .formField("avatarUrl", "not-a-url")  // Invalid URL
                .formField("score", "-10")  // Invalid: negative score
                .formField("role", "PLAYER");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-user"));

        verify(userService).getByUsername("testuser");
        verify(userService, never()).updateUserProfile(anyString(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_withAdminRole_shouldReturn3xxRedirectToUsersAndInvokeDeleteService() throws Exception {
        mockMvc.perform(post("/users/delete/testuser")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).deleteUser("testuser");
    }

    @Test
    void deleteUser_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(post("/users/delete/testuser").with(csrf()))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(userService);
    }

    @Test
    void editProfilePage_withAuthenticatedUser_shouldReturn200OkAndEditProfileView() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData();

        when(userService.getByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/edit-profile").with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("user", "editProfileRequest"));

        verify(userService).getByUsername("testuser");
    }

    @Test
    void editProfilePage_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(get("/edit-profile"))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(userService);
    }

    @Test
    void editProfile_withValidData_shouldReturn3xxRedirectToHomeAndInvokeUpdateService() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData();

        when(userService.getByUsername("testuser")).thenReturn(testUser);

        MockHttpServletRequestBuilder httpRequest = put("/edit-profile")
                .with(user(userData))
                .with(csrf())
                .formField("username", "updateduser")
                .formField("email", "updated@example.com")
                .formField("avatarUrl", "https://example.com/new-avatar.jpg")
                .formField("password", "")
                .formField("newPassword", "")
                .formField("score", "150")
                .formField("level", "2");

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService).getByUsername("testuser");
        verify(userService).updateProfile(any());
    }

    @Test
    void editProfile_withInvalidData_shouldReturn200OkAndEditProfileView() throws Exception {
        User testUser = createTestUser();
        UserData userData = createTestUserData();

        when(userService.getByUsername("testuser")).thenReturn(testUser);

        MockHttpServletRequestBuilder httpRequest = put("/edit-profile")
                .with(user(userData))
                .with(csrf())
                .formField("username", "")
                .formField("email", "invalid-email")
                .formField("avatarUrl", "invalid-url")
                .formField("password", "")
                .formField("newPassword", "");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"));

        verify(userService).getByUsername("testuser");
        verify(userService, never()).updateProfile(any());
    }

    @Test
    void editProfile_withoutAuthentication_shouldReturn302RedirectToLogin() throws Exception {
        mockMvc.perform(put("/edit-profile").with(csrf()))
                .andExpect(status().is3xxRedirection());

        verifyNoInteractions(userService);
    }
}
