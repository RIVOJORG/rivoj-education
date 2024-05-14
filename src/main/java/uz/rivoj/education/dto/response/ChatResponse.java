package uz.rivoj.education.dto.response;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatResponse {
    private UUID chatId;
    private String studentAvatar;
    private String name;
    private String surname;
    private String lastMessage;
}
