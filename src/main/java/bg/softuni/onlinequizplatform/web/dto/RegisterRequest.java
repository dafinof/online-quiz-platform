package bg.softuni.onlinequizplatform.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    @Size(min = 6, max = 24, message = "Username must be between 6 and 24 symbols")
    String username;

    @NotBlank
    @Size(min = 6, max = 6, message = "Password should be exactly 6 symbols")
    String password;

    @NotBlank
    @Size(min = 6, max = 6, message = "Password should be exactly 6 symbols")
    String confirmPassword;

    @Email
    String email;

}
