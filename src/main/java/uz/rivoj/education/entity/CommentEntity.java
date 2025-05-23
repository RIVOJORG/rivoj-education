package uz.rivoj.education.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentEntity extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private LessonEntity lesson;

    @ManyToOne
    private UserEntity owner;
    private String description;
}
