package uz.rivoj.education.dto.update;

import lombok.*;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CheckAttendanceDTO {
    private UUID attendanceId;
    private Integer score;
}
