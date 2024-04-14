package uz.rivoj.education.dto.response;


import lombok.*;

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
    private Integer coins;
    private Integer totalScores;
    private List<Integer> scores;

    /** Discount Entity qo'shilgandan keyin Integer o'rniga yozib qo'yish kerak*/
    private List<Integer> discounts;

}
