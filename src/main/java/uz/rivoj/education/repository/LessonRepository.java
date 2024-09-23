package uz.rivoj.education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.ModuleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, UUID> {
    boolean existsByModuleAndTitle(ModuleEntity module, String title);
    Optional<Page<LessonEntity>> findByModule_Id(Pageable pageable, UUID moduleId);
    Optional<LessonEntity>findFirstByModule_IdOrderByNumberAsc(UUID moduleId);;
    Optional<List<LessonEntity>> findAllByModule_IdOrderByNumberAsc(UUID moduleId);

}
