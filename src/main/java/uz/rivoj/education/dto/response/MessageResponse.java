package uz.rivoj.education.dto.response;


import lombok.*;
import uz.rivoj.education.entity.Message;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class MessageResponse {
    private List<Message> messages;
}
