package uz.rivoj.education.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Entity(name = "subject")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubjectEntity extends BaseEntity{
    private String title;
}
