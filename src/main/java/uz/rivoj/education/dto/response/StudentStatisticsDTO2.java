package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentStatisticsDTO2 {
    private String studentName;
    private String studentSurname;
    private String avatar;
    private List<AttendanceDTO> attendanceDTOList;
}