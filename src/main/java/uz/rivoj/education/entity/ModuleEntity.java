package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "module")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ModuleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonEntity> lessons;
    private Integer number;

    public ModuleEntity(SubjectEntity subject, Integer number) {
        this.subject = subject;
        this.number = number;
    }
}
