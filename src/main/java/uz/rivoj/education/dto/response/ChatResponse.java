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
    private UUID chat_id;
    private String student_avatar;
    private String name;
    private String surname;
    private String last_message;
}
