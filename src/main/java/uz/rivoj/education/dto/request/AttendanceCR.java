package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceCR {
    private UUID teacherId;
    private UUID lessonId;
    private List<String> answer;
}
