package uz.shaftoli.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.shaftoli.education.entity.Lesson;
import uz.shaftoli.education.entity.Module;

import java.util.UUID;
@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
}
