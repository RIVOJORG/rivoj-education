package uz.rivoj.education.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "teacher_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TeacherInfo extends BaseEntity {
    @OneToOne
    private UserEntity teacher;
    @ManyToOne(fetch = FetchType.EAGER)
    private SubjectEntity subject;
    private String avatar;
    private String about;
    private LocalDate birthday;

}
