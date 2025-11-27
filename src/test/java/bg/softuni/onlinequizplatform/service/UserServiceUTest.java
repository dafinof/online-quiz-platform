package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    // 1. mock all dependencies

    @Mock
    private  UserRepository userRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private static final String DEFAULT_AVATAR_URL = "https://st4.depositphotos.com/4177785/22707/v/450/depositphotos_227075876-stock-illustration-quiz-show-player-color-icon.jpg";
    @Mock
    private static final int DEFAULT_SCORE = 0;
    @Mock
    private static final int DEFAULT_LEVEL = 1;

    //2. inject all mocks
    @InjectMocks
    private UserService userService;

    // 3. Scenario to test
}
