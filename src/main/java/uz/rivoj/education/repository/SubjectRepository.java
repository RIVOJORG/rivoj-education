package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.SubjectEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, UUID> {
    boolean existsByTitle(String title);
    Optional<SubjectEntity> findByTitle(String title);
    Optional<SubjectEntity> findByModules_Id(UUID moduleId);
}
