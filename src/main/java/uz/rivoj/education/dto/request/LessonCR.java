package uz.rivoj.education.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private UUID teacherId;
    private UUID moduleId;
    private String description;
    private List<String> additionalLinks;
    private MultipartFile coverOfLesson;
    private MultipartFile lessonVideo;


}
