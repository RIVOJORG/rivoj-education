package uz.rivoj.education.dto.response;


import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EducationPageResponse {

    private Integer countModules;
    private Integer currentModule;
    private List<Integer> countLessons;
    private Integer currentLesson;
    private Boolean isLessonOver;
    private Integer coin;
    private Integer totalScore;
    private List<BestStudentResponse> bestStudentsOfLesson;

}
