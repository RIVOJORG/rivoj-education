package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.dto.response.ChatResponse;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,UUID> {
    Optional<List<ChatEntity>> findByMembersContaining(UserEntity user);
}
