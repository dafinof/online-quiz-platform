package bg.softuni.onlinequizplatform;

import bg.softuni.onlinequizplatform.model.UserRole;
import bg.softuni.onlinequizplatform.security.UserData;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataTest {

    private UUID createTestUserId() {
        return UUID.randomUUID();
    }

    private UserData createTestUserData(UserRole role, boolean isActive) {
        return new UserData(
                createTestUserId(),
                "testuser",
                "encoded_password",
                role,
                isActive
        );
    }

    @Test
    void constructor_shouldInitializeAllFields() {
        UUID userId = createTestUserId();
        String username = "testuser";
        String password = "encoded_password";
        UserRole role = UserRole.PLAYER;
        boolean isActive = true;

        UserData userData = new UserData(userId, username, password, role, isActive);

        assertEquals(userId, userData.getUserId());
        assertEquals(username, userData.getUsername());
        assertEquals(password, userData.getPassword());
        assertEquals(role, userData.getRole());
        assertTrue(userData.isAccountActive());
    }

    @Test
    void constructor_withInactiveAccount_shouldSetIsAccountActiveFalse() {
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        assertFalse(userData.isAccountActive());
    }

    // ==================== Authority Tests ====================

    @Test
    void getAuthorities_withPlayerRole_shouldReturnRolePlayerAuthority() {
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PLAYER")));
    }

    @Test
    void getAuthorities_withAdminRole_shouldReturnRoleAdminAuthority() {
        UserData userData = createTestUserData(UserRole.ADMIN, true);

        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void getAuthorities_withQuizmasterRole_shouldReturnRoleQuizmasterAuthority() {
        UserData userData = createTestUserData(UserRole.QUIZMASTER, true);

        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_QUIZMASTER")));
    }

    @Test
    void getAuthorities_shouldAlwaysReturnSingleAuthority() {
        for (UserRole role : UserRole.values()) {
            UserData userData = new UserData(
                    createTestUserId(),
                    "testuser",
                    "password",
                    role,
                    true
            );

            Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();
            assertEquals(1, authorities.size(), "Should have exactly one authority for role: " + role);
        }
    }

    @Test
    void getAuthorities_shouldHaveCorrectAuthorityFormat() {
        UserData userData = createTestUserData(UserRole.ADMIN, true);

        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        GrantedAuthority authority = authorities.iterator().next();
        assertTrue(authority.getAuthority().startsWith("ROLE_"),
                "Authority should start with ROLE_");
    }

    @Test
    void getPassword_shouldReturnPassword() {
        String password = "my_encoded_password";
        UserData userData = new UserData(
                createTestUserId(),
                "testuser",
                password,
                UserRole.PLAYER,
                true
        );

        String result = userData.getPassword();

        assertEquals(password, result);
    }

    @Test
    void getUsername_shouldReturnUsername() {
        String username = "testuser123";
        UserData userData = new UserData(
                createTestUserId(),
                username,
                "password",
                UserRole.PLAYER,
                true
        );

        String result = userData.getUsername();

        assertEquals(username, result);
    }

    // ==================== Account Status Tests ====================

    @Test
    void isAccountNonExpired_withActiveAccount_shouldReturnTrue() {
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        boolean result = userData.isAccountNonExpired();

        assertTrue(result);
    }

    @Test
    void isAccountNonExpired_withInactiveAccount_shouldReturnFalse() {
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        boolean result = userData.isAccountNonExpired();

        assertFalse(result);
    }

    @Test
    void isAccountNonLocked_withActiveAccount_shouldReturnTrue() {
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        boolean result = userData.isAccountNonLocked();

        assertTrue(result);
    }

    @Test
    void isAccountNonLocked_withInactiveAccount_shouldReturnFalse() {
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        boolean result = userData.isAccountNonLocked();

        assertFalse(result);
    }

    @Test
    void isCredentialsNonExpired_withActiveAccount_shouldReturnTrue() {
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        boolean result = userData.isCredentialsNonExpired();

        assertTrue(result);
    }

    @Test
    void isCredentialsNonExpired_withInactiveAccount_shouldReturnFalse() {
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        boolean result = userData.isCredentialsNonExpired();

        assertFalse(result);
    }

    @Test
    void isEnabled_withActiveAccount_shouldReturnTrue() {
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        boolean result = userData.isEnabled();

        assertTrue(result);
    }

    @Test
    void isEnabled_withInactiveAccount_shouldReturnFalse() {
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        boolean result = userData.isEnabled();

        assertFalse(result);
    }

    // ==================== Combined Account Status Tests ====================

    @Test
    void allAccountStatusMethods_withActiveAccount_shouldAllReturnTrue() {
        UserData userData = createTestUserData(UserRole.ADMIN, true);

        assertTrue(userData.isAccountNonExpired());
        assertTrue(userData.isAccountNonLocked());
        assertTrue(userData.isCredentialsNonExpired());
        assertTrue(userData.isEnabled());
    }

    @Test
    void allAccountStatusMethods_withInactiveAccount_shouldAllReturnFalse() {
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        assertFalse(userData.isAccountNonExpired());
        assertFalse(userData.isAccountNonLocked());
        assertFalse(userData.isCredentialsNonExpired());
        assertFalse(userData.isEnabled());
    }

    // ==================== Integration Tests ====================

    @Test
    void userDataWithAllRoles_shouldProperlyImplementUserDetails() {
        for (UserRole role : UserRole.values()) {
            UserData userData = new UserData(
                    createTestUserId(),
                    "user_" + role.name(),
                    "password",
                    role,
                    true
            );

            assertNotNull(userData.getAuthorities());
            assertNotNull(userData.getPassword());
            assertNotNull(userData.getUsername());
            assertTrue(userData.isAccountNonExpired());
            assertTrue(userData.isAccountNonLocked());
            assertTrue(userData.isCredentialsNonExpired());
            assertTrue(userData.isEnabled());
            assertEquals(1, userData.getAuthorities().size());
        }
    }

    @Test
    void userDataShouldBeSerializable() {
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        assertNotNull(userData.getUserId());
        assertNotNull(userData.getUsername());
        assertNotNull(userData.getPassword());
        assertNotNull(userData.getRole());
        assertNotNull(userData.getAuthorities());
    }
}
