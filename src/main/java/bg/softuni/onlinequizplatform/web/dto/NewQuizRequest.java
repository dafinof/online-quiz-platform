package bg.softuni.onlinequizplatform.web.dto;

import bg.softuni.onlinequizplatform.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewQuizRequest {

    private UUID id;

    @Size(min = 3, max = 50, message = "The name of the quiz should be between 3 and 50 symbols")
    @NotBlank
    private String name;

    private String description;

    @URL
    private String imageUrl;

    @Min(0)
    private int score;

    @NotNull
    private Category category;

    private List<QuestionRequest> questions;
}
