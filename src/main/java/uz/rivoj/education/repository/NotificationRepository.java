package uz.rivoj.education.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.DiscountEntity;
import uz.rivoj.education.entity.NotificationEntity;
import uz.rivoj.education.entity.UserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findAllByStudentId(UUID id);

}
