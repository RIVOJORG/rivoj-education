package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RankingPageResponse {
    List<BestStudentResponse> bestStudents;
}
