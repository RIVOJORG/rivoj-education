package uz.rivoj.education.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.rivoj.education.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, UUID> {
    Optional<StudentInfo> findByStudentId(UUID studentId);
    Optional<List<StudentInfo>> findByCurrentModule_Id(UUID currentModule_id);
    Page<StudentInfo> findBySubject_Id(UUID subjectId, Pageable pageable);
    Optional<List<StudentInfo>> findTop10BySubject_idOrderByTotalScoreDesc(UUID subjectId, Pageable pageable);
    Optional<List<StudentInfo>> findAllByOrderByTotalScoreDesc();
    StudentInfo findByStudent_Id(UUID studentId);
    Optional<List<StudentInfo>> findBySubject_Id(UUID subjectId);
    Optional<List<StudentInfo>> findAllBySubject_idOrderByTotalScoreDesc(UUID subjectId);


    @Query("SELECT si FROM student_info si JOIN FETCH si.student student WHERE si.subject.id = :subjectId AND " +
            "(COALESCE(:searchTerm, '') = '' OR " +
            "LOWER(student.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(student.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<StudentInfo> findBySubject_IdWithSearchTerm(@Param("subjectId") UUID subjectId,
                                                     @Param("searchTerm") String searchTerm,
                                                     Pageable pageable);

    Optional<List<StudentInfo>> findByLesson_Id(UUID lessonId);
}
