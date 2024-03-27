package uz.shaftoli.education.entity;

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
    @OneToMany
    private List<Module> module;
}
