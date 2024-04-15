package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.ChatEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,UUID> {
    @Query("SELECT c FROM chat c WHERE SIZE(c.members) = :userCount AND c.members = :userIds")
    ChatEntity findChatByUserIds(@Param("userIds") List<UUID> userIds, @Param("userCount") int userCount);

}
