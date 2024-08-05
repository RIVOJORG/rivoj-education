package uz.rivoj.education.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity(name = "teacher_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TeacherInfo extends BaseEntity {

    @OneToOne
    private UserEntity teacher;

    @ManyToOne
    private SubjectEntity subject;

    private String avatar;
    private String about;
}
