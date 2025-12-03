package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.Question;
import bg.softuni.onlinequizplatform.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuestionServiceITest {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void save_shouldPersistQuestion() {
        // arrange
        Question q = Question.builder()
                .name("What is Java?")
                .build();

        // act
        questionService.save(q);

        // assert
        List<Question> all = questionRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("What is Java?");
    }

    @Test
    void getAllQuestions_shouldReturnAllSavedQuestions() {
        // arrange
        Question q1 = Question.builder().name("Q1?").build();
        Question q2 = Question.builder().name("Q2?").build();
        questionRepository.save(q1);
        questionRepository.save(q2);

        // act
        List<Question> result = questionService.getAllQuestions();

        // assert
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Question::getName)
                .containsExactlyInAnyOrder("Q1?", "Q2?");
    }

    @Test
    void deleteQuestion_shouldRemoveExistingQuestion() {
        // arrange
        Question q = Question.builder()
                .name("Delete me")
                .build();
        questionRepository.save(q);

        // act
        questionService.deleteQuestion(q);

        // assert
        assertThat(questionRepository.findAll()).isEmpty();
    }
}
