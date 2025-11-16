package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.repository.UserRepository;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.web.dto.EditProfileRequest;
import bg.softuni.onlinequizplatform.web.dto.EditUserRequest;
import bg.softuni.onlinequizplatform.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_AVATAR_URL = "https://st4.depositphotos.com/4177785/22707/v/450/depositphotos_227075876-stock-illustration-quiz-show-player-color-icon.jpg";
    private static final int DEFAULT_SCORE = 0;
    private static final int DEFAULT_LEVEL = 1;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User Not Found"));

        return new UserData(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public void register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new RuntimeException("User Already Exists");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Password Mismatch");
        }

        List<Quiz> quizzes = new ArrayList<>();

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .avatarUrl(DEFAULT_AVATAR_URL)
                .role(UserRole.PLAYER)
                .score(DEFAULT_SCORE)
                .level(DEFAULT_LEVEL)
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .quizzes(quizzes)
                .build();

        userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public int getAverageSuccessPercent(User user) {
        return user.getQuizzes().isEmpty() ? 0 : user.getScore() / user.getQuizzes().size();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String username) {
        User user = getByUsername(username);

        userRepository.delete(user);
    }

    public User getById(UUID id) {
        return  userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public void updateProfile(EditProfileRequest editProfileRequest) {
        User user = getByUsername(editProfileRequest.getUsername());

        user.setUsername(editProfileRequest.getUsername());
        if (passwordEncoder.matches(passwordEncoder.encode(editProfileRequest.getPassword()), user.getPassword()) && !editProfileRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(editProfileRequest.getPassword()));
        }
        user.setEmail(editProfileRequest.getEmail());
        user.setAvatarUrl(editProfileRequest.getAvatarUrl());
        user.setScore(editProfileRequest.getScore());
        user.setLevel(editProfileRequest.getLevel());

        userRepository.save(user);
    }

    public void updateUserProfile(String username, EditUserRequest editUserRequest) {
        User user = getByUsername(username);
        user.setUsername(editUserRequest.getUsername());
        user.setAvatarUrl(editUserRequest.getAvatarUrl());
        user.setScore(editUserRequest.getScore());
        user.setLevel(editUserRequest.getLevel());
        if (!editUserRequest.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(editUserRequest.getNewPassword()));
        }
        user.setRole(editUserRequest.getRole());
        user.setActive(editUserRequest.isActive());
        user.setEmail(editUserRequest.getEmail());

        userRepository.save(user);
    }

    public List<User> getUsersByRole(UserRole userRole) {
        return userRepository.findByRole(userRole);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
