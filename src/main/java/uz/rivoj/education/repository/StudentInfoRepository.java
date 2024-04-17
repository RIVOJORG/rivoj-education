package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, UUID> {
    Optional<StudentInfo> findStudentInfoByStudentId(UUID id);
}
