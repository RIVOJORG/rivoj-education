package uz.shaftoli.education.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @JsonIgnore
    @OneToMany
    private List<Lesson> lessons;
    private Integer number;
}
