package bg.softuni.onlinequizplatform.web;

import bg.softuni.onlinequizplatform.exception.PasswordMismatchException;
import bg.softuni.onlinequizplatform.exception.UserNotFoundException;
import bg.softuni.onlinequizplatform.exception.UsernameAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleExceptionUserNotFound(UserNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user-not-found");

        return modelAndView;
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleExceptionUsernameAlreadyExist(UsernameAlreadyExistException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        return "redirect:/register";
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public String handleExceptionPasswordMismatch(PasswordMismatchException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessagePass", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleExceptionAllNotCaught(Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("default-error");

        return modelAndView;
    }
}
