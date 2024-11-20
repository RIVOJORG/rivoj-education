package uz.rivoj.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AttendanceDTO {
    private UUID attendanceId;
    private Integer score;
}
