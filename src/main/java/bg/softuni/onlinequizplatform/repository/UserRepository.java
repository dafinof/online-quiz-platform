package bg.softuni.onlinequizplatform.repository;

import bg.softuni.onlinequizplatform.model.User;
import bg.softuni.onlinequizplatform.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(UserRole role);
}
