package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.TeacherInfo;
import uz.rivoj.education.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherInfoRepository extends JpaRepository<TeacherInfo, UUID> {

    Optional<TeacherInfo> findTeacherInfoBySubjectId(UUID id);
    TeacherInfo findByTeacher(UserEntity teacher);

}
