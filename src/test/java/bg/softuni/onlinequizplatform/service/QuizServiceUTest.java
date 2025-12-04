package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.client.LeaderboardClient;
import bg.softuni.onlinequizplatform.model.*;
import bg.softuni.onlinequizplatform.repository.QuizRepository;
import bg.softuni.onlinequizplatform.web.dto.CreateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.NewQuizRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionOptionRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceUTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private LeaderboardClient leaderboardClient;

    @InjectMocks
    private QuizService quizService;

    @Test
    void getById_WhenNoSuchId_thenReturnsEmptyOptional() {
        UUID id = UUID.randomUUID();

        when(quizRepository.findById(id)).thenReturn(Optional.empty());

        assertTrue(quizService.getById(id).isEmpty());
    }

    @Test
    void getById_andIdIsPresent_thenReturnsQuiz() {
        UUID id = UUID.randomUUID();
        Quiz quiz = Quiz.builder().id(id).build();
        quizRepository.save(quiz);

        when(quizRepository.findById(id)).thenReturn(Optional.of(quiz));

        assertTrue(quizService.getById(id).isPresent());
    }

    @Test
    void createNewQuiz_savesQuizQuestionsAndOptions() {
        QuizRepository quizRepository = mock(QuizRepository.class);
        QuestionService questionService = mock(QuestionService.class);
        QuestionOptionService questionOptionService = mock(QuestionOptionService.class);
        LeaderboardClient leaderboardClient = mock(LeaderboardClient.class);

        QuizService quizService = new QuizService(
                quizRepository,
                questionService,
                questionOptionService,
                leaderboardClient
        );

        NewQuizRequest req = new NewQuizRequest();
        req.setName("Test Quiz");
        req.setImageUrl("url");
        req.setCategory(Category.GEOGRAPHY);
        req.setScore(100);

        QuestionOptionRequest opt1 = new QuestionOptionRequest(UUID.randomUUID(), "A", true, false);
        QuestionOptionRequest opt2 = new QuestionOptionRequest(UUID.randomUUID(), "B", false, false);

        QuestionRequest q1 = new QuestionRequest();
        q1.setName("Question 1");
        q1.setOptions(List.of(opt1, opt2));

        req.setQuestions(List.of(q1));

        quizService.createNewQuiz(req);

        verify(quizRepository, times(2)).save(any(Quiz.class));
        verify(questionService, times(2)).save(any(Question.class));
        verify(questionOptionService, times(2)).save(any(QuestionOption.class));
    }

    @Test
    void getAllQuizzesByCategory_whenQuizExists_thenReturnsQuizList() {
        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz = Quiz.builder().
                id(UUID.randomUUID())
                .category(Category.GEOGRAPHY)
                .build();

        quizzes.add(quiz);

        when(quizRepository.findByCategory(Category.GEOGRAPHY)).thenReturn(quizzes);

        List<Quiz> result = quizService.getAllQuizzesByCategory(Category.GEOGRAPHY);

        assertEquals(quizzes.size(), result.size());
    }

    @Test
    void getAllQuizzesByCategory_whenNoQuizExists_thenReturnsEmptyList() {
        List<Quiz> quizzes = new ArrayList<>();

        when(quizRepository.findByCategory(Category.HISTORY)).thenReturn(quizzes);

        List<Quiz> result = quizService.getAllQuizzesByCategory(Category.HISTORY);

        assertTrue(result.isEmpty());
    }

    @Test
    void submitQuiz_whenQuizDontExists_thenThrowNewRuntimeException() {
        when(quizRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.submitQuiz(new NewQuizRequest(), new User()));
    }

    @Test
    void submitQuiz_updatesScoreAndCallsLeaderboard() {
        UUID id = UUID.randomUUID();

        Quiz quiz = new Quiz();
        quiz.setId(id);

        NewQuizRequest req = new NewQuizRequest();
        req.setId(id);
        req.setScore(100);

        QuestionOptionRequest opt = new QuestionOptionRequest(UUID.randomUUID() ,"A", true, true);
        QuestionRequest qReq = new QuestionRequest();
        qReq.setOptions(List.of(opt));
        req.setQuestions(List.of(qReq));

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("test");
        user.setAvatarUrl("url");

        when(quizRepository.findById(id)).thenReturn(Optional.of(quiz));

        quizService.submitQuiz(req, user);

        verify(quizRepository).save(quiz);
        verify(leaderboardClient).upsertScore(
                any(CreateScoreRequest.class),
                eq(user.getId())
        );
    }

    @Test
    void getQuizEarnedScore_whenAllAnswersAreCorrect_thenReturnsFullScore(){
        QuestionOptionRequest option1 = QuestionOptionRequest.builder()
                .text("Option 1")
                .isCorrect(true)
                .isSelected(true)
                .build();

        QuestionOptionRequest option2 = QuestionOptionRequest.builder()
                .text("Option 2")
                .isCorrect(false)
                .isSelected(false)
                .build();

        QuestionRequest question1 = QuestionRequest.builder()
                .name("Question 1")
                .options(List.of(option1, option2))
                .build();

        QuestionRequest question2 = QuestionRequest.builder()
                .name("Question 2")
                .options(List.of(option1, option2))
                .build();

        NewQuizRequest quizRequest = NewQuizRequest.builder()
                .score(100)
                .questions(List.of(question1, question2))
                .build();

        int result = quizService.getQuizEarnedScore(quizRequest);

        assertEquals(100, result);
    }

    @Test
    void deleteQuizById_whenQuizNotFound_thenThrowsException() {
        UUID quizId = UUID.randomUUID();

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> quizService.deleteQuizById(quizId));
    }

    @Test
    void getAllQuizzesByUser_returnsUserQuizzes() {
        UUID userId = UUID.randomUUID();
        List<Quiz> quizzes = List.of(new Quiz(), new Quiz());

        when(quizRepository.findAllByUser_IdOrderByUpdatedOnDesc(userId)).thenReturn(quizzes);

        List<Quiz> result = quizService.getAllQuizzesByUser(userId);

        assertEquals(2, result.size());
        verify(quizRepository).findAllByUser_IdOrderByUpdatedOnDesc(userId);
    }

}

