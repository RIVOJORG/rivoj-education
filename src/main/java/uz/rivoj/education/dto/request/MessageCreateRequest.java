package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class MessageCreateRequest {
    private String text;
    private UUID chatId;
    private UUID senderId;
}
