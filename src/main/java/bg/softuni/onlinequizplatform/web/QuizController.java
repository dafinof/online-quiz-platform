package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.client.LeaderboardClient;
import bg.softuni.onlinequizplatform.model.Category;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.CreateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.DtoMapperQuiz;
import bg.softuni.onlinequizplatform.web.dto.NewQuizRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;
    private final LeaderboardClient leaderboardClient;

    public QuizController(QuizService quizService, UserService userService, LeaderboardClient leaderboardClient) {
        this.quizService = quizService;
        this.userService = userService;
        this.leaderboardClient = leaderboardClient;
    }

    @GetMapping("/quizzes")
    public ModelAndView getQuizzesPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("quizzes");

        List<Quiz> quizzesGeography = quizService.getAllQuizzesByCategory(Category.GEOGRAPHY);
        List<Quiz> quizzesHistory = quizService.getAllQuizzesByCategory(Category.HISTORY);
        List<Quiz> quizzesMusic = quizService.getAllQuizzesByCategory(Category.MUSIC);

        modelAndView.addObject("quizzesGeography", quizzesGeography);
        modelAndView.addObject("quizzesHistory", quizzesHistory);
        modelAndView.addObject("quizzesMusic", quizzesMusic);

        return modelAndView;
    }

    @GetMapping("/new-quiz")
    public ModelAndView getNewQuizPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("new-quiz");
        modelAndView.addObject("newQuizRequest" , new NewQuizRequest());

        return modelAndView;
    }

    @PostMapping("/new-quiz")
    public ModelAndView postNewQuiz(@Valid NewQuizRequest newQuizRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("new-quiz");
            modelAndView.addObject("newQuizRequest", newQuizRequest);
            return modelAndView;
        }

        quizService.createNewQuiz(newQuizRequest);
        return new ModelAndView("redirect:/quizzes");
    }

    @GetMapping("/quiz/{id}")
    public ModelAndView getQuizPage(@PathVariable("id") UUID id) {
        Optional<Quiz> quizById = quizService.getById(id);

        if (quizById.isEmpty()) {
            throw new RuntimeException("Quiz not found: " + id);
        }

        Quiz quizOriginal = quizById.get();
        NewQuizRequest quiz = DtoMapperQuiz.fromQuizToNewQuizRequest(quizOriginal);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("quiz");
        modelAndView.addObject("quiz", quiz);

        return  modelAndView;
    }

    @PostMapping("/quiz/submit")
    public ModelAndView submitQuiz(NewQuizRequest quizRequest, @AuthenticationPrincipal UserData userData) {

        Optional<Quiz> optionalQuiz = quizService.getById(quizRequest.getId());
        if (optionalQuiz.isEmpty()) {
            throw new RuntimeException("Quiz not found: " + quizRequest.getId());
        }

        User user = userService.getById(userData.getUserId());
        Quiz quiz = optionalQuiz.get();

        user.getQuizzes().add(quiz);
        // TODO To set the score based on correct answers - no logic in the controller, set it through the service
        user.setScore(user.getScore() + quiz.getScore());
        userService.save(user);

        leaderboardClient.createScore(new CreateScoreRequest(userData.getUserId(), user.getScore(), user.getUsername()));

        return new ModelAndView("redirect:/quizzes");
    }

    @GetMapping("result-page") // TODO either link the result page or remove it
    public ModelAndView getResultPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("result-page");
        return modelAndView;
    }

}
