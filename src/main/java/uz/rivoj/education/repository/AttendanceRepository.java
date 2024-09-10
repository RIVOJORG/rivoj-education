package uz.rivoj.education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.entity.AttendanceEntity;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.enums.AttendanceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity> findAttendanceEntitiesByStudent_IdAndLessonEntity_Module_Id(
            UUID studentId,
            UUID moduleId
    );


    List<AttendanceEntity> findAttendanceEntitiesByStudentIdAndLessonEntity(UUID studentId, LessonEntity lesson);
    List<AttendanceEntity> findAllByStudentId(UUID userId);

    Page<AttendanceEntity> findAllByStatus(Pageable pageable, AttendanceStatus status);

    List<AttendanceEntity> findAttendanceByLessonEntity(LessonEntity lessonEntity);

    Optional<AttendanceEntity> findByStudentAndLessonEntity(StudentInfo student, LessonEntity lessonEntity);
}
