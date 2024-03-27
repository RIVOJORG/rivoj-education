package uz.shaftoli.education.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "subject")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Subject extends BaseEntity{
    private String title;

    @JsonIgnore
    @OneToMany
    private List<Module> modules;
}
