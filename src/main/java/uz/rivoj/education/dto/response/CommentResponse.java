package uz.rivoj.education.dto.response;

import lombok.*;
import uz.rivoj.education.entity.LessonEntity;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentResponse {
    private UUID commentId;
    private UUID studentId;
    private UUID lessonId;
    private String name;
    private String surname;
    private String avatar;
    private String description;
}
