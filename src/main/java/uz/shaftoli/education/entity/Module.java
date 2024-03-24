package uz.shaftoli.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

@Entity(name = "module")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Module extends BaseEntity {
    @ManyToOne
    private Subject subject;
    @OneToMany
    private Lesson lesson;
    private Integer number;
}
