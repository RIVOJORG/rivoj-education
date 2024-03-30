package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceRequest {
    private UUID teacherId;
    private UUID studentId;
    private UUID lessonId;
    private String answer;
    private Integer appropriation;
    private Integer coin;
    private Boolean status;
}
