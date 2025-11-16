package bg.softuni.onlinequizplatform.web.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScoreRequest {
    private UUID userId;
    private int score;
    private String username;
}
