package uz.rivoj.education.dto.response;

import lombok.*;
import uz.rivoj.education.dto.response.ModuleResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminHomePageResponse {
    private List<Integer> modulesCounts;
    private Integer studentsCount;
    private List<StudentStatisticsDTO> students;
}
