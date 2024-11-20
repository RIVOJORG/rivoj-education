package uz.rivoj.education.dto.response;
import lombok.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceResponse {
    private UUID id;
    private UUID lesson_id;
    private UUID student_id;
    private UUID teacher_id;
    private List<String> answers;
    private String feedBack;
    private Integer score;
    private Integer coin;
    private AttendanceStatus status;
}
