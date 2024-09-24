package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UncheckedAttendanceResponse {
    private UUID attendanceId;
    private String attendanceSource;
    private String attendanceCover;
    private Integer currentModule;
    private Integer currentLesson;
    private String studentName;
    private String studentSurname;
}
