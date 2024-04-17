package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.rivoj.education.entity.DiscountEntity;
import uz.rivoj.education.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface DiscountRepository extends JpaRepository<DiscountEntity, UUID> {
    List<DiscountEntity> findDiscountEntitiesByStudentId(UUID id);
}
