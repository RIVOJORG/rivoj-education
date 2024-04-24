package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    private String avatar;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private ModuleEntity currentModule;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private LessonEntity lesson;

    private Boolean isLessonOver; // hozirgi lessonni tugatganmi yoki yo'qmiligini bildiradi

    private Integer coin;

    private Integer totalScore;

    private LocalDate birthday;

}
