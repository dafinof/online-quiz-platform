package bg.softuni.onlinequizplatform;

import bg.softuni.onlinequizplatform.job.DailyBonusScheduler;
import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyBonusSchedulerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private DailyBonusScheduler dailyBonusScheduler;

    private User createTestUser(UUID id, int score, UserRole role, LocalDateTime updatedOn) {
        return User.builder()
                .id(id)
                .username("testuser" + id)
                .password("encoded_password")
                .email("test@example.com")
                .avatarUrl("https://example.com/avatar.jpg")
                .role(role)
                .score(score)
                .level(score / 1000 + 1)
                .active(true)
                .createdOn(updatedOn)
                .updatedOn(updatedOn)
                .build();
    }

    @Test
    void giveDailyBonusToActiveUsers_withActiveUserUpdatedWithinPastDay_shouldAddBonusPoints() {
        User activeUser = createTestUser(UUID.randomUUID(), 100, UserRole.PLAYER, LocalDateTime.now().minusHours(12));
        when(userService.getAllUsers()).thenReturn(List.of(activeUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(110, savedUser.getScore());
        assertEquals(1, savedUser.getLevel());
    }

    @Test
    void giveDailyBonusToActiveUsers_withMultipleActiveUsers_shouldAddBonusToAll() {
        User user1 = createTestUser(UUID.randomUUID(), 100, UserRole.PLAYER, LocalDateTime.now().minusHours(6));
        User user2 = createTestUser(UUID.randomUUID(), 200, UserRole.PLAYER, LocalDateTime.now().minusHours(18));
        User user3 = createTestUser(UUID.randomUUID(), 300, UserRole.PLAYER, LocalDateTime.now().minusMinutes(30));

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2, user3));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        verify(userService, times(3)).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, times(3)).save(userCaptor.capture());

        List<User> savedUsers = userCaptor.getAllValues();
        assertEquals(110, savedUsers.get(0).getScore());
        assertEquals(210, savedUsers.get(1).getScore());
        assertEquals(310, savedUsers.get(2).getScore());
    }

    @Test
    void giveDailyBonusToActiveUsers_withInactiveUserNotUpdatedWithinPastDay_shouldNotAddBonus() {
        User inactiveUser = createTestUser(UUID.randomUUID(), 100, UserRole.PLAYER, LocalDateTime.now().minusDays(2));
        when(userService.getAllUsers()).thenReturn(List.of(inactiveUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void giveDailyBonusToActiveUsers_withMixedActiveAndInactiveUsers_shouldOnlyRewardActiveUsers() {
        User activeUser = createTestUser(UUID.randomUUID(), 100, UserRole.PLAYER, LocalDateTime.now().minusHours(12));
        User inactiveUser = createTestUser(UUID.randomUUID(), 200, UserRole.PLAYER, LocalDateTime.now().minusDays(3));

        when(userService.getAllUsers()).thenReturn(List.of(activeUser, inactiveUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        verify(userService, times(1)).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(110, savedUser.getScore());
        assertEquals(activeUser.getId(), savedUser.getId());
    }

    @Test
    void giveDailyBonusToActiveUsers_withUserScoreOver10000AndPlayerRole_shouldPromoteToQuizmaster() {
        User playerUser = createTestUser(UUID.randomUUID(), 9995, UserRole.PLAYER, LocalDateTime.now().minusHours(6));
        when(userService.getAllUsers()).thenReturn(List.of(playerUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(10005, savedUser.getScore());
        assertEquals(UserRole.QUIZMASTER, savedUser.getRole());
    }

    @Test
    void giveDailyBonusToActiveUsers_withUserScoreOver10000AndAdminRole_shouldNotChangeRole() {
        User adminUser = createTestUser(UUID.randomUUID(), 9995, UserRole.ADMIN, LocalDateTime.now().minusHours(6));
        when(userService.getAllUsers()).thenReturn(List.of(adminUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(10005, savedUser.getScore());
        assertEquals(UserRole.ADMIN, savedUser.getRole());
    }

    @Test
    void giveDailyBonusToActiveUsers_withUserScoreOver10000AndQuizmasterRole_shouldNotChangeRole() {
        User quizmasterUser = createTestUser(UUID.randomUUID(), 9995, UserRole.QUIZMASTER, LocalDateTime.now().minusHours(6));
        when(userService.getAllUsers()).thenReturn(List.of(quizmasterUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(10005, savedUser.getScore());
        assertEquals(UserRole.QUIZMASTER, savedUser.getRole());
    }

    @Test
    void giveDailyBonusToActiveUsers_withScoreBoundaryAt9990_shouldPromoteToQuizmaster() {
        User playerUser = createTestUser(UUID.randomUUID(), 9991, UserRole.PLAYER, LocalDateTime.now().minusHours(6));
        when(userService.getAllUsers()).thenReturn(List.of(playerUser));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(10001, savedUser.getScore());
        assertEquals(UserRole.QUIZMASTER, savedUser.getRole());
    }

    @Test
    void giveDailyBonusToActiveUsers_withLevelCalculation_shouldUpdateLevelCorrectly() {
        User userAtLevelBoundary = createTestUser(UUID.randomUUID(), 990, UserRole.PLAYER, LocalDateTime.now().minusHours(6));
        when(userService.getAllUsers()).thenReturn(List.of(userAtLevelBoundary));

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(1000, savedUser.getScore());
        assertEquals(2, savedUser.getLevel());  // 1000 / 1000 + 1 = 2
    }

    @Test
    void giveDailyBonusToActiveUsers_withNoUsers_shouldNotInvokeAnySave() {
        when(userService.getAllUsers()).thenReturn(List.of());

        dailyBonusScheduler.giveDailyBonusToActiveUsers();

        verify(userService, never()).save(any(User.class));
    }
}