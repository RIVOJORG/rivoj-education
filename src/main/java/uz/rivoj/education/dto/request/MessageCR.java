package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class MessageCR {
    private String text;
    private UUID chatId;
    private UUID senderId;
}
