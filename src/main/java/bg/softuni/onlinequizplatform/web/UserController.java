package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.model.Quiz;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.security.UserData;
import bg.softuni.onlinequizplatform.service.QuizService;
import bg.softuni.onlinequizplatform.service.UserService;
import bg.softuni.onlinequizplatform.web.dto.DtoMapperProfile;
import bg.softuni.onlinequizplatform.web.dto.DtoMapperUser;
import bg.softuni.onlinequizplatform.web.dto.EditProfileRequest;
import bg.softuni.onlinequizplatform.web.dto.EditUserRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final QuizService quizService;

    public UserController(UserService userService, QuizService quizService) {
        this.userService = userService;
        this.quizService = quizService;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal UserData userData) {
        User user = userService.getByUsername(userData.getUsername());
        int averageScore = userService.getAverageSuccessPercent(user);
        List<Quiz> quizzes = quizService.getAllQuizzesByUser(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("averageScore", averageScore);
        modelAndView.addObject("quizzes", quizzes);

        return modelAndView;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getUsersPage() {
        List<User> users = userService.getAllUsers();
        List<User> usersPlayer = userService.getUsersByRole(UserRole.PLAYER);
        List<User> usersAdmin = userService.getUsersByRole(UserRole.ADMIN);
        List<User> usersQuizMaster = userService.getUsersByRole(UserRole.QUIZMASTER);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);
        modelAndView.addObject("usersPlayer", usersPlayer);
        modelAndView.addObject("usersAdmin", usersAdmin);
        modelAndView.addObject("usersQuizMaster", usersQuizMaster);

        return modelAndView;
    }

    @GetMapping("/users/edit/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editUserPage(@PathVariable String username) {
        User user = userService.getByUsername(username);
        EditUserRequest editUserRequest = DtoMapperUser.fromUserToEditUserRequest(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-user");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editUserRequest", editUserRequest);

        return modelAndView;
    }

    @PutMapping("/users/edit/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editUser(@Valid EditUserRequest editUserRequest, BindingResult bindingResult, @PathVariable String username) {
        User user = userService.getByUsername(username);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-user");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editUserRequest", editUserRequest);

            return modelAndView;
        }

        userService.updateUserProfile(username, editUserRequest);

        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/users/delete/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable String username) {
        userService.deleteUser(username);

        return "redirect:/users";
    }

    @GetMapping("/edit-profile")
    public ModelAndView editProfilePage(@AuthenticationPrincipal UserData userData) {
        User user = userService.getByUsername(userData.getUsername());
        EditProfileRequest editProfileRequest = DtoMapperProfile.fromUserToEditProfileRequest(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfileRequest", editProfileRequest);

        return modelAndView;
    }

    @PutMapping("/edit-profile")
    public ModelAndView editProfile(@Valid EditProfileRequest editProfileRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData) {
        User user = userService.getByUsername(userData.getUsername());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editProfileRequest", editProfileRequest);

            return modelAndView;
        }

        userService.updateProfile(editProfileRequest);

        return new ModelAndView("redirect:/home");
    }

}
