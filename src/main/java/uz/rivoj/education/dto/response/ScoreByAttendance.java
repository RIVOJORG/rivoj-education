package uz.rivoj.education.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScoreByAttendance {
    private int lessonNumber;
    private int score;
}
