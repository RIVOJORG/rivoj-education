package uz.rivoj.education.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ChatCR {
    private String member1;
    private String member2;
    private  LocalDateTime  latestMessageDate;

    public ChatCR(String member1, String member2) {
        this.member1 = member1;
        this.member2 = member2;
        latestMessageDate = LocalDateTime.now();
    }
}
