package bg.softuni.onlinequizplatform.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScoreResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String avatarUrl;
    private int score;
    private LocalDateTime updatedOn;
}
