package uz.rivoj.education.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.dto.response.AttendanceDTO;
import uz.rivoj.education.dto.response.AttendanceSpecialResponse;
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
    Optional<List<AttendanceEntity>> findAllByLesson_Id(UUID lesson_Id);
    Optional<AttendanceEntity> findByStudentIdAndLessonId(UUID student_id, UUID lesson_id);

    @Query("SELECT a FROM attendance a " +
            "JOIN a.lesson l " +
            "JOIN l.module m " +
            "JOIN m.subject s " +
            "WHERE s.id = :subjectId AND a.status = 'UNCHECKED'")
    Optional<List<AttendanceEntity>> findUncheckedAttendanceBySubjectId(@Param("subjectId") UUID subjectId);

    @Query("SELECT new uz.rivoj.education.dto.response.AttendanceDTO(a.id, a.score) FROM attendance a " +
            "JOIN a.lesson l " +
            "JOIN l.module m " +
            "WHERE a.student.id = :studentId AND m.id = :moduleId " +
            "ORDER BY l.number")
    List<AttendanceDTO> findAttendanceByStudentIdAndModuleId(@Param("studentId") UUID studentId, @Param("moduleId") UUID moduleId);

    @Query("SELECT new uz.rivoj.education.dto.response.AttendanceSpecialResponse(" +
            "s.student.name AS studentName, s.student.surname AS studentSurname, s.student.avatar AS studentAvatar, " +
            "t.teacher.name AS teacherName, t.teacher.surname AS teacherSurname, t.teacher.avatar AS teacherAvatar, " +
            "m.number AS moduleNumber, l.number AS lessonNumber, subj.title AS subject, " +
            "a.feedBack AS feedBack, a.score AS score, a.coin AS coin, a.status AS status) " +
            "FROM attendance a " +
            "JOIN a.student s " +
            "JOIN a.teacher t " +
            "JOIN a.lesson l " +
            "JOIN l.module m " +
            "JOIN m.subject subj " +
            "WHERE a.id = :attendanceId")
    AttendanceSpecialResponse findAttendanceDetailsById(@Param("attendanceId") UUID attendanceId);

    void deleteByStudentId(UUID id);

    Optional<List<AttendanceEntity>> findAllByTeacherId(UUID teacherId);

    void deleteALlByLessonId(UUID lesson_id);
}
