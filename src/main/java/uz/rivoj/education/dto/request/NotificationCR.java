package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class NotificationCR {
    private UUID userId;
    private String title;
    private String description;
}
