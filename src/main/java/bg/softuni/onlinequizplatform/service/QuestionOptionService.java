package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.QuestionOption;
import bg.softuni.onlinequizplatform.repository.QuestionOptionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionOptionService {

    private final QuestionOptionRepository questionOptionRepository;

    public QuestionOptionService(QuestionOptionRepository questionOptionRepository) {
        this.questionOptionRepository = questionOptionRepository;
    }

    public void save(QuestionOption questionOption) {
        questionOptionRepository.save(questionOption);
    }
}
