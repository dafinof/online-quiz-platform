package bg.softuni.onlinequizplatform.client;

import bg.softuni.onlinequizplatform.web.dto.CreateScoreRequest;
import bg.softuni.onlinequizplatform.web.dto.UserScoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "leaderboard-service", url = "http://localhost:8081")
public interface LeaderboardClient {

    @PutMapping("/api/scores/v1/{id}")
    UserScoreResponse upsertScore(@RequestBody CreateScoreRequest request, @PathVariable UUID id);

    @DeleteMapping("/api/scores/v1/{id}")
    void deleteScore(@PathVariable UUID id);

    @DeleteMapping("/api/scores/v1")
    void deleteAllScores();

    @GetMapping("/api/scores/v1/top")
    List<UserScoreResponse> getTopScores();
}
