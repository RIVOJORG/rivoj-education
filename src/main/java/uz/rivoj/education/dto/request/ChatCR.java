package uz.rivoj.education.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ChatCR {
    private String member1;
    private String member2;
}
