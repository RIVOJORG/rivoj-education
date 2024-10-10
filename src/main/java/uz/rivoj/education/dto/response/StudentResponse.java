package uz.rivoj.education.dto.response;
import java.time.LocalDate;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StudentResponse {
   private UUID id;
    private String name;
    private String surname;
    private String avatar;
    private String phoneNumber;
    private LocalDate birth;
    private UUID subjectId;
    private String subjectName;
    private UUID currentLessonId;
    private UUID currentModuleId;
    private Integer currentLessonNumber;
    private Integer currentModuleNumber;
    private Integer totalCoins;
    private Integer totalScore;



}
