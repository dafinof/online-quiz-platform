package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.exception.PasswordMismatchException;
import bg.softuni.onlinequizplatform.exception.UserNotFoundException;
import bg.softuni.onlinequizplatform.exception.UsernameAlreadyExistException;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.repository.UserRepository;
import bg.softuni.onlinequizplatform.web.dto.RegisterRequest;
import bg.softuni.onlinequizplatform.web.dto.UserScoreResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizService quizService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encodedpass")
                .avatarUrl("avatar")
                .email("email@test.com")
                .role(UserRole.PLAYER)
                .score(500)
                .level(1)
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    // ==========================================================
    // loadUserByUsername()
    // ==========================================================
    @Test
    void loadUserByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        UserDetails result = service.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }

    // ==========================================================
    // register()
    // ==========================================================
    @Test
    void register_ShouldCreateUser_WhenValid() {
        RegisterRequest req = RegisterRequest.builder()
                .username("newuser")
                .password("123456")
                .confirmPassword("123456")
                .email("a@a.com")
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encoded123");

        service.register(req);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldFail_WhenUsernameExists() {
        RegisterRequest req = RegisterRequest.builder()
                .username("testuser")
                .password("123456")
                .confirmPassword("123456")
                .email("a@a.com")
                .build();

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        assertThrows(UsernameAlreadyExistException.class, () -> service.register(req));
    }

    @Test
    void register_ShouldFail_WhenPasswordsMismatch() {
        RegisterRequest req = RegisterRequest.builder()
                .username("abc123")
                .password("123456")
                .confirmPassword("000000")
                .email("a@a.com")
                .build();

        when(userRepository.findByUsername("abc123"))
                .thenReturn(Optional.empty());

        assertThrows(PasswordMismatchException.class, () -> service.register(req));
    }

    // ==========================================================
    // getByUsername()
    // ==========================================================
    @Test
    void getByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        User result = service.getByUsername("testuser");

        assertEquals(testUser, result);
    }

    @Test
    void getByUsername_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getByUsername("missing"));
    }

    // ==========================================================
    // getById()
    // ==========================================================
    @Test
    void getById_ShouldReturnUser() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        User result = service.getById(testUser.getId());

        assertEquals(testUser, result);
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(userRepository.findById(missingId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getById(missingId));
    }

    // ==========================================================
    // deleteUser()
    // ==========================================================
    @Test
    void deleteUser_ShouldDelete() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        service.deleteUser("testuser");

        verify(userRepository).delete(testUser);
    }

    // ==========================================================
    // getAverageSuccessPercent()
    // ==========================================================
    @Test
    void getAverageSuccessPercent_ShouldReturnZero_WhenNoQuizzes() {
        when(quizService.getAllQuizzesByUser(testUser.getId()))
                .thenReturn(List.of());

        int result = service.getAverageSuccessPercent(testUser);

        assertEquals(0, result);
    }

    @Test
    void getAverageSuccessPercent_ShouldCalculateCorrectly() {
        List<Quiz> quizzes = List.of(new Quiz(), new Quiz());
        testUser.setScore(200);

        when(quizService.getAllQuizzesByUser(testUser.getId()))
                .thenReturn(quizzes);

        int result = service.getAverageSuccessPercent(testUser);

        assertEquals(200 / (2 * 2), result); // score / (quizCount * 2)
    }

    // ==========================================================
    // setNewScore()
    // ==========================================================
    @Test
    void setNewScore_ShouldIncreaseScore_AndSave() {
        testUser.setScore(500);
        testUser.setLevel(1);

        when(userRepository.save(testUser)).thenReturn(testUser);

        service.setNewScore(testUser, 300); // new score = 800

        assertEquals(800, testUser.getScore());
        assertEquals(800 / 1000 + 1, testUser.getLevel());
        verify(userRepository).save(testUser);
    }

    @Test
    void setNewScore_ShouldPromoteRole_WhenOver10000() {
        testUser.setScore(10050);
        testUser.setRole(UserRole.PLAYER);

        service.setNewScore(testUser, 100);

        assertEquals(UserRole.QUIZMASTER, testUser.getRole());
    }

    // ==========================================================
    // getUsersByRole()
    // ==========================================================
    @Test
    void getUsersByRole_ShouldReturnList() {
        when(userRepository.findByRole(UserRole.PLAYER))
                .thenReturn(List.of(testUser));

        List<User> result = service.getUsersByRole(UserRole.PLAYER);

        assertEquals(1, result.size());
    }

    // ==========================================================
    // getScoresAfterTopThree()
    // ==========================================================
    @Test
    void getScoresAfterTopThree_ShouldReturnAll_WhenLessThanFour() {
        List<UserScoreResponse> list = List.of(
                new UserScoreResponse(), new UserScoreResponse()
        );

        List<UserScoreResponse> result = service.getScoresAfterTopThree(list);

        assertEquals(list, result);
    }

    @Test
    void getScoresAfterTopThree_ShouldReturnSublist_WhenMoreThanThree() {
        List<UserScoreResponse> list = List.of(
                new UserScoreResponse(),
                new UserScoreResponse(),
                new UserScoreResponse(),
                new UserScoreResponse(),
                new UserScoreResponse()
        );

        List<UserScoreResponse> result = service.getScoresAfterTopThree(list);

        assertEquals(2, result.size());
    }


}
