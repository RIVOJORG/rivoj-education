package uz.rivoj.education.dto.response;

import lombok.*;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RankingPageResponse {
    private List<BestStudentResponse> bestStudents;
    private int userOrder;
    private int userRank;
}
