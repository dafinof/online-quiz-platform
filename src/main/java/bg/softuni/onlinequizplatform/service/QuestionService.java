package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.Question;
import bg.softuni.onlinequizplatform.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void save(Question currentQuestion) {
        questionRepository.save(currentQuestion);
    }
}
