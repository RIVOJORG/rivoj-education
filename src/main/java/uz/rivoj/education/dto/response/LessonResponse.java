package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonResponse {
    private Integer number;
    private String title;
    private String content;
    private UUID moduleId;
}
