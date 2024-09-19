package uz.rivoj.education.dto.update;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonUpdateDTO {
    private UUID id;
    private String title;
    private String description;
    private List<String> additionalLinks;
}
