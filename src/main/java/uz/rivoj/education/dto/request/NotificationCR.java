package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class NotificationCR {
    private List<String> fcmList;
    private String messageTitle;
    private String messageBody;
}
