package uz.rivoj.education.dto.response;


import lombok.*;
import uz.rivoj.education.entity.DiscountEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HomePageResponse {

    private String phoneNumber;
    private String name;
    private String surname;
    private String avatar;
    private Integer currentModule;
    private Integer currentLesson;
    private Boolean isLessonOver;
    private Integer coin;
    private Integer totalScore;
    private List<Integer> scores;
    private List<DiscountResponse> discounts;

}
