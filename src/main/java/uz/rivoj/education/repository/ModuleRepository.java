package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.SubjectEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {

    ModuleEntity findBySubject(SubjectEntity subject);

    ModuleEntity findFirstBySubjectOrderByNumberAsc(SubjectEntity subject);

//    ModuleEntity findBySubjectAndNumber(SubjectEntity subject, Integer nextModuleNumber);

    Integer countBySubject(SubjectEntity subject);
    ModuleEntity findBySubjectAndNumber(SubjectEntity subject, Integer number);

    Optional<ModuleEntity> findByNumberAndSubjectId(Integer moduleNumber, UUID subjectId);
    Optional<List<ModuleEntity>> findAllBySubject(SubjectEntity subject);
}
