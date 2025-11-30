package bg.softuni.onlinequizplatform.job;

import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DailyBonusScheduler {
    private static final Integer DAILY_BONUS_POINTS = 10;

    private final UserService userService;

    public DailyBonusScheduler(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void giveDailyBonusToActiveUsers() {
        List<User> allUsers = userService.getAllUsers();
        LocalDateTime pastDay = LocalDateTime.now().minusDays(1);

        for (User user : allUsers) {
            if (user.getUpdatedOn().isAfter(pastDay)) {
                user.setScore(user.getScore() + DAILY_BONUS_POINTS);
                user.setUpdatedOn(LocalDateTime.now());
                user.setLevel(user.getScore() / 1000 + 1);
                if (user.getScore() > 10000 && user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.QUIZMASTER) {
                    user.setRole(UserRole.QUIZMASTER);
                }
                userService.save(user);
            }
        }
    }
}
