package bg.softuni.onlinequizplatform.service;

import bg.softuni.onlinequizplatform.model.Question;
import bg.softuni.onlinequizplatform.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void save(Question currentQuestion) {
        questionRepository.save(currentQuestion);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public void deleteQuestion(Question question) {
        questionRepository.delete(question);
    }
}
