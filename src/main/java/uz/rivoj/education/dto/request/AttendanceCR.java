package uz.rivoj.education.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceCR {
    private UUID lessonId;
    private String description;
}
