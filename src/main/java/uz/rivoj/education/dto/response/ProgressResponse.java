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
    private Integer lessonCount;
    private List<Integer> scoreList;
}
