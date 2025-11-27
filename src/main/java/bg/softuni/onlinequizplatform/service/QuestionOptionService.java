package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.QuestionOption;
import bg.softuni.onlinequizplatform.repository.QuestionOptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionOptionService {

    private final QuestionOptionRepository questionOptionRepository;

    public QuestionOptionService(QuestionOptionRepository questionOptionRepository) {
        this.questionOptionRepository = questionOptionRepository;
    }

    public void save(QuestionOption questionOption) {
        questionOptionRepository.save(questionOption);
    }

    public List<QuestionOption> getAllOptions() {
        return questionOptionRepository.findAll();
    }

    public void deleteQuestionOption(QuestionOption questionOption) {
        questionOptionRepository.delete(questionOption);
    }
}
