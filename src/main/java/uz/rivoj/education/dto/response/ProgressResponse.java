package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProgressResponse {
    private Integer moduleCount;
    private List<Integer> scoreList;
}
