package uz.rivoj.education.dto.response;

import lombok.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceSpecialResponse {
    private String studentName;
    private String studentSurname;
    private String studentAvatar;
    private String teacherName;
    private String teacherSurname;
    private String teacherAvatar;
    private Integer moduleNumber;
    private Integer lessonNumber;
    private String subject;
    private List<String> answers;
    private String feedBack;
    private Integer score;
    private Integer coin;
    private AttendanceStatus status;



    public AttendanceSpecialResponse(
            String studentName,
            String studentSurname,
            String studentAvatar,
            Integer moduleNumber,
            Integer lessonNumber,
            String subject,
            AttendanceStatus status) {
        this.studentName = studentName;
        this.studentSurname = studentSurname;
        this.studentAvatar = studentAvatar;
        this.moduleNumber = moduleNumber;
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.status = status;
    }
}
