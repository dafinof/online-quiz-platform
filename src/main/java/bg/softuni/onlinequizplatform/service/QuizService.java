package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.client.LeaderboardClient;
import bg.softuni.onlinequizplatform.model.*;
import bg.softuni.onlinequizplatform.repository.QuizRepository;
import bg.softuni.onlinequizplatform.web.dto.CreateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.NewQuizRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionOptionRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionService questionService;
    private final QuestionOptionService questionOptionService;
    private final LeaderboardClient leaderboardClient;


    public QuizService(QuizRepository quizRepository, QuestionService questionService, QuestionOptionService questionOptionService, LeaderboardClient leaderboardClient) {
        this.quizRepository = quizRepository;
        this.questionService = questionService;
        this.questionOptionService = questionOptionService;
        this.leaderboardClient = leaderboardClient;
    }

    @Transactional
    @CacheEvict(value = "quizzesByCategory", allEntries = true)
    public void createNewQuiz(NewQuizRequest newQuizRequest) {
        Quiz quiz = Quiz.builder()
                .name(newQuizRequest.getName())
                .imageUrl(newQuizRequest.getImageUrl())
                .category(newQuizRequest.getCategory())
                .score(newQuizRequest.getScore())
                .description(newQuizRequest.getDescription())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        quizRepository.save(quiz);

        List<QuestionRequest> questionRequests = newQuizRequest.getQuestions();
        for (QuestionRequest questionRequest : questionRequests) {
            List<QuestionOptionRequest> currentOptionRequests = questionRequest.getOptions();
            Question currentQuestion = Question.builder()
                    .name(questionRequest.getName())
                    .quiz(quiz)
                    .build();

            questionService.save(currentQuestion);

            for (QuestionOptionRequest questionOptionRequest : currentOptionRequests) {
                QuestionOption questionOption = QuestionOption.builder()
                        .text(questionOptionRequest.getText())
                        .isCorrect(questionOptionRequest.getIsCorrect())
                        .question(currentQuestion)
                        .build();

                questionOptionService.save(questionOption);
            }

            questionService.save(currentQuestion);
        }
        quizRepository.save(quiz);
    }

    @Cacheable("quizzesByCategory")
    public List<Quiz> getAllQuizzesByCategory(Category category) {
        return quizRepository.findByCategory(category);
    }

    public Optional<Quiz> getById(UUID id) {
        return quizRepository.findById(id);
    }

    //@Transactional
    public void submitQuiz(NewQuizRequest quizRequest, User user) {
        Optional<Quiz> optionalQuiz = getById(quizRequest.getId());
        if (optionalQuiz.isEmpty()) {
            throw new RuntimeException("Quiz not found: " + quizRequest.getId());
        }

        Quiz quiz = optionalQuiz.get();
        int quizEarnedScore = getQuizEarnedScore(quizRequest);

        quiz.setEarnedScore(quizEarnedScore);
        quiz.setUser(user);
        quizRepository.save(quiz);

        leaderboardClient.upsertScore(new CreateScoreRequest(user.getId(), user.getScore(), user.getUsername(), user.getAvatarUrl()), user.getId());
    }

    public int getQuizEarnedScore(NewQuizRequest quizRequest) {
        int quizScore = 0;
        int correctAnswers = 0;

        for (QuestionRequest questionRequest : quizRequest.getQuestions()) {
            for (QuestionOptionRequest questionOptionRequest : questionRequest.getOptions()) {
                if (questionOptionRequest.getIsCorrect() && questionOptionRequest.getIsSelected()) {
                    correctAnswers++;
                }
            }
        }

        quizScore = (quizRequest.getScore() / quizRequest.getQuestions().size()) * correctAnswers;

        return quizScore;
    }

    public List<Quiz> getAllQuizzesByUser(UUID id) {
        return quizRepository.findAllByUser_IdOrderByUpdatedOnDesc(id);
    }

    @CacheEvict(value = "quizzesByCategory", allEntries = true)
    public void deleteQuizById(UUID id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));

        List<Question> allQuestions = questionService.getAllQuestions();
        for (Question question : allQuestions) {
            if (question.getQuiz().getId().equals(quiz.getId())) {
                List<QuestionOption> questionOptions = questionOptionService.getAllOptions();
                for (QuestionOption questionOption : questionOptions) {
                    if (questionOption.getQuestion().getId().equals(question.getId())) {
                        questionOptionService.deleteQuestionOption(questionOption);
                    }
                }
                questionService.deleteQuestion(question);
            }
        }

        quizRepository.delete(quiz);
    }
}
