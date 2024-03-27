package uz.shaftoli.education.entity;


import jakarta.persistence.Entity;
import lombok.*;

@Entity(name = "teacher_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TeacherInfo extends BaseEntity{
    private UserEntity teacher;
    private Subject subject;
    private String about;
}
