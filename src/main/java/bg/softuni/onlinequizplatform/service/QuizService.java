package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.Category;
import bg.softuni.onlinequizplatform.model.Question;
import bg.softuni.onlinequizplatform.model.QuestionOption;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.repository.QuizRepository;
import bg.softuni.onlinequizplatform.web.dto.NewQuizRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionOptionRequest;
import bg.softuni.onlinequizplatform.web.dto.QuestionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionService questionService;
    private final QuestionOptionService questionOptionService;

    public QuizService(QuizRepository quizRepository, QuestionService questionService, QuestionOptionService questionOptionService) {
        this.quizRepository = quizRepository;
        this.questionService = questionService;
        this.questionOptionService = questionOptionService;
    }

    @Transactional
    public void createNewQuiz(NewQuizRequest newQuizRequest) {
        //String description = newQuizRequest.description - ako e prazno da slova defaultno neshto
        Quiz quiz = Quiz.builder()
                .name(newQuizRequest.getName())
                .imageUrl(newQuizRequest.getImageUrl())
                .category(newQuizRequest.getCategory())
                .score(100) // TODO to take if from the NewQuizRequest Dto
                .description("Let`s learn geography") // TODO to take if from the NewQuizRequest Dto
                .questions(new ArrayList<>())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        quizRepository.save(quiz);

        List<Question> questions = new ArrayList<>();
        List<QuestionRequest> questionRequests = newQuizRequest.getQuestions();
        for (QuestionRequest questionRequest : questionRequests) {
            List<QuestionOptionRequest> currentOptionRequests = questionRequest.getOptions();
            Question currentQuestion = Question.builder()
                    .name(questionRequest.getName())
                    .options(new ArrayList<>())
                    .quiz(quiz)
                    .build();

            questionService.save(currentQuestion);

            List<QuestionOption> currentOptions = new ArrayList<>();
            for (QuestionOptionRequest questionOptionRequest : currentOptionRequests) {
                QuestionOption questionOption = QuestionOption.builder()
                        .text(questionOptionRequest.getText())
                        .isCorrect(questionOptionRequest.getIsCorrect())
                        .question(currentQuestion)
                        .build();

                questionOptionService.save(questionOption);
                currentOptions.add(questionOption);
            }
            currentQuestion.setOptions(currentOptions);

            questionService.save(currentQuestion);
            questions.add(currentQuestion);
        }

        quiz.setQuestions(questions);
        quizRepository.save(quiz);

    }

    public List<Quiz> getAllQuizzesByCategory(Category category) {
        return quizRepository.findByCategory(category);
    }

    public Optional<Quiz> getById(UUID id) {
        return quizRepository.findById(id);
    }
}
