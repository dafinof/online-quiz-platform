package bg.softuni.onlinequizplatform.client;

import bg.softuni.onlinequizplatform.web.dto.CreateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.UpdateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.UserScoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "leaderboard-service", url = "http://localhost:8081")
public interface LeaderboardClient {

    @PostMapping("/api/scores")
    UserScoreResponse createScore(CreateScoreRequest request);

    @PutMapping("/api/scores/{id}")
    UserScoreResponse updateScore(UpdateScoreRequest request, @PathVariable UUID id);

    @DeleteMapping("/api/scores/{id}")
    void deleteScore(@PathVariable UUID id);

    @DeleteMapping("/api/scores")
    void deleteAllScores();

    @GetMapping("api/scores/top")
    List<UserScoreResponse> getTopScores();
}
