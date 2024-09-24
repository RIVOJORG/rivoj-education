package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.ModuleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {
    ModuleEntity findFirstBySubject_IdOrderByNumber(UUID subjectId);
    ModuleEntity findBySubject_IdOrderByNumber(UUID subjectId);
    Integer countBySubject_Id(UUID subjectId);
    Optional<ModuleEntity> findBySubject_IdAndNumber(UUID subjectId, Integer number);
    Optional<List<ModuleEntity>> findAllBySubject_IdOrderByNumberAsc(UUID subjectId);
}
