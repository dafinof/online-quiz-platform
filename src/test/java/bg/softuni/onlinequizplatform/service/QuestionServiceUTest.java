package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.Question;
import bg.softuni.onlinequizplatform.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceUTest {

    private QuestionRepository questionRepository;
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionRepository = mock(QuestionRepository.class);
        questionService = new QuestionService(questionRepository);
    }

    @Test
    void testSave_ShouldCallRepositorySave() {
        Question question = Question.builder()
                .id(UUID.randomUUID())
                .name("Test Question")
                .build();

        questionService.save(question);

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(question);
    }

    @Test
    void testGetAllQuestions_ShouldReturnListFromRepository() {
        List<Question> expected = List.of(
                Question.builder().id(UUID.randomUUID()).name("Q1").build(),
                Question.builder().id(UUID.randomUUID()).name("Q2").build()
        );

        when(questionRepository.findAll()).thenReturn(expected);

        List<Question> result = questionService.getAllQuestions();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void testDeleteQuestion_ShouldCallRepositoryDelete() {
        Question question = Question.builder()
                .id(UUID.randomUUID())
                .name("Test Q")
                .build();

        questionService.deleteQuestion(question);

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository, times(1)).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(question);
    }
}
