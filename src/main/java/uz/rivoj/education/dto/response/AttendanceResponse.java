package uz.rivoj.education.dto.response;
import lombok.*;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceResponse {
    private UUID teacherId;
    private UUID studentId;
    private UUID lessonId;
    private String answer;
    private Integer appropriation;
    private Integer coin;
    private Boolean status;
}
