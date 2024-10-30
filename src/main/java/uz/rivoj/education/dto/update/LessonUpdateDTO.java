package uz.rivoj.education.dto.update;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private UUID teacherId;
    private String title;
    private String description;
    private List<String> additionalLinks;
    private MultipartFile coverOfLesson;
    private MultipartFile lessonVideo;
}
