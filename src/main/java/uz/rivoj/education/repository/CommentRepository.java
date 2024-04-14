package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.CommentEntity;
import uz.rivoj.education.service.CommentService;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
}
