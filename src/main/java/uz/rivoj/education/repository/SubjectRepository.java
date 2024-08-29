package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.SubjectEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, UUID> {
    boolean existsByTitle(String title);

    SubjectEntity findByTitle(String title);

    @Query("SELECT s FROM subject s JOIN s.teachers t WHERE t.teacher.id = :teacherId")
    List<SubjectEntity> findAllByTeachersTeacherId(@Param("teacherId") UUID teacherId);
}
