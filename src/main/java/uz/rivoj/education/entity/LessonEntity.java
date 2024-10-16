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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private ModuleEntity module;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id", referencedColumnName = "teacher_id")
    private TeacherInfo teacherInfo;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendanceEntity> attendances;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;
    private Integer number;
    private String title;
    private String source;
    private String cover;
    private String description;
    @ElementCollection
    private List<String> additionalLinks;
}
