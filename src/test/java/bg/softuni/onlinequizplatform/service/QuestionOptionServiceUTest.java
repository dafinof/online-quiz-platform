package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.QuestionOption;
import bg.softuni.onlinequizplatform.repository.QuestionOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuestionOptionServiceUTest {
    @Mock
    private QuestionOptionRepository questionOptionRepository;

    private QuestionOptionService questionOptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        questionOptionService = new QuestionOptionService(questionOptionRepository);
    }

    @Test
    void save_shouldCallRepositorySave() {
        QuestionOption option = QuestionOption.builder()
                .text("Option A")
                .isCorrect(true)
                .build();

        questionOptionService.save(option);

        verify(questionOptionRepository, times(1)).save(option);
    }

    @Test
    void getAllOptions_shouldReturnAllOptions() {
        List<QuestionOption> expected = Arrays.asList(
                QuestionOption.builder().text("A").build(),
                QuestionOption.builder().text("B").build()
        );

        when(questionOptionRepository.findAll()).thenReturn(expected);

        List<QuestionOption> result = questionOptionService.getAllOptions();

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(questionOptionRepository, times(1)).findAll();
    }

    @Test
    void deleteQuestionOption_shouldCallRepositoryDelete() {
        QuestionOption option = QuestionOption.builder()
                .text("Option X")
                .build();

        questionOptionService.deleteQuestionOption(option);

        verify(questionOptionRepository, times(1)).delete(option);
    }
}
