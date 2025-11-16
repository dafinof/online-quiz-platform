package bg.softuni.onlinequizplatform.web.dto;

import bg.softuni.onlinequizplatform.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUserRequest {

    @NotBlank
    @Size(min = 6, max = 24, message = "Username must be between 6 and 24 symbols")
    private String username;

    @Pattern(regexp = "^$|.{6}$", message = "Password must be either empty or exactly 6 characters")
    private String newPassword;

    @NotBlank
    @URL
    private String avatarUrl;

    @NotNull
    private UserRole role;

    private boolean active;

    @PositiveOrZero
    private int score;

    @PositiveOrZero
    private int level;

    @Email
    String email;
}
