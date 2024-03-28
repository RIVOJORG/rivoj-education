package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.LessonEntity;

import java.util.UUID;
@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, UUID> {
}
