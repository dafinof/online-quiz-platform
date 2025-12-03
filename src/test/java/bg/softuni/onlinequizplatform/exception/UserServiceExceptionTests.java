package bg.softuni.onlinequizplatform.exception;

import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.repository.UserRepository;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceExceptionTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private QuizService quizService;

    @InjectMocks
    private UserService userService;

    public UserServiceExceptionTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_whenUsernameExists_thenThrowsUsernameAlreadyExistException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("pass");
        request.setConfirmPassword("pass");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(request));
    }

    @Test
    void register_whenPasswordsDoNotMatch_thenThrowsPasswordMismatchException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("pass1");
        request.setConfirmPassword("pass2");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());

        assertThrows(PasswordMismatchException.class, () -> userService.register(request));
    }

    @Test
    void getByUsername_whenUserDoesNotExist_thenThrowsUserNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getByUsername("nonexistent"));
    }

    @Test
    void getById_whenUserDoesNotExist_thenThrowsUserNotFoundException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(id));
    }
}
