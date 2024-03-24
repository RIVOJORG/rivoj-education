package uz.shaftoli.education.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "lesson")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Lesson  extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;
    private Integer number;
    private String title;
    private String content;
}
