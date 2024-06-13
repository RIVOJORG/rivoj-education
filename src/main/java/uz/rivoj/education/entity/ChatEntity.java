package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Comparator;
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




    public Message getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            // Assuming messages are sorted by timestamp
            return messages.get(messages.size() - 1);
        }
        return null;
    }

    // Method to get the last message with sorting
    public Message getLastMessageWithSorting() {
        if (messages != null && !messages.isEmpty()) {
            // Ensure the messages are sorted by timestamp
            messages.sort(Comparator.comparing(Message::getCreatedDate));
            return messages.get(messages.size() - 1);
        }
        return null;
    }

}

