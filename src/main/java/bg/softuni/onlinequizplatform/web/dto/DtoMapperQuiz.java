package bg.softuni.onlinequizplatform.web.dto;

import bg.softuni.onlinequizplatform.model.Question;
import bg.softuni.onlinequizplatform.model.QuestionOption;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.repository.QuestionOptionRepository;
import bg.softuni.onlinequizplatform.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DtoMapperQuiz {
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public DtoMapperQuiz(QuestionRepository questionRepository, QuestionOptionRepository questionOptionRepository) {
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
    }

    public NewQuizRequest fromQuizToNewQuizRequest(Quiz quiz) {
        List<Question> questions = questionRepository.getByQuizId(quiz.getId());
        List<QuestionRequest> questionRequests = new ArrayList<>();

        for (Question question : questions) {
            List<QuestionOption> options = questionOptionRepository.getByQuestionId(question.getId());
            List<QuestionOptionRequest> optionRequests = new ArrayList<>();
            for (QuestionOption option : options) {
                QuestionOptionRequest optionRequest = QuestionOptionRequest.builder()
                        .id(option.getId())
                        .text(option.getText())
                        .isSelected(false)
                        .isCorrect(option.isCorrect())
                        .build();

                optionRequests.add(optionRequest);
            }

            QuestionRequest questionRequest = QuestionRequest.builder()
                    .id(question.getId())
                    .name(question.getName())
                    .options(optionRequests)
                    .build();
            questionRequests.add(questionRequest);
        }

        return NewQuizRequest.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .description(quiz.getDescription())
                .imageUrl(quiz.getImageUrl())
                .category(quiz.getCategory())
                .score(quiz.getScore())
                .questions(questionRequests)
                .build();
    }

}
