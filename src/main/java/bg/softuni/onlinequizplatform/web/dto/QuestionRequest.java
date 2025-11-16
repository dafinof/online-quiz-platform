package bg.softuni.onlinequizplatform.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {
    private UUID id;

    @NotBlank
    private String name;

    @Size(min = 2, message = "Each question must have at least two options")
    private List<QuestionOptionRequest> options;
}
