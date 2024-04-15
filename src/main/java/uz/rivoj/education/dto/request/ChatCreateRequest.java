package uz.rivoj.education.dto.request;

import lombok.*;
import uz.rivoj.education.dto.response.MessageResponse;
import uz.rivoj.education.dto.response.UserResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ChatCreateRequest {
    private List<UserResponse> members;
    private List<MessageResponse> messages;
}
