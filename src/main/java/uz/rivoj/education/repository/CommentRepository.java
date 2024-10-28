package uz.rivoj.education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.CommentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    Optional<List<CommentEntity>> findByLessonId(UUID lessonId);

    Page<CommentEntity> findByLessonId(UUID lessonId, Pageable page);
    Optional<List<CommentEntity>> findByOwnerId(UUID ownerId);
}
