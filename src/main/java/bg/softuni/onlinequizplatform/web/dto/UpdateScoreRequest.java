package bg.softuni.onlinequizplatform.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScoreRequest {
    private int score;
}
