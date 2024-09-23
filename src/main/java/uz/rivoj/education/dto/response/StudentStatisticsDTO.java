package uz.rivoj.education.dto.response;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentStatisticsDTO {
    private String studentName;
    private String studentSurname;
    private String avatar;
    private Integer lessonCount;
    private List<Integer> scoreList;
}
