package uz.rivoj.education.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity(name = "lesson")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LessonEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private ModuleEntity module;
    private Integer number;
    private String title;
    private String source;
    private String cover;
    private String description;
    private LocalTime lessonDuration;
    private List<String> additionalLinks;
}
