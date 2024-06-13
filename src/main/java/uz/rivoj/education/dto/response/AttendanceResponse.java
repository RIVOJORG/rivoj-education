package uz.rivoj.education.dto.response;
import lombok.*;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceResponse {
    private UUID lessonId;
    private UUID studentId;
    private UUID teacherId;
    private String video;
    private String photo;
    private String feedBack;
    private Integer score;
    private Integer coin;
    private Boolean isCorrect;
}
