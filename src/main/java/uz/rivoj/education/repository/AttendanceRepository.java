package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.entity.AttendanceEntity;
import uz.rivoj.education.entity.ModuleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity> findAttendanceEntitiesByStudentIdAndLessonEntity_Module(
            UUID student_id,
            ModuleEntity module
    );
    List<AttendanceEntity> findAllByStudentId(UUID userId);
    Optional<AttendanceResponse> findByStudentAndLessonId();
}
