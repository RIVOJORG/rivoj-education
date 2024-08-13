package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.rivoj.education.entity.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, UUID> {
    Optional<StudentInfo> findStudentInfoByStudentId(UUID id);
    List<StudentInfo> findByLessonAndCurrentModule(LessonEntity lesson, ModuleEntity module);
    List<StudentInfo> findByLessonAndCurrentModuleAndSubject(LessonEntity lesson, ModuleEntity module, SubjectEntity subject);


    List<StudentInfo> findByCurrentModule(ModuleEntity module);



}
