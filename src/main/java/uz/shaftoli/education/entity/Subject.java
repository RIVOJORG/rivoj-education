package uz.shaftoli.education.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "subjects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Subject extends BaseEntity {
    private String title;

    @JsonIgnore
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Module> modules;
}