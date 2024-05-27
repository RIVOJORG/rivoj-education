package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "chat")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatEntity extends BaseEntity{
    @ManyToMany
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<UserEntity> members;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;
    

}

