package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonCR {
    private Integer number;
    private String title;
    private UUID moduleId;
    private String description;
}
