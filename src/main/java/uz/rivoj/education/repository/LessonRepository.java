package uz.rivoj.education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.ModuleEntity;

import java.util.UUID;
@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, UUID> {
    boolean existsByNumber(Integer number);

    boolean existsByTitle(String title);

    Page<LessonEntity> findLessonsByModule(Pageable pageable, ModuleEntity moduleEntity);

    LessonEntity findFirstByModuleOrderByNumberAsc(ModuleEntity nextModule);
}
