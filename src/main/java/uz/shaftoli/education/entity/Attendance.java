package uz.shaftoli.education.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "attendance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private TeacherInfo teacher;


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private StudentInfo student;


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private Lesson lesson;


    private String answer;
    private Integer appropriation;
    private Integer coin;
    private Boolean status;
}
