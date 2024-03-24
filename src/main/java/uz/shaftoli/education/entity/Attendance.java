package uz.shaftoli.education.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity(name = "attendance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Attendance extends BaseEntity {
    @ManyToOne
    private UserEntity teacher;
    @ManyToOne
    private UserEntity student;
    @ManyToOne
    private Lesson lesson;
    private String answer;
    private Integer appropriation;
    private Integer coin;
    private Boolean status;
}
