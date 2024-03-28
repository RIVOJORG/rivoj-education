package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonCreateRequest {
    private Integer number;
    private String title;
    private String content;
    private UUID moduleId;
}
