package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity(name = "student_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentInfo extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private UserEntity student;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private ModuleEntity currentModule;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private LessonEntity lesson;

    private Integer coin;

    private Integer totalScore;


}
