package uz.rivoj.education.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.rivoj.education.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, UUID> {
    Optional<StudentInfo> findByStudentId(UUID studentId);
    Optional<List<StudentInfo>> findByCurrentModule_Id(UUID currentModule_id);
    Optional<List<StudentInfo>> findBySubject_Id(UUID subjectId, Pageable pageable);
    Optional<List<StudentInfo>> findTop10BySubject_idOrderByTotalScoreDesc(UUID subjectId, Pageable pageable);
    Optional<List<StudentInfo>> findTop10ByOrderByTotalScoreAsc();
    Optional<List<StudentInfo>> findBySubjectId(UUID subjectId);
}
