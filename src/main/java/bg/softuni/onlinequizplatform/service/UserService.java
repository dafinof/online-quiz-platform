package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.exception.PasswordMismatchException;
import bg.softuni.onlinequizplatform.exception.UserNotFoundException;
import bg.softuni.onlinequizplatform.exception.UsernameAlreadyExistException;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.repository.UserRepository;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.web.dto.*;
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
    private static final String DEFAULT_AVATAR_URL = "https://www.shutterstock.com/image-photo/generate-quiz-night-poster-cartoon-260nw-2471934001.jpg";
    private static final int DEFAULT_SCORE = 0;
    private static final int DEFAULT_LEVEL = 1;
    private final QuizService quizService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, QuizService quizService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.quizService = quizService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return new UserData(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public void register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistException("User Already Exists");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords Mismatch");
        }

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
                .build();

        userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User Not Found"));
    }

    public int getAverageSuccessPercent(User user) {
        List<Quiz> quizzes = new ArrayList<>();
        quizzes = quizService.getAllQuizzesByUser(user.getId());

        return quizzes.isEmpty() ? 0 : user.getScore() / (quizzes.size() * 2);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String username) {
        User user = getByUsername(username);

        userRepository.delete(user);
    }

    public User getById(UUID id) {
        return  userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
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

    public void setNewScore(User user, int quizScore) {
        int score = user.getScore();
        user.setScore(score + quizScore);
        user.setLevel(user.getScore() / 1000 + 1);
        userRepository.save(user);
    }

    public List<UserScoreResponse> getScoresAfterTopThree(List<UserScoreResponse> topScores) {
        if (topScores.size() < 4) {
            return topScores;
        }

        return topScores.subList(3, topScores.size());
    }
}
