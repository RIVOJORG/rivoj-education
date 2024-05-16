package uz.rivoj.education.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetStudentFullInfoResponse {
    private String name;
    private String surname;
    private String avatar;
    private String phoneNumber;
    private String password;
    private LocalDate birthday;
    private String subject;
    private Integer currentModule;
    private Integer currentLesson;
    private Boolean isLessonOver;
    private Integer score;
    private Integer coin;
    private List<SpecialAttendanceResponse> attendanceList;
    private List<DiscountResponse> discountList;
}
