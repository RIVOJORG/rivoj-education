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
    private UserEntity student;
    private String photo;
    private LocalDate birthday;
}
