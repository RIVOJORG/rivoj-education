package uz.rivoj.education.dto.response;


import lombok.*;
import uz.rivoj.education.entity.Message;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class MessageResponse {
    private UUID chatId;
    private UUID senderId;
    private String text;
}
