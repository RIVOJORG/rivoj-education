package uz.rivoj.education.dto.response;


import lombok.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class NotificationResponse {
    private String title;
    private String description;
}
