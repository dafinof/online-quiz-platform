package bg.softuni.onlinequizplatform.data;

import bg.softuni.onlinequizplatform.model.*;
import bg.softuni.onlinequizplatform.repository.QuestionOptionRepository;
import bg.softuni.onlinequizplatform.repository.QuestionRepository;
import bg.softuni.onlinequizplatform.repository.QuizRepository;
import bg.softuni.onlinequizplatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initData (
            UserRepository userRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            QuestionOptionRepository questionOptionRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.count() > 0) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setAvatarUrl("https://example.com/admin.png");
            admin.setRole(UserRole.ADMIN);
            admin.setEmail("admin@example.com");
            admin.setActive(true);
            admin.setScore(0);
            admin.setLevel(1);
            admin.setCreatedOn(now);
            admin.setUpdatedOn(now);

            User quizmaster = new User();
            quizmaster.setUsername("quizmaster");
            quizmaster.setPassword(passwordEncoder.encode("qm123"));
            quizmaster.setAvatarUrl("https://example.com/qm.png");
            quizmaster.setRole(UserRole.QUIZMASTER);
            quizmaster.setEmail("qm@example.com");
            quizmaster.setActive(true);
            quizmaster.setScore(0);
            quizmaster.setLevel(1);
            quizmaster.setCreatedOn(now);
            quizmaster.setUpdatedOn(now);

            User player = new User();
            player.setUsername("player");
            player.setPassword(passwordEncoder.encode("player123"));
            player.setAvatarUrl("https://example.com/player.png");
            player.setRole(UserRole.PLAYER);
            player.setEmail("player@example.com");
            player.setActive(true);
            player.setScore(0);
            player.setLevel(1);
            player.setCreatedOn(now);
            player.setUpdatedOn(now);

            userRepository.saveAll(List.of(admin, quizmaster, player));

            Quiz shortQuiz = createQuiz("Geography Basics", Category.GEOGRAPHY,
                    "A short demo quiz", quizmaster, now, quizRepository);

            Quiz historyQuiz = createQuiz("World History", Category.HISTORY,
                    "A medium difficulty history quiz", quizmaster, now, quizRepository);

            Quiz musicQuiz = createQuiz("Music Challenge", Category.MUSIC,
                    "A music-themed quiz", quizmaster, now, quizRepository);

            createQuestionWithOptions(
                    "What is the capital of France?",
                    shortQuiz,
                    questionRepository,
                    questionOptionRepository,
                    new String[]{"Paris", "London", "Berlin"},
                    0
            );

            createQuestionWithOptions(
                    "2 + 2 = ?",
                    shortQuiz,
                    questionRepository,
                    questionOptionRepository,
                    new String[]{"3", "4", "5"},
                    1
            );

            for (int i = 1; i <= 10; i++) {
                createQuestionWithOptions(
                        "History question " + i,
                        historyQuiz,
                        questionRepository,
                        questionOptionRepository,
                        new String[]{"Correct answer", "Wrong 1", "Wrong 2"},
                        0
                );
            }

            for (int i = 1; i <= 10; i++) {
                createQuestionWithOptions(
                        "Music question " + i,
                        musicQuiz,
                        questionRepository,
                        questionOptionRepository,
                        new String[]{"Option A", "Option B", "Option C"},
                        2
                );
            }

            System.out.println(">>> DEMO DATA INITIALIZED");
        };
    }

    private Quiz createQuiz(String name, Category category, String description,
                            User owner, LocalDateTime now, QuizRepository repo) {

        Quiz quiz = new Quiz();
        quiz.setName(name);
        quiz.setCategory(category);
        quiz.setDescription(description);
        quiz.setImageUrl("https://media.istockphoto.com/id/1186386668/vector/quiz-in-comic-pop-art-style-quiz-brainy-game-word-vector-illustration-design.jpg?s=612x612&w=0&k=20&c=mBQMqQ6kZuC9ZyuV5_uCm80QspqSJ7vRm0MfwL3KLZY=");
        quiz.setScore(0);
        quiz.setEarnedScore(0);
        quiz.setUser(owner);
        quiz.setCreatedOn(now);
        quiz.setUpdatedOn(now);

        return repo.save(quiz);
    }

    private void createQuestionWithOptions(
            String questionText,
            Quiz quiz,
            QuestionRepository questionRepo,
            QuestionOptionRepository optionRepo,
            String[] options,
            int correctIndex
    ) {
        Question question = new Question();
        question.setName(questionText);
        question.setQuiz(quiz);

        questionRepo.save(question);

        for (int i = 0; i < options.length; i++) {
            QuestionOption option = new QuestionOption();
            option.setText(options[i]);
            option.setCorrect(i == correctIndex);
            option.setQuestion(question);

            optionRepo.save(option);
        }
    }
}
