package uz.rivoj.education.dto.response;


import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class MessagesResponse {
    /** Integer o'rniga Message yoziilshi kerak. Massage Entity hali qo'shilmagan*/
    private List<Integer> messages;
}
