package uz.rivoj.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;

@Entity(name = "attendance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private TeacherInfo teacher;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private StudentInfo student;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private LessonEntity lessonEntity;
    private String answer;
    private Integer score;
    private String feedBack;
    private Integer coin;
    private AttendanceStatus status;
}
