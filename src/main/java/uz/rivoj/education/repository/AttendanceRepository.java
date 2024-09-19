package uz.rivoj.education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.AttendanceEntity;
import uz.rivoj.education.entity.enums.AttendanceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {
    Optional<AttendanceEntity> findByStudent_IdAndLesson_IdAndStatusIs(UUID student_id, UUID lesson_id,AttendanceStatus status);
    Optional<List<AttendanceEntity>> findByStudentId(UUID userId);
    Optional<Page<AttendanceEntity>> findByStatus(Pageable pageable, AttendanceStatus status);
    Optional<AttendanceEntity> findByLesson_Id(UUID lesson_Id);
    Optional<AttendanceEntity> findByStudentIdAndLessonId(UUID student_id, UUID lesson_id);
}
