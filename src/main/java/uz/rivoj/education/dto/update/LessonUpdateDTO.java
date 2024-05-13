package uz.rivoj.education.dto.update;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonUpdateDTO {
    private Integer number;
    private String title;
    private String source;
    private String cover;
    private UUID moduleId;
}
