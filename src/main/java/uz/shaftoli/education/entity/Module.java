package uz.shaftoli.education.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "module")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Module extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private Lesson lesson;
    private Integer number;
}
