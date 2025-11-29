package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.client.LeaderboardClient;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.UserScoreResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class LeaderboardController {

    private final LeaderboardClient leaderboardClient;
    private final UserService userService;

    public LeaderboardController(LeaderboardClient leaderboardClient, UserService userService) {
        this.leaderboardClient = leaderboardClient;
        this.userService = userService;
    }

    @GetMapping("/leaderboard")
    public ModelAndView showLeaderboard(@AuthenticationPrincipal UserData userData) {
        List<UserScoreResponse> topScores = leaderboardClient.getTopScores();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("leaderboard");

        User user = userService.getById(userData.getUserId());
        List<UserScoreResponse> scoresAfterTopThree = userService.getScoresAfterTopThree(topScores);

        modelAndView.addObject("topScores", topScores);
        modelAndView.addObject("user", user);
        modelAndView.addObject("scoresAfterTopThree", scoresAfterTopThree);

        return modelAndView;
    }

    @DeleteMapping("/api/scores")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAll() {
        leaderboardClient.deleteAllScores();
        return "redirect:/leaderboard";
    }

    @DeleteMapping("/api/scores/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteScore(@PathVariable("id") UUID id) {
        leaderboardClient.deleteScore(id);
        return "redirect:/leaderboard";
    }

}
