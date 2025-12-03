package bg.softuni.onlinequizplatform.repository;

import bg.softuni.onlinequizplatform.model.Category;
import bg.softuni.onlinequizplatform.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByCategory(Category category);

    List<Quiz> findAllByUser_IdOrderByUpdatedOnDesc(UUID userId);
}
