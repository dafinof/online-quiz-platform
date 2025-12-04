package bg.softuni.onlinequizplatform;

import bg.softuni.onlinequizplatform.client.LeaderboardClient;
import bg.softuni.onlinequizplatform.model.Category;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.repository.QuizRepository;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.web.dto.CreateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.NewQuizRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionOptionRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Transactional
public class PostQuizITest {
    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepository quizRepository;

    @MockitoBean
    private LeaderboardClient leaderboardClient;

    @Test
    void submitQuiz_updatesScore_savesQuizAndCallsLeaderboard() {

        Quiz quiz = Quiz.builder()
                .name("Java Basics")
                .category(Category.GEOGRAPHY)
                .score(100)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        quiz = quizRepository.save(quiz);

        User user = User.builder()
                .username("tester")
                .password("pass")
                .avatarUrl("avatar.png")
                .email("t@t.com")
                .role(UserRole.PLAYER)
                .score(200)
                .level(1)
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        user.setId(UUID.randomUUID());

        QuestionOptionRequest opt1 = new QuestionOptionRequest(UUID.randomUUID(), "A", true, true);
        QuestionOptionRequest opt2 = new QuestionOptionRequest(UUID.randomUUID(),"B", false, false);

        QuestionRequest q1 = QuestionRequest.builder()
                .name("Java Basics")
                .options(List.of(opt1, opt2))
                .build();

        NewQuizRequest request = new NewQuizRequest();
        request.setId(quiz.getId());
        request.setName("Java Basics");
        request.setCategory(Category.GEOGRAPHY);
        request.setScore(100);
        request.setQuestions(List.of(q1));

        when(leaderboardClient.upsertScore(any(CreateScoreRequest.class), eq(user.getId())))
                .thenReturn(null);

        quizService.submitQuiz(request, user);

        Quiz updated = quizRepository.findById(quiz.getId()).orElseThrow();

        assertEquals(100, updated.getEarnedScore(), "Earned score must be calculated correctly");
        assertEquals(user.getId(), updated.getUser().getId(), "User must be assigned to quiz");

        ArgumentCaptor<CreateScoreRequest> requestCaptor =
                ArgumentCaptor.forClass(CreateScoreRequest.class);

        verify(leaderboardClient, times(1))
                .upsertScore(requestCaptor.capture(), eq(user.getId()));

        CreateScoreRequest sentRequest = requestCaptor.getValue();

        assertEquals(user.getId(), sentRequest.getUserId());
        assertEquals(user.getScore(), sentRequest.getScore());
        assertEquals(user.getUsername(), sentRequest.getUsername());
    }
}
