package uz.rivoj.education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.AttendanceEntity;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.enums.AttendanceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity> findAttendanceEntitiesByStudent_IdAndLesson_Module_Id(
            UUID studentId,
            UUID moduleId
    );


    Optional<List<AttendanceEntity>> findByStudent_IdAndLesson_Id(UUID student_id, UUID lesson_id);
    List<AttendanceEntity> findAllByStudentId(UUID userId);

    Page<AttendanceEntity> findAllByStatus(Pageable pageable, AttendanceStatus status);

    List<AttendanceEntity> findAttendanceByLesson_Id(UUID lesson_Id);

    Optional<AttendanceEntity> findByStudentAndLesson(StudentInfo student, LessonEntity lessonEntity);
    List<AttendanceEntity> findByStudent_Id(UUID studentId);


}
