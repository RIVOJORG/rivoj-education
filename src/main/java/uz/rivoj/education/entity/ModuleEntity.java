package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "module")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ModuleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;
    private Integer moduleNumber;
}
