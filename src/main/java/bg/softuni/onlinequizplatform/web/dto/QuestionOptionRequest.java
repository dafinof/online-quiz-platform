package bg.softuni.onlinequizplatform.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOptionRequest {
    private UUID id;

    @NotBlank
    private String text;

    private Boolean isSelected;

    private Boolean isCorrect;
}
