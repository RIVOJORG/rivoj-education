package uz.rivoj.education.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.SubjectEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {
    Optional<ModuleEntity> findBySubject_IdAndNumber(UUID subjectId, Integer number);
    Optional<List<ModuleEntity>> findAllBySubject_IdOrderByNumberAsc(UUID subjectId);
    Optional<ModuleEntity> findTopBySubjectAndNumberLessThanOrderByNumberDesc(SubjectEntity subject, Integer number);
    Integer countBySubject_Id(UUID subjectId);
}

