package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.model.Category;
import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.DtoMapperQuiz;
import bg.softuni.onlinequizplatform.web.dto.NewQuizRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class QuizController {

    private final QuizService quizService;
    private final DtoMapperQuiz dtoMapperQuiz;
    private final UserService userService;

    public QuizController(QuizService quizService, UserService userService, DtoMapperQuiz dtoMapperQuiz) {
        this.quizService = quizService;
        this.dtoMapperQuiz = dtoMapperQuiz;
        this.userService = userService;
    }

    @GetMapping("/quizzes")
    public ModelAndView getQuizzesPage(@AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("quizzes");

        User user = userService.getByUsername(userData.getUsername());
        List<Quiz> quizzesGeography = quizService.getAllQuizzesByCategory(Category.GEOGRAPHY);
        List<Quiz> quizzesHistory = quizService.getAllQuizzesByCategory(Category.HISTORY);
        List<Quiz> quizzesMusic = quizService.getAllQuizzesByCategory(Category.MUSIC);

        modelAndView.addObject("quizzesGeography", quizzesGeography);
        modelAndView.addObject("quizzesHistory", quizzesHistory);
        modelAndView.addObject("quizzesMusic", quizzesMusic);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/new-quiz")
    @PreAuthorize("hasAnyRole('ADMIN','QUIZMASTER')")
    public ModelAndView getNewQuizPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("new-quiz");
        modelAndView.addObject("newQuizRequest" , new NewQuizRequest());

        return modelAndView;
    }

    @PostMapping("/new-quiz")
    @PreAuthorize("hasAnyRole('ADMIN','QUIZMASTER')")
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
        NewQuizRequest quiz = dtoMapperQuiz.fromQuizToNewQuizRequest(quizOriginal);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("quiz");
        modelAndView.addObject("quiz", quiz);

        return  modelAndView;
    }

    @DeleteMapping("/quizzes/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteQuiz(@PathVariable("id") UUID id) {
        quizService.deleteQuizById(id);

        return "redirect:/quizzes";
    }

    @PostMapping("/quiz/submit")
    public ModelAndView submitQuiz(NewQuizRequest quizRequest, @AuthenticationPrincipal UserData userData) {
        User user = userService.getById(userData.getUserId());
        int quizEarnedScore = quizService.getQuizEarnedScore(quizRequest);

        userService.setNewScore(user, quizEarnedScore);
        quizService.submitQuiz(quizRequest, user);

        return new ModelAndView("redirect:/quizzes");
    }
}
