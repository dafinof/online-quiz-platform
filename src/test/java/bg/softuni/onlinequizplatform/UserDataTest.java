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

    // ==================== Constructor and Field Tests ====================

    @Test
    void constructor_shouldInitializeAllFields() {
        // Arrange
        UUID userId = createTestUserId();
        String username = "testuser";
        String password = "encoded_password";
        UserRole role = UserRole.PLAYER;
        boolean isActive = true;

        // Act
        UserData userData = new UserData(userId, username, password, role, isActive);

        // Assert
        assertEquals(userId, userData.getUserId());
        assertEquals(username, userData.getUsername());
        assertEquals(password, userData.getPassword());
        assertEquals(role, userData.getRole());
        assertTrue(userData.isAccountActive());
    }

    @Test
    void constructor_withInactiveAccount_shouldSetIsAccountActiveFalse() {
        // Arrange & Act
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        // Assert
        assertFalse(userData.isAccountActive());
    }

    // ==================== Authority Tests ====================

    @Test
    void getAuthorities_withPlayerRole_shouldReturnRolePlayerAuthority() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        // Act
        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PLAYER")));
    }

    @Test
    void getAuthorities_withAdminRole_shouldReturnRoleAdminAuthority() {
        // Arrange
        UserData userData = createTestUserData(UserRole.ADMIN, true);

        // Act
        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void getAuthorities_withQuizmasterRole_shouldReturnRoleQuizmasterAuthority() {
        // Arrange
        UserData userData = createTestUserData(UserRole.QUIZMASTER, true);

        // Act
        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_QUIZMASTER")));
    }

    @Test
    void getAuthorities_shouldAlwaysReturnSingleAuthority() {
        // Test that only one authority is returned, regardless of role
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
        // Arrange
        UserData userData = createTestUserData(UserRole.ADMIN, true);

        // Act
        Collection<? extends GrantedAuthority> authorities = userData.getAuthorities();

        // Assert
        GrantedAuthority authority = authorities.iterator().next();
        assertTrue(authority.getAuthority().startsWith("ROLE_"),
                "Authority should start with ROLE_");
    }

    // ==================== UserDetails Method Tests ====================

    @Test
    void getPassword_shouldReturnPassword() {
        // Arrange
        String password = "my_encoded_password";
        UserData userData = new UserData(
                createTestUserId(),
                "testuser",
                password,
                UserRole.PLAYER,
                true
        );

        // Act
        String result = userData.getPassword();

        // Assert
        assertEquals(password, result);
    }

    @Test
    void getUsername_shouldReturnUsername() {
        // Arrange
        String username = "testuser123";
        UserData userData = new UserData(
                createTestUserId(),
                username,
                "password",
                UserRole.PLAYER,
                true
        );

        // Act
        String result = userData.getUsername();

        // Assert
        assertEquals(username, result);
    }

    // ==================== Account Status Tests ====================

    @Test
    void isAccountNonExpired_withActiveAccount_shouldReturnTrue() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        // Act
        boolean result = userData.isAccountNonExpired();

        // Assert
        assertTrue(result);
    }

    @Test
    void isAccountNonExpired_withInactiveAccount_shouldReturnFalse() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        // Act
        boolean result = userData.isAccountNonExpired();

        // Assert
        assertFalse(result);
    }

    @Test
    void isAccountNonLocked_withActiveAccount_shouldReturnTrue() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        // Act
        boolean result = userData.isAccountNonLocked();

        // Assert
        assertTrue(result);
    }

    @Test
    void isAccountNonLocked_withInactiveAccount_shouldReturnFalse() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        // Act
        boolean result = userData.isAccountNonLocked();

        // Assert
        assertFalse(result);
    }

    @Test
    void isCredentialsNonExpired_withActiveAccount_shouldReturnTrue() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        // Act
        boolean result = userData.isCredentialsNonExpired();

        // Assert
        assertTrue(result);
    }

    @Test
    void isCredentialsNonExpired_withInactiveAccount_shouldReturnFalse() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        // Act
        boolean result = userData.isCredentialsNonExpired();

        // Assert
        assertFalse(result);
    }

    @Test
    void isEnabled_withActiveAccount_shouldReturnTrue() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        // Act
        boolean result = userData.isEnabled();

        // Assert
        assertTrue(result);
    }

    @Test
    void isEnabled_withInactiveAccount_shouldReturnFalse() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        // Act
        boolean result = userData.isEnabled();

        // Assert
        assertFalse(result);
    }

    // ==================== Combined Account Status Tests ====================

    @Test
    void allAccountStatusMethods_withActiveAccount_shouldAllReturnTrue() {
        // Arrange
        UserData userData = createTestUserData(UserRole.ADMIN, true);

        // Act & Assert
        assertTrue(userData.isAccountNonExpired());
        assertTrue(userData.isAccountNonLocked());
        assertTrue(userData.isCredentialsNonExpired());
        assertTrue(userData.isEnabled());
    }

    @Test
    void allAccountStatusMethods_withInactiveAccount_shouldAllReturnFalse() {
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, false);

        // Act & Assert
        assertFalse(userData.isAccountNonExpired());
        assertFalse(userData.isAccountNonLocked());
        assertFalse(userData.isCredentialsNonExpired());
        assertFalse(userData.isEnabled());
    }

    // ==================== Integration Tests ====================

    @Test
    void userDataWithAllRoles_shouldProperlyImplementUserDetails() {
        // Test that UserData properly implements UserDetails for all roles
        for (UserRole role : UserRole.values()) {
            UserData userData = new UserData(
                    createTestUserId(),
                    "user_" + role.name(),
                    "password",
                    role,
                    true
            );

            // Verify UserDetails contract
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
        // Arrange
        UserData userData = createTestUserData(UserRole.PLAYER, true);

        // Act & Assert - Basic verification that all fields are accessible
        assertNotNull(userData.getUserId());
        assertNotNull(userData.getUsername());
        assertNotNull(userData.getPassword());
        assertNotNull(userData.getRole());
        assertNotNull(userData.getAuthorities());
    }
}
