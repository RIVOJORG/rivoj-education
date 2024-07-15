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
    private String source;
    private String cover; // content abloshkasi
    private UUID moduleId;
    private String description;
}
