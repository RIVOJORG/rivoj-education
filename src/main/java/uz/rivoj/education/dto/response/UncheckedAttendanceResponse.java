package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UncheckedAttendanceResponse {
    private UUID attendanceId;
    private List<String> answerList;
    private Integer moduleNumber;
    private Integer lessonNumber;
    private UUID studentId;
    private String studentName;
    private String studentSurname;
    private String avatar;
}
