package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "subject")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubjectEntity extends BaseEntity {
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleEntity> modules;
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherInfo> teachers;
    private String title;

}

