package uz.rivoj.education.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonCR {
    private String title;
    private UUID moduleId;
    private String description;
    private List<String> additionalLinks;
    private LocalDate lessonDuration;
}
