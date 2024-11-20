package uz.rivoj.education.dto.response;

import lombok.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;

import java.util.List;

@NoArgsConstructor
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
            String teacherName,
            String teacherSurname,
            String teacherAvatar,
            Integer moduleNumber,
            Integer lessonNumber,
            String subject,
            List<String> answers,
            String feedBack,
            Integer score,
            Integer coin,
            AttendanceStatus status) {
        this.studentName = studentName;
        this.studentSurname = studentSurname;
        this.studentAvatar = studentAvatar;
        this.teacherName = teacherName;
        this.teacherSurname = teacherSurname;
        this.teacherAvatar = teacherAvatar;
        this.moduleNumber = moduleNumber;
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.answers = answers;
        this.feedBack = feedBack;
        this.score = score;
        this.coin = coin;
        this.status = status;
    }
}
