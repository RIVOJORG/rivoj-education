package uz.rivoj.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.UUID;

@Entity(name = "message")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Message extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "senderId", referencedColumnName = "id")
    private UserEntity sender;
    @ManyToOne
    @JoinColumn(name = "chatId", referencedColumnName = "id")
    private ChatEntity chat;
    private String text;
}
