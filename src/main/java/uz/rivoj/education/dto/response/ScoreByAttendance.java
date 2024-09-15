package uz.rivoj.education.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScoreByAttendance {
    private Integer lessonNumber;
    private Integer score;
}
