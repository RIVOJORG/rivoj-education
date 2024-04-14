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
    @JoinColumn(name = "membersId", referencedColumnName = "id")
    private List<UserEntity> members;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;
    

}

