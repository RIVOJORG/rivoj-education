package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceCR {
    private UUID teacherId;
    private UUID studentId;
    private UUID lessonId;
    private String answer;
    private Boolean status;
}
